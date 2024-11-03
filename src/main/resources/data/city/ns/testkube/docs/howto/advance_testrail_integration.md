# TestRail integration

- [TestRail integration](#testrail-integration)
  - [Prerequisites](#prerequisites)
  - [Configure TestRail](#configure-testrail)
    - [Configure Playwright](#configure-playwright)
    - [Build the test executor image](#build-the-test-executor-image)
    - [Configure Testkube manifests](#configure-testkube-manifests)
  - [Upload test to TestRail](#upload-test-to-testrail)
  
## Prerequisites

Make sure you have completed the local TestKube setup by following this guide: [Self Testing with Local TestKube](bootstrap_local_testkube.md).

Before proceeding, please review the [Playwright test example](../../executor-images/playwright/README.md) as this document builds upon the `Accessibility` example of Playwright.

## Configure TestRail

TestRail is a web-based test management tool that helps manage and track testing activities, including test case management, test execution, and reporting.

To upload test results to TestRail after running tests in TestKube, follow these steps:

### Configure Playwright

In `playwright.config.ts`, configure a junit reporter as shown below.
The report MUST be written to the location `$TEST_REPORTS_DIR/junit-report.xml`.

```typescript
import { defineConfig, devices } from '@playwright/test';

const testRailOptions = {
  // Whether to add <properties> with all annotations; default is false
  embedAnnotationsAsProperties: true,
  // Where to put the report.
  // use exactly `$TEST_REPORTS_DIR/junit-report.xml` for outputFile when executing in Testkube / container environment
  outputFile: process.env.TEST_REPORTS_DIR ? process.env.TEST_REPORTS_DIR + '/junit-report.xml' : './test-results/junit-report.xml'
};

export default defineConfig({
[...]
  reporter: [
    ['junit', testRailOptions],
  ],
[...]
});
```

### Build the test executor image

To build a Playwright executor image with TestRail support, add `testharness = testrail` to the attributes:

```starlark
playwright_test_image(
    name = "a11y_playwright_example",
    testharness = "testrail",
    srcs = _TEST_SRCS,
    config = "playwright.config.ts",
    playwright_args = [],
    visibility = ["//visibility:private"],
    deps = _TEST_DEPS,
)
```

With the above configuration, TestRail CLI and dependencies will be injected in the image.

### Configure Testkube manifests

When TestKube communicates with TestRail, it needs the following information:

- TESTRAIL_HOST: The host URL, e.g., <https://my-host.testrail.io>
- TESTRAIL_PROJECT: The test project name, e.g., My Test Project
- TESTRAIL_USERNAME: The username used to log in to TestRail
- TESTRAIL_PASSWORD: The password used to log in to TestRail
- TESTRAIL_TITLE: The name you can give to the test run

:warning: The `TESTRAIL_PROJECT` must exist on the TestRail server, before using it in the configuration.

Next, a secret in Testkube namespace is required to store the username and password:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: testrail-credentials
  namespace: testkube
type: Opaque
data:
  TESTRAIL_USER_NAME: {YOUR_USERNAME_BASE64_ENCODED}
  TESTRAIL_PASSWORD: {YOUR_PASSWD_BASE64_ENCODED}
```

Next, configure the environment variables in the `Test` specification

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
spec:
[...]
  executionRequest:
    Variables:
      TESTRAIL_HOST:
        name: TESTRAIL_HOST
        value: https://my-host.testrail.io/
        type: basic
      TESTRAIL_PROJECT:
        name: TESTRAIL_PROJECT
        value: MY-TESTPROJECT-ON-TESTRAIL
        type: basic
      TESTRAIL_TESTRUN_TITLE:
        name: TESTRAIL_TESTRUN_TITLE
        value: Woven-Test-Run-POC
        type: basic
      TESTRAIL_USER_NAME:
        name: TESTRAIL_USER_NAME
        type: secret
        valueFrom:
          secretKeyRef:
            name: testrail-credentials
            key: TESTRAIL_USER_NAME
      TESTRAIL_PASSWORD:
        name: TESTRAIL_PASSWORD
        type: secret
        valueFrom:
          secretKeyRef:
            name: testrail-credentials
            key: TESTRAIL_PASSWORD
```

Finally, to enable the communication between the executor pod and TestRail host, configure a ServiceEntry:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: ServiceEntry
metadata:
  labels:
    agora.woven.toyota/team: <MY_TEAM_NAME>
  name: <TEAM_NAME>-serviceentry
  namespace: testkube
spec:
  exportTo:
  - .
  hosts:
  - my-host.testrail.io
  ports:
  - name: https
    number: 443
    protocol: TLS
    targetPort: 443
  resolution: DNS
```

OK, now all efforts are done. Let's run the test and see the outcome.

## Upload test to TestRail

Run the test on TestKube, it will automatically upload the test results to TestRail after the test is completed.
You will see the test execution log as below

```sh
Testrail is enabled, starting precheck.
Testrail precheck passed, all required TESTRAIL_ environment variables are set.Executing trcli command.
.............
executing tests in worker
............
TestRail CLI 
TestRail instance: https://my-host.testrail.io/ 
Project: My test project
Run title: Woven-Test-Run-POC
Update run: No
Add to milestone: No
Auto-create entities: TrueParsing 
JUnit report.Processed 2 test cases in section example.spec.ts.Checking project. 
Done.Creating test run. 
results: 2/2, Done.
```

After logging into TestRail, you should be able to see the new test run that has been created and the results uploaded.

For a full example, please refer to the [Playwright build image](../../executor-images/playwright/examples/accessibility/BUILD) and the [Playwright test manifest file](../../executor-images/playwright/examples/accessibility/testkube/testkube-a11y-example-tests.yaml).

If you want TestRail integration with an executor other than Playwright, please post in #wcm_agora_testkube

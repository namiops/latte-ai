# Table of contents

- [Table of contents](#table-of-contents)
  - [Testkube Playwright executor images](#testkube-playwright-executor-images)
  - [Prerequisties](#prerequisties)
  - [General process](#general-process)
  - [Resolving paths in Playwright configuration file](#resolving-paths-in-playwright-configuration-file)
  - [Exporting Playwright Videos, Screenshots and Reports](#exporting-playwright-videos-screenshots-and-reports)

## Testkube Playwright executor images

This directory contains the Bazel rule to build an OCI Docker images for executing Playwright tests.
Downloading and Installing NPM packages during runtime in Agora clusters poses security risks.
The build images should be configured to includes all dependencies required to run the tests in an `offline` mode.
The images may be used with Testkube in Agora clusters, Local instances of Testkube, or directly with `docker run`.

## Prerequisties

- Monorepo guide ([Link](../../../../docs/monorepo/README.md)).
- Typescript / Javascript development guide ([Link](../../../../docs/development/javascript/README.md)).
- Deployment workflow guide ([Link](../../../../docs/monorepo/using-the-deployment-workflow.md)).
- Flux Image Update Automation ([Link](../../../../docs/technical_notes/cd.md)).
- Testkube getting started ([Link](../../docs/01_getting-started.md)).

## General process

- Write Playwright test scripts.

- Create and configure the Bazel BUILD file ([Example](./examples/accessibility/BUILD)). Build and load the executor image locally with Bazel.

```bash
bazel run //ns/testkube/executor-images/playwright/examples/accessibility:a11y_playwright_example.load

INFO: Analyzed target //ns/testkube/executor-images/playwright/examples/accessibility:a11y_playwright_example.load (0 packages loaded, 18 targets configured).
INFO: Found 1 target...
Target //ns/testkube/executor-images/playwright/examples/accessibility:a11y_playwright_example.load up-to-date:
  bazel-bin/ns/testkube/executor-images/playwright/examples/accessibility/a11y_playwright_example.load/tarball.tar
INFO: Elapsed time: 4.755s, Critical Path: 3.79s
INFO: 2 processes: 1 internal, 1 linux-sandbox.
INFO: Build completed successfully, 2 total actions
INFO: Running command line: bazel-bin/ns/testkube/executor-images/playwright/examples/accessibility/a11y_playwright_example.load.sh
dfabe13eb651: Loading layer [==================================================>]  7.392kB/7.392kB
eb9c2950ed9b: Loading layer [==================================================>]  45.75MB/45.75MB
The image ns/testkube/executor-images/playwright/examples/accessibility:a11y_playwright_example already exists, renaming the old one with ID sha256:c8eee28c0172b4f11d7658785042f0bc7c08e81063f74b63b8020a8420cf1038 to empty string
Loaded image: ns/testkube/executor-images/playwright/examples/accessibility:a11y_playwright_example # <--- loaded image
```

- Run the Docker image locally to verify correctness

```bash
docker run -it ns/testkube/executor-images/playwright/examples/accessibility:a11y_playwright_example

iqqq: The istio quitquitquit process is disabled

Running 4 tests using 1 worker
····
  4 passed (26.5s)
```

- Create a pull request and merge the Playwright scripts and BUILD files to the repository. Obtain the built Bazel images.

- Configure Testkube manifests with Bazel images [example](./examples/accessibility/testkube/testkube-a11y-example-tests.yaml). Use Flux image policies to keep the executor up to date with the latest image tag.

- Create a pull request to add Testkube manifests to the tenants [directory](../../../../infrastructure/k8s/environments/dev2/clusters/worker1-east/testkube/tenants/), send the review request to the team in #wcm-agora-testkube.

## Resolving paths in Playwright configuration file

To correctly resolve paths in the `playwright.config.ts` file and avoid errors, we use `__dirname` for the absolute path to the directory containing the file needed, not the current working directory.

For instance, to use a `.env` file located under the same directory, we must reference it as follows:

```typescript
import { defineConfig, devices } from '@playwright/test';
import path from 'path';

// MUST explicitly resolve path to any required files
require('dotenv').config({path: path.resolve(__dirname, '.env') })
```

## Exporting Playwright Videos, Screenshots and Reports

The `playwright_test_image` rule sets default values for the artifacts and reports directories via the environment variables: `ARTIFACTS_DIR` and `REPORTS_DIR` (see [playwright_test_image.bzl](../private/playwright_test_image.bzl)), which makes locating and copying the artifacts easier because the directory is known.

As such, we can reference the above variables in the `playwright.config.ts` file in the properties `outputDir` and `outputFolder`:

```typescript
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  ...
  // use TEST_ARTIFACTS_DIR for outputDir when executing in Testkube / container environment
  outputDir: process.env.TEST_ARTIFACTS_DIR ? process.env.TEST_ARTIFACTS_DIR: "./test-results",
  // use TEST_REPORTS_DIR for outputFolder when executing in Testkube / container environment
  reporter: [
    ['html', {
      open: 'never', 
      outputFolder: process.env.TEST_REPORTS_DIR ? process.env.TEST_REPORTS_DIR : "./playwright-report"
    }],
    ['junit', { 
      outputFile: process.env.TEST_REPORTS_DIR ? path.join(process.env.TEST_REPORTS_DIR, 'results.xml') : 'results.xml',
    }],
  ],
...
})
```

:bulb: Additional environment variables may also be passed to the `playwright_test_image` rule via the attribute `env`

:warning: Testkube, by default, sets the environment variable `CI=1`. This should be considered when using `process.env.CI` in the `playwright.config.ts` file.

## TestRail Integration Plugin
For teams using TestRail to manage test artifacts, there is a plugin available to upload test results to TestRail after test execution.

Step 1: Enable TestRail integration for your Playwright test, as described in [Enable TestRail](./../../docs/howto/advance_testrail_integration.md#enable-testrail).

Step 2: Configure TestRail access and test result settings, as described in [Configure TestRail](./../../docs/howto/advance_testrail_integration.md#configure-testrail-access).

For comprehensive documentation, refer to [TestRail Integration](./../../docs/howto/advance_testrail_integration.md).

# Setup load test in local testkube

## Table of contents

- [Setup load test in local testkube](#setup-load-test-in-local-testkube)
  - [Table of contents](#table-of-contents)
  - [Prerequisite](#prerequisite)
  - [Setup and run load test in local](#setup-and-run-load-test-in-local)
    - [Create load test with K6 executor](#create-load-test-with-k6-executor)
    - [Authenticate load test against SUT](#authenticate-load-test-against-sut)
    - [Define the load test scenario](#define-the-load-test-scenario)
    - [Define your load test goal](#define-your-load-test-goal)
    - [Run load test and check result](#run-load-test-and-check-result)

## Prerequisite

Please ensure your have completed local testkube setup by this [Self Testing with Local TestKube](bootstrap_local_testkube.md)

## Setup and run load test in local

Sometimes, you maybe want to run test locally, eg, load testing, then here is the right place.  

### Create load test with K6 executor

We have prepared and embed a job template ```non-injected-container-2048mib``` into local testkube,  you can take use of it for your load testing.

You can create your local_test_k6.yaml like below:

**:warning: ensure the test targets (SUT) are configured appropriately with resource utilization limits. Monitor closely during the test runs.**

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  name: my-load-test
  namespace: testkube
spec:
  type: agora-k6/script
  executionRequest:
    args:
    # script must be passed as first argument
    - |
      import http from 'k6/http'
      export let options = {
        vus:'5',
        duration: '60s',
        thresholds: {
          'http_req_duration': ['p(95)<2000'],
        },
      };
      export default function () {
        http.get('https://httpbin.test.k6.io/headers')
      }
    jobTemplateReference: non-injected-container-2048mib
```

K6 is a cloud native load test framework, you can get its official docs [here](https://k6.io/docs/)

### Authenticate load test against SUT

In situation that load test is run in local testkube against SUT(Service Under Test) in Agora cluster, it requires pass authentication before getting response.

K6 support both basic authentication and OAuth authentication in its [doc](https://k6.io/docs/examples/oauth-authentication/), while, your SUT maybe deployed with its own authentication in Agora cluster,  we had a doc particular for authentication,  please refer to [Authenticate your test with keycloak](./basic_run_injected_test.md).

### Define the load test scenario

In load testing, a load test scenario refers to a specific set of conditions or activities that simulate the expected usage patterns of a system under realistic loads. It is important to have your test scenario designed properly in prior to executing load test code.

Key parameters in defining load test scenario could be

- Virtual users: the numbers of concurrent users the test is going to simulate.
- Iterations: the numbers of actions each virtual user is going to execute.
- Duration:  the time period the load test will keep running.
- Ramp up/down:  we can simulate a more real world traffic that concurrent users gradually login and quit the system.
- Others:  like IP simulation, think time and so on parameters that make the load test is more real world oriented.

In K6,  we can use `Options` to configure the test scenario, below is simple example

```javascript
import { check } from 'k6';

export let options = {
  vus:'5',
  duration: '60s',
};
```

In above configuration, we are telling K6 to start the load testing with 5 concurrent users to execute the test scripts, and this test should keep running for 1 minute.

More `Options` parameters in K6 can be seen in its official [docs](https://k6.io/docs/using-k6/k6-options/reference/)

### Define your load test goal

A successful load test needs a goal. We might have formal SLAs to test against, or we might just want our API, app, or site to respond instantly. That is why specifying performance goals is such an important part of load testing.

You can use `thresholds` to define the load test goal

```javascript
import { check } from 'k6';
import http from 'k6/http';

export let options = {
  thresholds: {
    'http_req_duration': ['p95<2000'],
  },
};
```

Above `thresholds` json defined the condition a test is considered successful or not is, 95% of requests have a response time below 2 seconds(2000ms).

### Run load test and check result

For full example load test manifest file, please refer to [local-test-k6.yaml](../../local/examples/local-test-k6.yaml) as example.

You can run kubectl command to create this test in local testkube

```sh
kubectl apply -f local_test_k6.yaml
```

Then `my-load-test` will appear in testkube dashboard, you can run this test on dashboard now, after test completion, test results can be seen from `Log Output` tab!

There are also richer and real time like test result visualization solution offered by [K6 Dashboard](https://grafana.com/docs/k6/latest/results-output/web-dashboard/) and [Grafana Dashboard](https://grafana.com/docs/k6/latest/results-output/grafana-dashboards/).

At last, above doc is just purposed to explain essential things in load testing.  in real world,  the load test is maybe more complex in designing and coding, welcome to bring your problem to slack channel #wcm-agora-testkube for discussion!

# Overview

Testkube is a framework that natively integrates test orchestration and execution into Kubernetes.
It allows defining tests, executors, and test triggers with Custom Resources.

Its key features include:

- Highly customisable, use YAML to define Test steps, Test executors and Test jobs.
- An operator that controls Testkube custom resources handles changes.
- Test triggers that can watch pods and execute test suites on change.
- Interactive dashboard for executing tests and displaying logs.
- An API server that listens for Testkube execution requests ([docs](https://docs.testkube.io/openapi/))
- MongoDB for storing test results and various Testkube configurations such as telemetry settings and cluster-ID.
- MinIO for storage of test logs and artifacts

For more details, refer to the [official documentation](https://docs.testkube.io/articles/testkube-benefits).

Testkube can help us automate the execution of tests after deployments to the cluster.

## What You Can Do With TestKube

- Create a suite of Integration Tests to run when you update your application with [Test Triggers](https://docs.testkube.io/articles/test-triggers#what-is-a-testkube-test-trigger)
- Perform Scale/Performance Testing on your application in a custom environment with [container Executors](https://docs.testkube.io/test-types/container-executor/)
- Store and query test artifacts with [MinIO](https://docs.testkube.io/articles/artifacts-storage)
- [Webhooks](https://docs.testkube.io/articles/webhooks/)

## Touch TestKube Right Now
 
It takes you not more than 5 mintues to setup TestKube in your local!

Follow the guide 
[Get on with TestKube](local_testkube_index.md) 
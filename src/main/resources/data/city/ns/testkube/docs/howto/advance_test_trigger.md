# Setup test trigger

## Table of contents

- [Setup test trigger](#setup-your-own-executor)
  - [Prerequisite](#Prerequisite)
  - [Executor Custom Resource](#Executor-Custom-Resource)
  
## Prerequisite
Please ensure you have completed local testkube setup by this [Self Testing with Local TestKube](bootstrap_local_testkube.md) 

## Setup test trigger

Testkube can watch your microservice's resources and trigger tests on certain conditions. An example use case can be when Deployment new version rollouts are completed.
For this case, Kubernetes marks a Deployment as complete when it has the following characteristics:

- All of the replicas associated with the Deployment have been updated to the latest version specified, meaning any updates requested have been completed.
- All of the replicas associated with the Deployment are available.
- No old replicas for the Deployment are running.

When the rollout becomes “complete”, the Deployment controller sets a condition with the following attributes to the Deployment's .status.conditions:

```sh
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Progressing    True    NewReplicaSetAvailable
  Available      True    MinimumReplicasAvailable
```

We can use the above conditions to configure a `TestTrigger` object as following:

```yaml
apiVersion: tests.testkube.io/v1
kind: TestTrigger
metadata:
  name: test-trigger-my-deployment-modified
  namespace: testkube
spec:
  action: run
  event: modified
  # resource to watch for modification
  resource: deployment
  resourceSelector:
    name: httpbin
    namespace: httpbin
  # test to execute
  execution: test
  testSelector:
    name: test-curl-jq-httpbin
    namespace: testkube
  # conditions to trigger the test
  conditionSpec:
    conditions:
    # deployment becomes “complete”
    - reason: NewReplicaSetAvailable
      type: Progressing
      status: "True"
      # check if a condition's lastTransitionTime/lastUpdateTime is within this duration. avoids triggers on old conditions.
      ttl: 60
    - type: Available 
      status: "True"
  # optional: delay after a trigger is matched
  delay: 10s
```

for more information please refer to [TestTrigger CRD reference](https://docs.testkube.io/articles/crds-reference/#testtriggerspec)
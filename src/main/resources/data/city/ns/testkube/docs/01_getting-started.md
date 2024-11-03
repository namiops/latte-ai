# Getting Started with Testkube

## Table of contents

- [Getting Started with Testkube](#getting-started-with-testkube)
  - [Table of contents](#table-of-contents)
  - [Preparation](#preparation)
  - [Accessing the dashboard from the local machine](#accessing-the-dashboard-from-the-local-machine)
  - [Creating a service to experiment with](#creating-a-service-to-experiment-with)
  - [Writing manifests](#writing-manifests)
    - [Test Custom Resource](#test-custom-resource)
    - [Executor Custom Resource](#executor-custom-resource)
    - [Test Triggers](#test-triggers)
  - [Using TestKube in Agora with  Sidecar Injected Workloads](#using-testkube-in-agora-with--sidecar-injected-workloads)
  - [GitHub checkout](#github-checkout)
  - [Test artifacts storage in MinIO](#test-artifacts-storage-in-minio)
  - [Environment variables](#environment-variables)
  - [Slack notifications](#slack-notifications)

## Preparation

This guide aims to interact with Testkube in the local cluster. It requires:

- Following the [guide](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/README.md) to start a local cluster
- Testkube added to flux resources in [infrastructure/k8s/local/flux-system/kustomizations/system/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/flux-system/kustomizations/system/kustomization.yaml)

```yaml
resources:
  - testkube.yaml
```

- Creating and changing into a directory:

```shell
mkdir -p examples
cd examples
```

## Accessing the dashboard from the local machine

If we run minikube on an ec2 instance, we might want to access Testkube's dashboard on our local machine's browser.

One way is to use a combination of `minikube tunnel`, adding entries to the `/etc/hosts` file in the ec2 instance, and a SOCKS proxy in the local machine.

Alternatively, in the EC2 instance, forward EC2 ports (e.g. `49152` and `49153`) to `testkube-dashboard` and `testkube-api-server` services:

```sh
# In EC2 terminal 1
kubectl -n testkube port-forward svc/testkube-dashboard 49152:8080

# In EC2 terminal 2
kubectl -n testkube port-forward svc/testkube-api-server 49153:8088
```

Then, on our local machine, use two terminals for the following SSH tunnels:

```sh
# In local terminal 1
ssh -f -N -L 49152:localhost:49152 <ec2-dev>

# In local terminal 2
ssh -f -N -L 49153:localhost:49153 <ec2-dev>
```

We can then access the dashboard on
`localhost:49152`. When prompted for the Testkube API endpoint, use
`http://localhost:49153/v1`

For more details on the dashboard, refer to [Testkube dashboard documentation](https://docs.testkube.io/articles/testkube-dashboard/).
We can also interact with Testkube API's server via the above endpoint. For example, we can execute a test with the following:

```sh
curl -X POST localhost:49153/v1/tests/<test-name>/executions -d '{namespace: "testkube"}'
```

Refer to [OpenAPI specs](https://docs.testkube.io/openapi/) for more details.

## Creating a service to experiment with

Before we continue, let's deploy a simple service so that we can run our experiments against it. The following manifests deploy uninjected [HTTPBin](https://httpbin.org/) workload to an `httpbin` namespace. We will add sidecar injection in later sections and see how to adjust.

```sh
kubectl apply -f -<<EOF
apiVersion: v1
kind: Namespace
metadata:
  name: httpbin
---
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: httpbin
  name: httpbin
  labels:
    app: httpbin
spec:
  replicas: 1
  selector:
    matchLabels:
      app: httpbin
  template:
    metadata:
      labels:
        app: httpbin
    spec:
      containers:
      - name: httpbin
        image: docker.artifactory-ha.tri-ad.tech/kennethreitz/httpbin
        ports:
        - containerPort: 80
          name: http
---
apiVersion: v1
kind: Service
metadata:
  namespace: httpbin
  name: httpbin
  labels:
    app: httpbin
spec:
  ports:
  - name: http
    port: 80
    protocol: TCP
    targetPort: 80
  selector:
    app: httpbin
EOF
```

## Writing manifests

The most basic workflow involves interacting with two Custom Resources:

- Test Custom Resource ([docs](https://docs.testkube.io/articles/crds/#tests), [CRD](https://github.com/kubeshop/helm-charts/blob/testkube-1.11.174/charts/testkube-operator/templates/tests.testkube.io_tests.yaml))
- Executors Custom Resource ([docs](https://docs.testkube.io/articles/crds/#executors), [CRD](https://github.com/kubeshop/helm-charts/blob/testkube-1.11.174/charts/testkube-operator/templates/executor.testkube.io_executors.yaml))

### Test Custom Resource

Tests are single executor-oriented objects. By referencing `Executor` type handlers, tests can have different types (e.g. `curl/test`, `k6/script`, ... etc.) as long as the executors are installed in Testkube's namespace. Testkube ships with [pre-built executors types](https://docs.testkube.io/test-types/prebuilt-executor).

Let's create a simple test with the pre-built `curl/test` Executor against our `httpbin` workload:

```sh
cat <<EOF > test-curl-httpbin.yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  name: test-curl-httpbin
  namespace: testkube
spec:
  type: curl/test # Executor type handler
  content:
    type: string
    data: |-
      {
        "command": [
          "curl",
          "http://httpbin.httpbin.svc.cluster.local/headers",
          "-H",
          "'Accept: application/json'"
        ],
        "expected_status": "200"
      }
EOF
```

We can save the above file and apply it with:

```sh
kubectl apply -f test-curl-httpbin.yaml
```

Our new test is now visible in the Tests tab on the dashboard:
`http://localhost:49152/tests/executions/test-curl-httpbin`

Running this test shows results on the dashboard similar to:

```sh
[...]
🚚 Preparing test runner
running test [645c563cfe4d5bdfe674e491]
🚚 Preparing for test run
📦 Fetching test content from string...
✅ Content saved to path /tmp/test-content1204592122
🔑 Filling in the input templates
✅ Successfully filled the input templates
🔬 Executing in directory :
$ curl -is "http://httpbin.testkube.svc.cluster.local/headers" -H 'Accept: application/json'
HTTP/1.1 200 OK
Server: gunicorn/19.9.0
[...]
{
  "headers": {
    "'Accept": "application/json'",
    "Accept": "*/*",
    "Host": "httpbin.testkube.svc.cluster.local",
    "User-Agent": "curl/8.0.1"
  }
}

✅ Execution succeeded
✅ Test run succeeded
[...]
```

However, we might need to extend the pre-built Executor `curl/test` functionality. For instance, adding command line tools, specifying the entry point with commands and args or collecting specific files after test execution. In the next section, we will create a custom container executor.

### Executor Custom Resource

Sometimes, we might need to create a container with specific tools and binaries utilised in our test execution. Testkube allows defining and running custom Container Executors.

An Executor is a wrapper around a testing framework that runs as a Kubernetes job. It is a Docker container with particular binary(s) that is registered as an `Executor` CR in the cluster and referenced in the `Test` CR with a type handler (e.g. `curl/test`).

To define a custom Container Executor, first need an image for the custom container Executor. While we are free to choose any secure image we need from the artifactory, we created a custom `alpine` image with `curl` and `jq` installed for this demonstration. The image tag is:

```sh
docker.artifactory-ha.tri-ad.tech/wcm-cityos/testkube/alpine-curl-jq:0.1.0
```

Then, we can create a container `Executor` with the specifications:

```yaml
cat <<EOF > executor-alpine-curl-jq.yaml
apiVersion: executor.testkube.io/v1
kind: Executor
metadata:
  name: executor-alpine-curl-jq
  namespace: testkube
spec:
  executor_type: container
  types:
  - alpine-curl-jq/test # Executor type handler
  image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/testkube/alpine-curl-jq:0.1.0
EOF
```

We defined the above Executor with the following:

- `alpine-curl-jq/test`: Executor type handler that can be referenced in subsequent `Test` CRs
- a custom alpine image with additional command line tools (jq and curl)

Apply the above Executor with:

```sh
kubectl apply -f executor-alpine-curl-jq.yaml
````

Now let's create a test that uses the container Executor `alpine-curl-jq/test`:

```sh
cat <<EOF > test-curl-jq-httpbin.yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  name: test-curl-jq-httpbin
  namespace: testkube
spec:
  type: alpine-curl-jq/test
  executionRequest:
    command:
    - /bin/sh
    - -c
    - |-
        #!/bin/bash

        # custom entrypoint script

        set -euo pipefail

        curl -f "http://httpbin.httpbin.svc.cluster.local/headers" -H 'Accept: application/json' | jq .headers
EOF
```

Apply the above Test CR with the following:

```sh
kubectl apply -f test-curl-jq-httpbin.yaml
````

And running it from the dashboard returns:

```sh
...
{"type":"line","content":"✅ Initialization successful","time":"2023-05-11T06:22:32.1762189Z"}
{"type":"result","result":{"status":"running"},"time":"2023-05-11T06:22:32.176224368Z"}
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   140  100   140    0     0   1350      0 --:--:-- --:--:-- --:--:--  1359
{
  "Accept": "application/json",
  "Host": "httpbin.testkube.svc.cluster.local",
  "User-Agent": "curl/8.0.1"
}
...
```

So far, all of these operations and interactions are between uninjected workloads. The following section will show how to inject the httpbin workload to match Agora services closely.

### Test Triggers

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

## Using TestKube in Agora with  Sidecar Injected Workloads

By default, injected workloads only accept mutual TLS traffic (mtLS STRICT mode). As a result, our uninjected test workload faces issues when attempting to connect to injected workloads. For instance:

```sh
root@<some-uninjected-workload>:/# curl -f "http://<some-injected-service>.<namespace>.svc.cluster.local:<some-port>/"
* Trying 10.105.188.98...
* TCP_NODELAY set
* Connected to <some-injected-service>.<namespace>.svc.cluster.local (xx.xx.xxx.xx) port <some-port> (#0)
> GET / HTTP/1.1
> Host: <some-injected-service>.<namespace>.svc.cluster.local
> User-Agent: curl/7.61.1
> Accept: */*
>
* Recv failure: Connection reset by peer
* Closing connection 0
curl: (56) Recv failure: Connection reset by peer
```

Therefore, injecting our test workload with a sidecar is necessary to avoid the above issues.

First, let's modify httpbin resources to inject the sidecar:

```sh
kubectl apply -f -<<EOF
apiVersion: v1
kind: Namespace
metadata:
  name: httpbin
  labels:
    name: httpbin
    istio.io/rev: default
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: httpbin
  namespace: httpbin
  labels:
    app: httpbin
spec:
  replicas: 1
  selector:
    matchLabels:
      app: httpbin
  template:
    metadata:
      labels:
        app: httpbin
        istio.io/rev: default
    spec:
      containers:
      - name: httpbin
        image: docker.artifactory-ha.tri-ad.tech/kennethreitz/httpbin
        ports:
        - containerPort: 80
          name: http
EOF
```

Then, let's start modifying our `Test` resource.
Test executions with Testkube are essentially Kubernetes jobs with a pre-configured template. We can inject our Test workloads by modifying the default job template to add the label: `istio.io/rev: default` under `executionRequest` (refer to [Testkube documentation](https://docs.testkube.io/articles/creating-tests/#changing-the-default-job-template-used-for-test-execution) for more details):

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  [...]
spec:
  [...]
  executionRequest:
    # adjust the default job template used for execution
    jobTemplate: |
      apiVersion: batch/v1
      kind: Job
      spec:
        template:
          metadata:
            labels:
              # inject sidecar due to mTLS STRICT mode
              istio.io/rev: default
[...]
```

However, to complete the test job, we must add a command to quit the sidecar [(Istio issue reference)](https://github.com/istio/istio/issues/6324):

```sh
trap 'curl -sf -XPOST http://127.0.0.1:15020/quitquitquit' EXIT
```

Consequently, injected `Test` workloads MUST be able to send POST requests to the above URL.
note: [ambient mesh](https://istio.io/latest/blog/2022/introducing-ambient-mesh/) should not require such intervention.

Finally, the injected version of our `Test` workload would look like this:

```yaml
cat <<EOF > test-curl-httpbin-injected.yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  name: test-curl-httpbin-injected
  namespace: testkube
spec:
  type: alpine-curl-jq/test
  executionRequest:
    # adjust the default job template used for execution
    jobTemplate: |
      apiVersion: batch/v1
      kind: Job
      spec:
        template:
          metadata:
            labels:
              # inject sidecar due to mTLS STRICT mode
              istio.io/rev: default
    command:
    - /bin/sh
    - -c
    - |-
        #!/bin/bash

        # custom entrypoint script

        set -euo pipefail

        # on exit, stop the sidecar and complete the test job.
        trap 'curl -sf -X POST http://127.0.0.1:15020/quitquitquit' EXIT

        curl -f "http://httpbin.httpbin.svc.cluster.local/headers" -H 'Accept: application/json' | jq .headers
EOF
```

Apply the above Test with the following:

```sh
kubectl apply -f test-curl-httpbin-injected.yaml
````

And running it from the dashboard returns:

```sh
[...]
{"type":"line","content":"✅ Initialization successful","time":"2023-05-11T06:22:32.1762189Z"}
{"type":"result","result":{"status":"running"},"time":"2023-05-11T06:22:32.176224368Z"}
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   559  100   559    0     0   4379      0 --:--:-- --:--:-- --:--:--  4401
{
  "Accept": "application/json",
  "Host": "httpbin.httpbin.svc.cluster.local",
  "User-Agent": "curl/8.0.1",
  "X-Envoy-Attempt-Count": "1",
[...]
}
```

## GitHub checkout

Ideally, we store and maintain our test scripts in the repository next to our code. As such, may need to check out a directory from GitHub and add its contents to the container executor. In this case, we will need to create a secret that contains GitHub Personal Access Token for authentication:

```sh
kubectl -n testkube create secret generic city-github-token --from-literal=git-username=$GITHUB_USERNAME --from-literal=git-token=$GITHUB_TOKEN
```

Then, in the `Test` CustomResource add the following under `spec.content`:

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  [...]
spec:
  [...]
  # add files to the executor container from git
  content:
    type: git-dir
    repository:
      type: git
      uri: https://github.com/wp-wcm/city.git
      branch: main
      workingDir: path/to/file # checkout and run from this directory
      usernameSecret:
        name: city-github-token
        key: git-username
      tokenSecret:
        name: city-github-token
        key: git-token
[...]
```

Note: it is possible to use certificates instead of tokens.

## Test artifacts storage in MinIO

Adding `features: ["artifacts"]` to the Executor CR specifications instructs Testkube to collect and store artifacts generated by the tests:

```yaml
apiVersion: executor.testkube.io/v1
kind: Executor
metadata:
  [...]
spec:
  features:
    - artifacts
```

Once the test job is finished, a `scraper-job` collects the files in `volumeMountPath/dirs/` in the container and stores them in a MinIO bucket. The artifact collection can be specified in the Test CR with the following:

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  [...]
spec:
  [...]
    artifactRequest:
      storageClassName: <standard (for local) or testkube-tests-ebs-sc (for ci|lab|dev)>
      volumeMountPath: /<artifact-volume-mount-point>
      dirs:
        - path/to/artifacts/dir/
```

## Environment variables

To add and use environment variables in the container Executor, add the following to the `Test` CustomResource:

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  [...]
spec:
  [...]
  executionRequest:
    [...]
    variables:
      BASIC_VAR:
        name: BASIC_VAR
        type: basic # plaintext
        value: <some-value>
      SECRET_VAR:
        name: SECRET_VAR
        type: secret # secret variable appear prefixed with `RUNNER_SECRET_VAR_`
        valueFrom:
          secretKeyRef:
            name: <secret-name>
            key: <secret-key>
    args:
    # use the variables as arguments
    - $(BASIC_VAR)
    - $(RUNNER_SECRET_VAR_SECRET_VAR)
EOF
```

Note: although the environment variables are visible on the dashboard, and their values can be modified from there, flux reconciliation will revert to the committed values.

## Slack notifications

Channels can be added to the API server configmap to send Slack notifications on Testkube events. For instance, it is located [here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/testkube/patches/configmap-testkube-api-server-slack.patch.yaml) for the dev environment. 

For example, to send slack notifications to the channel `wcm-org-agora-infra-alerts` on the failure of the test `test-curl-httpbin-injected`, we add the following block to the configmap (under `.data.slack-config.json`):

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: testkube-api-server
data:
  # C05CVQRQKHZ: # wcm-org-agora-infra-alerts
  slack-config.json: |
    [
      {
        "ChannelID": "C05CVQRQKHZ",
        "selector": {},
        "testName": [
          "test-curl-httpbin-injected",
        ],
        "testSuiteName": [],
        "events": [
          "end-test-failed",
          "end-test-aborted",
          "end-test-timeout",
          "end-testsuite-failed",
          "end-testsuite-aborted",
          "end-testsuite-timeout"
        ]
      },
    ]
```

After creating and merging the PR with the desired changes, `testkube-api-server` must be restarted for the changes to apply. Please contact an Agora infrastructure team member for this operation.

Finally, we must add the Testkube [slack app](https://app.slack.com/apps-manage/TCSD68W5A/integrations/profile/A0591BJBL9W/permissions) to the target channel to start receiving the alerts.

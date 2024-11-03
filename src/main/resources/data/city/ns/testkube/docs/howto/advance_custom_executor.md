# Setup your own executor

## Table of contents

- [Setup your own executor](#setup-your-own-executor)
  - [Prerequisite](#Prerequisite)
  - [Executor Custom Resource](#Executor-Custom-Resource)
  
## Prerequisite
Please ensure you have completed local testkube setup by following this [Self Testing with Local TestKube](bootstrap_local_testkube.md) 

## Executor Custom Resource

Sometimes, we might need to create a container with specific tools and binaries utilized in our test execution. Testkube allows defining and running custom Container Executors.

An Executor is a wrapper around a testing framework that runs as a Kubernetes job. It is a Docker container with particular binary(s) that is registered as an `Executor` CR in the cluster and referenced in the `Test` CR with a type handler (e.g. `curl/test`).

### Prepare docker image

To define a custom Container Executor, firstly, you need an image for the custom container Executor. 

Of course, you are free to choose any secure image you need from the artifactory
, While we recommend you build your own image in OCI convention with ```iqqq = "IQQQ_WRAP"``` embedded (see [istio_quitquitquit](../../../istio_quitquitquit/README.md)) if you want your test to run in Agora cluster which requires test injection.  

### Define own executor

we created a custom `alpine` image with `curl` and `jq` installed for this demonstration. The image tag is:

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

### Define test using own executor

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


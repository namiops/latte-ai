# Setup local test

## Table of contents

- [Setup and run local test](#Setup-local-test)
  - [Prerequisite](#Prerequisite)
  - [Creating a local service to experiment with](#Creating-a-local-service-to-experiment-with)
  - [Setup and run local test](#Setup-and-run-local-test)
  
## Prerequisite
Please ensure your have compelted local testkube setup by this [Self Testing with Local TestKube](bootstrap_local_testkube.md) 

## Creating a local service to experiment with

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

## Setup and run local test

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
`http://localhost:8088/tests/executions/test-curl-httpbin`

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


# Setup injected test

## Table of contents

- [Setup and run injected test](#Setup-injected-test)
  - [Prerequisite](#Prerequisite)
  - [Creating an injected service to experiment with](#Creating-an-injected-service-to-experiment-with)
  - [Setup and run injected test](#Setup-and-run-injected-test)
  
## Prerequisite
Please ensure you have completed local testkube setup by following this [Self Testing with Local TestKube](bootstrap_local_testkube.md) 

## Why we need injected test
In Agora environment, services can only accept mutual TLS traffic (mTLS STRICT mode). As a result, our uninjected test workload will face issues when it runs in Agora environment and attempts to connect to the services. For instance:

```sh
root@agora-cluster-<some-uninjected-workload>:/# curl -f "http://<some-injected-service>.<namespace>.svc.cluster.local:<some-port>/"
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
Therefore, it is required to enable Istio injection for the test job to run correctly within Agora clusters. 
## Creating an injected service to experiment with
First, let's modify the httpbin resources to inject the sidecar:

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

## Setup and run injected test

To enable Istio injection for the test job, there are two key efforts in managing injected tests: one is to inject the Istio sidecar into the test container when the test starts, and the other is to terminate Istio when the test completes. 

Now, these efforts can be hidden at the test level by leveraging existing solutions at the Docker level. 

First step, you need to create your own test image embedding the command ```iqqq = "IQQQ_WRAP"```.  see below example
```
oci_image(
    name = "my-own-test-image",
    base = "@debian_12_slim_image",
    entrypoint = ["/bin/entrypoint.sh"],  # add entrypoint to enable IQQQ_WRAP
    exec_properties = {
        "Pool": "high_mem",
    },
    iqqq = "IQQQ_WRAP",
    tars = [
        ":.npmrc",
        ":entrypoint",
        ":home",
        ":passwd",
        ":group",
    ],
    user = "nonroot",
    visibility = ["//visibility:public"],
)
```
Above configuration will tell docker to terminate istio when main command is completed. see context here [istio_quitquitquit](../../../istio_quitquitquit/README.md)


Then, create your own custom executor (see [create custom executor](advance_custom_executor.md)) with base image in first step like below
```yaml
apiVersion: executor.testkube.io/v1
kind: Executor
metadata:
  name: my-own-executor
  namespace: testkube
spec:
  executor_type: container
  types:
  - alpine-curl-jq/test # Executor type handler
  image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/testkube/my-own-test-image
```   

The final step is to create your test with `my-own-executor`, then the test will be run and completed as a local test but with the workload injected automatically!

For more information, please refer to the project [curl-yq-executor](../../executor-images/curl-yq/) as an example. 

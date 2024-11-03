# Agora KNative Serving Onboarding Guideline
This is a guideline for developers who would like to deploy their services with KNative Services (KService) on the DEV cluster.
* [Agora KNative Serving Onboarding Guideline](#agora-knative-serving-onboarding-guideline)
  * [Example Service](#example-service)
  * [Namespace configuration](#namespace-configuration)
  * [Container configuration](#container-configuration)
    * [Container Port](#container-port)
  * [Image Registries](#image-registries)
  * [Endpoints](#endpoints)
    * [External Access](#external-access)
    * [Internal Access](#internal-access)
      * [Method 1: Directly call the generated Service (Recommended)](#method-1-directly-call-the-generated-service-recommended)
      * [Method 2: Call the gateway with a "Host" header](#method-2-call-the-gateway-with-a-host-header)
    * [Port forwarding for local debugging](#port-forwarding-for-local-debugging)
  * [Configure a Gateway per KService](#configure-a-gateway-per-kservice)
  * [Scaling pods](#scaling-pods)
    * [Overview](#overview)
    * [Autoscaling metrics](#autoscaling-metrics)
      * [Concurrency](#concurrency)
      * [RPS (Request per second)](#rps-request-per-second)
  * [Workspace setup in the next-gen environments](#workspace-setup-in-the-next-gen-environments)
  * [Known Issues](#known-issues)
    * [Incorrect status “Unknown” of KService resources on the legacy enviornments (lab \& dev)](#incorrect-status-unknown-of-kservice-resources-on-the-legacy-enviornments-lab--dev)
    * [Revision throws "failed to resolve image to digest" error](#revision-throws-failed-to-resolve-image-to-digest-error)
  * [FAQs](#faqs)
    * [After deploying KService, Revision is ready, but there is no pod showing up](#after-deploying-kservice-revision-is-ready-but-there-is-no-pod-showing-up)

## Example Service

- [Helloworld Go](../../../../k8s/common/lambda-sample/helloworld-go-0.1.0/service-helloworld-go.yaml)
- [Podinfo](../../../../k8s/common/lambda-sample/podinfo-0.1.0/service-podinfo.yaml)

## Namespace configuration

Make sure that your namespace is labeled with Istio label so that Istio sidecars are injected to your KService's pods.

## Container configuration

### Container Port
By default, a queue proxy, a side car injected by KNative Controller, is listening to port 8080 of the main container (A.K.A. `user-container`).
In case that your container exposes another port, you can configure it with `containerPort` as shown below taken from [Podinfo](../../../../k8s/common/lambda-sample/podinfo-0.1.0/service-podinfo.yaml):

```
spec:
  template:
    ...
    spec:
      containers:
        - image: docker.artifactory-ha.tri-ad.tech:443/stefanprodan/podinfo:6.3.6
          ports:
            - containerPort: 9898 # >> A port number of your app service
```


## Image Registries

Currently, we support these 2 registry hosts below for KService.
- docker.artifactory-ha.tri-ad.tech
- gcr.artifactory-ha.tri-ad.tech

Docker image tag resolution by KNative might fail because it cannot send a request to not-allowed registries. 
Please let Agora Infra team know if you would like to use other registries. 
The configuration is defined at `knative-serving-artifactory` ServiceEntry at `infrastructure/k8s/dev/istio-system/service_entries.yaml`.

## Endpoints

After KService objects (`service.serving.knative.dev/v1`) are successfully deployed, you can access your services by the 2 types of endpoints.

### External Access

**NOTE**: External access can be disabled by labeling a KService object with

```
metadata:
  labels:
    networking.knative.dev/visibility: cluster-local
```

The route to your service is published to the Ingress gateway at port 443 by default:

- Legacy env (ex. DEV): `ingressgateway` in `city-ingress` namespace
- The next-gen env (ex. DEV2): : `virtualgateway-default-city-pri-<hash>` in `city-private-ingress` namespace

Your service is not exposed to the Internet, and you can access it from the company network by the below URL.

- URL format: `https://<service_name>-<namespace>-lambda.<cluster_domain>`
- Example URL: 
  - Legacy env (ex. DEV): `https://helloworld-go-lambda-lambda.cityos-dev.woven-planet.tech`
  - The next-gen env (ex. DEV2): `https://helloworld-go-lambda-lambda.agora-dev.w3n.io`

### Internal Access

Note that the traffic actually flows a local gateway named `knative-local-gateway` in `isto-system` namespace.
The reason is that IP address of `<service_name>.<namespace>.svc.cluster.local` is mapped to local Gateway's Service, after `net-istio-controller` successfully reconciles networking resources.
The traffic through the gateway is required to gain benefits from Serverless deployment such as autoscaling and multiple revisions.


#### Method 1: Directly call the generated Service (Recommended)

You can call Kubernetes Service (SVC) generated per KService to access your service.
The SVC name is the same as your KService's name.
The type of SVC is ExternalName, and its IP address is mapped to KNative local gateway's address.

To access your KService locally, use `http://<service_name>.<namespace>.svc.cluster.local`.
For example, you can send an HTTP request to KService `helloworld-go` deployed in `lambda` namespace by `http://helloworld-go.lambda.svc.cluster.local`.

#### Method 2: Call the gateway with a "Host" header

To access your service, use a gateway address and add "Host" to a header like the below example
- Service URL:
  - Legacy env (ex. DEV): `knative-local-gateway.istio-system.svc.cluster.local`
  - The next-gen env (ex. LAB2): `knative-local-gateway.knative-serving.svc.cluster.local`
- Host: `<service_name>.<namespace>`
- Example Host: `helloworld-go.lambda`

For example, you can run `curl` from another pod to get a service status by:

```bash
$ curl  knative-local-gateway.istio-system.svc.cluster.local 
  -H "Host: helloworld-go.lambda"
```

### Port forwarding for local debugging

We cannot directly port-forward to KService service or pod, because its pod might be scaled down by default. 
KServing traffic needs to pass the activator to scale up the pod and decide which pod/revision should be sent to. So we need to hit the activator endpoint instead.

port forward to activator-service
```
kubectl port-forward -n knative-serving svc/activator-service 8888:80
```

send HTTP request with Knative-Serving headers

```
curl localhost:8888 -H "Knative-Serving-Namespace: <namespace>" -H "Knative-Serving-Revision: <kservice's revision name>"
```
e.g.
```
curl localhost:8888 -H "Knative-Serving-Namespace: lambda" -H "Knative-Serving-Revision: helloworld-go-00006"
```

## Configure a Gateway per KService

In general, as of KNative `v1.11.x`, we can configure KNative Gateways by modifying ConfigMap `config-istio`.
However, all of existing KServices' VirtualService will be bound to those Gateways.
In case of exposing the KService to the Internet (ex. binding with city-public-ingress Gateway), it is not acceptable due to security concerns.
Setting Gateway per KServe is still under discussion in 
- Issue [Customize Istio Virtual Service spec.gateways via Kservice](https://github.com/knative-extensions/net-istio/issues/1124)
- PR [feat: Filter on istio gateways based on ingress annotations](https://github.com/knative-extensions/net-istio/pull/1137)

Meanwhile waiting for a new feature, we can manually create a VirtualService by replicating the KNative-generated VirtualService with a specific Gateway.
For example, see a working sample in (Manual VirtualService)[../../../../k8s/common/lambda-sample/istio/virtualservice-helloworld-go-ingress-manual-0.1.0].
With the above sample, we can access KService by `https://helloworld-go-lambda-lambda.agora-lab.woven-planet.tech`, even though the KService is labeled with the below label for a private Service.
```
labels:
  networking.knative.dev/visibility: cluster-local
```
## Scaling pods

### Overview
A pod called `autoscaler` in `knative-serving` is taking a decision to adjust `replicas` of each KNative-generated Deployment by judging from amount of incoming traffic and Pod status. 
The autoscaler sends health-check probes to a queue-proxy container of each Revision's pod (port `9090` named `http-autometric`) to get metrics for scaling decision.

### Autoscaling metrics
In this runbook, we discuss only `concurrency` and `rps` (requests per second) metrics which are supported by the default autoscaler (KPA - KNative Pod Autoscaler).
For more details and HPA autoscaler, refer to the official doc [Configure metrics](https://knative.dev/docs/serving/autoscaling/autoscaling-metrics/).

For each Revision, there are 2 configurable values related to scaling metrics:
1. Target value
  - default for concurrency: 100
  - default for RPS: 200
2. Target utilization (percentage)
  - default: 70%

For both concurreny and RPS metrics, the actual value that the autoscaler uses for scaling decision is calculated by `(Target value x Target utilization)`. 
For an example, in case that the RPS metric and the default values are used, KNative tries to distribute traffic to every Revision's pod for maintaining 140 requests per second. 
If the average RPS across all pods are over 140 RPS, the autoscaler scale up pods in advance, before their workloads reach the maximum capacity (the target value).

#### Concurrency

Concurrency (the default metric for scaling) is the number of simultaneous requests that can be processed by each Revision's pod.
For more details, refer to the official doc [Concurrency](https://knative.dev/docs/serving/autoscaling/concurrency/)

The below example shows how to configure concurrency metrics in per Revision:
```
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: helloworld-go
  namespace: lambda
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/metric: "concurrency"
        autoscaling.knative.dev/target: "100"
        autoscaling.knative.dev/target-utilization-percentage: "80"
```

#### RPS (Request per second)
For more details, refer to the official doc [RPS](https://knative.dev/docs/serving/autoscaling/rps-target/). Please note the global config `container-concurrency-target-percentage` (default: 70) from `config-autoscaler` ConfigMap is also used to calculate the actual scaling target, even though its name is misleading.

The below example shows how to configure RPS metrics in per Revision:

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: helloworld-go
  namespace: lambda
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/metric: "rps"
        autoscaling.knative.dev/target: "40"
        autoscaling.knative.dev/target-utilization-percentage: "80"
```

**WARNING:** You should do load testing to find the maximum RPS that one pod can handle. 
Setting too high RPS can lead to the degraded performance, because the autoscaler don't scale up your pods.
For example, your pod can actually process requests at most 20 RPS, but `target` is 40 and `target-utilization-percentage` is 80. The actual scaling target becomes 32 RPS which your pod would never achieve.
As a result, your pod cannot trigger the autoscaler for scaling up and causes higher latency.


## Workspace setup in the next-gen environments

KNative's Gateways on Lab2/Dev2 have been configured differently from the legacy environments.
The diagram below describes the traffic flow of KNative Services.
**NOTE:** The namespace `lambda` generally represents your application namespace where KService is deployed.
![diagram](../static/Lab2_KNative_Traffic_Diagram.svg)

Since the new Workspace concept provides additional traffic restriction between workspaces, we need to explicitly configure WorkspaceSettings to allow connection from `knative` workspace to your application namespace (like `lambda` in the above example). 
This is required when the service isolation is turned on.
Here are 2 steps to do that:

1. In `infrastructure/k8s/environments/<cluster>/clusters/mgmt-east/knative/workspacesettings-knative.yaml`, add your workspace in `exportTo` and `importFrom`. For example:

```yaml
apiVersion: admin.gloo.solo.io/v2
kind: WorkspaceSettings
metadata:
  name: knative
  namespace: knative
spec: 
  exportTo:
  - workspaces:
    - name: city-gateway
    - name: <your_workspace>  # <---- your workspace goes here.
  importFrom:
  - workspaces:
    - name: <your_workspace>  # <---- your workspace goes here.
  options:
    serviceIsolation:
      enabled: true
```

2. In `infrastructure/k8s/environments/<cluster>/clusters/mgmt-east/<your_workspace>/workspacesettings-<your_workspace>.yaml`, add `knative` workspace in `exportTo` and `importFrom`. For example:

```yaml
apiVersion: admin.gloo.solo.io/v2
kind: WorkspaceSettings
metadata:
  name: <your_workspace>
  namespace: <your_workspace>
spec: 
  exportTo:
  - workspaces:
    - name: knative # <---- knative workspace goes here.
  importFrom:
  - workspaces:
    - name: knative # <---- knative workspace goes here.
  options:
    serviceIsolation:
      enabled: true
```

Refer to [infrastructure/k8s/environments/lab2/clusters/mgmt-east/lambda/workspacesettings-lambda.yaml](../../../../k8s/environments/lab2/clusters/mgmt-east/lambda/workspacesettings-lambda.yaml) for a working example.

## Known Issues

### Incorrect status “Unknown” of KService resources on the legacy enviornments (lab & dev)

Cause: There is only one ingress gateway in Agora for now and it is shared between other services. KNative tries to send health checking probes to other unrelated services. More detailed analysis can be found [here](https://docs.google.com/document/d/1oSzpTjncSZlTKWGlxpJZjXb7X0u_lVsSPin7TOUgYgk/edit#heading=h.dxy5qlo90dzg).

Action: please ignore this incorrect status, since your service and networking route are actually ready. 
Confirm that your Revision is ready by the below command and try to send any requests to the deployed KService.

```bash
kubectl get -n <your_namespace> revision
```

### Revision throws "failed to resolve image to digest" error
Cause: A docker image from a not-allowed registry is specified so that the KNative controller cannot reach to the registry for [Image Tag Resolution](https://knative.dev/docs/serving/tag-resolution/).

Action: refer to [Image Registries](#image-registries) section and change the docker registry to one in the allowed list.

## FAQs

### After deploying KService, Revision is ready, but there is no pod showing up

The reason is that the pod is scaled down to zero by default.
After fresh deployment, there might be no any incoming requests to that revision.
Then, it doesn't create any pod.

However, you can change this behavior by adding an annotation in the template.
For example, to scale pods down to at least 3 pods:

```yaml
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/min-scale: "3"
...
```

# Guidelines for maintaining Agora KNative Serving

This document describes guidelines for maintaining KNative Serving (namespace `knative-serving`) in Agora.
The target audience is the Infra team.


## Deploy KNative Serving

There are 3 main components:
  - [serving](https://github.com/knative/serving)
  - [net-istio](https://github.com/knative-sandbox/net-istio)
  - [net-certmanager](https://github.com/knative-sandbox/net-certmanager)

**NOTE:** Note that KNative Serving CRDs are put together inside a subfolder at `serving-<version>-<agora-revision>/crds`.

When you deploy KNative Eventing to a new environment, do not forget to include CRDs from the subfolder.
For example, the resource list in `kustomization.yaml` should be like:

```yaml
  - ../../common/knative-serving/net-certmanager-v1.10.1-agora1
  - ../../common/knative-serving/net-istio-v1.10.1-agora1
  - ../../common/knative-serving/serving-v1.10.2-agora1/crds
  - ../../common/knative-serving/serving-v1.10.2-agora1
```

## Upgrade KNative Serving
The patch version number of each component is independent. 
This means that we can install any same minor versions of 3 main components.
For example, we can install these components together:
- serving: `v1.10.2`
- net-istio: `v1.10.1`
- net-certmanager: `v1.10.1`

For installing the components, the script is available at [import script](../../../../k8s/common/knative-serving/bin/import).
You can run the script by the below example to install each component.
```
$ ./import -n serving -v v1.10.2
$ ./import -n net-istio -v v1.10.1
$ ./import -n net-certmanager -v v1.10.1
```

## Configuration
There are 13 ConfigMap objects deployed in `knative-serving` namespace. 
For flexibility of configuration in different environments, each ConfigMap is seperated by its type and its version under [configs folder](../../../../k8s/common/knative-serving/configs).
The description of configurations originally is in ConfigMaps' comments, but it is removed for readability.
You can find those descriptions in [Agora KNative ConfigMap](https://docs.google.com/spreadsheets/d/1AvCDwhzZ_l5sNeUMavxPobv1i98fujO1YHtgIpMJ5Hg/edit?usp=sharing) instead.

When you need to edit any ConfigMap objects, it is recommended to bump the config's patch version, and write e any changes you made in `CHANGELOG` file.

The following subsections listed by ConfigMap name explain important configurations you might need to know.

### ConfigMap config-autoscaler

#### Adjust a limit of scaling
It is dangerous for a whole cluster, if we do not limit the number of scaled pods. 
Heavy traffic might trigger pod scaling and, as a result, unlimited pod scaling consumes all memory and nodes in the cluster.

You can set the limit by the following:
- `max-scale-limit`: The hard limit of scaling. We MUST set this value. Otherwise, KNative controller uses a default value of 0 meaning that there is no limit.
- `max-scale`: A default limit of scaling. It MUST not exceed `max-scale-limit` value. However, it can be overriden by annotating KService template with `autoscaling.knative.dev/min-scale`.

### ConfigMap config-gc

#### Adjust the number of non-active Revisions

Revisions which are referenced by a Route are considered active.
The routing state can be checked at a label of each Revision:
```yaml
  labels:
    serving.knative.dev/routingState: active
```
In case of non-active retained Revisions, the value is `reserve`.

By default, KNative Serving controllers retain at least 20 non-active and upto 1000 non-active Revisions.
These number are too high and the down side is a waste of IP addresses and SVCs created per Revision.
Here is the default value in `config-gc`:
```yaml
  min-non-active-revisions: "20"
  max-non-active-revisions: "1000"
  retain-since-create-time: "48h"
  retain-since-last-active-time: "15h"
```

When these values are changed, KNative Serving controllers pick up the change automatically. 
You do not need to restart any controllers.

However, the changes are effective to each KService only when a new Revision is created.
For example, when `min-non-active-revisions` is decreased from `20` to `2` and `helloworld-go` KService has 20 non-active Revisions.
Those 18 non-active Revisions remain, until there is update in the `helloworld-go` KService and it creates a new Revision.

**NOTE:** This video [Taming Thousands of Knative Services for Thousands of Users - Martin Henke & Norman Böwing, IBM](https://youtu.be/rRWdfCu3b-s?t=1414) is recommended if you would like to learn more about tuning these values.


### ConfigMap config-istio

#### Configure Ingress and Local Gateways
There are 2 kinds of required gateways for KNative Serving traffic. If one of these kinds is missing, KService fails to start.

1. Ingress Gateway: listening incoming requests for public and private KServices.
2. Local Gateway: listening incoming requests for private KServices.

**NOTE:** Public in this case means access from the company network outside of the cluster (not the Internet).

We can choose gateways by specifying the following key and values in ConfigMap `config-istio`.
A name of Gateway and Service of corresponding Gateway are required.
Please note that the pattern in the key is a part of confguration as well.

1. Ingress Gateway config
   1. Format: `gateway.<namespace>.<gateway_name>: <service_name>.<namespace>.svc.cluster.local`
   2. Default: `gateway.knative-serving.knative-ingress-gateway: istio-ingressgateway.istio-system.svc.cluster.local`
   3. Example: `gateway.city-ingress.ingressgateway: ingressgateway.city-ingress.svc.cluster.local`
2. Local Gateway config
   1. Format: `local-gateway.<namespace>.<gateway_name>: <service_name>.<namespace>.svc.cluster.local`
   2. Default: `local-gateway.knative-serving.knative-local-gateway: knative-local-gateway.istio-system.svc.cluster.local`
   3. Example: `local-gateway.city-local-gateway.knative-local-gateway: knative-local-gateway.city-local-gateway.svc.cluster.local`

If there is no config, a default config will used for each type of Gateway.

#### Gateways in the next-gen environments

In the next-gen environments, these gateways are created by 2 kinds of Gloo resources: VirtualGateways and GatewayLifecycleManagers. 
The name of Service is configurable, but the name of Gateway is statically generated and looks like this pattern `virtualgateway-<shortened_name>-<hash>`. 
For example, `virtualgateway-knative-local-ga-e050430e9046a71063b1d6f5e1ca3ed` on Lab2's worker1-east.
The name doesn't change if the gateway configuration changes.

As a result, ConfigMap `config-istio` should be configured per cluster. 
When we create a new Gateway managed by Gloo Mesh for KNative traffic, we need to reconfigure `config-istio` in a separate pull request once the gateway is up and we know the generated name.
Refer to `infrastructure/k8s/environments/lab2/clusters/worker1-east/knative-serving/configmap-config-istio.yaml` for the working config.

### ConfigMap config-network

#### Configure service endpoint URLs
In Agora, you get a service endpoint with `<service_name>-<namespace>-lambda.<cluster_doamin>`.
The reason of putting everything in one subdomain is to align with Agora service policy and to have a valid TLS certificate.
For example, you deploy this KService on DEV cluster:
- name: `autobuild`
- namespace: `zonai`
- endpoint URL: `https://autobuild-zonai-lambda.cityos-dev.woven-planet.tech`

However, we can change behavior by setting `domain-template` with another template. 

## Test Cases

This section describes test steps to ensure all basic functionalities of KNative Serving. 
It is recommended to performs these tests after you deployed or upgraded KNative Serving.

### Case 1 : Deploy 1 KService

**Steps:**

1. Deploy 1 sample KService. You may take a manifest file from [service-helloworld-go.yaml](../../../../../infra/k8s/agora-lambda/speedway/common/helloworld-go-0.0.1/service-helloworld-go.yaml)

**Expected:**

1. KNative Serving controller (a pod named `controller`) creates 1 Revision and 1 Pod. 
2. Later, Revision's status becomes “Ready” and the Pod is scaled down to zero due within 5 minutes to no traffic.
3. SVC of the KService (type of ExternalName) points to the SVC of KNative local gateway. For example, 

```bash
$ kubectl get svc -n agora-lambda-prod h
elloworld-go
NAME            TYPE           CLUSTER-IP   EXTERNAL-IP                                                          PORT(S)   AGE
helloworld-go   ExternalName   <none>       knative-local-gateway.agora-knative-serving-prod.svc.cluster.local   80/TCP    30d
```

### Case 2 : Delete and recreate 1 KService

**Steps:**

1. Make sure there is one ready KService or you can follow the steps in [Case 1](#case-1--deploy-1-kservice).
2. Delete the KService.
3. Deploy the same KService again.

**Expected:**

1. After the 2nd step, all Revisions and Deployments owned by the KService should be deleted.
2. After the 3rd step, the controller reconciles 1 Revision and 1 Pod correctly. There should be no error of `NotOwned` at the KService.
3. Later, Revision status becomes “Ready” and the Pod is scaled down to zero due to no traffic.

### Case 3 : Create a new Revision

**Steps:**

1. Make sure there is one ready KService.
2. Edit an environment variable of the main container or an annotation to let the controller creates a new Revision.
3. Wait until the Revision becomes ready.
4. Repeat step 2 and 3 for 5 times.

**Expected:**

1. The controller creates 1 new Revision and Deployment. The number in Revision name should be increased by 1. The status of Revision becomes Ready within 5 minutes.
2. When you send a request, the pod of the latest Revision is scaled up and response to the request.
3. After the 4th step, there are at most 3 ready Revisions at time. The number of retained Revisions might be different according to the config at [configmap-config-gc.yaml](../../../../../infra/k8s/agora-knative-serving/speedway/common/knative-serving/configs/gc-0.0.2/configmap-config-gc.yaml)

### Case 4 : Scaling up from zero and scaling down to zero

**Steps:**

1. Deploy 1 KService without setting `min-scale` (the default value is 0)
2. Wait until its Revision becomes Ready and its Pod was scaled down to zero
3. Send 1 HTTP request locally to the endpoint of KService. You can check the guideline at [KService endpoints](./onboard-guideline.md#endpoints).

**Expected:**

1. The pod is scaled up to 1 and processes the request.
2. After 1 minute without any incoming request, the pod is scaled down to 0.

### Case 5 : Load test for scaling up pod to `max-scale`

**Steps:**

1. Deploy 1 KService with `max-scale` = 5 and `min-scale` = 0 (the hard limit in Agora is 20) by adding the below annotations.

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: your-service
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/min-scale: "0"
        autoscaling.knative.dev/max-scale: "5"
```

2. Wait until its Revision becomes Ready and its Pod was scaled down to zero.
3. Send HTTP requests locally by a load testing tool to the endpoint of KService. You can check the guideline at [KService endpoints](./onboard-guideline.md#endpoints).

**Expected:**

1. The pods are scaled up to 5 and processes the request.
2. After 1 minute without any incoming request, all pods are scaled down to 0.

### Case 6 : Traffic splitting

TBD

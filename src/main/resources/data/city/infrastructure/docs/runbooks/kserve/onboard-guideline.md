# Agora KServe Onboarding Guideline
This is a guideline for developers who would like to deploy AI services on the DEV cluster.

* [Agora KServe Onboarding Guideline](#agora-kserve-onboarding-guideline)
  * [Example Service](#example-service)
    * [Simple InferenceService](#simple-inferenceservice)
    * [GPU InferenceService with S3 credential](#gpu-inferenceservice-with-s3-credential)
  * [Credential for Storage Initializer](#credential-for-storage-initializer)
  * [Image Registries](#image-registries)
  * [Python Dependencies](#python-dependencies)
  * [Endpoints](#endpoints)
    * [External Access](#external-access)
    * [Internal Access](#internal-access)
  * [Migration to the next-gen environments](#migration-to-the-next-gen-environments)
    * [Workspace setup](#workspace-setup)
    * [Fix incompatibility with Istio CNI](#fix-incompatibility-with-istio-cni)
  * [Known Issues](#known-issues)
    * [Incorrect status “Unknown” of InferenceService resources on the legacy environments (lab \& dev)](#incorrect-status-unknown-of-inferenceservice-resources-on-the-legacy-environments-lab--dev)
  * [FAQs](#faqs)
    * [An InferenceService pod is running, but its model API service is not accessible](#an-inferenceservice-pod-is-running-but-its-model-api-service-is-not-accessible)
    * [A storage initializer container cannot start](#a-storage-initializer-container-cannot-start)

## Example Service

### Simple InferenceService
- [Sklearn Iris](../../../k8s/common/kserve-sample/inference-service/sklearn-iris-0.1.0)
- [Pytorch MNIST](../../../k8s/common/kserve-sample/inference-service/pytorch-mnist-0.1.0)

### GPU InferenceService with S3 credential
You can check out an example service from VisionAI team. We need to create a custom ServingRuntime for Vault secret injection.
- [ServingRuntime - VisionAI's Torchserve with S3 secret injection](../../../k8s/common/kserve-sample/serving-runtime/vai-torchserve-s3-vault-0.1.0)
- [InferenceService - VisionAI's person detector](../../../k8s/common/kserve-sample/inference-service/person-detector-vault-0.1.0)

## Credential for Storage Initializer
We recommend keeping credential for pulling your model files from a cloud storage (ex. S3 bucket) on Vault and using Vault Secret Injection to inject the secret to KServe’s pod.
Example for injecting S3 credential to InferenceService has been provided above.
More information is available at [Vault 101](https://developer.woven-city.toyota/docs/default/Component/vault-tutorial/en/00_index/). 
The secret injection requires a service account with `system:auth-delegator` cluster role.
The Agora DevRel team can support creating a Vault secret store and this service account.


## Image Registries
Because KServe is installed with Serverless mode over KNative, docker image tag resolution by KNative might fail because it cannot send a request to not-allowed registries. 
Refer to allowed registries in [KNative Serving - Onboard Guidline](../knative/serving/onboard-guideline.md#image-registries).

## Python Dependencies
Due to service mesh security, we allow access from certain namespaces (ex. `vision-ai` and `kserve-test`) to external hosts for installing Python dependencies as the following:
- pypi.org
- pypi.python.org
- files.pythonhosted.org

A KServe container might throw an error about failing python dependency installation because pip cannot connect to those not-allowed hosts. 
Please let Agora Infra team know if you would like to use other python package hosts. 
The configuration is defined at `kserve-python-dependencies` ServiceEntry at `infrastructure/k8s/dev/istio-system/service_entries.yaml`.

## Endpoints
After InferenceService objects are successfully deployed, you can access your KServe service by the 2 types of endpoints.

### External Access

**NOTE**: External access can be disabled by labeling an InferenceService object with 
```
metadata:
  labels:
    networking.knative.dev/visibility: cluster-local
```

The route to your service is published to the Ingress gateway (`ingressgateway` in `city-ingress` namespace) by default. Your service is not exposed to the Internet and you can access it from the company network by the below URL.

- URL format: `<inference_service_name>-predictor-<namespace>-lambda.cityos-dev.woven-planet.tech`
- Example URL: `person-detector-predictor-lambda.cityos-dev.woven-planet.tech`

### Internal Access
Another service inside Agora cluster can access your AI service by sending traffic to the following address:
- URL format: `<inference_service_name>-predictor.<namespace>.svc.cluster.local`
- Example URL: `pytorch-mnist-predictor.kserve-test.svc.cluster.local`

Note that the traffic actually flows a local gateway named `knative-local-gateway` in `isto-system` namespace.
The reason is that IP address of `<inference_service_name>-predictor.<namespace>.svc.cluster.local` is mapped to `knative-local-gateway.istio-system.svc.cluster.local` address.
The traffic through the gateway is required to gain benefits from Serverless deployment such as autoscaling and multiple revisions.

Another alternative method is using a gateway address directly and adding "Host" to a header like the below example
- Service URL:
  - Legacy env (ex. DEV): `knative-local-gateway.istio-system.svc.cluster.local`
  - The next-gen env (ex. LAB2): `knative-local-gateway.knative-serving.svc.cluster.local`
- Host: `<inference_service_name>-predictor.<namespace>`
- Example Host: `pytorch-mnist-predictor.kserve-test`

For example, you can run `curl` from another pod to get a service status by: 
```
$ curl  knative-local-gateway.istio-system.svc.cluster.local/v1/models 
  -H "pytorch-mnist-predictor.kserve-test"
```

## Migration to the next-gen environments

### Workspace setup
Workspace setup is required to allow traffic from KNative workspace to your application workspace where InferenceServices are deployed.
Refer to [Knative Onboarding Guideline - Workspace setup](../knative/serving/onboard-guideline.md#workspace-setup-in-the-next-gen-environments) for more details.

### Fix incompatibility with Istio CNI
The service mesh in the next-gen environments has Istio CNI plugin enabled.
As a result, an init container (such as KServe's storage initializer) cannot connect to any external services, because Envoy is not ready.
The issue can be fixed by running the init container by UID `1337`.
KServe has provided this feature since `v0.11.0-rc1` and you can do that by updating `InferenceService` like the below:
```
apiVersion: serving.kserve.io/v1beta1
kind: InferenceService
metadata:
  name: pytorch-mnist
  namespace: kserve-test
  annotations:
    # Add this new annotation to storage initializer with UID 1337
    serving.kserve.io/storage-initializer-uid: "1337"
spec:
  predictor:
    model:
      # KServe v0.11.0-rc1 controller cannot mutate a pod without `securityContext` setting.
      securityContext:
        allowPrivilegeEscalation: false
```

## Known Issues
Since KServe is deployed in Serverless mode, it tightly couples with KNative Serving.
Make sure to check [Known Issues](../knative/serving/onboard-guideline.md#known-issues) and [FAQs](../knative/serving/onboard-guideline.md#faqs) in KNative's runbook as well.

### Incorrect status “Unknown” of InferenceService resources on the legacy environments (lab & dev)
Action: please ignore this incorrect status, since your service and networking route are actually ready.

Cause: The issue is from KNative Serving side. Refer to known issues in [KNative Serving - Onboard Guideline](../knative/serving/onboard-guideline.md#known-issues).

## FAQs

### An InferenceService pod is running, but its model API service is not accessible
The model service worker running in background might fail to start, because it cannot install runtime dependencies (additional Python packages).
For example, in the case of Torchserve, the worker installs Python packages defined inside an MAR file.

The reason is that we need to explicitly allow those package hosts by Istio's ServiceEntry.
Otherwise, a pod is not allowed to make a connection to those hosts outside a service mesh.
To fix the issue, see [Python Dependencies](#python-dependencies). 

### A storage initializer container cannot start
Credentials for accessing cloud storage might be invalid.
If the issue happens in the next-gen environments and the container fails due to a connection problem, check out explanation and solution in [Fix incompatibility with Istio CNI](#fix-incompatability-with-istio-cni)

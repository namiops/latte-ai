# Agoractl Serverless

Plugin to generate Kubernetes (k8s) manifests for various serverless components, including services, brokers, and sources.

## Introduction

The 'serverless' plugin is designed to generate Kubernetes (k8s) manifests for various serverless components, including services, brokers, and sources.
    
Upon execution, it creates a manifest tailored to the selected serverless component. The generated manifest will be saved in a directory created by the script, with its name specified through the '--name' parameter.    

### Serverless Component Explanations:
#### Service
- https://developer.woven-city.toyota/docs/default/Component/serverless-tutorial/serverless-using-agoractl/01_creating-service/

##### Creation mode
- `Both manifest and application code` If you want the command to scaffold both manifest and application code for you, use `--application_at` and `--application_language`
- `Manifest only` leave out the `--application_at` and `--application_language` and the script will generate only k8s manifest
- `Application only` specify `--application_only` along with `--application_at` and `--application_language`

##### Accessing services created by Agoractl Serverless manually

You can access your serverless service via `curl` or even `web-browser` if you are within Woven by Toyota VPN.

A link to your lambda will be of a following format:
```
https://<SERVICE_NAME>-<SERVICE_NAMESPACE>-lambda.<CLUSTER_DOMAIN>
```

For example: https://minimal-service-sample-serverless-lambda.cityos-dev.woven-planet.tech

##### Querying serverless service status

To get an overall view of your serverless service use: 

```
kubectl get -n <NAMESPACE> kservice <NAME> -o yaml
```

##### Querying serverless service revisions

This is helpful to see why revision failed.

First - get a revision from the list:
```
kubectl get -n <NAMESPACE> revision
```

Then - see revision status:
```
kubectl get -n <NAMESPACE> revision <NAME> -o yaml
```

##### More about kservices

- https://github.com/wp-wcm/city/blob/main/infrastructure/docs/runbooks/knative/serving/onboard-guideline.md

#### Broker          

- https://developer.woven-city.toyota/docs/default/Component/serverless-tutorial/serverless-using-agoractl/02_creating-broker/

#### Source          

- https://developer.woven-city.toyota/docs/default/Component/serverless-tutorial/serverless-using-agoractl/03_creating-source/

#### Additional configuration
Serverless Bazel build targets often have additional configuration options that target power users, see [schema files](https://github.com/wp-wcm/city/tree/main/ns/serverless/ytt/templates) to see all available configuration options for services, brokers and sources.

Some configuration examples are

- [Configure serverless scale bounds](https://github.com/wp-wcm/city/blob/f84dd8cb71559b6d98643a6fc11f675cbedb5182/ns/serverless/ytt/templates/serverless_service_schema.yaml#L27)
- [Setting environment variables (Prometheus config)](https://github.com/wp-wcm/city/blob/f84dd8cb71559b6d98643a6fc11f675cbedb5182/infrastructure/k8s/common/serverless/log-collector/additional-configuration.yaml#L11-L14)

## Example: What can I build with serverless?
Some of the current serverless samples

- [`Minimal service`](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/serverless/demo/minimal-service-sample): The most minimal service that will run with a default [sockeye](https://github.com/n3wscott/sockeye) image.
- [`IoTA source`](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/serverless/demo/iota-source-sample): The IoTA source that subscribe to `test` tenant's devices in `*.*.logs` topic and it sinks the event to the above [Minimal service](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/serverless/demo/minimal-service-sample)
- [`Eventing demo`](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/serverless/demo/eventing-fork-sample/README.md): This is an example of using serverless as eventing, where we have service as a source with [cloud event player](https://github.com/ruromero/cloudevents-player) who forward event to broker. Then we set up triggers/sinks for a broker to vend them to branch services.

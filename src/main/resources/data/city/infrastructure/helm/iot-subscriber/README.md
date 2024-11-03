# IoT Subscriber Helm Chart


## Overview
This chart deploys a small IoT Subscriber. This is meant to be used by a developer to help with a small demonstration of how to talk to Agora's Iot Broker

For more details about the source code, refer to this [README](/ns/iot/subscriber/README.md) provided here


## Pre-requisites of using the chart
This chart is meant to help Agora users have an example of a working Subscriber that is listening for messages on the IoT Broker. The broker allows usage of Vault, which handles credentials for the subscriber to use for login purposes

For setup of how to setup Vault in a local minikube environment, please refer to the [README](/infrastructure/k8s/local/vault-example/administrator/README.md) here

For more details of how Vault works, you can refer to the [README](/infrastructure/k8s/local/vault-example/auto-renewal/README.md) here


## How to Use This Chart
The helm chart should be easy to install via `helm`

```shell
cd infrastructure/helm/iot-subscriber
helm install <deployment_name> . -n iot [--create-namespace] # use --create-namespace to allow Helm to make it if it doesn't exist
```
# Agora KNative Eventing Onboarding Guideline

* [Agora KNative Eventing Onboarding Guideline](#agora-knative-eventing-onboarding-guideline)
  * [Sample code](#sample-code)
    * [Source](#source)
    * [Broker](#broker)
    * [Trigger](#trigger)
    * [Sink](#sink)
  * [Workspace setup in the next-gen environments](#workspace-setup-in-the-next-gen-environments)

## Sample code

Here is a list of example code for learning the essential custom resources of KNative Eventing.
However, to develop your event-driven applications, we recommend to generate related files with `agoractl`.
See [How to do Serverless](https://developer.woven-city.toyota/docs/default/Component/serverless-tutorial/) for more details.

### Source

- [CloudEvent player](../../../../k8s/common/lambda-sample/kservice/cloudevent-player-0.1.0/): A debugging service for sending simple CloudEvent to Broker/Sink
  - **NOTE:** Modify environment variables for selecting a target Broker/Sink.
  - Sample screenshot: ![cloudevent-player-screenshot](../static/cloudevent-player-screenshot.png)
- [KafkaSource for local](../../../../k8s/common/lambda-sample/eventing/kafka/source-0.1.0/)
- [Isolated KafkaSource](../../../../k8s/common/lambda-sample/eventing/kafka/kafka-isolated-source-0.1.0/)

### Broker 

- [InMemory-channel Broker](../../../../k8s/common/lambda-sample/eventing/broker/mt-channel-0.1.0/)
- [Kafka-channel Broker for local](../../../../k8s/common/lambda-sample/eventing/broker/kafka-channel-0.1.0/)

### Trigger

**NOTE:** Modify broker, filter, and subscriber to match your eventing setup.

- [Trigger for Sockeye KService](../../../../k8s/common/lambda-sample/eventing/trigger/sockeye-0.1.0/)
- [Trigger for HelloworldGo KService](../../../../k8s/common/lambda-sample/eventing/trigger/helloworld-go-0.1.0/)
- [Trigger for Sockeye KService (Kafka-channel edition)](../../../../k8s/common/lambda-sample/eventing/trigger/sockeye-kafka-broker-0.1.0/)

### Sink

- [Sockeye KService](../../../../k8s/common/lambda-sample/kservice/sockeye-0.1.0/): A debugging service for showing CloudEvent objects as Web UI.
  - Logging output is also available in a pod.
  - Sample screenshot: ![sockeye-screenshot](../static/sockeye-screenshot.png)
- [KafkaSink for local](../../../../k8s/common/lambda-sample/eventing/kafka/sink-0.1.0/)

## Workspace setup in the next-gen environments

Update your workspace settings by following [KNative Serving - Workspace setup in the next-gen environments](../serving/onboard-guideline.md#workspace-setup-in-the-next-gen-environments).


Refer to [infrastructure/k8s/environments/lab2/clusters/mgmt-east/lambda/workspacesettings-lambda.yaml](../../../../k8s/environments/lab2/clusters/mgmt-east/lambda/workspacesettings-lambda.yaml) for a working example.


<!-- Below are the links used commonly in the document -->
[InMemory-channel Broker]:../../../../k8s/common/lambda-sample/eventing/broker/mt-channel-0.1.0/

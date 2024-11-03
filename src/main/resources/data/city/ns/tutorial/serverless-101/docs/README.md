# Introduction

## What is serverless?

Serverless applications are light-weight, event-driven applications commonly known as _lambdas_ that run in the cloud. 

In a serverless world, you only need to write your code and push it to the cloud. Beyond that, we take care of the deployment, resource scaling, routing, and all subsequent processes. 

This approach allows you to focus on your business logic without having to worry about the infrastructure.

### Benefits

The serverless platform provides the following benefits:

* It allows you to fully focus on application development without needing to touch Kubernetes networking and scaling configurations for deployment.
* Your application becomes automatically scalable. It can be even scaled down to zero when there are no requests for a while.
* Your application services can be released more quickly.
* Both layers of your serverless implementation will be managed by us:
  * Infrastructure layer (Agora Infrastructure Team)
  * Service layer (Agora Services Team)

### Architecture

It consists of the following parts:

* [**Knative:**](https://knative.dev/docs/) The core of the serverless architecture, built on top of Kubernetes. Consists of [serving](https://knative.dev/docs/concepts/#knative-serving) and [eventing](https://knative.dev/docs/concepts/#knative-eventing-the-event-driven-application-platform-for-kubernetes) components.  
* [**Bazel:**](https://bazel.build/) Build system used to create Docker images and Knative manifests. Agora provides custom serverless rules to help you quickly scaffold your Knative applications.
* [**Zebra:**](https://developer.woven-city.toyota/docs/default/component/zebra-service/) A GitHub Actions job within the Agora repository that automates and simplifies Knative manifest deployment.
* [**Agoractl:**](https://developer.woven-city.toyota/docs/default/component/agoractl-tutorial/) CLI tool for resource scaffolding and creating custom Bazel rules.

![img](assets/serverless-simple1.png)

### Capabilities

Currently, the following event-driven patterns are available:

![img](assets/serverless-capabilities.png)

## Serverless using Agoractl

[Agoractl](https://developer.woven-city.toyota/docs/default/component/agoractl-tutorial/) is a CLI tool that automates parts of the Agora infrastructure for users. Currently, it is required for creating several components within a serverless application, as shown in the diagram below:

![img](assets/agoractl-capabilities.png)

### Required components

You'll need to use Agoractl to generate the following components in order:

* **Service:** Deployable unit that handles and responds to events or function calls. It abstracts the workload and exposes it as a network service, enabling it to be discovered and interacted with. This is generated with Agoractl in two parts:
  * `Kubernetes manifests`: Kubernetes (k8s) manifests to deploy the application
  * `Application`: Application code based on specified language templates. The serverless plugin can create the foundational structure for your application, preparing it for deployment into service.
* **Broker:** Event hub or messaging layer that receives and forwards events within your serverless architecture. Brokers provide a mechanism to connect event producers and consumers (also known as _sinks_; see below). They allow you to send events to multiple services at the same time or customize how events are sent to your services.
* **Source:** Bridge between the external and serverless environments. It sends and receives data from available sources, converts this data into events, and forwards the events to the appropriate broker or directly to services for processing. Agora is compatible with three available sources:
    * _IoTA_: A RabbitMQ-based source using the AMQP plugin, the serverless plugin can help you to create a resource that will subscribe to your desired MQTT topic and then forward the message to your sink service.
    * _Kafka_: Reads messages stored in existing Apache Kafka topics, and sends those messages as CloudEvents through HTTP to its configured sink.
    * _Sink binding_: Directs a subject to a sink. A subject is a Kubernetes resource that embeds a PodSpec template and produces events, and a sink is an addressable Kubernetes object that can receive events.

Next, we will go through a step-by-step guide on how to create and deploy a simple serverless application:

* [**Serverless quickstart tutorial using Agoractl**](01_quickstart)

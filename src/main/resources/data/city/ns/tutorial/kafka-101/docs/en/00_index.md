# Welcome

This tutorial and associated code is here to help you understand Agora's
Service Bus. With the Service Bus, Agora is providing a common way for services
to enable asynchronous workflows. To do this we're using [**Apache Kafka**](https://kafka.apache.org/)

## Why Agora Picked Kafka

Kafka is a well-known event streaming platform that can handle large amounts of
data in a way that is:

* Performance
  * High throughput is one of the main features of Kafka; messages can be
    delivered at low latency and in high volume
* Scalable
* Integral
  * Streams are durable and fault-tolerant
* Highly Available
  * Works across multiple zones and multi-tenant/multi-cluster setups

For all of these reasons, Agora decided to try Kafka, for more details you can
refer to our team's [**Technical Note**](https://docs.google.com/document/d/1VhuUh3RzaSowKTjuq1NtDjQ9lfAHntJe4tLPSg0iAio/edit#heading=h.5qm13wuvtiz9) 


## When To Use Asynchronous Communication

In software development there are a few workflows that would work quite well
with an asynchronous design. Some examples can be:

* Making a UI responsive
  * An example of this would be a mobile app that has two buttons, the two
    buttons have two independent functions: one changes the color of the
    buttons and the other gets data. Pushing button one shouldn't block the
    other button's operation. Asynchronous programming can help with this
* Fire and Forget
  * A service needs to send off a Notification. The logic of sending the
    notification can be separated from the logic of when to send the
    notification; the service just knows that certain use cases require a
    notification to be sent out. With asynchronous programming we could send
    off a notification to another service and let that service handle the
    process, without having to wait for a response.
* Scaling Effectively on a Server
  * If we are running a back-end service that makes a lot of database calls,
    using asynchronous programming can help us to batch or have these calls
    made in a way where one doesn't interfere or wait for another.

!!! Note
  
    Its important to note in Agora **synchronous flows are perfectly acceptable
    and in a lot of cases, a better fit than an asynchronous flow**. The
    purpose of the service bus is to help provide a common way for services to
    work in an asynchronous manner, but synchronous flows are also not only
    okay, but sometimes necessary.

## What This Tutorial Covers

This tutorial will provide an example of two services, separated by namespaces,
that are using Kafka to communicate with each other, which is in its own
namespace. The **producer** produces one thing: butter. It will then
**publish** a message to the service bus on a **topic** letting people know
that butter has been made.

![kafka](./assets/kafka-overview.png)

The producer only needs to worry about making butter and then sending out a
notification that butter is made; whoever wants to eat that butter, or consume
it; it is a separate concern from the producer which means that the producer
doesn't need to wait for confirmation that someone has gotten the butter that
it has sent.

On the other side, we will have a **consumer** that is interested in the butter
that the producer creates. To accomplish its task, it will **subscribe** to the
**topic** that the producer is sending butter, and then consume the butter
made.

By having our services set up this way, our consumer and producer are loosely
coupled from each other; if the producer improves its butter making process, or
the consumer changes the way it consumes the butter, they won't affect each
other directly, nor impede each other's development.

## Pre-requisites For The Tutorial

This tutorial requires the following installed locally

* **Minikube**
  * Minikube requires a backing driver, and this tutorial is using **Docker**.
    You can find instructions [**here**](https://minikube.sigs.k8s.io/docs/drivers/docker/)
  * You can find instructions on how to do this on the [**minikube site**](https://minikube.sigs.k8s.io/docs/start/)
    for Windows, Mac, and Linux systems
  * Note: Default memory setting for minikube might not be sufficient for Kafka deployment. It is better to specify memory by adding ``--memory=4096`` when starting **minikube** cluster. If you fail to do so, try increase Docker memory config and go over the step again. 
* **Kubectl**
  * You can find instructions on how to install
    [**here**](https://kubernetes.io/docs/tasks/tools/) for Windows, Mac, and Linux

The tutorial also presumes you're working from the **source project root** which for our purposes is:

```shell
/ns/tutorial/kafka-101/
```

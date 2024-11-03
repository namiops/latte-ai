# Agora Documents

## Overview

Welcome! This directory is meant to house and store general documentation
related to Agora. Here, you can find resources on what Agora is, how Agora helps
its users, what services Agora provides (and does not provide), and more!

## What is Agora

* **Dedicated infrastructure**
  * One of Agora's primary responsibilities is the infrastructure that
    services in Woven City will run upon. Agora provides a team that is
    dedicated to improving and maintaining the infrastructure so that service
    teams do not have to.
* **Approved Platform**
  * Agora is an approved platform per
    the [Woven City Policy](https://docs.google.com/document/d/1ucZKV6in36CsDeQVMplapJdy6ALU0oCS-ME845XDPFA/edit#heading=h.ygqigbny0e0).
    This helps services to meet security, privacy, data, and other Woven City
    Policies in a more consistent and uniform way.
* **Common Services**
  * Agora provides several common services that are meant to be usable to all
    service teams. By providing these services, other teams don't need to
    reinvent the wheel. These services are meant to be typical requirements
    for a lot of software applications such as:
    * Notifications
    * Scheduling
    * Authentication and Authorization
    * Secrets Management
    * Common User Data
    * And More
* **Support and Advisement**
  * Agora provides services to our service teams to help them understand
    Agora, the various services they provide, and direct help in implementing
    certain uses of Agora if necessary.

## Agora Speedway

Speedway is Agora's latest Kubernetes cluster which hosts applications on [Stargate Multi-Cloud](https://portal.tmc-stargate.com/docs/default/Component/STARGATE-WELCOME-GUIDES/) (SMC) while also providing many custom resources that SMC doesn't have.

Details and steps to get on Speedway lies in [its latest documentation](https://developer.woven-city.toyota/docs/default/Component/agora-migrations-tutorial/speedway/).

## What common components does Agora have

Woven City MUST deliver a high quality and consistent user experience by providing key standardized features include:

* The ID authentication service, [Woven ID](/docs/default/Component/id-homepage)
* The authorization framework for Woven ID, [Drako](/docs/default/Component/drako-service)
* The service for managing additional information for the Woven ID, [Basic Users & Residents Registration (BURR)](/docs/default/Component/burr-v2)
* The [Certificate Vending Machine (CVM)](/docs/default/Component/cvm-service)
* The service to manage IoT devices and hardwares, [Agora IoT Platform (IoTA)](/docs/default/Component/iota-service)
* The service to manage data consent [Consent management](/docs/default/Component/consent-management-service/en/consent/)
* Built-in storage solutions such as [PostgreSQL](/docs/default/Component/postgres-service), [Redis](/docs/default/Component/redis-tutorial/en/00_index/), [Object Storage](/docs/default/Component/object-storage-service), etc.
* A [serverless platform](/docs/default/Component/serverless-tutorial) to support light-weight, event-driven applications
* The message bus service [Agora Kafka](/docs/default/component/kafka-service/01_quickstart/)
* The [CI](/docs/default/domain/agora-domain/monorepo/)/[CD](/docs/default/Component/agora-migrations-tutorial/speedway/citycd/) pipeline to deploy your Artifactories to the platform
* The [observability stack](https://developer.woven-city.toyota/docs/default/Component/agora-migrations-tutorial/speedway/observability-for-speedway/) and [Telemetry collector](https://developer.woven-city.toyota/docs/default/Component/telemetry-collector) to collect and visualize your metrics, log and trace
* Various other hosted tools such as [Deployment Preview](/docs/default/domain/agora-domain/development/deployment-preview/), [Testkube](https://github.com/wp-wcm/city/tree/main/ns/testkube/docs) to help improve your development velocity

As a cloud platform, Agora allows you to design and run applications efficiently. For example:

* Deploy RESTful APIs using the CI/CD pipeline and manage data with built-in storage solutions
* Use the authorization framework for secure communication between services and the message bus for privacy-compliant data exchange
* Register devices or hardware using the IoTA platform to communicate between devices and services deployed on Agora

Overall, you can design your application with the building blocks Agora provides and gain the platform team's support for running your service.

All of a sudden as a service developer, you are spending more time worrying about keeping your service upright rather than improving it.
Agora's job is to help service teams with handling or managing these requirements by helping provide tooling and common services that allow teams to minimize their effort.

[ref](https://docs.google.com/drawings/d/1faQcHo4rrq2QeVmtnYrKkhXszuGKQXIZsLANCYyp3uc/edit)

![mswithout](./assets/microservice_without_agora.jpg)

## How Agora Helps

### Streamlined Platform for Woven City Services

Agora identifies the common functionalities required across all Woven City services and serves as a dynamic incubator to support them, providing these services in a readily available and user-friendly manner that opens up rich APIs.

### Standardized Software Development

Agora hosts Woven City's software development platform, designed to enhance developer experience and ensure consistent user experiences through a scalable, standardized framework.

By providing shared components and comprehensive API documentation, Agora facilitates seamless collaboration.

#### [ref](https://app.diagrams.net/#G14X5mDov8NxfR-UjXWIHqwXpX1DanJO-L)

![overview](./assets/agora_ovewview.png)

## How to get Started

### Onboarding

Please feel free to go through the documentation at your own pace, but if you'd
like to learn more right away about how to get started, you can start with the
onboarding guide [here](./developers/agora-onboarding).

### Developer Portal

Our Developer Portal also provides a current catalog of all current systems,
services, APIs, and more that you can search at your leisure. If you're reading
this on the Developer Portal you can already start searching either on the left
side panel, or performing a Search at the top. Otherwise, please feel free to
reach the portal

* [Top page](https://developer.woven-city.toyota/)
* [Welcome page](https://developer.woven-city.toyota/welcome)

### Contact Us

You can also reach out to Agora team directly! The Agora Team provides an Ask Me
Anything (AMA) channel on
Woven's [Slack](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7).
The Developer Relations Team also provides Consultation hours if you would like
to sit down and get some more direct assistance or advice on how to approach
your project and how to work with Agora

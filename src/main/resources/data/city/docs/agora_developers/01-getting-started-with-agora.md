# Getting started with Agora

!!! info
    Agora, as an ever-evolving platform, is going through many rapid changes and the information in this documentation might not be up-to-date. If you encounter any issues, please reach out to us in the [Agora AMA Slack channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).

!!! tip
    Looking for Speedway? Please refer to the [corresponding documentation](/docs/default/Component/agora-migrations-tutorial/speedway/) for more information.

## Overview

This guide takes you through the sequence of how to complete a generic setup of the Agora platform that fits most use cases. It is divided into the following sections:

* [Basic environment:](#basic-environment) The OS, programming environment, and language you are using
* [Minimum setup:](#minimum-setup) The bare minimum setup you will need to deploy and access your services on the platform
* [Available components:](#available-components) Use-case dependent components that can be implemented on top of the minimum setup layer

Each section contains links to the relevant quick-start tutorials that guide you through the steps to implement the necessary tools/components.

### Basic environment

#### Operating system

The Agora platform is built for Linux x86_64, so we strongly recommend performing your setup in one of the following environments:

* Linux PC
* macOS or Windows with a remote Linux instance
* Windows subsystem for Linux (WSL)

**Note:** You may choose to use macOS or Windows, but please note that you may run into issues and we do not provide support or documentation for these systems.

#### Programming environment

You will need to have the following programming tools installed on your system:

* [Git](https://docs.github.com/en/get-started/quickstart/set-up-git)
* [Bazel](/docs/default/domain/agora-domain/development/bazel/#installing-bazel)

#### Programming language

Agora is compatible with most programming languages. For a complete list, as well as more information on developing in each language, go [here](https://github.com/wp-wcm/city/tree/main/docs/development).

### Minimum setup

A minimum instance of Agora can be set up in the following steps:

#### Onboarding to our GitHub repository

##### Obtain GitHub EMU access

**Note:** To get access to the the WCM Github EMU organization, you need to have a Woven Planet e-mail address.

1. Go to [this link](https://portal.tmc-stargate.com/projects/76). If prompted to sign in using SSO to Global Access Center, please do so.
1. You should see an error message that says "_You do not have permission to view this project. Request access to view._". Fill in the _Business Justification_ textbox and click _Request access_; you can use the following justification: "I would like to obtain access to the WCM Github EMU organization."
1. The organization owners will approve your request. If you need immediate access, notify the @wcm-cicd team on the #wcm-cicd-support Slack channel and specify which team(s) you wish to be added to (see below).
1. After your request is approved, the system may take up to 40 minutes to create your account. Once your account is created, specify which team(s) you wish to be added to by adding yourself under the team(s) in [this file](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/ci/github/config.auto.tfvars.json) and making a pull request.

##### Clone the monorepo

1. Set up your SSH key on GitHub. On the [SSH and GPG keys page](https://github.com/settings/keys), under the authentication key for your VM, select _Configure SSO_ and authorize `wp-wcm`.
1. You will need to set up your _container image_, and generate a _YAML manifest file_ to deploy your image. They can be found in our GitHub repository. Clone it with the command:
`git clone git@github.com:wp-wcm/city.git`.
1. **Note:** For developers who already have a working application and want to deploy it to Agora, set up your own branch and CI/CD with the following steps:
    * First, create your own branch in the repository. Modify [`git-city.yaml`](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/flux-system/sources/git-city.yaml) to point to your branch instead of `/main`, then commit and push these changes.
    * Next, set up CI/CD using FluxCD and run the `bin/bootstrap` script by following the steps outlined [here](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/README.md#bootstrap-fluxcd).

##### Set up your namespace

You need a namespace for your project. To create one, make the following `_namespace.yaml` file in `city/infrastructure/k8s/common/<YourNamespace>`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: <YourNamespace>
  labels:
    istio.io/rev: default
```

**Note:** To create your namespace, you should already have a tenant set up for your team. If you don't have an existing tenant yet, see [this guide](02-tenant-ns-quickstart.md) for instructions on how to set it up.

### Available components

After you have set up the minimum layers for deployment, you can mix and match the services you need for your use case. Below is a list of components you can integrate into your Agora platform according to their respective functions.

#### Data and storage

Solutions for storing and transmitting data on the platform

* [PostgreSQL Operator](/docs/default/Component/postgres-service/01_postgres-zebra/): database for relational data
* [Redis](/docs/default/Component/redis-tutorial/en/01_cluster_exploration/#deploy-redis): for transient data storage
* [Secure KVS (CouchDB)](/docs/default/component/securekvs-tutorial/en/01_getting_started/): non-relation database with encryption

##### Messaging

* [Apicurio](/docs/default/component/apicurio-service/02_usage/): schema registry for automating data verification and data flows
* [Kafka](/docs/default/Component/kafka-tutorial/en/01_kafka_setup/): scalable event streaming platform that can work across multiple zones and cluster setups

#### Identity and access management

For authenticating and authorizing your users

* [Certificate Vending Machine (CVM)](/docs/default/Component/cvm-service/samples/): service for signing and returning certificates to perform mutual TLS with backend services deployed on Agora
* [CityService Operator](/docs/default/Component/city-service-operator-service/): configure traffic and security settings for users
* [Drako](/docs/default/Component/drako-service): Agora's policy decision point (PDP) system configured directly through Kubernetes
* [SPIRE/SPIFFE](/docs/default/component/spire/): for secure identification of systems and service-to-service communication

#### Internet of things (IoT)

For integrating IoT devices and services into your platform

* [Agora IoT Platform (IoTA)](/docs/default/Component/iota-service): system for grouping devices and provisioning them to communicate with the Agora cluster and IoT message broker
* [RabbitMQ](/docs/default/component/iota-service/Concepts/Data%20Broker/): data broker of choice for IoTA

#### Observability

For visualizing and tracking the metrics of your components

* [Grafana](/docs/default/Component/grafana-tutorial/en/00_index/): Agora's primary visualization tool
* [Prometheus](/docs/default/Component/prometheus-tutorial/en/00_index/): freeware for event monitoring and alerts
* Jaeger: coming soon
* Kiali: coming soon
* Loki: coming soon

#### Services

Agora-native and third-party services that you can deploy to the platform

Native:

* [Agoralctl](/docs/default/Component/agoractl-tutorial/01_manual/): CLI tool for automating parts of the Agora infrastructure for users
* [Agora Serverless](/docs/default/component/scheduler-service/#quickstart): A plugin to generate Kubernetes (k8s) manifests for serverless components such as services, brokers, and sources.
* [Telemetry collector](/docs/default/component/telemetry-collector)
* [Notification](/docs/default/component/notification-service/)
* [Scheduler](/docs/default/component/scheduler-service/#quickstart)
* [Zebra](/docs/default/Component/zebra-service/02_quickstart/): code abstraction tool that can be customized for a variety of use cases

External:

* [Face Identifier](/docs/default/Component/face-identifier-service)
* [Map](/docs/default/Component/map-service/)

#### User data privacy and security

Manage the collection, access, and usage of personal data stored on the platform

* [Basic Users and Residents Register (BURR)](/docs/default/Component/burr-v2): a backend service to manage personal information of Woven ID users for Test Course business and its clients.
* [Consent management](/docs/default/component/consent-management-service/en/consent/): mechanism that enforces access to personal information based on cluster service identity
* [Vault](/docs/default/domain/agora-domain/agora_developers/vault/01_vault_overview/#working-with-vault): identity-based secrets and gated encryption management system

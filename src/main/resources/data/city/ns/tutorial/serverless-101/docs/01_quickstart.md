# Quickstart tutorial


## Overview

This quickstart tutorial shows you how to deploy a demo serverless application that sends and receives Slack messages with a RabbitMQ source. Through it, you will learn how to create the basic components as shown below using Agoractl.

![img](assets/serverless_design_10.png)


## What you'll learn

From this tutorial, you will learn how to:

* [**1. Set up your project folder**](#1-set-up-your-project-folder)
* [**2. Create your service**](#2-create-your-service)
* [**3. Create your broker**](#3-create-your-broker)
* [**4. Configure your source and deploy your service**](#4-configure-your-source-and-deploy-your-service)

For a working example, see [here](https://github.com/wp-wcm/city/tree/main/infra/k8s/dev/agora-tenant/serverless-demo).

## What you’ll need

Before starting this tutorial, you should have already read the [conceptual introduction to serverless](README.md). You should also have the following tools and files in place. If you haven't done so, use the links below to get started on the respective pre-requisites:

* Bazel installed using [Bazelisk](https://bazel.build/install/bazelisk)
* Standalone version of [Kustomize](https://kubectl.docs.kubernetes.io/installation/kustomize) for service scaffolding
* An existing [tenant and namespace](https://github.com/wp-wcm/city/blob/main/ns/tutorial/agora-101/docs/01_tenant-ns-quickstart.md)


### Programming language

For creating your service, you can use any language capable of starting an HTTP server. However, we strongly recommend using one of the following Agoractl-supported languages to reduce the overhead of manually wiring your application to Bazel:

* Go
* Rust
* Java
* Python

For demonstration purposes, this tutorial uses Go as the language of choice.

## Steps

### 1. Set up your project folder

First, we need a subdirectory to store our application code. Under `city/projects/`, make a new folder called `<YourProjectFolder>`. 

### 2. Create your service

Now we are ready to start creating our service.

In Agoractl, run the following command:

```
$ cd infra/k8s/dev/<YourNamespace>/<YourServerlessProject>
$ bazel run //ns/agoractl -- serverless create service \
    --name=<YourService> \
    --namespace=<YourNamespace> \
    --application_at=/projects/<YourProjectFolder>/<YourService> \
    --application_language=<YourAppLanguage>
```

This results in the following folder structures:

* `<YourServerlessProject>`: Located under your namespace subdirectory, for storing your YAML files.

    ```
    $ tree
    .
    ├── kustomization.yaml
    └── <YourService>
        └── BUILD

    2 directories, 2 files
    ```

* `<YourService>`: Located in your project folder; stores your application code. 

    ```
    $ tree
    .
    └── <YourService>
        ├── BUILD
        └── main.go

    6 directories, 10 files
    ```

For other helpful commands and configurations, see [References: Service](references.md#service).

With that, you have generated a simple working service that can be auto-deployed using Zebra when you make a pull request. For this tutorial, we will continue creating all the components for our service before committing the changes.  

### 3. Create your broker

Next, we need to create a broker for our service. 

Run the following command in Agoractl:

```
$ bazel run //ns/agoractl -- serverless create broker \
    --name=<YourBroker> \
    --namespace=<YourNamespace> \
    --sink=//infra/k8s/dev/<YourNamespace>/<YourServerlessProject>/<ServerlessComponentFolder>:<ServerlessComponent>
```

In the above command, `<ServerlessComponentFolder>` refers to the subfolder of your serverless project that contains the relevant component (in this case your broker), and `<ServerlessComponent>` corresponds to the Bazel target name for that component. (See [here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/iot/iota-ota-0.0.2/ota-source-binding/BUILD#L8) for an example.)

This will give `<YourServerlessProject>` folder the structure below:

```
$ tree
.
├── kustomization.yaml
├── <YourBroker>
│   └── BUILD
└── <YourService>
    └── BUILD

3 directories, 3 files
```

For other helpful commands and configurations, see [References: Broker](references.md#broker).

### 4. Configure your source and deploy your service

The final step before our service can be deployed is to set up the source. Agora is compatible with the following three source types:

* [**IoTA**](#iota-source)
* [**Kafka**](#kafka-source)
* [**Sink binding**](#sink-binding)

For other helpful commands and configurations, see [References: Source](references.md#source).

#### IoTA source

Run the Agoractl command below:

```
$ bazel run //ns/agoractl -- serverless create source iota \
            --name=<YourSource> \
            --namespace=<YourNamespace> \
            --tenant=test \
            --topic="*.*.<YourTopic>" \
            --sink=//infra/k8s/dev/<YourNamespace>/<YourServerlessProject>/<ServerlessComponentFolder>:<ServerlessComponent>
```

!!! Tip
    The \* (asterisk) in the topic value represents a wildcard entry, meaning that `*.*.<YourTopic>` can match with topic names like `a.b.<YourTopic>`, `group.test.<YourTopic>`, and so on.


#### Kafka source

Run the Agoractl command below:

```
$ bazel run //ns/agoractl -- serverless create source kafka \
            --name=<YourSource> \
            --namespace=<YourNamespace> \     
            --consumer_group="dispatcher" \            
            --topic=<YourTopic1> \        
            --topic=<YourTopic2> \ 
            --sink=//infra/k8s/dev/<YourNamespace>/<YourServerlessProject>/<ServerlessComponentFolder>:<ServerlessComponent>
```

#### Sink binding

Run the Agoractl command below:

```
$ bazel run //ns/agoractl -- serverless create source binding \
            --name=<YourSource> \
            --namespace=<YourNamespace> \
            --origin_kind=Deployment \
            --origin_name=app-deployment \
            --origin_namespace=<YourNamespace> \
            --sink=//infra/k8s/dev/<YourNamespace>/<YourServerlessProject>/<ServerlessComponentFolder>:<ServerlessComponent>
```

#### Folder structure output

This should result in the following structure for `<YourServerlessProject>`:

```
$ tree
.
├── kustomization.yaml
├── <YourSource>
│   └── BUILD
├── <YourBroker>
│   └── BUILD
└── <YourService>
    └── BUILD

4 directories, 4 files
```

Now that you have created all the necessary components for your service, you can commit the changes and make a pull request. Zebra will detect and run the `BUILD` files to generate all the manifest files required for deployment.

## Conclusion

Now you have learned how to deploy a simple serverless service and send/receive requests using Agoractl!

For a more extensive example with different services and sources, see [Serverless demo](https://developer.woven-city.toyota/docs/default/component/serverless-tutorial/serverless-using-agoractl/04_serverless-demo/).

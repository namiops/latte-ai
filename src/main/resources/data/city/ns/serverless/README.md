# Serverless
[TN-0333](http://go/tn-0333)

Serverless applications are light-weight, event-driven applications commonly known as lambdas that run in the cloud.

In a serverless world, you only need to write your code and push it to the cloud. Beyond that, we take care of the deployment, resource scaling, routing, and all subsequent processes.

This approach allows you to focus on your business logic without having to worry about the infrastructure.

## Architecture

### Pieces of the puzzle

Serverless consists of multiple moving parts:

1. [Knative](#knative).
2. [Bazel](#bazel).
3. [Zebra](#zebra).
4. [AgoraCTL](#agoractl).

#### Knative

[Knative (Kn)](https://knative.dev/docs/) is the core of serverless architecture. 
This technology provides serverless capabilities built on top of [Kubernetes (k8s)](https://kubernetes.io/).

It consists of two parts: 
[serving](https://knative.dev/docs/concepts/#knative-serving) and 
[eventing](https://knative.dev/docs/concepts/#knative-eventing-the-event-driven-application-platform-for-kubernetes). 
Both are supported in Agora Serverless.

To rapidly deploy applications and other event driven helper features with Kn, users have to create Kn-managed
[custom resources (CRs)](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/).
This is often tedious and requires k8s and Kn knowledge, but Agora Serverless simplifies this process.

#### Bazel

[Bazel](https://bazel.build/) is a build system used in Agora. In Serverless, it is used to create Docker images as well as Kn manifests
using [bazel rules](https://bazel.build/extending/rules). Agora prepared a set of Serverless Rules in order to help its tenants to
quickly scaffold Kn applications.

#### Zebra

[Zebra](https://developer.woven-city.toyota/docs/default/component/zebra-service/)
is a GitHub job within Agora's monorepo built to automatically invoke specific bazel rules upon PR creation. 
Serverless uses Zebra to simplify Kn manifest deployment.

#### AgoraCTL

[AgoraCTL](https://developer.woven-city.toyota/docs/default/component/agoractl-tutorial/)
is a commandline tool that helps Agora users to scaffold various manifests. 
Serverless uses AgoraCTL to help users create Serverless Bazel Rules that will be invoked by Zebra.

### Connecting the dots

Now that we know what Serverless consists of, let's take a look how these parts fit together in the architecture.

![](https://github.com/wp-wcm/city/blob/main/ns/serverless/docs/assets/serverless-architecture.png)

1. Using AgoraCTL, the user scaffolds Serverless Bazel Targets (SBTs) and a code template 
    in one of the following languages: `Golang`, `Rust`, `Python` or `Java`. 
    They can then extend a code template with their own business logic.
2. Once ready, the user pushes a PR to Agora's monorepo.
   - Upon pushing, Zebra will invoke SBTs and create an additional commit with generated manifests into the same PR.
   - After Zebra's commit is in, all tests will pass and the PR will be ready for merging.
3. During the merge into `main`, previously built manifests will be synced into k8s by [Flux](https://fluxcd.io/) 
    and images built from users' code will be pushed to [Artifatory](https://artifactory-ha.tri-ad.tech/).
4. Once CRs defined by Kn manifests are created in k8s by Flux, Kn will deploy Serverless application to the cluster.
    The application will use the image previously pushed to Artifactory.
5. After creation, users can query their serverless resources using `kubectl`.

> **⚠️ _IMPORTANT NOTE:_** Serverless code generation works inside [Agora's monorepo](https://github.com/wp-wcm/city/tree/main) ONLY!

### What event-driven patterns are available at the moment?

![](https://github.com/wp-wcm/city/blob/main/ns/serverless/docs/assets/serverless-capabilities.png)

### What can AgoraCTL scaffold?

![](https://github.com/wp-wcm/city/blob/main/ns/serverless/docs/assets/agoractl-capabilities.png)

## What's next?

### Start with AgoraCTL

An entry point for the user of Serverless is [AgoraCTL](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial).

Please follow the link above and the provided instructions to create your very own Serverless Project!

### Source code

[Serverless package](https://github.com/wp-wcm/city/tree/main/ns/serverless) contains serverless boilerplate code generation scripts and examples.

### Real-world applications powered by Agora Serverless
- [Log Collector](https://github.com/wp-wcm/city/tree/main/ns/serverless/log-collector)
- [IoTA OTA](https://github.com/wp-wcm/city/tree/main/ns/iot/iota-ota)

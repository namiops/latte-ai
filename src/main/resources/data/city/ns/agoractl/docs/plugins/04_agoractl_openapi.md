# Agoractl OpenAPI Plugin

Plugin to generate compilable and deployable service code from an OpenAPI specification.

## Introduction

Agora developers are encouraged to use the [OpenAPI](https://www.openapis.org/) specification when developing services
and clients to interact them, as it guarantees that the client and server side implementations of the API do not get
out of sync, and makes it easier for third parties to write code that interacts with the service.

To assist in this endeavour, the [OpenAPI Generator](https://openapi-generator.tech/) project exists.  This is an
application that takes a YAML file that follows the OpenAPI specification, and generates compilable and deployable
code from it.  The code can be generated for either the client side or the server side and can be generated in a large
number of languages.  Once generated, the code can (usually) be compiled and executed locally, without any manual
changes being required.

The OpenAPI plugin uses this application to generate services that can be deployed on the Agora platform.  First, it
uses the generator to generate code that can be compiled and executed locally, and then it applies some tweaks to that
code, such as the addition of Bazel _BUILD_ files, that can be used to build the service into a Docker image.  It will
also scan the monorepo's language-specifiy dependency files for missing dependencies and add any that are required.

This Docker image created using this _BUILD_ file is then ready to be deployed onto an Agora cluster.  This can be
done with the help of the [Service Manifests](06_agoractl_service_manifests.md) plugin.

## Supported Languages

The OpenAPI plugin currently supports the generation of Go, JavaScript/TypeScript and Rust services.  It is still
relatively new, so the number of OpenAPI specification files that it has been tested is still relatively small.

Please take this into account if you use it, and feel free to contact the Developer Relations team if you run into
problems!

## Usage

The plugin uses an OpenAPI generator running inside of Agora, so you must be connected to the Internet and inside the
Woven by Toyota network, or connected via VPN to use it.

The plugin requires four arguments arguments to run, the most important of which is the YAML file containing the
OpenAPI specification for which to generate the service.  Here is an example of using the plugin to generate a service:

```shell
bazel run //ns/agoractl -- openapi <name> <namespace_name> <openapi_spec> <generator>
```

For some languages, a single manual step will be required in order to build the project after it is generated.  This
step will be explained by the plugin, along with details of how to build and run the target image.

## Arguments

The arguments are explained in detail in the plugin itself, so will not be repeated here.  To see this documentation,
use the following command:

```shell
bazel run //ns/agoractl -- openapi --help
```

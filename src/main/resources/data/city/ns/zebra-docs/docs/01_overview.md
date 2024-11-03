# Overview

## What is code abstraction?

Agora requires a large number of configuration files and other boilerplate to set up services - these control many aspects of the system, such as storage, access to the message bus, and configuration for the service itself at the mesh level. 

In order to make onboarding as simple as possible for service teams, we use abstraction and tooling to ensure that service developers only need to specify the minimum required settings, and nothing more.

## Components of code abstraction

The heavy lifting for zebra is done via Bazel. Bazel is a build system that ensures reproducibility, meaning that inputs and outputs to parts of the build process must be explicitly specified in advance. Terminology and concepts are greatly simplified here, please see the [Bazel docs](https://bazel.build/) for full details.

Other components include:

* *Target instantiation*: Each package in Bazel requires a BUILD file. The instructions in this file, including target instantiation, provide explicit input files and declare what will be output.

* *Toolchains*: Bazel requires some setup in order to use additional software, referred to as a `toolchain`, as part of the build process. In many cases, these tools are already present, if not, they will need to be added - please see the [Extending](04_extending.md) section. 

* *Rules*: Bazel executes its build system via rules, which provide inputs and outputs for segments of the build process. Creating a new extension for Zebra involves writing rules, otherwise only target instantiation is required. 



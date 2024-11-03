<!-- TOC -->

- [Why do we need k8s-tools?](#why-do-we-need-k8s-tools)
- [How to build and push to artifactory](#how-to-build-and-push-to-artifactory)
- [Next steps improvement](#next-steps-improvement)

<!-- /TOC -->

## Why do we need k8s-tools?
- These tools will be used in CI runner
- Ensure every team members have the same toolsets and version 
- Avoid outdated and/or inconsistent behaviors in development
- (To do) Provide an easy way to install common tools

## How to build and push to artifactory
Before starting, ensure netrc is set up properly. Follow the steps [here](https://docs.woven-planet.tech/engineering_software/artifactory/support/Docker-Registry/) to get the credentials.

```netrc
# ~/.netrc
machine artifactory-ha.tri-ad.tech
login {your WP email}
password {your Artifactiry API token}
```

```bash
# format
bazel run //tools/k8s-tools:push_cityos_infra_tools_{platform}_{arch}

# available platforms and architectures
# linux amd64
# darwin arm64
# darwin amd64

# example
bazel run //tools/k8s-tools:push_cityos_infra_tools_linux_amd64 # to build & push linux_amd64
# tools is going to be uploaded in https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/wcm-cityos/k8s-tools
```

## How to install on dev machine

### Installation

Execute script supported for your system. 
```bash
# format
bazel run //tools/k8s-tools:install_cityos_infra_tools
```

It will take some time to complete the installation.
As a result you will see the logs as below:

```
INFO: Build completed successfully, N total actions
Are you sure you want them to be installed? Type Y or y to confirm.
```

Check the packages in the list and approve the upload to your /usr/local/bin directory.


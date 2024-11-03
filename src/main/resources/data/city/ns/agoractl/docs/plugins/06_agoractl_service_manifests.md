# Agoractl Service Manifests Plugin

Plugin to generate service manifests required to deploy a service contained in a preexisting Docker image.

## Introduction

Once a developer has developed and locally tested a service, and encapsulated it in a Docker image, the next step is to
deploy that image in a Agora cluster.  The Kubernetes manifests required for this task are many and their structure
can vary between different clusters.  At the time of writing, there are "old style" and "new style" clusters, with
very differnt structures.  This plugin supports both types of clusters.

When executed, it will generate a set of reusable manifests in the _infrastructure/k8s/common_ directory, that can be
used with all cluster types.

It will also generate cluster-specific manifests into the appropriate cluster's directory.  For instance, for the local
cluster, the files are generated under the _infrastructure/k8s/local_ directory, and for the lab2 cluster, they
are generated under the _infrastructure/k8s/environments/lab2/clusters_ directory.

## Usage

Here is an example of using the plugin to generate a set of manifests for the default _local_ cluster:

```shell
bazel run //ns/agoractl -- service_manifests <workspace_name> <namespace_name> <name> <image_path> <port> <stage>
```

The command above will potentially generate a lot of files, so should be executed in a clean repo (one that contains no
changes).  Then, a simple "git status" command will show which files have been changed or created, and you can add these
to a git commit and push them to GitHub.  For simple cases, the generated manifests will work without manual changes being
required.  If this is not the case, or if you have special requirements, please contact the Developer Relations team for
help!

### Deploying to Multiple Clusters

It is also possible to invoke this command multiple times, to generate manifests for different clusters.  For instance,
the following invocation will create a set of files in the _common_ and _local_ directories:

```shell
bazel run //ns/agoractl -- service_manifests my-app my-namespace my-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/my-namespace/my-app:my-image-3d3d7d4974-1699427910
```

If you add the generated manifests to the monorepo, it will result in the image _my-image-3d3d7d4974-1699427910_ being
deployed to the _local_ cluster.  If you later decide that you wish to also deploy to the _lab2_ cluster, simply run
the command again, specifying "--cluster lab2" as a parameter:

```shell
bazel run //ns/agoractl -- service_manifests my-app my-namespace my-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/my-namespace/my-app:my-image-3d3d7d4974-1699427910 --cluster lab2
```

This will generate only the _lab2_ specific manifests and will reuse the common manifests in the _common_ directory.

### Using different Images on different Clusters

The image used can be specified individually for different clusters, so you can deploy one version of your image to the
_local_ cluster and a different one to the _lab2_ cluster, simply by running the above commands twice with different
images.

## Arguments

The arguments are explained in detail in the plugin itself, so will not be repeated here.  To see this documentation,
use the following command:

```shell
bazel run //ns/agoractl -- service_manifests --help
```

You can find an example for generating manifests files in this [example.md](https://github.com/wp-wcm/city/blob/main/ns/agoractl/agoractl_service_manifests/example.md) file

# Generating manifests files using Agoractl plugin

In this section, we will learn how we can create the manifests files automatically
using Agoractl plugin.


## How to run Agoractl


### Invocation via Bazel

We can generate service manifests file by using service_manifests postional argument as shown below

```shell
$ bazel run //ns/agoractl service_manifests -- --help

usage: agoractl service_manifests [-h] [-l CLUSTER] name deployment_name image_name namespace_name

Plugin to enable the creation of k8s manifests for deploying a service onto an Agora cluster. Executing this plugin will cause both common and cluster-specific manifests to be created and placed into the appropriate directory. It supports old-style (local,
lab, dev) and new-style (lab2, dev2) clusters and will adapt to those clusters as required. It can be executed multiple times in order to add manifests to multiple clusters, so you could, for example, firstly deploy your service to your own local cluster
using '--cluster local', then deploy it to lab with '--cluster lab', and then deploy it to dev with '--cluster dev'. The manifests generated by this plugin can deploy different image versions to different clusters, and are considered 'best practice' by the
Developer Relations team. It is highly recommended that this plugin is used for the creation of deployment manifests, rather than creating those manifests yourself by hand.

positional arguments:
  name                  the name of the service to generate
  deployment_name       the name of the k8s deployment to generate
  image_name            the name of the Docker image to deploy
  namespace_name        the namespace in which to generate the service

options:
  -h, --help            show this help message and exit
  -l CLUSTER, --cluster CLUSTER
                        Name of Agora cluster to use. Default is 'local'
```

An example showcasing the generation of manifests files for a service named **clock** in **lab2 cluster** is shown below:

```shell
$ bazel run //ns/agoractl service_manifests clock clock-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/clock-namespace/clock:colinward-clock-image-e11d5a62b9-1690269944 clock-namespace -- --cluster lab2 
INFO: Analyzed target //ns/agoractl/commander:commander_bin (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //ns/agoractl/commander:commander_bin up-to-date:
  bazel-bin/ns/agoractl/commander/commander_bin
INFO: Elapsed time: 0.217s, Critical Path: 0.01s
INFO: 1 process: 1 internal.
INFO: Build completed successfully, 1 total action
INFO: Running command line: bazel-bin/ns/agoractl/commander/commander_bin service_manifests clock clock-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/clock-namespace/clock:colinward-clock-image-e11d5a62b9-1690269944 clock-namespace --cluster lab2

Generating code...

Generating common service files to <repo>/infrastructure/k8s/common/clock
Generating cluster specific service files to <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/clock
Generating flux-system files to <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml
Generating gloomesh files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/clock
Generating gloomesh workspace files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/kustomization.yaml
Generating flux-system files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services/kustomization.yaml
Updating virtual gateway file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml
Writing /Users/zakiahmed.qureshi/Desktop/work/git-repos/city/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml

Done!

Process completed. From here, you can create a PR on this repository to save the service

        * Please make sure that you are on a branch that is not 'main' when you make the commit

        * Please make sure to follow your progress at https://github.com/wp-wcm/city
```

Let's look at the arguments required to generate the manifests files. You can change these arguments according to your needs.

* name - **clock**
* deployment_name - **clock-deployment**
* image_name - **docker.artifactory-ha.tri-ad.tech/wcm-cityos/clock-namespace/clock:colinward-clock-image-e11d5a62b9-1690269944**
* namespace_name - **clock_namespace**
* --cluster - **lab2** (you can change the cluster name to your desired cluster dev, lab or lab2. The default value is local)

Confirm the generated manifests files and commit in a branch other than 'main'. After that you can create a PR to deploy these changes to your desired cluster.

```shell
Generating common service files to <repo>/infrastructure/k8s/common/clock
Generating cluster specific service files to <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/clock
Generating flux-system files to <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml
Generating gloomesh files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/clock
Generating gloomesh workspace files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/kustomization.yaml
Generating flux-system files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services/kustomization.yaml
Updating virtual gateway file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml
Writing /Users/zakiahmed.qureshi/Desktop/work/git-repos/city/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml
```

The files generated for lab2 is different than lab and dev since we are using gloo-mesh for lab2. you can learn more about the file structure and resources for lab2 onboarding [here](https://github.com/wp-wcm/city/blob/main/infrastructure/docs/runbooks/Lab2/onboard.md).

Here is an example for generated files in dev cluster using bazel.

```shell
$ bazel run //ns/agoractl service_manifests clock clock-city-service clock-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/clock-namespace/clock:colinward-clock-image-e11d5a62b9-1690269944 clock-namespace -- --cluster dev
INFO: Analyzed target //ns/agoractl/commander:commander_bin (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //ns/agoractl/commander:commander_bin up-to-date:
  bazel-bin/ns/agoractl/commander/commander_bin
INFO: Elapsed time: 0.176s, Critical Path: 0.01s
INFO: 1 process: 1 internal.
INFO: Build completed successfully, 1 total action
INFO: Running command line: bazel-bin/ns/agoractl/commander/commander_bin service_manifests clock clock-city-service clock-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/clock-namespace/clock:colinward-clock-image-e11d5a62b9-1690269944 clock-namespace --cluster dev

Generating code...

Generating common service files to <repo>/infrastructure/k8s/common/clock
Generating cluster specific service files to <repo>/infrastructure/k8s/dev/clock
Generating flux-system files to <repo>/infrastructure/k8s/dev/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/dev/flux-system/kustomizations/services/kustomization.yaml
Appending clock.yaml to resources

Done!

Process completed. From here, you can create a PR on this repository to save the service

        * Please make sure that you are on a branch that is not 'main' when you make the commit

        * Please make sure to follow your progress at https://github.com/wp-wcm/city
```



### Invocation via Python

Generating manifests files using Python can be achieved by following command.
(don't forget to change the directory to /ns/agoractl)

```shell
$ cd ns/agoractl 
$ ./agoractl.py service_manifests clock clock-deployment docker.artifactory-ha.tri-ad.tech/wcm-cityos/clock-namespace/clock:colinward-clock-image-e11d5a62b9-1690269944 clock-namespace --cluster lab2  
/Users/zakiahmed.qureshi/Library/Python/3.9/lib/python/site-packages/urllib3/__init__.py:34: NotOpenSSLWarning: urllib3 v2.0 only supports OpenSSL 1.1.1+, currently the 'ssl' module is compiled with 'LibreSSL 2.8.3'. See: https://github.com/urllib3/urllib3/issues/3020
  warnings.warn(
Generating code...

Generating common service files to <repo>/infrastructure/k8s/common/clock
Generating cluster specific service files to <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/clock
Generating flux-system files to <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml
Generating gloomesh files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/clock
Generating gloomesh workspace files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/kustomization.yaml
Generating flux-system files to <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services
Updating kustomisation file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services/kustomization.yaml
Updating virtual gateway file <repo>/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml
Writing /Users/zakiahmed.qureshi/Desktop/work/git-repos/city/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml

Done!

Process completed. From here, you can create a PR on this repository to save the service

        * Please make sure that you are on a branch that is not 'main' when you make the commit

        * Please make sure to follow your progress at https://github.com/wp-wcm/city
```

confirm the generated files and raise a PR to deploy.
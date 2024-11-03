# Using the Deployment Workflow

## Requirements

Please make sure you have the following:

- Access to Agora's monorepo. Instructions to request access [here](https://docs.google.com/document/d/12S82-UYnmfu6OLuHBug4vLgJ007jl-mTcI8zsZvEha0/edit?usp=sharing)
- A namespace in the cluster already provisioned
  - To find instructions for it, please check the [Tenant and Namespace creation guide](../agora_developers/02-tenant-ns-quickstart.md)
- A way to build images to the company's Artifactory HA
  - Access to the artifactory can be requested from IT team if you do not have it

It is also recommended that you already have continuous delivery set up through Bazel. If you have not done that yet,
please read how to do that here: [Developing With Bazel](./../development/bazel/README.md). If you are
a power user and are only using this repository for continuous deployments with kubernetes onto the CityOS cluster,
then you must have already set up continuous delivery and have your images in an accessible location for the deployment
system.

## Setting Up Automatic Deployments

The following two steps of setting up the deployments and making the deployment automatic should be done in the same PR.
When making the PR, make sure to request reviews from one of your teammates, and at least one member of the CI/CD team.

### Setting Up Deployments

If it does not exist already, create a new directory for your namespace in `/infra/k8s/{cluster}/{namespace}`
where `{cluster}` is the cluster you wish to deploy to, and `{namespace}` is your namespace.

The current available clusters are:

- dev

Add the following lines to the `kustomization.yaml` file in the namespace that you are deploying to under the `images` field.:

```yaml
# city/infra/k8s/{cluster}/{namespace}/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: {namespace}
resources:
  - ... # configurations
images:
  - ... # other images
  - name: <your-service>-placeholder
    newName: docker.artifactory-ha.tri-ad.tech/wcm-backend/<your-service> # {"$imagepolicy": "tenant-lps-backend:<your-service>-image-policy:name" }
    newTag: main-aaaaaaaa-0000000000 # {"$imagepolicy": "tenant-lps-backend:<your-service>-image-policy:tag" }
```

Please note that the `newTag` value of `main-aaaaaaaa-0000000000` is temporary and will be replaced when the image update automation overwrites it.
If you already have an existing image, please use the tag for the existing image.

### Making Deployments Automatic

To make your deployments automatic, add the following configuration to `/infra/k8s/{cluster}/flux-tenant-lps/flux/projects/<your-service>.yaml`:

```yaml
---
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImageRepository
metadata:
  name: <your-service>-image-repository
spec:
  interval: 1m
  image: docker.artifactory-ha.tri-ad.tech/wcm-backend/<your-service>
---
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImagePolicy
metadata:
  name: <your-service>-image-policy
spec:
  imageRepositoryRef:
    name: <your-service>-image-repository
  filterTags:
    pattern: '^main-[a-f0-9]+-(?P<ts>[0-9]+)$'
    extract: '$ts'
  policy:
    numerical:
      order: asc
```

In addition, add it into the kustomization file in the same directory:

```yaml

apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ... # other files
  - <your-service>.yaml
```

After that, add the following configuration to `/infra/k8s/{cluster}/flux-tenant-lps/flux/automations/<your-service>.yaml`
(please copy from existing file and adjust the following attribute):

```yaml
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImageUpdateAutomation
metadata:
  name: projects-image-update-automation-<your-service>
spec:
  git:
    commit:
      messageTemplate: |
        k8s(dev/<your-service>): ...
    push:
      branch: __image/dev/<your-service>/image-updates
  update:
    path: infra/k8s/dev/<your-service>
```

In addition, add it into the kustomization file in the same directory:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ... # other files
  - <your-service>.yaml
```

## Deployment Files

To configure your deployments, you can create your own directory in your project's directory `city/projects/<your-service>/k8s/{environment}` and place your
kubernetes files in there. In addition, you must add it to the `kustomization.yaml` file in the cluster and namespace you want to deploy to:

```yaml
# city/infra/k8s/{cluster}/{namespace}/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: {namespace}
resources:
  - ... # other configurations
  - ../../../../projects/<your-service>/k8s/{environment}
images:
  - ... # images
```

**Note: When specifying the image to deploy in your deployment configurations, use the image name you have put in the
`images` field. If you are following this guide, then the value should be `<your-service>-placeholder`**

## Push to Repository

If everything is connected properly, the system should be able to detect and deploy your service when a new image is pushed
to the repository. Make sure you push to the `docker.artifactory-ha.tri-ad.tech/wcm-backend/<your-service>` image repository.
After pushing, the deployment should trigger and the image will be deployed to the cluster.

You can check the status in the slack channel `#wcm-city-os-bots`. Please monitor your deployment to see if it caused any errors.

# Deployments

## FluxCD

The monorepo uses a GitOps system called FluxCD. This system is connected to the CityOS cluster and will automatically
deploy images to the cluster when it detects a new image.

You can read more about Flux [here](https://fluxcd.io/).

## Hybrid Deployment System

The Flux deployment system is actually independent of the build system. If you wish to set up the deployment system
to deploy external images that are not built within the monorepo, you can do so. Please see the [Usage Guide](/docs/monorepo/using-the-deployment-workflow.md.md).

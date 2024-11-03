# Creating a New Service

This guide will help you set up a new service in the monorepo and hook up everything
so that you can develop smoothly.

## Requirements

* A Github Team
  * If you do not have this, please see [Setting Up Github Teams](./setting-up-github-teams.md)
* Access to Agora's monorepo. Instructions to request access on [How to Get Access to Github EMU](https://docs.google.com/document/d/12S82-UYnmfu6OLuHBug4vLgJ007jl-mTcI8zsZvEha0)
* A namespace in the cluster already provisioned
  * To find instructions for it, please check [the Tenant and Namespace creation guide](../agora_developers//02-tenant-ns-quickstart.md)

## Creating a New Directory in the Monorepo

First, you should create a new directory under the `projects` directory with the name of your project. This directory
name should be a clear name of your project. This would likely be your existing repositories name, but the name is up to
your team to decide. Inside the new directory add an empty file called `.gitkeep`.

To be able to safely work alongside multiple teams, the monorepo will be managed by
a [CODEOWNERS](../../.github/CODEOWNERS) file. This file will allow putting clear ownership of who has access to which
parts of the monorepo. When moving your project to the monorepo, we will request that you add a new line for your
project's directory under a section for your team.

With these two changes complete, please create a pull request to add your project with the proper codeowner rule! After
the PR is created, the monorepo maintainers will review and, after approval, merge your PR.

If you are unsure of how to set up the codeowners, please see [here](./setup-teams.md#establishing-code-ownership).

## Setting Up Automated Builds and CI Integration

See [Setting Up Your Project With Bazel](../development/bazel/README.md#setting-up-your-project-with-bazel).

## Setting Up Automatic Deployments

See [Setting Up Automatic Deploys](./using-the-deployment-workflow.md#setting-up-automatic-deploys)

Note that if you finished the previous step, then the automated builds will already be set up, and so the pipeline will
automatically build and deploy your code.

## Optional

### Setting up `direnv`

See [Setting Up `direnv`](./tools/direnv.md).

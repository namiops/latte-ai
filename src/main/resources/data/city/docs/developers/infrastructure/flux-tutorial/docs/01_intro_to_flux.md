# Introduction to flux

> Flux is a tool for keeping Kubernetes clusters in sync with sources of
configuration (like Git repositories), and automating updates to configuration
when there is new code to deploy.

Flux is a Cloud Native Computing Foundation incubating project. It is a set of
tooling that interacts with kubernetes on your behalf. This tooling is used by
organizations to practice **gitops**.

Each cluster in Agora has flux deployed on it, which deploys several
controllers into the kubernetes cluster. These controllers will inspect a
source of configuration, find differences between the sources and the current
state of the cluster and update the cluster with the difference. Beyond these
core responsibilities there are additional controllers that notify external
systems, and monitor image repositories to allow for image automation.

In Agora we use flux2, documentation and strucutre is significantly different
between the two versions so be sure to check the correct version.

## Historical notes and motivation

Historically Agora was updating cluster state through `kubectl apply` actions
which are neither repeatable, nor automated. To improve on this the
infrastructure team looked for tooling to automate and standardize the CI/CD
flow and the decision was made to decouple CI from CD. CI is now handled by a
combination of github actions and Bazel.

For CD the team was considering solutions that were cloud native, open source,
and available at the time the decision was being made and at that time only
Flux and Argo were potential candidates. Between the two Flux was
significantly more lightweight, easier to run and get started, and ultimately
ended up being the solution chosen.

## Gitops

> GitOps is a way of managing your infrastructure and applications so that
whole system is described declaratively and version controlled (most likely
in a Git repository), and having an automated process that ensures that the
deployed environment matches the state specified in a repository.

## Controllers

In the following sections we will go through each controller individually to
elaborate on their roles in the cluster.

* [Source](02_source_controller.md)
* [Kustomize](03_kustomize_controller.md)
* [Helm](04_helm_controller.md)
* [Notification](05_notification_controller.md)
* [Image Automation](06_image_automation_controller.md)

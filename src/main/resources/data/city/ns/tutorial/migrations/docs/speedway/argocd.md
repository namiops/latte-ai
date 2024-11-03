# ArgoCD

## Current GitOps explained

As we are moving to Speedway (our 3rd gen k8s infrastructure),  we use ArgoCD as our CD tool and we followed GitOps to make the continuous deployment and ArgoCD to visualize deployment status.

## Terminologies explained

[CityCD](https://docs.google.com/document/d/1WGbYMc5XMKCqXkXiel_5d-vskgdxqNdsXEvd4l1I28U/edit): Our flavour of ArgoCD and GitOps.

[Speedway](https://docs.google.com/document/d/16r6cL_ylrz08DNQDsJeH777s2WvsuQVOWqpiBDQAMZk/edit#heading=h.5qm13wuvtiz9): Agora 3rd gen Infrastructure that utilizes vCluster to operate on SMC.

[vCluster](https://www.vcluster.com/docs/): A virtualized Kubernetes cluster that runs on top of another Kubernetes cluster.

[SMC](https://docs.google.com/document/d/1c-qoi8BhnkwFU3X0BPHELIHkKQFr6-K1X7GkXJ7Vof0/edit#heading=h.g7wkmkc447px): Actual infrastructure.

**GitOps**: Git repository is the source of truth for the latest code and the latest config.

**Kustomize**: Our ways to template application with k8s manifests.

![argocd gitops](../assets/argocd.png)

When the new source code is pushed to GitHub, the code will be

- Tested, built and then pushed to a container registry -> Done by GitHub Action
- Change on Config Repository (if there is config change) -> Done by AgoCD
- Sync that Config change into Speedway Cluster -> Done by Argo CD

![argocd speedway](../assets/argo2.png)

## ArgoCD UI explained

#### Tiles/Apps

Current ArgoCD sits in [dev environment](https://argocd.agora-dev.w3n.io/) and it can read both Speedway prod and dev environment.

The red bracket ( APP OF APPS ) is the admin setup to make the system work, and it doesn't contain useful information for developers.

As a developer we shouldn't worry about the Red Bracket, but we should only look at it's subset ( the Blue bracket ).  Which contains the status/liveness of each k8s resources of your services.

![argo4](../assets/argo4.png)

#### Out of sync

If there is merging to main branch of "City-monorepo", ArgoCD would know and show you "out of sync".

"out of sync" means that the application diverged from the desired state (defined in GitHub main branch), and [it would automatically try to sync the application](https://github.com/wp-wcm/city/blob/e4bf83ef06c7596fbf5dece873684017b54a38c0/infra/k8s/argocd/nextgen/ci/core.yaml#L185).

However, since ArgoCD is reading the vCluster, in case it is not in sync with the actual cluster, we may see some stale states on ArgoCD.

![argocd diff](../assets/argo3.png)

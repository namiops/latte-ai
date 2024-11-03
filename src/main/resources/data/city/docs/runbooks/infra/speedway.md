# Speedway Clusters

| Last Update | 2024-07-19               |
|-------------|--------------------------|
| Tags        | Infrastructure, Speedway |

## Content

* [Things to Check for New Deployment](#things-to-check-for-new-deployment)

## Things to Check for New Deployment

1) Check your deployment environments configuration in
   the [projects Bazel file](../../../infrastructure/k8s/environments/dev2/clusters/ci-east/argocd-apps/projects/_projects.bzl)
2) Check that you have the following in
   the [Infra Configuration Base](../../../infra/configs/base)
    1) A directory folder which contains...
    2) A RBAC YAML manifest
    3) A `kustomization` File
3) For your environment you're deploying to, make sure to check:
    1) You have an entry under `namespaces` in the
       respective [Control Plane File GC](../../../infra/configs/prod/agora-control-plane/gc/configmap-proxy-config.yaml)
       or  [Control Plane File ML](../../../infra/configs/prod/agora-control-plane/ml/configmap-proxy-config.yaml)
    2) Your directory (`/infra/my-env/my-dir`) has at least a `kustomization`
       file that points to your `base` configuration (aka. `../../base/my-dir`)
4) If you are deploying to Production you need to have additional context on your Deployment:
   1) `securityContext.seccompProfile.type:` RuntimeDefault
   2) `runAsUser:` 1000
   3) `runAsGroup:` 1000
   4) `runAsNonRoot:` true
   5) `containers.securityContext.allowPrivilegeEscalation:` false
   6) `containers.securityContext.readOnlyRootFilesystem:` true
   7) `containers.securityContext.capabilities.drop:` -ALL

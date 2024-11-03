# ID Namespace
Our namespace contain several components
## Active service
* keycloak
* drako-v1
* drako-polis-v1
* sts-v2
* postgress db
* secure-kvs

## Deprecated service
* sts-v1
* ums
* external-authorizer

# Deployment environment
However, the DB configuration is quite particular to each environment. Because
of this, when including this in your cluster configuration make sure to:

1. declare your DB locally (not from common).
2. patch the keycloak resources so it uses the correct DB.



# Test
## Smoke Test
Running smoke test requires
- `id-test-drako-v1` echo service
- `id-test-sts-v2` httpbin service
- Testkube executor and Testkube test config in `id/testkube-x.x.x` (this `testkube` resources are part of `testkube/kustomization` as it requires testkube to operate)

## Generating Manifests

> [!WARNING]
> Please note the `--` in the `bazel run` commands below. This is necessary, in
> order to separate the arguments to `bazel run` from the arguments to the
> `drako-manifests:cmd` > script.

### Drako

You can use the following script to automatically create new manifests
for Drako, replacing `<app version>` (e.g. `0.6.1`), `<manifests version>` (e.g.
`0.1.0`), and `<image tag>` (e.g. `main-98d414cb8f-1701396742`):

```console
$ bazel run //infrastructure/k8s/common/id/bazel/gen-drako-manifests:cmd -- \
    --app-version=<app version> \
    --manifests-version=<manifests version> \
    --image-tag=<image tag>
```

### DrakoPolis

You can use the following script to automatically create new manifests
for DrakoPolis, replacing `<app version>` (e.g. `0.2.0`), `<manifests version>` (e.g.
`0.1.0`), and `<image tag>` (e.g. `main-2a38e9d06e-1701909000`):

```console
$ bazel run //infrastructure/k8s/common/id/bazel/gen-drako-polis-manifests:cmd -- \
    --app-version=<app version> \
    --manifests-version=<manifests version> \
    --image-tag=<image tag>
```

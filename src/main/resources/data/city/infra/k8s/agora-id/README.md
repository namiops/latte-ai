# Agora ID

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
$ bazel run //infra/k8s/agora-id/common/bazel/gen-drako-manifests:cmd -- \
    --app-version=<app version> \
    --manifests-version=<manifests version> \
    --image-tag=<image tag>
```

### DrakoPolis

You can use the following script to automatically create new manifests
for DrakoPolis, replacing `<app version>` (e.g. `0.2.0`), `<manifests version>` (e.g.
`0.1.0`), and `<image tag>` (e.g. `main-2a38e9d06e-1701909000`):

```console
$ bazel run //infra/k8s/agora-id/common/bazel/gen-drako-polis-manifests:cmd -- \
    --app-version=<app version> \
    --manifests-version=<manifests version> \
    --image-tag=<image tag>
```

### DrakoBuddy

You can use the following script to automatically create new manifests for
DrakoBuddy, replacing `<app version>` (e.g. `0.1.0`), `<manifests version>`
(e.g.  `0.1.0`), and `<image tag>` (e.g. `main-2a38e9d06e-1701909000`):

```console
$ bazel run //infra/k8s/agora-id/common/bazel/gen-drako-buddy-manifests:cmd -- \
    --app-version=<app version> \
    --manifests-version=<manifests version> \
    --image-tag=<image tag>
```

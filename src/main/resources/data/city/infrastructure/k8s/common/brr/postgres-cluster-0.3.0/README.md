This is a manifest for postgres cluster on lab2.

## How to update the manifest

Edit `postgres-values.yaml` then run the command below:

```
$ bazel run //infrastructure/k8s/common/brr/postgres-cluster-0.3.0:burr-postgres.copy
```

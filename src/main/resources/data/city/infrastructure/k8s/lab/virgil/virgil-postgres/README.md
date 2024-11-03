This is a manifest for postgres cluster on agora lab.

## How to update the manifest

Edit `postgres-values.yaml` then run the command below:

```
$ bazel run //infrastructure/k8s/lab/virgil-postgres:virgil-wcps-postgres.copy
```
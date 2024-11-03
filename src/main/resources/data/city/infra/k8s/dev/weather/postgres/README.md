# BUILD

## BAZEL

```text
# Delete comment out of BUILD.bazel before run the following commands. There are separated DBs, but zebra doesn't support it. So need to create manually.
bazel run //infra/k8s/dev/weather/postgres:weather-db.copy
bazel run //infra/k8s/dev/weather/postgres:poteka-db.copy
```

## Values

- [Reference](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/agora-postgres-cluster)

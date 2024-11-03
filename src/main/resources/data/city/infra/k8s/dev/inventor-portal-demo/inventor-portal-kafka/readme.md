# How to generate files using zebra

The GitHub actions will run these commands instead of us, so you don't have to worry about this.

If you want to run the command manually, you can run the following commands:

## FYI: Commands

- Check the supported commands:

```shell
bazel query //infra/k8s/dev/inventor-portal-demo/inventor-portal-demo-kafka/...
```

- Generate/Update `z-kafka-config.yaml`

```shell
bazel run //infra/k8s/dev/inventor-portal-demo/inventor-portal-demo-kafka:kafka_config.copy  
```

- Generate/Update `z-apicurio-schema.yaml`

```shell
bazel run //infra/k8s/dev/inventor-portal-demo/inventor-portal-demo-kafka:apicurio_schema.copy
```


- Generate/Update `ns/kafka-docs/api/z-kafka-async-api.yaml`

```shell
bazel run //ns/kafka-docs/api:asyncapi.copy
```

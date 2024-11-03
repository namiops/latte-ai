# ac-user-registration-resident-registration-api-server

## Set environment variables for local development

Create `local.env` by copying a template file `local.env.template`.

```bash
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/resident
cp local.env.template local.env
```

Ask backend members how to set each secret variables.

## Build and Run

```bash
# get dependencies if you need
bazel run @go_sdk//:bin/go -- get
# update BUILD files if you need
bazel run //:gazelle 
bazel run //:buildifier 

# build image
# Run the build command whenever you update the source code
bazel run //projects/ac-user-registration/backend/resident/api-server/internal/cmd:image.load
# run image
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/resident
docker compose -f compose_resident_api.yaml up
```

## serverless applications

### resident-bulk-registration

```bash
bazel run //projects/ac-user-registration/backend/resident/api-server/serverless/resident-bulk-registration:image.load
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/resident/serverless/resident-bulk-registration
docker compose up

```

- [Kafka topic dashboard](https://kafka-monitor.cityos-dev.woven-planet.tech/ui/kafka-cluster/topic/ac-user-registration.resident-bulk-registration/data?sort=Oldest&partition=All)
- [Logs for the serverless application](https://observability.cityos-dev.woven-planet.tech/grafana/explore?orgId=1&left=%7B%22datasource%22:%22P8E80F9AEF21F6940%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22datasource%22:%7B%22type%22:%22loki%22,%22uid%22:%22P8E80F9AEF21F6940%22%7D,%22editorMode%22:%22code%22,%22expr%22:%22%7Bnamespace%3D%5C%22ac-user-registration%5C%22,%20pod%3D~%5C%22resident-bulk-registration.%2A%5C%22,%20container%3D%5C%22user-container%5C%22%7D%20%7C%3D%20%60%60%22,%22queryType%22:%22range%22%7D%5D,%22range%22:%7B%22from%22:%22now-24h%22,%22to%22:%22now%22%7D%7D)

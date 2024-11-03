# GPS Producer-Consumer
This is a POC application testing producing and consuming Kafka messages serialized with GPS pipeline Protobuf schema.

## Configuration

## Build the project with Bazel

Execute the following command from your shell (from the monorepo's root folder):

1. Run BUILD file generation tools and execute BUILD file linter

```sh
bazel run //:gazelle
bazel run //:buildifier
bazel run //:buildifier_check
```

2. Sync Golang dependencies by updating the go.mod file

```
go mod tidy
```

3. Run Gazelle rule to sync Golang repositories' dependencies with the go.mod file in the root of the monorepo

```
bazel run //:gazelle_update_repos_go
```

4. Build the docker image target.

```
bazel build //projects/location-service/test-tools/gps-producer-consumer/cmd/gps-producer-consumer
```

In case of building on a Mac or Windows locally with intention to use the image to deploy on Linux machine (eg. AWS EC2
instance), use this command to cross-compile instead

```
bazel build //projects/location-service/test-tools/gps-producer-consumer/cmd/gps-producer-consumer --platforms=@io_bazel_rules_go//go/toolchain:linux_amd64 
```

## Run the project with Bazel

* Run as binary

```
bazel run //projects/location-service/test-tools/gps-producer-consumer/cmd/gps-producer-consumer:gps-producer-consumer.binary
```

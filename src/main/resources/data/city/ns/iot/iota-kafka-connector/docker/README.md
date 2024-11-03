# creating iota-kafka-connector docker image

## Usage
Please run this script on the same kind of machine as EKS (linux/amd64)

```shell
./build_and_push.sh
```

## Debezium JDBC connector
The debezium jdbc connector is added as part of wcl poc. The connector plugin is downloaded as per [these](https://debezium.io/documentation/reference/stable/install.html#_installing_a_debezium_connector) instructions in the debezium documentation.

TODO: use bazel to download and package the jar.

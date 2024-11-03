# OTEL generator

This is a simple OTEL objects generator.

It generates logs, traces or metrics and sends them to log-collector.

## Usage:

### Sending all signal kinds (supported by Agora, json format):

```bash
bazel run otel-generator
```

### Sending traces (not supported by Agora yet, protobuf format):

```bash
bazel run otel-generator -- --kind=traces --path=/YOUR/PATH --endpoint=YOUR.ENDPOINT
```

Example: Sending traces to dev2 log collector
```
bazel run otel-generator -- --kind=traces --path=/otel/v1/traces --endpoint=log-collector-serverless-lambda.agora-dev.w3n.io
```

Example: Running trace in Prod
```
bazel run otel-generator -- --kind=traces --path=/telemetry/v1/traces --endpoint=iot.woven-city-api.toyota
```

Example: Running logs in Prod with mTLS from device provisioning for tenant:test group:nt-test-group with deviceA (provisioned with iotactl)
```
bazel run otel-generator -- --kind=logs --path=/telemetry/v1/logs --endpoint=iot.woven-city-api.toyota:4318 --tls=true --cert=$HOME/.iota/prod/test/nt-test-group/deviceA_crt.pem --key=$HOME/.iota/prod/test/nt-test-group/deviceA_key.pem
```

Example: Running combined in Prod with mTLS from device provisioning
```
bazel run otel-generator -- --path=/telemetry --endpoint=iot.woven-city-api.toyota:4318 --tls=true --cert=$HOME/.iota/prod/test/nt-test-group/deviceA_crt.pem --key=$HOME/.iota/prod/test/nt-test-group/deviceA_key.pem
```

### To see all available arguments run:
```bash
bazel run otel-generator -- --help
```

### Confirm the result
#### Prod
* logs : [link](https://wcmagoraprod.grafana.net/explore?schemaVersion=1&panes=%7B%22bi3%22:%7B%22datasource%22:%22grafanacloud-logs%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22expr%22:%22%7Bnamespace%3D%5C%22agora-iot-prod%5C%22,%20agora_source%3D%5C%22test%5C%22%7D%20%7C%3D%20%60otel-log-generator%20test%20message%60%22,%22queryType%22:%22range%22,%22datasource%22:%7B%22type%22:%22loki%22,%22uid%22:%22grafanacloud-logs%22%7D,%22editorMode%22:%22builder%22%7D%5D,%22range%22:%7B%22from%22:%22now-30m%22,%22to%22:%22now%22%7D%7D%7D&orgId=1)
* traces: copy the trace ID and search in query box [here](https://wcmagoraprod.grafana.net/explore?schemaVersion=1&panes=%7B%228bx%22:%7B%22datasource%22:%22grafanacloud-traces%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22datasource%22:%7B%22type%22:%22tempo%22,%22uid%22:%22grafanacloud-traces%22%7D,%22queryType%22:%22traceql%22,%22limit%22:20,%22tableType%22:%22traces%22,%22query%22:%220fa9eaa607a91823de4a3a813b634b48%22%7D%5D,%22range%22:%7B%22from%22:%22now-1h%22,%22to%22:%22now%22%7D%7D%7D&orgId=1)
* metrics: check the [otel_generator_counter_total metrics](https://wcmagoraprod.grafana.net/explore?schemaVersion=1&panes=%7B%22med%22:%7B%22datasource%22:%22grafanacloud-prom%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22expr%22:%22otel_generator_counter_total%22,%22range%22:true,%22instant%22:true,%22datasource%22:%7B%22type%22:%22prometheus%22,%22uid%22:%22grafanacloud-prom%22%7D,%22editorMode%22:%22builder%22,%22legendFormat%22:%22__auto%22,%22useBackend%22:false,%22disableTextWrap%22:false,%22fullMetaSearch%22:false,%22includeNullMetadata%22:true%7D%5D,%22range%22:%7B%22from%22:%22now-15m%22,%22to%22:%22now%22%7D%7D%7D&orgId=1)

# Telemetry Collector

Telemetry collector's purpose is to propagate user [OTLP-formatted telemetry](https://opentelemetry.io/docs/specs/otel/) to Agora.
This is useful for services/devices being hosted outside Agora that want to have their telemetry signals stored in Agora for centralized observability.

Please note that production endpoints exposed to the internet are secured with mTLS which will require your external device/service to have a valid certificate vended by [CVM](https://developer-portal.agora-dev.w3n.io/docs/default/Component/cvm-service) or [IoTA](https://developer-portal.agora-dev.w3n.io/docs/default/Component/iota-service).

The workflow is as follows:

1. You export your telemetry via [OTEL SDKs/instrumentations](https://opentelemetry.io/ecosystem/registry/) to the telemetry collector endpoint
   - The payload must be no bigger than <b>10Mb</b>.
   - For consistency please use [http/protobuf](https://opentelemetry.io/docs/zero-code/net/configuration/#otlp) encoding.
2. Telemetry collector forwards the logs to Agora Observability Stack by default.
   - Alternative destinations are possible, please contact `#wcm-agora-services` for the details.
3. In Agora Observability Stack case, you can query your telemetry in [Grafana](#grafana-dashboards).

Telemetry collector was formerly known as _Log Collector_, and some parts of
the documentation may still refer to is as such for historical reasons. In the
context of Agora, these two names should be considered synonymous.

Demos:

1. [Sending OTEL signals](https://drive.google.com/file/d/1uT5rewJpegBNrUedA9PMpQF9JMai5flS/view?usp=drive_link) via HTTP.
2. [Sending OTEL signals](https://drive.google.com/file/d/15e6azQWW6k5BOSXs7VKT6e-bL-KCVRfW/view?usp=drive_link) via [IoTAD](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/iotad/).
3. [SDK usage sample](https://github.com/wp-wcm/city/tree/main/ns/demo/otel-python).
4. [Distributed tracing sample](https://github.com/wp-wcm/city/tree/main/ns/demo/otel-propagation).

## Getting started

You may send your telemetry to the telemetry collector via the following URLs/ports:

| Cluster               | Handler                    | URL                                                                      | PORT |
|-----------------------|----------------------------|--------------------------------------------------------------------------|------|
| Speedway prod         | OTEL logs handler          | https://iot.woven-city-api.toyota/telemetry/v1/logs                      | 4318 |
|                       | OTEL traces handler        | https://iot.woven-city-api.toyota/telemetry/v1/traces                    | 4318 |
|                       | OTEL metrics handler       | https://iot.woven-city-api.toyota/telemetry/v1/metrics                   | 4318 |
| Speedway dev          | OTEL logs handler          | https://dev-iot.woven-city-api.toyota/telemetry/v1/logs                  | 4318 |
|                       | OTEL traces handler        | https://dev-iot.woven-city-api.toyota/telemetry/v1/traces                | 4318 |
|                       | OTEL metrics handler       | https://dev-iot.woven-city-api.toyota/telemetry/v1/metrics               | 4318 |
| Pre-prod              | ~~Default format handler~~ | https://log-collector-serverless-lambda.agora-dev.w3n.io                 | 443  |
|                       | OTEL logs handler          | https://log-collector-serverless-lambda.agora-dev.w3n.io/otel/v1/logs    | 443  |
|                       | OTEL traces handler        | https://log-collector-serverless-lambda.agora-dev.w3n.io/otel/v1/traces  | 443  |
|                       | OTEL metrics handler       | https://log-collector-serverless-lambda.agora-dev.w3n.io/otel/v1/metrics | 443  |

## Grafana dashboards

In order to access the Agora Grafana dashboards, your Woven user needs to be assigned to the `observability` group.
If you're not a part of this group, please reach out to the DevRel in `#wcm-org-agora-ama` for the assistance.

Grafana dashboards provided by Agora:

* [Prod](https://wcmagoraprod.grafana.net/d/FPKYjsq4k/telemetry-collector-logs-per-tenant?var-search=&var-source=$__all&from=now-15m&to=now)
* [Pre-prod](https://athena.agora-dev.w3n.io/grafana/d/FPKYjsq4k/log-collector-logs-per-tenant?orgId=1&refresh=5s)
* [Legacy](https://observability.cityos-dev.woven-planet.tech/grafana/d/569ef518df53e079e43c60b6f2fa95b00fd20b0b/log-collector-logs-per-tenant?orgId=1&refresh=5s)

## Telemetry export examples

<b>OTEL telemetry (via otel-generator):</b>

You can try sending OTEL telemetry from the [otel-generator](https://github.com/wp-wcm/city/tree/main/ns/demo/otel-generator): a sample program that you can use as an instrumentation reference or for the testing purposes. Note that the `otel-generator` sends OTEL signals via an official OTEL SDK (golang).

Example logs:
```bash
bazel run //ns/demo/otel-generator:otel-generator -- --kind=logs --path=/telemetry/v1/logs --endpoint=<collector_url:port>
```

Example traces:
```bash
bazel run //ns/demo/otel-generator:otel-generator -- --kind=traces --path=/telemetry/v1/traces --endpoint=<collector_url:port>
```

Example metrics:
```bash
bazel run //ns/demo/otel-generator:otel-generator -- --kind=metrics --path=/telemetry/v1/metrics --endpoint=<collector_url:port>
```

If you are sending your signals to a non-https endpoint (which can be the case if your are inside of a service mesh or simply testing) - use `--insecure` parameter.

Example:
```bash
bazel run //ns/demo/otel-generator:otel-generator -- --kind=logs --path=/telemetry/v1/logs --endpoint=localhost:8080 --insecure
```

## In-cluster access prerequisites in the production environment (aka Gen3 Speedway):

### Default use case

If your services are deployed to Agora production clusters, please consider using Agora's Alloy OTLP endpoints directly to reduce latency (port 4317 for grpc or 4318 for http):

- Dev: http://grafana-k8s-monitoring-alloy.agora-observability-dev.svc
- Prod: http://grafana-k8s-monitoring-alloy.agora-observability-prod.svc

### Special use cases

If you are planning to query your telemetry through `agora_source` label and further correlate it with your device telemetry or want to forward your telemetry to `Databricks`, please use following in-cluster endpoints (port 4318 for http):

- Dev: http://telemetry-collector.agora-iot-dev.svc/telemetry
- Prod: http://telemetry-collector.agora-iot-prod.svc/telemetry

Additionally, please add an entry to a Sidecar manifest in `your` namespace (SMC [example](https://portal.tmc-stargate.com/docs/default/component/stargate-welcome-guides/stargate-multicloud/documentation/features/service-mesh/intra-mesh-traffic/#one-way-a-b)); then add your namespace to [this file](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-iot/speedway/prod/tenant-api-access.yaml), so that `autocommit` could automatically create an allow-listing authorization policy. 

 Example tenant-api-access.yaml (you can also add an [IoTA API access](https://developer.woven-city.toyota/docs/default/component/iota-service/Tasks/onboarding/#in-cluster-services) if needed):

 ```yaml
 #@data/values
 ---
 - tenant: <iota-tenant-name-or-your-namespace-name>
   namespace: <your-namespace-name>
   iota:
     include: false
   telemetry:
     include: true
 ```

 Example Sidecar custom resource hosts entry (`your` namespace):

 ```yaml
 - "agora-iot-prod/*"
 ```

You can also [use](https://github.com/wp-wcm/city/blob/55ed445858e80b78d2faad80cab55d94ff0f7584/infra/k8s/agora-iot/citycd.yaml#L21) Argo CD to substitute variables like [this](https://github.com/wp-wcm/city/blob/55ed445858e80b78d2faad80cab55d94ff0f7584/infra/k8s/agora-iot/speedway/common/telemetry-collector-0.0.4/otel-collector.yaml#L120).

## Alternative telemetry destinations.

It is possible to use OTEL mechanisms to export data [to other destinations](https://opentelemetry.io/ecosystem/registry/?language=collector&component=exporter).
If you are interested in this use case, please [reach out to the Services Team on Slack](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A) to get started.

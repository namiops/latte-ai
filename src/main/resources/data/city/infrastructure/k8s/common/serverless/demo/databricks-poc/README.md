# Databricks + OTEL PoC

This package contains an example deployment with an OTEL-collector sidecar container. It uses OTLP receiver to ingest OTEL signals and Kafka exporter to send them to predefined Kafka topics.

Please also see `databricks-poc.yaml` as it has the configuration with detailed explanations.

## Diagram
![](./databricks-otel-sidecar.png)

## Usage lab2:

**1. Navigate to a common consumer UI and keep the tab open:**
- https://minimal-service-sample-serverless-lambda.agora-lab.w3n.io/

You can also set the filter to `dev.knative.kafka.event` in the top left corner of the screen to hide unrelated events. 

**2. Exec into a PoC container:**

```shell
kubectl -n serverless exec -it deploy/databricks-poc-sample-deployment -- bash
```

**3. Once inside of the container, generate some OTEL signals which will be sent to Kafka:**

```shell
./otel-generator --endpoint=localhost:4318 --pipeline=databricks --path=/ --insecure
```

**4. Or to "default gateway" - log-collector:**

```shell
./otel-generator --endpoint=localhost:4318 --pipeline=whatever-here --path=/ --insecure
```

**5. Depending on your choice above you should see your events in one of:**
- https://minimal-service-sample-serverless-lambda.agora-lab.w3n.io/
- [lab2 Grafana](https://athena.agora-lab.w3n.io/grafana/explore?orgId=1&left=%7B%22datasource%22:%2236319fda-1323-48f2-9756-651de59b55e2%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22queryType%22:%22getTraceSummaries%22,%22query%22:%22%22,%22group%22:%7B%22GroupARN%22:%22default%22,%22GroupName%22:%22Default%22%7D,%22region%22:%22default%22,%22editorMode%22:%22builder%22,%22expr%22:%22%7Bagora_provider%3D%5C%22log-collector%5C%22%7D%20%7C%3D%20%60%60%22%7D%5D,%22range%22:%7B%22from%22:%22now-5m%22,%22to%22:%22now%22%7D%7D)

## Notes
- You can find an application code here: https://github.com/wp-wcm/city/tree/main/ns/demo/otel-generator
- You can find sample topics setup here: https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/serverless/demo/kn-kafka-topics-sample

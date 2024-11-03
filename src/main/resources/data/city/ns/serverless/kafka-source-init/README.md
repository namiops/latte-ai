# Kafka Source Init container code

This is used to create a valid Knative-aware (through SinkBinding) configuration supplier to [an isolated kafka source](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/lambda-sample/eventing/kafka/kafka-isolated-source-0.1.0)

## Local testing:
```bash
DATA_PLANE_CONFIG_FILE_PATH=/tmp/data.json CONFIG_HASH=a K_SINK=b SINK_NAME=c SINK_NAMESPACE=d KAFKA_CONSUMER_GROUP=e KAFKA_TOPICS=f,j bazel run :binary

cat /tmp/data.json
```

## Example:

```json
{
  "generation": "1697183538",
  "resources": [
    {
      "uid": "248d4ce6-e6f0-4086-9756-d73c3071854d",
      "topics": [
        "f",
        "j"
      ],
      "bootstrapServers": "kafka.default:9094",
      "egresses": [
        {
          "consumerGroup": "e",
          "destination": "b",
          "discardReply": {},
          "uid": "248d4ce6-e6f0-4086-9756-d73c3071854d",
          "egressConfig": {
            "retry": 10,
            "backoffDelay": "300",
            "timeout": "600000"
          },
          "deliveryOrder": "ORDERED",
          "reference": {
            "uuid": "90a726df-880c-4b78-99fe-8cb2c965066f",
            "namespace": "d",
            "name": "c"
          },
          "vReplicas": 1,
          "featureFlags": {}
        }
      ],
      "multiAuthSecret": {},
      "reference": {
        "uuid": "90a726df-880c-4b78-99fe-8cb2c965066f",
        "namespace": "d",
        "name": "c"
      }
    }
  ]
}
```

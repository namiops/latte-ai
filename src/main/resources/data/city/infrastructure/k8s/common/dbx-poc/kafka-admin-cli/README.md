# Kafka Admin CLI

## Commands to confirm the connectivity with DBX PoC MSK

```sh
# Listing topics
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVER --command-config $KAFKA_CLI_CONFIG --list

# Consuming messages
kafka-console-consumer --bootstrap-server $KAFKA_BOOTSTRAP_SERVER --consumer.config $KAFKA_CLI_CONFIG --topic agora_test_topic

# Producing messages
kafka-console-producer --bootstrap-server $KAFKA_BOOTSTRAP_SERVER --producer.config $KAFKA_CLI_CONFIG --topic agora_test_topic
```

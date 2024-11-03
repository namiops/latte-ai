# KNative Eventing extension for Kafka

## Kafka Sink & Source
The sample configs in the below folders are used for testing Kafka Sink & Source:
- cityos-kafka-topic-0.1.0
- sink-0.1.0
- source-0.1.0

In the sample, Kafka Source captures messages from Kafka topic `lambda.knative-source-topic` and pass them to Kafka Sink.
The Kafka Sink redirect the incomming messages to another Kafka topic `lambda.knative-sink-topic`.

You can use Kafka console cli to trigger the above eventing scenario in the local cluster.
The cli is also available at Deployment `kafka-cli` in namespace `kafka-sample-app`.

1. Run the below command and wait for incomming messages.
```
$ kafka-console-consumer --bootstrap-server cityos-kafka-kafka-bootstrap.kafka:9092 --topic lambda.knative-sink-topic 
```
2. In another terminal, run a message producer by
```
$ kafka-console-producer --topic lambda.knative-source-topic --bootstrap-server cityos-kafka-kafka-bootstrap.kafka:9092
```
3. Submit any messages
```
>{"message":"Hello from Source topic"}
>test
```
4. In the consumer side, you should see new messages appear.
```
{"message":"Hello from Source topic"}
test
```

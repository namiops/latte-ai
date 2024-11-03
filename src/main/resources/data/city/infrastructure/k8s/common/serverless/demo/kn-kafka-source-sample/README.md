# Kafka serverless source sample

This is a sample deployment of the Kafka serverless source. It reads messages from the following topics:
- kafka-topic-one
- kafka-topic-two

Then forwards incoming messages to:

- LAB2: https://minimal-service-sample-serverless-lambda.agora-lab.w3n.io
- DEV2: https://minimal-service-sample-serverless-lambda.agora-dev.w3n.io
- LAB: https://minimal-service-sample-serverless-lambda.agora-lab.woven-planet.tech
- DEV: https://minimal-service-sample-serverless-lambda.cityos-dev.woven-planet.tech

## Sending events to Kafka and verifying results

There is an `kafka-admin-cli` toolbox deployment in a `serverless` namespace.

1. Connect to it via `kubectl -n serverless exec -it deploy/kafka-admin-cli -- bash`
2. Execute `kafka-console-producer --topic serverless.kafka-topic-one --bootstrap-server kafka.default:9094`
    - `serverless.kafka-topic-two` can also be used, the as the source listens to the both topics.
3. Send some data, for example: `{"message":"Hello from Kafka!"}`
4. Check one of the links above (depending on the cluster) to see the events.

## Topics creation

Navigate to `infrastructure/k8s/common/serverless/demo/kn-kafka-topics-sample` to see topic a creation sample.

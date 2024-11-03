# Troubleshooting and best practices

This is a collection of common problems and best practices around RabbitMQ and queue management. This document SHOULD be used as a guideline and not a final solution because depending on your architecture the proposed design MAY or MAY NOT be applicable.

## Alternate Exchange for "Unroutable messages"

One of the most common issues while working with AMQP and Exchanges is the *Unroutable Message* error thrown by RabbitMQ. This happens for [multiple reasons](https://www.rabbitmq.com/publishers.html#unroutable), often when the defined exchange doesn't know to which destination route a message because the routing key doesn't match with any binding.

In the diagram below the proposed architecture will bind an *alternate exchange* (called *ae* in the code further down) to the *main* exchange. The alternate exchange will then publish to a queue (think of it as of a Dead Letter Queue) and ideally a specialized consumer is pulling from this queue and redirecting to observability for further analysis. 

The key aspect of this strategy is that without an alternate exchange the message is mostly lost and it becomes hard to understand in which circumstances the routing / binding is failing. With a dedicated exchange / queue developers can pull the message(s) and find out key details about payload and failing binding keys.

![whatevs](../../diagrams/alternateex.png)

By default, we have created `ae` policies for each vhost so that unroutable messages from main exchanges will go to `ae`. You can check them from the [Rabbit Admin UI](https://rmq-ui-iot.agora-dev.w3n.io/#/policies). We have also implemented an *Unrouted Messages Consumer*. This consumer continuously consumes the messages in `ae` and send them to the observability platform. In this way, we could prevent an accumulation of messages in Rabbit that could potentially trigger alarms.

These messages can be found and queried in the [Grafana Loki UI](https://athena.agora-dev.w3n.io/grafana/explore?orgId=1&left=%7B%22datasource%22:%22da2e85e8-e424-4b99-b0f8-c0b4319f22b5%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22editorMode%22:%22builder%22,%22expr%22:%22%7Bagora_provider_namespace%3D%5C%22serverless%5C%22,%20agora_source%3D%5C%22test%5C%22%7D%20%7C%3D%20%60unrouted%60%22,%22queryType%22:%22range%22%7D%5D,%22range%22:%7B%22from%22:%22now-1h%22,%22to%22:%22now%22%7D%7D). For this, you need to specify `agora_provider_namespace` as `serverless` and `agora_source` as `<YOUR_NAMESPACE>`.

By default, `amq.topic` is excluded from the `alternate-exchange` policy to prevent MQTT messages from being mistakenly directed to the `ae` queue in the absence of an MQTT subscriber. However, if your MQTT queues are persistent, you can include `amq.topic` in the `alternate-exchange` policy without any concerns.

### Code examples
The following YAML demonstrates the way to create an `ae` and set rules to bind main exchanges in your vhost to it. The `spec.pattern` property allows us to use a regex to exclude the `ae` itself and `amq.topic`, enabling rerouting of all unroutable messages for consumption and analysis. 

The policy has been created by default so you don't need to apply it again. But just in case you wish to define your own `ae` binding rule, simply modify the regex pattern `spec.pattern` in the YAML below and commit it to your code repository. The CD pipeline will then implement this change to the cluster and your RabbitMQ configurations. Alternatively, you can create/modify this policy using the [Rabbit Admin UI](https://rmq-ui-iot.agora-dev.w3n.io/#/policies). 

```YAML
apiVersion: rabbitmq.com/v1beta1
kind: Policy
metadata:
  name: policy-alternate-exchange
  namespace: <your namespace>
spec:
  vhost: <your vhost>
  name: policy-alternate-exchange
  pattern: ^(?!ae$|amq\.topic$).*$ # This regex matches any string that does not end with "ae" or "amq.topic". You can define your own matching rule here.
  applyTo: exchanges
  definition:
    alternate-exchange: ae
  rabbitmqClusterReference:
    name: rabbitmq
    namespace: iot
---
```

## Queue consumer

### Use Push API
When implementing an AMQP queue consumer, it is preferable for the application to subscribe to RabbitMQ, allowing it to push messages (deliveries) directly to the application for better performance. For more details, [see RabbitMQ's API guide on consuming](https://www.rabbitmq.com/api-guide.html#consuming).

### Connection recovery
The consumer should be capable of tolerating a RabbitMQ node being down for short periods. For instance, Agora frequently carries out cluster operations necessitating node restarts, which can take a few minutes for a RabbitMQ node to restart. This is particularly important if you are using classic queues, where data is available on only one node due to the absence of queue mirroring. Consequently, consumers depend on a single node for their operations.

Some libraries are offering automatic connection recovery. Please refer to [RabbitMQ's documentation on connection recovery](https://www.rabbitmq.com/consumers.html#connection-recovery) for information on how to configure this feature.

#### Go
Golang consumer application can register to the NotifyClose() and re-run the connection setup as explained in [amqp091 library](https://github.com/rabbitmq/amqp091-go/blob/4a009c7fe6dd10008d69bf8a8f13427983659570/connection.go#L328-L352). See [example_client](https://github.com/rabbitmq/amqp091-go/blob/main/example_client_test.go) for the sample implementation.

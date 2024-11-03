# Code Lab

[kafka-filterer-sample](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/kafka-filterer-sample) with its service implementation [simple-avro-maven](https://github.com/wp-wcm/city/tree/main/ns/kafka-apicurio-sample/simple-avro-maven) is a sample application of Filterer.

In this scenario, the producer writes one greeting message per second containing the sender's name which is considered PII. The consumer just consumes the messages and prints them out.

Let's take a look at the manifests and the code.

## [Producer manifests](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1)

Let's take a look at producer manifests first.

### [Kafka config](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-values.yaml)

[kafka-values.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-values.yaml) is the template value for generating the Kafka-related manifests. Here are the things to highlight:

```yaml
cityoskafkas:
- namespace: kafka-filterer-producer
  topics:
  - name: simple-avro-maven-topic
    # (...)
    private: true
    allow_filterer: true
    owner: producer
    # (...)
    messages:
    - # (...)
      value:
        description: "Avro record for Greeting entity"
        type: avro
        schema:
          # (...)
          content: #@ data.read("kafka-apicurio-sample-greeting-schema.avsc")
```

- `private: true` means that the topic is a private topic.
- `allow_filterer: true` means that the topic allows Filterer to access itself.
- `owner: producer` means that the topic is owned by the service that produces messages to the topic.
- `type: avro` means that the topic's messages are Avro records.
- `content: #@ data.read("kafka-apicurio-sample-greeting-schema.avsc")` reads the schema definition contents in the specified file (which we cover next).

!!! warning "Supported schema type"

    Currently, only Avro schema is supported. If you want other schema types (e.g. protobuf, json schema), please reach out to us in #wcm-agora-team-ama channel in Slack.

### [Avro schema](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-apicurio-sample-greeting-schema.avsc)

```json
{
  "type": "record",
  "name": "Greeting",
  "fields": [
    {
      "name": "Message",
      ...
    },
    {
      "name": "Time",
      ...
    },
    {
      "name": "Sender",
      "type": "string",
      "x-woven-infotype": "PERSON_NAME",
      ...
    }
  ]
}
```

[kafka-apicurio-sample-greeting-schema.avsc](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-apicurio-sample-greeting-schema.avsc) is the Avro schema definition for the messages in the topic. Please notice `"x-woven-infotype": "PERSON_NAME"`. This is how you annotate the schema with PII information.


## [Consumer manifests](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/kafka-filterer-sample/consumer-0.0.1)

Let's not turn to the consumer manifests.

### [Kafka config](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/consumer-0.0.1/kafka-values.yaml)

Let's see the diff with the producer's Kafka config.

```diff
diff -u infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-values.yaml infrastructure/k8s/common/kafka-filterer-sample/consumer-0.0.1/kafka-values.yaml
--- infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-values.yaml
+++ infrastructure/k8s/common/kafka-filterer-sample/consumer-0.0.1/kafka-values.yaml
@@ -2,15 +2,15 @@
 #@data/values
 ---
 cityoskafkas:
-- namespace: kafka-filterer-producer
+- namespace: kafka-filterer-consumer
   topics:
   - name: simple-avro-maven-topic
     # (...)
     private: true
     allow_filterer: true
-    owner: producer
+    owner: consumer
     maxMessageSize: 100KB
     cleanupPolicy: compact,delete
     messages:
@@ -28,3 +28,5 @@
             - avro
             - kafka
           content: #@ data.read("kafka-apicurio-sample-greeting-schema.avsc")
+  consumerGroups:
+  - simple-avro-maven-consumer
```

- `namespace`: The sink topic is in the separate namespace from the source topic, hence this difference.
- `owner`: The consumer service owns the sink topic.
- `consumerGroups`: In this scenario there is no consumer group for the source private topic in the source namespace (i.e. Filterer is the only consumer) but the sink private topic has a consumer group for the sample consumer service to consume messages.


### [Avro schema](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/consumer-0.0.1/kafka-apicurio-sample-greeting-schema.avsc)

See it's exactly the same as [the producer Avro schema](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/kafka-filterer-sample/producer-0.0.1/kafka-apicurio-sample-greeting-schema.avsc). Filterer is not supposed to affect the schema of the messages.


## [Client application code](https://github.com/wp-wcm/city/blob/main/ns/kafka-apicurio-sample/simple-avro-maven/src/main/java/io/apicurio/registry/examples/simple/avro/maven/SimpleAvroMavenExample.java)

Let's take a look at the code of the producer and consumer service.

In this sample app, a single file [SimpleAvroMavenExample.java](https://github.com/wp-wcm/city/blob/main/ns/kafka-apicurio-sample/simple-avro-maven/src/main/java/io/apicurio/registry/examples/simple/avro/maven/SimpleAvroMavenExample.java) implements both the producer and consumer functionality (don't do this in production code 😅), and it acts differently depending on the given argument.

The [consumer part of the code](https://github.com/wp-wcm/city/blob/e8c9152838199bcb7d1b9b76c5d3ad62cf781f51/ns/kafka-apicurio-sample/simple-avro-maven/src/main/java/io/apicurio/registry/examples/simple/avro/maven/SimpleAvroMavenExample.java#L190-L217) is fairly boring -- it is just a normal Kafka consumer implementation (which is a good thing!) so we won't cover it in this document.

There are several points to highlight in the producer part of the code.

### Producer<Object, Object> createKafkaProducer()

The value serializer class config is worth highlighting.

```java
import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
// (...)

// Use the Apicurio Registry provided Kafka Serializer for Avro
props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());
```

Agora adopts [Apicurio Registry](https://developer.woven-city.toyota/docs/default/Component/apicurio-service) as our schema registry, and it is required to use the Apicurio Registry provided Kafka Serializer. This is especially important because the serializer is responsible for stamping the message schema's global ID in each of the produced messages behind the scenes (without you having to do it explicitly).

!!! warning "Adaptability of Confluent Schema Registry serializers"

    Admittedly, Apicruio Registry serializer/deserializer has lower language support than [Confluent's](https://docs.confluent.io/platform/current/schema-registry/index.html) does (i.e. only Java library exists).
    Please reach out to us in #wcm-agora-team-ama channel in Slack if you need to use Confluent's ser/des so we can add support for it in Filterer.

### void keepPublishing(String schemaData)

Apart from the usual producer logic,

```java
private static final String KAFKA_USER_ID_HEADER = "x-woven-id";
// (...)
producedRecord.headers().add(KAFKA_USER_ID_HEADER, ("woven-id-" + keyId).getBytes());
```

is special about this sample app. Filterer expects this header to be present to determine which user this personal information is about. In the real use case, use the real WovenID as the header value.

## Wrap-up

In this document, we covered the basic concepts of Filterer, how to set it up, and how your manifests and code should look like. 

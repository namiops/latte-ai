# Usage

## How to register the schema

We want to manage the schemas with a declarative way so [apicurio-registry-content-sync-operator](https://github.com/Apicurio/apicurio-registry-content-sync-operator) is deployed in Agora.

To register the schema, please add the `Artifact` resource like following:

```yaml
apiVersion: artifact.apicur.io/v1alpha1
kind: Artifact
metadata:
  name: sample-schema
  namespace: sample-schema
spec:
  version: "1"
  groupId: sample-group  # This is optional. If you don't specify this, the groupId will be set as `default`
  artifactId: sample-schema
  name: Greeting
  description: "Avro record for Greeting entity"
  type: AVRO
  labels:
  - avro
  - kafka
  content: |
    {
        "type": "record",
        "name": "Greeting",
        "fields": [
          {
            "name": "Message",
            "type": "string",
            "doc": "Hello-[ID] will be set. [ID] is auto-incremented",
            "default": "Hello-0"
          },
          {
            "name": "Time",
            "type": "long",
            "doc": "the timestamp of the message",
            "default": 0
          },
          {
            "name": "Sender",
            "type": "string",
            "doc": "the person who sent the message",
            "x-woven-infotype": "PERSON_NAME",
            "default": "Api Curio"
          }
        ]
    }
```

The `fields.doc` property is optional but SHOULD be filled in so that anyone can understand your message content. 
If the item is PII, `x-woven-infotype` MUST be specified conforming to [TN-0055 Data Privacy Annotations - Google Docs](https://docs.google.com/document/d/1qxOgzbV6Q_elZgbA-zD5nw4LSSD3JGpDu00ReSsheYQ/edit).


If you have the use-case that you have to register the schema in runtime like calling the API, please reach out to @agora-data-orchestration at [#wcm-org-agora-ama](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7)

You can check if the schema is registered at [Apicurio UI](https://apicurio.cityos-dev.woven-planet.tech/ui/artifacts)

## How to update the schema

It's necessary to update the `spec.version` as well to update the schema as follows:

```yaml
apiVersion: artifact.apicur.io/v1alpha1
kind: Artifact
metadata:
  name: sample-schema
  namespace: sample-schema
spec:
  version: "2"  # <====== UPDATE the version
  ...
```


### Content rules

Apicurio supports two content rules, `Validity rule` and `Compatibility rule`

About these rules, check [Apicurio Registry artifact and rule reference :: Apicurio Registry](https://www.apicur.io/registry/docs/apicurio-registry/2.4.x/getting-started/assembly-registry-reference.html#registry-rule-types_registry)

[Unfortunately, these settings cannot be configured in the CRD for now (June, 2023)](https://github.com/Apicurio/apicurio-registry-content-sync-operator/issues/98).

For now, `Validity rule` is set `FULL` globally whereas `Compatibility rule` is not enabled.

![apicurio-global-content-rule.png](img/apicurio-global-content-rule.png)


If you want to enable `Compatibility rule`, please make a contact with @agora-data-orchestration. We will enable that for you.

![apicurio-validity-rule.png](img/apicurio-validity-rule.png)


## How to fetch the schema

You can fetch your schema by appending the following to the base URL:

- For Apicurio URL: `/apis/registry/v2/groups/${group_id}/artifacts/${artifact_id}/`
- For Confluent v6 compatible URL (the `group_id` must be `default`): `/apis/ccompat/v6/schemas/ids/${content_id}`
- For Confluent v7 compatible URL (the `group_id` must be `default`): `/apis/ccompat/v7/schemas/ids/${content_id}`

The base URL is as follows:

| ENV            | INTERNAL BASE URL                               | EXTERNAL BASE URL                             |
|----------------|-------------------------------------------------|-----------------------------------------------|
| Speedway Prod  | http://apicurio.agora-apicurio-system-prod:8080 | https://apicurio.woven-city.toyota            |
| Speedway Dev   | http://apicurio.agora-apicurio-system-dev:8080  | https://dev-apicurio.woven-city.toyota        |
| Dev2 (Preprod) | http://apicurio.apicurio:8080                   | https://apicurio.agora-dev.w3n.io             |
| Dev1 (Legacy)  | http://apicurio.apicurio:8080                   | https://apicurio.cityos-dev.woven-planet.tech |

- e.g. the sample URL for the example above will be https://apicurio.${cluster_domain}/apis/registry/v2/groups/sample-group/artifacts/sample-schema

## Integration with Kafka

A schema is typically used in data serialization, which is the process of converting data structures or objects into a format that can be transmitted across a network or stored in a file. In this context, a schema defines the format of the serialized data and is used to validate the data as it is being deserialized by another system or application. Apicurio Registry supports Avro, JSON Schema, and Protobuf serializers and deserializers (serdes). When you write producers and consumers using these supported formats, they handle the details of the wire format for you, so you don’t have to worry about how messages are mapped to bytes. Kafka producer applications can use serializers to encode messages that conform to a specific event schema. Kafka consumer applications can then use deserializers to validate that messages have been serialized using the correct schema, based on a specific schema ID.

![apicurio-kafka-arch](img/registry-serdes-architecture.png)

(source: https://www.apicur.io/registry/docs/apicurio-registry/1.3.3.Final/assets-images/images/getting-started/registry-serdes-architecture.png)

!!!Note
    There are some strategies to look up a schema in Apicurio Registry and `TopicIdStrategy` will be used as default.
    https://www.apicur.io/registry/docs/apicurio-registry/2.4.x/getting-started/assembly-using-kafka-client-serdes.html#registry-serdes-concepts-strategy_registry


If you are using [our monorepo](https://github.com/wp-wcm/city), you can configure your topic with schema more easily using [zebra](https://developer.woven-city.toyota/docs/default/Component/zebra-service)

We have some sample codes that interact with Kafka and Apicurio registry.

- Golang sample: https://github.com/wp-wcm/city/blob/main/ns/kafka-pass-by-ref-demo/sample1-use-existing-service/medical-advisory-svc/misc.go
- Java sample: https://github.com/wp-wcm/city/tree/main/ns/kafka-apicurio-sample

For Java applications, you can see the official examples:
https://github.com/Apicurio/apicurio-registry-examples

However, there are not many samples on the internet that use Apicurio Registry, and there are more using Confluent Registry. Especially, Apicurio's library only exists in Java, so it might be inconvenient if you want to use it in other languages. In such cases, I recommend using Confluent's library with Apicurio's provided Confluent-compatible URL (for example, in the dev1 environment: https://apicurio.cityos-dev.woven-planet.tech/apis/ccompat/v6/).

If you have any issues or questions, please reach out to @agora-data-orchestration at [#wcm-org-agora-ama](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7)!

## Usecases other than Kafka

- Personal Data Store(PDS)
  - PDS validates the message based on the json schema
  - You can check the deployed schema at [PDS Async API](https://developer.woven-city.toyota/catalog/default/api/pds-schema-async-api/definition)
- **Your Application will be here!**

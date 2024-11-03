# Limitation

Filterer is in its early stages and has dozens of limitations. We attempt to list some of the potentially notable items here.
We are eager to prioritize things based on the actual use cases, so please do not hesitate to give us feedback in #wcm-agora-team-ama channel in Slack.

- Filtering granularity is at the message level
    - This means that if a message contains a data attribute that the user hasn’t consented to share, the entire message will be dropped. Maybe by-field filtering is useful in some cases. ([Monday#4930568435 (Potential) Personal Information Filterer to support _by_field_ mode](https://wovencity.monday.com/boards/3813113014/pulses/4930568435))
- Only Avro schema is supported
    - Some might want support for protobuf and json schema. ([Monday#4956302546 Personal Information Filterer to support protobuf](https://wovencity.monday.com/boards/3813113014/pulses/4956302546) / [Monday#4956303370 ](https://wovencity.monday.com/boards/3813113014/pulses/4956303370))
- Only Support Kafka clients using Apicurio Registry serializer/deserializer.
    - Supporting Confluent Schema Registry serializer/deserializer might be helpful. ([Monday#5103863720 PII Filterer to support records where globalID is embedded in the payload instead of header](https://wovencity.monday.com/boards/3813113014/pulses/5103863720))
- Only [TopicIdStrategy](https://www.apicur.io/registry/docs/apicurio-registry/2.4.x/getting-started/assembly-using-kafka-client-serdes.html#registry-serdes-concepts-strategy_registry:~:text=strategy.ArtifactReferenceResolverStrategy.-,Strategies%20to%20return%20a%20reference%20to%20an%20artifact,-Apicurio%20Registry%20provides) is supported.
    - The major implication of it is that one topic can only have one schema. In case multiple data structures may be sent to the same topic, the schema needs to use [unions](https://avro.apache.org/docs/1.11.1/specification/#unions). ([Monday#5042742173 (Potential) Personal Information Filterer to support other schema lookup strategies than `TopicIdStrategy`](https://wovencity.monday.com/boards/3813113014/pulses/5042742173))
- Performance of Filterer (with its dependencies) are not well-tested ([Monday#5167310311 (Potential) Conduct a performance/latency test for Personal Information Filterer](https://wovencity.monday.com/boards/3813113014/pulses/5167310311))

# Overview

## What is Apicurio Registry?

Apicurio registry is a schema registry mainly developed by RedHat.

[Confluent schema registry](https://docs.confluent.io/platform/current/schema-registry/index.html) is popular but unfortunately it has an issue in license. Apicurio registry supports similar functions and is [licensed under Apache License 2.0](https://github.com/Apicurio/apicurio-registry/blob/main/LICENSE), which fits our objective `vendor-agnostic`.

Apicurio supports the Confluent compatible URL so we can use the confluent schema registry libraries as well.
- v6: https://apicurio.{cluster_domain}/apis/ccompat/v6
- v7: https://apicurio.{cluster_domain}/apis/ccompat/v7

Details are in the official document: 
- [Apicurio Registry documentation :: Apicurio Registry](https://www.apicur.io/registry/docs/apicurio-registry/2.4.x/index.html)


## Why use Apicurio Registry?

### for Kafka users
Having a Schema Registry is really best practice when utilizing Kafka to ensure there is an automated way of ensuring data verification, schema evolution, and ability for new consumers to emerge without breaking downstream. Essentially these are the basics of good data governance when using Kafka.

As your use of Kafka develops and the people who use it change, having a Schema Registry ensures:
- Data verification
- Peace of mind
- No breaking schemas even as your Kafka and team evolve.

Details are in [What is the Schema Registry and why do you need to use it?](https://www.conduktor.io/blog/what-is-the-schema-registry-and-why-do-you-need-to-use-it/)

Also, check the document written by IA team.
- [Request and Response Pattern(Asynchronous) - Google Docs](https://docs.google.com/document/d/1sQa72_P2rHou22T53w4f5fZp1Uy11iz4CKXwXMMoJ0M/edit)


### for the others

Even when you don't use Kafka, you can get some benefit of the functions the schema registry provides, such as ensuring data verification, schema evolution.
[The Personal Data Store(PDS)](https://github.com/wp-wcm/city/tree/main/ns/pds) can be such a use-case. 

# Kafka Personal Information Filterer

## Introduction

Sharing personal information through asynchronous channels like Kafka while respecting user privacy is important but hard.

Kafka Personal Information Filterer (just Filterer hereafter) is one of the solutions to this problem. The basic idea here is to bridge two private topics that sit in different namespaces and do some message filtering based on the topic's schema and user consent.

For more details about the design decisions, please refer to [TN-0353 Async messaging and privacy, consent - Filterer](https://docs.google.com/document/d/1_ySF-gpb7sJf5l43QOHSETnD5oL-GTjkEm8IAioi7I8/edit#heading=h.5qm13wuvtiz9).


!!! info "Feedback Wanted!"

    This component is ready for use but has the bare minimum functionality, with possibly insufficient performance.

    We are looking for feedback from you to clarify the direction of the improvement so we can support you most effectively. Please reach out to us in #wcm-agora-team-ama channel in Slack.

## Prerequisite knowledge

- You are expected to have a basic understanding
    - Kafka, including topics, schemas, and consumer groups.
    - how consent checking works in Agora, including the concept of _Service Mapping_ and _Data Attributes_. [See here](https://developer.woven-city.toyota/docs/default/Component/consent-management-service/en/consent/) if you are not familiar.

## How it works

Here's the overview of how Filterer relates to other components.

![overview](./assets/overview.png)

_(source: [Lucid App link](https://lucid.app/lucidchart/8920b1ec-448b-4c04-b4df-5e190389d465/edit?v=194&s=612&invitationId=inv_23067b07-4b8d-4304-8d58-df6787e41d84&page=0_0#))_

!!! tip "Private topic"

    A private topic is a topic that is only accessible by a single namespace and has a strict retention period policy. It is the recommended way to share personal information through Kafka among services within a namespace.

    More information can be read [here](https://docs.google.com/document/d/1wWupLXcqrbyLrVkLJZKvjFFk5qumtZjMIKTJItC9OmM/edit).


Assuming the setup process described in the next section is completed, the flow works as follows (the bullet number corresponds to the numbers in the description):

1. The publisher service writes the messages to the source private topic (=the private topic in the namespace where the publisher resides).
2. Filterer consumes the messages from the source private topic.
3. Filterer extracts the schema’s global ID (that resolves to the specific version of a schema) from the message, loads the PII-annotated schema specified by the global ID, and determines the set of [Data Attributes](https://docs.google.com/document/d/1sbZ8_b-WKYN3GWFayefUFcAA8Po0YmUFTJM9ZbfJZWY/edit#bookmark=id.62gk5zs4t063) the messages may include.
4. Filterer extracts the user ID from the message and queries the consent service: "Has this user consented to share these Data Attributes with this consumer service?"
5. Based on the result, Filterer filters out the data the user hasn’t consented to share with the Service.
6. Filterer writes the consent-verified message to the sink private topic (=the private topic in the namespace where the consumer resides).
7. Finally, the consumer service consumes the consent-verified message from the sink private topic.


!!! tip "Filtering granularity"

    In the current implementation, the filtering granularity is at the message level. This means that if a message contains a data attribute that the user hasn’t consented to share, the entire message will be dropped.

    Give us feedback in #wcm-agora-team-ama channel in Slack if you have use cases that this model doesn't support.


!!! info "Filterer accessing private topics from a separate namespace"

    You may notice Filterer is accessing private topics from a separate namespace. Consider Filterer as an exceptional component developed and managed by Agora that has read access to its source private topics and write access to its sink private topics from a separate namespace.


A good thing about this design is that both the producer and consumer service programs don’t need to know anything about the consent service or Filterer. You can simply implement them as a normal Kafka producer/consumer.


## Deployment and ownership

One Filterer instance is deployed per `<Source, Sink>` pair. Multiple Filterers can be deployed pointing to one source topic but different sink topics (vice versa). They are deployed by Agora and in one of the Agora-managed namespaces.

Agora owns all the instances of Filterer and monitors their health, including its consumer group lags on the source private topic.

The producer and consumer service team own the source/sink private topic respectively, and are responsible for correctly annotating the schemas for those topics.


## Setup process

For the whole flow to work, the following resources need to be set up per use case basis:

1. Filterer
2. Source private topic (and its PII-annotated schema)
3. Sink private topic (and its PII-annotated schema)

As the typical process, the following instruction assumes the private topic already exists on the producer side, and a new consumer team is attempting to consume messages from it.

### Requesting Filterer setup

Please reach out to us in #wcm-agora-team-ama channel in Slack to request Filterer setup, including the following information:

1. The source namespace
2. The sink namespace
3. The topic name
4. The consumer's client name [registered in the consent service](https://developer.woven-city.toyota/docs/default/Component/consent-management-service/en/consent/#register-in-service-mapping)
5. An acknowledgment from the producer team (e.g. Slack message link) that they are okay with your service consuming from their private topic

### Prepare the sink topic

The consumer service team prepares the sink private topic with its PII-annotated schema. The schema will look the same as the source private topic’s schema that should exist already.

!!! tip "private topic preparation"

    Agora MAY help with this step, so please do not hesitate to clarify in the above request that you need help with this step in case you are unsure. Agora MAY fully automate this step in the future.


### Agora will let you know

Once all the preparation is done, Agora will let you know you are ready to consume messages.

!!! tip "You're a producer service wanting to write messages to an existing private topic?"

    The basic idea is the same. Please reread the above instruction by replacing producer⇔consumer / source⇔sink where it makes sense.

## Next up

Now that you have a basic understanding of Filterer, let's take a deeper look at the working code!

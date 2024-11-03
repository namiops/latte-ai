# Kafka and Privacy: Async request/response

## Introduction

The goal of this tutorial is to get you familiar with how Agora encourages you to respect user privacy under the context of _cross-namespace_, _asynchronous request-response_, with a concrete, working reference implementation (in Golang).

!!! info "Technical Note"

    The idea articulated in this tutorial is based on [TN-0249 Asynchronous messaging and PII, consent - Async request/response](https://docs.google.com/document/d/1Su28z4w9A4eznizaaF3yxFR2wm76SFXyYAQ-_fyW-rc/edit).


!!! note "Intra-namespace Kafka usage"

    If you are looking to share personal data through Kafka ONLY WITHIN your Kubernetes namespace, things can be a lot easier. Please refer to [TN-0205 PII, Kafka and consent - Private Topics](https://docs.google.com/document/d/1wWupLXcqrbyLrVkLJZKvjFFk5qumtZjMIKTJItC9OmM/edit).

## Problem Description

Respecting privacy, or respecting user consent before sharing their data with other services, is one of the most important properties that Woven City aims to have. On the other hand, making sure user consent is always checked for asynchronous workflow is anything but easy. Let’s look into some of the reasons.


### Producer generally doesn’t know who the consumer will be

Usually in SYNChronous communication (e.g. REST API, gRPC), the communication initiator knows who’s the peer it’s talking to. It is technically possible for the producer (=the communication initiator who wants to send data to the peer) to check if the consumer (=the peer who receives the data) has the user’s consent to process the personal data it’s about to send.

Generally, that’s not the case in ASYNChronous communication. Producer writes messages once to a topic, and multiple consumers (unknown to the producer) may consume the messages from the topic. It is inherently impossible to check consent at the producing time.

### Producer generally doesn’t know when the consumer(s) will be consuming the data

In asynchronous workflows, regardless of the technology/implementation of the messaging system (e.g. Kafka, RabbitMQ, etc), produced messages are retained in the messaging system at least until the consumer(s) read them (or exceptionally, some sort of TTL has passed).

Suppose the producer knew who the consumer will be, and could check the user had consented to share their data to that particular consumer _at the time of writing the message to the messaging system_.

It is still possible that the user withdraws the consent before the consumer consumes it, in which case, the user might get upset if the consumer service is too knowledgeable about them even after consent withdrawal.

This brings another complexity to this problem.

### Hard for consumer developers to make sure consent is always checked for the consumed messages

All these problems may be solved simply if every consumer service always checks if it has the user consent before it stores/processes any bit of the consumed data.

But can you, suppose you are a consumer service developer, make sure you don’t ever forget to write that consent-check logic? If you can, can you still make sure your colleagues don’t ever forget to?

### Producers can’t just rely on the consumers’ correctness

OK, maybe consent can be checked by the consumer.

But as the actual service that manages the personal data and tries to share it with other services, it is also the producer service's responsibility to make sure the users' preferences are respected. Are you, suppose you are a producer service developer, willing to rely on the consumer services’ correctness to check consent before they process the data? Can you hold the same answer when Woven City starts to onboard 3rd party services that our organization doesn’t have any control over?

## Wrap up

Respecting user consent in asynchronous workflows is tough. The following page introduces a design pattern to solve this issue.

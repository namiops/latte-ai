# SPIFFE/SPIRE tutorial

Welcome to SPIFFE/SPIRE tutorial!

[SPIFFE](https://spiffe.io/) is a set of standards for securely identifying software systems in dynamic and heterogeneous environments. [SPIRE](https://spiffe.io/docs/latest/spire-about/) is our chosen implementation for `(Agora-external) <=> (Agora-internal)` service-to-service communication. 

While there are tons of information about this topic on the Internet (see [Reference section](/docs/default/Component/spire/00_whats_spiffe_spire#references) on the next page), this tutorial aims to guide through the minimal knowledge required to get started with SPIFFE/SPIRE with some hands-on examples.

## Why are we here?

!!! Success "TL;DR - Practical short answer"

    Because you want to have your services running outside Agora interact with services inside Agora (vice versa), **securely**.

### Identity problem

> Trust is an extremely important and difficult thing to establish. This has been compounded by a variety of factors as the world has moved to a microservice model - standard perimeter-defense methodologies don’t work well when you have to cross k8s clusters (or separate VPCs, or even separate cloud providers), and firewall rules are impossible to keep updated when pod workloads can be deployed and evicted in seconds.
> 
> A cornerstone of any trust model is identity and authentication. How do workloads identify themselves, and how do counterparties verify that the information provided is accurate?

-- Cited from [TN-0213 - Look ma, no keys: SPIFFE in Agora](https://docs.google.com/document/d/1JvCU28FcR4fEieXyagODlfcy9dy2S3C3I6T_C7cySKk/edit#heading=h.a94lfxx9vg0o)


Every service running in Agora is automatically assigned a verifiable identity. This identity can be used for authentication and authorization; so for example, if you’re a developer of service A, it’s easy to configure service A such that it can only allow requests from service B and C developed by other teams, if all of A, B, and C are running in Agora (through mechanisms like [CityService](https://developer.woven-city.toyota/docs/default/Component/city-service-operator-service) or [Drako](https://developer.woven-city.toyota/docs/default/Component/drako-service)).

But that’s not always the case. While Agora aims to onboard as many services involved in Woven City as possible, there are good reasons that some services may want to run their workloads outside Agora, such as

- When the service is strongly tied to the hardware/facility it runs on, and difficult to move that to the cloud.
- When we need to involve 3rd party services that we have no control over and are destined to run outside Agora.

In such cases, things get much more complicated because no central place can issue and distribute reliable identities by default anymore.

### Encryption in transit problem

Identity problem discussed above, knowing "Who is it that I'm talking to?" is one aspect of secure service-to-service communication. Keeping the messages between those parties confidential and integral is another aspect of it.

[Mutual TLS](https://www.cloudflare.com/learning/access-management/what-is-mutual-tls/) (or mTLS for short) is the standard protocol for achieving this in Woven City project, and is enforced by default within Agora service-to-service interactions.

Again when we start to think about external services, it’s not that simple. How do we distribute/rotate/revoke certificates in a scalable manner?

## What's next?

Now that we know the problem better, let's dig deeper into SPIFEE/SPIRE, the open source projects that are meant to address the very problem described above.

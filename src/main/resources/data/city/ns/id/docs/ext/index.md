---
title: Home
summary: About the Woven ID platform
authors:
  - maru@woven.toyota
date: 2024-09-26
---
# [go/wovenid](http://go/wovenid)

Woven ID is a collection of services that allow teams to add authentication and
authorization to their services. Here you can find resources to help you
getting started with securing your workload in Agora.

## Before you begin

It is expected that you have some understanding of how Agora relates to Kubernetes
and service mesh, although specific service mesh knowledge is not required.

Throughout this documentation we use the words `client` and `tenant` as defined
in [TN-0482].

## Getting Started

You can find more about our individual services by digging down into each category.

* [Authentication]
* [Authorization]

## Contact

If you have any questions or feedback, reach out to us through the [AMA] slack
channel.

## Components

Here is a list of the systems we develop for reference:

* [drako]: authentication and authorization framework.
* [drako buddy]: inject drako authorization filter on REST workloads.
* [drakopolis]: group management systems (REST and CI/CD).
* [keycloak]: OpenSource software we use as foundation for our IdP (see [#things-to-avoid]).
* [leonidas]: [OAuth 2.0] client management system.
* [propy]: LDAP bridge for network device authentication.

We also have some experimental work done in [DID].

## Things to Do

* leverage your proximity with the development relations team.
* reach out to identiy team through slack or email whenever needed.
* send us feedback of our services, documentation and support.
* use standard APIs that implement the [OAuth 2.0] standard flows.
* delegate your REST application authorization needs to [drako].
* reach out to us to discuss your design before committing to it.
* use [drakopolis] for group management, even if not integrating with [drako].

## Things to Avoid

* **do not** call the [keycloak] APIs directly.
  * in particular keycloak admin API is not supported and strongly discouraged.
  * use [leonidas] API for [OAuth 2.0] client management.
  * use [drakopolis] API and CI/CD for group management.
  * if possible, use [drako] for your authorization needs.
  * standard [OAuth 2.0] APIs are encouraged.
* **do not** design your app based on external documentation (e.g. [keycloak] and blogs).
* **do not** design your application around non standard (e.g. [OAuth 2.0]) JWT claims, except
 those provided by Agora as default.

If your application has requirements that contradicts any of the above, please
reach out to the identity team for a solution (both short and long term).


[AMA]: https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7
[Authentication]: authentication
[Authorization]: authorization
[DID]: https://www.w3.org/TR/did-core/
[drako]: ../drako-service
[drako buddy]: ../drako-service/drako_buddy
[drakopolis]: ../drako_polis-service
[keycloak]: https://www.keycloak.org/
[leonidas]: ../leonidas-service
[OAuth 2.0]: https://oauth.net/2/
[propy]: https://github.com/wp-wcm/city/tree/main/ns/id/propy
[TN-0482]: http://go/tn-0482

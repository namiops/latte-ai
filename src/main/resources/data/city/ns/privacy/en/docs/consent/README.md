# Consent Management in Agora

[ama-channel]: http://go/agora-ama
[rest-api-docs]: /catalog/default/api/consent-api-v3alpha/definition
[grpc-api-docs]: /catalog/default/api/consent-grpc-api/definition
[service-page]: /docs/default/component/agora-service-pages/consent

## Introduction

The Agora Consent Service is the central storage and decision point for user consent in Woven City. It stores the information necessary for fine-grained enforcement of user consent and provides APIs to manage and use this information.

### How to contact us {#contact-consent}

If you have any questions or requests, please reach out to the Consent team (`@agora-consent`) on the [`#wcm-org-agora-ama`][ama-channel] Slack channel.

### What is in this document?

This document gives an overview of the Agora Consent Service and its parts, it explains relevant concepts and provides technical guidance on using and integrating with the Consent Service.

### Who is this document for?

This document is aimed at WCM stakeholders who want to use the Agora Consent Service to integrate consent management with their apps and services. The overview and concepts are relevant to all roles, and the technical guidance is especially relevant to engineers who want to interface with the Consent Service.

### Where can I find the Consent Service's API? {#api-hostname-ports}

#### API Docs (interface definitions)

- REST (HTTP) API: [OpenAPI definition][rest-api-docs]
- gRPC API: [gRPC definition][grpc-api-docs]

#### Hostname

- `consent.woven-city-api.toyota` in prod (speedway) environments.
- `consent.agora-dev.w3n.io/` in Agora pre-prod (dev2) and from office network/VPN (e.g. development machine).
- For other environments, see the [service page for the Consent Service][service-page].

#### Ports and HTTPS/TLS

When accessing the Consent Service APIs from an environment that has Istio (such as pre-prod or prod), requests should be made in plaintext (no HTTPS/TLS) to ports 80 (REST) and 11863 (gRPC). In this case, Istio/Envoy will handle the secure transport.

When accessing the Consent Service APIs from an environment that _doesn't_ have Istio (from a local dev machine, for example), requests should be made with HTTPS/TLS to ports 443 (REST) and 11863 (gRPC).

Showing these combinations in a table:

|          | With Istio            | Without Istio           |
|----------|-----------------------|-------------------------|
| REST API | port `80` / no TLS    | port `443` / with TLS   |
| gRPC API | port `11863` / no TLS | port `11863` / with TLS |

## Concepts and Terminology {#concepts-terminology}

### What is Consent?

[twc-privacy-policy-en]: https://docs.google.com/document/d/1dw6eiUKSL2UgAWp70oJiRgTye2o3Sc12/edit
[twc-privacy-policy-ja]: https://docs.google.com/document/d/1mQXUycaqE5CPS3AkVA-72j2kX_tXif4P/edit

**Consent** is the principle stating that data subjects (users) have control over which services should have access to which of their personal information (personal data).
As described in the Woven City Privacy Policy ([English][twc-privacy-policy-en] / [Japanese][twc-privacy-policy-ja]), services running in the city are forbidden from accessing or collecting users' personal data without obtaining explicit permission first.

### Overview {#domain-overview}

The following diagram shows the important concepts of the Consent Service as boxes with light blue background. The black arrows and lines show how the concepts relate to each other. The colored area outlines in the background show which concepts are relevant for which subdomain for consent.

The concepts and subdomains are explained in the sections below.

![Consent Service domain model diagram](./assets/consent-domain-model.png)
<!-- Diagram source: https://lucid.app/lucidchart/f979117e-a2c0-4cfa-b71d-171a40731862/edit?invitationId=inv_792b4cae-ed58-44f8-9bd3-42e117c44179&page=MyXSGt6uM35-# -->

### Parts of the Consent Service

The **Consent Service** is a backend web service that runs on Agora and provides APIs for consent management and enforcement to other services in Woven City.

#### Subdomains of Consent {#consent-subdomains}

The domain of responsibility of the Consent Service is split into the following subdomains:

- **Consent Grant Management** (or just **Grant Management**), to record when data subjects grant or revoke consent, and to look up their current consent grants

- **Client Group Management** (or just **Group Management**), to create and delete groups, and to add and remove clients to/from groups

- **Consent Checking**, to check whether a data subject has granted the necessary consent for a client to handle specific personal data

#### APIs of the Consent Service {#consent-api-groups}

For Consent Grant Management and Client Group Management, the Consent Service provides REST API endpoints.

For Consent Checking, the Consent Service provides a REST API endpoint and a gRPC API endpoint for server-side consent checking, and a Consent Update Feed as a gRPC streaming endpoint for client-side consent checking. The server-side consent check is useful for cases where the checks happen infrequently or streaming all the consent updates to the client is otherwise undesirable. The client-side consent check is preferable when consent checks happen frequently or fine-grained data filtering is involved, reducing the latency incurred by individual checks.

There are **Consent client libraries** available for several programming languages to make it easier to use the Consent Update Feed. These libraries are developed and maintained in collaboration with engineers from Woven Inventor Garage. You can find these libraries in [this Github repository][consent-lib-repo]. If you have any questions about these libraries, please [contact us](#contact-consent).

[consent-lib-repo]: https://github.com/wp-wcm/consent-library

All API endpoints are documented in the [OpenAPI definition][rest-api-docs] (REST/HTTP API) and [gRPC definition][grpc-api-docs] (gRPC API).

### Consent Domain Concepts

#### Data Subjects (Users)

The **data subject** is the person who owns the data. Ensuring that the subject’s data is only handled according to their wishes and explicit consent is the main job of consent management.

#### Data Attributes {#data-attributes}

**Data attributes** are the categories or types of data for which data subjects can grant consent, for example `PERSON_NAME`, `EMAIL_ADDRESS`, or `CREDIT_CARD_NUMBER`.

#### Clients and Groups {#clients-and-groups}

A **client** is a piece of software that wants to handle user data, such as an app or a backend service. Each particular piece of software is considered as a separate client, identified by a unique identifier, its **client ID**. Multiple clients can make up a software product or service as seen by users and the business.

A **client group** (or just **group**) is a collection of clients that have something in common for the purposes of consent management, such as being part of the same product, participating in the same experiment, being operated by the same legal entity, etc. The Consent Service does not care about or prescribe any detailed meaning of groups beyond this.

Each client can belong to multiple groups. If a client does not belong to any groups, no consent can be granted to it.

Groups can be empty (i.e. contain no clients), for example during setup or technical changes. Groups cannot contain other groups (no nesting / hierarchy).

The entirety of the association between clients and groups (stored in the Consent Service) is called the **consent client grouping**.

#### Consent Grants and Actions {#grants-and-actions}

A **consent grant** (or just **grant**) is the record of a specific consent that the data subject granted. The consent applies to a specific data attribute, a specific action, and one (or two in some cases – discussed below) specific group(s).

Note that a grant has no “status” (such as “granted” or “revoked”). The presence of a grant record means that the data subject granted consent, and its absence means the data subject did not grant consent (or has revoked it).

The **action** specifies what the group is allowed to do with the data subject’s data. At the moment, there is no exhaustive list of possible actions. However, there is one action that is handled specially: sharing the data subject’s data with another party.

For a **share grant** (i.e. action is `SHARE`), two groups are associated with the grant: 1) the group to whom the data subject grants consent to share their data (the _“consent-for”_ group), and 2) the group with whom the data subject consent to let their data be shared (the _“shared-with”_ group).

For a **non-share grant** (i.e. action is _not_ `SHARE`, such as `USE` or `PROCESS`), one group is associated with the grant: the group to whom the data subject grants consent to perform the given action with their data (the _“consent-for”_ group).

#### Consent Checking {#consent-check-logic}

A **consent check** happens when a client wants to handle user data. For example, whenever Woven App wants to read your user name and profile picture from BURR to show it to you, BURR does a consent check to ensure you have granted the appropriate consents.

When consent is being checked, the following data is sent to the Consent Service in the request. This is also illustrated in the [domain diagram above](#domain-overview) as the "Consent Check Request" concept.

- The Woven ID of the data subject, i.e. the user whose data is being accessed.
- The ID of the client that is handling the data.  In the example above, this would be BURR.
- The action that this client wants to perform with the data. In the example above, this would be "share" (i.e. it with another client).
- In case of a "share" action, the ID of the client with whom the data would be shared. In the example above, this would be Woven App's backend.
- The list of data attributes the client wants to handle. In the example above, this would be the data attributes for user name and profile picture.

Based on the client grouping and all the user's consent grants, and the result of the consent check is decided following these rules:

- If action is _not_ "share":
  - If the data subject has granted consent for the given action and data attributes to any of the groups that the given client is a part of, then the result is "consent grant"; otherwise the result is "consent not granted".
- If action is "share":
  - If the data subject has granted consent for the given data attributes to any of the groups that the given client handling the data (the "consent-for" client) is a part of to share their data with any of the groups that the given client receiving the data (the "shared-with" client) is a part of, then the result is "consent grant"; otherwise the result is "consent not granted".
- If the either of the given client IDs is not part of any group, an error is returned.
- If the data subject is unknown or any data attribute is unknown, the consent check will result in "consent not granted."

## Where to next?

- For developers who only want to access personal data provided by another service, please refer to [this section](#access-personal-data)
- For developers who want to create personal data and manage different types of personal data, please refer to [this section](#create-personal-data)
- For developers who want to store personal data in an Agora consent-aware storage (BURR), please refer to [this section](#consent-aware-storage)
- For developers who want to store personal data in their custom storage, please refer to [this section](#custom-storage)
- For developers who want to share personal data with other services, please refer to [this section](#sharing-personal-data)

!!! note

    In many cases, the service creating personal data will also be the service storing it. If that is the case
    service developers must follow instructions in *both* sections, as they are both required.

<!-- TODO: group all the use case sections under a common h2 heading (or move to separate file?) -->

## I want to access personal data {#access-personal-data}

Before your client can be granted consent by data subjects, it must belong to at least one client group. See the section [Managing Groups and Clients](#managing-groups-and-clients) for instructions on how to do this.

Once this is done, your users can grant their consent for your client to use their data.

### Registering users' consent {#registering-consent}

[rest-api-docs-grant-management]: /catalog/default/api/consent-api-v3alpha/definition#/Consent%20Grant%20Management
[rest-api-docs-grant-consent]: /catalog/default/api/consent-api-v3alpha/definition#/Consent%20Grant%20Management/post_consents
[rest-api-docs-get-grants]: /catalog/default/api/consent-api-v3alpha/definition#/Consent%20Grant%20Management/get_consents_user
[rest-api-docs-revoke-consent]: /catalog/default/api/consent-api-v3alpha/definition#/Consent%20Grant%20Management/post_consents_user_revoke

In the future, there will be dedicated UIs for users to manage their consent and defined workflows for obtaining user consent.

Until these are available, you can directly call the relevant [Grant Management API endpoints][rest-api-docs-grant-management].

Here is an example that grants consent to the "Uber Eats" group to "USE" the data subject's `CREDIT_CARD_NUMBER` and `EMAIL_ADDRESS` ([API docs][rest-api-docs-grant-consent]). These code examples use HTTPS to run them from your dev PC, see the [API hostname and ports section](#api-hostname-ports) for details.

```shell
curl -X POST --location "https://consent.agora-dev.w3n.io/v2alpha/consents" \
    -H "Content-Type: application/json" \
    -d '{
          "data_subject_id": "12345",
          "consent_for_group_id": "Uber Eats",
          "action": "USE",
          "data_attributes": ["CREDIT_CARD_NUMBER", "EMAIL_ADDRESS"]
        }'
```

You can confirm the result by reading back the data subject's grants ([API docs][rest-api-docs-get-grants]):

```shell
curl -X GET --location "https://consent.agora-dev.w3n.io/v2alpha/consents/user/12345"
```

The response should contain the consent we just granted:

```json
{
  "grants": [
    // . . .
    {
      "action": "USE",
      "consent_for_group_id": "Uber Eats",
      "data_attributes": [ "CREDIT_CARD_NUMBER", "EMAIL_ADDRESS" ]
    },
    // . . .
  ]
}
```

And here's how you can revoke the same consent grant ([API docs][rest-api-docs-revoke-consent]):

```sh
curl -X POST --location "https://consent.agora-dev.w3n.io/v2alpha/consents/user/12345/revoke" \
    -H "Content-Type: application/json" \
    -d '{
          "consent_for_group_id": "Uber Eats",
          "action": "USE",
          "data_attributes": ["CREDIT_CARD_NUMBER", "EMAIL_ADDRESS"]
        }'
```

## I want to create personal data {#create-personal-data}

[data-mapping-sheet]: https://docs.google.com/spreadsheets/d/1UtdYCk7UoJ3-DdGQlytkcmpPPdYAv1jmQY1DYLIdIuE/edit#gid=804982504

Currently, the Consent Service does not validate the data attributes used for consent grants or consent checks, and neither do the Consent client libraries (other than ensuring that data attributes are not empty).

Consequently, for development and prototyping you can use arbitrary data attributes to fit your use case and business case.

Before proceeding into production use, there will be alignment on the allowed data attribute values, at least on a governance level in coordination with the Privacy team. There may also be stricter technical validation, but the details of this are not clear at this point.

A proposed list of data attributes that went through early alignment with Privacy team is available in [this spreadsheet][data-mapping-sheet].

If you have any questions about the data attributes or would like to request changes or addition of new data attributes, please[contact us](#contact-consent).

## I want to store personal data {#store-personal-data}

### Using BURR (Recommended) {#consent-aware-storage}

[burr-docs]: /docs/default/component/burr-v2/

As of FY2024Q1, [BURR][burr-docs] is the preferred common storage solution for personal data in Agora. BURR is currently partially integrated with the Consent service, and this integration will be expanded in the future. If you're interested in BURR and its consent integration, please [contact us](#contact-consent).

### Using custom storage {#custom-storage}

Services that want to store personal data themselves must make sure to enforce consent before storing the data and for every usage of the data, and only handle it in accordance with the data subject's consent at that time.

To check user consent for enforcement, you can use the consent checking APIs provided by the Consent Service (server-side consent check) or you can subscribe to the Consent Update Feed and use the information to check consent locally in your app (client-side consent check). Libraries are available in some programming languages to simplify the client-side consent check. For more information, please see the [Consent APIs section](#consent-api-groups).

As described in more detail in the [Consent Checking section](#consent-check-logic), your service needs to feed the following information into the consent check for storing and using personal data:

1. The _data attribute(s)_ for the data being handled, see the [Data Attributes section](#data-attributes) and in the ["create personal data" section](#create-personal-data) above.
1. The Woven ID of the data subject whose data is being handled. In many cases this will be the "currently logged-in" user, but not always.
1. The action that is to be performed on the data, for example `STORE` or `USE`, see the [Consent Grants and Actions section](#grants-and-actions).
1. The ID of the client handling the data, as registered in the Consent Client Grouping, see the [Clients and Groups section](#clients-and-groups).

If the consent check result is "not granted" or an error is returned, your service **MUST NOT** further handle that data for the indicated action, and must instead discard the data from that workflow.

For example, if your service does not have the data subject's consent to store their data, then it may not store it and must discard it instead. If it wants to use the data but does not have consent to do so, it must omit that data from the use.

If your service handles multiple data with different data attributes, the consent checks might return different results for each data attribute. In that situation, it is okay to discard only the information for which consent is not granted, and continue with the data for which it is granted, if that makes sense for the use case.

## I want to share personal data with another service {#sharing-personal-data}

Similar to the ["custom storage"](#custom-storage) section above, when sharing personal data with other
parties (even other services that you have developed), you must verify that the user has consented
to sharing their data with the receiving client.

This may include personal data sent as part of a request to another client, as a part
of a response returned to other clients, personal data on messages sent via asynchronous channels, etc.

To check user consent for enforcement, you can use the consent checking APIs provided by the Consent Service (server-side consent check) or you can subscribe to the Consent Update Feed and use the information to check consent locally in your app (client-side consent check). Libraries are available in some programming languages to simplify the client-side consent check. For more information, please see the [Consent APIs section](#consent-api-groups).

As described in more detail in the [Consent Checking section](#consent-check-logic), your service needs to feed the following information into the consent check for sharing personal data:

1. The _data attribute(s)_ for the data to be shared, see the [Data Attributes section](#data-attributes) and in the ["create personal data" section](#create-personal-data) above.
1. The Woven ID of the data subject whose data is to be shared. In many cases this will be the "currently logged-in" user, but not always.
1. The action that is to be performed on the data, in this case this is `SHARE`, see the [Consent Grants and Actions section](#grants-and-actions).
1. The ID of the client that would give out the data ("consent-for" client), as registered in the Consent Client Grouping, see the [Clients and Groups section](#clients-and-groups).
1. The ID of the client that would receive the data ("shared-with" client), as registered in the Consent Client Grouping.

If the consent check result is "not granted" or an error is returned, your service **MUST NOT** share that data with the other client.

!!! note

    Please note that enforcing consent for the party the information is being shared with implies
    that you MUST know who that party is. Due to this, sharing personal data over media that may be consumed
    by multiple unknown parties (e.g. public Kafka topics) is FORBIDDEN.

## Managing groups and clients {#managing-groups-and-clients}

[rest-api-docs-create-group]: /catalog/default/api/consent-api-v3alpha/definition#/Client%20Group%20Management/post_groups_group
[rest-api-docs-add-clients-to-group]: /catalog/default/api/consent-api-v3alpha/definition#/Client%20Group%20Management/post_groups_group_clients
[rest-api-docs-get-client-grouping]: /catalog/default/api/consent-api-v3alpha/definition#/Client%20Group%20Management/get_groups

For a client to be able to use personal data, it must have received consent from the owner of that data. To receive consent, the client must be registered in the Consent Client Grouping of the Consent Service, i.e. it must belong to at least one group.

You can use the Group Management API endpoints to list the existing client grouping, create a group if necessary, and add your client to the group.

For example, to create a group and add a client to it, you can use the following API calls (using HTTPS to run from your dev PC, see the [API hostname and ports section](#api-hostname-ports) for details).

Create the group with the ID `Coffee-Consortium` ([API docs][rest-api-docs-create-group]):

```sh
curl -X POST --location "https://consent.agora-dev.w3n.io/v3alpha/admin/groups/Coffee-Consortium"
```

Then add the client with the ID `coffee-recommender-backend` to it ([API docs][rest-api-docs-add-clients-to-group]):

```sh
curl -X POST --location "https://consent.agora-dev.w3n.io/v3alpha/admin/groups/Coffee-Consortium/clients?client_ids=coffee-recommender-backend"
```

You can confirm your updates by retrieving the whole client grouping with ([API docs][rest-api-docs-get-client-grouping]):

```sh
curl -X GET --location "https://consent.agora-dev.w3n.io/v3alpha/admin/groups"
```

The response should contain your group and client:

```json
{
  "groups": [
    // . . .
    { "group_id": "Coffee-Consortium" },
    // . . .
  ],
  "associations": [
    // . . .
    {
      "group_id": "Coffee-Consortium",
      "client_id": "coffee-recommender-backend"
    },
    // . . .
  ]
}
```

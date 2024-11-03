<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Woven ID](#woven-id)
  - [Monitoring](#monitoring)
  - [How to get User Information](#how-to-get-user-information)
  - [How to get Keycloak Client Id](#how-to-get-keycloak-client-id)
  - [FAQs](#faqs)
    - [Are Woven IDs required to access Agora Services?](#are-woven-ids-required-to-access-agora-services)
    - [Can I get or create a Woven ID?](#can-i-get-or-create-a-woven-id)
    - [Will Visitors Need a Woven ID?](#will-visitors-need-a-woven-id)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Woven ID

| Last Update | 2024-04-25              |
|-------------|-------------------------|
| Tags        | IAM, Keycloak, Identity |

## Monitoring

* [Drako Service Page](../../../ns/service-page/docs/id/drako/README.md)
* [Keycloak Service Page](../../../ns/service-page/docs/id/keycloak/README.md)
* [Identity Postgres Service Page](../../../ns/service-page/docs/id/postgresql/README.md)

## How to get User Information

For users who need to have a bearer token or other parts of their Woven ID for
making requests to Agora, there is an endpoint that can be access for headers
per the user.

The endpoint is at https://id-test-drako-v1.agora-dev.w3n.io/headers

The JSON response will look like this. Some key fields

* `x-user-name` - the username
* `x-user-id` - the user UUID
* `x-auth-request-access-token` the bearer token

```json
{
  "host": {
    "hostname": "id-test-drako-v1.agora-dev.w3n.io",
    "ip": "::6",
    "ips": []
  },
  "http": {
    "method": "GET",
    "baseUrl": "",
    "originalUrl": "/headers",
    "protocol": "http"
  },
  "request": {
    "params": {
      "0": "/headers"
    },
    "query": {
    },
    "cookies": {
      "CITYCOOKIE":
      //OMMITED
    },
    "body": {
    },
    "headers": {
      "host": "id-test-drako-v1.agora-dev.w3n.io",
      //OMITTED
      "x-auth-request-acccess-token": "Bearer TOKEN",
      "x-user-id": "8475ecd0-4244-4dfc-b17d-718d6db226e4",
      "x-user-name": "USER NAME",
      "x-email": "USER EMAIL"
      //OMITTED
    }
  },
  "environment": {
    //OMITTED
  }
}
```

## How to get Keycloak Client Id

Perform the following action to get the client id per the
deployed `KeycloakClient`:

```shell
 k -n <namespace> get secret keycloak-client-secret-<namespace> --template={{.data.CLIENT_ID}} | base64 -D
```

## FAQs

### Are Woven IDs required to access Agora Services?

Yes, **all traffic into Agora must authenticate itself**. Users will use Woven
IDs to authenticate with Agora.

### Can I get or create a Woven ID?

Yes, you can. Woven ID is exposed to the internet and everyone can register for
an account.

### Will Visitors Need a Woven ID?

Yes, Visitors will register ahead of time and have Woven IDs. If you need
further information look to the
[City Ops Onboarding Journey](https://docs.google.com/presentation/d/1XyGFxAODQzGSc4I6Gzqz4OL2MJbkF_9Q1JzNRsNTc3M/edit#slide=id.g269ecf56fff_0_677)

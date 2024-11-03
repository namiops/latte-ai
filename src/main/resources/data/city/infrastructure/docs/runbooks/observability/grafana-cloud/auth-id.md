# Grafana Cloud authentication with ID

## TL;DR
Access to Grafana is by Keycloak groups. These are currently managed manually by infra.
~~Just add people to the appropriate group in [Keycloak](https://id.agora-dev.w3n.io) (see table below).~~
We changed the default group to `editor` so every user has at least editor
access by default.
See [Monday #7311874557](https://wovencity.monday.com/boards/5710223440/pulses/7311874557) for details.

## Admin

### Managing groups in keycloak

Currently there are 2 groups in `Keycloak` that we (`agora-infra`) manage manually.

  - [agora-infra](https://id.agora-dev.w3n.io/auth/admin/woven/console/#/woven/groups/5348f0b8-e09b-4c7c-ba92-c87513c0280c)
  - [agora-observability-editor](https://id.agora-dev.w3n.io/auth/admin/woven/console/#/woven/groups/841c55d7-18bc-4e53-880c-58568fbd399b) (obsolete)

The mapping between `Keycloak` groups and `Grafana` groups as below

| Keycloak Group               | Grafana Group  | Comments                                                                                |
|        :---:                 |     :---:      | :---                                                                                    |
| [agora-infra]                | admin          | Members of this group can manage our groups in `Keycloak`. Should only be Infra members |
|                              | editor         | Editor is the default group everybody gets.                                             |

## Setup

Here the steps for setting up `Keycloak` authentication.

### Keycloak client
Setup a `KeycloakClient` in the cluster.
See [this example](../../../../k8s/environments/dev2/clusters/worker1-east/agora-observability-dev/auth/keycloakclient-grafana-cloud-speedway-dev.yaml).
Pay attention to the `defaultClientScopes`, `Uris` and `clientId`.

### Configuration on Grafana Cloud

Configure `Generic OAuth` as follows:

  - Display name: `Woven by Toyota`
  - ClientId: _<clientId> as configured in your `KeycloakClient` above_
  - Client secret: "" _empty, because we use a public client_
  - Auth style: `AutoDetect`
  - Scopes: `email`, `offline_access`, `profile`, `roles`, `openid`
  - AuthURL: `https://<ID_URI>/auth/realms/woven/protocol/openid-connect/auth`
  - Token URL: `https://<ID_URI>/auth/realms/woven/protocol/openid-connect/token`
  - API URL: `https://<ID_URI>/auth/realms/woven/protocol/openid-connect/userinfo`
  - Allow sign-up `enabled`
  - Sign out redirect URL: `<stack home URL>`
  - User mapping
    - keep everything empty except the following:
    - Email attribute name: `email:primary`
    - Role attribute path:
      - old with `viewer` default:
        ```
        contains(groups[*], '/agora-infra') && 'Admin' || contains(groups[*], '/agora-observability-editor') && 'Editor' || 'Viewer'
        ```
      - new with `editor` default:
        ```
        contains(groups[*], '/agora-infra') || 'Editor'
        ```
  - Extra security measures
    - set nothing here

For reference:
 - ID\_URI for LAB2: https://id.agora-dev.w3n.io

<!-- Below are the links used in the document -->
[agora-observability-editor]: https://id.agora-dev.w3n.io/auth/admin/woven/console/#/woven/groups/841c55d7-18bc-4e53-880c-58568fbd399b
[agora-infra]: https://id.agora-dev.w3n.io/auth/admin/woven/console/#/woven/groups/5348f0b8-e09b-4c7c-ba92-c87513c0280c

# Propy - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

Propy is Agora's LDAP gateway to authenticate devices in the Woven City network.


</td>
<td markdown="block" width="50%">

## Contact

- E-mail: wcm-agora-identity@woven-planet.global
- Slack channel: [#wcm-org-agora-auth](https://woven-by-toyota.slack.com/archives/C032Z73091N)
- Slack mention: `@agora-identity`
- Calendar: On-Call (🚧TODO🚧)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Endpoints
- speedway dev: `ldaps://dev-propy.woven-city-api.toyota:636`
- speedway prod: `ldaps://propy.woven-city-api.toyota:636`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [CISCO ISE](../../CISCO ISE/)
- Downstream (this service calls):
  - [ServiceNow](../../ServiceNow/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [propy](https://github.com/wp-wcm/city/tree/main/ns/id/propy): application code

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/common),
  [local](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/local),
  [dev](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/dev),
  [prod](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/prod)
- Docker images:
  [propy](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/propy/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)
- Commit history:
  [propy](https://github.com/wp-wcm/city/commits/main/ns/id/propy),
  [common](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/common),
  [local](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/local),
  [dev](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/dev),
  [prod](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/prod)

</td>
<td markdown="block">

## Testing

- Smoke Tests:
  dev (🚧TODO🚧)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)
- Service Ingress Metrics:
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)
- Service Egress Metrics:
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)
- Resource Metrics:
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)
- Logs:
  [dev](https://wcmagoradev.grafana.net/goto/egeP8X3IR?orgId=1),
  [prod](https://wcmagoraprod.grafana.net/goto/httVQX3Ig?orgId=1)
- Logs (Security):
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)
- Traces:
  dev (🚧TODO🚧),
  prod (🚧TODO🚧)

</td>
<td markdown="block">

## Documentation

- Architecture:
  document (🚧TODO🚧)
- Operations:
  [runbooks](http://go/agora-id-propy-runbooks),
  [CMs](http://go/agora-id-propy-cms),
  [postmortems](http://go/agora-id-propy-pms)
- Reviews:
  [security (go-live)](http://go/agora-id-golive-review),
  [security (Propy)](https://virgil-web-frontend.cityos-dev.woven-planet.tech/projects/60)

</td>
</tr>
</table>

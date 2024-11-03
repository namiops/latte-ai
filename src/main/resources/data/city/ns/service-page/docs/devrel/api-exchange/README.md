# API Exchange - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

API Exchange offers a central place for publish API Specifications for teams in WCM


</td>
<td markdown="block" width="50%">

## Contact

- E-mail: wcm-agora-devrel@woven-planet.global
- Slack channel: [#wcm-org-agora-ama](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7)
- Slack mention: `@agora-devrel`

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Endpoints
- API Exchange (Pre-prod): `https://api-exchange.agora-dev.w3n.io`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - none
- Downstream (this service calls):
  - none

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [api-exchange](https://github.com/wp-wcm/city/tree/main/ns/api-exchange): application code

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/api-exchange),
  [Pre-prod (Management - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/api-exchange),
  [Pre-prod (Worker - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/api-exchange)
- Docker images:
  [api-exchange](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/api-exchange)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  TODO (🚧TODO🚧)
- Commit history:
  [developer](https://github.com/wp-wcm/city/commits/main/ns/api-exchange),
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/api-exchange),
  [Pre-prod (Management - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/api-exchange),
  [Pre-prod (Worker - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/api-exchange)

</td>
<td markdown="block">

## Testing


</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  [all environments](https://toyotaglobal.enterprise.slack.com/archives/C04PJ6FSFLL)
- Service Dashboard:
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/LhzSmwTIk/devrel-overview?orgId=1)
- Traces - TODO when Tempo traces is available in Pre-prod:
  dev2 (🚧TODO🚧)

</td>
<td markdown="block">

## Documentation

- Overview:
  [Operational Slides](https://docs.google.com/presentation/d/1dK5ztezuiuUqm5jrdAgr8S2ACAOVENDENj7z2QNhNFM/edit#slide=id.g22cfa734e09_0_19)
- Architecture Diagrams:
  [C4 Diagrams](https://lucid.app/lucidchart/b40a4302-5d47-4d4c-ae21-a961849cf1aa/edit?viewport_loc=-11%2C-11%2C2397%2C1541%2C0_0&invitationId=inv_aae715e2-e600-407b-9e37-cc125e7b2870)
- Internal:
  [Backstage Documentation](https://github.com/wp-wcm/city/tree/main/ns/api-exchange/README.md)

</td>
</tr>
</table>

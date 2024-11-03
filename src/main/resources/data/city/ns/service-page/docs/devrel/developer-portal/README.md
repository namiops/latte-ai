# Developer Portal - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

Developer Portal hosts documentation and services for streamlining the process of onboarding to Agora


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
- Developer Portal (Lab2): `https://developer-portal.agora-lab.w3n.io`
- Developer Portal (Pre-prod): `https://developer-portal.agora-dev.w3n.io`
- Developer Portal (Speedway): `https://developer.woven-city.toyota`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [Github](../../Github/)
  - [Github Enterprise](../../Github Enterprise/)
- Downstream (this service calls):
  - none

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [developer](https://github.com/wp-wcm/city/tree/main/ns/developer): application code

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/developer-portal),
  [local](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/developer-portal),
  [lab (DEPRECATED)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/developer-portal),
  [dev](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/developer-portal),
  [Lab 2 (Management - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/developer-portal),
  [Lab 2 (Worker - East)](https://github.com/wp-wcm/city/tree/maininfrastructure/k8s/environments/lab2/clusters/worker1-east/developer-portal),
  [Pre-prod (Management - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/developer-portal),
  [Pre-prod (Worker - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/developer-portal)
- Docker images:
  [developer-portal](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/developer-portal)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  TODO (🚧TODO🚧)
- Commit history:
  [developer](https://github.com/wp-wcm/city/commits/main/ns/developer),
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/developer-portal),
  [local](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/developer-portal),
  [lab (DEPRECATED)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/developer-portal),
  [dev](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/developer-portal),
  [Lab 2 (Management - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/developer-portal),
  [Lab 2 (Worker - East)](https://github.com/wp-wcm/city/tree/maininfrastructure/k8s/environments/lab2/clusters/worker1-east/developer-portal),
  [Pre-prod (Management - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/developer-portal),
  [Pre-prod (Worker - East)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/developer-portal),
  [Terraform](https://github.com/wp-wcm/city/tree/main/infrastructure/terraform/modules/agora_vault_agoradevrel)

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
  [C4 Diagrams](https://lucid.app/lucidchart/494a3b1c-6b4a-440d-b249-dc4c1096395b/edit?viewport_loc=-1962%2C-1006%2C3563%2C2291%2CLPMrk_Gt-tws&invitationId=inv_24c5bc25-208d-4f8b-9878-0e45e47b3517)
- Internal:
  [Backstage Documentation](https://github.com/wp-wcm/city/tree/main/ns/developer/backstage/README.md)

</td>
</tr>
</table>

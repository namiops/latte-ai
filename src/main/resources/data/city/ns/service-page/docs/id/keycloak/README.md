# Keycloak - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

Keycloak provides Identity and Access Management services.


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
- lab: `https://id.agora-lab.woven-planet.tech`
- dev: `https://id.cityos-dev.woven-planet.tech`
- lab2: `https://id.agora-lab.w3n.io`
- dev2: `https://id.agora-dev.w3n.io`
- speedway-dev: `https://dev-id.woven-city.toyota`
- speedway-prod: `https://id.woven-city.toyota`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [id/drako](../drako/)
  - [id/security-token-service-v2](../security-token-service-v2/)
- Downstream (this service calls):
  - [id/postgresql](../postgresql/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [keycloak-image](https://github.com/wp-wcm/city/tree/main/ns/id/keycloak-image): custom Keycloak image builder
- [keycloak-spi](https://github.com/wp-wcm/city/tree/main/ns/id/keycloak-spi): Extend base Keycloak with additional Service Provider Interfaces (SPI)
- [keycloak-theme-woven](https://github.com/wp-wcm/city/tree/main/ns/id/keycloak-theme-woven): Custom visual theme for Keycloak

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/id),
  [local](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/id),
  [lab](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/id),
  [dev](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/id),
  [lab2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/id),
  [dev2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/id)
- Docker images:
  [keycloak v18](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak/),
  [keycloak v19](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak-19/),
  [keycloak v20](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak-20/),
  [keycloak v21](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak-21/),
  [keycloak v22](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak-22/),
  [keycloak v24](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak-24/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/0827280ce76e9779260cbb3ac8564d1fed87d0fa),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/0827280ce76e9779260cbb3ac8564d1fed87d0fa),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/fedae991-7dc2-4063-925a-d96e9fd8664f)
- Commit history:
  [keycloak-image](https://github.com/wp-wcm/city/commits/main/ns/id/keycloak-image),
  [keycloak-spi](https://github.com/wp-wcm/city/commits/main/ns/id/keycloak-spi),
  [keycloak-theme-woven](https://github.com/wp-wcm/city/commits/main/ns/id/keycloak-theme-woven),
  [common](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/id),
  [local](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/local/id),
  [lab](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/lab/id),
  [dev](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/dev/id),
  [lab2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/id),
  [dev2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/id)

</td>
<td markdown="block">

## Testing

- Smoke Tests:
  lab (🚧TODO🚧),
  dev (🚧TODO🚧),
  lab2 (🚧TODO🚧),
  dev2 (🚧TODO🚧)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  lab (🚧TODO🚧),
  [dev](http://go/agora-id-keycloak-alerts-dev),
  lab2 (🚧TODO🚧),
  [dev2](https://athena.agora-dev.w3n.io/grafana/alerting/list?queryString=%7Bnamespace%3D%22id%22,%20group%3D~%22keycloak.%2B%22%7D)
- Authentication Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/3160074b74052a2edd1ea99dc337eb9b13ad8c24),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/3160074b74052a2edd1ea99dc337eb9b13ad8c24),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/24fbcdbf-5f1b-42d1-bbbe-2af5ea031086),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/24fbcdbf-5f1b-42d1-bbbe-2af5ea031086)
- Service Ingress Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/be9aafc10451b86e78b934d480cf20bb0d5ff3f1),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/be9aafc10451b86e78b934d480cf20bb0d5ff3f1),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/c63c39ef-1c3b-482a-bc03-571342b67ef9),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/c63c39ef-1c3b-482a-bc03-571342b67ef9)
- Service Egress Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/b4e51eb6918534086d7cc706d1bef207f4d37404),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/b4e51eb6918534086d7cc706d1bef207f4d37404),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/8a23225d-5525-4761-8ef6-37d5f60b9b84),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/8a23225d-5525-4761-8ef6-37d5f60b9b84)
- Resource Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/96f419399c629b608e391427bfa00c72bce5d824),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/96f419399c629b608e391427bfa00c72bce5d824),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/4fbca40f-9d41-459b-9ea6-beb7964ec698),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/4fbca40f-9d41-459b-9ea6-beb7964ec698)
- Logs:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/113f5fa2ccb1fcdbdea990cac62f2b079ae3cb6c),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/113f5fa2ccb1fcdbdea990cac62f2b079ae3cb6c),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/554c2a17-74e7-480a-b2f9-0fd6f1f5a970),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/554c2a17-74e7-480a-b2f9-0fd6f1f5a970)
- Traces:
  [lab (all)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=keycloak.id&lookback=2d),
  [dev (all)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=keycloak.id&lookback=2d),
  [lab (errors)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=keycloak.id&lookback=2d&tags=%7B%22error%22%3A%22true%22%7D),
  [dev (errors)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=keycloak.id&lookback=2d&tags=%7B%22error%22%3A%22true%22%7D),
  [lab (slow)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=keycloak.id&lookback=2d&minDuration=100ms),
  [dev (slow)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=keycloak.id&lookback=2d&minDuration=100ms)

</td>
<td markdown="block">

## Documentation

- Architecture:
  [official document](http://go/agora-id-keycloak-arch)
- Operations:
  [runbooks](http://go/agora-id-keycloak-runbooks),
  [CMs](http://go/agora-id-keycloak-cms),
  [postmortems](http://go/agora-id-keycloak-pms)
- Reviews:
  [security (go-live)](http://go/agora-id-golive-review),
  [security (JWKS)](http://go/agora-id-jwks-sec-review)

</td>
</tr>
</table>

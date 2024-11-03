# BURR Core v2 - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

The Basic Users and Residents Register (BURR) (formerly BRR = Basic Residents Register) is an API for Woven inventor and operator services on the Agora platform to utilize personal information about Woven City users and residents.


</td>
<td markdown="block" width="50%">

## Contact

- E-mail: wcm-agora-data@woven-planet.global
- Slack channel: [#wcm-org-agora-burr](https://toyotaglobal.enterprise.slack.com/archives/C03QDEEEKU1)
- Slack mention: `@agora-burr`

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Endpoints
- lab: `http://brr.agora-lab.woven-planet.tech/core/v2alpha`
- dev: `http://brr.cityos-dev.woven-planet.tech/core/v2alpha`
- lab2: `http://burr.agora-lab.w3n.io/core/v2alpha`
- dev2: `http://burr.agora-dev.w3n.io/core/v2alpha`
- speedway dev: `https://dev-burr.woven-city-api.toyota/core/v2alpha`
- speedway prod: `https://burr.woven-city-api.toyota/core/v2alpha`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - none
- Downstream (this service calls):
  - [consent](../consent/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [burr v2](https://github.com/wp-wcm/city/tree/main/ns/burr): Source code for service and related components for BURR v2 services
- [burr v2 api spec](https://github.com/wp-wcm/city/tree/main/ns/burr/api): OpenAPI files for BURR v2 API
- [burr](https://github.com/wp-wcm/city/tree/main/ns/brr): Source code for service and related components for BURR services
- [burr api spec](https://github.com/wp-wcm/city/tree/main/ns/brr/api): OpenAPI files for BURR API

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/brr),
  [lab (`brr`)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/brr),
  [dev (`brr`)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/brr),
  [dev (`brr-b`)](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/brr-b),
  [lab2/mgmt-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/burr),
  [lab2/worker1-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/burr),
  [lab2/worker1-west](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-west/burr),
  [dev2/mgmt-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/burr),
  [dev2/worker1-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/burr),
  [dev2/worker1-west](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-west/burr),
  [speedway/dev](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-burr/speedway/dev)
- Docker images:
  [burr/core-v2 (service)](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/burr/core-v2),
  [burr/city-address-importer (job)](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/burr/city-address-importer),
  [burr/happy-path-test-v2-executor (testkube)](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/burr/happy-path-test-v2-executor)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  none
- Commit history:
  [burr](https://github.com/wp-wcm/city/commits/main/ns/burr),
  [api spec](https://github.com/wp-wcm/city/commits/main/ns/brr/api),
  [common](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/brr),
  [lab (`brr`)](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/lab/brr),
  [dev (`brr`)](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/dev/brr),
  [dev (`brr-b`)](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/dev/brr-b),
  [lab2/mgmt-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/burr),
  [lab2/worker1-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/burr),
  [dev2/mgmt-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/burr),
  [dev2/worker1-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/burr)

</td>
<td markdown="block">

## Testing

- Happy Path Tests:
  [lab](https://testkube.agora-lab.woven-planet.tech/tests/burr-happy-path-test-v2),
  [dev](https://testkube.cityos-dev.woven-planet.tech/tests/burr-happy-path-test-v2),
  [lab2](https://testkube.agora-lab.w3n.io/tests/burr-happy-path-test-v2),
  [dev2](https://testkube.agora-dev.w3n.io/tests/burr-happy-path-test-v2)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  [lab + lab2](https://toyotaglobal.enterprise.slack.com/archives/C0676R66K6V),
  [dev](https://toyotaglobal.enterprise.slack.com/archives/C04QGLPLLQN)
- Service Dashboard:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/860d7c9afc5846359c39b7961a611b4362832058/burr-core-v2-service-dashboard),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/860d7c9afc5846359c39b7961a611b4362832058/burr-core-v2-service-dashboard),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/4f8e06c7-9dd6-44c3-adff-95d76c62c32d/burr-core-v2-service-dashboard),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/4f8e06c7-9dd6-44c3-adff-95d76c62c32d/burr-core-v2-service-dashboard)
- Logs:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/860d7c9afc5846359c39b7961a611b4362832058/burr-core-v2-service-dashboard),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/860d7c9afc5846359c39b7961a611b4362832058/burr-core-v2-service-dashboard),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/4f8e06c7-9dd6-44c3-adff-95d76c62c32d/burr-core-v2-service-dashboard),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/4f8e06c7-9dd6-44c3-adff-95d76c62c32d/burr-core-v2-service-dashboard)
- Traces:
  [lab (all)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=core-v2.brr&lookback=2d),
  [dev (all)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=core-v2.brr&lookback=2d)

</td>
<td markdown="block">

## Documentation

- Overview:
  [Atlas](http://go/consent-burr-atlas)
- API reference:
  [Core v2 API](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition)
- External:
  [BURR v2 conceptual documentation](https://developer.woven-city.toyota/docs/default/Component/burr-v2),
  [BURR conceptual documentation](https://developer.woven-city.toyota/docs/default/Component/brr-service)
- Internal:
  [Dev README (core v2)](https://github.com/wp-wcm/city/tree/main/ns/burr/core-v2/README.md),
  [Dev README (v1)](https://github.com/wp-wcm/city/tree/main/ns/brr/README.md)

</td>
</tr>
</table>

# Consent Service - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

The Consent Service implements consent management and enforcement in Agora.


</td>
<td markdown="block" width="50%">

## Contact

- E-mail: wcm-agora-data@woven-planet.global
- Slack channel: [#wcm-org-agora-ama](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7)
- Slack mention: `@agora-consent`

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Endpoints
- lab (grpc): `grpc://consent.consent.svc.cluster.local:9001`
- lab (http): `http://consent.consent.svc.cluster.local`
- dev (grpc): `grpc://consent.consent.svc.cluster.local:9001`
- dev (http): `http://consent.consent.svc.cluster.local`
- lab2 (grpc): `grpc://consent.agora-lab.w3n.io:11863`
- lab2 (http): `https://consent.agora-lab.w3n.io`
- dev2 (grpc): `grpc://consent.agora-dev.w3n.io:11863`
- dev2 (http): `https://consent.agora-dev.w3n.io`
- speedway/dev (grpc): `grpc://consent.woven-city-api.toyota:11863`
- speedway/dev (http): `https://consent.woven-city-api.toyota`
- speedway/prod (grpc): `grpc://consent.woven-city-api.toyota:11863`
- speedway/prod (http): `https://consent.woven-city-api.toyota`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [burr](../burr/)
- Downstream (this service calls):
  - none

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [consent](https://github.com/wp-wcm/city/tree/main/ns/privacy/consent): application code

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/consent),
  [local](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/consent),
  [lab](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/consent),
  [dev](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/consent),
  [lab2/mgmt-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/consent),
  [lab2/worker1-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/consent),
  [dev2/mgmt-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/consent),
  [dev2/worker1-east](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/consent)
- Docker images:
  [privacy/consent](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/privacy/consent)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  TODO (🚧TODO🚧)
- Commit history:
  [consent](https://github.com/wp-wcm/city/commits/main/ns/privacy/consent),
  [common](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/consent),
  [local](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/local/consent),
  [lab](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/lab/consent),
  [dev](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/dev/consent),
  [lab2/mgmt-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/consent),
  [lab2/worker1-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/consent),
  [dev2/mgmt-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/consent),
  [dev2/worker1-east](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/consent)

</td>
<td markdown="block">

## Testing

- Smoke Tests (deployment-triggered):
  [lab](https://testkube.agora-lab.woven-planet.tech/tests/consent-smoke-test),
  [dev](https://testkube.cityos-dev.woven-planet.tech/tests/consent-smoke-test),
  [lab2](https://testkube.agora-lab.w3n.io/tests/consent-smoke-test),
  [dev2](https://testkube.agora-dev.w3n.io/tests/consent-smoke-test)
- Smoke Tests (scheduled):
  [lab](https://testkube.agora-lab.woven-planet.tech/tests/consent-smoke-test-scheduled),
  [dev](https://testkube.cityos-dev.woven-planet.tech/tests/consent-smoke-test-scheduled),
  [lab2](https://testkube.agora-lab.w3n.io/tests/consent-smoke-test-scheduled),
  [dev2](https://testkube.agora-dev.w3n.io/tests/consent-smoke-test-scheduled)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  [lab + lab2](https://toyotaglobal.enterprise.slack.com/archives/C066MH8F1K8),
  [dev + dev2](https://toyotaglobal.enterprise.slack.com/archives/C057Y7GKPCY)
- Service Dashboard:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/e1eb3f37e17e8e2992e75d489b912c64605240e6),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/e1eb3f37e17e8e2992e75d489b912c64605240e6),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/77fe3b61-1ea2-4c47-b322-6db0501b3ce6/consent-service-dashboard),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/77fe3b61-1ea2-4c47-b322-6db0501b3ce6/consent-service-dashboard)
- Traces:
  [lab (all)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=consent.consent&lookback=2d),
  [dev (all)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=consent.consent&lookback=2d)

</td>
<td markdown="block">

## Documentation

- Overview:
  [Atlas](http://go/consent-burr-atlas)
- API reference:
  [HTTP API](https://developer.woven-city.toyota/catalog/default/api/consent-api-v3alpha/definition),
  [gRPC API](https://developer.woven-city.toyota/catalog/default/api/consent-grpc-api/definition)
- External:
  [Consent management](https://developer.woven-city.toyota/docs/default/component/consent-management-service/en/consent/),
  [Consent 101 codelab](https://developer.woven-city.toyota/docs/default/component/consent-management-service/en/consent_101/00_README/)
- Internal:
  [README](https://github.com/wp-wcm/city/tree/main/ns/privacy/consent/README.md)

</td>
</tr>
</table>

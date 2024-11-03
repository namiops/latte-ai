# DrakoPolis - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

DrakoPolis is a RESTFul API over HTTP for managing Drako Groups.


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
- lab (internal): `http://drako-polis-v1.id.svc.cluster.local:8080`
- dev (internal): `http://drako-polis-v1.id.svc.cluster.local:8080`
- lab2 (internal): `http://drako-polis-v1.id.svc.cluster.local:8080`
- lab2 (external): `https://drakopolis.agora-lab.w3n.io`
- dev2 (internal): `http://drako-polis-v1.id.svc.cluster.local:8080`
- dev2 (external): `https://drakopolis.agora-dev.w3n.io`
- dev2 (stable): `https://drakopolis.woven-city-api.toyota`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [istio-system/istiod](../../istio-system/istiod/)
- Downstream (this service calls):
  - [default/kubernetes](../../default/kubernetes/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [drako_polis](https://github.com/wp-wcm/city/tree/main/ns/id/drako_polis): application code
- [drako_data](https://github.com/wp-wcm/city/tree/main/ns/id/drako_data): library for sharing data types including Kubernetes CRDs

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/id),
  [local](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/id),
  [lab](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/id),
  [dev](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/id),
  [lab2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/id/drako-polis),
  [dev2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/id/drako-polis),
  [speedway/local](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/local/3-drako-polis),
  [speedway/dev](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/dev/3-drako-polis),
  [speedway/prod](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/prod/3-drako-polis)
- Docker images:
  [drako_polis](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/drako_polis/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  lab2 (🚧TODO🚧),
  dev2 (🚧TODO🚧),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Commit history:
  [drako_polis](https://github.com/wp-wcm/city/commits/main/ns/id/drako_polis),
  [common](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/id),
  [local](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/local/id),
  [lab](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/lab/id),
  [dev](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/dev/id),
  [lab2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/id),
  [dev2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/id),
  [speedway/local](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/local/3-drako-polis),
  [speedway/dev](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/dev/3-drako-polis),
  [speedway/prod](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-id/speedway/prod/3-drako-polis)

</td>
<td markdown="block">

## Testing

- Integration Tests:
  lab2 (🚧TODO🚧),
  dev2 (🚧TODO🚧),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  lab2 (🚧TODO🚧),
  dev2 (🚧TODO🚧),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Service Ingress Metrics:
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/c3b30a9c-c28c-4a28-b8b7-f81fb5956fc6),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/99102061-698f-4b8f-8bca-ce0986f04c3c),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Service Egress Metrics:
  lab2 (🚧TODO🚧),
  dev2 (🚧TODO🚧),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Resource Metrics:
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/c70be175-26cc-4aa7-ad0d-5a7c52e84633),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/b86a133a-1077-42fe-a2d9-10bfb113600f),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Deployments:
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/5be71988-771e-40f1-9ca2-47750353feb8),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/0950fb02-ee90-4c93-93ce-b557a6efcde3),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Logs:
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/72a937bf-1aef-4f6f-b3d0-2b027c5eb5fb),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/80edb29a-ab3b-4e96-a1fb-0836a47cb897),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)
- Traces:
  lab2 (🚧TODO🚧),
  dev2 (🚧TODO🚧),
  speedway/dev (🚧TODO🚧),
  speedway/prod (🚧TODO🚧)

</td>
<td markdown="block">

## Documentation

- Agora Documentation:
  [Drakopolis](https://developer.woven-city.toyota/docs/default/Component/drako_polis-service),
  [REST API Definition](https://developer.woven-city.toyota/catalog/default/api/drako-polis-api-v1alpha/definition)
- Operations:
  [runbooks](http://go/agora-id-drakopolis-runbooks),
  [CMs](http://go/agora-id-drakopolis-cms),
  [postmortems](http://go/agora-id-drakopolis-pms)

</td>
</tr>
</table>

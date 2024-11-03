# Drako - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

Drako is Agora's Policy Decision Point system. It is defined in
[Istio](https://istio.io) as an [external authorizer](http://go/tn-0167).


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
- lab: `grpc://drako-v1.id.svc.cluster.local:9001`
- dev: `grpc://drako-v1.id.svc.cluster.local:9001`
- lab2: `grpc://drako-v1.id.svc.cluster.local:9001`
- dev2: `grpc://drako-v1.id.svc.cluster.local:9001`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [istio-system/istiod](../../istio-system/istiod/)
- Downstream (this service calls):
  - [id/keycloak](../keycloak/)
  - [secure-kvs/couchdb-operator](../../secure-kvs/couchdb-operator/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- [drako](https://github.com/wp-wcm/city/tree/main/ns/id/drako): application code
- [drako_data](https://github.com/wp-wcm/city/tree/main/ns/id/drako_data): library for sharing data types including Kubernetes CRDs

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
  [drako](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/drako/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/23436b6615725a7e664e1f4d9dc8abf05713eb97),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/23436b6615725a7e664e1f4d9dc8abf05713eb97),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/44215f9c-100e-4daa-8b05-40f05cf5b88b),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/44215f9c-100e-4daa-8b05-40f05cf5b88b)
- Commit history:
  [drako](https://github.com/wp-wcm/city/commits/main/ns/id/drako),
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
  [lab](https://testkube.agora-lab.woven-planet.tech/tests/id-test-drako-v1-smoke),
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
  [dev](http://go/agora-id-drako-alerts-dev),
  lab2 (🚧TODO🚧),
  [dev2](https://athena.agora-dev.w3n.io/grafana/alerting/list?queryString=%7Bnamespace%3D%22id%22,%20group%3D~%22drako.%2B%22%7D)
- Service Ingress Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/2eabb47d54dd8c41aab8702946f17941c454a761),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/2eabb47d54dd8c41aab8702946f17941c454a761),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/39dca738-1e7a-4dd0-8eca-5db6bade2801),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/39dca738-1e7a-4dd0-8eca-5db6bade2801)
- Service Egress Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/3f1defdfa4889b640f005d96f4c26a9620856e9e),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/3f1defdfa4889b640f005d96f4c26a9620856e9e),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/f4488119-233d-4a93-8588-8732181b9607),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/f4488119-233d-4a93-8588-8732181b9607)
- Resource Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/14f16a3dfe1f2617932bc968fad4644c4fadd3a8),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/14f16a3dfe1f2617932bc968fad4644c4fadd3a8),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/d515f304-180a-4c39-b1aa-555f78077e1a),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/d515f304-180a-4c39-b1aa-555f78077e1a)
- Logs:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/73f0b78a2382a9fdede24dffe528e77e017cf25b),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/73f0b78a2382a9fdede24dffe528e77e017cf25b),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/3e51237d-0915-4a82-8324-7cd233d4585f),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/3e51237d-0915-4a82-8324-7cd233d4585f)
- Logs (Security):
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/df42381ddccafa48280d8c8a86295b8fee339cdc),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/df42381ddccafa48280d8c8a86295b8fee339cdc),
  [lab2](https://athena.agora-dev.w3n.io/grafana/d/3e51237d-0915-4a82-8324-7cd233d4585f),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/3e51237d-0915-4a82-8324-7cd233d4585f)
- Traces:
  [lab (all)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=drako-traces.id&lookback=2d),
  [dev (all)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=drako-traces.id&lookback=2d),
  [lab (errors)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=drako-traces.id&lookback=2d&tags=%7B%22error%22%3A%22true%22%7D),
  [dev (errors)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=drako-traces.id&lookback=2d&tags=%7B%22error%22%3A%22true%22%7D),
  [lab (slow)](https://observability.agora-lab.woven-planet.tech/jaeger/search?service=drako-traces.id&lookback=2d&minDuration=100ms),
  [dev (slow)](https://observability.cityos-dev.woven-planet.tech/jaeger/search?service=drako-traces.id&lookback=2d&minDuration=100ms)

</td>
<td markdown="block">

## Documentation

- Architecture:
  document (🚧TODO🚧)
- Operations:
  [runbooks](http://go/agora-id-drako-runbooks),
  [CMs](http://go/agora-id-drako-cms),
  [postmortems](http://go/agora-id-drako-pms)
- Reviews:
  [security (go-live)](http://go/agora-id-golive-review),
  [security (Drako)](http://go/agora-id-drako-sec-review)

</td>
</tr>
</table>

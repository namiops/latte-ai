# PostgreSQL - Service Page

<table markdown="1" border="1">
<tr markdown="1" valign="top">
<td markdown="block" width="50%">

## Overview

PostgreSQL is used as a database for Keycloak.
It is deployed by Crunchy Data's Postgres Operator.


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
- lab: `tcp://postgresql-primary.id.svc.cluster.local:5432`
- dev: `tcp://postgresql-primary.id.svc.cluster.local:5432`
- lab2: `tcp://postgresql-primary.id.svc.cluster.local:5432`
- dev2: `tcp://postgresql-primary.id.svc.cluster.local:5432`

</td>
<td markdown="block">

## Service Dependencies

- Upstream (this service is called by):
  - [id/keycloak](../keycloak/)
- Downstream (this service calls):
  - none

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Code Repositories

- none

</td>
<td markdown="block">

## Infrastructure

- Kubernetes manifests:
  [postgres](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/id/postgres),
  [common](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/id),
  [local](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/local/id),
  [lab](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/lab/id),
  [dev](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/dev/id),
  [lab2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/id),
  [dev2](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/id),
  [postgresql-operator](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/cityos-system)
- Docker images:
  [crunchy-postgres](https://artifactory-ha.tri-ad.tech/ui/native/crunchydata/crunchydata/crunchy-postgres/),
  [custom-pgbackrest](https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/postgresql/custom-pgbackrest/),
  [crunchy-postgres-exporter](https://artifactory-ha.tri-ad.tech/ui/native/crunchydata/crunchydata/crunchy-postgres-exporter/)

</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Deployment

- Deployment history:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/c660a9bc34f94e98fa4d5ac0a6a6030262b5a907),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/c660a9bc34f94e98fa4d5ac0a6a6030262b5a907),
  [lab2](),
  [dev2]()
- Commit history:
  [postgres](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/id/postgres),
  [common](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/id),
  [local](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/local/id),
  [lab](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/lab/id),
  [dev](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/dev/id),
  [lab2](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/id),
  [dev2](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/id),
  [postgresql-operator](https://github.com/wp-wcm/city/commits/main/infrastructure/k8s/common/cityos-system)

</td>
<td markdown="block">

## Testing


</td>
</tr>
<tr markdown="1" valign="top">
<td markdown="block">

## Observability

- Alerts:
  lab (🚧TODO🚧),
  [dev](http://go/agora-id-postgresql-alerts-dev),
  lab2 (🚧TODO🚧),
  [dev2](https://athena.agora-dev.w3n.io/grafana/alerting/list?queryString=%7Bnamespace%3D%22id%22,group%3D~%22postgresql.%2B%22%7D)
- Service Ingress Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/e34f4b5d9bc6547bbb95e0b308245f3e095c812f),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/e34f4b5d9bc6547bbb95e0b308245f3e095c812f),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/b00316d2-d4e3-4b6e-95ef-5dae82317953),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/b00316d2-d4e3-4b6e-95ef-5dae82317953)
- Service Egress Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/736d6f6c3f15b9a3152da89722371c2c7ffc8108),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/736d6f6c3f15b9a3152da89722371c2c7ffc8108),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/92fbf6b5-17fd-4b46-9439-84c1b8de5da5),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/92fbf6b5-17fd-4b46-9439-84c1b8de5da5)
- Resource Metrics:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/8bfbf794f7511d00d4be58128b56a76a68d71054),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/8bfbf794f7511d00d4be58128b56a76a68d71054),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/20776cfe-7a15-420c-be0a-5fc10d8ea98d),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/20776cfe-7a15-420c-be0a-5fc10d8ea98d)
- Logs:
  [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/7af279716e4c954dae51ee851b6c53852d54e18c),
  [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/7af279716e4c954dae51ee851b6c53852d54e18c),
  [lab2](https://athena.agora-lab.w3n.io/grafana/d/a7ca2341-ea44-412e-8c6e-eaac7319f610),
  [dev2](https://athena.agora-dev.w3n.io/grafana/d/a7ca2341-ea44-412e-8c6e-eaac7319f610)

</td>
<td markdown="block">

## Documentation

- Operations:
  [Management tools](https://patroni.readthedocs.io),
  [runbooks](http://go/agora-id-postgresql-runbooks),
  [CMs](http://go/agora-id-postgresql-cms),
  [postmortems](http://go/agora-id-postgresql-pms)
- Reviews:
  privacy (🚧TODO🚧),
  security (🚧TODO🚧)

</td>
</tr>
</table>

# pgbench

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=flat-square)

This chart deploys a pgbench as a Kubernetes Job to an Agora cluster. To terminate the job after the pgbench workload finishes inside the Istio service mesh, the chart deploys an envoy-sidecar-helper container as well which is the same one used by Agora's reaper solution.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| database | string | `nil` | Database name used by the test |
| dbCredentials.passwordKey | string | `"password"` | K8S secret key for the database password name. |
| dbCredentials.secret | string | `nil` | Kubernetes secret that includes the database credentials. |
| dbCredentials.userKey | string | `"username"` | K8S secret key for the database user name. |
| dbHost | string | `nil` | Hostname of the database endpoint. |
| image | string | `"docker.artifactory-ha.tri-ad.tech:443/postgres:15"` | Image that includes "pgbench". |
| name | string | `"pgbench"` | Name for the creating K8S job. |
| pgbenchInitializeParameters[0] | string | `"--initialize"` |  |
| pgbenchInitializeParameters[1] | string | `"--scale"` |  |
| pgbenchInitializeParameters[2] | string | `"1"` |  |
| pgbenchParameters | list | `["--time","60","--client","1","--jobs","1"]` | pgbench parameters |

## How to generate this README

[helm-docs](https://github.com/norwoodj/helm-docs) is required.

```bash
$ helm-docs -c infrastructure/helm/pgbench --template-files infrastructure/helm/pgbench/README.md.gotmpl
```

## How to use

```bash
$ helm install -n <namespace> <Helm release name> infrastructure/helm/pgbench --set \
    dbCredentials.secret=pgbench.agora-postgresql.credentials.postgresql.acid.zalan.do,\
    dbHost=agora-postgresql.shared-rdbms.svc.cluster.local,\
    database=pgbench
```

## Reference

- [pgbench](https://www.postgresql.org/docs/current/pgbench.html)
- [envoy-sidecar-helper](https://github.com/maksim-paskal/envoy-sidecar-helper)

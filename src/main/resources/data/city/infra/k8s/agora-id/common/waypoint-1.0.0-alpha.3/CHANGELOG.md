# Changelog

## v1.0.0-alpha.3
### Export `'*'` now removed
Speedway cluster does not support this wildcard export. The wildcard export is not supported in speedway.

### FQDN are moved to infra team
Service entry for internal FQDN routing will be deployed to infra team namespace
- remove fqdn entrypoint (move to city ingress)

### ServiceEntry for workload selector now removed
SMC cluster only limit ServiceEntry effect inside in own namespace. There will be no benefit to keep using ServiceEntry for workloadselector. we will back to use traditional kube service endpoint

### Remove configmap replacement
Kustomize replacement is really confusing and lead to bad maintainability. As FQDN will be moved to somewhere else the replacement is no longer needed
- unify gateway to annotation selector patch (see readme/configuration guide/gateway annotation)

## v1.0.0-alpha.2
- remove stable keyword
- support hostname configuration in configmap

## v1.0.0-alpha.1
first release

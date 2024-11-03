# apicurio-registry-content-sync-operator

## versions

### 0.0.2

The keycloak operator is upgraded so the domain of `AUTH_TOKEN_ENDPOINT` is updated from `keycloak-discovery.id.svc.cluster.local:8080` to `keycloak-http.id.svc.cluster.local`.

### 0.0.1
The base yamls are in [the apicurio-registry-content-sync-operator
standard directory](https://github.com/Apicurio/apicurio-registry-content-sync-operator/tree/main/deploy/standalone) (commit id: `d0e250532934dabd2552a241262c41d0087a5cc1`)

The following changes are applied:

- add namespace
- [WATCH_NAMESPACES(quarkus.operator-sdk.namespaces)](https://github.com/Apicurio/apicurio-registry-content-sync-operator/blob/d0e250532934dabd2552a241262c41d0087a5cc1/sync/src/main/resources/application.properties#L15)
  is set to "" to watch all the namespace
- change `Role` to `ClusterRole` to watch all the namespace 

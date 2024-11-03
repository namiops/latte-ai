# Apicurio in `common`

## Integration with Keycloak

Apicurio Keycloak integration is implemented based on [Securing Apicurio Registry 2.0 using Keycloak | Apicurio](https://www.apicur.io/blog/2021/05/28/registry-security)

## CHANGELOG

The directory is named like `apicurio-<env_version>-agora<our_version>`

### apicurio-0.0.2-agora2

The keycloak operator is upgraded and the following changes are applied:

- the domain of `AUTH_TOKEN_ENDPOINT` is updated from `keycloak-discovery.id.svc.cluster.local:8080` to `keycloak-http.id.svc.cluster.local`.
- `keycloakclient.spec.serviceAccountRealmRoles` is specified to `apicurio-registry-developer` in `apicurio` KeycloakClient.`apicurio-registry-content-sync-operator` uses the client credentials flow to have the write access to our Apicurio registry and this configuration is necessary.

### apicurio-0.0.2-agora1

The initial version of the next-gen(e.g. Lab2/Dev2) environment

### apicurio-0.0.1-agora1

The initial version of the legacy(e.g. Lab1/Dev1) environment

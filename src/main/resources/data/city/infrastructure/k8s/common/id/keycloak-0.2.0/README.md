# Keycloakx

This folder represents the manifests used to build a keycloak installation based
on the Keycloak operator 18.0.0-legacy.

The keycloak version is set by the operator and this can accidentaly be used by
other versions as well - with unknown success levels at this stage

operator uses Keycloak operator 18.0.0-legacy.
It manages Quarkus Keycloak instances with [external keycloak](https://www.keycloak.org/docs/latest/server_installation/index.html#_external_keycloak). It allows you to create clients.



The folder `vault-example` contains example manifests that should be added
in an environment where you want to setup keycloak with vault.

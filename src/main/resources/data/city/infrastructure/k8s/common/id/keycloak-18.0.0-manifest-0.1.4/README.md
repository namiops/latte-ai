# Keycloak

This folder represents the manifests used to build a keycloak installation based
on the Keycloak operator 18.0.0-legacy.

The keycloak version is set by the operator and this can accidentaly be used by
other versions as well - with unknown success levels at this stage



The folder `vault-example` contains example manifests that should be added
in an environment where you want to setup keycloak with vault.

# Changelog
- add `PodDisruptionBudget` due to the issue of incompatible of Keycloak Operator ([iamsprint/6043079182](https://go/iamsprint/6043079182))
- adjust JVM heap memory allocation parameter. According to the Statefulset resource configuration at ~70% ([iamsprint/5677061273](https://go/iamsprint/5677061273))

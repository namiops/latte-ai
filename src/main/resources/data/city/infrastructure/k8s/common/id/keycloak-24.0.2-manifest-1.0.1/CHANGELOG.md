# CHANGELOG

## Manifest v1.0.1
- Disables Keycloak's own metrics endpoint
- Exposes keycloak-metrics-spi plugin metrics to prometheus

## Manifest v1.0.0
- initiate new version of keycloak to v24.0.2
- upgrade guide: https://www.keycloak.org/docs/latest/upgrading/index.html#migrating-to-24-0-0
- The user profile feature is now enabled by default. The `declarative-user-profile`feature is no longer available, because the user profile is assumed to be enabled
- enable metrics endpoint by default [iamsprint/5676178766](https://go/iamsprint/5676178766)
- annotation with `prometheus.io` for metric scraping [iamsprint/5676178766](https://go/iamsprint/5676178766)
- remove `ExternalKeycloak`
- use IPv4 as default, add extra patch for IPv6 support `ipv6-support`
- pod anti affinity support will be optional by default, see `pod-anti-affinity-support` patch to enable this feature
- rename `PodDisruptionBudget` to avoid name colision with other version of keycloak
- revise `version` and `app` labels, move it to `commonLabels`

## Manifest v0.2.2
- Moved the `keycloak-realm-master.yaml` and `keycloak-realm-woven.yaml` to each environment, so those files won't be duplicated when creating new manifests

## Manifest v0.2.1
- Support `workspaceExportTo` labels in `service-keycloak-22-http`. This will be exported for another workspace. The label will be controlled by `id.workspacesetting`
- Add `PodDisruptionBudget` to ensure there will be at least 1 Keycloak instance left during the k8s Node rearrange. - Adjust JVM heap memory allocation parameter. According to the Statefulset resource configuration at ~70% ([iamsprint/5677061273](https://go/iamsprint/5677061273))
- Add `emptydir` for a default Vault mount volume.
- Add `manifestVersion`, `version,` and `name` of the component to the customization annotation transformer

## Manifest v0.2.0
- add Vault support argument. Vault is point to `/opt/vault`


# Release status
Execute this command to see the release stage
```
grep -r "keycloak-24.0.2-manifest-1.0.0" ./environments/
grep -r "keycloak-24.0.2-manifest-1.0.0" ./dev/
grep -r "keycloak-24.0.2-manifest-1.0.0" ./lab/
```

# Diff and commit mesesage
To find the list of change log commit please execute this command in this folder.

```
git log --pretty=oneline -- . 
```

To find out the diff between previous version please run
```
diff -ruN ../keycloak-22.0.0-manifest-0.2.2 .
```


# CHANGELOG

## Manifest v0.2.2
- Moved the `keycloak-realm-master.yaml` and `keycloak-realm-woven.yaml` to each environment, so those files won't be duplicated when creating new manifests

## Manifest v0.2.1
- Support `workspaceExportTo` labels in `service-keycloak-22-http`. This will be exported for another workspace. The label will be controlled by `id.workspacesetting`
- Add `PodDisruptionBudget` to ensure there will be at least 1 Keycloak instance left during the k8s Node rearrange. - Adjust JVM heap memory allocation parameter. According to the Statefulset resource configuration at ~70% ([iamsprint/5677061273](https://go/iamsprint/5677061273))
- Add `emptydir` for a default Vault mount volume.
- Add `manifestVersion`, `version,` and `name` of the component to the customization annotation transformer

## Manifest v0.2.0
- add Vault support argument. Vault is point to `/opt/vault`

# Diff and commit mesesage
To find the list of change log commit please execute this command in this folder.

```
git log --pretty=oneline -- . 
```

To find out the diff between previous version please run
```
diff -ruN ../keycloak-22.0.0-manifest-0.2.0 .
```


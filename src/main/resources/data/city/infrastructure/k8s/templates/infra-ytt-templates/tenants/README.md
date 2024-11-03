# IYTT Tenants Templates

These templates are used to generate the following files

* `infrastructure/k8s/dev/flux-system/kustomizations/tenants/kustomization.yaml`

## Testing the templates

It is recommended to test the templates via Bazel. You can do that by
following the instructions [here](../../../../k8s/dev/flux-system/kustomizations/tenants).

You can also use YTT here by performing the following in this directory

```shell
$ ytt -f .      
apiVersion: kustomize.toolkit.fluxcd.io/v1beta2
kind: Kustomization
metadata:
  name: tenant-scions
  namespace: flux-system
spec:
  interval: 1m0s
  path: ${manifest_root}/flux-tenants/scions
  dependsOn:
  - name: services
  prune: true
  sourceRef:
    kind: GitRepository
    name: cityos
  postBuild:
    substitute:
      name: scions
      bootstrap_interval: 1m0s
      bootstrap_path: revenants-toll
      bootstrap_prune: "true"
      cluster_name: my-cluster
      git_url: ssh://git@github.tri-ad.tech/my-scions-project
      git_branch: develop
      istio_rev: default
      tenant_owner_group: aad:myAAD
      tenant_engineer_group: aad:myAAD
```

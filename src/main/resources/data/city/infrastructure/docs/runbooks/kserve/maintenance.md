# Maintenance Guideline

* [Maintenance Guideline](#maintenance-guideline)
  * [Upgrade KServe on Speedway](#upgrade-kserve-on-speedway)
    * [1 Update a namespace in all ServiceAccounts](#1-update-a-namespace-in-all-serviceaccounts)
    * [2 TLS certificate for webhook service](#2-tls-certificate-for-webhook-service)

## Upgrade KServe on Speedway

### 1 Update a namespace in all ServiceAccounts

Since KServe namespace on Speedway is different from the original one (from `kserve` to `agora-kserve-dev` for example), some RoleBindings for ServiceAccounts don't work properly. For example, RoleBinding rendered from Helm looks like the below:

```yaml
# Source: kserve/templates/rolebinding.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: kserve-leader-election-rolebinding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: kserve-leader-election-role
subjects:
- kind: ServiceAccount
  name: kserve-controller-manager
  namespace: kserve     # <--------- THIS NAMESPACE WON'T BE UPDATED BY KUSTOMIZATION IF WE DON'T FOLLOW THE NEXT STEP.
```


Then, after we rendered KServe manifest files from Helm chart, we need to make sure that we manually set a namespace in all ServiceAccounts:

- `kserve-controller-manager`
- `modelmesh-controller`
- `modelmesh`

For example,

```yaml
# Source: kserve/templates/serviceaccount.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  labels:
    app.kubernetes.io/instance: kserve-controller-manager
    app.kubernetes.io/managed-by: kserve-controller-manager
    app.kubernetes.io/name: kserve-controller-manager
  name: kserve-controller-manager
  namespace: kserve # <--------- MAKE SURE THAT NAMESPACE IS SET WITH `kserve`.
```

### 2 TLS certificate for webhook service

See patch files for these webhook:
- [ValidatingWebhookConfiguration for kserve-controller](../../../../infra/k8s/agora-kserve/speedway/prod-ml/patches/validatingwebhookconfiguration-inferenceservice.yaml)
- [ValidatingWebhookConfiguration for modelmesh-controller](../../../../infra/k8s/agora-kserve/speedway/prod-ml/patches/validatingwebhookconfiguration-modelmesh-servingruntime.yaml)

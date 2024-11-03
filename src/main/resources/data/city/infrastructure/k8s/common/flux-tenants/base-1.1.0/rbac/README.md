# RBAC Guideline
This guideline expalins the RBAC model implemented for tenants and how to customize RBAC for a specific team if that team requires to access more API resources. For more details and background of the RBAC model, please see [TN-0258 RBAC model of Tenant's Flux Reconcilers for Self-service Functionality](https://docs.google.com/document/d/1ysaWhf4OV_qmIT0LGBYykwk3NC-Sc2t-0a3eaMaPoNs/edit#heading=h.lg61s7dek3ke).

- [RBAC Guideline](#rbac-guideline)
  - [RoleBinding Types](#rolebinding-types)
  - [RBAC in Flux Namespace](#rbac-in-flux-namespace)
  - [RBAC in Application Namespaces](#rbac-in-application-namespaces)
    - [Generate RoleBinding files](#generate-rolebinding-files)
      - [Base RoleBindings](#base-rolebindings)
      - [RoleBindings for a specific service](#rolebindings-for-a-specific-service)
    - [Customize Permissions per Namespace](#customize-permissions-per-namespace)

## RoleBinding Types
There are 2 types of RoleBindings implemented for tenants: `group` and `reconciler`:
1. `group` folder: is for RoleBinding objects binding a human user group (Azure AD group) and a read-only ClusterRole object. Currently, there are 2 kinds of ClusterRoles:
   - Tenant owner
   - Tenant engineer
2. `reconciler` folder is for RoleBinding objects binding Flux reconciler's service account and a read/edit base ClusterRole object. By default, the service account is given some permissions defined inside `reconciler/base`. In case that some reconcilers requires customized permissions, we can add more RoleBindings in a new folder. For example, add RoleBindings to `reconciler/kserve`  for read/edit permissions to KServe API resources.

## RBAC in Flux Namespace
When a flux tenant is deployed, one namespace (ex. `flux-tenant-mobility` namespace) is created for keeping deployment secrets, Flux reconciler's service account, Flux Kustomization objects and etc.  In case of tenant users, only tenant owners are allowed to access this namespace.

**NOTE:** RBAC objects are deployed and patched by `../flux/kustomization.yaml`.

## RBAC in Application Namespaces
One tenant is allowed to have multiple application namespaces. 
Objects inside those application namespaces are managed by the Flux reconciler from the Flux namespace. Then, we need to deploy reconciler's RoleBinding inside each namespace.

**NOTE:** RBAC objects are deployed by `../ns/kustomization.yaml` and their namespace metadata is patched separately for each namespace.

### Generate RoleBinding files

#### Base RoleBindings
YAML template files are provided under `cityos/infrastructure/k8s/templates/tenants/rbac/flux-reconciler` folder. 
For example, we can regenerate base RoleBindings for Flux reconcilers by updating `cityos/infrastructure/k8s/templates/tenants/rbac/flux-reconciler/base-values.yaml` and run this script:
```
cd cityos/infrastructure/k8s/bin
./gen_reconciler_rbac -o ./base
```
Then, replace `cityos/infrastructure/k8s/common/flux-tenants/base-1.1.0/rbac/reconciler/base` with the output folder.

#### RoleBindings for a specific service
In case that you would like to generate RoleBindings for only 1 service, create a new value file. 
For example, `cityos/infrastructure/k8s/templates/tenants/rbac/flux-reconciler/kserve-values.yaml`:
```
#@data/values
---
permissions:
  - kserve-edit
  - kserve-view

```

Then, run the script:
```
cd cityos/infrastructure/k8s/bin
./gen_reconciler_rbac -f ..//templates/tenants/rbac/flux-reconciler/kserve-values.yaml -o ./kserve
```
Then, move `./kserve` to `cityos/infrastructure/k8s/common/flux-tenants/base-1.1.0/rbac/reconciler/kserve`

### Customize Permissions per Namespace
With the current design, we can allow custom permissions per namespace by creating new RoleBindings binding a reconciler with additional base ClusterRoles. The below is example to do so.

Assume that, in DEV cluster, `mobility` tenant has 3 namespaces: `happo-one`, `palcal`, and `niseko`. Since the  `mobility` tenant requires KServe API resources which are not allowed by default, we would like to grant extra permissions in all namespaces owned by this tenant. There are 4 steps to do that:

1. Make sure that you have deployed read/edit base roles for KServe.
```
ClusterRole: cityos-base-kserve-edit
ClusterRole: cityos-base-kserve-view
```

2. Create new RoleBindings for read/edit KServe permissions under `reconciler/kserve`.
```
reconciler/kserve
├── kustomization.yaml
├── rolebinding-flux-reconciler-kserve-edit.yaml
└── rolebinding-flux-reconciler-kserve-view.yaml
```

The contents of each file are as the following:
```
# rolebinding-flux-reconciler-kserve-edit.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  labels:
    toolkit.fluxcd.io/tenant: flux-tenant-${name}
  name: flux-reconciler-kserve-edit
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cityos-base-kserve-edit
subjects:
  - kind: ServiceAccount
    name: flux-tenant-${name}
    namespace: flux-tenant-${name}

# rolebinding-flux-reconciler-kserve-view.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  labels:
    toolkit.fluxcd.io/tenant: flux-tenant-${name}
  name: flux-reconciler-kserve-view
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cityos-base-kserve-view
subjects:
  - kind: ServiceAccount
    name: flux-tenant-${name}
    namespace: flux-tenant-${name}
```

3. In `cityos/infrastructure/k8s/dev/flux-tenants/mobility` where we deploy tenant's Flux Kustomizations, create an `rbac` folder and `kustomization.yaml` file for building custom RoleBinding objects by Kustomization.
**NOTE:** We can allow more API permissions by adding a new entry here.
```
# cityos/infrastructure/k8s/dev/flux-tenants/mobility/rbac/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- ../../../common/flux-tenants/base-1.1.0/rbac/reconciler/kserve
```

4. For each namespace, all namespace's `kustomization.yaml` files should be updated to include the `rbac` folder from the previous step. 
  - `cityos/infrastructure/k8s/dev/flux-tenants/mobility/namespaces/happo-one/kustomization.yaml`
  - `cityos/infrastructure/k8s/dev/flux-tenants/mobility/namespaces/palcal/kustomization.yaml`
  - `cityos/infrastructure/k8s/dev/flux-tenants/mobility/namespaces/niseko/kustomization.yaml`
For example, update `palcal` namespace's `kustomization.yaml`:
```
# cityos/infrastructure/k8s/dev/flux-tenants/mobility/namespaces/palcal/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: palcal
resources:
- ../../../../../common/flux-tenants/base-1.1.0/ns
- ../../rbac

patches:
  - patch: |-
      - op: replace
        path: "/metadata/name"
        value: palcal
    target:
      kind: Namespace
```

Finally, the file structure where we have updated configs should look like below:
```
k8s/common/flux-tenants/base-1.1.0/rbac/reconciler/kserve
├── kustomization.yaml
├── rolebinding-flux-reconciler-kserve-edit.yaml
└── rolebinding-flux-reconciler-kserve-view.yaml

k8s/dev/flux-tenants/mobility
├── kustomization.yaml
├── namespaces
│   ├── kustomization.yaml
│   ├── happo-one
│   │   └── kustomization.yaml
│   ├── niseko
│   │   └── kustomization.yaml
│   └── palcal
│       └── kustomization.yaml
└── rbac
    └── kustomization.yaml
```

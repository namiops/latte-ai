# Migrating Your Tenant and Namespace from `dev` to `pre-prod`

!!! Warning
    The migration process below may be subject to changes with infrastructure updates. Please refer to the working examples and/or [contact us](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7) if you need assistance.

This is a step-by-step guide on how to migrate your tenant and namespace from the legacy `dev` environment onto the `pre-prod` cluster.

* For a live working example containing all the files modified here, see [this pull request](https://github.com/wp-wcm/city/pull/27168).
* If you need assistance at any point, feel free to submit a question on our [AMA channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).

## What you'll learn

* [1. Prepare your deployment manifests](#1-prepare-your-deployment-manifests)
* [2. Set up the following features:](#2-set-up-flux-kustomization-namespace-and-rbac)
  * [a. Flux kustomization](#a-flux-kustomization)
  * [b. Namespace](#b-namespace)
  * [c. RBAC](#c-rbac)
* [3. Automate image updates (optional)](#3-automate-image-updates-optional)
* [4. Run Gazelle](#4-run-gazelle)

## Steps

### 1. Prepare your deployment manifests

First, navigate to `infrastructure/k8s/common/<YOUR_NAMESPACE>/<YOUR_SERVICE>/base/` and create the following files:

```yaml title="deployment.yaml"
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: <YOUR_SERVICE>
  name: <YOUR_SERVICE>
spec:
  replicas: 1
  selector:
    matchLabels:
      app: <YOUR_SERVICE>
  template:
    metadata:
      labels:
        app: <YOUR_SERVICE>
    spec:
      containers:
        - name: <YOUR_SERVICE>
          image: <YOUR_IMAGE>
          ports:
            - containerPort: 8080
              name: http
              protocol: TCP
          securityContext:
            allowPrivilegeEscalation: false
      securityContext:
        runAsNonRoot: true
```

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yaml
  - service.yaml
```

```yaml title="service.yaml"
apiVersion: v1
kind: Service
metadata:
  name: <YOUR_SERVICE>
spec:
  selector:
    app: <YOUR_SERVICE>
  ports:
    - port: 8080
      name: http
      protocol: TCP
```

In the folder `infrastructure/k8s/common/<YOUR_NAMESPACE>/<YOUR_SERVICE>/dev/`, create the following file:

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: <YOUR_NAMESPACE>
resources:
  - ../base
```

Now go to `infrastructure/k8s/environments/dev2/clusters/worker1-east/<YOUR_NAMESPACE>/` and create the following file:

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../../../../../common/<YOUR_NAMESPACE>/<YOUR_SERVICE>/dev
images:
  - name: <YOUR_IMAGE>
    newName: <YOUR_REPOSITORY> # {"$imagepolicy": "flux-tenants:<YOUR_TENANT>-image-policy-dev:name" }
    newTag: "main-ffffffff-1709858264" # {"$imagepolicy": "flux-tenants:<YOUR_POLICY>:tag" }
```

### 2. Set up Flux kustomization, namespace, and RBAC

Next, set up your Flux kustomization, namepsace, and role-based access control (RBAC) by following the steps in the sections below. Your final folder structure will look something like this:

```sh
$ pwd
~/infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants

$ tree
.
├── _core                                   
│   └── kustomizations              
│       ├── kustomization.yaml          <--- MODIFY
│       ├── <YOUR_TENANT>               <--- CREATE 
│       │   ├── kustomization.yaml      <--- CREATE 
│       │   └── <YOUR_NAMESPACE>.yaml   <--- CREATE 
├── namespaces
│   ├── kustomization.yaml              <--- MODIFY
│   ├── <YOUR_NAMESPACE>                <--- CREATE 
│   │   └── kustomization.yaml          <--- CREATE 
├── rbac
│   ├── <YOUR_TENANT>                   <--- CREATE 
│   │   ├── kustomization.yaml          <--- CREATE 
│   │   └── rbac.yaml                   <--- CREATE 
```

#### a. Flux kustomization

The files that needs to be modified or created are shown below:

```sh
├── _core                                   
│   └── kustomizations              
│       ├── kustomization.yaml          <--- MODIFY
│       ├── <YOUR_TENANT>               <--- CREATE 
│       │   ├── kustomization.yaml      <--- CREATE 
│       │   └── <YOUR_NAMESPACE>.yaml   <--- CREATE 
```

Under `_core/kustomizations/`, create a folder named `<YOUR_TENANT>`.
In that folder, create the file `<YOUR_NAMEPSACE>.yaml` with the contents below:

```yaml
apiVersion: kustomize.toolkit.fluxcd.io/v1
kind: Kustomization
metadata:
  name: <YOUR_NAMESPACE>
  namespace: flux-tenants
spec:
  interval: 1m0s
  path: ${manifest_root}/<YOUR_NAMESPACE>
  prune: true
  serviceAccountName: flux-tenants
  sourceRef:
    kind: GitRepository
    name: git-source-city
  targetNamespace: <YOUR_NAMESPACE>
  postBuild:
    substituteFrom:
      - kind: ConfigMap
        name: cluster-vars
```

Then, create a a file under `_core/kustomizations/<YOUR_TENANT>/` as follows:

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - <YOUR_NAMESPACE>.yaml
```

Modify `_core/kustomizations/kustomization.yaml` by adding the new folder `<YOUR_TENANT>` under the `resources:` section alphabetically.

#### b. Namespace

The files that needs to be modified or created are shown below:

```zsh
├── namespaces
│   ├── kustomization.yaml              <--- MODIFY
│   ├── <YOUR_NAMESPACE>                <--- CREATE 
│   │   └── kustomization.yaml          <--- CREATE 
```

Under `namespaces/`, create the folder `<YOUR_NAMEPSPACE>`.
Create a new file under this new folder with the following contents:

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../base
  - ../../rbac/<YOUR_TENANT>
patches:
  - patch: |-
      - op: replace
        path: "/metadata/name"
        value: <YOUR_NAMESPACE>
    target:
      kind: Namespace
```

Modify the `namepsaces/kustomization.yaml` file by adding the new `<YOUR_NAMEPSACE>` folder under the `resources:` section alphabetically.

#### c. RBAC

The files that needs to be modified or created are shown below:

```sh
├── rbac
│   ├── <YOUR_TENANT>                   <--- CREATE 
│   │   ├── kustomization.yaml          <--- CREATE 
│   │   └── rbac.yaml                   <--- CREATE 
```

Under `rbac/`, create a folder named `<YOUR_TENANT>`.
Create a file under `rbac/<YOUR_TENANT>/` containing the following contents:

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - rbac.yaml
```

In the same folder, create a file as follows:

```yaml title="rbac.yaml"
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  labels:
    toolkit.fluxcd.io/tenant: flux-tenants
  name: cityos-rolebinding-tenant-<YOUR_TENANT>-owners
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cityos-role-tenant-owner
subjects:
  - kind: Group
    apiGroup: rbac.authorization.k8s.io
    # cityos-tenant-<YOUR_TENANT>-administrators
    name: aad:ebb5d013-77f5-4ef1-b6a6-65b697b66148    <--- Modify these for your own group
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  labels:
    toolkit.fluxcd.io/tenant: flux-tenants
  name: cityos-rolebinding-tenant-<YOUR_TENANT>-engineers
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cityos-role-tenant-engineer
subjects:
  - kind: Group
    apiGroup: rbac.authorization.k8s.io
    # cityos-tenant-<YOUR_TENANT>-engineers
    name: aad:aabba867-6a2f-4512-b3b4-407924374053    <--- Modify these for your own group
```

!!! Note
    You can find your groups AAD IDs on the [Azure Portal Dashboard](https://portal.azure.com/#view/Microsoft_AAD_IAM/GroupsManagementMenuBlade/~/AllGroups).

### 3. Automate image updates (optional)

!!! Note
    This section sets up image update automation for your deployments. You may skip this part if you don't need this feature for now and set it up later if required.

Go to `infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/flux/automations/` and make the changes below.
In `kustomization.yaml`, add the following in the correct alphabetical position:

```yaml
- <YOUR_NAMESPACE>.yaml
```

And create `<YOUR_NAMESPACE>.yaml` as follows:

```yaml
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImageUpdateAutomation
metadata:
  name: <eas-enesim-biz-automation-dev>
  namespace: flux-tenants
  labels:
    sharding.fluxcd.io/key: nativegit
spec:
  git:
    checkout:
      ref:
        branch: main
    commit:
      author:
        email: 129496058+city-fluxcd[bot]@users.noreply.github.com
        name: city-fluxcd[bot]
      messageTemplate: |
        k8s(dev2//<YOUR_NAMESPACE>): bump {{range .Updated.Images}}{{printf "%s:%s\n" .Repository .Identifier}}{{end}}

        Files:
        {{ range $filename, $_ := .Updated.Files -}}
        - {{ $filename }}
        {{ end -}}

        Objects:
        {{ range $resource, $_ := .Updated.Objects -}}
        - {{ $resource.Kind }} {{ $resource.Name }}
        {{ end -}}

        Images:
        {{ range .Updated.Images -}}
        - {{.}}
        {{ end -}}

        By: {{ .AutomationObject }}
    push:
      branch: __image/dev2/<YOUR_NAMESPACE>/image-updates
  interval: 3m0s
  sourceRef:
    kind: GitRepository
    name: git-source-city
  update:
    path: infrastructure/k8s/environments/dev2/clusters/worker1-east/<YOUR_NAMESPACE>
    strategy: Setters
```

In `infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/flux/images/`, insert `<YOUR_NAMESPACE>.yaml` in the correct positions in the `BUILD` and `kustomization.yaml` files.

Then, make a new `<YOUR_NAMESPACE>.yaml` as follows:

```yaml
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImageRepository
metadata:
  name: <YOUR_REPOSITORY>
  namespace: flux-tenants
spec:
  image: docker.artifactory-ha.tri-ad.tech/wcm-backend/<YOUR_NAMESPACE>/frontend-prod
  interval: 1m0s
---
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImagePolicy
metadata:
  name: <YOUR_POLICY>
  namespace: flux-tenants
spec:
  imageRepositoryRef:
    name: <YOUR_REPOSITORY>
  filterTags:
    pattern: ".*main-[a-f0-9]+-(?P<runid>[0-9]+)"
    extract: "$runid"
  policy:
    numerical:
      order: asc
---
```

### 4. Run Gazelle

Finally, use the Gazelle tool to auto-generate your `BUILD` files, among other functions.

!!! Note
    If you don't have Bazel installed, please follow [these steps for installation](/docs/default/domain/agora-domain/development/bazel/#installing-bazel).

Run the following command anywhere within the city monorepo:

```sh
bazel run //:gazelle
```

This concludes the steps for generating your tenant and namespace in the `pre-prod` environment. When you are done, create a pull request for all the changes and contact the [Agora DevRel team](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7) for approval.

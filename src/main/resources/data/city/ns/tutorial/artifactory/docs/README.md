# Migration to Artifactory SaaS on Agora Platform

## Using Artifactory SaaS on Agora Speedway Platform (monorepo user)

To ensure smooth migration, we ask for all monorepo user to configure `imagePullSecrets` to all workload from now on.

### Update the workload to add ImagePullSecret

Update all the workloads that use the internal artifactory to add the `imagePullSecrets`.
- Use `city-art-token` as the secret. This secret should be present in all namespaces on Speedway
- Images that pulled public registry but using internal artifactory as proxy must be using `imagePullSecrets` as well

To add `imagePullSecrets` to all workloads, add this to the kustomization.yaml on the root directory (`infra/k8s/*/speedway/{dev/prod}/kustomization.yaml`)

```yaml
kind: Kustomization
namespace: {namespace}
resources: ...
patches:
  - target:
      kind: CronJob
    patch: |-
      - op: add
        path: /spec/jobTemplate/spec/template/spec/imagePullSecrets
        value: [{ name: city-art-token }]
  - target:
      kind: (DaemonSet|Deployment|Job|Pod|ReplicaSet|StatefulSet)
    patch: |-
      - op: add
        path: /spec/template/spec/imagePullSecrets
        value: [{ name: city-art-token }]
```

Or if you prefer to add it per workload, this is an example if the workload is a `Deployment` (`StatefulSet` will also share the same configuration)

```yaml
kind: Deployment
metadata:
  name: helloworld-go
spec:
  template:
    spec:
      containers:
      imagePullSecrets:
      - name: city-art-token
```

!!! warning
    For helm the places to add imagePullSecrets varies between chart, please adjust accordingly

## Using Artifactory SaaS on Agora Speedway Platform (non-monorepo user)

### Configure Access

!!! warning
    Skip if you already did this step for pre-prod.

!!! warning
    Based on our experience, it takes about 2-3 hours for this to get reflected.

1. On the Stargate Portal page, create a new User Group.
    ![creating a user group in Artifactory](./assets/add-user-group.png)
1. Assign the Artifactory resources to the group. Add all repositories that you want to use in Agora environment
    ![assigning resources to a group in Artifactory](./assets/add-resource.png)
1. Add `svc-agora-ci@woven-planet.global` to the members
    ![adding a specific member to the new group](./assets/add-member.png)

### Verify the namespace have the necessary secret

The secret should be created automatically for the namespace, but please confirm if you have it by running this command:

```sh
kubectl get secrets city-art-token  
```

If the secret does not exist, please contact @agora-build at [#wcm-cicd-support](https://toyotaglobal.enterprise.slack.com/archives/C02660CMJLT) channel.

### Update the workload to add ImagePullSecret

Update all the workloads that use the new artifactory to configure the `imagePullSecrets`. This is an example if the workload is a `Deployment` (`StatefulSet` will also share the same configuration)

```yaml
kind: Deployment
metadata:
  name: helloworld-go
spec:
  template:
    spec:
      containers:
      imagePullSecrets:
      - name: city-art-token
```

### Update citycd.yaml to point the image to new artifactory

1. Find the `citycd.yaml` and update the image to the new URL of the artifactory

    ```yaml
    images:
      <name>:
        image: jp1-artifactory.stargate.toyota/...
    ```

1. After the PR is merged, new Image Update PR(s) should be created
1. Merge the Image Update PR and verify the workload can start using the new image


## Using Artifactory SaaS on Agora Pre-prod environment

!!! warning
    Currently this document intended for the deprecated environments: Pre-Prod and Legacy Dev. We will update this document when we have information for Speedway.

This document provides guidelines to teams for how to use the new Artifactory SaaS provided by Stargate with Agora's clusters.

- [artifactory.stargate.toyota](https://artifactory.stargate.toyota)
- [jp1-artifactory.stargate.toyota](https://jp1-artifactory.stargate.toyota)

### Configure Access (for non-monorepo user)

!!! warning
    Based on our experience, it takes about 2-3 hours for this to get reflected.

1. On your Stargate Portal page, create a new User Group.
    ![creating a user group in Artifactory](./assets/add-user-group.png)
1. Assign the Artifactory resources to the group. Add all repositories that you want to use in Agora environment
    ![assigning resources to a group in Artifactory](./assets/add-resource.png)
1. Add `svc-agora-ci@woven-planet.global` to the members
    ![adding a specific member to the new group](./assets/add-member.png)

### Configure ClusterRoleBinding to Pull Secrets from Vault

!!! note
    If you are already using vault in your namespace, you can skip this part.

1. Create `ClusterRoleBinding` so your namespace are able to pull secrets from Vault.

    ```yaml
    # infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/namespaces/<YOUR NAMESPACE>/vault-auth.yaml

    apiVersion: rbac.authorization.k8s.io/v1
    kind: ClusterRoleBinding
    metadata:
      name: <YOUR NAMESPACE>-auth-delegator
    roleRef:
      apiGroup: rbac.authorization.k8s.io
      kind: ClusterRole
      name: system:auth-delegator
    subjects:
    - kind: ServiceAccount
      name: <YOUR NAMESPACE>-sa
      namespace: <YOUR NAMESPACE>
    ```

!!! note
    Make sure the `<YOUR NAMESPACE>-sa` exists on your namespace.
    If not, please also create the service account.

### Configure ExternalSecret on your `namespace` to Pull Secrets from Vault 

Based on the environment you use, replace `mountpath` with this value:

- Pre Prod: `kubernetes-dev2-worker1-east`
- Legacy Dev: `kubernetes-dev`

```yaml
apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: city-artifactory
spec:
  provider:
    vault:
      server: https://dev.vault.tmc-stargate.com
      namespace: ns_stargate/ns_dev_wcmshrd_agoracicd
      path: kv-agora-dev2-ci
      version: v2
      auth:
        kubernetes:
          mountPath: <REPLACE WITH ENVIRONMENT>
          role: city-artifactory
          serviceAccountRef:
            name: <YOUR SERVICE ACCOUNT FROM PREVIOUS STEP>
---
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: city-artifactory
spec:
  secretStoreRef:
    kind: SecretStore
    name: city-artifactory
  target:
    name: city-art-token
    template:
      type: kubernetes.io/dockerconfigjson
  data:
  - secretKey: .dockerconfigjson
    remoteRef:
      key: city-artifactory
      property: .dockerconfigjson
```

This will create `city-art-token` secret on your namespace. Verify it by running this command:

```sh
kubectl get secrets city-art-token  
```

If configured properly, it should output the following:

```plain
NAME             TYPE                             DATA   AGE
city-art-token   kubernetes.io/dockerconfigjson   1      7m27s
```

### Update the workload to the ImagePullSecret

Update all your workloads that use the new artifactory to configure the `imagePullSecrets`. This is an example if your workload is a `Deployment` (`StatefulSet` will also share the same configuration)

```yaml
kind: Deployment
metadata:
  name: helloworld-go
spec:
  template:
    spec:
      containers:
      - name: go
        image: jp1-artifactory.stargate.toyota/...
      imagePullSecrets:
      - name: city-art-token
```

### Update flux ImageRepository

Finally update the flux `ImageRepository` with the new Artifactory URL and also add `secretRef`.

!!! note
    `artifactory.stargate.toyota` will route you to the nearest geographic cluster, we recommend that you use the region specific URLs to avoid mistakes (`jp1`)

```yaml
apiVersion: image.toolkit.fluxcd.io/v1beta2
kind: ImageRepository
metadata:
  name: your-image-repository
spec:
  image: jp1-artifactory.stargate.toyota/...
  secretRef:
    name: city-art-token
```

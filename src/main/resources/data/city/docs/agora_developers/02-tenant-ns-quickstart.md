# Tenant and namespace creation guide

!!! warning
    This document outlines the steps to create a namespace for the legacy Dev environment of Agora. Since Agora has evolved into its production version, [Speedway](https://developer.woven-city.toyota/docs/default/Component/agora-migrations-tutorial/speedway/), please refer to the latest documentation.
    If you have any questions, please reach out to us in the [Agora AMA channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).

## Overview

Agora uses [Kubernetes namespaces](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) to allocate spaces for service applications and provide scoping of objects. This enables teams to determine the best way to deploy their services accordingly. The conceptual framework is shown below:

![Namespace concept map](./tenant-ns-quickstart-01.png)

This quickstart tutorial guides you through the steps to create a tenant for your team in the Agora team Kubernetes environment, as well as how to add namespaces within your tenant group.

## What you'll learn

At the end of this guide, you will learn how to:

* [1. Add an Azure Active Directory (AAD) group and create a GitHub team](#1-add-an-azure-active-directory-aad-group-and-create-your-github-team)
* [2. Set up your namespace](#2-create-your-namespace)
* [3. Link your namespace to your tenant group](#3-link-your-namespace-to-your-tenant-group)
* [4. Configure access to the `dev` cluster](#4-configure-access-to-the-dev-cluster)

## What you’ll need

Before starting this tutorial, you should have completed the steps below from the [Getting Started](README.md) guide:

1. Set up your programming environment
2. Obtain GitHub EMU access
3. Clone the GitHub monorepo

Also, you need to have the following tools installed:

* [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
* [kubelogin](https://github.com/Azure/kubelogin) *(You won't use this command directly in this set up process, but it's used behind the scenes.)*
  * Do **not** use Snap to install kubelogin as it will not be able to resolve DNS names properly!

## Steps

First, create a new branch in the [city repository](https://github.com/wp-wcm/city).

### 1. Add an Azure Active Directory (AAD) group and create your GitHub team

#### Email addresses

[aad/config.auto.tfvars.json](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/dev2/bootstrap/aad/config.auto.tfvars.json) in the `city/infrastructure/terraform/` subdirectory manages the information on all groups and membership on the platform.

You need to add your team members' email addresses to this file under the `tenant_groups` section as follows:

```json
{ 
  "tenant_groups": [
    {
      "name": "<YourTeamName>",
      "description": "CityOS tenant engineer (<YourTeamName>)",
      "administrator_owners": null,
      "administrator_members": null,
      "engineer_owners": null,
      "engineer_members": [
        "<example1@woven-planet.global>",
        "<example2@woven-planet.global>"
        // Other team members        
      ]
    },
    // Other teams
  ]
}
```

!!! Tip
    Add your list of members under `engineer_members` only, and make sure that it is sorted in alphabetical order.

See [aad/README.md](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/dev2/bootstrap/aad/README.md) for details.

#### GitHub usernames

Next, create a GitHub team and add your team members' usernames to [ci/github/config.auto.tfvars.json](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/ci/github/config.auto.tfvars.json) as follows:

```json
{
  "teams": [
    {
      "name": "<YourTeamName>",
      "members": [
        "<GitHubUsername1>",
        "<GitHubUsername2>",
        // Other team members
      ]
    },
    // Other teams
  ]
}
```

When you are done, make a pull request. The Agora team will review and approve the changes.

### 2. Create your namespace

To create your Kubernetes namespace, create the following `kustomization.yaml` file in `city/infrastructure/k8s/dev/flux-tenants/lps/namespaces/<YourNamespace>/`:

```yaml
# filename: kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: <YourNamespace>
resources:
  - ./../base
  - ./../../rbac/<YourTeamName>
patches:
  - patch: |-
      - op: replace
        path: "/metadata/name"
        value: <YourNamespace>
    target:
      kind: Namespace
```

Then, add your newly created folder name to `city/infrastructure/k8s/dev/flux-tenants/lps/namespaces/kustomization.yaml`.

### 3. Link your namespace to your tenant group

Now that you have your namespace, you need to modify several YAML files in the corresponding subdirectories to link it to your tenant group and team.

#### Obtain object IDs for your AAD groups

After your pull request has been approved, you should be able to find two entries for your team on the [Azure portal](https://portal.azure.com/#view/Microsoft_AAD_IAM/GroupsManagementMenuBlade/~/AllGroups):

* cityos-tenant-\<YourTeamName>-administrator
* cityos-tenant-\<YourTeamName>-engineer

Note down the object ID for both entries to be used later.

#### Create and modify your files

Go to the `city/infrastructure/k8s/dev/flux-tenants/lps/` subdirectory.

You can skip this step if your team's RBAC(Role Based Access Control) is available at `.../lps/rbac/<YourTeamName>`.

Make the modifications shown below.

1. Create the following `kustomization.yaml` under `.../lps/rbac/<YourTeamName>/`:

    ```yaml
    # filename: kustomization.yaml
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    resources:
    - rbac.yaml
    ```

2. Create `rbac.yaml` and copy your object IDs into `rbac.yaml` as follows:

    ```yaml
    # filename: rbac.yaml
    apiVersion: rbac.authorization.k8s.io/v1
    kind: RoleBinding
    metadata:
      labels:
        toolkit.fluxcd.io/tenant: flux-tenant-lps
      name: cityos-rolebinding-tenant-<YourTeamName>-owners
    roleRef:
      apiGroup: rbac.authorization.k8s.io
      kind: ClusterRole
      name: cityos-role-tenant-owner
    subjects:
    - kind: Group
      apiGroup: rbac.authorization.k8s.io
      # cityos-tenant-<YourTeamName>-administrators
      name: aad:<AdminObjectID>
    ---
    apiVersion: rbac.authorization.k8s.io/v1
    kind: RoleBinding
    metadata:
      labels:
        toolkit.fluxcd.io/tenant: flux-tenant-lps
      name: cityos-rolebinding-tenant-<YourTeamName>-engineers
    roleRef:
      apiGroup: rbac.authorization.k8s.io
      kind: ClusterRole
      name: cityos-role-tenant-engineer
    subjects:
    - kind: Group
      apiGroup: rbac.authorization.k8s.io
      # cityos-tenant-<YourTeamName>-engineers
      name: aad:<EngineerObjectID>
    ```

3. Create your Flux kustomization file in [`infra/k8s/dev/_core/namespaces/`](https://github.com/wp-wcm/city/tree/main/infra/k8s/dev/_core/namespaces) folder.

    Name of the file: `<YourNamespace>.yaml` and copy paste below content.

    ```yaml
    # filename: <YourNamespace>.yaml
    apiVersion: kustomize.toolkit.fluxcd.io/v1beta2
    kind: Kustomization
    metadata:
      name: <YourNamespace>-kustomization
    spec:
      interval: 1m0s
      path: ./infra/k8s/dev/<YourNamespace>
      prune: true
      serviceAccountName: flux-tenant-lps
      sourceRef:
        kind: GitRepository
        name: git-source-city
      targetNamespace: <YourNamespace>
      postBuild:
        substituteFrom:
          - kind: ConfigMap
            name: cluster-vars
    ```

    Now, you need to add this file name `<YourNamespace>.yaml` in `kustomization.yaml` file present in the same folder as shown below:

    ```yaml
    # filename: kustomization.yaml
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    namespace: flux-tenant-lps
    resources:
      - <YourNamespace>.yaml         # Please follow alphabetical order while inserting your file
      - ac-access-control.yaml
      - ac-reg-visitor-personal.yaml
    ```

4. Create a folder under [`infra/k8s/dev/`](https://github.com/wp-wcm/city/tree/main/infra/k8s/dev/) with following contents

    Name of the folder: `<YourNamespace>`

    Create a `Kustomization.yaml` file under your newly created folder and paste the following code

    ```yaml
    # filename: kustomization.yaml
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    resources: []
    ```

With these changes you can create a PR and request the infra team to review your PR. Slack channel : [`#wcm-org-agora-infra`](https://toyotaglobal.enterprise.slack.com/archives/C02USLDU1U3) The file changes for creating a `namespace` looks something like this: <https://github.com/wp-wcm/city/pull/6113/files>

### 4. Configure access to the `dev` cluster

The final step is to set up access to the `dev` cluster.

First, merge [`kubeconfig`](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/kubeconfig) into `~/.kube/config` using these commands:

```sh
# Move to infrastructure/k8s/dev/ and merge kubeconfig into ~/.kube/config
cd <...>/dev
export KUBECONFIG=${HOME}/.kube/config:$(pwd)/kubeconfig
mkdir -p ~/.kube
kubectl config view --flatten > ~/.kube/config.new
mv ~/.kube/config ~/.kube/config.bak
mv ~/.kube/config.new ~/.kube/config
```

Switch to the `dev` context using kubectl:

```sh
kubectl config use-context dev
```

!!! Tip
    You can also use the command `kubectx dev` in kubectx if you have it installed.

!!! Tip
    If you are not in the office network, make sure to configure your GlobalProtect settings to use `imras.ts.tri-ad.global` as the gateway for the company VPN.

Next, make an API request with kubectl:

```sh
kubectl get serviceaccount -n <YourNamespace>
```

You will see a login prompt. After logging in, you should get the following output:

```sh
$ kubectl get sa -n <YourTeamName>
NAME      SECRETS   AGE
default   1         31d
```

## Conclusion

Now you have learned how to create a namespace in Agora's Kubernetes environment. For more information, see:

* [Kubernetes Documentation: Namespaces](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)
* [Getting started with Agora](README.md)
* [Namespaces 101](/docs/default/Component/namespace-tutorial/en/00_index/)

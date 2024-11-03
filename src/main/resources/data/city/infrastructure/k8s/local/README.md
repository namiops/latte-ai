Local Cluster
=============

The local cluster is meant to be a personal development cluster running in minikube (or similar kube-in-a-box tools)

- [Local Cluster](#local-cluster)
  - [Bootstrap FluxCD](#bootstrap-fluxcd)
  - [Deploy your application](#deploy-your-application)
    - [Example](#example)
  - [FluxCD operation](#fluxcd-operation)
  - [Set up authentication with Azure AD (Optional)](#set-up-authentication-with-azure-ad-optional)
    - [Requirements](#requirements)
    - [Start minkikube with Open ID Connect (OIDC) enabled](#start-minkikube-with-open-id-connect-oidc-enabled)
    - [Add a new context in kubeconfig](#add-a-new-context-in-kubeconfig)
    - [Verify your permissions](#verify-your-permissions)
  - [Notes](#notes)

## Bootstrap FluxCD

Update [the GitRepository definition](flux-system/sources/git-city.yaml) to specify the target branch that you're currently using your development.
```
  ref:
    branch: main
```

Unlike our production clusters we use the HTTPS protocol to sync the cityos repository.
This allows a developer to use a [GitHub Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token) to sync the Git repository.

```
export GITHUB_USERNAME=<your_username>
export GITHUB_TOKEN=<your_token>
```

Check whether or not minikube is already running.
```
minikube status
```

If minikube is already running, you can delete it.
```
minikube delete
```

Developers need to run the bootstrap script on a supported Linux system, i.e. on a Linux laptop, a Linux development PC, or on an [AWS EC2 Dev VM](https://developer.woven-city.toyota/docs/default/domain/agora-domain/agora_developers/development_environment/01_setting_up_ec2_dev_vm) (a developer instance on AWS VPC).

Change into the directory of your git checkout that has this README.MD.
```
cd ~/git/cityos/infrastructure/k8s/local
```

Run the [bin/bootstrap](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/bin/bootstrap) script.
```
bin/bootstrap
```

Memory and number of CPUs can be optionally specified.
```
bin/bootstrap 4 15827
```

The [bin/bootstrap](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/bin/bootstrap) script will:
  - start minikube if it is not already running
  - install FluxCD CRDs and controller deployments
  - register infrastructure/k8s/local as the entrypoint for the pipeline.
  - create the necessary secret for syncing the git repository

## Deploy your application

Now, you can deploy your application to your local cluster through the FluxCD
pipeline.

Create a folder under infrastructure/k8s/local/ to store your deployment manifests and add an entry referring it into
[flux-system/kustomizations/services/kustomization.yaml](flux-system/kustomizations/services/kustomization.yaml).

### Example

You added the folder *infrastructure/k8s/local/mytool.*

Add the following to infrastructure/k8s/local/flux-system/kustomizations/services/kustomization.yaml:
```yaml
 resources:
- ../../../mytool
```

*Note that you need to push your branch to github for FluxCD to be able to pick it up*

## FluxCD operation

You can view flux sources information with the commands below. These will display information about the repository flux will be fetching from.

To view the City OS git source
```
flux get sources git city
```
And to view the git repository information in detail
```
kubectl describe -n flux-system gitrepositories city
```

The commands below will display information on flux 'kustomizations', which are basically pipelines for deploying services, as defined kubernetes manifest files.

To view the list of kustomizations
```
flux get kustomizations
```
Or to watch them
```
watch flux get kustomizations
```
To dump out kustomization details (replacing <name> with a name from the above command)
```
kubectl describe -n flux-system kustomizations <name>
```

## Set up authentication with Azure AD (Optional)

This instruction is for those who would like to replicate the authentication setting of Lab/Dev clusters on your local cluster. 
It simulates how your AAD user can interact with Lab/Dev clusters. Refer to [Kubernetes OpenID Connect](https://blog.microfast.ch/kubernetes-openid-connect-3883043f0e94) if you're interested in the authentication flow.

### Requirements
- [kubelogin](https://github.com/int128/kubelogin)

### Start minkikube with Open ID Connect (OIDC) enabled

Here in this tutorial, we start minikube with OIDC enabled and Azure AD is used as an identity provider (IdP).
We use the same Azure Enterprise Application [CityOS Kubernetes API - dev](https://portal.azure.com/#view/Microsoft_AAD_IAM/ManagedAppMenuBlade/~/Overview/objectId/ea55fe24-1a2d-488d-b8cc-9f0ba12ce307/appId/f3570a5e-edb2-4e67-9fe9-a92cbc267c6c/preferredSingleSignOnMode~/null) as one we're using for Lab/Dev clusters.

**Warning:** If you are plan to use another Azure Enterprise Application, make sure that your AAD group is added into that Enterprise Application. Otherwise, K8S API server cannot recognize your group membership.

1. Please get `Application (client) ID` and `Directory (tenant) ID` from Azure App registrations [CityOS Kubernetes API - dev](https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/~/Overview/appId/f3570a5e-edb2-4e67-9fe9-a92cbc267c6c/isMSAApp~/false) and replace them in the below commands.
```bash
export AAD_APP_ID=<your Application (client) ID>
export AAD_TENANT_ID=<your Directory (tenant) ID>
minikube start \
    --kubernetes-version="1.21.14" \
    --extra-config=apiserver.oidc-client-id="${AAD_APP_ID}" \
    --extra-config=apiserver.oidc-issuer-url="https://sts.windows.net/${AAD_TENANT_ID}/" \
    --extra-config=apiserver.authorization-mode=Node,RBAC \
    --extra-config=apiserver.oidc-username-claim="upn" \
    --extra-config=apiserver.oidc-username-prefix="aad:" \
    --extra-config=apiserver.oidc-groups-claim="groups" \
    --extra-config=apiserver.oidc-groups-prefix="aad:"
```
2. Follow steps in [Bootstrap FluxCD](#bootstrap-fluxcd) so that we have some resources to play with.
```
bin/bootstrap
```
3. Manually, deploy RBAC resources from Lab or Dev cluser. Currently, RBAC resources such as `cityos-role-platform-engineer` ClusterRole are not available in a local cluster.
```
kubectl apply -k ../dev/cityos-system/rbac
```
4. Make sure ClusterRoles are deployed successfully.
```
kubectl get clusterroles cityos-role-platform-engineer
NAME                            CREATED AT
cityos-role-platform-engineer   2022-12-01T04:48:54Z
```

### Add a new context in kubeconfig
By default, minikube generates a kubeconfig file at `~/.kube/config` and the default context inside it uses basic certificate authentiction.
With the default context, `cluster-admin` cluster role is granted to you.
We create a new context to allow us to authenticate with Azure AD instead. 

1. In the `contexts` section at `~/.kube/config`, add the following config:
```yaml
- context:
    cluster: minikube
    namespace: default
    user: aaduser
  name: aaduser
```

2. Then, replace `<your Application (client) ID>` and `<your Directory (tenant) ID>`, and add the following config in the `users` section:
```yaml
- name: aaduser
  user:
    exec:
      apiVersion: client.authentication.k8s.io/v1beta1
      args:
      - get-token
      - --environment
      - AzurePublicCloud
      - --server-id
      - <your Application (client) ID>
      - --client-id
      - <your Application (client) ID>
      - --tenant-id
      - <your Directory (tenant) ID>
      command: kubelogin
      env: null
      interactiveMode: IfAvailable
      provideClusterInfo: false
```

### Verify your permissions

At the first time you login, you will be prompted to Azure's login page.
Then, run the below commands to verify your set of permissions:
- Test if you can perform a specific action.
```bash
kubectl --context=aaduser auth can-i get pod -n flux-system
yes
```
- Get a set of permissions in a namespace
```bash
kubectl --context=aaduser auth can-i -n flux-system --list
Resources                          Non-Resource URLs   Resource Names  Verbs
pods/exec                          []                  []              [create list get]
sessions.workspace.maistra.io      []                  []              [create delete patch update get list watch]
pods/portforward                   []                  []              [create list get]
secrets                            []                  []              [create update delete patch get watch list]
...
```

## Notes

Do not push
[flux-system/kustomizations/services/kustomization.yaml](flux-system/kustomizations/services/kustomization.yaml)
and [flux-system/sources/git-city.yaml](flux-system/sources/git-city.yaml)
to the **main** branch with your local specific descriptions.

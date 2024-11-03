# Flux Runbook
<!-- TOC -->

- [Flux Runbook](#flux-runbook)
    - [Description of Flux](#description-of-flux)
    - [Lifecycle Management](#lifecycle-management)
        - [Upgrade](#upgrade)
    - [Debugging Options](#debugging-options)
        - [Known Issues](#known-issues)
        - [Other Links](#other-links)

<!-- /TOC -->

| System  | Owner                              | Path to system                                               | System Enabled in Environments |
| ------- | ---------------------------------- | ------------------------------------------------------------ | ------------------------------ |
| Flux CD | City Platform, City OS, Infra Team | [/infrastructure/k8s/common/flux-system](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/common/flux-system/) | Local, lab, ci, dev            |      |

## Description of Flux

> Flux is a tool for keeping Kubernetes clusters in sync with sources of configuration (like Git repositories), and automating updates to the configuration when there is new code to deploy.
>
> Flux version 2 ("v2") is built from the ground up to use Kubernetes' API extension system, and to integrate with Prometheus and other core components of the Kubernetes ecosystem. In version 2, Flux supports multi-tenancy and support for syncing an arbitrary number of Git repositories, among other long-requested features.
>
> Flux v2 is constructed with the [GitOps Toolkit](https://github.com/fluxcd/flux2#gitops-toolkit), a set of composable APIs and specialized tools for building Continuous Delivery on top of Kubernetes.
>
> Flux is a Cloud Native Computing Foundation ([CNCF](https://www.cncf.io/)) project.

Source: https://github.com/fluxcd/flux2#readme

## Life Cycle Management

### Upgrade

1. Find the new version on [Releases Page](https://github.com/fluxcd/flux2/releases/)
2. Updating Bazel
  - find similar lines below in `infrastructure/tools/k8s-tools/tools.bzl`:
```
    http_archive(
        name = "k8s_tools_flux",
        urls = [
            "https://github.com/fluxcd/flux2/releases/download/vVERSION/flux_VERSION.2_linux_amd64.tar.gz",
        ],
        sha256 = "DIGEST",
        build_file_content = """
```
  - Adjust `urls` and `sha256` to the new version.
  - Please remember to upgrade also for MacOS
3. Update common manifest (run in new manifest folder)
  - Run script under `infrastructure/k8s/common/flux-system/bin/` 
    - Please ensure to specify right agora revision and version: `./import.sh -t fluxcd -n fluxcd -r agoraREVISION -v VERSION`
    - Please note script could be run only on MacOS or Linux machines
  - Make sure no breaking changes (run in `infrastructure/k8s/common/flux-system`)
```bash
diff -rup fluxcd-OLD_VERSION-agoraREVISION fluxcd-NEW_VERSION-oldagoraREVISION
```
4. Update local cluster: adjust this line in `infrastructure/k8s/local/flux-system/base/kustomization.yaml` to the new version
```yaml
resources:
../../common/flux-system/fluxcd-VERSION-agoraREVISION
```
5. After testing proceed with upgrades on the other environments
  - Please remember about our principle to upgrade one environment per day
## Debugging Options

### Known Issues

#### Variable substitution not working as expected

If you want to have variables (typically those in cluster-vars.yaml, often \${agora\_environment})  substituted by FluxCD, remember to add a section like this:

Example, if you want to substitute variables from the cluster-vars.yaml file:

```yaml
  postBuild:
    substituteFrom:
      - kind: ConfigMap
        name: cluster-vars
```
### Other Links

## Developing in a FluxCD managed environment

### Switch GIT source for part of the tree (INFRA only)
***The following is for use by the INFRA-team only and should probably not be used at all.***

If you are developing something that requires lots of changes and cannot
test locally, and you don't want to deploy manually but don't want to go
through the whole PR / test cycle, this is an alternative for you.

### Note of caution
It is possible to break the cluster using this approach. Therefore :
  - Do not change the git source for any of the core namespaces like any system service or a shared namespace.
  - Your branch is not write-protected. It is possible that somebody could overwrite your branch and that this change then gets applied.
  - Raise awareness. Ping infra-team members about this change (slack).

### Setup
  - Add a custom git source in flux-system/sources/ (copy git-cityos.yaml and modify)
    - Change the branch from "main" to whatever branch you are working on
  - Change sourceRef.name in the respective flux-system/kustomizations/\*\* file(s)
  - Commit this change to main, then for the rest of the time, you can work on your custom branch.
  - Obviously this only works, when you are the only one working on this part of the NS.
  - Only do this on LAB.
  - Don't forget to revert your change when you are done.


## How to bootstrap a clusters

### Create Github app (one time setup only)

**This step should be one time only operation and written for informational purpose.**

1. [Create and install Github App](https://docs.github.com/en/apps/creating-github-apps/creating-github-apps/creating-a-github-app) and give proper permissions
   - Repositories Permission
     - Administration (write)
     - Contents (write)
     - Pull Requests (write)
1. Go to the [App](https://github.com/organizations/wp-wcm/settings/apps/city-fluxcd) page
1. Generate private key for the next step. On General tab, scroll down to the Private keys section and click `Generate a private key`.
   - We already create a private key for Flux authentication and stored it on LastPass (Agora Infra shared folder). If you don't have access, please contact any infra member to give you access. 

### Prepare Github secrets

Flux support multiple ways to [authenticate to Github](https://fluxcd.io/flux/cmd/flux_create_secret_git/). In Agora we are using Github App private key for flux. We have created the app from previous step and can be accessed from [here](https://github.com/organizations/wp-wcm/settings/apps/city-fluxcd).


1. Go to LastPass Agora Infra shared folder and save the `FluxCD github app private key` to your machine
1. Create k8s secrets (make sure you are in the correct k8s context and namespace) 
   ```
   flux create secret git git-city-private-key --url ssh://git@github.com/wp-wcm/city --private-key-file=./private.pem
   ```
   The command will return you a ssh public key and will be use in the next step (store it). 

1. Create `GitRepository` object and use the secret from previous step
   ```yaml
    apiVersion: source.toolkit.fluxcd.io/v1beta1
    kind: GitRepository
      metadata:
        name: city
        namespace: flux-system
      spec:
        interval: 1m0s
        ref:
          branch: "main"
        secretRef:
          name: git-city-private-key
        url: ssh://git@github.com/wp-wcm/city
   ```

### Use Github App credentials to create deploy keys to repository

**This step should be one time only per repository**

When we executed `flux create secret git` command from previous step, it will returning the public key we should add it to the repository deploy keys.

You can add the deploy keys from the UI, but it will bind your accounts and the commit flux made will be associated with your account ([like this](https://woven-by-toyota.slack.com/archives/C02660CMJLT/p1680666729621599)).

1. [Generate a JWT token using the App private key](https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/generating-a-json-web-token-jwt-for-a-github-app).
1. [Generate installation access token](https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/authenticating-as-a-github-app-installation#generating-an-installation-access-token) using JWT token from previous step
   ```
   curl --request POST \
   --url "https://api.github.com/app/installations/35900683/access_tokens" \
   --header "Accept: application/vnd.github+json" \
   --header "Authorization: Bearer $JWT_TOKEN"\
   --header "X-GitHub-Api-Version: 2022-11-28" 
   ```

   `35900683` is the installation id of FluxCD app on wp-wcm org https://github.com/organizations/wp-wcm/settings/installations/35900683
1. [Add deploy key](https://docs.github.com/en/rest/deploy-keys?apiVersion=2022-11-28#create-a-deploy-key) using installation access token
   ```
   curl -L \
   -X POST \
   -H "Accept: application/vnd.github+json" \
   -H "Authorization: Bearer $INSTALLATION_TOKEN"\
   -H "X-GitHub-Api-Version: 2022-11-28" \
   https://api.github.com/repos/wp-wcm/city/keys \
   -d '{"title":"FluxCD","key":"ssh-rsa AAA...","read_only":false}'
   ```

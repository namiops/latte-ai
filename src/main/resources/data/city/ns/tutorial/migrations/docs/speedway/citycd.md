# CityCD

CityCD is Agora's next-gen Continuous Delivery platform.

## New CityCD Project namespace setup

First, make sure you have a namespace configured in Speedway or [request a new one](./README.md#first-step-namespace-creation), then you can get started with the configuration.

For this guide, we will take `tsl` team as example. Your directory name needs to be prefixed with `agora-` for example: `agora-tsl`.

1. Create a directory with the desired name in `infra/k8s/<dir-name>`. Ex: [infra/k8s/agora-tsl](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-tsl)
1. Create the `CityCD` configuration file at `infra/k8s/<dir-name>/citycd.yaml`. Ex: [infra/k8s/agora-tsl/citycd.yaml](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/citycd.yaml)
1. Run `bazel run //infra/k8s:citycd_register`
1. For deploying to `Speedway Dev`, create a folder `infra/k8s/<dir-name>/speedway/dev` and add a kustomization file. For ex: [infra/k8s/agora-tsl/speedway/dev/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/speedway/dev/kustomization.yaml)
1. For deploying to `Speedway Prod`, create a folder `infra/k8s/<dir-name>/speedway/prod` and add a kustomization file. For ex: [infra/k8s/agora-tsl/speedway/prod/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/speedway/prod/kustomization.yaml)

!!! warning
    Your `citycd.yaml` should follow below points:

    - The `name` field **MUST** match the directory name
    - You **SHOULD** add the `hosts` aliases you wish to deploy manifests to
    - You **SHOULD** add image references for enabling the Image Update Automation
    - You **MUST** add `roles`
      - You only need to add your team as the **owner** role
      - Adding other teams as **owner** or **reader** is optional
      - Do _NOT_ add `agora-infrastructure`, `agora-devrel`, `speedway-ns-manager`, or `agora-build` as owner or reader to the roles, unless one of these teams actually owns the namespace!
      - Teams are the same name that is requested for your github team. For example, `agora-data` is the name of the team for `@wp-wcm/agora-data` on github
      - You can also view your assigned teams in ArgoCD's user info page

An example `citycd.yaml` is shown below for `tsl` team. Please configure it accordingly.

<!-- markdownlint-disable-next-line MD046 -->
```yaml title="citycd.yaml"
# $schema: ../../../ns/citycd/schema.yaml
name: agora-tsl
hosts:
  speedway:
    dev:
    prod:
images:
  tsl-delivery-manager:
    image: docker.artifactory-ha.tri-ad.tech/wcm-backend/tsl-delivery-manager
  tsl-facility-manager:
    image: docker.artifactory-ha.tri-ad.tech/wcm-backend/tsl-facility-manager
  tsl-frontend-business:
    image: docker.artifactory-ha.tri-ad.tech/wcm-backend/tsl-frontend-business
  tsl-frontend-resident:
    image: docker.artifactory-ha.tri-ad.tech/wcm-backend/tsl-frontend-resident
  tsl-frontend-residentpost:
    image: docker.artifactory-ha.tri-ad.tech/wcm-backend/tsl-frontend-residentpost
roles:
  - group: tsl-software # this is the github name, e.g. @wp-wcm/tsl-software
    roles:
    - owner
  - group: agora-devrel
    roles:
    - owner
```

[Example PR for tsl team](https://github.com/wp-wcm/city/pull/33940/files)

### What resources are created?

Many resources and argo applications will be created from your `citycd.yaml`. Many of these resources are admin/setup applications that you do not need to worry about.

_Note: This is based on the example above_.

- **Main Manifest Applications** (ArgoCD resources)
  - For every `host: alias` combination, an application will be made to deploy manifests to the namespace
    - Available `host: alias` combinations:
      - `speedway:`
        - `dev`
        - `dev-ml`
        - `prod`
        - `prod-ml`
  - These applications are grouped by `<host>-<alias>` and the real namespace used on the cluster will be the name of the application.
    - E.g. In `Speedway Dev` all namespaces will have `-dev` appended to them. Ex: `agora-tsl-dev`
  - By default all have: `auto sync`, `self-heal`, and `prune` enabled
    - You may disable/enable any of these options at-will
  - Argo Application: [speedway-dev/agora-tsl-dev](https://argocd.agora-dev.w3n.io/applications/citycd-speedway-dev/agora-tsl-dev?resource=)
    - Path: `infra/k8s/agora-tsl/speedway/dev`
  - Argo Application: [speedway-prod/agora-tsl-prod](https://argocd.agora-dev.w3n.io/applications/citycd-speedway-prod/agora-tsl-prod?resource=)
    - Path: `infra/k8s/agora-tsl/speedway/prod`
- **Image Policy**
  - For every `<image>` provided under `images:`field, an `ImagePolicy/ImageRepository` will be automatically created.
  - Here, `images.tsl-delivery-manager` will create the following policy that may be used as shown below:
    - **Full Policy**: Updates the yaml field with the image’s url and tag
      - Example: `{ "$imagepolicy": "citycd-image-updater:agora-tsl.tsl-delivery-manager" }`
    - **Name Policy**: Updates the yaml field with only the image’s url
      - [Example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/speedway/prod/kustomization.yaml#L24): `{ "$imagepolicy": "citycd-image-updater:agora-tsl.tsl-delivery-manager:name" }`
    - **Tag Policy**: Updates the yaml field with only the image’s tag
      - [Example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/speedway/prod/kustomization.yaml#L25): `{ "$imagepolicy": "citycd-image-updater:agora-tsl.tsl-delivery-manager:tag" }`

More configuration will be added over time.

## FAQ

### Kubectl access for vCluster

You can get kubectl access to only your namespace by adding:

1. The `RBAC resource` and
1. `RoleBinding` patch in your `kustomization.yaml` 

as shown below: ([example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/speedway/dev/kustomization.yaml))

```yaml title=".../speedway/dev/kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - sidecar.yaml
  - ../../../common/rbac/rolebinding-edit   <---- This is the RoleBinding
patches:
  - patch: |               <---- This is the patch to add your AAD Group ID
      - op: replace
        path: /subjects/0/name
        value: afd6d213-d3dd-497f-99d6-b22e52eda89d  <---- This is your AAD Group ID
    target:
      kind: RoleBinding
      name: edit-rolebinding
```

- You can add the RBAC resource and patch in both the `~/speedway/dev/kustomization.yaml` and `~/speedway/prod/kustomization.yaml` to get access in both `Speedway Dev` and `Speedway Prod`
- You can find your `AAD Group ID` on the [Azure Groups Dashboard](https://portal.azure.com/#view/Microsoft_AAD_IAM/GroupsManagementMenuBlade/~/AllGroups). Search for your team in the search bar and copy/paste the `Object Id` column.
- You can find the [kubeconfig file for the vCluster](https://github.com/wp-wcm/city/tree/main/infra/kubeconfig) in the monorepo.

***Note*** 

Temporarily, to access the cluster you can download context files from [Here](https://portal.tmc-stargate.com/mtfuji) by clicking on "Kubeconfig" button in top right. 

Also to be able to use different context files together you can use the following commands 

```
export KUBECONFIG=~/.kube/config:/Users/rishant.agarwal/Downloads/non-prod:/Users/rishant.agarwal/Projects/city/infra/kubeconfig/speedway-dev-vcluster-kubeconfig.yaml:/Users/rishant.agarwal/Projects/city/infra/kubeconfig/speedway-prod-vcluster-kubeconfig.yaml
```
** Change above paths and add all different context files you have 

```
 kubectl config get-contexts
 ```

 Output
 ```
 CURRENT   NAME                                                                                                     CLUSTER                                                                                                  AUTHINFO                                                                                                 NAMESPACE
          agora-foodagri-dev-gc-0-apps-ap-northeast-1                                                              gc-0-apps-ap-northeast-1                                                                                 gc-0-apps-ap-northeast-1-pinniped                                                                        agora-foodagri-dev
          agora-foodagri-dev-ml-0-apps-ap-northeast-1                                                              ml-0-apps-ap-northeast-1                                                                                 ml-0-apps-ap-northeast-1-pinniped                                                                        agora-foodagri-dev
          agora-foodagri-stage-gc-0-apps-ap-northeast-1                                                            gc-0-apps-ap-northeast-1                                                                                 gc-0-apps-ap-northeast-1-pinniped                                                                        agora-foodagri-stage
          agora-foodagri-stage-ml-0-apps-ap-northeast-1                                                            ml-0-apps-ap-northeast-1                                                                                 ml-0-apps-ap-northeast-1-pinniped                                                                        agora-foodagri-stage
          dev                                                                                                      dev                                                                                                      cityos-dev-aad-login                                                                                     foodagri
          dev2-mgmt-east                                                                                           dev2-mgmt-east                                                                                           cityos-dev2-mgmt-east-aad-login
          dev2-mgmt-west                                                                                           dev2-mgmt-west                                                                                           cityos-dev2-mgmt-west-aad-login
          dev2-worker1-east                                                                                        dev2-worker1-east                                                                                        cityos-dev2-worker1-east-aad-login                                                                       foodagri-qa
          dev2-worker1-west                                                                                        dev2-worker1-west                                                                                        cityos-dev2-worker1-west-aad-login
          minikube                                                                                                 minikube                                                                                                 minikube                                                                                                 default
          rancher-desktop                                                                                          rancher-desktop                                                                                          rancher-desktop
          vcluster_dev_agora-control-plane-dev_agora-control-plane-dev-gc-0-apps-ap-northeast-1-pinniped           vcluster_dev_agora-control-plane-dev_agora-control-plane-dev-gc-0-apps-ap-northeast-1-pinniped           vcluster_dev_agora-control-plane-dev_agora-control-plane-dev-gc-0-apps-ap-northeast-1-pinniped
          vcluster_prod_agora-control-plane-prod_agora-control-plane-prod-gc-0-apps-prod-ap-northeast-1-pinniped   vcluster_prod_agora-control-plane-prod_agora-control-plane-prod-gc-0-apps-prod-ap-northeast-1-pinniped   vcluster_prod_agora-control-plane-prod_agora-control-plane-prod-gc-0-apps-prod-ap-northeast-1-pinniped
          wcm-cicd-dev-gc-0-apps-ap-northeast-1                                                                    gc-0-apps-ap-northeast-1                                                                                 gc-0-apps-ap-northeast-1-pinniped                                                                        wcm-cicd-dev
          wcm-cicd-stage-gc-0-apps-ap-northeast-1                                                                  gc-0-apps-ap-northeast-1                                                                                 gc-0-apps-ap-northeast-1-pinniped                                                                        wcm-cicd-stage
*         yott-dev-gc-0-apps-ap-northeast-1                                                                        gc-0-apps-ap-northeast-1                                                                                 gc-0-apps-ap-northeast-1-pinniped                                                                        yott-dev
          yott-stage-gc-0-apps-ap-northeast-1                                                                      gc-0-apps-ap-northeast-1                                                                                 gc-0-apps-ap-northeast-1-pinniped                                                                        yott-stage
```

Switch to needed context using following command 

```
kubectl config use-context <CONTEXT-NAME>
```

Access pods using following command
```
 kubectl get pods -n <YOUR NAMESPACE>
```

In case you want to port-forward 
```
kubectl --context <CONTEXT> --as <NS>-admin port-forward pod/xxxx local-port:remote-port
```

### FilterTags and Pattern for ImagePolicy

By default `citycd` creates an `ImagePolicy` with following specs:

```yaml title="ImagePolicy"
apiVersion: image.toolkit.fluxcd.io/v1beta1
kind: ImagePolicy
metadata:
...
  name: agora-vision-ai-playground.cpf-ip-sync
  namespace: citycd-image-updater
spec:
  filterTags:
    extract: $version
    pattern: ^dev-[a-f0-9]+-(?P<version>[0-9]{14})$
  imageRepositoryRef:
    name: agora-vision-ai-playground.cpf-ip-sync
  policy:
    numerical:
      order: asc
```

You can find all your `ImagePolicy`, `ImageRepository` and `ImageUpdateAutomation` on ArgoUI under the Application `<NAMESPACE_NAME>.image-updater`. For example: `agora-vision-ai-playground.image-updater`

If you want to update the pattern for your image tag you can add/update the `policySpec` configuration in your `citycd.yaml`. An [example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-vision-ai-playground/citycd.yaml#L6C1-L12C58) is shown below:

```yaml title="citycd.yaml"
images:
  cpf-ip-sync:
    image: docker.artifactory-ha.tri-ad.tech/vision-ai-cloud/cpf-ip-sync
    policySpec:
      filterTags:
        extract: $version
        pattern: '^dev-[a-f0-9]+-(?P<version>[0-9]{14})$'
```

### Common Resources Directory

In the existing setup, it is common for teams to have a common directory to load base resources for dev, staging, prod environments.
In this new structure we require that common is in your namespace’s directory.
Doing this keeps your manifests closer together, and simplifies code ownership.

An example structure for the `agora-helloworld` namespace would be:

<!-- markdownlint-disable-next-line MD046 -->
```plain
./infra/k8s/
└── agora-helloworld/
    ├── common/
    │   └── kustomization.yaml
    └── speedway/
        ├── dev/
        │   └── kustomization.yaml
        ├── stg/
        │   └── kustomization.yaml
        └── prod/
            └── kustomization.yaml
```

If you have a resource that is shared across many namespaces, then you MAY create a directory in infra/k8s/common, but this will be restricted  for shared manifests that anyone MAY import and use within the monorepo and not for namespace specific resources.

### How to use ArgoCD CLI

!!! note
    The user must be connected to the VPN.

<!-- markdownlint-disable-next-line MD046 -->
```sh linenums="0"
argocd login --sso --grpc-web argocd.agora-dev.w3n.io

# Check if connected
argocd account get-user-info

# Logout
argocd logout argocd.agora-dev.w3n.io
```

### Variable Substitutions

#### What Variables Do I Have?

All available variables can be seen from your Application object in [ArgoCD UI](https://argocd.agora-dev.w3n.io/), go to your application page, click the app and go to parameter tab ([example](https://argocd.agora-dev.w3n.io/applications/citycd-speedway-prod/agora-tsl-prod?resource=&node=argoproj.io%2FApplication%2Fcitycd-speedway-prod%2Fagora-tsl-prod%2F0&tab=parameters))

#### Global Variables

These variables will be available to all ArgoCD applications deploying the namespace manifests for a `CityCD` Workspace. E.g. The application deploying `infra/k8s/agora-my-namespace/speedway/dev` will have the global variables.

The global variables are variables that consist of:

- Static values across all hostaliases, e.g. `COMPANY_NAME=Woven` is the same in speedway-dev and speedway-prod.
- Unique values per hostalias, e.g. `CLUSTER_DOMAIN` is different in `Speedway Dev` and `Speedway Prod`

_**If you need to add a global variable(s), please reach out to @agora-build**_

#### CityCD Workspace Variables

If you have variable that need to be shared only within your `CityCD` workspace, or for specific hostaliases, you can add custom variables to your `citycd.yaml` file. The root `substitution` field sets the variable for all hostaliases, where `substitutions` on a specific hostalias sets it exclusively to that hostalias.

<!-- markdownlint-disable-next-line MD046 -->
```yaml title="citycd.yaml"
name: agora-xx
hosts:
  speedway:
    dev:
    prod:
      substitutions:
        MY_PROD_VAR: my-prod-var-values # only available in the prod env
        STATUS: prod # replace variable defined below only for this env
substitutions:
  STATUS: wip # available in all envs
```

#### Overwriting Variables

The order of importance for variables is:

- Hostalias (e.g. hosts.speedway.prod.substitutions defined in `citycd.yaml`)
- `CityCD` Workspace (e.g. `substitutions` defined in `citycd.yaml`)
- Global Variables

An overwrite occurs when you use the same variable name as an already defined variable.

#### How to use the variable in the k8s manifest

You can provide any variable that is available to your application. The details on our supported functions can be read [here](https://github.com/wp-wcm/city/tree/main/ns/citycd/envsubst#supported-functions)

<!-- markdownlint-disable-next-line MD046 -->
```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: test-service
  labels:
    wheredoiwork: ${COMPANY_NAME} # global variable
    mystatus: ${STATUS} # workspace variable defined in the citycd.yaml
```

### How to setup Slack notifications

ArgoCD will send notifications for these events

- `Application` sync is successful (`info`)
- `Application` sync is fail (`error`)
- `Application` health status are in `Degraded`, `Missing`, or `Unknown` (`error`)

Add `notifications.slack` object to the environment (ex: `hosts.speedway.dev`) you want to set up.

- `channels` - List of channel name without #
  - Optional. If set, notification will be sent to these channels
- `errorChannels` - List of channel name without #
  - Optional. If set, **error** notification will be sent to these channels and **non-error** will be sent to `channels` (if set)
- `mentions` - List of Slack Group ID
  - Optional. If set, **error** notification will mention these groups. Only slack group is supported (no personal mention) and it only accepts Group ID

<!-- markdownlint-disable-next-line MD046 -->
```yaml
name: agora-helloworld
hosts:
  speedway:
    dev:
      notifications:
        slack:
          channels:
            - mynoisychannel
          errorChannels:
            - myalertchannel
          mentions:
            - S065T8EMHHR

# sample configurations

# All notifications sent to channel(s) without mention group(s)
notifications:
  slack:
    channels:
      - mynoisychannel

# Only error notification sent to channel(s) without mention group(s)
notifications:
  slack:
    errorChannels:
      - myalertchannels

# Only error notification sent to channel(s) with mention group(s)
notifications:
  slack:
    errorChannels:
      - myalertchannels
    mentions:
      - S065T8EMHHR

# Separate error and non-eror channels with mention group(s)
notifications:
  slack:
    channels:
      - mynoisychannel
    errorChannels:
      - myalertchannels
    mentions:
      - S065T8EMHHR
```

How to get `Slack Group ID`

- Click your @group on slack
- Click `…` (three dots)
- Copy `group ID`

## Troubleshooting

### "metadata.annotations: Too long" error

This can be seen when deploying CRD.

<!-- markdownlint-disable-next-line MD046 -->
```text linenums="0"
Failed sync attempt to 04fd2b0ffa1cbb8c7883e4640b4f727431e49fd8: one or more
objects failed to apply, reason: CustomResourceDefinition.apiextensions.k8s.io
"redisfailovers.databases.spotahome.com" is invalid: metadata.annotations: Too
long: must have at most 262144 bytes (retried 5 times).
```

To resolve, check the "Replace" checkbox in Sync options in ArgoCD and sync again

### Error syncing to physical cluster

#### "stream error when reading response body"

This is an issue we are facing periodically.

<!-- markdownlint-disable-next-line MD046 -->
```text linenums="0"
Error syncing to physical cluster: stream error when reading response body, may
be caused by closed connection. Please retry. Original error: stream error:
stream ID 104857; NO_ERROR; received from peer
```

To find a solution, please check the other pod log(?)

#### Exceeded quota

If you see the message `exceeded quota: default, requested: limits.cpu=2500m, used: limits.cpu=10, limited: limits.cpu=12`, this could be a false alarm.
It would be better to check the host cluster.

![Screenshot of ArgoCD quota exceeded error](../assets/speedway-city-cd-quota-error.png)

To fix this, create a PR to update the quota like <https://github.tri-ad.tech/TRI-AD/mtfuji-namespaces/pull/1232> (if you didn't specify anything in the dev section, prod ones would be copied to lower envs).

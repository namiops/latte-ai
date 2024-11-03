# Vault Runbook
<!-- TOC -->

- [Vault Runbook](#vault-runbook)
    - [Description of Vault](#description-of-vault)
    - [Lifecycle Management](#lifecycle-management)
        - [Upgrade](#upgrade)
    - [Debugging Options](#debugging-options)
        - [Known Issues](#known-issues)
        - [Other Links](#other-links)

<!-- /TOC -->

| System  | Owner                              | Path to system                                               | System Enabled in Environments |
| ------- | ---------------------------------- | ------------------------------------------------------------ | ------------------------------ |
| Vault agent | Infra Team | [/infrastructure/k8s/common/vault](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/vault/) | Local, lab, ci, dev            |      |

## Description of Vault

TODO

## Life Cycle Management

TODO

### Upgrade

1. The recommended installation method is via the [Vault Helm Chart](https://github.com/hashicorp/vault-helm). Find the release of the helm chart you want to use [on the releases page](https://github.com/hashicorp/vault-helm/releases). We will use the chart version in Step 3.

2. If you are intending to update to a specific version of the Vault Agent Sidecar Injector you must ensure that the helm chart references that version, or pass it in the [infrastructure/k8s/common/vault/bin/vault-agent-injector-values.yaml](infrastructure/k8s/common/vault/bin/vault-agent-injector-values.yaml) like below. Here we're passing version `1.3.0`:

    ```yaml
    injector:
    enabled: true.
    externalVaultAddr: ${external_vault_address}
    authPath: auth/kubernetes
    image:
        repository: docker.artifactory-ha.tri-ad.tech/hashicorp/vault-k8s
        tag: "1.3.0"
    agentImage:
        repository: docker.artifactory-ha.tri-ad.tech/hashicorp/vault
    ```
    If you are not updating the helm chart version but updating the values file, create a new values file with an incremented agoraN revision like `vault-agent-injector-values-agora{REVISION-NUMBER}.yaml` and use this as an input for the following command

3. Run the import script with the required flags `./import -f vault-agent-injector-values.yaml -r agora1 -t vault -v {CHART-VERSION}`. You can view the flag options in the import script. This will run the helm chart and unfurl the output so that it can be applied to the cluster. Ensure the output folder follows the naming convention `vault-agent-injector-{CHART-VERSION}-agora{REVISION-NUMBER}`, for example `vault-agent-injector-0.25.0-agora1`.

4. Raise a PR to merge the outputs to the repo. You SHOULD do this separately to referencing the new version in any of the environments in case it is required to rollback a deployment.

5. Reference the folder from Step 3 in the required environment's kustomize file. To upgrade the DEV environment for example, you'd update [infrastructure/k8s/dev/vault/kustomization.yaml](infrastructure/k8s/dev/vault/kustomization.yaml)
    ```yaml
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    resources:
    - ../../common/vault/vault-agent-injector-{CHART-VERSION}-{REVISION-NUMBER}
    ```

#### Testing the upgrade

Depending on the reason for upgrade, restart a pod that has vault injection enabled, and ensure that it is able to start up correctly. Then, confirm that secrets are loaded correctly into the pod. For example, the vault annotations for Developer Portal are mounting secrets in `/secrets`. We can shell into the pod and see that they have been mounted.

```
ls /secrets
-rw-r--r-- 1 _apt node 2075 Oct 18 01:12 secret-external-github-credentials.yaml
-rw-r--r-- 1 _apt node 2162 Oct 18 01:12 secret-github-credentials.yaml
-rw-r--r-- 1 _apt node  394 Oct 18 01:12 secret-github-oauth-credentials.yaml
```


## Debugging Options

TODO

### Known Issues

TODO

### Other Links

TODO

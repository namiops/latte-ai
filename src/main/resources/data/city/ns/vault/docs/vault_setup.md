# How To Set Up Vault

This document is here to guide you through setup of Vault

## Pre-Requisites

Before you use this document you should do the following:

### Install Vault CLI

You **SHOULD** use the Vault CLI for the purposes of being able to better audit and track any commands made for debugging issues.
To install Vault CLI please refer to the [CLI Quick Start Instructions](https://developer.hashicorp.com/vault/tutorials/getting-started/getting-started-install)

### Request a Namespace

One thing to consider before requesting a new namespace is if you actually need one:

* Vault Namespaces **SHOULD** be scoped per team
* Vault Namespaces **MAY** have secrets for multiple applications
* Team members **MAY** have access to multiple Vault Namespaces

To request a namespace use the **Vault Namespace Request** on the [#wcm-org-agora-ama](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7) channel

![request](assets/vault-request.png)

#### Namespace Patterns

Namespaces are managed by the Woven IT team. Namespace naming conventions are
managed by Woven IT. The following restrictions are on namespaces:

* Letters and numbers only
* A name length between 3 and 252 characters

!!! Note
    For teams that have already been using Vault, you have a new namespace already.
    For details please reach out to DevRel or simply login to the Vault via OIDC to see your new namespace

## Using Vault CLI

For your setup and use of the CLI tool, it helps to have a few environment variables you can refer to.
For your Vault Address variable (`VAULT_ADDR`) refer to the table:

| Environment    | Vault Address (VAULT_ADDR)           |
|----------------|--------------------------------------|
| Non-Production | <https://dev.vault.tmc-stargate.com> |
| Production     | <https://vault.tmc-stargate.com>     |

=== "MacOs/Linux"

    ```shell
    # Vault Address
    export VAULT_ADDR=https://dev.vault.tmc-stargate.com

    # Vault Namespace
    # Needs to be unset when logging in, and then set when you want to read a path
    export VAULT_NAMESPACE="" 
    ```

=== "Windows PowerShell"

    ```shell
    # Vault Address
    env:VAULT_ADDR = 'https://dev.vault.tmc-stargate.com'

    # Vault Namespace
    # Needs to be unset when logging in, and then set when you want to read a path
    env:VAULT_NAMESPACE = ''
    ```

After setting this up you can then run the following command to log in

```shell
$ vault login -method=oidc -path=gac
Complete the login via your OIDC provider. Launching browser to:

    https://login.microsoftonline.com/...


Waiting for OIDC authentication to complete...
Success! You are now authenticated. The token information displayed below
is already stored in the token helper. You do NOT need to run "vault login"
again. Future Vault requests will automatically use this token.

Key                  Value
---                  -----
token                ...
token_accessor       ...
token_duration       1h
token_renewable      true
token_policies       ["default"]
identity_policies    []
policies             ["default"]
token_meta_role      default
```

After this you can then set `VAULT_NAMESPACE` to your namespace

=== "MacOs/Linux"

    ```shell
    export VAULT_NAMESPACE="ns_stargate/<my_namespace>" 
    ```

=== "Windows PowerShell"

    ```shell
    env:VAULT_NAMESPACE = 'ns_stargate/<my_namespace>'
    ```

## Set Up A Vault Secret Engine

Run the following command via th CLI to enable the KV-V2 Engine:

```shell
$ vault secrets enable -path=<PATH> kv-v2
Success! Enabled the kv-v2 secrets engine at: <PATH>/
```

!!! Note
    Use snake_case for `PATH`

Run the following command via the CLI to mount a secret into the secret engine

```shell
$ vault kv put -mount=<PATH> <MY_SECRET_NAME> \
<MY_SECRET_KEY_1>=12345678 \
<MY_SECRET_KEY_2>="abcdefg"
```

!!! Note
    Use snake_case for MY_SECRET_NAME and MY_SECRET_KEY

To update or add additional keys you can run something like the following:

```shell
$ vault kv patch -mount=<PATH> <MY_SECRET_NAME> \ 
<MY_SECRET_KEY_3>="AB12CD34"
```

## Set Up A Vault Policy

Run the following command via the CLI to create a policy `my_policy` that allows `read` capability to the paths `kv-my-environment/my_secret_name` and `kv-my-environment/data/my_secret_name`

```shell
$ vault policy write "<POLICY_NAME>" - <<EOF
path "<PATH>/<MY_SECRET_NAME>" {
  capabilities = [ "read" ]
}
path "<PATH>/data/<MY_SECRET_NAME>" {
  capabilities = [ "read" ]
}
EOF
Success! Uploaded policy: <POLICY_NAME>
```

!!! Note
    Use snake_case for POLICY_NAME

## Set Up Vault Authentication

### 1. Find your Cluster and Issuer

You can find this in one of two ways:

* If using Preproduction, refer to the [Infra Team's Document](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/README.md)
* If using Speedway
  * To find the **Cluster Name** refer to the list on the [Stargate Portal](https://portal.tmc-stargate.com/mtfuji)
  * To find the **OIDC URL** for the Cluster refer to the [Mt. Fuji Documentation](https://mtfuji-docs.prod.stargateapp.toyota/documentation/features/service-account-oidc/)

Some common clusters are as follows:

| Cluster Name                  | Environment    | Description               | OIDC URL                                                                            |
|-------------------------------|----------------|---------------------------|-------------------------------------------------------------------------------------|
| gc-0-apps-ap-northeast-1      | Non-production | General Compute Cluster   | <https://oidc.eks.ap-northeast-1.amazonaws.com/id/F11A5118565811C24A9FE43601C7323D> |
| gc-0-apps-prod-ap-northeast-1 | Production     | General Compute Cluster   | <https://oidc.eks.ap-northeast-1.amazonaws.com/id/B74F6B2B9B1A56C04FC1C8817F02A3B4> |
| ml-0-apps-ap-northeast-1      | Non-production | Machine Learning Cluster  | <https://oidc.eks.ap-northeast-1.amazonaws.com/id/735A402FBC10842ADDE727E20F91BB24> |
| ml-0-apps-prod-ap-northeast-1 | Production     | Machine Learning Cluster  | <https://oidc.eks.ap-northeast-1.amazonaws.com/id/0B4727BDF0D14106B422F5F239AB53BC> |

Once found perform the following to set up local variables:

```shell
export CLUSTER_NAME=<MY_CLUSTER_NAME>
export VAULT_JWT_PATH=jwt_$CLUSTER_NAME
export ISSUER=<OIDC_URL>
```

!!! Note
    Use snake_case for MY_CLUSTER_NAME by converting the name (e.g. `gc-0-apps-ap-northeast-1` -> `gc_0_apps_ap_northeast_1`)

### 2. Set up your Service Account and Namespace

When you know your namespace and the name of your service account perform the following:

```shell
export K8S_NAMESPACE=<NAMESPACE>
export SA_NAME=<NAMESPACE>-secrets
```

### 3. Set up the Auth Engine

Perform the following to enable and configure the JWT Engine

```shell
vault auth enable -path=$VAULT_JWT_PATH jwt
vault write auth/$VAULT_JWT_PATH/config oidc_discovery_url="${ISSUER}"
```

### 4. Set up the Auth Engine Role

Run the following to associate a role for your JWT Auth Engine:

```shell
$ vault write auth/$VAULT_JWT_PATH/role/read_all_<K8S_NAMESPACE> \
role_type="jwt" \
bound_audiences="vault" \
user_claim_json_pointer=true \
user_claim="/kubernetes.io/namespace" \
bound_subject="system:serviceaccount:${K8S_NAMESPACE}:${SA_NAME}" \
policies="default,<POLICY_NAME>" \
ttl="1h"
```

!!! Note
    Use snake_case for POLICY_NAME and K8S_NAMESPACE after `read_all` (e.g `read_all_my-cool-namespace` -> `read_all_my_cool_namespace`)

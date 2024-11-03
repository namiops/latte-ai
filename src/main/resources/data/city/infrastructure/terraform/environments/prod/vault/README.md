# Vault IaC

For more general information, best practice, and Vault CLI, refer to [Hashicorp Vault in Agora](../../../../../ns/vault/README.md).

* [Vault IaC](#vault-iac)
  * [Terraform Folder Structure](#terraform-folder-structure)
  * [Location of Terraform states](#location-of-terraform-states)
  * [Important changes in Vault for SMC workloads](#important-changes-in-vault-for-smc-workloads)
    * [KV Engine](#kv-engine)
  * [Vault CLI Authentication](#vault-cli-authentication)
  * [Manually Terragrunt Apply](#manually-terragrunt-apply)
  * [Authentication engine for SMC clusters](#authentication-engine-for-smc-clusters)
  * [Best Practices](#best-practices)
    * [KV Engine Structure](#kv-engine-structure)
  * [Migration steps after a CD pipeline is operational](#migration-steps-after-a-cd-pipeline-is-operational)

## Terraform Folder Structure

Due to Woven Security team's recommendation, we have separated Vault namespaces per Agora area.
Then, Vault IaC folders are divided per Vault namespaces as listed in [Location of Terraform states](#location-of-terraform-states).


**NOTE:** Agora Infra has allowed each Agora area to apply their Vault terraform code by themselves before a CD pipeline is available. Check out the steps at [Manually Terragrunt Apply](#manually-terragrunt-apply).


## Location of Terraform states

Before a Terraform CD pipeline is operational, terraform states for each namespace are stored separately on the AWS account that Vault namespace owner has access.

Once the CD pipeline is operational, Agora Infra will migrate these state files to `wcm-agora-tfstate-prod` bucket.

The table below shows details of each team's Vault IaC folder and its terraform state location. The AdminisitorAccess role on a corresponding account is required to apply Vault IaC.

| Team folder | Vault Namespace                   | S3 Bucket                                | AWS Account Profile    |
| ----------- | --------------------------------- | ---------------------------------------- | ---------------------- |
| cicd        | TBD                               | wcm-agora-tfstate-prod                   | prod-transit           |
| id          | ns_stargate/ns_prod_agoraid       | wcm-agora-tfstate-prod-platform-internal | prod-platform-internal |
| infra       | ns_stargate/ns_prod_agorainfra    | wcm-agora-tfstate-prod                   | prod-transit           |
| data        | TBD                               | wcm-agora-tfstate-prod-storage-valet     | prod-storage-valet     |
| devrel      | ns_stargate/ns_prod_agoradevrel   | wcm-agora-tfstate-prod-platform-internal | prod-platform-internal |
| services    | ns_stargate/ns_prod_agoraservices | wcm-agora-tfstate-prod-platform-internal | prod-platform-internal |

**NOTE:** The related Vault configuration on Stargate is available at [Woven City - Shared](https://portal.tmc-stargate.com/projects/76)

## Important changes in Vault for SMC workloads

Unlike Agora pre-prod environment, SMC clusters don't support Kubernetes Auth Backend. See more details on [SMC official doc - Vault JWT](https://portal.tmc-stargate.com/docs/default/Component/STARGATE-WELCOME-GUIDES/stargate-multicloud/documentation/features/service-account-oidc/vault-jwt/).
In addition, the naming convention of Agora namespaces has been changed to `agora-<namespace>-<env>`. 
Then, we need to migrate Vault configurations from Agora pre-prod to SMC environments.

### KV Engine

Then, to migrate Vault IaC for a KV engine and Vault roles from Agora pre-prod to Agora prod environment, we need to:

1. Create a new JWT auth backend per SMC cluster. Agora Infra has prepared a new module [agora_vault_jwt_auth_bootstrap](../../../modules/agora_vault_jwt_auth_bootstrap). Deploy it to your Vault namespace to let your workloads on SMC authenticate with your Vault namespace.
  
   1. Example: module `smc_eks_gc_0_east_vault_agorainfra_boostrap` at [infra/main.tf](./infra/main.tf)

2.  Create a new Vault role for the new JWT backend above. Agora Infra has prepared a new module [agora_vault_jwt_backend_role/kv_engine_read_only](../../../modules/agora_vault_jwt_backend_role/kv_engine_read_only) for a simple use case (a read-only role for secret paths in a KV engine).

    1.  Example: module `smc_eks_gc_0_east_vault_kserve_test_backend` at [infra/smc_eks_gc_0_east-role.tf](./infra/smc_eks_gc_0_east-vault.tf)
    2.  **NOTE:** In case you would like to write your module, make sure to use `vault_jwt_auth_backend_role` instead and not to copy a Kubernetes backend role from your old module.

## Vault CLI Authentication

1. Authenticate with Vault using the root namespace. We MUST NOT set `VAULT_NAMESPACE` at this step.

```sh
# This needs to be unset if your session expires and you need to login again
unset VAULT_NAMESPACE
unset VAULT_TOKEN
export VAULT_ADDR=https://vault.tmc-stargate.com
vault login -method=oidc -path=gac
export VAULT_TOKEN=$(cat ~/.vault-token)
```

2. Set an env variable with your team namespace. You can refer to available Vault namespaces in [common.yaml](./common.yaml) at `vault.namespaces` key. Note that this step is not required for running `terraform apply`, since Vault address and namespace have been configured in `provider.tf` file.

```sh
export VAULT_NAMESPACE="ns_stargate/ns_prod_agora<YOUR_TEAM>"
```

For example, 

```sh
export VAULT_NAMESPACE="ns_stargate/ns_prod_agorainfra"
```

## Manually Terragrunt Apply

1. Authenticate with Prod Vault. See steps in [Vault CLI Authentication](./#vault-cli-authentication).
2. Run AWS SSO login with the AWS profile set for your Vault namespace, as shown in [Location of Terraform states](#location-of-terraform-states). For example, to modify IaC for `ns_stargate/ns_prod_agoraservices`:
   1. Copy AWS profiles from [prod/aws_config.ini](../aws_config.ini) to your `~/.aws/config` file.
   2. Run `aws sso login --profile prod-platform-internal`
3. Go to your team's Vault folder
4. Check terraform plan

```bash
export AWS_PROFILE=<profile_name>
terragrunt init
terragrunt plan
```

5. Apply the change

```bash
terragrunt apply
```

## Authentication engine for SMC clusters

**NOTE:** For setting up a JWT auth engine by Vault CLI, see [Vault Setup - Setting up a JWT Authentication Engine](../../../../../ns/vault/docs/vault_setup.md#setting-up-a-jwt-authentication-engine)

In the production environment, we have access to two clusters on SMC generally:
- EKS gc-0 ap-northeast-1 (General computing cluster)
- EKS ml-0 ap-northeast-1 (GPU cluster)

We need to create JWT Auth Methods separately for each cluster.
It is recommended to create the JWT Auth method and roles in a separate file per cluster.
For example:

- `smc_eks_gc_0_east-vault.tf`: Deploy Vault resources for EKS gc-0 ap-northeast-1
- `smc_eks_ml_0_east-vault.tf`: Deploy Vault resources for EKS ml-0 ap-northeast-1

## Best Practices

### KV Engine Structure

Each Agora area is responsible for setting up their KV engine and secrets. You are free to design the path and tree structure of the KV engine.

Here is the recommendation for a secret structure.

1. Create 1 KV engine per 1 environment by [agora_vault_kv_engine/agora_ns](../../../modules/agora_vault_kv_engine/agora_ns). For example, `kv-prod`.
2. Make a path separate secrets per cluster:
   1. `smc-eks-gc-0-east`
   2. `smc-eks-ml-0-east`

See the below example. Note that token, username, and password are secret keys.

```
kv-prod
├── smc-eks-gc-0-east
│ ├── knative
│ │   └── token
│ └── kserve-test-cpu
│     ├── username
│     └── password
└── smc-eks-ml-0-east
  ├── knative
  │   └── token
  └── kserve-test-gpu
      ├── username
      └── password
```

**NOTE:** Alternative structures can be found at [Recommended Best Practices For Vault Use - Organize Secrets Via A Schema](../../../../../ns/vault/docs/best_practices.md#organize-secrets-via-a-schema).

## Migration steps after a CD pipeline is operational

TLDR; Each team is required to manually create Vault role and policy to let CI runners manage Vault resources.

TBD

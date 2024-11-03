# Agora Vault CI JWT Auth Engine

This module sets up JWT auth engine and role for Agora GitHub runners to automate your Vault namespace.

## Usage

**Note:** This module is for Agora use only.

1. Login to Vault via Vault CLI: `vault login -method=oidc -path=gac`
2. Set the `VAULT_TOKEN` as an environment variable
3. Replace `namespace = SET_NAMESPACE` with your namespace no trailing slashes (
   example: `ns_stargate/wcmshrd_agoradevrel`)
4. Run `terraform init`
5. Run `terraform plan` setting the `env` to the desired environment
6. Run `terraform apply` and confirm the configuration

## Requirements

| Name      | Version |
|-----------|---------|
| Terraform | >= 0.13 |

## Providers

| Name            | Version  |
|-----------------|----------|
| Hashicorp Vault | >= 3.5.0 |

## Modules

None

## Resources

| Name                        | Description                                                                                                                   | Type        | Default | Required |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------|-------------|---------|----------|
| vault_jwt_auth_backend      | The [JWT Engine](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/jwt_auth_backend)              | Resource    | null    | yes      |
| vault_policy_document       | The [Vault Policy Document](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/policy_document) | Data Source | null    | yes      |
| vault_policy                | The [Vault Policy](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy)                      | Resource    | null    | yes      |
| vault_jwt_auth_backend_role | The [JWT Auth Role](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/jwt_auth_backend_role)      | Resource    | null    | yes      | 

## Inputs

| Name        | Description      | Type     | Default | Required |
|-------------|------------------|----------|---------|----------|
| environment | Environment name | `string` | null    | yes      |

## Outputs

| Name             | Description                |
|------------------|----------------------------|
| ci_runner_engine | The name of the JWT Engine |

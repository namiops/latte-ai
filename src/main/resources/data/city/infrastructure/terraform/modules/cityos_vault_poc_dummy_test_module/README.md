# City OS Vault POC Dummy Test Module

## Description

This module creates infra for a dummy deployment used for Vault testing and verification

## Requirements

| Name                                   | Version         |
|----------------------------------------|-----------------|
| [terraform](https://www.terraform.io/) | 0.13 or greater |
| [vault](https://www.vaultproject.io/)  | >= 3.5.0        |

## Providers

| Name                                                                         | Version  |
|------------------------------------------------------------------------------|----------|
| [vault](https://registry.terraform.io/providers/hashicorp/vault/latest/docs) | >= 3.5.0 |

## Modules

No modules.

## Resources

| Name                                                                                                                                             | Type        |
|--------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| [vault_policy_document](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/policy_document)                        | data source |
| [vault policy](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy)                                             | resource    |
| [vault_kubernetes_auth_backend_role](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/kubernetes_auth_backend_role) | resource    |
| [vault_mount](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/mount)                                               | resource    |
| [vault_generic_secret](https://registry.terraform.io/providers/hashicorp/vault/3.5.0/docs/resources/generic_secret)                              | resource    |

## Inputs

| Name         | Description                                                                                                                                                                                                                        | Type     | Default | Required |
|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|---------|:--------:|
| environment  | An environment name that will be used in the mount path for kubernetes auth backend                                                                                                                                                | `string` | n/a     |   yes    |
| auth_backend | The kubernetes authentication engine to work with                                                                                                                                                                                  | `string` | n/a     |   yes    |
| engine_path  | The path of the KV-V2 Secret Engine, points to the one made at default by the [cityos_vault_kv_engine module](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/terraform/modules/cityos_vault_kv_engine) | `string` | n/a     |   yes    |

## Outputs

| Name        | Description                                   |
|-------------|-----------------------------------------------|
| policy_name | The name of the policy to attach to roles     |
| role_name   | The name of the role to attaching to accounts |

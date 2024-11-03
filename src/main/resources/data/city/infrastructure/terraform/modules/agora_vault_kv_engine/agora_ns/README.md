# Agora Vault KV Engine with customizable Vault namespace

This module can be automated by Agora GitHub runners.

## Requirements

No requirements.

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_this"></a> [this](#module\_this) | ../ | n/a |

## Resources

No resources.

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_auth_backend"></a> [auth\_backend](#input\_auth\_backend) | The kubernetes authentication engine to work with | `string` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_github_jwt"></a> [github\_jwt](#input\_github\_jwt) | Used to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_mount"></a> [mount](#input\_mount) | Used to specify fwt path to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_vault_address"></a> [vault\_address](#input\_vault\_address) | The Vault address | `string` | n/a | yes |
| <a name="input_vault_namespace"></a> [vault\_namespace](#input\_vault\_namespace) | The Vault namespace | `string` | `"ns_stargate/ns_dev_wcmshrd_agorainfra"` | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_policy_name"></a> [policy\_name](#output\_policy\_name) | The name of the policy to use for attaching to roles |
| <a name="output_role_name"></a> [role\_name](#output\_role\_name) | The name of the role to use for attaching to accounts |

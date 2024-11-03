<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | >= 3.5.0 |

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_agora_vault_kv_engine"></a> [agora\_vault\_kv\_engine](#module\_agora\_vault\_kv\_engine) | ../agora_vault_kv_engine | n/a |

## Resources

No resources.

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_github_jwt"></a> [github\_jwt](#input\_github\_jwt) | Used to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_mount"></a> [mount](#input\_mount) | Used to specify fwt path to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_vault_address"></a> [vault\_address](#input\_vault\_address) | The Vault address | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_kv_engine_path"></a> [kv\_engine\_path](#output\_kv\_engine\_path) | n/a |
<!-- END_TF_DOCS -->

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | >= 3.5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_vault"></a> [vault](#provider\_vault) | >= 3.5.0 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [vault_mount.default](https://registry.terraform.io/providers/hashicorp/vault/3.5.0/docs/resources/mount) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_environment"></a> [environment](#input\_environment) | An environment name that will be used in the mount path for kubernetes auth backend | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_kv_engine_path"></a> [kv\_engine\_path](#output\_kv\_engine\_path) | n/a |

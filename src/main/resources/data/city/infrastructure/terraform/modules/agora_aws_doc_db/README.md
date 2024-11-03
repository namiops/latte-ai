<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | >= 3.5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |
| <a name="provider_vault"></a> [vault](#provider\_vault) | >= 3.5.0 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_docdbelastic_cluster.docdb](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/docdbelastic_cluster) | resource |
| [aws_security_group.allow_27017](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_vpc_security_group_ingress_rule.allow_27017_ipv4](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_security_group_ingress_rule) | resource |
| [aws_vpc_security_group_ingress_rule.allow_27017_ipv6](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_security_group_ingress_rule) | resource |
| [aws_vpc.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/vpc) | data source |
| [vault_kv_secret_v2.password](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/kv_secret_v2) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_admin_user_name"></a> [admin\_user\_name](#input\_admin\_user\_name) | n/a | `string` | `"mongo"` | no |
| <a name="input_admin_user_password_key"></a> [admin\_user\_password\_key](#input\_admin\_user\_password\_key) | Key of the secret in vault | `string` | `"db_password"` | no |
| <a name="input_admin_user_password_name"></a> [admin\_user\_password\_name](#input\_admin\_user\_password\_name) | Full name of the secret in vault | `string` | `"scrat"` | no |
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | n/a | `string` | n/a | yes |
| <a name="input_preferred_maintenance_window"></a> [preferred\_maintenance\_window](#input\_preferred\_maintenance\_window) | n/a | `string` | `"Sat:10:00-Sat:11:00"` | no |
| <a name="input_shard_capacity"></a> [shard\_capacity](#input\_shard\_capacity) | n/a | `number` | `2` | no |
| <a name="input_shard_count"></a> [shard\_count](#input\_shard\_count) | n/a | `number` | `2` | no |
| <a name="input_subnet_ids"></a> [subnet\_ids](#input\_subnet\_ids) | n/a | `list(string)` | n/a | yes |
| <a name="input_vault_address"></a> [vault\_address](#input\_vault\_address) | The Vault address | `string` | n/a | yes |
| <a name="input_vault_github_jwt"></a> [vault\_github\_jwt](#input\_vault\_github\_jwt) | Used to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_vault_mount"></a> [vault\_mount](#input\_vault\_mount) | Secret engine name | `string` | n/a | yes |
| <a name="input_vault_namespace"></a> [vault\_namespace](#input\_vault\_namespace) | The Vault namespace | `string` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | n/a | `string` | n/a | yes |

## Outputs

No outputs.
<!-- END_TF_DOCS -->
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

<!-- BEGIN_TF_DOCS -->
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
| <a name="input_allowed_domains"></a> [allowed\_domains](#input\_allowed\_domains) | Allowed domains for the PKI Secret Backend role | `list(string)` | n/a | yes |
| <a name="input_auth_backend"></a> [auth\_backend](#input\_auth\_backend) | The kubernetes authentication engine to work with | `string` | n/a | yes |
| <a name="input_cert_ttl"></a> [cert\_ttl](#input\_cert\_ttl) | TTL for certificates | `number` | n/a | yes |
| <a name="input_create_cvm_2024_cert"></a> [create\_cvm\_2024\_cert](#input\_create\_cvm\_2024\_cert) | Determines whether to create root cert and role named cvm\_2024 (as part of certificate rotation) | `bool` | `false` | no |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_github_jwt"></a> [github\_jwt](#input\_github\_jwt) | Used to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_max_lease_ttl"></a> [max\_lease\_ttl](#input\_max\_lease\_ttl) | Maximum possible lease duration for certs generated under this PKI | `number` | n/a | yes |
| <a name="input_mount"></a> [mount](#input\_mount) | Used to specify fwt path to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_pki_role_key_bits"></a> [pki\_role\_key\_bits](#input\_pki\_role\_key\_bits) | The number of bits to use for generated keys | `number` | `4096` | no |
| <a name="input_pki_role_key_type"></a> [pki\_role\_key\_type](#input\_pki\_role\_key\_type) | The type of key expected for submitted CSR | `string` | `"rsa"` | no |
| <a name="input_pki_root_cert_key_bits"></a> [pki\_root\_cert\_key\_bits](#input\_pki\_root\_cert\_key\_bits) | The number of bits to use for generated keys for pki root cert | `number` | `4096` | no |
| <a name="input_pki_root_cert_key_type"></a> [pki\_root\_cert\_key\_type](#input\_pki\_root\_cert\_key\_type) | The type of the key to generate private key for pki root cert | `string` | `"rsa"` | no |
| <a name="input_root_ca_cert_ttl"></a> [root\_ca\_cert\_ttl](#input\_root\_ca\_cert\_ttl) | TTL for the root CA cert | `number` | n/a | yes |
| <a name="input_vault_address"></a> [vault\_address](#input\_vault\_address) | The Vault address | `string` | n/a | yes |
| <a name="input_vault_namespace"></a> [vault\_namespace](#input\_vault\_namespace) | The Vault namespace | `string` | `"ns_stargate/ns_dev_wcmshrd_services"` | no |

## Outputs

No outputs.
<!-- END_TF_DOCS -->
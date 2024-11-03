## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | >= 3.5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_vault"></a> [vault](#provider\_vault) | 3.20.1 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [vault_kubernetes_auth_backend_role.cvm_admin](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/kubernetes_auth_backend_role) | resource |
| [vault_mount.cvm_mount](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/mount) | resource |
| [vault_pki_secret_backend_role.cvm](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_role) | resource |
| [vault_pki_secret_backend_role.cvm_2024](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_role) | resource |
| [vault_pki_secret_backend_root_cert.cvm](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_root_cert) | resource |
| [vault_pki_secret_backend_root_cert.cvm_2024](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_root_cert) | resource |
| [vault_policy.cvm_admin](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy) | resource |
| [vault_policy_document.cvm_admin](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_allowed_domains"></a> [allowed\_domains](#input\_allowed\_domains) | Allowed domains for the PKI Secret Backend role | `list(string)` | n/a | yes |
| <a name="input_auth_backend"></a> [auth\_backend](#input\_auth\_backend) | The kubernetes authentication engine to work with | `string` | n/a | yes |
| <a name="input_cert_ttl"></a> [cert\_ttl](#input\_cert\_ttl) | TTL for certificates | `number` | n/a | yes |
| <a name="input_create_cvm_2024_cert"></a> [create\_cvm\_2024\_cert](#input\_create\_cvm\_2024\_cert) | Determines whether to create root cert and role named cvm\_2024 (as part of certificate rotation) | `bool` | `false` | no |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_max_lease_ttl"></a> [max\_lease\_ttl](#input\_max\_lease\_ttl) | Maximum possible lease duration for certs generated under this PKI | `number` | n/a | yes |
| <a name="input_pki_role_key_bits"></a> [pki\_role\_key\_bits](#input\_pki\_role\_key\_bits) | The number of bits to use for generated keys | `number` | `4096` | no |
| <a name="input_pki_role_key_type"></a> [pki\_role\_key\_type](#input\_pki\_role\_key\_type) | The type of key expected for submitted CSR | `string` | `"rsa"` | no |
| <a name="input_pki_root_cert_key_bits"></a> [pki\_root\_cert\_key\_bits](#input\_pki\_root\_cert\_key\_bits) | The number of bits to use for generated keys for pki root cert | `number` | `4096` | no |
| <a name="input_pki_root_cert_key_type"></a> [pki\_root\_cert\_key\_type](#input\_pki\_root\_cert\_key\_type) | The type of the key to generate private key for pki root cert | `string` | `"rsa"` | no |
| <a name="input_root_ca_cert_ttl"></a> [root\_ca\_cert\_ttl](#input\_root\_ca\_cert\_ttl) | TTL for the root CA cert | `number` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_pki_engine_path"></a> [pki\_engine\_path](#output\_pki\_engine\_path) | Path for the PKI engine |

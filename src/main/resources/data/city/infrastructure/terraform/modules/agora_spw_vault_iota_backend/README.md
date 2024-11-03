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
| [vault_jwt_auth_backend_role.iota_device_admin](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/jwt_auth_backend_role) | resource |
| [vault_mount.iota_device_mount](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/mount) | resource |
| [vault_pki_secret_backend_role.iota_devices_2025](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_role) | resource |
| [vault_pki_secret_backend_root_cert.iota_devices_2025](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_root_cert) | resource |
| [vault_policy.iota_device_admin](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy) | resource |
| [vault_policy.iota_devices_2025](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy) | resource |
| [vault_policy_document.iota_device_admin](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/policy_document) | data source |
| [vault_policy_document.iota_devices_2025](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_allowed_domains"></a> [allowed\_domains](#input\_allowed\_domains) | Allowed domains for the PKI Secret Backend role | `list(string)` | n/a | yes |
| <a name="input_allowed_service_account_namespaces"></a> [allowed\_service\_account\_namespaces](#input\_allowed\_service\_account\_namespaces) | List of namespaces able to access Vault auth backend role | `list(string)` | <pre>[<br>  "iot"<br>]</pre> | no |
| <a name="input_allowed_service_accounts"></a> [allowed\_service\_accounts](#input\_allowed\_service\_accounts) | List of service account names able to access Vault auth backend role | `list(string)` | <pre>[<br>  "iota"<br>]</pre> | no |
| <a name="input_auth_jwt_backend"></a> [auth\_jwt\_backend](#input\_auth\_backend) | The jwt authentication engine to work with | `string` | n/a | yes |
| <a name="input_cert_ttl"></a> [cert\_ttl](#input\_cert\_ttl) | TTL for certificates | `number` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_max_lease_ttl"></a> [max\_lease\_ttl](#input\_max\_lease\_ttl) | Maximum possible lease duration for certs generated under this PKI | `number` | n/a | yes |
| <a name="input_pki_role_key_bits"></a> [pki\_role\_key\_bits](#input\_pki\_role\_key\_bits) | The number of bits to use for generated keys | `number` | `4096` | no |
| <a name="input_pki_role_key_type"></a> [pki\_role\_key\_type](#input\_pki\_role\_key\_type) | The type of key expected for submitted CSR | `string` | `"rsa"` | no |
| <a name="input_root_ca_cert_ttl"></a> [root\_ca\_cert\_ttl](#input\_root\_ca\_cert\_ttl) | TTL for the root CA cert | `number` | n/a | yes |
| <a name="input_bound_audiences"></a> [bound\_audiences](#input\_bound\_audiences) | List of audiences for JWT authentication | `list(string)` | <pre>[<br>  "https://kubernetes.default.svc", "vault"<br>]</pre> | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_pki_engine_path"></a> [pki\_engine\_path](#output\_pki\_engine\_path) | Path for the PKI engine |

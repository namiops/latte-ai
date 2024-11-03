<!-- BEGIN_TF_DOCS -->
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
| [vault_mount.intermediate](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/mount) | resource |
| [vault_mount.root](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/mount) | resource |
| [vault_pki_secret_backend_intermediate_cert_request.intermediate](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_intermediate_cert_request) | resource |
| [vault_pki_secret_backend_intermediate_set_signed.intermediate](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_intermediate_set_signed) | resource |
| [vault_pki_secret_backend_role.issuer](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_role) | resource |
| [vault_pki_secret_backend_root_cert.root](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_root_cert) | resource |
| [vault_pki_secret_backend_root_sign_intermediate.intermediate](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/pki_secret_backend_root_sign_intermediate) | resource |
| [vault_policy.issuer](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy) | resource |
| [vault_policy_document.issuer](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/data-sources/policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_environment"></a> [environment](#input\_environment) | The name of the Agora Environment | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_intermediate_pki_path"></a> [intermediate\_pki\_path](#output\_intermediate\_pki\_path) | Vault mount path of Gloo Mesh Relay Intermediate PKI |
| <a name="output_issuer_pki_role"></a> [issuer\_pki\_role](#output\_issuer\_pki\_role) | Vault PKI backend role for generating Relay certificates |
| <a name="output_issuer_policy_name"></a> [issuer\_policy\_name](#output\_issuer\_policy\_name) | The name of the Vault policy used by issuer auth roles |
| <a name="output_root_pki_path"></a> [root\_pki\_path](#output\_root\_pki\_path) | Vault mount path of Gloo Mesh Relay Root PKI |
<!-- END_TF_DOCS -->
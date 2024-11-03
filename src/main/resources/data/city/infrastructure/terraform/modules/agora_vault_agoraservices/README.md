## Requirements

| Name            | Version |
|-----------------|---------|
| Terraform       | >= 0.13 |

## Providers
| Name            | Version  |
|-----------------|----------|
| Hashicorp Vault | >= 3.5.0 |

## Modules
None

## Resources

| Name                               | Description                                                                                                                                                              | Type     | Default | Required |
|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|---------|----------|
| vault_policy                       | The [Hashicorp Vault Policy](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/policy) used to tie a set of secrets to a given role or roles | Resource | null    | yes      |
| vault_kubernetes_auth_backend_role | The [Hashicorp Kubernetes Auth Backend Role](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/kubernetes_auth_backend_role)                 | Resource | null    | yes      |


## Inputs

| Name            | Description                                                                                                       | Type     | Default | Required |
|-----------------|-------------------------------------------------------------------------------------------------------------------|----------|---------|----------|
| auth_backend    | The name of the desired [Secret Engine](https://www.vaultproject.io/docs/secrets/kubernetes) that you wish to use | `string` | null    | yes      |
| environment     | Environment name policy                                                                                           | `string` | null    | yes      |
| vault_namespace | The assigned [Vault Namespace](https://developer.hashicorp.com/vault/docs/enterprise/namespaces)                  | `string` | null    | yes      |



## Outputs
| Name        | Description                    |
|-------------|--------------------------------|
| policy_name | The name of the policy created |
| role_name   | The name of the role created   |

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

| Name | Source | Version |
|------|--------|---------|
| <a name="module_agora_services_vault_kv_engine"></a> [agora\_services\_vault\_kv\_engine](#module\_agora\_services\_vault\_kv\_engine) | ../agora_vault_kv_engine | n/a |

## Resources

| Name | Type |
|------|------|
| [vault_auth_backend.this](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/auth_backend) | resource |
| [vault_jwt_auth_backend.this](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/jwt_auth_backend) | resource |
| [vault_kubernetes_auth_backend_config.this](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/kubernetes_auth_backend_config) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_domain"></a> [cluster\_domain](#input\_cluster\_domain) | cluster domain, example agora-lab.w3n.io for lab2 | `string` | `""` | no |
| <a name="input_cluster_oidc_issuer_url"></a> [cluster\_oidc\_issuer\_url](#input\_cluster\_oidc\_issuer\_url) | The K8S OIDC issuer | `string` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_github_jwt"></a> [github\_jwt](#input\_github\_jwt) | Used to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_kubernetes_ca_cert"></a> [kubernetes\_ca\_cert](#input\_kubernetes\_ca\_cert) | The K8S certs | `string` | n/a | yes |
| <a name="input_kubernetes_host"></a> [kubernetes\_host](#input\_kubernetes\_host) | The K8S host | `string` | n/a | yes |
| <a name="input_mount"></a> [mount](#input\_mount) | Used to specify fwt path to authenticate to vault when running tf in CI | `string` | `""` | no |
| <a name="input_vault_address"></a> [vault\_address](#input\_vault\_address) | The Vault address | `string` | n/a | yes |
| <a name="input_vault_jwt_auth_endpoint"></a> [vault\_jwt\_auth\_endpoint](#input\_vault\_jwt\_auth\_endpoint) | The Vault JWT auth endpoint | `string` | n/a | yes |
| <a name="input_vault_k8s_auth_endpoint"></a> [vault\_k8s\_auth\_endpoint](#input\_vault\_k8s\_auth\_endpoint) | The Vault K8S auth endpoint | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_vault_k8s_auth_backend"></a> [vault\_k8s\_auth\_backend](#output\_vault\_k8s\_auth\_backend) | n/a |
<!-- END_TF_DOCS -->
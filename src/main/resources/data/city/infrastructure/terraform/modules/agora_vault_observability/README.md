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

| Name         | Description                                                                                                       | Type     | Default | Required |
|--------------|-------------------------------------------------------------------------------------------------------------------|----------|---------|----------|
| environment  | Environment name policy                                                                                           | `string` | null    | yes      |
| auth_backend | The name of the desired [Secret Engine](https://www.vaultproject.io/docs/secrets/kubernetes) that you wish to use | `string` | null    | yes      |


## Outputs
| Name        | Description                    |
|-------------|--------------------------------|
| policy_name | The name of the policy created |
| role_name   | The name of the role created   |
****
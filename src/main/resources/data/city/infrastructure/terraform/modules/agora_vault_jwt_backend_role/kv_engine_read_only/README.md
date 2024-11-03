# A generic module for Vault JWT backend role

## Requirements

| Name                                                                      | Version  |
| ------------------------------------------------------------------------- | -------- |
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13  |
| <a name="requirement_vault"></a> [vault](#requirement\_vault)             | >= 3.5.0 |

## Providers

| Name                                                    | Version  |
| ------------------------------------------------------- | -------- |
| <a name="provider_vault"></a> [vault](#provider\_vault) | >= 3.5.0 |

## Modules

No modules.

## Resources

| Name                                                                                                                                                    | Type        |
| ------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| [vault_kubernetes_auth_backend_role.default](https://registry.terraform.io/providers/hashicorp/vault/3.5.0/docs/resources/kubernetes_auth_backend_role) | resource    |
| [vault_policy.default](https://registry.terraform.io/providers/hashicorp/vault/3.5.0/docs/resources/policy)                                             | resource    |
| [vault_policy_document.policy_rules](https://registry.terraform.io/providers/hashicorp/vault/3.5.0/docs/data-sources/policy_document)                   | data source |

## Inputs

| Name                                                                     | Description                                          | Type     | Default | Required |
| ------------------------------------------------------------------------ | ---------------------------------------------------- | -------- | ------- | :------: |
| <a name="input_auth_backend"></a> [auth\_backend](#input\_auth\_backend) | The kubernetes authentication engine to work with    | `string` | n/a     |   yes    |
| <a name="input_environment"></a> [environment](#input\_environment)      | Environment name                                     | `string` | n/a     |   yes    |
| <a name="input_target_infra"></a> [target\_infra](#input\_target\_infra) | Target infrastructure component. Choose from \[aad\] | `string` | n/a     |   yes    |

## Outputs

| Name                                                                    | Description                                           |
| ----------------------------------------------------------------------- | ----------------------------------------------------- |
| <a name="output_policy_name"></a> [policy\_name](#output\_policy\_name) | The name of the policy to use for attaching to roles  |
| <a name="output_role_name"></a> [role\_name](#output\_role\_name)       | The name of the role to use for attaching to accounts |

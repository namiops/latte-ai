# Agora Azure AD Group module for tenant developers

## Providers

| Name                                                           | Version |
| -------------------------------------------------------------- | ------- |
| <a name="provider_azuread"></a> [Azure AD](#provider\_azuread) | >=2.0.1 |

## Resources

| Name                                                                                                                              | Type        |
| --------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/client_config) | data source |
| [azuread_group.tenant_administrator](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group)       | resource    |
| [azuread_group.tenant_engineer](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group)            | resource    |
| [azuread_users.administrator_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users)    | data source |
| [azuread_users.administrator_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users)   | data source |
| [azuread_users.engineer_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users)         | data source |
| [azuread_users.engineer_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users)        | data source |

## Inputs

| Name                                                                                                                               | Description                                          | Type           | Default | Required |
| ---------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------- | -------------- | ------- | :------: |
| <a name="administrator_group_owner_names"></a> [administrator\_group\_owner\_names](#input\_administrator\_group\_owner\_names)    | Principle names of owners in an administrator group  | `list(string)` | n/a     |   yes    |
| <a name="administrator_group_member_names"></a> [administrator\_group\_member\_names](#input\_administrator\_group\_member\_names) | Principle names of members in an administrator group | `list(string)` | n/a     |   yes    |
| <a name="engineer_group_owner_names"></a> [engineer\_group\_owner\_names](#input\_engineer\_group\_owner\_names)                   | Principle names of owners in an engineer group       | `list(string)` | n/a     |   yes    |
| <a name="engineer_group_member_names"></a> [engineer\_group\_member\_names](#input\_engineer\_group\_member\_names)                | Principle names of members in an engineer group      | `list(string)` | n/a     |   yes    |
| <a name="group_description"></a> [group\_description](#input\_group\_description)                                                  | Description of an engineer group                     | `string`       | ""      |    no    |
| <a name="name_prefix"></a> [name\_prefix](#input\_name\_prefix)                                                                    | A prefix for a new group name                        | `string`       | n/a     |   yes    |

## Outputs

| Name                                                                                                                          | Description |
| ----------------------------------------------------------------------------------------------------------------------------- | ----------- |
| <a name="aad_tenant_administrator_group"></a> [aad\_tenant\_administrator\_group](#output\_aad\_tenant\_administrator\_group) | n/a         |
| <a name="aad_tenant_engineer_group"></a> [aad\_tenant\_engineer\_group](#output\_aad\_tenant\_engineer\_group)                | n/a         |

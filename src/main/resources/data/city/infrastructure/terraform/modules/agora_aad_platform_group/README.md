# Agora Azure AD Group module for platform developers

## Providers

| Name                                                           | Version |
| -------------------------------------------------------------- | ------- |
| <a name="provider_azuread"></a> [Azure AD](#provider\_azuread) | >=2.0.1 |

## Resources

| Name                                                                                                                              | Type        |
| --------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/client_config) | data source |
| [azuread_group.this](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group)                       | resource    |
| [azuread_users.owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users)                  | data source |
| [azuread_users.members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users)                 | data source |

## Inputs

| Name                                                                              | Description                                          | Type           | Default | Required |
| --------------------------------------------------------------------------------- | ---------------------------------------------------- | -------------- | ------- | :------: |
| <a name="group_description"></a> [group\_description](#input\_group\_description) | Group's description                                  | `string`       | ""      |    no    |
| <a name="name"></a> [name](#input\_name)                                          | A group name                                         | `string`       | n/a     |   yes    |
| <a name="owner_names"></a> [owner\_names](#input\_owner\_names)                   | Principle names of owners in an administrator group  | `list(string)` | n/a     |   yes    |
| <a name="member_names"></a> [member\_names](#input\_member\_names)                | Principle names of members in an administrator group | `list(string)` | n/a     |   yes    |

## Outputs

| Name                                             | Description |
| ------------------------------------------------ | ----------- |
| <a name="aad_group"></a> [aad_group](#aad_group) | n/a         |

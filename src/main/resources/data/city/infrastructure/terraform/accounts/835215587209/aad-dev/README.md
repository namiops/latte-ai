## Guidelines

### Add a new AD group
We manage information of all groups and their membership in `config.auto.tfvars.json`.
In case of a platform group (Agora service developers), you can add a new object to `platform_groups` array. For example, 
```
{ 
  "platform_groups": [
    {
      "name": "trainer",
      "description": CityOS platform trainer",
      "members": [
        "ash.kethchum@woven-planet.global",
        "misty.natsumi@woven-planet.global"
      ]
    }
  ]
}
```

In case of a tenant group (application developers), you can add a new object to `tenant_groups` array. 
The object format is a bit different from `platform_groups`, since the below keys are used instead of `members` key:
- `administrator_owners` 
- `administrator_members` 
- `engineer_owners` 
- `engineer_members` 

The reason is that we would like to support managing membership of tenant's administrator group. For example,
```
{ 
  "tenant_groups": [
    {
      "name": "mobility",
      "description": "CityOS tenant engineer (Mobility team)",
      "administrator_owners":[
        "haresh.nanarkar@woven-planet.global",
        "marcelo.freitas@woven-planet.global",
        "mathieu.sauve-frankel@woven-planet.global"
      ],
      "administrator_members":[
        "akihiro.kanomune@woven-planet.global",
        "haresh.nanarkar@woven-planet.global"
      ],
      "engineer_owners": [
        "haresh.nanarkar@woven-planet.global",
        "marcelo.freitas@woven-planet.global",
        "mathieu.sauve-frankel@woven-planet.global"
      ],
      "engineer_members": []
    }
  ]
}
```

However, only `engineer_members` is acutally required for simplicity. The other kinds of members can be null. 
In case of null values, they will be replaced by default values. For example,
```
{ 
  "tenant_groups": [
    {
      "name": "rocket",
      "description": "CityOS tenant engineer (Rocket team)",
      "administrator_owners": null,
      "administrator_members": null,
      "engineer_owners": null,
      "engineer_members": [
        "james.kojirou@woven-planet.global",
        "jessie.musashi@woven-planet.global"
      ]
    }
  ]
}
```

**NOTE:** 
- Make sure that a list of members is always sorted with alphabetic order.
- We can use optional attribute for those nullable, but it is supported from terraform `v1.3.0`. Currently, we're using `v1.1.7` generally in our CI cluster.

## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_azuread"></a> [azuread](#provider\_azuread) | n/a |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_keycloak_oidc"></a> [keycloak\_oidc](#module\_keycloak\_oidc) | ../../../modules/agora_aad_oidc | n/a |
| <a name="module_cityos_tenant_area_management_group"></a> [cityos\_tenant\_area\_management\_group](#cityos\_tenant\_area\_management\_group) | ../../../modules/cityos_aad_tenant_group | n/a |
| <a name="module_cityos_tenant_food_agriculture_group"></a> [cityos\_tenant\_food\_agriculture\_group](#cityos\_tenant\_food\_agriculture\_group) | ../../../modules/cityos_aad_tenant_group | n/a |
| <a name="module_cityos_tenant_ia_group"></a> [cityos\_tenant\_ia\_group](#cityos\_tenant\_ia\_group) | ../../../modules/cityos_aad_tenant_group | n/a |
| <a name="module_cityos_tenant_lps_group"></a> [cityos\_tenant\_lps\_group](#cityos\_tenant\_lps\_group) | ../../../modules/cityos_aad_tenant_group | n/a |

## Resources

| Name | Type |
|------|------|
| [azuread_group.cityos_platform_administrator](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_platform_engineer](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_platform_impersonate_cluster_admin](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_platform_viewer](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_tenant_lps_administrator](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_tenant_lps_engineer](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_tenant_mobility_administrator](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_group.cityos_tenant_mobility_engineer](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/resources/group) | resource |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/client_config) | data source |
| [azuread_users.cityos_platform_administrator_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_administrator_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_engineer_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_engineer_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_impersonate_cluster_admin_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_impersonate_cluster_admin_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_viewer_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_platform_viewer_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_tenant_mobility_administrator_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_tenant_mobility_administrator_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_tenant_mobility_engineer_members](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |
| [azuread_users.cityos_tenant_mobility_engineer_owners](https://registry.terraform.io/providers/hashicorp/azuread/latest/docs/data-sources/users) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_AAD_CLIENT_ID"></a> [AAD\_CLIENT\_ID](#input\_AAD\_CLIENT\_ID) | Client ID for the app that will manage credentials | `string` | n/a | yes |
| <a name="input_AAD_CLIENT_SECRET"></a> [AAD\_CLIENT\_SECRET](#input\_AAD\_CLIENT\_SECRET) | client secret of the above app | `string` | n/a | yes |
| <a name="input_AAD_TENANT_ID"></a> [AAD\_TENANT\_ID](#input\_AAD\_TENANT\_ID) | AAD tenant | `string` | `"8c31bd04-5a9a-4aa1-8bfa-c1edfe26a6a0"` | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_application_tenant_id"></a> [application\_tenant\_id](#output\_application\_tenant\_id) | n/a |
| <a name="output_oidc_application_id"></a> [oidc\_application\_id](#output\_oidc\_application\_id) | n/a |
| <a name="output_sp_password"></a> [sp\_password](#output\_sp\_password) | n/a |
| <a name="output_group_id_map"></a> [group_id_map](#output\_group\_id\_map) | n/a |

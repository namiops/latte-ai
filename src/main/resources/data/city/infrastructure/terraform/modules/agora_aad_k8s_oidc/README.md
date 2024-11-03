# Azure AD as OIDC for Kubernetes API
This module creates Azure App Registration (Application) and Azure Enterprise Application (Service Principal) as
OpenID Connect (OIDC) for accessing Kubernetes API. 
You can allow specific users or AAD groups to access your Kubernetes cluster by configuring groups with this module.

## Requirements

| Name                                                                | Version |
| ------------------------------------------------------------------- | ------- |
| <a name="requirement_azuread"></a> [azuread](#requirement\_azuread) | 2.22.0  |


**NOTE:** This module expects Azure AD provider to be configured.
The required version of the provider is `v2.22.0` to align with `agora_aad_oidc` module.
However, the actual required version is `v2.4.0` since it introduces
`azuread_app_role_assignment` resource ([reference](https://github.com/hashicorp/terraform-provider-azuread/releases/tag/v2.4.0)). 


## Usage

1. To use this module, configure module inputes something like
```
module "agora_k8s_oidc_your_env" {
  source = "/path/to/agora_aad_k8s_oidc"

  application_name = "CityOS Kubernetes API - <your env name>"
  consent_description = "Access CityOS Kubernetes API - <your env name>"
  consent_display_name = "Access CityOS Kubernetes API - <your env name>" 
  owner_names = [
    "<your account1>",
    "<your account2>"
  ]
  identifier_uris = []
  redirect_uris = ["http://localhost/"]
  
  # for service principal
  sp_owner_names = [
    "<your account1>",
    "<your account2>"
  ]

  # for app role assignments
  assigned_users = [
    "<your account1>",
    "<your account2>"
  ]
  assigned_platform_group_ids = [
    "<your group id1>",
    "<your group id2>"
  ]
  assigned_tenant_group_ids = [
    "<your group id3>",
    "<your group id4>"
  ]
}
```
NOTE: At the first time, you can leave `identifier_uris` empty, since we need application ID for this configuration.
You can get the application ID, after Azure App Registration is created.

2. Once the resource is created. Update `identifier_uris` variable.
```
module "agora_k8s_oidc_your_env" {
  source = "/path/to/agora_aad_k8s_oidc"

  application_name = "CityOS Kubernetes API - <your env name>"
  consent_description = "Access CityOS Kubernetes API - <your env name>"
  consent_display_name = "Access CityOS Kubernetes API - <your env name>" 
  owner_names = [
    "<your account1>",
    "<your account2>"
  ]
  identifier_uris = ["api://<your_application_id>"]
  redirect_uris = ["http://localhost/"]

  ...
}
```

## Modules

No modules.

## Resources

| Name                                                                                                                                                        | Type     |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| [azuread_application.k8s_api](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/application)                                  | resource |
| [azuread_service_principal.k8s_api_sp](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/service_principal)                   | resource |
| [azuread_app_role_assignment.k8s_api_user_assignment](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/app_role_assignment)  | resource |
| [azuread_app_role_assignment.k8s_api_group_assignment](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/app_role_assignment) | resource |

## Inputs 

| Name                                                                                                                      | Description                                                       | Type           | Default | Required |
| ------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------- | -------------- | ------- | :------: |
| <a name="input_application_name"></a> [application\_name](#input\_application\_name)                                      | string containing the display name for your application           | `string`       | n/a     |   yes    |
| <a name="input_consent_description"></a> [consent\_description](#input\_consent\_description)                             | consent description for both admin and user                       | `string`       | ""      |    no    |
| <a name="input_consent_display_name"></a> [consent\_display\_name](#input\_consent\_display\_name)                        | consent display name for both admin and user                      | `string`       | ""      |    no    |
| <a name="input_identifier_uris"></a> [identifier\_uris](#input\_identifier\_uris)                                         | list containing the identifier uris                               | `list(string)` | []      |    no    |
| <a name="input_owner_names"></a> [owner\_names](#input\_owner\_names)                                                     | principal names of application and service principal owners       | `list(string)` | n/a     |   yes    |
| <a name="input_redirect_uris"></a> [redirect\_uris](#input\_redirect\_uris)                                               | list containing the redirect uris to register in AAD              | `list(string)` | n/a     |   yes    |
| <a name="input_assigned_users"></a> [assigned\_users](#input\_assigned\_users)                                            | list containing user principal names to be assigned with app role | `list(string)` | []      |    no    |
| <a name="input_assigned_platform_group_ids"></a> [assigned\_platform\_group\_ids](#input\_assigned\_platform\_group\_ids) | list containing group ids to be assigned with platform app role   | `list(string)` | []      |    no    |
| <a name="input_assigned_tenant_group_ids"></a> [assigned\_tenant\_group\_ids](#input\_assigned\_tenant\_group\_ids)       | list containing group ids to be assigned with tenant app role     | `list(string)` | []      |    no    |

## Outputs

| Name                                                                                      | Description |
| ----------------------------------------------------------------------------------------- | ----------- |
| <a name="output_oidc_application_id"></a> [oidc\_application\_id](#oidc\_application\_id) | n/a         |

## Useful Resources
- [The difference between AzureAD App Registrations and Enterprise Applications explained](https://www.seb8iaan.com/the-difference-between-azuread-app-registrations-and-enterprise-applications-explained/)

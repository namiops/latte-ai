# Entra ID app registration

This module will create an Entra ID application to be used with Pinniped.

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_azuread"></a> [azuread](#requirement\_azuread) | 2.52.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_azuread"></a> [azuread](#provider\_azuread) | 2.52.0 |
| <a name="provider_time"></a> [time](#provider\_time) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [azuread_application.oidc_application](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/resources/application) | resource |
| [azuread_service_principal.oidc_principal](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/resources/service_principal) | resource |
| [azuread_service_principal_password.oidc_principal_password](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/resources/service_principal_password) | resource |
| [time_rotating.oidc_password](https://registry.terraform.io/providers/hashicorp/time/latest/docs/resources/rotating) | resource |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/data-sources/client_config) | data source |
| [azuread_users.aad_owners](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/data-sources/users) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_application_name"></a> [application\_name](#input\_application\_name) | string containing the display name for your application | `string` | n/a | yes |
| <a name="input_owner_names"></a> [owner\_names](#input\_owner\_names) | principal names of application and service principal owners | `list(string)` | n/a | yes |
| <a name="input_redirect_uris"></a> [redirect\_uris](#input\_redirect\_uris) | list containing the redirect uris to register in AAD | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_oidc_application_id"></a> [oidc\_application\_id](#output\_oidc\_application\_id) | n/a |
| <a name="output_oidc_application_tenant_id"></a> [oidc\_application\_tenant\_id](#output\_oidc\_application\_tenant\_id) | n/a |
| <a name="output_oidc_principal"></a> [oidc\_principal](#output\_oidc\_principal) | n/a |
| <a name="output_sp_password"></a> [sp\_password](#output\_sp\_password) | n/a |

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_azuread"></a> [azuread](#requirement\_azuread) | 2.52.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_azuread"></a> [azuread](#provider\_azuread) | 2.52.0 |
| <a name="provider_time"></a> [time](#provider\_time) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [azuread_application.oidc_application](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/resources/application) | resource |
| [azuread_service_principal.oidc_principal](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/resources/service_principal) | resource |
| [azuread_service_principal_password.oidc_principal_password](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/resources/service_principal_password) | resource |
| [time_rotating.oidc_password](https://registry.terraform.io/providers/hashicorp/time/latest/docs/resources/rotating) | resource |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/data-sources/client_config) | data source |
| [azuread_users.aad_owners](https://registry.terraform.io/providers/hashicorp/azuread/2.52.0/docs/data-sources/users) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_application_name"></a> [application\_name](#input\_application\_name) | string containing the display name for your application | `string` | n/a | yes |
| <a name="input_owner_names"></a> [owner\_names](#input\_owner\_names) | principal names of application and service principal owners | `list(string)` | n/a | yes |
| <a name="input_redirect_uris"></a> [redirect\_uris](#input\_redirect\_uris) | list containing the redirect uris to register in AAD | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_oidc_application_id"></a> [oidc\_application\_id](#output\_oidc\_application\_id) | n/a |
| <a name="output_oidc_application_tenant_id"></a> [oidc\_application\_tenant\_id](#output\_oidc\_application\_tenant\_id) | n/a |
| <a name="output_oidc_principal"></a> [oidc\_principal](#output\_oidc\_principal) | n/a |
| <a name="output_sp_password"></a> [sp\_password](#output\_sp\_password) | n/a |
<!-- END_TF_DOCS -->
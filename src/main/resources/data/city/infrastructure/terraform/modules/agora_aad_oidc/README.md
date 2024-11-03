# AAD

This a module to aid you on creating Azure AD openid configurations. This is
useful for us when enabling SSO from Woven IT's corporate identities to our
KeyCloak users, and also to allow SSO when talking to our clusters.

## Requirements

This module expects Azure AD provider to be configured.


The Azure AD configuration should point to a service account that is able to
manage application it owns.

## Usage

To use this module, do something like

```
module "yourmodule" {
  source = "path/to/this/dir"
  application_name = "name_of_your_app_on_aad"
  redirect_uris ["http://redirect.urls.for.your.app"]
}
```

It will manage your app config in AAD.

**TODO:** scopes?




## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_azuread"></a> [azuread](#requirement\_azuread) | 2.22.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_azuread"></a> [azuread](#provider\_azuread) | 2.22.0 |
| <a name="provider_time"></a> [time](#provider\_time) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [azuread_application.oidc_application](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/application) | resource |
| [azuread_service_principal.oidc_principal](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/service_principal) | resource |
| [azuread_service_principal_password.oidc_principal_password](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/service_principal_password) | resource |
| [time_rotating.oidc_password](https://registry.terraform.io/providers/hashicorp/time/latest/docs/resources/rotating) | resource |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/data-sources/client_config) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_application_name"></a> [application\_name](#input\_application\_name) | string containing the display name for your application | `string` | n/a | yes |
| <a name="input_redirect_uris"></a> [redirect\_uris](#input\_redirect\_uris) | list containing the redirect uris to register in AAD | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_oidc_application_id"></a> [oidc\_application\_id](#output\_oidc\_application\_id) | n/a |
| <a name="output_oidc_application_tenant_id"></a> [oidc\_application\_tenant\_id](#output\_oidc\_application\_tenant\_id) | n/a |
| <a name="output_oidc_principal"></a> [oidc\_principal](#output\_oidc\_principal) | n/a |
| <a name="output_sp_password"></a> [sp\_password](#output\_sp\_password) | n/a |

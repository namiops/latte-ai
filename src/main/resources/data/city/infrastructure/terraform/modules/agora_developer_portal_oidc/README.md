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

```terraform
module "yourmodule" {
  source = "path/to/this/dir"
  application_name = "name_of_your_app_on_aad"
  redirect_uris ["http://redirect.urls.for.your.app"]
}
```

It will manage your app config in AAD.

**TODO:** scopes?

## Requirements List

| Name | Version |
|------|---------|
| azuread | 2.22.0 |

## Providers List

| Name | Version |
|------|---------|
| azuread | 2.22.0 |
| time | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [azuread_application.oidc_application](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/application) | resource |
| [azuread_service_principal.oidc_principal](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/resources/service_principal) | resource |
| [azuread_client_config.current](https://registry.terraform.io/providers/hashicorp/azuread/2.22.0/docs/data-sources/client_config) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| application_name | string containing the display name for your application | `string` | n/a | yes |
| redirect_uris | list containing the redirect uris to register in AAD | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| oidc_application_id | n/a |
| oidc_application_tenant_id | n/a |
| oidc_principal | n/a |
| sp_password | n/a |

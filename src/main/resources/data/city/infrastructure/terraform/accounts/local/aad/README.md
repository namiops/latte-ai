# AAD SSO

This creates a SSO user to be configured against your local keycloak for corp
SSO. This is not required for everyday work and is mostly for working on SSO
specific tasks.

Vault configuration is being handled by out of the box options (e.g. 
`~/.vault-token` file, `VAULT_ADDR` env var, so on). 

The token is configured to expire in 3 months, so if it stops working for you
overnight then you probably just need to run this again.

## Azure AD Configuration format

The module expects you to have your AAD configuration exported as the following
environment variables:

* `TF_VAR_AAD_CLIENT_ID`
* `TF_VAR_AAD_CLIENT_SECRET`
* `TF_VAR_TENANT_ID` (optional, default to WP prod) 

These credentials must be for a user which can manage applications. To create
one, you can follow the sec's vending machine repository
[instructions](https://github.tri-ad.tech/information-security/woven-azure-ad-management/tree/main/vending-machine).


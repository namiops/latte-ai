# Agora Lab 2.0

[Lucid Chart](https://lucid.app/lucidchart/3033f7fc-75e4-4429-9d26-15988880aba1/edit?invitationId=inv_6071f8d5-3ed4-4bae-af7a-ca2e991cdbaa)

![](lab2.jpeg)

## Requirements

You must have all of the following

* aws cli v2
* aws session manager extentions
* azure cli
* terraform
* terragrunt
* vault
* VPN connection (imras-ts)

## Authenticating

This module is meant to be run via our CI/CD pipeline. This being said sometime
you need to run `terragrunt` by hand.
These root modules require access to multiple cloud environments including

### AWS

The following entry needs to be added to your `~/.aws/config` file

```ini
[profile lab2-transit]
region = ap-northeast-1
sso_start_url = https://woven.awsapps.com/start
sso_region = ap-northeast-1
sso_account_id = 716975162005
sso_role_name = AdministratorAccess
```

```sh
aws sso login --profile lab2-transit --no-browse
export AWS_PROFILE=lab2-transit
```

### WP Vault infrastructure

```sh
# This needs to be unset if your session expires and you need to login again
unset VAULT_NAMESPACE
unset VAULT_TOKEN
export VAULT_ADDR=https://dev.vault.tmc-stargate.com
vault login -method=oidc -path=gac
export VAULT_TOKEN=$(cat ~/.vault-token)
```

* WP AzureAD tenant

```sh
az login --use-device-code
```

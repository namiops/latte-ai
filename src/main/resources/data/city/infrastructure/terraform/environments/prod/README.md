# Agora Prod 

See [MVP Agora-managed AWS accounts for Production](https://docs.google.com/document/d/1uZ8akOPzhLCGRRjp0BpuDgs3M9LgQYNOPJuskUrzJx4/edit#heading=h.5qm13wuvtiz9) for more details.

<!-- vim-markdown-toc GFM -->

* [Agora Prod](#agora-prod)
  * [Requirements](#requirements)
  * [Authenticating](#authenticating)
    * [AWS](#aws)
    * [WP Vault infrastructure](#wp-vault-infrastructure)
    * [WP AzureAD tenant](#wp-azuread-tenant)

<!-- vim-markdown-toc -->


## Requirements

You must have all of the following

* aws cli v2
* aws session manager extensions
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

1. The profiles in [aws_config.ini](./aws_config.ini) need to be added to your `~/.aws/config` file. In the following steps, `<profile_name>` refers to a profile name picked from the config.

2. Run AWS sso login. You can add `--no-browser` option if you run the command on your remote machine.

For example,

```sh
aws sso login --profile <profile_name>
```

3. Export the profile as an env variable for running Terraform commands.

For example,

```sh
export AWS_PROFILE=<profile_name>
```

### WP Vault infrastructure

See [Vault CLI Authentication](./vault/README.md#vault-cli-authentication)

### WP AzureAD tenant

```sh
az login --use-device-code
```

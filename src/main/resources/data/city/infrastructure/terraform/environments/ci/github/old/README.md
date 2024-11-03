This module sets up the `cityos-platform/cityos` GitHub repo.

State is saved in S3, so you need a working `saml2aws` setup to run this. You also need a GitHub
PAT with the following scopes: `repo`, `read:org` (Go to https://github.tri-ad.tech/settings/tokens
to generate a token). Then, set the following env variables before running TF: `GITHUB_USERNAME`,
`GITHUB_TOKEN`.
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_github"></a> [github](#requirement\_github) | 4.26.1 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_github"></a> [github](#provider\_github) | 4.26.1 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_cityos_teams"></a> [cityos\_teams](#module\_cityos\_teams) | ../modules/cityos_team | n/a |

## Resources

| Name | Type |
|------|------|
| [github_branch_protection.main](https://registry.terraform.io/providers/integrations/github/4.26.1/docs/resources/branch_protection) | resource |
| [github_branch_protection.master](https://registry.terraform.io/providers/integrations/github/4.26.1/docs/resources/branch_protection) | resource |
| [github_repository.cityos](https://registry.terraform.io/providers/integrations/github/4.26.1/docs/resources/repository) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cityos_teams"></a> [cityos\_teams](#input\_cityos\_teams) | n/a | <pre>list(object({<br>    name        = string,<br>    description = string,<br>    approvers   = list(string),<br>    members     = list(string),<br>  }))</pre> | n/a | yes |

## Outputs

No outputs.

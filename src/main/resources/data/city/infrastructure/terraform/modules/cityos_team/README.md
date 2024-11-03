## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_github"></a> [github](#provider\_github) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [github_team.approvers](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team) | resource |
| [github_team.team](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team) | resource |
| [github_team_membership.approver_membership](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team_membership) | resource |
| [github_team_membership.approver_team_membership](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team_membership) | resource |
| [github_team_membership.membership](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team_membership) | resource |
| [github_team_repository.approvers_repository](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team_repository) | resource |
| [github_team_repository.team_repository](https://registry.terraform.io/providers/integrations/github/latest/docs/resources/team_repository) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_approvers"></a> [approvers](#input\_approvers) | team members capable of approving sensitive changes | `list(string)` | n/a | yes |
| <a name="input_description"></a> [description](#input\_description) | description of team purpose | `string` | n/a | yes |
| <a name="input_gh_privacy"></a> [gh\_privacy](#input\_gh\_privacy) | GitHub team privacy | `string` | `"closed"` | no |
| <a name="input_members"></a> [members](#input\_members) | team members | `list(string)` | n/a | yes |
| <a name="input_name"></a> [name](#input\_name) | name of team | `string` | n/a | yes |
| <a name="input_repos"></a> [repos](#input\_repos) | repositories to associate with the team | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_github_approvers_team_id"></a> [github\_approvers\_team\_id](#output\_github\_approvers\_team\_id) | github\_approvers\_team\_id |
| <a name="output_github_team_id"></a> [github\_team\_id](#output\_github\_team\_id) | github\_team\_id |

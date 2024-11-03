<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_budgets_budget.daily_fixed_cost](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/budgets_budget) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_aws_cost_explorer_budgets_subscribers"></a> [aws\_cost\_explorer\_budgets\_subscribers](#input\_aws\_cost\_explorer\_budgets\_subscribers) | List of Email subscribers to aws budgets notifications | `list(string)` | n/a | yes |
| <a name="input_cost_unit"></a> [cost\_unit](#input\_cost\_unit) | The unit of measurement used for the actual spend, or budget threshold | `string` | n/a | yes |
| <a name="input_daily_fixed_cost_limit_amount"></a> [daily\_fixed\_cost\_limit\_amount](#input\_daily\_fixed\_cost\_limit\_amount) | The amount of daily fixed cost being measured for the budget | `string` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | The name of the Agora Environment | `string` | n/a | yes |

## Outputs

No outputs.
<!-- END_TF_DOCS -->
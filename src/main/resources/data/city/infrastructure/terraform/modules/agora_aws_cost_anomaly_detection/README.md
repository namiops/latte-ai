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
| [aws_ce_anomaly_monitor.services_anomaly_monitor](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ce_anomaly_monitor) | resource |
| [aws_ce_anomaly_subscription.daily_subscription](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ce_anomaly_subscription) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cost_anomaly_detection_subscribers"></a> [cost\_anomaly\_detection\_subscribers](#input\_cost\_anomaly\_detection\_subscribers) | List of Email subscribers to the cost anomaly notifications | `list(string)` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | The name of the Agora Environment | `string` | n/a | yes |

## Outputs

No outputs.
<!-- END_TF_DOCS -->
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
| [aws_route.ipv4](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route) | resource |
| [aws_route.ipv6](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cidr_blocks"></a> [cidr\_blocks](#input\_cidr\_blocks) | n/a | `map(string)` | n/a | yes |
| <a name="input_ipv6_cidr_blocks"></a> [ipv6\_cidr\_blocks](#input\_ipv6\_cidr\_blocks) | n/a | `map(string)` | n/a | yes |
| <a name="input_route_table_ids"></a> [route\_table\_ids](#input\_route\_table\_ids) | n/a | `list(string)` | n/a | yes |
| <a name="input_transit_gateway_id"></a> [transit\_gateway\_id](#input\_transit\_gateway\_id) | n/a | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ipv4_route_table_entries"></a> [ipv4\_route\_table\_entries](#output\_ipv4\_route\_table\_entries) | n/a |
| <a name="output_ipv6_route_table_entries"></a> [ipv6\_route\_table\_entries](#output\_ipv6\_route\_table\_entries) | n/a |
<!-- END_TF_DOCS -->
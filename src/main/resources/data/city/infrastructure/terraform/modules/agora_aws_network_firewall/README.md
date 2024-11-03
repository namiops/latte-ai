# Agora AWS Network Firewall module

Create AWS Network Firewall for inspection traffic in a VPC.
All traffic is dropped by default, except the traffic to the allowed domains.

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 5.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_network_firewall"></a> [network\_firewall](#module\_network\_firewall) | terraform-aws-modules/network-firewall/aws | v1.0.1 |
| <a name="module_network_firewall_policy"></a> [network\_firewall\_policy](#module\_network\_firewall\_policy) | terraform-aws-modules/network-firewall/aws//modules/policy | v1.0.1 |
| <a name="module_network_firewall_rule_group_stateful"></a> [network\_firewall\_rule\_group\_stateful](#module\_network\_firewall\_rule\_group\_stateful) | terraform-aws-modules/network-firewall/aws//modules/rule-group | v1.0.1 |

## Resources

| Name | Type |
|------|------|
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_allowed_domains"></a> [allowed\_domains](#input\_allowed\_domains) | A list of allowed domains through HTTP/HTTPS protocols | `list(string)` | n/a | yes |
| <a name="input_name"></a> [name](#input\_name) | Firewall name | `string` | n/a | yes |
| <a name="input_subnet_mapping"></a> [subnet\_mapping](#input\_subnet\_mapping) | A map of subnets where the firewall will be deployed | `map(string)` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | VPC where the firewall will be deployed | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_firewall_arn"></a> [firewall\_arn](#output\_firewall\_arn) | The Amazon Resource Name (ARN) that identifies the firewall |
| <a name="output_firewall_id"></a> [firewall\_id](#output\_firewall\_id) | The Amazon Resource Name (ARN) that identifies the firewall |
| <a name="output_firewall_policy_arn"></a> [firewall\_policy\_arn](#output\_firewall\_policy\_arn) | The Amazon Resource Name (ARN) that identifies the firewall policy |
| <a name="output_firewall_policy_id"></a> [firewall\_policy\_id](#output\_firewall\_policy\_id) | The Amazon Resource Name (ARN) that identifies the firewall policy |
| <a name="output_firewall_policy_resource_policy_id"></a> [firewall\_policy\_resource\_policy\_id](#output\_firewall\_policy\_resource\_policy\_id) | The Amazon Resource Name (ARN) of the firewall policy associated with the resource policy |
| <a name="output_firewall_policy_update_token"></a> [firewall\_policy\_update\_token](#output\_firewall\_policy\_update\_token) | A string token used when updating a firewall policy |
| <a name="output_firewall_rule_group_stateful_arn"></a> [firewall\_rule\_group\_stateful\_arn](#output\_firewall\_rule\_group\_stateful\_arn) | The Amazon Resource Name (ARN) that identifies the rule group |
| <a name="output_firewall_rule_group_stateful_id"></a> [firewall\_rule\_group\_stateful\_id](#output\_firewall\_rule\_group\_stateful\_id) | The Amazon Resource Name (ARN) that identifies the rule group |
| <a name="output_firewall_rule_group_stateful_resource_policy_id"></a> [firewall\_rule\_group\_stateful\_resource\_policy\_id](#output\_firewall\_rule\_group\_stateful\_resource\_policy\_id) | The Amazon Resource Name (ARN) of the rule group associated with the resource policy |
| <a name="output_firewall_rule_group_stateful_update_token"></a> [firewall\_rule\_group\_stateful\_update\_token](#output\_firewall\_rule\_group\_stateful\_update\_token) | A string token used when updating the rule group |
| <a name="output_firewall_status"></a> [firewall\_status](#output\_firewall\_status) | Nested list of information about the current status of the firewall |
| <a name="output_firewall_update_token"></a> [firewall\_update\_token](#output\_firewall\_update\_token) | A string token used when updating a firewall |

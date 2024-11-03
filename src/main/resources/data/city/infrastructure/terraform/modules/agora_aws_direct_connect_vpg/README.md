# Agora AWS Virtual Private Gateway for AWS Direct Connect

To create a new VPG and associate it with DX Gateway managed by EnTec and WCM-ICT teams.

## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_dx_gateway_association_proposal.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/dx_gateway_association_proposal) | resource |
| [aws_vpn_gateway.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpn_gateway) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_dx_gateway_id"></a> [dx\_gateway\_id](#input\_dx\_gateway\_id) | n/a | `string` | n/a | yes |
| <a name="input_dx_gateway_owner"></a> [dx\_gateway\_owner](#input\_dx\_gateway\_owner) | The account number of DX Gateway's owner | `string` | n/a | yes |
| <a name="input_name"></a> [name](#input\_name) | n/a | `string` | n/a | yes |
| <a name="input_vpc_cidr_blocks"></a> [vpc\_cidr\_blocks](#input\_vpc\_cidr\_blocks) | VPC CIDR blocks for allowed prefixes | `list(string)` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | n/a | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_proposal_id"></a> [proposal\_id](#output\_proposal\_id) | The proposal ID of DX Gateway association |
| <a name="output_vpg_amazon_side_asn"></a> [vpg\_amazon\_side\_asn](#output\_vpg\_amazon\_side\_asn) | The ASN of Virtual Private Gateway |
| <a name="output_vpg_id"></a> [vpg\_id](#output\_vpg\_id) | The ID of Virtual Private Gateway |

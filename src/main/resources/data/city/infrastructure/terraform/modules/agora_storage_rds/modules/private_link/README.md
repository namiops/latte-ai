## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |
| <a name="provider_dns"></a> [dns](#provider\_dns) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_lb.rds_nlb](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb) | resource |
| [aws_lb_listener.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_target_group.rds_nlb_target_group](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group) | resource |
| [aws_lb_target_group_attachment.rds_nlb_tg_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_vpc_endpoint_service.rds_vpc_endpoint_service](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_endpoint_service) | resource |
| [aws_vpc_endpoint_service_allowed_principal.rds_vpc_es_allowed_principle](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_endpoint_service_allowed_principal) | resource |
| [dns_a_record_set.rds_proxy_ips](https://registry.terraform.io/providers/hashicorp/dns/latest/docs/data-sources/a_record_set) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_access_logs_s3_bucket_name"></a> [access\_logs\_s3\_bucket\_name](#input\_access\_logs\_s3\_bucket\_name) | Name of the s3 bucket to use for NLB logs | `string` | n/a | yes |
| <a name="input_endpoint_service_allowed_principals"></a> [endpoint\_service\_allowed\_principals](#input\_endpoint\_service\_allowed\_principals) | Principal to allow to use the VPC Endpoint Service | `string` | n/a | yes |
| <a name="input_nlb_name"></a> [nlb\_name](#input\_nlb\_name) | Desired name of the NLB | `string` | n/a | yes |
| <a name="input_nlb_target_group_name"></a> [nlb\_target\_group\_name](#input\_nlb\_target\_group\_name) | Desired name of the NLB target group | `string` | n/a | yes |
| <a name="input_proxy_host"></a> [proxy\_host](#input\_proxy\_host) | Host of the RDS proxy | `string` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | Identifier of the VPC in which to create the target group | `string` | n/a | yes |
| <a name="input_vpc_subnet_ids"></a> [vpc\_subnet\_ids](#input\_vpc\_subnet\_ids) | List of subnet IDs to attach to the LB | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_endpoint_service_arn"></a> [endpoint\_service\_arn](#output\_endpoint\_service\_arn) | The ARN of the created Endpoint Service |
| <a name="output_nlb_arn"></a> [nlb\_arn](#output\_nlb\_arn) | The ARN of the created NLB |

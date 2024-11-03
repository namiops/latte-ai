## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_private_link"></a> [private\_link](#module\_private\_link) | ../private_link | n/a |
| <a name="module_rds_instance_irsa"></a> [rds\_instance\_irsa](#module\_rds\_instance\_irsa) | ../rds_instance_irsa | n/a |
| <a name="module_rds_proxy"></a> [rds\_proxy](#module\_rds\_proxy) | ../rds_proxy | n/a |

## Resources

| Name | Type |
|------|------|
| [aws_db_instance.rds_instance](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/db_instance) | resource |
| [aws_db_parameter_group.education](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/db_parameter_group) | resource |
| [aws_kms_key.rds_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_key_policy.key_policy_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key_policy) | resource |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.key_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_access_logs_s3_bucket_name"></a> [access\_logs\_s3\_bucket\_name](#input\_access\_logs\_s3\_bucket\_name) | Name of the s3 bucket to use for NLB logs | `string` | n/a | yes |
| <a name="input_allocated_storage"></a> [allocated\_storage](#input\_allocated\_storage) | Size in GB of the database instance to provision | `string` | n/a | yes |
| <a name="input_db_subnet_group_id"></a> [db\_subnet\_group\_id](#input\_db\_subnet\_group\_id) | Name of the DB subnet group. The DB instance will be created in the VPC associated with the DB subnet group. | `string` | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | ARN of the OIDC provider on the external AWS account (fetched by agora\_aws\_oidc\_provider module) | `string` | n/a | yes |
| <a name="input_eks_oidc_provider_url"></a> [eks\_oidc\_provider\_url](#input\_eks\_oidc\_provider\_url) | URL of the EKS cluster OIDC provider. Do not include protocol (https://) | `string` | n/a | yes |
| <a name="input_endpoint_service_allowed_principals"></a> [endpoint\_service\_allowed\_principals](#input\_endpoint\_service\_allowed\_principals) | Principal to allow to use the VPC Endpoint Service | `string` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment (dev3/prod) the RDS and related resources are created in | `string` | n/a | yes |
| <a name="input_iam_role_name"></a> [iam\_role\_name](#input\_iam\_role\_name) | Name of the created IAM role | `string` | n/a | yes |
| <a name="input_instance_class"></a> [instance\_class](#input\_instance\_class) | Instance class to be used to generated the RDS instance | `string` | n/a | yes |
| <a name="input_service_account_name"></a> [service\_account\_name](#input\_service\_account\_name) | Name of the service account to be used for IRSA | `string` | n/a | yes |
| <a name="input_service_account_namespace"></a> [service\_account\_namespace](#input\_service\_account\_namespace) | Namespace of the service account to be used for IRSA | `string` | n/a | yes |
| <a name="input_username"></a> [username](#input\_username) | Username for the master DB user. | `string` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | Identifier of the VPC in which to create the target group. | `string` | n/a | yes |
| <a name="input_vpc_security_group_ids"></a> [vpc\_security\_group\_ids](#input\_vpc\_security\_group\_ids) | List of VPC security groups to associate with the RDS instance | `string` | n/a | yes |
| <a name="input_vpc_subnet_ids"></a> [vpc\_subnet\_ids](#input\_vpc\_subnet\_ids) | One or more VPC subnet IDs to associate with the new proxy | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_endpoint_service_arn"></a> [endpoint\_service\_arn](#output\_endpoint\_service\_arn) | ARN of the Enpoint Service created |
| <a name="output_iam_details"></a> [iam\_details](#output\_iam\_details) | IAM role details of RDS proxy access role |
| <a name="output_nlb_arn"></a> [nlb\_arn](#output\_nlb\_arn) | ARN of the NLB created |
| <a name="output_rds_arn"></a> [rds\_arn](#output\_rds\_arn) | ARN of the RDS instance created |
| <a name="output_rds_id"></a> [rds\_id](#output\_rds\_id) | Identifier of the RDS instance created |
| <a name="output_rds_key_arn"></a> [rds\_key\_arn](#output\_rds\_key\_arn) | ARN of the KMS key created to encrypt RDS |
| <a name="output_rds_proxy_arn"></a> [rds\_proxy\_arn](#output\_rds\_proxy\_arn) | ARN of the RDS proxy created |
| <a name="output_rds_proxy_name"></a> [rds\_proxy\_name](#output\_rds\_proxy\_name) | Name of the RDS proxy created |

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
| <a name="module_cross_account_irsa_iam_role"></a> [cross\_account\_irsa\_iam\_role](#module\_cross\_account\_irsa\_iam\_role) | ../cross_account_irsa_iam_role | n/a |

## Resources

| Name | Type |
|------|------|
| [aws_iam_role_policy_attachment.rds_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_policy_document.rds_instance_iam_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | ARN of the OIDC provider on the external AWS account (fetched by agora\_aws\_oidc\_provider module) | `string` | n/a | yes |
| <a name="input_eks_oidc_provider_url"></a> [eks\_oidc\_provider\_url](#input\_eks\_oidc\_provider\_url) | URL of the EKS cluster OIDC provider. Do not include protocol (https://) | `string` | n/a | yes |
| <a name="input_iam_role_name"></a> [iam\_role\_name](#input\_iam\_role\_name) | Name of the IAM role to create with access | `string` | n/a | yes |
| <a name="input_rds_key_arn"></a> [rds\_key\_arn](#input\_rds\_key\_arn) | ARN of the KMS key for the target RDS instance | `string` | n/a | yes |
| <a name="input_rds_proxy_arn"></a> [rds\_proxy\_arn](#input\_rds\_proxy\_arn) | ARN of the target RDS proxy | `string` | n/a | yes |
| <a name="input_service_account_name"></a> [service\_account\_name](#input\_service\_account\_name) | Name of the ServiceAccount for IRSA | `string` | n/a | yes |
| <a name="input_service_account_namespace"></a> [service\_account\_namespace](#input\_service\_account\_namespace) | Namespace of the ServiceAccount for IRSA | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | IAM role ARN that would be mapped to a K8S service account |
| <a name="output_iam_role_name"></a> [iam\_role\_name](#output\_iam\_role\_name) | The name for a K8S service account mapped to the IAM role via IRSA |

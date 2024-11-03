<!-- BEGIN_TF_DOCS -->
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
| [aws_iam_policy.bucket_space_iam_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_role_policy_attachment.s3_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_policy_document.bucket_space_iam_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_agora_bucket_oidc_provider_arn"></a> [agora\_bucket\_oidc\_provider\_arn](#input\_agora\_bucket\_oidc\_provider\_arn) | ARN of the OIDC provider on Agora Bucket's AWS account | `string` | n/a | yes |
| <a name="input_agora_eks_oidc_provider_url"></a> [agora\_eks\_oidc\_provider\_url](#input\_agora\_eks\_oidc\_provider\_url) | URL of the OIDC provider on the Agora EKS AWS account | `string` | n/a | yes |
| <a name="input_bucket_arn"></a> [bucket\_arn](#input\_bucket\_arn) | ARN of the target user S3 bucket | `string` | n/a | yes |
| <a name="input_bucket_kms_key_arn"></a> [bucket\_kms\_key\_arn](#input\_bucket\_kms\_key\_arn) | ARN of the KMS key for the bucket key of the target S3 bucket | `string` | n/a | yes |
| <a name="input_role_name"></a> [role\_name](#input\_role\_name) | Name of the IAM role | `string` | n/a | yes |
| <a name="input_service_account_name"></a> [service\_account\_name](#input\_service\_account\_name) | Name of the ServiceAccount for IRSA | `string` | n/a | yes |
| <a name="input_service_account_namespace"></a> [service\_account\_namespace](#input\_service\_account\_namespace) | Namespace of the ServiceAccount for IRSA | `string` | n/a | yes |
| <a name="input_space_name"></a> [space\_name](#input\_space\_name) | Top-level object key prefix that the IAM role targets | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | IAM role ARN that would be mapped to a K8S service account |
| <a name="output_iam_role_name"></a> [iam\_role\_name](#output\_iam\_role\_name) | The name for a K8S service account mapped to the IAM role via IRSA |
| <a name="output_space_arn"></a> [space\_arn](#output\_space\_arn) | Bucket space ARN |
<!-- END_TF_DOCS -->
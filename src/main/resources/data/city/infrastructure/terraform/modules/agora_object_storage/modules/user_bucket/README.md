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
| <a name="module_bucket_spaces_irsa"></a> [bucket\_spaces\_irsa](#module\_bucket\_spaces\_irsa) | ../bucket_space_irsa | n/a |
| <a name="module_user_bucket"></a> [user\_bucket](#module\_user\_bucket) | terraform-aws-modules/s3-bucket/aws | 3.5.0 |

## Resources

| Name | Type |
|------|------|
| [aws_kms_key.bucket_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |

## Inputs

| Name                                                                                                                                 | Description                                                            | Type | Default | Required |
|--------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------|------|---------|:--------:|
| <a name="input_agora_bucket_oidc_provider_arn"></a> [agora\_bucket\_oidc\_provider\_arn](#input\_agora\_bucket\_oidc\_provider\_arn) | Agora Bucket's OIDC provider ARN                                       | `string` | n/a |   yes    |
| <a name="input_agora_eks_oidc_provider_url"></a> [agora\_eks\_oidc\_provider\_url](#input\_agora\_eks\_oidc\_provider\_url)          | Agora EKS's OIDB provider URL                                          | `string` | n/a |   yes    |
| <a name="input_bucket_name"></a> [bucket\_name](#input\_bucket\_name)                                                                | S3 bucket name for the target namespace                                | `string` | n/a |   yes    |
| <a name="input_logging_bucket_name"></a> [logging\_bucket\_name](#input\_logging\_bucket\_name)                                      | Bucket name for storing bucket access logs                             | `string` | n/a |   yes    |
| <a name="input_namespace"></a> [namespace](#input\_namespace)                                                                        | Kubernetes namespace that owns the created bucket                      | `string` | n/a |   yes    |
| <a name="input_key_accessor_arn"></a> [key_accessor_arn](#input\_key\_accessor\_arn)                                                 | Role ARN that should be given limited access to the KMS key            | `string` | n/a |    no    |
| <a name="input_spaces"></a> [spaces](#input\_spaces)                                                                                 | Map with space names as keys and IAM role names as values bucket spaces | `map(string)` | n/a |   yes    |
| <a name="input_add_on_bucket_policy"></a> [add_on_bucket_policy](#input\_add\_on\_bucket\_policy)                                    | An additional policy that should be attached to the bucket     | n/a | yes |

## Outputs

| Name                                                                                             | Description                                           |
|--------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| <a name="output_bucket_kms_key_arn"></a> [bucket\_kms\_key\_arn](#output\_bucket\_kms\_key\_arn) | ARN of the KMS key configured as the bucket key       |
| <a name="output_bucket_spaces"></a> [bucket\_spaces](#output\_bucket\_spaces)                    | Information of the created spaces for the user bucket |
| <a name="output_bucket_policy"></a> [bucket\_policy](#output\_bucket\_policy)                    | The policy that was tied to the created bucket |
| <a name="output_s3_bucket_arn"></a> [s3\_bucket\_arn](#output\_s3\_bucket\_arn)                  | ARN of the S3 Bucket created for the target namespace |
<!-- END_TF_DOCS -->
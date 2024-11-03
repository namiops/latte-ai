## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |
| <a name="provider_random"></a> [random](#provider\_random) | n/a |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_aws_s3_bucket_mimir_blocks"></a> [aws\_s3\_bucket\_mimir\_blocks](#module\_aws\_s3\_bucket\_mimir\_blocks) | terraform-aws-modules/s3-bucket/aws | n/a |
| <a name="module_mimir_iam_role"></a> [mimir\_iam\_role](#module\_mimir\_iam\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.mimir_iam_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_kms_key.mimir_blocks](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_iam_policy_document.mimir_role_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.mimir_blocks_bucket_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_openid_provider"></a> [openid\_provider](#input\_openid\_provider) | ARN of OpenID Provider of the Cluster for IRSA | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | IRSA Role ARN needed for mimir |
| <a name="output_s3_bucket_blocks_name"></a> [s3\_bucket\_blocks\_name](#output\_s3\_bucket\_blocks\_name) | Name of S3 Bucket for mimir blocks |

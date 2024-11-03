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
| <a name="module_aws_s3_bucket_this_admin"></a> [aws\_s3\_bucket\_this\_admin](#module\_aws\_s3\_bucket\_this\_admin) | terraform-aws-modules/s3-bucket/aws | n/a |
| <a name="module_aws_s3_bucket_this_chunks"></a> [aws\_s3\_bucket\_this\_chunks](#module\_aws\_s3\_bucket\_this\_chunks) | terraform-aws-modules/s3-bucket/aws | n/a |
| <a name="module_aws_s3_bucket_this_ruler"></a> [aws\_s3\_bucket\_this\_ruler](#module\_aws\_s3\_bucket\_this\_ruler) | terraform-aws-modules/s3-bucket/aws | n/a |
| <a name="module_this_iam_role"></a> [this\_iam\_role](#module\_this\_iam\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.this_iam_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_kms_key.this_admin](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_key.this_chunks](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_key.this_ruler](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [random_pet.this](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/pet) | resource |
| [aws_iam_policy_document.this_admin_bucket_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.this_chunks_bucket_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.this_role_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.this_ruler_bucket_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_environment"></a> [environment](#input\_environment) | Environment name | `string` | n/a | yes |
| <a name="input_openid_provider"></a> [openid\_provider](#input\_openid\_provider) | ARN of OpenID Provider of the Cluster for IRSA | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | IRSA Role ARN needed for Annotation for this |
| <a name="output_s3_bucket_admin_name"></a> [s3\_bucket\_admin\_name](#output\_s3\_bucket\_admin\_name) | Name of S3 Bucket for Admin Configuration |
| <a name="output_s3_bucket_chunks_name"></a> [s3\_bucket\_chunks\_name](#output\_s3\_bucket\_chunks\_name) | Name of S3 Bucket for Logs |
| <a name="output_s3_bucket_ruler_name"></a> [s3\_bucket\_ruler\_name](#output\_s3\_bucket\_ruler\_name) | Name of S3 Bucket for Ruler |

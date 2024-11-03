# Agora OTA DynamoDB

This module packages the infrastructural resources required to implement OTA DynamoDB insert/query tables.

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 3.73 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 3.73 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="dynamodb_release_blocks"></a> [dynamodb\_release\_blocks](#dynamodb\_release\_blocks) | terraform-aws-modules/dynamodb-table/aws | 4.0.1 |
| <a name="dynamodb_release_distribution_blocks"></a> [dynamodb\_release\_distribution\_blocks](#dynamodb\_release\_distribution\_blocks) | terraform-aws-modules/dynamodb-table/aws | 4.0.1 |
| <a name="aws_dynamodb_ota_irsa"></a> [aws\_dynamodb\_ota\_irsa](#aws\_dynamodb\_ota\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.ota_dynamodb_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy_document.ota_dynamodb_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_kms_key_policy.key_policy_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key_policy) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | cluster's OIDC ARN to delegate trust to | `string` | n/a | yes |
| <a name="input_resource_prefix"></a> [resource\_prefix](#input\_resource\_prefix) | The string the resource names under this module is prefixed with | `string` | n/a | yes |

## Outputs

| Name                                                                                          | Description                                            |
|-----------------------------------------------------------------------------------------------|--------------------------------------------------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn)                  | The IRSA role created for use by the ci runner         |
| <a name="output_bucket_arn"></a> [iam\_bucket\_arn](#output\_iam\_bucket\_arn)                | The ARN of the created Bucket                          |
| <a name="output_bucket_key"></a> [iam\_bucket\_key](#output\_iam\_bucket\_key)                | The ARN of the KMS key we use to encrypt the Bucket    |
| <a name="output_bucket_key_id"></a> [iam\_bucket\_key_id](#output\_iam\_bucket\_key\_id)      | The ID of the KMS key we use to encrypt the Bucket     |
| <a name="output_bucket_key_policy"></a> [iam\_bucket\_arn](#output\_iam\_bucket\_key\_policy) | The policy of the KMS key we use to encrypt the Bucket |
| <a name="output_bucket_policy"></a> [iam\_bucket\_policy](#output\_iam\_bucket\_policy)       | The policy created for the bucket                      |

# Agora Multi BYOB - User (Elevated)

This module creates a policy that the Service team's role can use to access your bucket in an elevated manner.

This should not be run by the Services Team, it is given to the user for them to run.

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy_document.s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name                                                                                                | Description                                                                      | Type           | Default          | Required |
|-----------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|----------------|------------------|:--------:|
| <a name="accessor_arn"></a> [accessor_arn](#input\_accessor\_arn)                                   | ARN of the role that will access your bucket (given to you by the Services team) | `string`       | n/a              |   yes    |
| <a name="bucket"></a> [bucket](#input\_bucket)                                                      | ID of your bucket (not the full ARN)                                             | `string`       | n/a              |   yes    |
| <a name="kms_key_arn"></a> [kms_key_arn](#input\_kms\_key\_arn)                                     | The ARN of the KMS key used to encrypt your bucket                               | `string`       | n/a              |    no    |

## Outputs

| Name                                                                      | Description                                             | Type     | Default | Required |
|---------------------------------------------------------------------------|---------------------------------------------------------|----------|---------|:--------:|
| <a name="bucket_policy"></a> [bucket_policy](#output\_bucket\_policy)     | The bucket policy to add to your existing bucket policy | `string` | n/a     |   yes    |
| <a name="kms_key_policy"></a> [kms_key_policy](#output\_kms\_key\_policy) | The KMS policy to add to your existing policy           | `string` | n/a     |   yes    |

# S3 module for VMS recording server instances

## Requirements

| Name                                                                      | Version |
| ------------------------------------------------------------------------- | ------- |
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws)                   | >= 5.70 |

## Providers

| Name                                              | Version |
| ------------------------------------------------- | ------- |
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 5.70 |

## Modules

| Name                                                                              | Source                              | Version |
| --------------------------------------------------------------------------------- | ----------------------------------- | ------- |
| <a name="module_vai_vms_rs_s3"></a> [vai\_vms\_rs\_s3](#module\_vai\_vms\_rs\_s3) | terraform-aws-modules/s3-bucket/aws | 4.1.2   |

## Resources

| Name                                                                                                                                                      | Type     |
| --------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| [aws_iam_policy.s3_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy)                                        | resource |
| [aws_iam_role_policy_attachment.s3_policy_attach](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_kms_key.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key)                                                   | resource |

## Inputs

| Name                                                                                                     | Description                                                      | Type           | Default | Required |
| -------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------- | -------------- | ------- | :------: |
| <a name="input_bucket_name"></a> [bucket\_name](#input\_bucket\_name)                                    | Name used by instance and other resources created by this module | `string`       | n/a     |   yes    |
| <a name="input_instance_profile_names"></a> [instance\_profile\_names](#input\_instance\_profile\_names) | A list of instance profiles to be granted with access to this S3 | `list(string)` | n/a     |   yes    |

## Outputs

| Name                                                                               | Description                                       |
| ---------------------------------------------------------------------------------- | ------------------------------------------------- |
| <a name="output_bucket_arn"></a> [bucket\_arn](#output\_bucket\_arn)               | Recording server's bucket ARN                     |
| <a name="output_bucket_key_arn"></a> [bucket\_key\_arn](#output\_bucket\_key\_arn) | The ARN of the KMS key used to encrypt the bucket |
| <a name="output_bucket_key_id"></a> [bucket\_key\_id](#output\_bucket\_key\_id)    | The ID of the KMS key used to encrypt the bucket  |
| <a name="output_bucket_policy"></a> [bucket\_policy](#output\_bucket\_policy)      | The bucket's policy                               |
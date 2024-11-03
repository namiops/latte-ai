## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |
| <a name="requirement_github"></a> [github](#requirement\_github) | 4.18.1 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |
| <a name="provider_cloudinit"></a> [cloudinit](#provider\_cloudinit) | n/a |
| <a name="provider_tls"></a> [tls](#provider\_tls) | n/a |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_iam_role"></a> [iam\_role](#module\_iam\_role) | terraform-aws-modules/iam/aws//modules/iam-assumable-role | ~> 5.18 |
| <a name="module_kms"></a> [kms](#module\_kms) | terraform-aws-modules/kms/aws | 1.1.0 |

## Resources

| Name | Type |
|------|------|
| [aws_ebs_volume.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ebs_volume) | resource |
| [aws_iam_policy.volume_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_instance.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance) | resource |
| [aws_key_pair.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/key_pair) | resource |
| [aws_volume_attachment.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/volume_attachment) | resource |
| [tls_private_key.key_pair](https://registry.terraform.io/providers/hashicorp/tls/latest/docs/resources/private_key) | resource |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.volume_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_session_context.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_session_context) | data source |
| [aws_subnet.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/subnet) | data source |
| [cloudinit_config.this](https://registry.terraform.io/providers/hashicorp/cloudinit/latest/docs/data-sources/config) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_ami_id"></a> [ami\_id](#input\_ami\_id) | AMI for VMS instance | `string` | `"ami-01a8ef944f58a13cd"` | no |
| <a name="input_ami_shared_accounts"></a> [ami\_shared\_accounts](#input\_ami\_shared\_accounts) | AWS accounts to share KMS keys with for using AMI created by this EC2 | `list(string)` | `[]` | no |
| <a name="input_data_ebs_volume_iops"></a> [data\_ebs\_volume\_iops](#input\_data\_ebs\_volume\_iops) | n/a | `number` | `4000` | no |
| <a name="input_data_ebs_volume_size"></a> [data\_ebs\_volume\_size](#input\_data\_ebs\_volume\_size) | n/a | `number` | `32` | no |
| <a name="input_data_ebs_volume_throughput"></a> [data\_ebs\_volume\_throughput](#input\_data\_ebs\_volume\_throughput) | n/a | `number` | `1000` | no |
| <a name="input_data_ebs_volume_type"></a> [data\_ebs\_volume\_type](#input\_data\_ebs\_volume\_type) | n/a | `string` | `"gp3"` | no |
| <a name="input_enable_ebs_encryption"></a> [enable\_ebs\_encryption](#input\_enable\_ebs\_encryption) | n/a | `bool` | `true` | no |
| <a name="input_instance_name"></a> [instance\_name](#input\_instance\_name) | Name used by instance and other resources created by this module | `string` | n/a | yes |
| <a name="input_instance_type"></a> [instance\_type](#input\_instance\_type) | n/a | `string` | `"c6i.4xlarge"` | no |
| <a name="input_region"></a> [region](#input\_region) | n/a | `string` | `"ap-northeast-1"` | no |
| <a name="input_root_block_device_volume_iops"></a> [root\_block\_device\_volume\_iops](#input\_root\_block\_device\_volume\_iops) | n/a | `number` | `4000` | no |
| <a name="input_root_block_device_volume_size"></a> [root\_block\_device\_volume\_size](#input\_root\_block\_device\_volume\_size) | n/a | `number` | `512` | no |
| <a name="input_root_block_device_volume_throughput"></a> [root\_block\_device\_volume\_throughput](#input\_root\_block\_device\_volume\_throughput) | n/a | `number` | `1000` | no |
| <a name="input_root_block_device_volume_type"></a> [root\_block\_device\_volume\_type](#input\_root\_block\_device\_volume\_type) | n/a | `string` | `"gp3"` | no |
| <a name="input_security_group_id"></a> [security\_group\_id](#input\_security\_group\_id) | Security groups attached to the instance | `string` | n/a | yes |
| <a name="input_subnet_id"></a> [subnet\_id](#input\_subnet\_id) | Subnet where the instance will be deployed | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_instance_profile_name"></a> [iam\_instance\_profile\_name](#output\_iam\_instance\_profile\_name) | n/a |
| <a name="output_instance_id"></a> [instance\_id](#output\_instance\_id) | n/a |
| <a name="output_root_block_device_name"></a> [root\_block\_device\_name](#output\_root\_block\_device\_name) | n/a |
| <a name="output_root_block_device_volume_id"></a> [root\_block\_device\_volume\_id](#output\_root\_block\_device\_volume\_id) | n/a |

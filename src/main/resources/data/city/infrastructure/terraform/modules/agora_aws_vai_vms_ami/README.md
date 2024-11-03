# Private AMI for VisionAI VMS instances

To create an AMI from a selected volume and share it privately to other accounts (ex. sharing from Dev to Prod)

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_ami.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ami) | resource |
| [aws_ami_launch_permission.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ami_launch_permission) | resource |
| [aws_ebs_snapshot.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ebs_snapshot) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_name"></a> [name](#input\_name) | A name of an AMI | `string` | n/a | yes |
| <a name="input_root_device_name"></a> [root\_device\_name](#input\_root\_device\_name) | A root device name in an AMI | `string` | `"/dev/sda1"` | no |
| <a name="input_shared_account_ids"></a> [shared\_account\_ids](#input\_shared\_account\_ids) | A list of account numbers for sharing a new AMI privately | `list(string)` | `[]` | no |
| <a name="input_volume_id"></a> [volume\_id](#input\_volume\_id) | An EBS volume id for creating an AMI | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ami_id"></a> [ami\_id](#output\_ami\_id) | n/a |

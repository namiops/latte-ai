## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_github"></a> [github](#requirement\_github) | 4.18.1 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |
| <a name="provider_cloudinit"></a> [cloudinit](#provider\_cloudinit) | n/a |
| <a name="provider_github"></a> [github](#provider\_github) | 4.18.1 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_ebs_volume.cityos_dev_vm_ebs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ebs_volume) | resource |
| [aws_iam_instance_profile.cityos_dev_vm_instance_profile](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_instance_profile) | resource |
| [aws_iam_policy.cityos_dev_vm_volume_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_role.cityos_dev_vm_role](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role) | resource |
| [aws_instance.cityos_dev_vm](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance) | resource |
| [aws_key_pair.cityos_dev_vm_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/key_pair) | resource |
| [aws_ami.cityos_dev_vm](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/ami) | data source |
| [aws_caller_identity.self](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.cityos_dev_vm_assume_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.cityos_dev_vm_volume_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_subnet.default](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/subnet) | data source |
| [cloudinit_config.cityos_dev_vm](https://registry.terraform.io/providers/hashicorp/cloudinit/latest/docs/data-sources/config) | data source |
| [github_user.cityos_dev_vm](https://registry.terraform.io/providers/integrations/github/4.18.1/docs/data-sources/user) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_availability_zone"></a> [availability\_zone](#input\_availability\_zone) | The AWS availability zone to create the spot instance request in | `string` | `"ap-northeast-1a"` | no |
| <a name="input_ec2_spot_prices"></a> [ec2\_spot\_prices](#input\_ec2\_spot\_prices) | n/a | `map(any)` | <pre>{<br>  "m5.xlarge": 0.1,<br>  "t3.medium": 0.03,<br>  "t3.xlarge": 0.1<br>}</pre> | no |
| <a name="input_github_login"></a> [github\_login](#input\_github\_login) | The github\_login to fetch ssh\_authorized keys from | `string` | n/a | yes |
| <a name="input_instance_name"></a> [instance\_name](#input\_instance\_name) | The name used to describe the aws\_instance | `string` | n/a | yes |
| <a name="input_instance_type"></a> [instance\_type](#input\_instance\_type) | n/a | `string` | `"t3.xlarge"` | no |
| <a name="input_region"></a> [region](#input\_region) | The AWS region | `string` | n/a | yes |
| <a name="input_security_group_id"></a> [security\_group\_id](#input\_security\_group\_id) | n/a | `string` | n/a | yes |
| <a name="input_shell"></a> [shell](#input\_shell) | n/a | `string` | `"/bin/bash"` | no |
| <a name="input_user"></a> [user](#input\_user) | The user id that will be created on the dev instance | `string` | n/a | yes |

## Outputs

No outputs.

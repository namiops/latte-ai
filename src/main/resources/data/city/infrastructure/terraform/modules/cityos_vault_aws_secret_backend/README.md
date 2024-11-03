## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | >= 3.5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |
| <a name="provider_local"></a> [local](#provider\_local) | n/a |
| <a name="provider_vault"></a> [vault](#provider\_vault) | >= 3.5.0 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_iam_access_key.vault](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_access_key) | resource |
| [aws_iam_policy.vault_root](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_user.vault](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user) | resource |
| [aws_iam_user_policy_attachment.vault_root](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user_policy_attachment) | resource |
| [local_file.bootstrap_vars](https://registry.terraform.io/providers/hashicorp/local/latest/docs/resources/file) | resource |
| [vault_aws_secret_backend.default](https://registry.terraform.io/providers/hashicorp/vault/3.5.0/docs/resources/aws_secret_backend) | resource |
| [aws_caller_identity.default](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.vault_aws_root](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_aws_region"></a> [aws\_region](#input\_aws\_region) | n/a | `string` | `"ap-northeast-1"` | no |
| <a name="input_bootstrap_file_name"></a> [bootstrap\_file\_name](#input\_bootstrap\_file\_name) | n/a | `string` | `"bootstrap.auto.tfvars.json"` | no |
| <a name="input_bootstrap_file_path"></a> [bootstrap\_file\_path](#input\_bootstrap\_file\_path) | n/a | `string` | `""` | no |
| <a name="input_bootstrapped"></a> [bootstrapped](#input\_bootstrapped) | n/a | `bool` | `false` | no |
| <a name="input_environment"></a> [environment](#input\_environment) | An environment name used as part of the path to the aws secrets backend | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_user_arn"></a> [iam\_user\_arn](#output\_iam\_user\_arn) | n/a |
| <a name="output_vault_aws_secret_backend"></a> [vault\_aws\_secret\_backend](#output\_vault\_aws\_secret\_backend) | n/a |

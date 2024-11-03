bootstrap
=============

This module uses the [remote_state]() module to initialize an S3 bucket to use for terraform remote state.
It also sets up the IAM service account for Terraform used by our GitHub Actions and adds a secret to GitHub

It must be bootstrapped manually once and then other modules may use the bucket as a remote backend.

Running this code requires valid Admin level AWS credentials for the 835215587209 account and a Owner level github PAT

```
$ export GITHUB_TOKEN=yourtokenhere
$ terraform init
$ terraform apply -auto-approve
```

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | 4.5.0 |
| <a name="requirement_github"></a> [github](#requirement\_github) | 4.18.1 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 4.5.0 |
| <a name="provider_github"></a> [github](#provider\_github) | 4.18.1 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_remote_state"></a> [remote\_state](#module\_remote\_state) | nozaq/remote-state-s3-backend/aws | 1.1.0 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_access_key.terraform](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/resources/iam_access_key) | resource |
| [aws_iam_user.terraform](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/resources/iam_user) | resource |
| [aws_iam_user_policy_attachment.admin_access](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/resources/iam_user_policy_attachment) | resource |
| [aws_iam_user_policy_attachment.remote_state_access](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/resources/iam_user_policy_attachment) | resource |
| [github_actions_secret.terraform_access_key](https://registry.terraform.io/providers/integrations/github/4.18.1/docs/resources/actions_secret) | resource |
| [github_actions_secret.terraform_secret_key](https://registry.terraform.io/providers/integrations/github/4.18.1/docs/resources/actions_secret) | resource |
| [aws_iam_policy.admin_access](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/iam_policy) | data source |

## Inputs

No inputs.

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_dynamodb_table_id"></a> [dynamodb\_table\_id](#output\_dynamodb\_table\_id) | The DynamoDB table to manage lock states. |
| <a name="output_kms_key_id"></a> [kms\_key\_id](#output\_kms\_key\_id) | The KMS customer master key to encrypt state buckets. |
| <a name="output_state_bucket"></a> [state\_bucket](#output\_state\_bucket) | The S3 bucket to store the remote state file. |

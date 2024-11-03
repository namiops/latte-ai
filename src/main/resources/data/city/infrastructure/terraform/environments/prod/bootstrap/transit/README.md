cd transit/bootstrap

rm backend.tf

terraform init

terraform apply -auto-approve

edit ../../common.hcl with new KMS key

terragrunt init -migrate-state to regen backend.tf and migrate

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | = 1.3.9 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 5.3 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | 3.15.0 |

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_bootstrap"></a> [bootstrap](#module\_bootstrap) | ../../../../modules/agora_aws_environment_bootstrap | n/a |

## Resources

No resources.

## Inputs

No inputs.

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ci_runner_iam_arn"></a> [ci\_runner\_iam\_arn](#output\_ci\_runner\_iam\_arn) | IAM role arn for use by ci runner |
<!-- END_TF_DOCS -->
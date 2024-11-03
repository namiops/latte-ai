# Agora Multi BYOB

This module contains the infrastructure resources necessary to allow an IRSA role that has access to buckets in 
multiple other AWS accounts.

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
| Name                                                                                  | Source                          | Version |
|---------------------------------------------------------------------------------------|---------------------------------|---------|
| <a name="module_multi_byob_irsa"></a> [multi\_byob\_irsa](#module\_multi\_byob\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |


## Resources

| Name                                                                                                                                | Type |
|-------------------------------------------------------------------------------------------------------------------------------------|------|
| [aws_iam_policy.s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy)                  | resource |
| [aws_iam_policy_document.s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name                                                                                                    | Description                                                                                                                                | Type     | Default | Required |
|---------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|----------|---------|:--------:|
| <a name="input_bucket_arns"></a> [bucket\_arns](#input\_bucket\_arns)                                   | List of S3 bucket ARNs that the Service Account needs access to                                                                            | `list(string)` | n/a     |   yes    |
| <a name="input_elevated_access_bucket_arns"></a> [elevated\_access\_bucket\_arns](#input\_bucket\_arns) | List of S3 bucket ARNs that the Service Account needs elevated access to                                                                   | `list(string)` | n/a     |   yes    |
| <a name="input_elevated_access_key_arns"></a> [elevated\_access\_bucket\_arns](#input\_bucket\_arns)    | List of KMs key ARNs that the Service Account needs elevated access to (for buket content decryption)                                      | `list(string)` | n/a     |   yes    |
| <a name="input_bucket_dir"></a> [bucket\_dir](#input\_bucket\_dir)                                      | Directory within the S3 buckets that the Service Account needs access to                                                                   | `string` | n/a     |   yes    |
| <a name="input_agora_oidc_providers"></a> [agora\_oidc\_providers](#input\_agora\_oidc\_providers)      | Object that describes the ARN and URL for the OIDC providers to establish trusted providers. URLS MUST omit the protocol (e.g. 'https://`) | map(object) | `null`  |   true   |
| <a name="input_resource_prefix"></a> [resource\_prefix](#input\_resource\_prefix)                       | The string the resource names under this module is prefixed with                                                                           | `string` | n/a     |   yes    |
| <a name="input_service_account"></a> [service\_account](#input\_service\_account)                       | The Service Account name to tie the created role to                                                                                        | `string` | n/a     |   yes    |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | The IRSA role created for use by the ci runner |

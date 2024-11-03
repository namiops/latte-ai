# Agora Log Collector S3

This module packages the infrastructural resources required to implement
data pipeline for Log Collector S3 logs upload.

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

| Name | Source | Version |
|------|--------|---------|
| <a name="module_log_collector_irsa"></a> [log\_collector\_irsa](#module\_log\_collector\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.log_collector_s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy_document.log_collector_s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_bucket_arns"></a> [bucket\_arns](#input\_bucket\_arns) | List of S3 bucket ARNs that Log Collector needs access to | `list(string)` | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | cluster's OIDC ARN to delegate trust to | `string` | n/a | yes |
| <a name="input_resource_prefix"></a> [resource\_prefix](#input\_resource\_prefix) | The string the resource names under this module is prefixed with | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | The IRSA role created for use by the ci runner |

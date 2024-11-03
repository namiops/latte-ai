# Agora IoTA SmartHome

This module packages the infrastructural resources required to implement
data pipeline for SmartHome IoT data.

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
| <a name="module_iota_smarthome_hudi"></a> [iota\_smarthome\_hudi](#module\_iota\_smarthome\_hudi) | terraform-aws-modules/s3-bucket/aws | 3.5.0 |
| <a name="module_iota_smarthome_hudi_transformer_role"></a> [iota\_smarthome\_hudi\_transformer\_role](#module\_iota\_smarthome\_hudi\_transformer\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |
| <a name="module_iota_smarthome_irsa"></a> [iota\_smarthome\_irsa](#module\_iota\_smarthome\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.iota_smarthome_hudi_s3_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.smarthome_s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_kms_key.iota_smarthome_hudi](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_iam_policy_document.iota_smarthome_hudi_s3_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.smarthome_s3_access](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_bucket_arns"></a> [bucket\_arns](#input\_bucket\_arns) | List of S3 bucket ARNs that IoTA needs access to | `list(string)` | n/a | yes |
| <a name="input_bucket_kms_key_arns"></a> [bucket\_kms\_key\_arns](#input\_bucket\_kms\_key\_arns) | List of kms key ARNs that IoTA needs access to | `list(string)` | n/a | yes |
| <a name="input_destination_namespace"></a> [destination\_namespace](#input\_destination\_namespace) | The service's namespace where copy and transform spark jobs will be deployed | `string` | `"iot"` | no |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | cluster's OIDC ARN to delegate trust to | `string` | n/a | yes |
| <a name="input_resource_prefix"></a> [resource\_prefix](#input\_resource\_prefix) | The string the resource names under this module is prefixed with | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn) | The IRSA role created for use by the ci runner |

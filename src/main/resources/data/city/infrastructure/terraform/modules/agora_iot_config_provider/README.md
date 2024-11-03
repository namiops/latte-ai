# Agora IoT Config provider

This module packages the infrastructural resources for parameter in iot.

After creating this module, it will create the resource to access parameter store in the path iota/$tenant_name/*. Each path is also map to it's own KMS key to access the parameter.

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 3.73 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 3.73 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="aws_iot_config_provider_irsa"></a> [aws\_iot\_config\_provider\_irsa](#aws\_iot\_config\_provider\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |


## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.access_ssm_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_kms_key.iot_config_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_alias.ota_events_parameters_put_key_aliases](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_alias) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="resource_prefix"></a> [resource\_prefix](#input\_resource\_prefix) | Prefix of the resource name. Used this to differentiate the IoT config provider for the environment stage e.g. agora-lab | string | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | Cluster's OIDC ARN to delegate trust to | `string` | n/a | yes |
| <a name="input_tenants"></a> [tenants](#input\_tenants) | List for tenants name to used to create it's KMS keys for the parameter secrets | `list(string)` | n/a | yes |
| <a name="input_region"></a> [region](#input\_region) | AWS region to create the parameter store resources | `string` | n/a | yes |

## Outputs

| Name                                                                                          | Description                                            |
|-----------------------------------------------------------------------------------------------|--------------------------------------------------------|
| <a name="output_iam_role_arn"></a> [iam\_role\_arn](#output\_iam\_role\_arn)                  | The IRSA role created by this module                   |
| <a name="output_kms_key_ids"></a> [kms\_key\_ids](#output\_kms\_key\_ids)                     | Map of the KMS key IDs by tenant                       |

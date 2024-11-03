## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | ~> 1.1.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 4.35.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_aws_ebs_csi_driver_iam_role"></a> [aws\_ebs\_csi\_driver\_iam\_role](#module\_aws\_ebs\_csi\_driver\_iam\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |
| <a name="module_kafka"></a> [kafka](#module\_kafka) | ./kafka | n/a |
| <a name="module_kafka-privatelink"></a> [kafka-privatelink](#module\_kafka-privatelink) | ./kafka-privatelink | n/a |
| <a name="module_logging_stack_lab"></a> [logging\_stack\_lab](#module\_logging\_stack\_lab) | ../../../modules/agora_logging_stack | n/a |
| <a name="module_network"></a> [network](#module\_network) | ./network | n/a |
| <a name="module_pca"></a> [pca](#module\_pca) | ./pca | n/a |
| <a name="module_proxy-bastion"></a> [proxy-bastion](#module\_proxy-bastion) | ./proxy-bastion | n/a |

## Resources

| Name | Type |
|------|------|
| [aws_vpc_endpoint_service.cityos-ingress](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_endpoint_service) | resource |
| [aws_eks_cluster.lab](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/eks_cluster) | data source |
| [aws_iam_openid_connect_provider.lab](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_openid_connect_provider) | data source |
| [aws_lb.cityos-ingress](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/lb) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_wit_vpc_endpoint_principals"></a> [wit\_vpc\_endpoint\_principals](#input\_wit\_vpc\_endpoint\_principals) | AWS ARNs of accounts we accept aws\_vpc\_endpoints from for istio ingress | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ebs_csi_iam_role_arn"></a> [ebs\_csi\_iam\_role\_arn](#output\_ebs\_csi\_iam\_role\_arn) | IRSA Role ARN needed for Annotation |
| <a name="output_lab_logging_stack_iam_role_arm"></a> [lab\_logging\_stack\_iam\_role\_arm](#output\_lab\_logging\_stack\_iam\_role\_arm) | IRSA Role ARN needed for Annotation for Lab Logging Stack |
| <a name="output_lab_logging_stack_s3_admin_name"></a> [lab\_logging\_stack\_s3\_admin\_name](#output\_lab\_logging\_stack\_s3\_admin\_name) | Name of S3 Bucket for Admin Configuration for Lab Logging Stack |
| <a name="output_lab_logging_stack_s3_chunks_name"></a> [lab\_logging\_stack\_s3\_chunks\_name](#output\_lab\_logging\_stack\_s3\_chunks\_name) | Name of S3 Bucket for for Lab Logging Stack |
| <a name="output_lab_logging_stack_s3_ruler_name"></a> [lab\_logging\_stack\_s3\_ruler\_name](#output\_lab\_logging\_stack\_s3\_ruler\_name) | Name of S3 Bucket for Ruler for Lab Logging Stack |

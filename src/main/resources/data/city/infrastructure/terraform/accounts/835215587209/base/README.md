## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | 4.5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 4.5.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_aws_ebs_csi_driver_iam_role"></a> [aws\_ebs\_csi\_driver\_iam\_role](#module\_aws\_ebs\_csi\_driver\_iam\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |
| <a name="module_cityos_efs_csi_file_system"></a> [cityos\_efs\_csi\_file\_system](#module\_cityos\_efs\_csi\_file\_system) | ../../../modules/cityos_efs_file_system | n/a |
| <a name="module_efs_csi_irsa"></a> [efs\_csi\_irsa](#module\_efs\_csi\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |
| <a name="module_kafka"></a> [kafka](#module\_kafka) | ../../../modules/cityos_kafka | n/a |

## Resources

| Name | Type |
|------|------|
| [aws_vpc_endpoint_service.cityos-ingress](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/resources/vpc_endpoint_service) | resource |
| [aws_vpc_ipv4_cidr_block_association.cityos_dev_vpc_secondary](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/resources/vpc_ipv4_cidr_block_association) | resource |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/caller_identity) | data source |
| [aws_eks_cluster.current](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/eks_cluster) | data source |
| [aws_iam_openid_connect_provider.current](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/iam_openid_connect_provider) | data source |
| [aws_lb.cityos-ingress](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/lb) | data source |
| [aws_security_groups.eks_shared_node_security_groups](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/security_groups) | data source |
| [aws_subnets.private_subnets](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/subnets) | data source |
| [aws_vpc.cityos_dev_vpc](https://registry.terraform.io/providers/hashicorp/aws/4.5.0/docs/data-sources/vpc) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_azure_ad"></a> [azure\_ad](#input\_azure\_ad) | Azure AD OIDC provider config | `map(any)` | n/a | yes |
| <a name="input_efs_csi_file_systems"></a> [efs\_csi\_file\_systems](#input\_efs\_csi\_file\_systems) | Parameters for EFS Filesystem | <pre>map(object({<br>    performance_mode                = string<br>    throughput_mode                 = string<br>    provisioned_throughput_in_mibps = string<br>    lifecycle_policy = list(object({<br>      transition_to_ia                    = string<br>      transition_to_primary_storage_class = string<br>    }))<br>  }))</pre> | n/a | yes |
| <a name="input_wit_vpc_endpoint_principals"></a> [wit\_vpc\_endpoint\_principals](#input\_wit\_vpc\_endpoint\_principals) | AWS ARNs of accounts we accept aws\_vpc\_endpoints from for istio ingress | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ebs_csi_iam_role_arn"></a> [ebs\_csi\_iam\_role\_arn](#output\_ebs\_csi\_iam\_role\_arn) | IRSA Role ARN needed for Annotation |
| <a name="output_efs_csi_iam_role_arn"></a> [efs\_csi\_iam\_role\_arn](#output\_efs\_csi\_iam\_role\_arn) | IRSA Role ARN needed for Annotation |
| <a name="output_efs_file_system_ids"></a> [efs\_file\_system\_ids](#output\_efs\_file\_system\_ids) | EFS Filesystem Map of IDs associated to Name needed for parameters in StorageClass |
| <a name="output_vpc_id"></a> [vpc\_id](#output\_vpc\_id) | vpc id for this project |

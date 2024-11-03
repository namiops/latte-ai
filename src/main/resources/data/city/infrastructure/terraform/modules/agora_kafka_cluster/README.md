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
| <a name="module_awspca_issuer_irsa_role"></a> [awspca\_issuer\_irsa\_role](#module\_awspca\_issuer\_irsa\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |

## Resources

| Name | Type |
|------|------|
| [aws_acmpca_certificate.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate) | resource |
| [aws_acmpca_certificate_authority.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate_authority) | resource |
| [aws_acmpca_certificate_authority_certificate.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate_authority_certificate) | resource |
| [aws_acmpca_permission.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_permission) | resource |
| [aws_cloudwatch_log_group.broker_cloudwatch_logs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudwatch_log_group) | resource |
| [aws_iam_policy.kafka_pca_cert_issuer_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_kms_key.broker_cloudwatch_logs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_key.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_msk_cluster.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster) | resource |
| [aws_msk_cluster_policy.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster_policy) | resource |
| [aws_msk_configuration.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_configuration) | resource |
| [aws_security_group.kafka_subnet_sg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_security_group.kafka_zookeeper_node_sg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.cloudwatch_logs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.kafka_pca_cert_issuer_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_partition.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/partition) | data source |
| [aws_region.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/region) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_awspca_issuer_namespace_service_accounts"></a> [awspca\_issuer\_namespace\_service\_accounts](#input\_awspca\_issuer\_namespace\_service\_accounts) | Namespace and name of AWSPCA issuer serviceaccount | `list(string)` | n/a | yes |
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | The name that will be associated with the MSK cluster | `string` | n/a | yes |
| <a name="input_cluster_policy"></a> [cluster\_policy](#input\_cluster\_policy) | [account\_id]: [role\_name]: read\_topics: - "" write\_topics: - "" consumer\_groups: - "" | <pre>map(map(object({<br>    read_topics     = list(string)<br>    write_topics    = list(string)<br>    consumer_groups = list(string)<br>  })))</pre> | n/a | yes |
| <a name="input_eks_cluster_oidc_provider_arn"></a> [eks\_cluster\_oidc\_provider\_arn](#input\_eks\_cluster\_oidc\_provider\_arn) | EKS cluster OIDC provider ARN | `string` | n/a | yes |
| <a name="input_kafka_client_ipv4_cidr_blocks"></a> [kafka\_client\_ipv4\_cidr\_blocks](#input\_kafka\_client\_ipv4\_cidr\_blocks) | List of IPv6 CIDR blocks that will be permitted to talk to kafka | `list(string)` | `[]` | no |
| <a name="input_kafka_client_ipv6_cidr_blocks"></a> [kafka\_client\_ipv6\_cidr\_blocks](#input\_kafka\_client\_ipv6\_cidr\_blocks) | List of IPv6 CIDR blocks that will be permitted to talk to kafka | `list(string)` | `[]` | no |
| <a name="input_kafka_client_security_groups"></a> [kafka\_client\_security\_groups](#input\_kafka\_client\_security\_groups) | List of security group IDs that will be permitted for | `list(string)` | `[]` | no |
| <a name="input_msk_log_retention_in_days"></a> [msk\_log\_retention\_in\_days](#input\_msk\_log\_retention\_in\_days) | log retention period in days for MSK cluster access logs | `number` | `90` | no |
| <a name="input_server_properties"></a> [server\_properties](#input\_server\_properties) | String to be passed as the server.properties setting for MSK | `string` | n/a | yes |
| <a name="input_subnet_ids"></a> [subnet\_ids](#input\_subnet\_ids) | The list of Kafka Cluster ENIs will be attached to | `list(string)` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | The ID of the AWS VPC the Kafka cluster and associated resources will be deployed into | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_awspca_arn"></a> [awspca\_arn](#output\_awspca\_arn) | The ARN of the AWS Private CA created for use with MSK |
| <a name="output_awspca_issuer_iam_role_arn"></a> [awspca\_issuer\_iam\_role\_arn](#output\_awspca\_issuer\_iam\_role\_arn) | The ARN of the IAM role created for cert-manager acmpca issuer |
| <a name="output_bootstrap_brokers_tls"></a> [bootstrap\_brokers\_tls](#output\_bootstrap\_brokers\_tls) | Kafka TLS connection host:port pairs |

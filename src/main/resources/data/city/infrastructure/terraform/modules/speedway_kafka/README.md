# Speedway Kafka (KRaft mode)

This doc is generated with the following command:

```shell
terraform-docs markdown table --output-file README.md --output-mode inject .
```

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34.0 |
| <a name="requirement_dns"></a> [dns](#requirement\_dns) | >= 3.4.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34.0 |
| <a name="provider_dns"></a> [dns](#provider\_dns) | >= 3.4.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_awspca_issuer_irsa_role"></a> [awspca\_issuer\_irsa\_role](#module\_awspca\_issuer\_irsa\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |
| <a name="module_kafka_operator_irsa_role"></a> [kafka\_operator\_irsa\_role](#module\_kafka\_operator\_irsa\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |

## Resources

| Name | Type |
|------|------|
| [aws_acmpca_certificate.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate) | resource |
| [aws_acmpca_certificate_authority.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate_authority) | resource |
| [aws_acmpca_certificate_authority_certificate.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate_authority_certificate) | resource |
| [aws_acmpca_permission.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_permission) | resource |
| [aws_cloudwatch_log_group.broker_cloudwatch_logs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudwatch_log_group) | resource |
| [aws_iam_policy.kafka_operator](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.kafka_pca_cert_issuer_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_kms_alias.msk_logging_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_alias) | resource |
| [aws_kms_key.msk_kms_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_key.msk_logging_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_lb.msk_broker_nlb](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb) | resource |
| [aws_lb_listener.msk_broker_listener_iam](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_listener.msk_broker_listener_jmx_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_listener.msk_broker_listener_node_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_listener.msk_broker_listener_tls](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_target_group.msk_broker_tg_iam](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group) | resource |
| [aws_lb_target_group.msk_broker_tg_jmx_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group) | resource |
| [aws_lb_target_group.msk_broker_tg_node_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group) | resource |
| [aws_lb_target_group.msk_broker_tg_tls](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group) | resource |
| [aws_lb_target_group_attachment.msk_broker_tg_attachment_iam](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_lb_target_group_attachment.msk_broker_tg_attachment_jmx_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_lb_target_group_attachment.msk_broker_tg_attachment_node_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_lb_target_group_attachment.msk_broker_tg_attachment_tls](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_msk_cluster.kafka_cluster](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster) | resource |
| [aws_msk_cluster_policy.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster_policy) | resource |
| [aws_msk_configuration.kafka_cluster_config](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_configuration) | resource |
| [aws_s3_bucket.broker_logs_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) | resource |
| [aws_s3_bucket.nlb_access_logs_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) | resource |
| [aws_s3_bucket_policy.allow_access_from_nlb](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_policy) | resource |
| [aws_s3_bucket_public_access_block.broker_logs_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_public_access_block) | resource |
| [aws_s3_bucket_public_access_block.nlb_logs_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_public_access_block) | resource |
| [aws_security_group.kafka_sg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_security_group.nlb_sg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_security_group_rule.allow_all_outbound](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_from_nlb_to_kafka_iam](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_from_nlb_to_kafka_iam_node_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_from_nlb_to_kafka_jmx_metrics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_from_nlb_to_kafka_tls](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_iam_inbound](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_jmx_metrics_inbound](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_node_metrics_inbound](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_tls_inbound](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_vpc_endpoint_service.msk_broker_service](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_endpoint_service) | resource |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.allow_access_from_nlb_to_kms](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.kafka_operator](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.kafka_pca_cert_issuer_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.kms_logs_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_network_interface.msk_enis](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/network_interface) | data source |
| [aws_partition.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/partition) | data source |
| [aws_region.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/region) | data source |
| [dns_a_record_set.msk_bootstrap_broker_ips](https://registry.terraform.io/providers/hashicorp/dns/latest/docs/data-sources/a_record_set) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_awspca_issuer_namespace_service_accounts"></a> [awspca\_issuer\_namespace\_service\_accounts](#input\_awspca\_issuer\_namespace\_service\_accounts) | Namespace and name of AWSPCA issuer serviceaccount | `list(string)` | n/a | yes |
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | The name that will be associated with the MSK cluster | `string` | n/a | yes |
| <a name="input_cluster_policy"></a> [cluster\_policy](#input\_cluster\_policy) | [account\_id]: [role\_name]: read\_topics: - "" write\_topics: - "" consumer\_groups: - "" | <pre>map(map(object({<br>    read_topics     = list(string)<br>    write_topics    = list(string)<br>    consumer_groups = list(string)<br>  })))</pre> | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | n/a | `string` | n/a | yes |
| <a name="input_is_msk_ready"></a> [is\_msk\_ready](#input\_is\_msk\_ready) | A boolean variable to tell if MSK is ready. This should be set to false when deploying this first time. When true, the private link will be created. | `bool` | `false` | no |
| <a name="input_kafka_operator_namespace_service_accounts"></a> [kafka\_operator\_namespace\_service\_accounts](#input\_kafka\_operator\_namespace\_service\_accounts) | Namespace and name kafka-operator serviceaccount | `list(string)` | n/a | yes |
| <a name="input_kafka_version"></a> [kafka\_version](#input\_kafka\_version) | n/a | `string` | n/a | yes |
| <a name="input_kafka_volume_size"></a> [kafka\_volume\_size](#input\_kafka\_volume\_size) | n/a | `number` | n/a | yes |
| <a name="input_msk_broker_cloudwatch_log_retention_days"></a> [msk\_broker\_cloudwatch\_log\_retention\_days](#input\_msk\_broker\_cloudwatch\_log\_retention\_days) | n/a | `number` | n/a | yes |
| <a name="input_private_link_allowed_principals"></a> [private\_link\_allowed\_principals](#input\_private\_link\_allowed\_principals) | List of ARN or AWS Principals that are allowed to use the Endpoint Service | `list(string)` | n/a | yes |
| <a name="input_server_properties"></a> [server\_properties](#input\_server\_properties) | String to be passed as the server.properties setting for MSK | `string` | n/a | yes |
| <a name="input_subnet_ids"></a> [subnet\_ids](#input\_subnet\_ids) | The list of Kafka Cluster ENIs will be attached to | `list(string)` | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | The ID of the AWS VPC the Kafka cluster and associated resources will be deployed into | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_aws_msk_cluster_arn"></a> [aws\_msk\_cluster\_arn](#output\_aws\_msk\_cluster\_arn) | The ARN of the AWS Private CA created for use with MSK |
| <a name="output_awspca_arn"></a> [awspca\_arn](#output\_awspca\_arn) | The ARN of the AWS Private CA created for use with MSK |
| <a name="output_awspca_issuer_iam_role_arn"></a> [awspca\_issuer\_iam\_role\_arn](#output\_awspca\_issuer\_iam\_role\_arn) | The ARN of the IAM role created for cert-manager acmpca issuer |
| <a name="output_bootstrap_brokers_tls"></a> [bootstrap\_brokers\_tls](#output\_bootstrap\_brokers\_tls) | Kafka TLS connection host:port pairs |
| <a name="output_kafka_operator_iam_role_arn"></a> [kafka\_operator\_iam\_role\_arn](#output\_kafka\_operator\_iam\_role\_arn) | The ARN of the IAM role created for cert-manager acmpca issuer |
<!-- END_TF_DOCS -->

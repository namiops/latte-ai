## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_acmpca_certificate_authority.kafka_pca](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acmpca_certificate_authority) | resource |
| [aws_cloudwatch_log_group.broker_cloudwatch_logs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudwatch_log_group) | resource |
| [aws_iam_policy.kafka_pca_cert_issuer_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_user.kafka_pca_cert_issuer_user](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user) | resource |
| [aws_iam_user_policy_attachment.attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user_policy_attachment) | resource |
| [aws_kms_key.kms-key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_msk_cluster.kafka-cluster](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster) | resource |
| [aws_msk_cluster_policy.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster_policy) | resource |
| [aws_msk_configuration.kafka-cluster-config](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_configuration) | resource |
| [aws_route_table.kafka-route-table](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route_table) | resource |
| [aws_route_table_association.kafka-route-table-association](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route_table_association) | resource |
| [aws_s3_bucket.broker_logs_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) | resource |
| [aws_security_group.kafka-subnet-sg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_security_group.kafka-zookeeper-node-sg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_subnet.kafka-subnet](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/subnet) | resource |
| [aws_iam_policy_document.kafka_pca_cert_issuer_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_security_groups.eksctl_node_groups](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/security_groups) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | The name that will be associated with the MSK cluster | `string` | n/a | yes |
| <a name="input_cluster_policy"></a> [cluster\_policy](#input\_cluster\_policy) | [account\_id]: [role\_name]: read\_topics: - "" write\_topics: - "" consumer\_groups: - "" | <pre>map(map(object({<br>    read_topics     = list(string)<br>    write_topics    = list(string)<br>    consumer_groups = list(string)<br>  })))</pre> | n/a | yes |
| <a name="input_eksctl_node_groups"></a> [eksctl\_node\_groups](#input\_eksctl\_node\_groups) | List of EKS node group names, including wildcards, used as a filter for allowing inbound traffic from EKS when creating the Kafka security group | `list(string)` | n/a | yes |
| <a name="input_server_properties"></a> [server\_properties](#input\_server\_properties) | String to be passed as the server.properties setting for MSK | `string` | n/a | yes |
| <a name="input_subnet_list"></a> [subnet\_list](#input\_subnet\_list) | List of private VPC subnets to use for deployment of Kafka brokers. Objects are of the form {name: string, cidr-block: string, availability-zone: string} | <pre>list(<br>    object({<br>      name              = string<br>      cidr_block        = string<br>      availability_zone = string<br>    })<br>  )</pre> | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | The ID of the AWS VPC the Kafka cluster and associated resources will be deployed into | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_cityos-kafka-bootstrap_brokers_tls"></a> [cityos-kafka-bootstrap\_brokers\_tls](#output\_cityos-kafka-bootstrap\_brokers\_tls) | CityOS Kafka TLS connection host:port pairs |

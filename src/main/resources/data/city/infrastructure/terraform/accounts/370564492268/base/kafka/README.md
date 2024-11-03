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
| [aws_cloudwatch_log_group.kafka-broker-cloudwatch-logs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudwatch_log_group) | resource |
| [aws_kms_key.kafka-kms-key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_msk_cluster.wcm-cityos-kafka-cluster](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster) | resource |
| [aws_msk_cluster_policy.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_cluster_policy) | resource |
| [aws_msk_configuration.wcm-cityos-kafka-cluster-config](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_configuration) | resource |
| [aws_msk_scram_secret_association.example](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/msk_scram_secret_association) | resource |
| [aws_s3_bucket.broker_logs_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) | resource |
| [aws_secretsmanager_secret.kafka-user-login-admin-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-cis-a-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-cis-b-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-consumer-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-iot-consumer-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-iot-producer-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-producer-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.kafka-user-login-schema-registry-secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-admin-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-cis-a-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-cis-b-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-consumer-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-iot-consumer-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-iot-producer-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-producer-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.kafka-user-login-schema-registry-secret-policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-admin-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-cis-a-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-cis-b-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-consumer-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-iot-consumer-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-iot-producer-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-producer-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_secretsmanager_secret_version.kafka-user-login-schema-registry-secret-string](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_iam_policy_document.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_policy"></a> [cluster\_policy](#input\_cluster\_policy) | [account\_id]: [role\_name]: read\_topics: - "" write\_topics: - "" consumer\_groups: - "" | <pre>map(map(object({<br>    read_topics     = list(string)<br>    write_topics    = list(string)<br>    consumer_groups = list(string)<br>  })))</pre> | n/a | yes |
| <a name="input_kafka-private-subnet-sg-id"></a> [kafka-private-subnet-sg-id](#input\_kafka-private-subnet-sg-id) | n/a | `any` | n/a | yes |
| <a name="input_kafka-subnet-id-1"></a> [kafka-subnet-id-1](#input\_kafka-subnet-id-1) | n/a | `any` | n/a | yes |
| <a name="input_kafka-subnet-id-2"></a> [kafka-subnet-id-2](#input\_kafka-subnet-id-2) | n/a | `any` | n/a | yes |
| <a name="input_kafka-subnet-id-3"></a> [kafka-subnet-id-3](#input\_kafka-subnet-id-3) | n/a | `any` | n/a | yes |
| <a name="input_wcm_cityos_pca-id"></a> [wcm\_cityos\_pca-id](#input\_wcm\_cityos\_pca-id) | n/a | `any` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_wcm-cityos-kafka-bootstrap_brokers_tls"></a> [wcm-cityos-kafka-bootstrap\_brokers\_tls](#output\_wcm-cityos-kafka-bootstrap\_brokers\_tls) | CityOS Kafka TLS connection host:port pairs |
| <a name="output_wcm-cityos-kafka-zookeeper_connect_string"></a> [wcm-cityos-kafka-zookeeper\_connect\_string](#output\_wcm-cityos-kafka-zookeeper\_connect\_string) | n/a |

# Speedway Kafka IAM for k8s (ACL)

This module implements the option2 of [TN-0440 Open Agora Kafka to External AWS Accounts - Google Docs](https://docs.google.com/document/d/1XlVC_D3tfyGYYfSR7ShztdQQ-cfuZVSjYxAIid7W7Is/edit#heading=h.5qm13wuvtiz9)

This doc is generated with the following command:

```shell
terraform-docs markdown table --output-file README.md --output-mode inject .
```

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_irsa_role"></a> [irsa\_role](#module\_irsa\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.11.1 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.common](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.consumer_groups](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.read_topics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.write_topics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy_document.common](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.consumer_groups](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.read_topics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.write_topics](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_aws_msk_cluster_arn"></a> [aws\_msk\_cluster\_arn](#input\_aws\_msk\_cluster\_arn) | This will be like `arn:aws:kafka:us-west-2:730335515733:cluster/dbx-poc-kafka-cluster/ed5cb18a-35c8-4c04-8e83-263f52ef1f3b-4` | `string` | n/a | yes |
| <a name="input_consumer_groups_prefix"></a> [consumer\_groups\_prefix](#input\_consumer\_groups\_prefix) | This will be like `namespaceA.` | `string` | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | Use the `oidc_provider_arn` output of agora\_aws\_oidc\_provider | `string` | n/a | yes |
| <a name="input_iam_suffix"></a> [iam\_suffix](#input\_iam\_suffix) | This will be suffixed to the created IAM role/policy. | `string` | `"-msk"` | no |
| <a name="input_k8s_namespace_service_account"></a> [k8s\_namespace\_service\_account](#input\_k8s\_namespace\_service\_account) | this will be like `<k8s_namespace>:<k8s_service_account>` | `string` | n/a | yes |
| <a name="input_read_topic_prefixes"></a> [read\_topic\_prefixes](#input\_read\_topic\_prefixes) | This will be like [`namespaceA.`, `namespaceB.`] | `list(string)` | n/a | yes |
| <a name="input_write_topic_prefixes"></a> [write\_topic\_prefixes](#input\_write\_topic\_prefixes) | This will be like [`namespaceA.`, `namespaceB.`] | `list(string)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_irsa_role_arn"></a> [irsa\_role\_arn](#output\_irsa\_role\_arn) | The ARN of the IAM role created for IRSA |
<!-- END_TF_DOCS -->

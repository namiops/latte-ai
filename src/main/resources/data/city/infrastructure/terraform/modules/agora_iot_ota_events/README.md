# Agora OTA Events

This module packages the infrastructural resources for OTA events.

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
| <a name="aws_ota_events_irsa"></a> [aws\_s3\_ota\_irsa](#aws\_ota\_events\_irsa) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | 5.0.0 |

## Resources

| Name | Type |
|------|------|
| [aws_sqs_queue.event_queues](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/sqs_queue) | resource |
| [aws_sqs_queue_policy.event_queues_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/sqs_queue_policy) | resource |
| [aws_sqs_queue.event_dead_letter_queues](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/sqs_queue) | resource |
| [aws_kms_key.sqs_keys](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_kms_alias.sqs_key_aliases](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_alias) | resource |
| [aws_iam_policy.event_queues_message_consume_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.event_queues_kms_decrypt_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy_document.kms_sqs_key_policy_doc](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_ota_events_config_map"></a> [ota\_events\_config\_map](#input\_ota\_events\_config\_map) | Map from tenant to s3 bucket arn that send notification to the SQS` | map | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | cluster's OIDC ARN to delegate trust to | `string` | n/a | yes |
| <a name="input_resource_prefix"></a> [resource\_prefix](#input\_resource\_prefix) | The string the resource names under this module is prefixed with | `string` | n/a | yes |

## Outputs

| Name                                                                                          | Description                                            |
|-----------------------------------------------------------------------------------------------|--------------------------------------------------------|
| <a name="output_sqs_queue_urls"></a> [iam\_sqs\_queue\_urls](#output\_sqs\_queue\_urls)                  | The URLs of the SQS queues.         |
| <a name="output_sqs_queue_arns"></a> [iam\_sqs\_queue\_arns](#output\_sqs\_queue\_arns)                | The ARNs of the SQS queues.       |


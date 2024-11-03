<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 5.3 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 5.38.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_agora_bucket_oidc_provider"></a> [agora\_bucket\_oidc\_provider](#module\_agora\_bucket\_oidc\_provider) | ../../../../../../../terraform/modules/agora_aws_oidc_provider | n/a |
| <a name="module_smarthome_irsa_iam_role"></a> [smarthome\_irsa\_iam\_role](#module\_smarthome\_irsa\_iam\_role) | ../../../../../../../terraform/modules/agora_object_storage/modules/cross_account_irsa_iam_role | n/a |
| <a name="module_user_bucket"></a> [user\_bucket](#module\_user\_bucket) | terraform-aws-modules/s3-bucket/aws | 3.5.0 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.smarthome_iam_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_role_policy_attachment.smarthome_policy_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_kms_key.bucket_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/kms_key) | resource |
| [aws_iam_policy_document.allow_hms_pod_doc](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.smarthome_policy_doc](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_agora_eks_oidc_provider_url"></a> [agora\_eks\_oidc\_provider\_url](#input\_agora\_eks\_oidc\_provider\_url) | agora eks cluster oidc provider url | `string` | `"oidc.eks.ap-northeast-1.amazonaws.com/id/6650625D283964F0BAC2BC1AE56FE0E9"` | no |
| <a name="input_agora_hms_role_arn"></a> [agora\_hms\_role\_arn](#input\_agora\_hms\_role\_arn) | agora role that agora's hms pod assumes | `list(string)` | <pre>[<br>  "arn:aws:iam::074769536177:role/agora-dev2-IRSA-HMS-S3"<br>]</pre> | no |
| <a name="input_agora_service_account_name"></a> [agora\_service\_account\_name](#input\_agora\_service\_account\_name) | the workload in the agora eks cluster that accesses source and destination path is associated with this service account | `string` | `"dev2-iot-tsdb-sh-byob"` | no |
| <a name="input_agora_service_account_namespace"></a> [agora\_service\_account\_namespace](#input\_agora\_service\_account\_namespace) | the workload in the agora eks cluster that accesses source and destination path is deployed in this namespace | `string` | `"fss-smarthome"` | no |
| <a name="input_bucket_name"></a> [bucket\_name](#input\_bucket\_name) | s3 bucket name | `string` | `"iota-tsdb-sandbox-dev2"` | no |
| <a name="input_destination_path"></a> [destination\_path](#input\_destination\_path) | the s3 sub-directory where hudi table will be created | `string` | `"hudi"` | no |
| <a name="input_policy_name"></a> [policy\_name](#input\_policy\_name) | iam policy associated with the irsa role | `string` | `"iota-dev2-byob-policy"` | no |
| <a name="input_role_name"></a> [role\_name](#input\_role\_name) | iam role that realizes irsa | `string` | `"iota-dev2-byob-role"` | no |
| <a name="input_source_path"></a> [source\_path](#input\_source\_path) | the s3 sub-directory where aws timestream will be unloaded | `string` | `"src"` | no |

## Outputs

No outputs.
<!-- END_TF_DOCS -->
<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_logging_bucket"></a> [logging\_bucket](#module\_logging\_bucket) | ./modules/logging_bucket | n/a |
| <a name="module_user_buckets"></a> [user\_buckets](#module\_user\_buckets) | ./modules/user_bucket | n/a |

## Resources

No resources.

## Inputs

| Name                                                                                                                                 | Description                                                                                                                                                                                                                                                                                                                                                                  | Type                                                                                                                                                  | Default | Required |
|--------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|---------|:--------:|
| <a name="input_agora_bucket_oidc_provider_arn"></a> [agora\_bucket\_oidc\_provider\_arn](#input\_agora\_bucket\_oidc\_provider\_arn) | Agora Bucket's OIDC provider ARN                                                                                                                                                                                                                                                                                                                                             | `string`                                                                                                                                              | n/a | yes |
| <a name="input_agora_eks_oidc_provider_url"></a> [agora\_eks\_oidc\_provider\_url](#input\_agora\_eks\_oidc\_provider\_url)          | Agora EKS's OIDB provider URL                                                                                                                                                                                                                                                                                                                                                | `string`                                                                                                                                              | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment)                                                                  | Environment name                                                                                                                                                                                                                                                                                                                                                             | `string`                                                                                                                                              | n/a | yes |
| <a name="input_namespaces"></a> [namespaces](#input\_namespaces)                                                                     | Map of the information of the target namespaces.<br>    The strucure of each namespace informatino should be the following.<br>    {<br>      <arbitary key> = {<br>        bucketName = <S3 bucket name><br>        namespace: <namespace> <br>   keyAccessorARN: <keyAccessorARN> <br>     spaces = {<br>          <space\_name> = <IAM role name><br>        }<br>      } | <pre>map(object({<br>    bucketName = string<br>    namespace  = string<br>    keyAccessorARN  = string<br>   spaces     = map(string)<br>  }))</pre> | n/a | yes |
| <a name="input_add_on_bucket_policies"></a> [add_on_bucket_policies](#input\_add\_on\_bucket\_policies)                              | Map of bucket name to an additional policy that should be attached to the bucket                                                                                                                                                                                                                                                                                             | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_logging_bucket_arn"></a> [logging\_bucket\_arn](#output\_logging\_bucket\_arn) | ARN of the S3 bucket for keeping access logs for all user buckets |
| <a name="output_user_buckets"></a> [user\_buckets](#output\_user\_buckets) | List of the information of the created user buckets and its spaces |
<!-- END_TF_DOCS -->
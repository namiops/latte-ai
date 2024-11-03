## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 5.3 |

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_groups"></a> [groups](#module\_groups) | ./modules/group_creation | n/a |
| <a name="module_logging_bucket"></a> [logging\_bucket](#module\_logging\_bucket) | ./modules/logging_bucket | n/a |
| <a name="module_rds_instances"></a> [rds\_instances](#module\_rds\_instances) | ./modules/rds_instance | n/a |

## Resources

No resources.

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_allowed_principals"></a> [allowed\_principals](#input\_allowed\_principals) | Principal to allow to use the VPC Endpoint Service | `string` | n/a | yes |
| <a name="input_eks_oidc_provider_arn"></a> [eks\_oidc\_provider\_arn](#input\_eks\_oidc\_provider\_arn) | ARN of the OIDC provider on the external AWS account (fetched by agora\_aws\_oidc\_provider module) | `string` | n/a | yes |
| <a name="input_eks_oidc_provider_url"></a> [eks\_oidc\_provider\_url](#input\_eks\_oidc\_provider\_url) | URL of the EKS cluster OIDC provider. Do not include protocol (https://) | `string` | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment (dev3/prod) the RDS and related resources are created in | `string` | n/a | yes |
| <a name="input_namespaces"></a> [namespaces](#input\_namespaces) | Map of the information of the target namespaces.<br>    The strucure of each namespace information should be the following.<br>    {<br>      <arbitary key> = {<br>        instanceClass: <Size of the DB instance to provision><br>        allocatedStorage: <Size of the DB in GB to provision><br>        username: <Master username of the DB><br>        serviceAccountName: <ServiceAccount name><br>        namespace: <namespace><br>        iamRoleName: <IAM role name><br>      }<br>    } | <pre>map(object({<br>    instanceClass = string<br>    allocatedStorage = string<br>    username = string<br>    serviceAccountName = string<br>    namespace = string<br>    iamRoleName = string<br>  }))</pre> | n/a | yes |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | Identifier of the VPC in which to create target resources | `string` | n/a | yes |
| <a name="input_vpc_subnet_ids"></a> [vpc\_subnet\_ids](#input\_vpc\_subnet\_ids) | One or more VPC subnet IDs to associate with the new proxy | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_rds_instances"></a> [rds\_instances](#output\_rds\_instances) | List of the information from the created RDS and related resources |

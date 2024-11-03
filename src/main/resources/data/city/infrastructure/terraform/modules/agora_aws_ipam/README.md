<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_ipam_regional_resources"></a> [ipam\_regional\_resources](#module\_ipam\_regional\_resources) | ./modules/ipam_region | n/a |

## Resources

| Name | Type |
|------|------|
| [aws_vpc_ipam.this](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_ipam) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_regions"></a> [regions](#input\_regions) | n/a | `map(any)` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ipam_id"></a> [ipam\_id](#output\_ipam\_id) | n/a |
| <a name="output_regions"></a> [regions](#output\_regions) | n/a |
<!-- END_TF_DOCS -->
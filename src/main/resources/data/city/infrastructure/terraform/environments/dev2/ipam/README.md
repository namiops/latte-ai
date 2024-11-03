<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | = 1.3.9 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 5.3 |

## Providers

No providers.

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_ipam"></a> [ipam](#module\_ipam) | ../../../modules/agora_aws_ipam | n/a |
| <a name="module_ipam_resource_discovery_associator"></a> [ipam\_resource\_discovery\_associator](#module\_ipam\_resource\_discovery\_associator) | ../../../modules/agora_aws_ipam/modules/resource_discovery_associator | n/a |
| <a name="module_mgmt_east_ipam"></a> [mgmt\_east\_ipam](#module\_mgmt\_east\_ipam) | ../../../modules/agora_aws_ipam/modules/resource_discovery | n/a |
| <a name="module_mgmt_west_ipam"></a> [mgmt\_west\_ipam](#module\_mgmt\_west\_ipam) | ../../../modules/agora_aws_ipam/modules/resource_discovery | n/a |
| <a name="module_worker1_east_ipam"></a> [worker1\_east\_ipam](#module\_worker1\_east\_ipam) | ../../../modules/agora_aws_ipam/modules/resource_discovery | n/a |
| <a name="module_worker1_west_ipam"></a> [worker1\_west\_ipam](#module\_worker1\_west\_ipam) | ../../../modules/agora_aws_ipam/modules/resource_discovery | n/a |

## Resources

No resources.

## Inputs

No inputs.

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ipam"></a> [ipam](#output\_ipam) | n/a |
<!-- END_TF_DOCS -->
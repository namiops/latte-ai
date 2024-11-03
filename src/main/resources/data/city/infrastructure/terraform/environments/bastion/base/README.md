# Imported VPC


Imports an existing VPC and modifies it to work with Gen3 network.

## Before importing

Before importing the VPC, make sure that a connector Transit Gateway is shared with the account containing the VPC using RAM share.

~~Run the import script in `bin/import-vpc.sh` and follow the instructions.~~ Runbook script will be added at a later date.

## Import

1. Get configurations for existing VPC to import
    - Input Account ID and VPC ID
    - Fetch Subnet configurations from VPC and Account ID
2. Bootstrap imported account with a Delegation Role
3. Update main module to share a connector Transit Gateway to this account
4. Import existing VPC
    1. Generate terraform code
    2. Generate configuration code
    3. Import resources into terraform
5. Modify existing VPC to provide IPv6 support
    1. Temporarily disable IPv6 features on the subnet (`enable_dns64=false`, `enable_resource_name_dns_aaaa_record_on_launch=false`)
    2. Run `terragrunt apply`
    3. Re-enable IPv6 features on the subnet (`enable_dns64=true`, `enable_resource_name_dns_aaaa_record_on_launch=true`)
    4. Run `terragrunt apply`

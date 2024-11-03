# Import existing AzureAD resources to Terraform

Terraform generally manages AzureAD resources by using Azure Service Principal as a client.
EnTec has provided the Service Principals (SP in short) to Agora by [woven-azure-ad-management](https://github.com/wp-prodsec/woven-azure-ad-management/tree/main/vending-machine/prod/terraform-users):

1. For DEV: [tf-dev-cityos-l5oq-client](https://portal.azure.com/#view/Microsoft_AAD_IAM/ManagedAppMenuBlade/~/Overview/objectId/763f8fbb-7fa4-4072-b799-6f13106aa79a/appId/930b090e-7a10-4931-9989-e4c341a47883).
   - Object ID: `763f8fbb-7fa4-4072-b799-6f13106aa79a`.

2. For PROD: [tf-prod-cityos-uy43-client](https://portal.azure.com/#view/Microsoft_AAD_IAM/ManagedAppMenuBlade/~/Overview/objectId/58ebf29c-1389-49ad-a624-d2aeaf0a2217/appId/5f24ebcb-df7e-48e1-87ac-2ff28fea126f/preferredSingleSignOnMode~/null/servicePrincipalType/Application/fromNav/).
   - Object ID: `58ebf29c-1389-49ad-a624-d2aeaf0a2217`.

Essentially, we need to add this client ID to a list of resource owners, before we can use Terraform to automate the resources.
This can be done only via a command line and commands are different depending on a resource type.

**NOTE:** 
- Only the owner of the resource has permission to perform the operation.
- In below example, DEV service principal is used.

* [Import existing AzureAD resources to Terraform](#import-existing-azuread-resources-to-terraform)
  * [Pre-requisites](#pre-requisites)
  * [AzureAD Group](#azuread-group)
    * [1 Get a group ID by name](#1-get-a-group-id-by-name)
    * [2 Add a client ID to owners](#2-add-a-client-id-to-owners)
    * [3 Create a new Terraform module](#3-create-a-new-terraform-module)
    * [4 Import existing group into terraform state](#4-import-existing-group-into-terraform-state)
    * [5 Create a pull request](#5-create-a-pull-request)


## Pre-requisites

1. Run `az login` before performing any operations.
2. Load `tf-dev-cityos-l5oq-client`'s credential in env variables for Terraform

```bash
export TF_VAR_AAD_CLIENT_ID=`az keyvault secret show --vault-name tf-dev-cityos-l5oq-kv --name client-client-id | jq -r ".value"`
export TF_VAR_AAD_CLIENT_SECRET=`az keyvault secret show --vault-name tf-dev-cityos-l5oq-kv --name client-client-secret | jq -r '.value'`
```

## AzureAD Group

### 1 Get a group ID by name

This step is to make sure that there is no duplicate group name and get an ID for importing a resource into a Terraform state.

```bash
$ az ad group show -g <group_name> | grep id
```

Example 

```bash
$ az ad group show -g wiz-agora-reader | grep id
  "id": "64f3935b-8ec5-42a7-9c1d-472b7f90860f",
```

### 2 Add a client ID to owners

**NOTE:** `sp_client_id` is a client ID of the Service Principal that EnTec provided for Agora.

```bash
$ az ad group owner add -g <group_id> --owner-object-id <sp_client_id>
```

Example

```bash
az ad group owner add -g 64f3935b-8ec5-42a7-9c1d-472b7f90860f --owner-object-id 763f8fbb-7fa4-4072-b799-6f13106aa79a
```

### 3 Create a new Terraform module

- Add group members, and the default group owners to [dev/aad/config.auto.tfvars.json](../../../terraform/accounts/dev/aad/config.auto.tfvars.json). Note that a root key differentiates a group type. For example, adding a new group type for Wiz access:
  
  ```json
    "default_wiz_owners": [
      "misty.natsumi@woven-planet.global"
    ],
    "wiz_groups": [
    {
      "name": "admin",
      "description": "wiz agora admin",
      "members": [
        "ash.kethchum@woven-planet.global"
      ]
    },
    {
      "name": "reader",
      "description": "wiz agora reader",
      "members": [
        "james.kojirou@woven-planet.global",
        "jessie.musashi@woven-planet.global"
      ]
    }
  ]
  ```

- Add new Terraform variables to [dev/aad/variables.tf](../../../terraform/accounts/dev/aad/variables.tf). Variable names MUST be the same as JSON keys from the previous step (`default_wiz_owners` and `wiz_groups` in the example).
- Add a new module at [dev/aad/main.tf](../../../terraform/accounts/dev/aad/main.tf). For example:

  ```terraform
  module "wiz_agora_group" {
    for_each = tomap({
      # NOTE: Change a group member variable here
      for i, group in var.wiz_groups :
      group.name => group
    })

    source = "../../../modules/agora_aad_platform_group"

    group_description         = each.value.description
    name                      = format("wiz-agora-%s", each.value.name)
    # NOTE: Change a default owner variable here
    owner_names               = var.default_wiz_owners
    member_names              = each.value.members
    ci_service_principal_name = local.ci_service_principal_name
  }
  ```


### 4 Import existing group into terraform state

```bash
$ terraform init
$ terraform import 'module.<new_group_module>["<group_name_postfix>"].azuread_group.this' <aad_group_id>
```

For example, group `wiz-agora-reader` and its group ID `64f3935b-8ec5-42a7-9c1d-472b7f90860f`

```bash
$ terraform init
$ terraform import 'module.wiz_agora_group["reader"].azuread_group.this' 64f3935b-8ec5-42a7-9c1d-472b7f90860f
```


### 5 Create a pull request

Create a pull request and check the output from `Protected Workflow - AAD tf plan` workflow.
Make sure that the workflow works fine and shows nothing changed.

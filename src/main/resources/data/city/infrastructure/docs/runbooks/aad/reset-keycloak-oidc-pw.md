# Reset Keycloak OIDC's password

## Introduction

On the DEV cluster, we allow users to use their Azure account to authenticate with Agora's identity service by intregating Azure Application with our Keycloak service. 
There is one service principal associated with the Azure Application and we need to manually rotate its password every 3 months.

See more details about the implementation in `city/infrastructure/terraform/modules/agora_aad_oidc/service_registrations.tf`. 

## Prerequisites

1. Access to Vault (namespace: `ns_dev/ns_cityos_platform`)
2. Access to Terraform state on AWS account `835215587209`
3. Access to EKS DEV cluster with a privilege to impersonate an administrator role

If you need these accesses, please submit an Infra operational request at [Agora INFRA Operation Request](https://wkf.ms/3INlZXL)

## Set up required environment variables

These env variables are required when you run terraform.

```bash
export VAULT_ADDR=https://dev.vault.w3n.io
export VAULT_NAMESPACE=ns_dev/ns_cityos_platform
export VAULT_TOKEN=<your_vault_access_token>
export TF_VAR_AAD_CLIENT_ID=<aad_client_id>
export TF_VAR_AAD_CLIENT_SECRET=<aad_client_secret>
export AWS_PROFILE=<profile_for_aws_835215587209>
```

**NOTE:** 
- You can get `<aad_client_id>` and `<aad_client_secret>` from Vault
  - namespace: `ns_dev/ns_cityos_platform`
  - path: `kv-ci/aad-protected-runner`
- `<profile_for_aws_835215587209>` is a profile config for accessing AWS `835215587209` when you authenticate via aws cli `aws sso login --profile <profile_for_aws_835215587209>`

## Steps to Update OIDC Password

**NOTE:** The following steps target only 2 modules to limit terraform changes by using terraform option `-target <target_resource>`: `module.keycloak_oidc` (for DEV) in `keycloak` folder and `module.cityos_vault_keycloak_backend` in `vault` folder. 
Change this module name to target the OIDC and Vault backend for another environment. For example, `module.keycloak_oidc_lab2`. It is recommended to rotate a password of one environment at a time.

1. In `city/infrastructure/terraform/modules/agora_aad_oidc/service_registrations.tf`, force changing password by
   1. Commenting out `resource "time_rotating" "oidc_password"` resource
   2. Commenting out `rotate_when_changed` block in `resource "azuread_service_principal_password" "oidc_principal_password"`
   
```terraform
...
# resource "time_rotating" "oidc_password" {
#   rotation_months = 3
# }

# Create Service Principal password
resource "azuread_service_principal_password" "oidc_principal_password" {
#  rotate_when_changed = {
#    rotation = time_rotating.oidc_password.id
#  }
  service_principal_id = azuread_service_principal.oidc_principal.id
}
```

2. Change the working directory to `city/infrastructure/terraform/accounts/835215587209/keycloak`. Run `terraform plan -target module.keycloak_oidc` to confirm the change which removes `time_rotating.oidc_password` and recreates `azuread_service_principal_password.oidc_principal_password` like the below log. Then, run `terraform apply -target module.keycloak_oidc`.

```terraform
Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  - destroy
-/+ destroy and then create replacement

Terraform will perform the following actions:

  # module.keycloak_oidc.azuread_service_principal_password.oidc_principal_password must be replaced
-/+ resource "azuread_service_principal_password" "oidc_principal_password" {
      + display_name         = (known after apply)
      ~ end_date             = "2025-01-10T06:05:52Z" -> (known after apply)
      ~ id                   = "1b8104c6-4b31-4fa2-ad67-4590261eeb76/password/e4b3d27d-2d77-4f47-8dbf-9ee3076b2301" -> (known after apply)
      ~ key_id               = "e4b3d27d-2d77-4f47-8dbf-9ee3076b2301" -> (known after apply)
      - rotate_when_changed  = {
          - "rotation" = "2023-01-10T06:05:51Z"
        } -> null # forces replacement
      ~ start_date           = "2023-01-10T06:05:52Z" -> (known after apply)
      ~ value                = (sensitive value)
        # (1 unchanged attribute hidden)
    }

  # module.keycloak_oidc.time_rotating.oidc_password will be destroyed
  # (because time_rotating.oidc_password is not in configuration)
  - resource "time_rotating" "oidc_password" {
      - day              = 10 -> null
      - hour             = 6 -> null
      - id               = "2023-01-10T06:05:51Z" -> null
      - minute           = 5 -> null
      - month            = 4 -> null
      - rfc3339          = "2023-01-10T06:05:51Z" -> null
      - rotation_months  = 3 -> null
      - rotation_rfc3339 = "2023-04-10T06:05:51Z" -> null
      - second           = 51 -> null
      - unix             = 1681106751 -> null
      - year             = 2023 -> null
    }

Plan: 1 to add, 0 to change, 2 to destroy.
```

3. Revert the change at step 1. This will create `time_rotating.oidc_password` and change the password again by recreating `azuread_service_principal_password.oidc_principal_password` but its expiration date is set in this step.
4. From `city/infrastructure/terraform/accounts/835215587209/keycloak` folder, run `terraform plan -target module.keycloak_oidc` to confirm the change. Then, run `terraform apply -target module.keycloak_oidc`.
5. Go to the Vault IaC folder at `city/infrastructure/terraform/accounts/835215587209/vault`.
6. Run `terraform plan -target module.cityos_vault_keycloak_backend` to confirm the change. Then, `terraform apply -target module.cityos_vault_keycloak_backend` to sync the update password from Azure App `cityos_dev_keycloak` to Vault `woven_azuread-client-secret`.

```terraform
Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  ~ update in-place

Terraform will perform the following actions:

  # module.cityos_vault_keycloak_backend.vault_generic_secret.azuread_service_principal[0] will be updated in-place
  ~ resource "vault_generic_secret" "azuread_service_principal" {
      ~ data_json           = (sensitive value)
        id                  = "kv-dev/woven_azuread-client-secret"
        # (4 unchanged attributes hidden)
    }

Plan: 0 to add, 1 to change, 0 to destroy.
```

7. Now, the new password is ready on Vault, and the Keycloak service can access it. Restart Keycloak's StatefulSet on DEV cluster.

```bash
kubectx dev
kubectl -n id rollout restart statefulset/keycloak
```

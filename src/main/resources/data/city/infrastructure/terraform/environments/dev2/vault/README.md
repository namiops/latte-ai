# Vault IaC

* [Vault IaC](#vault-iac)
  * [Vault for Speedway DEV](#vault-for-speedway-dev)
    * [Folder structure](#folder-structure)
    * [Pre-requisite steps for automation with GitHub runner](#pre-requisite-steps-for-automation-with-github-runner)


## Vault for Speedway DEV

### Folder structure

| Team folder | Vault Namespace                  |
| ----------- | -------------------------------- |
| cicd        | TBD                              |
| id          | ns_stargate/ns_dev_agoraid       |
| infra       | ns_stargate/ns_dev_agorainfra    |
| data        | TBD                              |
| devrel      | ns_stargate/ns_dev_agoradevrel   |
| services    | ns_stargate/ns_dev_agoraservices |

### Pre-requisite steps for automation with GitHub runner

These steps create JWT auth method and Vault role for our CI runner to manage your Vault namespace.
Make sure to update some environment variables per your namespace.

1. Login to Vault with Vault CLI

```bash
unset VAULT_NAMESPACE
unset VAULT_TOKEN
export VAULT_ADDR=https://dev.vault.tmc-stargate.com
vault login -method=oidc -path=gac
export VAULT_TOKEN=$(cat ~/.vault-token)
```

2. Set a env var for your namespace

```bash
export VAULT_NAMESPACE="ns_stargate/ns_dev_agora<YOUR_TEAM>"
```

3. Go to [agora_vault_ci_jwt_engine folder](../../../modules/agora_vault_ci_jwt_engine/).

```bash
cd ../../../modules/agora_vault_ci_jwt_engine/
```

4. Delete any existing terraform state files and cache. You can skip this step if you have never run terraform in this folder

```bash
rm -rf .terraform*
rm -f terrform.tfstate*
```

5. Set Terraform variables by ENVs

```bash
export TF_VAR_namespace=${VAULT_NAMESPACE}
export TF_VAR_environment="dev3"
```

6. Initialize Terraform module and check its plan

```bash
terraform init
terraform plan
```

You should see a plan creating 3 resources like below

```tf
data.vault_policy_document.github: Reading...
data.vault_policy_document.github: Read complete after 0s [id=1716121604]

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  + create

Terraform will perform the following actions:

  # vault_jwt_auth_backend.github will be created
  + resource "vault_jwt_auth_backend" "github" {
      + accessor           = (known after apply)
      + bound_issuer       = "https://token.actions.githubusercontent.com/stargate"
      + description        = "GitHUB OIDC JWT Auth backend"
      + disable_remount    = false
      + id                 = (known after apply)
      + local              = false
      + namespace_in_state = true
      + oidc_discovery_url = "https://token.actions.githubusercontent.com/stargate"
      + path               = "jwt-github-dev3"
      + tune               = (known after apply)
      + type               = "jwt"
    }

  # vault_jwt_auth_backend_role.github will be created
  + resource "vault_jwt_auth_backend_role" "github" {
      + backend                      = "jwt-github-dev3"
      + bound_audiences              = [
          + "https://github.com/wp-wcm",
        ]
      + bound_claims                 = {
          + "sub" = "repo:wp-wcm/protected-workflows:ref:refs/heads/main"
        }
      + bound_claims_type            = (known after apply)
      + clock_skew_leeway            = 0
      + disable_bound_claims_parsing = false
      + expiration_leeway            = 0
      + id                           = (known after apply)
      + not_before_leeway            = 0
      + role_name                    = "ci_runner_github_oidc"
      + role_type                    = "jwt"
      + token_policies               = [
          + "github-ci-runner-dev3",
        ]
      + token_type                   = "default"
      + user_claim                   = "aud"
      + user_claim_json_pointer      = false
      + verbose_oidc_logging         = false
    }

  # vault_policy.github will be created
  + resource "vault_policy" "github" {
      + id     = (known after apply)
      + name   = "github-ci-runner-dev3"
      + policy = <<-EOT
            path "*" {
              capabilities = ["create", "read", "update", "delete", "list", "sudo"]
            }
        EOT
    }

Plan: 3 to add, 0 to change, 0 to destroy.

Changes to Outputs:
  + ci_runner_engine = "jwt-github-dev3"
```

7. Deploy Vault JWT auth method and role by

```bash
terraform apply
```

8. Verify on the Vault side by Vault CLI (or go to Vault UI [dev.vault.tmc-stargate.com](https://dev.vault.tmc-stargate.com) and go to `Access` menu to see if there is a new Vault auth method `jwt-github-dev3`).

```bash
$ vault auth list
...                                                 n/a
jwt-github-dev3/                  jwt         auth_jwt_12345678         GitHUB OIDC JWT Auth backend                               n/a
```

```bash
$  vault read auth/jwt-github-dev3/role/ci_runner_github_oidc
Key                        Value
---                        -----
allowed_redirect_uris      <nil>
bound_audiences            [https://github.com/wp-wcm]
bound_claims               map[sub:[repo:wp-wcm/protected-workflows:ref:refs/heads/main]]
```

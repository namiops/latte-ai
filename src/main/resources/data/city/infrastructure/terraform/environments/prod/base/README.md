# Base IaC for network resources in all accounts

This folder is for deploying core networking resources owned by Agora Infra such as:
- VPC
- Transit Gateways
- Transit Gateway attachments
- etc

Even though these resources are on different accounts, they are tightly coupled.
Then, we should manage them in one Terraform folder.

## Terraform state and IAM role

The terraform state file is stored on `prod-transit`.

By the Agora module `agora_aws_environment_bootstrap/modules/child_account` in the bootstrap folder, we've set up IAM assumable roles to allow a user with `prod-transit` profile to interact with all accounts.

## To apply change

1. AWS SSO log in with `prod-transit` profile

```base
aws sso login --profile prod-transit
export AWS_PROFILE=prod-transit
```

2. Check TF plan

```bash
terragrunt init
terragrunt plan
```

3. Apply the change

```bash
terragrunt apply
```

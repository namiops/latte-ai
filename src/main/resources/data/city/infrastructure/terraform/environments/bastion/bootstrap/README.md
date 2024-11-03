# How to bootstrap

```bash
cd bootstrap
rm backend.tf

unset VAULT_NAMESPACE
export VAULT_ADDR=https://dev.vault.tmc-stargate.com
vault login -method=oidc

export VAULT_TOKEN=$(cat $HOME/.vault-token)
export VAULT_NAMESPACE=ns_dev/ns_cityos_platform

terraform init
terraform apply -auto-approve

# edit ../common.hcl with new KMS key
terragrunt init -migrate-state # to regen backend.tf and migrate
```

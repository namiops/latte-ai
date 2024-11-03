cd transit/bootstrap

rm backend.tf

terraform init

terraform apply -auto-approve

edit ../../common.hcl with new KMS key

terragrunt init -migrate-state to regen backend.tf and migrate

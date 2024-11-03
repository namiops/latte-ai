# README

As for now, we to run apply with profile `prod-storage-valet`.
Later, the terraform state will be migrated to `prod-transit` and will be automated by CD pipeline.
Here is the initial set up the resources first time.

1. Authenticate with AWS

```bash
aws sso login --profile prod-storage-valet
```

2. Remove `backend.tf` to use a local state since there no S3 bucket for terraform state at the moment.

3. Run terraform

```
terraform init
terraform plan
terraform apply
```

4. Get KMS's key id from the local state by

```
$ terraform state show module.remote_state.aws_kms_key.this | grep key_id
```

5. Create `backend.tf` and update `<KMS_KEY_ID>`

```tf
terraform {
  backend "s3" {
    bucket         = "wcm-agora-tfstate-prod-storage-valet"
    key            = "bootstrap/storage-valet/terraform.tfstate"
    region         = "ap-northeast-1"
    encrypt        = true
    kms_key_id     = <KMS_KEY_ID>
    dynamodb_table = "tf-remote-state-lock-prod-storage-valet"
  }
}

```

6. Move the local state to S3 bucket by

```bash
terraform init -migrate-state
```

The command will show a prompt like the below. Enter `yes`.

```
Initializing the backend...
Do you want to copy existing state to the new backend?
  Pre-existing state was found while migrating the previous "local" backend to the
  newly configured "s3" backend. No existing state was found in the newly
  configured "s3" backend. Do you want to copy this state to the new "s3"
  backend? Enter "yes" to copy and "no" to start with an empty state.

  Enter a value: 
```

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | = 1.3.9 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 5.3 |
| <a name="requirement_vault"></a> [vault](#requirement\_vault) | 3.15.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 5.53.0 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_remote_state"></a> [remote\_state](#module\_remote\_state) | nozaq/remote-state-s3-backend/aws | 1.5.0 |

## Resources

| Name | Type |
|------|------|
| [aws_s3_bucket_lifecycle_configuration.finops_mandatory](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_lifecycle_configuration) | resource |

## Inputs

No inputs.

## Outputs

No outputs.
<!-- END_TF_DOCS -->

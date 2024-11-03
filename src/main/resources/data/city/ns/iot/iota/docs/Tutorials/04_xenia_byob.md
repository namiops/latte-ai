# Xenia Bring Your Own Bucket (BYOB) Quickstart

## Overview

This quickstart tutorial shows you how to grant Xenia access to your S3 bucket, notify the Agora Services Team of your bucket, and distribute updates to your devices using your bucket.

## What you'll learn

From this tutorial, you will learn how to apply our directory structure and policy to your bucket, as well as how to distribute your update in the following steps:

* [1. Run the Terraform (TF) module against your bucket/AWS account](#1-run-the-terraform-modules-against-your-bucketaws-account)
* [2. Notify the Services Team](#2-notify-the-services-team)
* [3. Distribute a release to your device or group](#3-distribute-a-release-to-your-device-or-group)

## What you’ll need

Before starting this tutorial, you should already have the following prepared:

* Your own bucket
* The release file that you want to distribute to your device
* A provisioned device that you want to release to ([docs here](../Tasks/iotactl.md/#iotactl-provision))

## Steps

### 0. Setup Terraform
#### Through your CICD process
* Recommended for Production
#### Running manually
* For Dev and testing purpose
* [Install terraform](https://developer.hashicorp.com/terraform/install?product_intent=terraform), run terraform plan and terraform apply to apply the S3 bucket to your account, and
* Switching to the correct aws profile (see instruction from [go/aws](go/aws) -> Access keys)

### 1. Run the Terraform modules against your bucket/AWS account
If you are operating in the monorepo, you can find our TF modules in the locations below. Refer to the respective `README.md` for the field specifications.

* [Xenia bucket structure](https://github.com/wp-wcm/city/tree/main/infrastructure/terraform/modules/agora_aws_services_ota_bucket_user) (`infrastructure/terraform/modules/agora_aws_services_ota_bucket_user`)
* [Access](https://github.com/wp-wcm/city/tree/main/infrastructure/terraform/modules/agora_aws_services_multi_byob_user) (`infrastructure/terraform/modules/agora_aws_services_multi_byob_user`)

* Update the terraform modules' input with the follow variable
  * `YOUR_BUCKET_NAME` : The bucket name of your choice
  * `XENIA_ACCESSOR_ARN`
    * Speedway dev : arn:aws:iam::074769536177:role/dev-iot-ota-s3_byob_account_federated_role
    * Speedway prod : arn:aws:iam::211125476557:role/prod-iot-ota-s3_byob_account_federated_role
  * `XENIA_SQS_ARN`
    * Speedway dev : arn:aws:sqs:ap-northeast-1:074769536177:agora-speedway-dev-iota-`YOUR_TENANT`-events
    * Speedway prod : arn:aws:sqs:ap-northeast-1:211125476557:agora-prod-iota-`YOUR_TENANT`-events

!!! Note
    Your `XENIA_ACCESSOR_ARN`, `XENIA_SQS_ARN` value varies based on environment. Please reach out to the [Agora Services Team](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A) (@agora-services) if you have any question regarding choosing the values.

* Creating your `main.tf` and populating the fields, you should have something like this:

```
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.34"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  profile = "<YOUR-PROFILE>"
  allowed_account_ids = ["<YOUR_ACCOUNT>"]
}

module "ota_bucket_struct"{
  source = "infrastructure/terraform/modules/agora_aws_services_ota_bucket_user"
  groups = ["<LIST_OF_YOUR_GROUPS>"] // e.g. ["test-group1", "real-group2"]
  tenant = "<YOUR_TENANT>"
  bucket = "<YOUR_BUCKET_NAME>"
}

module "byob_user"{
  source = "infrastructure/terraform/modules/agora_aws_services_multi_byob_user"
  accessor_arn = "<XENIA_ACCESSOR_ARN>"
  bucket = "<YOUR_BUCKET_NAME>"
  depends_on = [module.ota_bucket_struct]
}
```
see [appendix](#appendix) for the full working example


* Apply the resources with terraform
  * via manual terraform apply
  ```bash
    terraform init
    terraform plan
    terraform apply
  ```

The `OTA Bucket Structure` TF module will create the necessary directory hierarchy in your bucket: e.g., for a `test` tenant with `group1`, `group2`, and `group3`, it will generate the following directory structure:

```
ota/
    test/
        group1/
            <your releases>
        group2/
            <your other releases>
        group3/
            <even more releases>
```

!!!Note It is then up to you or your CI pipeline to populate the correct directory with the correct release file(s) - we do not have permissions to upload to/delete from your bucket!

### 2. Notify the Services Team
Once your bucket is set up, contact [our team](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A) (@agora-services) at [#wcm-org-agora-services]() and provide the following information 
  * Tenant name
  * Bucket name
  * KMS key: This is generated after apply the above terraform in KMS -> Customer managed keys

We will then update our SQS/roles/policies to allow our Xenia service to access your bucket.

!!! for service team, refer to this [SOPs](https://github.com/wp-wcm/city/blob/main/ns/iot/iota-ota/README.md#allowing-a-team-to-byob) for setting up BYOB resources.

### 3. Add bucket_notification and re-run the terraform
Once service team complete the setup above, re-run the terraform main.tf to complete the bucket_notification setup, this will allow your bucket to send notification to services team's SQS.
```
resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket   = <bucket-name>
  queue {
    id            = "ota-event"
    queue_arn     = "<XENIA_SQS_ARN>"
    events        = ["s3:ObjectCreated:*", "s3:ObjectTagging:*", "s3:ObjectRemoved:*"]
    filter_prefix = "ota/"
  }
}
```

### 4. Upload Release
To upload a release, upload the release file to your bucket with the following filepath:
```
ota/<YOUR_TENANT>/<YOUR_GROUP>/<RELEASE_NAME>
```
To set the release type (which is required for distributing your release) you can set the `x-agora-ota-release-type` tag for your S3 file. The release type will default to `software` if you do not specify this tag.

### 5. Distribute a release to your device or group
For detailed instructions of the process, please refer to [this tutorial](../Concepts/OTA/cli_tutorial.md#2-distribute-a-release-to-a-device-or-a-group).

## Conclusion
Following this tutorial, you should be able to distribute a release from your own bucket to your IoT devices.

If you run into any issues, please contact us for assistance.


## Appendix
For reference, below is a full working example, including bucket and KMS key creation:

```tf
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.34"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region  = "ap-northeast-1"
  profile = "<your-profile>>"
  allowed_account_ids = ["<your account>"]
}

### START Bucket & KMS Policy Creation ###

module "byob_user_test_byob" {
  source = "../../../../../infrastructure/terraform/modules/agora_aws_services_multi_byob_user"
  accessor_arn = "arn:aws:iam::074769536177:role/dev2-iot-ota-s3_byob_account_federated_role"
  bucket = "<your bucket name>"
  kms_key_arn = aws_kms_key.ota_key.arn
  scope_dir = "ota"
}

### END Bucket & KMS Policy Creation ###


### START Bucket Setup ###
module "iot_ota_blocks" {

  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "3.11.0"

  bucket                                = "<YOUR_BUCKET_NAME>"
  attach_policy                         = true
  block_public_acls                     = true
  block_public_policy                   = true
  ignore_public_acls                    = true
  restrict_public_buckets               = true
  attach_deny_insecure_transport_policy = true
  attach_require_latest_tls_policy      = true

  policy = module.byob_user_test_byob.bucket_policy.json

  versioning = {
    status     = true
    mfa_delete = false
  }

  server_side_encryption_configuration = {
    rule = {
      apply_server_side_encryption_by_default = {
        kms_master_key_id = aws_kms_key.ota_key.arn
        sse_algorithm     = "aws:kms"
      }
    }
  }
}


module "test_byob_bucket_struct" {
  source = "../../../../../infrastructure/terraform/modules/agora_aws_services_ota_bucket_user"
  groups = ["<your group1>", "<your group12>"]
  tenant = "<your tenant>"
  bucket = module.iot_ota_blocks.s3_bucket_id
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket   = <bucket-name>
  queue {
    id            = "ota-event"
    queue_arn     = <XENIA_SQS_ARN>
    events        = ["s3:ObjectCreated:*", "s3:ObjectTagging:*", "s3:ObjectRemoved:*"]
    filter_prefix = "ota/"
  }
}

### END Bucket Setup ###

### START KMS Setup ###
resource "aws_kms_key" "ota_key" {
  description         = "KMS key for S3 object OTA"
  enable_key_rotation = true
}

data "aws_iam_policy_document" "concat_key_policies"{
  source_policy_documents = [data.aws_iam_policy_document.key_policy_document.json, module.byob_user_test_byob.kms_key_policy.json]
}

resource "aws_kms_key_policy" "key_policy_attachment" {
  key_id = aws_kms_key.ota_key.id
  policy = data.aws_iam_policy_document.concat_key_policies.json
}

data "aws_caller_identity" "current" {}

data "aws_iam_policy_document" "key_policy_document" {
  statement {
    actions = [
      "kms:*"
    ]
    principals {
      identifiers = [data.aws_caller_identity.current.account_id]
      type        = "AWS"
    }

    resources = [aws_kms_key.ota_key.arn]
  }
}

### END KMS Setup ###
```

# Deployed Terraform Setup
## Intro

Our Terraform (TF) setup for OTA is a bit complex due to a few requirements:
* We need to have full r/w access to our own communal bucket
* We also need read-only access to all users' buckets (for generating pre-signed URLs)
* We have SQS queues per tenant that take in bucket events

All of these need to be accessible from a single role, since one Service Account in kubernetes can only have one AWS 
Role associated with it.

## What's the setup?

Simply put, we need to run our TF against a few different accounts, breaking it down further we have 3+ accounts that we
need to interact with:
* Main Agora AWS account: this is where we create our role, SQS queues, and allow our role to access different buckets
* Storage AWS Account: we update our bucket policy to allow us to access the bucket with our Agora AWS account role
* User AWS Account: Every user that uses Bring Your Own Bucket (BYOB) needs to give access to our role, and send events to our SQS queue

Each part here has its own TF module, which we'll go through in turn and explain its general purpose. For further 
information you can look at the module itself, we'll focus on the _what_ each TF module does and _why_ we need it - not 
_how_ it does it.

## Main Agora Account

This account holds the bulk of our TF, and will require the most setup. It is the account that we have the most insight 
into however, and can debug most easily.

In the main Agora AWS account we set up a few things, starting withe the Role our Service Account assumes:
* For our AWS Role the [multi_byob](../../../infrastructure/terraform/modules/agora_aws_services_multi_byob/README.md) TF module takes: 
  * A list of buckets it should grant the role access to (and the ARN of the KMS key they are encrypted with)
  * The folder in the bucket(s) that the role should have access to (usually `ota`)
  * The name of the Service Account that can assume this role
* And returns an AWS Role ARN that gets added to the Service Account manifest

Additionally, we also set up an SQS queue for every tenant that uses BYOB:
* This is done with the [agora_iot_ota_events](../../../infrastructure/terraform/modules/agora_iot_ota_events/README.md) TF module, which takes:
  * A map of tenants to buckets
* And returns a list of the SQS queues' ARNs and URLs

| Env          | File Location                                                                                             |
|--------------|-----------------------------------------------------------------------------------------------------------|
| PreProd      | [worker1_east-iot.tf](../../../infrastructure/terraform/environments/dev2/base/worker1_east-iot.tf)       |
| Prod         | [iot-main.tf](../../../infrastructure/terraform/environments/prod/accounts/platform-internal/iot-main.tf) |
| Speedway Dev | [speedway_dev-iot.tf](../../../infrastructure/terraform/environments/dev2/base/speedway_dev-iot.tf)       |

## Storage AWS Account:

We use the Storage team's AWS account for provisioning our "communal" S3 bucket that we use to store non-BYOB tenants' 
releases. That is configured using the Storage team's `agoractl` plugin.

What we need to do is go a step further and alter the bucket and KMS key policy to allow access from the role we created 
in the main Agora Account:
* We start off with the [agora_object_storage](../../../infrastructure/terraform/modules/agora_object_storage) TF module
  * Update it to have `add_on_bucket_policies` populated
  * This should point to an instance of the [agora_aws_services_multi_byob_user_elevated](../../../infrastructure/terraform/modules/agora_aws_services_multi_byob_user_elevated) TF module
  * The value should be something like `{"<bucket_name>": "<JSON Policy>"}`
* We also need to update our `<namespace>.yaml` file to include the ARN of the role that will access the Bucket's KMS key:

This is complicated at best, for that reason we would recommend looking at the following examples of how it is set up:

| Env          | `agora_object_storage` usage                                                                                           | `agora_aws_services_multi_byob_user_elevated` usage                                                                 | `<namespace>`.yaml location                                                                                                   |
|--------------|------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| PreProd      | [bucket1_east-agora_bucket.tf](../../../infrastructure/terraform/environments/dev2/base/bucket1_east-agora_bucket.tf)  | [bucket1_east-iot.tf](../../../infrastructure/terraform/environments/dev2/base/bucket1_east-iot.tf)                 | [iot.yaml](../../../infrastructure/terraform/environments/dev2/base/bucket1_east-agora_bucket/iot.yaml)                       |
| Prod         | [bucket.tf](../../../infrastructure/terraform/environments/prod/accounts/storage-valet/bucket.tf)                      | [bucket-iot.tf](../../../infrastructure/terraform/environments/prod/accounts/storage-valet/bucket-iot.tf)           | [agora-iot-prod.yaml](../../../infrastructure/terraform/environments/prod/accounts/storage-valet/buckets/agora-iot-prod.yaml) |
| Speedway Dev | [bucket1_east-agora_bucket.tf](../../../infrastructure/terraform/environments/dev2/base/bucket1_east-agora_bucket.tf)  | [bucket1_speedway-dev-iot.tf](../../../infrastructure/terraform/environments/dev2/base/bucket1_speedway-dev-iot.tf) | [agora-iot-dev.yaml](../../../infrastructure/terraform/environments/dev2/base/bucket_dev3-agora_bucket/agora-iot-dev.yaml)          |

## User AWS Account:

The user AWS account is relatively straightforward, and is already covered in [our guide](../iota/docs/Tutorials/04_xenia_byob.md) 
for them.

There is no work for us to do from this side, apart from help users when they have issues.
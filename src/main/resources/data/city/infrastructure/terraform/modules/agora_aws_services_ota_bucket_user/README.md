# Agora OTA Bucket - User

This module creates the correct directory structure in your bucket to hold releases.

This should not be run by the Services Team, it is given to the user for them to run.

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 0.13 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 4.34 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | >= 4.34 |

## Resources

| Name                                                                                                                   | Type |
|------------------------------------------------------------------------------------------------------------------------|------|
| [aws_s3_object.tenant-folder](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_object)   | resource |
| [aws_s3_object.group-folders[]](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_object) | resource |

## Inputs

| Name                                                                              | Description                                                                      | Type           | Default          | Required |
|-----------------------------------------------------------------------------------|----------------------------------------------------------------------------------|----------------|------------------|:--------:|
| <a name="input_tenant"></a> [tenant](#input\_tenant)                              | The name of your tenant                                                          | `string`       | n/a              |   yes    |
| <a name="groups"></a> [groups](#input\_groups)                                    | The groups under your tenant                                                     | `list(string)` | n/a              |   yes    |
| <a name="bucket"></a> [bucket](#input\_bucket)                                    | ID of your bucket (not the full ARN)                                             | `string`       | n/a              |   yes    |

## Outputs
None
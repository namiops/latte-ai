## Requirements

| Name            | Version |
|-----------------|---------|
| Terraform       | >= 0.13 |

## Providers
| Name          | Version   |
|---------------|-----------|
| Hashicorp AWS | >= 4.61.0 |

## Modules
None

## Resources

| Name                                                                                                                                                                             | Description                                 | Type     | Default | Required |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------|----------|---------|----------|
| [aws_s3_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket)                                                                           | The Bucket used to store documents          | Resource | null    | yes      |
| [aws_s3_bucket_acl](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_acl)                                                                   | ACL Configuration for the Bucket            | Resource | null    | yes      |
| [aws_s3_bucket_versioning](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_versioning)                                                     | Bucket Versioning configuration             | Resource | null    | yes      |
| [aws_s3_bucket_server_side_encryption_configuration](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_server_side_encryption_configuration) | Bucket Server Side Encryption configuration | Resource | null    | yes      |
| [aws_s3_bucket_public_access_block](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_public_access_block)                                   | Bucket Public Access Block configuration    | Resource | null    | yes      | 
| [aws_iam_user](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user)                                                                             | AWS IAM User configuration                  | Resource | null    | yes      |
| [aws_iam_user_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user_policy)                                                               | AWS IAM User Policy                         | Resource | null    | yes      |
| [aws_iam_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document)                                                    | AWS IAM User Policy Configuration           | Resource | null    | yes      | 


## Inputs

| Name        | Description                      | Type     | Default     | Required |
|-------------|----------------------------------|----------|-------------|----------|
| environment | Environment name policy          | `string` | null        | yes      |
| iam_id      | The name of the desired IAM user | `string` | "backstage" | yes      |
| site_id     | The site ID                      | `string` | null        | yes      |


## Outputs
| Name                 | Description                                                                                                |
|----------------------|------------------------------------------------------------------------------------------------------------|
| s3_bucket_name       | The name of the S3 Bucket                                                                                  |
| s3_bucket_region     | The region location of the Bucket                                                                          |
| backstage_cicd_arn   | The ARN for the CI/CD permissions that can be used for updating the S3 Bucket, for use by CI/CD            |
| backstage_reader_arn | The ARN for the reader permissions that can be used to read the S3 Bucket, for use by the Developer Portal |

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
| [aws_iam_user](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user)                                                                             | AWS IAM User configuration                  | Resource | null    | yes      |
| [aws_iam_user_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user_policy)                                                               | AWS IAM User Policy                         | Resource | null    | yes      |
| [aws_iam_policy_document](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document)                                                    | AWS IAM User Policy Configuration           | Resource | null    | yes      | 

## Inputs

| Name | Description   | Type     | Default     | Required |
|------|---------------|----------|-------------|----------|
| iam_user_name | IAM User Name | `string` | null        | yes      |
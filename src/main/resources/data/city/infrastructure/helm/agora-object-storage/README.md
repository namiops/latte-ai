## NOTES

# agora-object-storage

![Version: 0.0.0](https://img.shields.io/badge/Version-0.0.0-informational?style=flat-square)

The chart generates manifests for using an S3 bucket from pods. The chart will be used with Terraform code through Zebra to prepare the necessary AWS resources, including the S3 bucket.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| awsAccount | string | `nil` | AWS account ID that holds an IAM role for the ServiceAccount. |
| bucketName | string | `nil` | Namespace's S3 bucket name. |
| bucketRegion | string | `"ap-northeast-1"` | AWS region name for the target S3 bucket |
| commonLabels | object | `{"app.kubernetes.io/name":"agora-object-storage"}` | Labels to filter resources created by the chart. |
| prefixes | object | `{}` | Map representing the configured object key prefixes. {    <object key prefix>:      iamRoleName: <IAM role name>     serviceAccountName: <ServiceAccount name>   ... } |

## Usage

We plan to use this chart through a Bazel macro that trigger the code generation of the Terraform code
for actually creating an S3 bucket, IAM roles, and a KMS key, in addition to the K8S resource through this chart.
We will ensure correctly passing the AWS resource information to the chart in the Bazel macro.

If you try to use the chart alone, please make sure the AWS resource is correctly passed.
Otherwise, IRSA won't be correctly configured.

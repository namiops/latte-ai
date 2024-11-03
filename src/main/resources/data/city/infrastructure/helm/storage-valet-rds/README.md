## NOTES

# storage-valet-rds

![Version: 0.0.0](https://img.shields.io/badge/Version-0.0.0-informational?style=flat-square)

The chart generates manifests for using an RDS instance from pods. The chart will be used with Terraform code through Zebra to prepare the necessary AWS resources, including the RDS instance.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| awsAccount | string | `nil` | AWS account ID that holds an IAM role for the ServiceAccount. |
| commonLabels | object | `{"app.kubernetes.io/name":"storage-valet-rds"}` | Labels to filter resources created by the chart. |
| rdsProxyHost | string | `nil` | RDS proxy hostname that the service entry must allow |
| rdsRegion | string | `"ap-northeast-1"` | AWS region name for the target RDS instance |

## Usage

We use this chart through a Bazel macro that trigger the code generation of the k8s manifests.
The terraform for actually creating an S3 bucket, IAM roles, and a KMS key is handled seperately to this chart in the agoractl plugin.
Using the plugin will ensure correctly passing the AWS resource information to the chart in the Bazel macro.

If you try to use the chart alone, please make sure the AWS resources are correctly passed.
Otherwise, IRSA will not be correctly configured.

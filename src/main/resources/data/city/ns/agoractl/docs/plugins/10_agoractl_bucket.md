# Agoractl Bucket

Plugin to generate AWS S3 buckets and resources to support a connection to the
instances.

## Introduction

In order to use external AWS services with workloads running on Agora clusters
the Storage team has created a set of IAM Roles for Service Accounts (IRSA)
enabled services for the quick and easy provisioning of these AWS resources.
To use S3 resources there are 2 steps, creating the terraform resources with a
yaml file,  and creating the service account and service entries with a bazel
target (that executes a helm chart). These steps, while not tremendously
difficult, do require that certain specific pieces of information matches
across seperate files and by implementing this plug can more reliably be
assured to be correct.

Note: While Storage reccommends using agoractl to provision RDS instances, it
does not mandate it. It is entirely possible for a team to add a yaml file for
terraform and a bazel target for k8s resources. However getting these values
correct is the responsibility of the team raising the PR and this tooling
exists to assist in doing so.

For more information about this solution please refer to the
[Developer Portal](https://developer.woven-city.toyota/docs/default/Component/object-storage-service)

## Available subcommands:
```
    "create"
    "info"
    "service-account"
```

## Available arguments:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
||--environment|str|True||Environment for which to prepare the RDS instance|
||--namespace|str|True||Namespace for which to prepare the RDS instance|

## Available environments:
- lab2
- dev2
- dev3
- prod

## Creating an S3 bucket
```
bazel run //ns/agoractl -- bucket --environment <environment> --namespace <namespace> create --reset-prefixes <bool> --postgresql <bool> --prefixes <prefixes>
```

### Available arguments:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
||--reset-prefixes|bool|False|False|If specified, the existing prefix configurations will be replaced with the passed ones. Otherwise, the passed prefixes will be appended to the existing ones.|
||--postgresql'|bool|False|False|If specified, the object key prefix configuration for PgAgora (Zalando) is automatically included.|
||--prefixes|Array|True|[]|The top level object key prefixes. For each prefix, an IAM role will be created. Optionally, you can specify the ServiceAccount name by separating with a comma.|

## Describing an S3 bucket
```
bazel run //ns/agoractl -- bucket --environment <environment> --namespace <namespace> info
```

## Creating Service Accounts and Service Entries for S3
```
bazel run //ns/agoractl -- bucket --environment <environment> --namespace <namespace> service-account
```

### Available arguments:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-n|--bazel-target-name|str|False|agora_rds|The name of the generated bazel target|

# Agoractl RDS

Plugin to generate AWS RDS instances and resources to support a connection to
the instances.

## Introduction

In order to use external AWS services with workloads running on Agora clusters
the Storage team has created a set of IAM Roles for Service Accounts (IRSA)
enabled services for the quick and easy provisioning of these AWS resources.
To use RDS resources there are 3 steps, creating the terraform resources with a
yaml file, creating the service account and service entries with a bazel target
(that executes a helm chart), and creating a privatelink in an external
repository. These steps, while not tremendously difficult, do require that
certain specific pieces of information matches across seperate files and by
implementing this plug can more reliably be assured to be correct.

Note: While Storage reccommends using agoractl to provision RDS instances, it
does not mandate it. It is entirely possible for a team to add a yaml file for
terraform, a bazel target for k8s resources and to raise a PR themselves for the
privatelink. However getting these values correct is the responsibility of the
team raising the PR and this tooling exists to assist in doing so.

For more information about this solution please refer to the
[Developer Portal](https://developer.woven-city.toyota/docs/default/Component/storage-valet-rds)

## Available subcommands:
```
    "create"
    "info"
    "k8s"
    "privatelink"
```

## Available arguments:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
||--environment|str|True||Environment for which to prepare the RDS instance|
||--namespace|str|True||Namespace for which to prepare the RDS instance|

## Available environments:
- dev3

Prod will be implemented shortly

## Creating an RDS instance
```
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> create -u <username> -i <instance class> -s <allocated storage> -sn <shortname>
```

### Available arguments:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-i|--instance-class|str|False|db.t3.large|Instance Class to provision in AWS for RDS|
|-s|--allocated-storage|str|False|50|Total storage in GB to provision for RDS|
|-u|--username|str|False|user|Username used to connect to the database|
|-sn|--shortname|str|True||Shortname prepended to aws resources for naming recognition. Only change this value if it is absolutely mandatory, changing it will result in the resources being reconstructed and that will break the privatelink|

## Describing an RDS instance
```
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> info
```

## Creating Service Accounts and Service Entries for RDS
```
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> k8s -p <proxy endpoint>
```

### Available arguments:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-p|--proxy-endpoint|str|True||Endpoint of RDS Proxy provisioned in the "create" step|
|-n|--bazel-target-name|str|False|agora_rds|The name of the generated bazel target|

## Creating a privatelink to the SMC cluster for the RDS instance
This is a convenience command that requires the user of this command to have
the
[Privatelinks Repository](https://github.com/sg-innersource/mtfuji-private-link-configs)
already cloned onto their system and the path supplied to the command. This
will gracefully add the privatelink to the cluster that is specified. After it
is added the user must commit and raise the PR themselves.

```
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> privatelink -pr <path to privatelink repository> -p <proxy endpoint> -e <endpoint service> -c <cluster to which to add the privatelink>
```

### Available arguments:

|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-pr|--privatelink-repository|str|True|Location of the privatelink repository|
|-p|--proxy-endpoint|str|True||Endpoint of the RDS proxy to which we will create a privatelink|
|-e|--endpoint-service-name|str|True||Endpoint Service name to connect to for the privatelink|
|-c|--cluster|str|True||Cluster to which to add the privatelink|

Valid options for the cluster come from the following list:
- gc-0-apps-ap-northeast-1
- gc-0-apps-prod-ap-northeast-1
- ml-0-apps-ap-northeast-1
- ml-0-apps-prod-ap-northeast-1

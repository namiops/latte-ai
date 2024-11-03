# Storage Valet RDS - Manage your instance with Agoractl

The `rds` subcommand for Agoractl offers helper command to onboard and manage
an RDS instance within Storage Valet RDS.

We recommend using Agoractl through Bazel from the Woven City project monorepo
because Bazel will transparently prepare the internal tooling like the Python
runtime rather than requiring the user to install various pip modules. The
sample commands in the document assume the usage of Agoractl through bazel.

If you haven’t installed Agoractl yet, please do it first, referring to
[the Agoractl documents](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial).

## `agoractl rds`

`agoractl rds` takes two required parameters.

The first one is `--namespace`.
Storage Valet RDS offers up to one rds instance per Kubernetes namespace, and
you need to specify the target namespace with this parameter.

The second one is `--environment`.
Here are the current supported environments.

Available environments

* dev3
* prod

### RDS Command
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
||--environment|str|True||Environment for which to prepare the RDS instance|
||--namespace|str|True||Namespace for which to prepare the RDS instance|

Command help reference:

```bash
bazel run //ns/agoractl -- rds --help
usage: agoractl rds [-h] --environment {dev3,prod} --namespace NAMESPACE {info,create,k8s,privatelink} ...

This command offers the subcommands for using Storage Valet RDS, which is a solution to easily get an RDS instance with default configuration for the workload running inside Agora.

positional arguments:
  {info,create,k8s,privatelink}

options:
  -h, --help            show this help message and exit
  --environment {dev3,prod}
                        The target Agora environment. It should be one of dict_keys(['dev3', 'prod'])
  --namespace NAMESPACE
                        The namespace that owns the RDS instance.
```

## Onboarding / Update RDS configuration (`agoractl rds create`)

You can create or update your instance with `agoractl rds create`.

Command execution reference:

```bash
bazel run //ns/agoractl -- rds --environment dev3 --namespace test-dev create -i db.t3.large -s 50 -u postgres -sn testdev

allocatedStorage: "50"
backupRetentionPeriod: 1
backupWindow: 16:00-17:00
encrypted: true
iamRoleName: test-dev-dev3-proxy-access
instanceClass: db.t3.large
maintenanceWindow: Sun:18:00-Sun:19:00
namespace: test-dev
serviceAccountName: test-dev-sa
shortname: testdev
username: postgres
```

This command generates or updates a YAML configuration file for your RDS
instance at the appropriate location for the specified environment. Storage
Valet RDS uses Terraform to provision an RDS instance. When the generated
configuration file is merged to the `main` branch of the Woven City
mono-repository, your bucket will be created or updated, referring to the
configuration file in the GitHub workflow for the Storage Valet RDS Terraform
deployment.

The merge of the Storage Valet RDS configuration file requires a review from the
Agora Data team
([@wp-wcm/agora-data](https://github.com/orgs/wp-wcm/teams/agora-data)).
Please feel free to ask for the review for your PR with the mention
`@agora-storage` on the `wcm-org-agora-ama` Slack channel.

### Create Subcommand
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-i|--instance-class|str|False|db.t3.large|Instance Class to provision in AWS for RDS|
|-s|--allocated-storage|str|False|50|Total storage in GB to provision for RDS|
|-u|--username|str|False|user|Username used to connect to the database (should not contain hyphens)|
|-sn|--shortname|str|True||Shortname prepended to AWS resources for naming recognition. The shortname string must comply with AWS resource naming conventions (such that only lowercase alphanumeric characters, periods, and hyphens are allowed). Only change this value if it is absolutely mandatory, changing it will result in the resources being reconstructed and that will break the privatelink|
|-en|--encrypted|str|False|true|Should the RDS database storage be encrypted or not? (accepts 'true' or 'false', default: 'true')|
|-bw|--backup-window|str|False|16:00-17:00|The daily time range in UTC during which automated backups are created, format: 'hh24:mm-hh24:mm' (e.g. 09:46-10:16). Must not overlap with maintenance_window|
|-br|--backup-retention-period|int|False|1|Number of days to retain backups for (value between 0 and 35)|
|-mw|--maintenance-window|str|False|Sun:18:00-Sun:19:00|The window in UTC to perform maintenance in, format: 'ddd:hh24:mi-ddd:hh24:mi' (e.g. Mon:00:00-Mon:03:00).|

## Generating ServiceAccount and ServiceEntry manifests (`agoractl rds k8s`)

Currently, generating ServiceAccount manifests through Zebra is supported by
Agoractl. `agoractl rds k8s` outputs the Bazel target definition to generate
the manifests. You can copy and paste it to `BUILD` file for your namespace.

Command execution reference:

```bash
bazel run //ns/agoractl -- rds --environment dev3 --namespace test-dev k8s -p agora-my-rds-proxy.proxy-abc.ap-northeast-1.rds.amazonaws.com -s "rds\!db-abcde-fg-123-8456-00000000" -ri "2h"

load("//ns/storage-valet/rds/bazel:storage_valet_rds.bzl", "storage_valet_rds_k8s_resources")

storage_valet_rds_k8s_resources(
    name = "agora_rds",
    environment = "dev3",
    host = "agora-my-rds-proxy.proxy-abc.ap-northeast-1.rds.amazonaws.com",
    namespace = "test-dev",
    refresh_interval = "2h",
    secret = "rds!db-abcde-fg-123-8456-00000000",
    terraform_target = "//infrastructure/terraform/environments/dev2/base/storage-valet-rds-dev3/configs:test-dev.yaml",
)
```

### K8s subcommand:
|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-p|--proxy-endpoint|str|True||Endpoint of RDS Proxy provisioned in the "create" step|
|-s|--secret-arn|str|True||Name or ARN of the RDS user secret provisioned in the "create" step|
|-n|--bazel-target-name|str|False|agora_rds|The name of the generated bazel target|
|-ri|--external-secret-refresh-interval|str|False|"1h"|Kubernetes ExternalSecret polling refresh interval. The refresh interval is the amount of time before the secret value is read again from the SecretStore provider. Valid time units are "ns", "us" (or "µs"), "ms", "s", "m", "h". May be set to zero to fetch and create secret once. Please see: https://external-secrets.io/latest/api/externalsecret/|

## Generating privatelinks (`agoractl rds privatelink`)

To connect the RDS instances to the workload cluster we must create
privatelinks to make sure they are IP routable. `agoractl rds privatelink` can
generate the correct configuration for you provided you supply the path to a
cloned git repository of `mtfuji-private-link-configs`.

Command execution reference:

```bash
bazel run //ns/agoractl -- rds --environment dev3 --namespace agora-pgagora-dev privatelink -pr /Users/eric.waddell/Workspace/mtfuji-private-link-configs -p agora-pgagora-dev-dev3-rds-proxy.proxy-ctc6wwki862g.ap-northeast-1.rds.amazonaws.com -e com.amazonaws.vpce.ap-northeast-1.vpce-svc-05c8cfc6e8489e7e3 -c gc-0-apps-ap-northeast-1
agora-pgagora:
  apps:
    gc-0-apps-ap-northeast-1:
      agora-pgagora-dev-dev3-rds-proxy.proxy-ctc6wwki862g.ap-northeast-1.rds.amazonaws.com:
        service_name: com.amazonaws.vpce.ap-northeast-1.vpce-svc-05c8cfc6e8489e7e3
```

### Privatelink subcommand:

|Short Flag|Long Flag|Type|Required|Default|Description|
|--|--|--|--|--|--|
|-pr|--privatelink-repository|str|True||Location of the privatelink repository|
|-p|--proxy-endpoint|str|True||Endpoint of the RDS proxy to which we will create a privatelink|
|-e|--endpoint-service-name|str|True||Endpoint Service name to connect to for the privatelink|
|-c|--cluster|str|True||Cluster to which to add the privatelink|

Valid options for the cluster come from the following list:

* gc-0-apps-ap-northeast-1
* gc-0-apps-prod-ap-northeast-1
* ml-0-apps-ap-northeast-1
* ml-0-apps-prod-ap-northeast-1

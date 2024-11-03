# Storage Valet S3 - Manage your bucket with Agoractl

The `bucket` subcommand for Agoractl offers helper commands to onboard and
manage a bucket of Storage Valet S3.

We recommend using Agoractl through Bazel from the Woven City project
mono-repository because Bazel will transparently prepare for every internal
tool, such as the Python runtime. The sample commands in the document take the
Agoractl use with Bazel.

If you haven’t installed Agoractl yet, please do it first, referring to
[the Agoractl documents](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial).

## `agoractl bucket`

`agoractl bucket` takes two required parameters.

The first one is `--namespace`.
Storage Valet S3 offers up to one bucket per Kubernetes namespace, and you need
to specify the target namespace with this parameter.

The second one is `--environment`.
Here are the current supported environments.

| Environment | Description |
| --- | --- |
| `lab2` | The Agora internal environment for the development targets the `pre-prd` environment. |
| `lab2_sandbox_test` | The environment keyword for the Storage Valet S3 development itself. Please do not use this unless you're the developer of Storage Valet S3. | 
| `dev2` | The Agora development environment on the previous generation kubernetes cluster. |
| `dev3` | The Agora development environment on the current generation kubernetes cluster: Speedway. |
| `prod` | The Agora production environment on the current generation kubernetes cluster: Speedway. |

Those parameters should be specified before the Storage Valet S3 subcommands
(`info`, `create`, and `service-account`).

Here is the command's reference.

```bash
$ bazel run //ns/agoractl -- bucket --help 2>&/dev/null
usage: agoractl bucket [-h] --environment {lab2_sandbox_test,lab2} --namespace NAMESPACE {info,create,service-account} ...

This command offers the subcommands for using Storage Valet S3, which is a solution to easily get an S3 bucket with default bucket configuration for the workload running inside Agora.

positional arguments:
  {info,create,service-account}

options:
  -h, --help            show this help message and exit
  --environment {lab2_sandbox_test,lab2}
                        The target Agora environment. It should be one of dict_keys(['lab2_sandbox_test', 'lab2'])
  --namespace NAMESPACE
                        The namespace that owns the bucket.
```

## Onboarding / Update bucket configuration (`agoractl bucket create`)

You can create or update your bucket with `agoractl bucket create`.

Here is a sample command execution.
```bash
$ bazel run //ns/agoractl -- bucket --namespace postgresql-sample --environment lab2 create --prefixes test zalando-sample-wal 2>&/dev/null 
bucketName: lab2-postgresql-sample-4bf7ca
environment: lab2
namespace: postgresql-sample
spaces:
  test: lab2-postgresql-sample-4bf7ca-test
  zalando-sample-wal: lab2-postgresql-sample-4bf7ca-zalando-sample-wal
```

This command generates or updates a YAML configuration file for your bucket at
the appropriate location for the specified environment. Storage Valet S3 uses
Terraform to provision a S3 bucket. When the generated configuration file is
merged to the `main` branch of the Woven City mono-repository, your bucket will
be created or updated, referring to the configuration file in the GitHub
workflow for the Storage Valet S3 Terraform deployment.

The merge of the Storage Valet S3 configuration file requires a review from the
Agora Data team
([@wp-wcm/agora-data](https://github.com/orgs/wp-wcm/teams/agora-data)).
Please feel free to ask for the review for your PR with the mention
`@agora-storage` on the `wcm-org-agora-ama` Slack channel.

Here is the reference.
```bash
$ bazel run //ns/agoractl -- bucket --namespace postgresql-sample --environment lab2 create --help 2>&/dev/null
usage: agoractl bucket create [-h] [--prefixes [PREFIXES ...]]

Create or update the Storage Valet S3 configuration.

options:
  -h, --help            show this help message and exit
  --prefixes [PREFIXES ...]
                        The top level object key prefixes. For each prefix, an IAM role will be created.
```

## Generate ServiceAccount manifests (`agoractl bucket service-account`)

Currently, generating ServiceAccount manifests through Zebra is supported by
Agoractl. `agoractl bucket service-account` outputs the Bazel target definition
to generate the manifests. You can copy and paste it to `BUILD` file for your
namespace.

Here is an example of the command execution.
```py
$ bazel run //ns/agoractl -- bucket --namespace postgresql-sample --environment lab2 service-account 2>&/dev/null 
load("//ns/object-storage/bazel:agora_s3_bucket.bzl", "agora_s3_bucket_k8s_resources")

agora_s3_bucket_k8s_resources(
    name = "agora_bucket",
    environment = "lab2",
    namespace = "postgresql-sample",
    terraform_target = "//infrastructure/terraform/environments/lab2/base/bucket1_east-agora_bucket:postgresql-sample.yaml",
)
```

Zebra is a Woven City in-house technology that utilizes Bazel to generate code
for the Woven City mono-repository. You can get more information about Zebra in
[the Zebra documents](https://developer.woven-city.toyota/docs/default/Component/zebra-service).

Storage Valet S3 creates IAM roles for your workload, and the generated
ServiceAccount is configured for
[IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html),
which transparently brings the assumed role's credential to your workload by
just attaching the ServiceAccount to your pod.

Here is the reference for `agoractl bucket service-account`.
```bash
$ bazel run //ns/agoractl -- bucket --namespace postgresql-sample --environment lab2 service-account --help 2>&/dev/null
usage: agoractl bucket service-account [-h] [--bazel-target-name BAZEL_TARGET_NAME]

Generate a Bazel target for generating ServiceAccounts that allow your pods to access to the provisiond bucket via IRSA. You can copy the generated manifest to the BUILD file for your namespace and generate the manifests by run the following Bazel target. $ bazel run <your Bazel package label>:<Bazel target name>.k8_resources.copy

options:
  -h, --help            show this help message and exit
  --bazel-target-name BAZEL_TARGET_NAME
                        The name for the generated Bazel target.
```

## View your bucket's configuration (`agoractl bucket info`)

You can see the current configuration for your bucket with `agoractl bucket info`.
Here is a sample command execution.
```bash
$ bazel run //ns/agoractl -- bucket --namespace postgresql-sample --environment lab2 info 2>&/dev/null
bucketName: lab2-postgresql-sample-4bf7ca
environment: lab2
namespace: postgresql-sample
spaces:
  test: lab2-postgresql-sample-4bf7ca-test
  zalando-sample-wal: lab2-postgresql-sample-4bf7ca-zalando-sample-wal
```

Here is the reference for the command.
```bash
$ bazel run //ns/agoractl -- bucket --namespace postgresql-sample --environment lab2 info --help 2>&/dev/null
usage: agoractl bucket info [-h]

Show the bucket information for the specified namespace.

options:
  -h, --help  show this help message and exit
```

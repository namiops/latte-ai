# Query

The Agora Data Platform also allows data from a variety of sources to be queried, either individually or as part of a cross-source data join. This is handled via a query engine called Trino. 

Trino is an important part of the security and access model. Each service team will have their own deployment, which can be set to use a designated service account for accessing external resources.

## Configuration

Note that this takes advantage of in-CI tooling, which is available to users of the monorepo, to create deployment, boilerplate configuration, and other files. If you want to use the deata platform and you are not yet set up to deploy workloads to the Agora cluster, please contact the Agora team.

A complete configuration file looks like this:

- Bazel `BUILD` file

```starlark
load("//ns/agora_trino:agora_trino.bzl", "agora_trino")

agora_trino(
    name = "trino",
    namespace = "<YOUR_NAMESPACE>",
    release_name = "trino", # NOTE: this name is used for the deployment. k8s has a length limitation on names, so `trino` is the recommended name unless otherwise required.
    values_file = "trino-values.yaml",
    permissions_file = "permissions.json", # (Optional, the default is null) A JSON file containing the RBAC rules to apply to Trino. Please see the Authorization section.
)

# This filegroup is required, as Bazel's glob operator cannot cross package boundaries.
# Since this BUILD file is needed to instantiate the target under the /infrastructure hierarchy,
# this subfolder becomes a separate package and all files inside are hidden from
# the top-level file glob, and subsequently all validations will fail.
filegroup(
    name = "files",
    srcs = glob(["*.yaml"]),
    visibility = ["//visibility:public"],
)
```


- `trino-values.yaml`

```yaml
#@ load("@ytt:data", "data")
#@data/values
---
trino:
  service_account: "trino-demo-irsa-sa"
  create_service_account: true
  irsa_role_arn: "arn:aws:iam::478791916040:role/byob_account_federated_role"
  allow_superset_access: true
  catalogs:
    hudi:
    - catalog_name: "trino-demo-hudi"
      endpoint: "https://s3.ap-northeast-1.amazonaws.com"
    postgres: 
    - catalog_name: "trino-demo-postgres"
      url: "jdbc:postgresql://hudi-ingestion-db-primary.data-platform-demo.svc:5432/hudi-ingestion-db"
      secret_name: "hudi-ingestion-db-pguser-hudi-ingestion"
      secret_username_key: "user"
      secret_password_key: "password"
      array_mapping: "DISABLED"
  coordinator:
    resources:
      limits:
        cpu: 300m
        memory: 8192Mi
      requests:
        cpu: 150m
        memory: 4096Mi
  worker:
    resources:
      limits:
        cpu: 200m
        memory: 4096Mi
      requests:
        cpu: 100m
        memory: 2048Mi
  server:
    workers: 2
    autoscaling:
      enabled: true
      maxReplicas: 10
      targetCPUUtilizationPercentage: 70
```

The available configuration fields are as follows. All fields are required unless noted otherwise:

* *service_account*: the name of the service account that the Trino deployment will use to access resources.
* *create_service_account*: (Optional) Whether to create the ServiceAccount specified above or not to create. If set to false, the ServiceAccount MUST be created via other means. Defaults to false.
* *irsa_role_arn*: (Required if `create_service_account`=true) The ARN of the AWS IAM Role the ServiceAccount is annotated with.
* *allow_superset_access*: (Optional) Whether to create the AuthorizationPolicy to allow [Agora Superset](/docs/default/Component/data-platform/07_superset) access to your Trino deployment. If set to false, you cannot use Superset to query data through your Trino, or the AuthorizationPolicy MUST be created via other means. Defaults to false.

To query data, one or more _catalogs_ need to be added. These catalogs are actually data sources, not to be confused with the Datahub data catalog that is part of the data platform.

Currently, the platform supports three types of data source: 

* `hudi`: which is any data that has been imported via the data platform
* `iceberg`: another framework used to write columnar data to object storage
* `postgres`: direct queries from Postgres databases, without importing the data

`hudi` and `iceberg` use the same configuration, both fields required:

* *catalog_name*: an arbitrary name for this data source
* *endpoint*: the URL, including protocol, to the S3 bucket, e.g. `https://s3.ap-northeast-1.amazonaws.com`

`postgres` has a different set of requirements:

* *catalog_name*: an arbitrary name for this data source
* *url*: the URL, including `jdbc:`, protocol, port, and DB, e.g. `"jdbc:postgresql://hudi-ingestion-db-primary.data-platform-demo.svc:5432/hudi-ingestion-db"`

Secret management is done via Kubernetes secrets. Do not check secret data into the repository! If you are using the PgAgora Postgres setup, this secret will have been created for you by the Kubernetes operator. If you are managing your secrets manually, you may create your own secret. (it is recommended to use Vault and ExternalSecrets to manage your secret.)

* secret_name: "hudi-ingestion-db-pguser-hudi-ingestion"
* secret_username_key: "user"
* secret_password_key: "password"

Also, additional configurations are supported for postgres:

* array_mapping (Optional, default is `DISABLED` ): how to handle postgres array type. choose from [`DISABLED`, `AS_ARRAY`, `AS_JSON`]. For more details, see https://trino.io/docs/current/connector/postgresql.html#array-type-handling  

The `coordinator/worker/server` options can also be configured in the following fields. The default values can be observed in the example above:

* *coordinator*  (Optional): Specifies [the coordinator configuration](https://github.com/trinodb/charts/blob/97f640a4444f5b6e10bb79a54c5dc94eeb07ab2c/charts/trino/README.md?plain=1#L59-L75).
* *worker*  (Optional): Specifies [the worker configuration](https://github.com/trinodb/charts/blob/97f640a4444f5b6e10bb79a54c5dc94eeb07ab2c/charts/trino/README.md?plain=1#L76-L92).
* *server* (Optional): Specifies [the server configuration](https://github.com/trinodb/charts/blob/97f640a4444f5b6e10bb79a54c5dc94eeb07ab2c/charts/trino/README.md?plain=1#L18-L36). The autoscaling is enabled by default.

### Notes

!!! Note "Flux substition required"

    These BUILD file and the trino-values file will result in the full Kubernetes manifests through [Zebra](/docs/default/Component/zebra-service).
    The resulting manifests contain variables to be substituted by Flux. For this reason **it is required to configure the Flux substition** in your Flux Kustomization file, typically under:

    - `infra/k8s/dev/_core/namespaces` in Legacy cluster.
    - `infrastructure/k8s/environments/dev2/clusters/worker*/flux-tenants/_core/kustomizations` for NextGen cluster.

    An example configuration can be found in [example-tenant-dev.yaml](https://github.com/wp-wcm/city/blob/2840599cb3e40a87e154a6d6690440232c1c0823/infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/_core/kustomizations/example-tenant/example-tenant-dev.yaml#L15-L18)

    If you see any error regarding `"${cluster_domain}" invalid` or alike, it is likely this configuration is missing.

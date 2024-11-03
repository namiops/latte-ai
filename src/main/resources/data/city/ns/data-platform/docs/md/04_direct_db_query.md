# Recipe - Dashboard creation with direct data query

This recipe assumes the following:

* A user wants to use Superset to create a dashboard from data that exists in a Postgres database that resides in their AWS account. (Similar methodologies can be used for PgAgora databases, but no cross-account networking or permissions setup is required.)

## Diagram

![Recipe - Direct query and dashboard](./recipe-direct-query.png)

<!-- Source: https://www.figma.com/file/rWDj39dHNYcsLBA7cFLFyI/Recipe1---Dashboard-%2B-direct-query?type=whiteboard&node-id=0-1&t=W5nBEqTdUNuxEiCh-0 -->

## Steps

* Deploy the Trino query engine in their NS
* Request the engine be added to Superset with user permissions
* Login to Superset and create the dashboard

### Deploy the Trino query engine in their NS

```trino-values.yaml
#@ load("@ytt:data", "data")
#@data/values
---
trino:
  service_account: "trino-demo-irsa-sa"
  catalogs:
    postgres: 
    - catalog_name: "trino-demo-postgres"
      url: "jdbc:postgresql://my-db.demo.svc:5432/data-db"
      secret_name: "my-credentials-k8s-secret"
      secret_username_key: "user"
      secret_password_key: "password"
```

This sets up the Trino query engine to directly retrieve data from the Postgres source. 

In order to avoid teams checking in sensitive data directly, this requires the creation of a Kubernetes secret. This may be done for you by the PG Operator, or by hand.

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-credentials-k8s-secret
data:
  user: aHVkaS1pbmdlc3Rpb24=
  password: aHVkaS1pbmdlc3Rpb24=
```

### Other required files

BUILD file - necessary for CI/CD to create the job. 

```BUILD
load("//ns/hoodiestreamer_job:hoodiestreamer_job.bzl", "hoodiestreamer_job")
load("//ns/agora_trino:agora_trino.bzl", "agora_trino")

agora_trino(
    name = "trino",
    namespace = "data-platform-demo",
    release_name = "trino",
    values_file = "trino-values.yaml",
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

!!! Note "Flux substition required"

    In addition to these files, it is required to configure the Flux substition. See [Query#Notes](/docs/default/Component/data-platform/03_query/#notes).

* Request the engine be added to Superset with user permissions

Please contact either Developer Relations or the Orchestration team for assistance.

* Login to Superset and create the dashboard

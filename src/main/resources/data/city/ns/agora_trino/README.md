# Zebra lib for Trino deployments

## What is Trino?

Trino is a query engine that supports various sources, like data written to object storage via the Hudi framework, as well as databases. 

## Why do I need it?

In Agora, we use IAM Roles for Service Accounts (IRSA) to allow workloads to access non-Kubernetes resources, such as S3 buckets. In order to provide secure access to data, each team's namespace will contain its own Trino query engine, provided with a service account and permissions to ensure that only that team can read or write to the data sources.

If you have data written to by a Hoodiestreamer job, this is the easiest way to query that data.

If you want to use Superset, the queries to build the dashboards are routed through Trino.

## What will be deployed?

By default, a coordinator and two worker pods for Trino will be deployed, along with a headless service to allow communication between the coordinator and workers, and a VirtualService to allow access to the deployment via URL.
    
## Fields to configure:

see https://developer.woven-city.toyota/docs/default/Component/data-platform/03_query/#configuration

### BUILD

```text
load("//ns/agora_trino:agora_trino.bzl", "agora_trino")

agora_trino(
    name = "my-ns-trino",
    namespace = "my-ns",
    release_name= "my-ns-trino",
    values_file = "values.yaml",
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

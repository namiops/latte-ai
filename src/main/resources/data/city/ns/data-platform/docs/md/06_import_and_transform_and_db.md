# Recipe - Data import / transform and dashboard creation

This recipe assumes the following:

* A user wants to use Superset to create a dashboard from data that exists in JSON format in an S3 bucket that resides in their AWS account
* They want to import the data to take advantage of the scale, as the dashboard involves large queries
* They want to import the data to an S3 bucket in their AWS account and maintain the policies
* They want to apply a transformation to the imported data and put the processed data in another bucket
* The business processes are read-heavy, with significantly more data being read in a given time period than is written

## Diagram

![Recipe - S3 import, transform and dashboard](./recipe-s3-import-transform.png)

<!-- Source: https://www.figma.com/file/8qvAFnxqu0DqQeJJgttIAt/Recipe3---Dashboard-%2B-S3-import-%2B-Transform?type=whiteboard&t=W5nBEqTdUNuxEiCh-0 -->

## Steps

* Provision a streamer job to import the data
* Provision a second streamer job to read and transform the data
* Deploy the Trino query engine in their NS
* Request the engine be added to Superset with user permissions
* Login to Superset and create the dashboard

### Provision a streamer job to import the data

```hoodiestreamer-values-1.yaml
#@ load("@ytt:data", "data")
#@data/values
---
hoodiestreamer_job:
  namespace: "data-platform-demo"
  name: "hudi-s3-import"
  schema_file: "schema.avsc"
  table_type: "COPY_ON_WRITE"
  source_ordering_field: "ts"
  target_base_path: "s3a://my-s3-bucket/hudi/example"
  service_account:
    name: "hudi-irsa-sa"
    auto_create: true
    irsa_role_arn: arn:aws:iam::478791916040:role/byob_account_federated_role
  record_key: row_id
  partition_key: user_id
  dfs: 
    root: s3a://my-source-s3-bucket/data
```

```hoodiestreamer-values-2.yaml
#@ load("@ytt:data", "data")
#@data/values
---
hoodiestreamer_job:
  namespace: "data-platform-demo"
  name: "hudi-s3-transform"
  schema_file: "schema-transformed.avsc"
  source_class: "org.apache.hudi.utilities.sources.ParquetDFSSource"
  table_type: "COPY_ON_WRITE"
  source_ordering_field: "ts"
  target_base_path: "s3a://my-s3-bucket/hudi/example-transformed"
  service_account:
    name: "hudi-irsa-sa"
    auto_create: true
    irsa_role_arn: arn:aws:iam::478791916040:role/byob_account_federated_role
  record_key: row_id
  partition_key: user_id
  continuous: true
  dfs: 
    root: s3a://my-s3-bucket/hudi/example
  sql_transform: SELECT a.row_id, a.user_id, a.ts, a.field_1, a.field_2 FROM <SRC> a
```

Of note here:

* COPY_ON_WRITE is used for read-heavy jobs. This will handle merging of newly written data at write time, increasing the overhead for writes but significantly reducing the time required for data reads
* Data is written using the `ts` field as the sort
* Data is written to the `my-s3-bucket` S3 bucket with the `/hudi/example` path.
* The `hudi-irsa-sa` ServiceAccount is used by the streaming import - this requires cross-account setup to be able to read from the S3 bucket in the external account. Docs will be written soon, please contact Developer Relations in the meantime for assistance.
* The per-row `record_key` is the `row_id` field
* Data is partitioned into separate paths inside the object storage by the `ts` `user_id`.
* Since data is being read from an S3 bucket, the `dfs` specifies this, with the path to the source data folder set as `root:`.
* The second job keeps many of the same settings, but has: 
  - a separate schema with only the fields in the SELECT statement
  - a different destination
  - the source set to the output of the first job
  - a source_class setting that specifies that the data is in Parquet
  - a sql_transform that is applied from the data being imported in the first job. Note that the table in the FROM clause should always be the exact string `<SRC>`, the streamer job will replace that with the data source during transform execution.
  - continuous execution set, to update as the first job executes

### Deploy the Trino query engine in their NS

```trino-values.yaml
#@ load("@ytt:data", "data")
#@data/values
---
trino:
  service_account: "trino-demo-irsa-sa"
  catalogs:
    hudi:
    - catalog_name: "trino-demo-hudi"
      endpoint: "https://s3.ap-northeast-1.amazonaws.com"
```

This sets up the Trino query engine with the S3 data source. This assumes the bucket containing the imported data lives in the `ap-northeast-1` AWS region.

### Other required files

BUILD file - necessary for CI/CD to create the job. 

```BUILD
load("//ns/hoodiestreamer_job:hoodiestreamer_job.bzl", "hoodiestreamer_job")
load("//ns/agora_trino:agora_trino.bzl", "agora_trino")

values_files_1 = [
    "schema.avsc",
    "hoodiestreamer-values-1.yaml",
]

values_files_2 = [
    "schema-transformed.avsc",
    "hoodiestreamer-values-2.yaml",
]

hoodiestreamer_job(
    name = "hs_s3_import",
    copy_to_source = True,
    output = "z-hs-job-1.yaml",
    values_files = values_files_1,
)

hoodiestreamer_job(
    name = "hs_s3_transform",
    copy_to_source = True,
    output = "z-hs-job-2.yaml",
    values_files = values_files_2,
)

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

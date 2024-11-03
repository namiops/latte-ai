# Zebra lib for HoodieStreamer jobs

Hudi's Deltastreamer is a component within the Apache Hudi framework designed for real-time data ingestion. Specifically, it processes change data capture (CDC) streams from various sources, only writing changed data records. Its primary function lies in efficiently ingesting real-time data into Hudi-managed storage systems.

## Reasons you might want to do this:

_Import from S3:_ Compared to standard object storage, Hudi provides ACID transactions, enabling reliable data updates. Additionally, Hudi’s storage format and indexing mechanisms optimize query performance, allowing for faster analytical processing of imported data. 

_Import from Kafka:_ By moving data out of Kafka, it is possible to query data directly with a variety of standard tools. The structured format makes querying simpler, and syncing to the data catalog allows for effective data governance.

_Import from a database:_ Enables low-cost scaling for large datasets, while keeping the queryability and structure from the database format. 
    
## Fields to configure:

see: https://developer.woven-city.toyota/docs/default/Component/data-platform/02_import/

## Example

### values.yaml

```yaml
#@ load("@ytt:data", "data")
#@data/values
---
hoodiestreamer_job:
  namespace: "abc"
  name: "test-job"
  schema_file: "schema.avsc"
  config_file: "dfs-source.conf"
  table_type: "COPY_ON_WRITE"
  source_class: "org.apache.hudi.utilities.sources.JsonDFSSource"
  source_ordering_field: "start_datetime"
  target_base_path: "s3a://data-platform-sandbox-s3/hudi/example"
  target_table: "hudi.stc-table"
  service_account: "spark"
```

### schema.avsc

```text
{
  "namespace": "io.woven.city.avro",
  "type": "record",
  "name": "TestSchema",
  "fields": [
    {
      "name": "trip_id",
      "type": "string"
    },
    {
      "name": "start_datetime",
      "type": [
        "null",
        {
          "type": "long"
        }
      ]
    },
    {
      "name": "end_datetime",
      "type": [
        "null",
        {
          "type": "long"
        }
      ]
    },
    {
      "name": "latitude",
      "type": "double"
    },
    {
      "name": "longitude",
      "type": "double"
    }
  ]
}

```

### dfs-source.conf

```yaml
# Key fields, for kafka example
hoodie.datasource.write.recordkey.field=start_datetime
hoodie.datasource.write.partitionpath.field=start_datetime

hoodie.deltastreamer.schemaprovider.source.schema.file=/opt/spark/schema-cm/schema.avsc
hoodie.deltastreamer.schemaprovider.target.schema.file=/opt/spark/schema-cm/schema.avsc

hoodie.embed.timeline.server=true
hoodie.embed.timeline.server.port=39000

hoodie.meta.sync.datahub.emitter.server=http://datahub-datahub-gms.datahub:8080
hoodie.datasource.hive_sync.database=agora-demo-db
hoodie.datasource.hive_sync.table=dest-table
hoodie.deltastreamer.source.dfs.root=s3a://data-platform-s3-source/data

# If you want to use Trino, the Hive metastore sync is necessary (https://hudi.apache.org/docs/syncing_metastore/)
hoodie.datasource.hive_sync.metastore.uris=thrift://hive-metastore.hive-metastore:9083
hoodie.datasource.hive_sync.mode=hms
hoodie.datasource.hive_sync.partition_fields=start_datetime
```

### BUILD
```text
load("//ns/hoodiestreamer_job:hoodiestreamer_job.bzl", "hoodiestreamer_job")

values_files = [
    "dfs-source.conf",
    "schema.avsc",
    "values.yaml",
]

hoodiestreamer_job(
    name = "hs_s3_ingest",
    copy_to_source = True,
    output = "z-hs-job.yaml",
    values_files = values_files,
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

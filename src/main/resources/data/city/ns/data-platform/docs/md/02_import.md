# Import

The Agora Data Platform allows data from a variety of sources, such as Kafka, S3, or databases, to be imported. Your data will be stored in a columnar format in object storage, either Agora-provided or controlled by your team in another AWS account. Data is automatically secured and catalogued.

Both one-time imports and ongoing synchronization are supported, though note that ongoing sync has some delay depending on your application setup - if sub-second latency is required, please contact the Agora team to discuss your requirements.

## Configuration

Note that this takes advantage of in-CI tooling, which is available to users of the monorepo, to create the Spark job, boilerplate configuration, and other files. If you want to use the deata platform and you are not yet set up to deploy workloads to the Agora cluster, please contact the Agora team.


A complete configuration file looks like this:

```
#@ load("@ytt:data", "data")
#@data/values
---
hoodiestreamer_job:
  namespace: "data-platform-demo"
  name: "hudi-s3-ingest"
  schema_file: "schema.avsc"
  table_type: "COPY_ON_WRITE"
  source_ordering_field: "ts"
  target_base_path: "s3a://data-platform-sandbox-s3/hudi/electric-example"
  service_account:
    name: "hudi-s3-sa"
    auto_create: true
    irsa_role_arn: arn:aws:iam::478791916040:role/byob_account_federated_role
  record_key: meter_id
  partition_key: ts
  timebased_keygen:
    type: "DATE_STRING"
    input_dateformat: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    output_dateformat: "yyyy-MM-dd"
  dfs: 
    root: s3a://data-platform-sandbox-s3-source-power/data
  monitoring: true
  continuous:
    enabled: true
    min_sync_interval_seconds: 3600
  sql_transform: SELECT a.meter_id, a.ts, FROM <SRC> a
```

The available configuration fields are as follows. Unless noted otherwise, all fields are required:

* *namespace*: the namespace to deploy the import job into, normally set to your teams' namespace
* *name*: the name to use for the job, will also be applied to the table in the columnar file format
* *schema_file* (Optional when using JDBC source): the name of the schema file. DFS/Kafka source must have schemas. Currently the Avro format is supported.
* *table_type*: One of `COPY_ON_WRITE` or `MERGE_ON_READ`. Tradeoffs are documented extensively [in the Table Types documentation](https://hudi.apache.org/docs/table_types/). Briefly, for write-intensive jobs that are rarely queried, `MERGE_ON_READ` is suggested, for query-optimized datasets `COPY_ON_WRITE` is recommended.
* *source_ordering_field*: the schema key to order data by when written to disk.
* *record_key*: (Optional) the schema key to use for per-record keys (used for fast updates and deletes).
* *partition_key*: (Optional) the schema key to use for data partitioning, expressed as pathed folders in the object store. See [the Hudi partitioning section](https://developer.woven-city.toyota/docs/default/Component/data-platform/90_hudi_learning/#partitioning) for more details.
* *timebased_keygen*: (Optional) The key generation configuration if the record key or the partition key are time-based. See [the Hudi partitioning section](https://developer.woven-city.toyota/docs/default/Component/data-platform/90_hudi_learning/#how-to-configure-the-time-granularity-for-my-time-based-partition-key) for more details.
* *target_base_path*: the full path, including subfolders to the desired object storage location for the imported data. `S3A://` protocol is supported. 
* *service_account*: the Kubernetes service account to apply to the import job.
    * *name*: Name of the service account
    * *auto_create*: Whether to create the ServiceAccount specified above or not to create (and use an existing one). Defaults to false.
    * *irsa_role_arn*: The IAM Role ARN that you created for IRSA. See IRSA config docs (TODO: https://wovencity.monday.com/boards/3813113014/pulses/5380527289).
* *monitoring* (Optional, default is set to true): If enabled, Prometheus will scrape the metrics, allowing you to monitor details like heap memory on the Grafana dashboard, for instance, at http://go/agora-dev1-spark-applications-dashboard.
* *continuous.enabled* (Optional, default is set to false): Delta Streamer runs in continuous mode running {source-fetch -> Transform -> Hudi Write} in loop. Default value is false, which means, its syncOnce mode. Hudi fetches one batch of data from the source, ingests to hudi, does meta sync etc and exits. If you wish to do streaming ingest continuously, you can enable this config.
* *continuous.min_sync_interval_seconds* (Optional, default is set to 300): If you are running deltastreamer in continuous mode, the min sync interval of each sync is defined by this config. After each batch of {source-fetch -> Transform -> Hudi Write}, hudi will add a delay of {min-sync-interval-seconds} before going for next round.
* *sql_transform*: (Optional) SQL transformation to be applied on the imported data. Note that the `FROM` clause must include the string `<SRC>`, which will be replaced during execution with the location the data is imported from.
* *op*: (Optional) Type of operation. Defaults to UPSERT, but INSERT and BULK_INSERT are available if all data is new. See [the Hudi write operations docs](https://hudi.apache.org/docs/write_operations) for details.

If you want to add extra `spark_conf`, set the following:

```
*extra_spark_conf:*
    spark.memory.fraction: "0.2"
    spark.***: <***> 
```

Based on your data source, one of the following is required:

* For imports from another object storage like S3, convenience specifications are available for CSV and JSON:

```
*csv:*
  *root*: <full path to the folder containing your data>
  *header* (Optional, defaults to false): whether your data contains a header row
```

```
*json:*
  *root*: <full path to the folder containing your data>
```

If you want to import from another file type, the base `dfs` config is available, but a `source_class` is also required. 
```
*dfs:*
  *root*: <full path to the folder containing your data>
*source_class:* "org.apache.hudi.utilities.sources.ParquetDFSSource"
```

* For imports from a Kafka topic:

```
*kafka:*
  *topic*: <the topic containing your data>
```

* For imports from a JDBC source like PostgreSQL:

```
*jdbc:*
  *url*: <url of the JDBC connection>
  *user*: <user to use for authentication of the JDBC connection>
  *password_secret_name*: <k8s secret name that contains the password for the user>
  *password_secret_key*: <password key in the k8s secret>
  *table_name*: <table name you want to import>
  *incr_pull*: <Will the JDBC connection perform an incremental pull?>
  *incr_column_name*: <If run in incremental mode, this field will be used to pull new data incrementally>
  *incr_fallback_to_full_fetch*: <boolean which if set true makes an incremental fetch fallback to a full fetch if there is any error in the incremental read>
```

If you need additional configuration in the source configuration file:

```
*extra_config:*
  - "hoodie.config.field.a: value1"
  - "hoodie.config.field.b: value2"
```

and each field will be added as-is to the configuration, each on its own line.

Similarly, if you need to pass extra arguments to the main HoodieStreamer class:

```
*extra_args:*
  - "--flag"
  - "value"
```

## Full examples of additional files

Several additional files are required:

* A [service entry](https://github.com/wp-wcm/city/blob/main/ns/data-platform/docs/spark-job-sample/service-entry.yaml) for external access to S3
* The [schema](https://github.com/wp-wcm/city/blob/main/ns/data-platform/docs/spark-job-sample/schema.avsc) for your data
* The [BUILD](https://github.com/wp-wcm/city/blob/main/ns/data-platform/docs/spark-job-sample/BUILD)
* A [kustomization](https://github.com/wp-wcm/city/blob/main/ns/data-platform/docs/spark-job-sample/kustomization.yaml) to tie it all together and deploy

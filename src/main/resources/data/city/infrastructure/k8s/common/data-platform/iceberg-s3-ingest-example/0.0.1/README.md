# iceberg-s3-ingest-example

**DANGER**

This is no longer a recommended way for setting up Icerberg ingestion. When setting up Icerberg ingestion, consider using IRSA.

This is temporary but using the credential to access the following buckets in 478791916040 (data-platform-poc-account)

- data-platform-sandbox-s3
- data-platform-sandbox-s3-source-power
- data-platform-sandbox-s3-iceberg-warehouse
- data-platform-sandbox-s3-hms-warehouse
- data-platform-sandbox-s3-...

So it is necessary to set the following beforehand:

```shell
kubectl create -n data-platform-demo secret generic iceberg-warehouse-aws-secret \
--from-literal=AWS_ACCESS_KEY_ID="<ACCESS_KEY>" \
--from-literal=AWS_SECRET_ACCESS_KEY="<SECRET_KEY>"
```

TODO: [Sprint [Orc] - [Lab2] Use IRSA for S3 access](https://wovencity.monday.com/boards/3813113014/views/90945203/pulses/5199248131)

## quick start iceberg-spark application using Hive metastore as catalog

This is based on [Spark and Iceberg Quickstart](https://iceberg.apache.org/spark-quickstart/)

**NOTE** When using HMS, first we need to create namespace.

```shell
create namespace nyc;
```

Create table:

```shell
CREATE TABLE demo.nyc.taxis
(
  vendor_id bigint,
  trip_id bigint,
  trip_distance float,
  fare_amount double,
  store_and_fwd_flag string
)
PARTITIONED BY (vendor_id);
```

Insert data:

```shell
INSERT INTO demo.nyc.taxis
VALUES (1, 1000371, 1.8, 15.32, 'N'), (2, 1000372, 2.5, 22.15, 'N'), (2, 1000373, 0.9, 9.01, 'N'), (1, 1000374, 8.4, 42.13, 'Y');
```

Check the result:

```shell
SELECT * FROM demo.nyc.taxis;
```

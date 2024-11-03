# Presto

## Setup

**DANGER**

This is no longer a recommended way for setting up Presto. When setting up Presto, consider using IRSA.


```shell
kubectl create -n data-platform-demo secret generic warehouse-aws-credential \
--from-literal=AWS_ACCESS_KEY_ID="<ACCESS_KEY>" \
--from-literal=AWS_SECRET_ACCESS_KEY="<SECRET_KEY>" \
```

## CLI

```shell
[root@presto-coordinator-5c6d4b58d4-tbd4c /]# presto-cli --catalog hudi --schema agora-demo-db
presto:agora-demo-db> show tables;
          Table
--------------------------
 demo-kafka-to-s3
 power-consumption-schema
(2 rows)

Query 20231030_022657_00052_2rksy, FINISHED, 1 node
Splits: 19 total, 19 done (100.00%)
[Latency: client-side: 137ms, server-side: 118ms] [2 rows, 86B] [16 rows/s, 728B/s]


presto:agora-demo-db> select * from "power-consumption-schema" limit 10;
 _hoodie_commit_time | _hoodie_commit_seqno  | _hoodie_record_key | _hoodie_partition_path |                            _hoodie_file_name                             | global_intensity | glo>
---------------------+-----------------------+--------------------+------------------------+--------------------------------------------------------------------------+------------------+---->
 20231030012453363   | 20231030012453363_5_0 | 840070560E         | 840070560E             | e78421f6-7ec9-4c3d-8301-9e8c325200db-0_5-25-50_20231030012453363.parquet |              1.8 |    >
 20231030012453363   | 20231030012453363_2_0 | 840076693W         | 840076693W             | 2f827e00-9465-4b27-a587-48db14b9f753-0_2-25-47_20231030012453363.parquet |              1.2 |    >
 20231030012453363   | 20231030012453363_9_0 | 84007B094E         | 84007B094E             | 3cb56d27-0183-4396-81d6-0afbe62839d4-0_9-25-54_20231030012453363.parquet |              5.0 |    >
 20231030012453363   | 20231030012453363_3_0 | 84007B008L         | 84007B008L             | dbba1ac0-f4dc-4f70-aea0-446f3d02a2a0-0_3-25-48_20231030012453363.parquet |             11.2 |    >
 20231030012453363   | 20231030012453363_7_0 | 84007B118F         | 84007B118F             | 5db88382-420f-48e1-986f-d1a0678fd7d4-0_7-25-52_20231030012453363.parquet |              9.6 |    >
 20231030012453363   | 20231030012453363_1_0 | 840073025J         | 840073025J             | b0f29ed5-a4c6-4ec6-a477-078b2c257a3a-0_1-25-46_20231030012453363.parquet |              4.2 |    >
 20231030012453363   | 20231030012453363_0_0 | 840072460V         | 840072460V             | 88998dab-6b6b-4164-b43a-87512fc4f0a0-0_0-25-45_20231030012453363.parquet |              1.4 |    >
 20231030012453363   | 20231030012453363_8_0 | 840071520Y         | 840071520Y             | 3d12b43e-15ab-4ca7-b1c4-c65ec5f9f95f-0_8-25-53_20231030012453363.parquet |             17.0 |    >
 20231030012453363   | 20231030012453363_6_0 | 840074925Z         | 840074925Z             | 6f7ca3d3-34cb-450f-9761-d1fbc5783a81-0_6-25-51_20231030012453363.parquet |              1.6 |    >
 20231030012453363   | 20231030012453363_4_0 | 840075016Y         | 840075016Y             | c7bd7fda-1c15-4fd6-9fad-d64da1e76b39-0_4-25-49_20231030012453363.parquet |             21.6 |    >
(10 rows)
```

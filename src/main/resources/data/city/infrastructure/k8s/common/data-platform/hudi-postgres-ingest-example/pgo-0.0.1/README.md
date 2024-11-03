# hudi-postgres-ingest-example

This is the sample for [Hudi streamer JDBC ingestion](https://hudi.apache.org/docs/hoodie_streaming_ingestion/#jdbc-source) 

You can check the result by running the following query using Trino/Presto:

```sql
SELECT * from "data-platform-demo"."hudi-poetgres-ingest-01"
```
10 rows will be returned. 

## memo

### incremental ingestion test

If you want to check the incremental ingestion, 
you can add more data after deploying the `hudi-postgres-ingest` SparkApplication with `--continous` mode:

(`--continous` mode has issues and this will be investigated in
[Sprint [Orc] - Investigate the failure in Hudi streamer ingestion from Agora Postgres](https://wovencity.monday.com/boards/3813113014/views/90945203/pulses/5488140629)

```shell
psql -d hudi-ingestion-db -U postgres
```

```sql
INSERT INTO sample (ts, name)
VALUES ('2023-11-03 14:30:00', 'Emma'),
       ('2023-11-03 14:45:00', 'Liam'),
       ('2023-11-03 15:00:00', 'Mia'),
       ('2023-11-03 15:15:00', 'Noah'),
       ('2023-11-03 15:30:00', 'Ava'),
       ('2023-11-03 15:45:00', 'Oliver'),
       ('2023-11-03 16:00:00', 'Isabella'),
       ('2023-11-03 16:15:00', 'Sophia'),
       ('2023-11-03 16:30:00', 'William'),
       ('2023-11-03 16:45:00', 'James');
``` 

After a while, you can run the query command `SELECT * from "data-platform-demo"."hudi-poetgres-ingest-01 "` again.
This will return 20 rows.

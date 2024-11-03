# Hive metastore (HMS)

Using Hive metastore (HMS) as a standalone service.

## Debug

### Check Postgres

We can check the table content like the following:

```shell
bash-4.4$ psql -d hms-db -U postgres

hms-db=# select * from "TBLS";
TBL_ID | CREATE_TIME | DB_ID | LAST_ACCESS_TIME | OWNER | OWNER_TYPE | RETENTION  | SD_ID | TBL_NAME |    TBL_TYPE    | VIEW_EXPANDED_TEXT | VIEW_ORIGINAL_TEXT | IS_REWRITE_ENABLED
--------+-------------+-------+------------------+-------+------------+------------+-------+----------+----------------+--------------------+--------------------+--------------------
1 |  1695185984 |     7 |         -1326098 | root  | USER       | 2147483647 |     1 | taxis    | EXTERNAL_TABLE |                    |                    | f
(1 row)
```

### Admin operation

We can use `hive-metastore-admin-cli` for the admin operation.
See [Manual Operations Guide - Hive metastore](https://wovencity.monday.com/docs/5348359962?blockId=5dc69c28-6616-4d70-8384-26399e992af0) for more details

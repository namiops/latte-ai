# Powerporter DB mingration

## Prerequisite

Install [goose](https://github.com/pressly/goose).

    go install github.com/pressly/goose/v3/cmd/goose@latest

Set your working directory to here (`projects/ems-kit/background/db`).

## Local environment

### Create a fresh local environment

```shell
$ docker run -d --rm --name postgres -e POSTGRES_PASSWORD=deadbeef -e POSTGRES_DB=pwp-db -p 5432:5432 postgres:14.7-bullseye

$ export GOOSE_DRIVER="postgres"
$ export GOOSE_DBSTRING="postgres://postgres:deadbeef@localhost:5432/pwp-db"

$ goose status                                                
2024/04/18 16:04:22     Applied At                  Migration
2024/04/18 16:04:22     =======================================
2024/04/18 16:04:22     Pending                  -- 001_create_tables.sql

$ goose up
2024/04/18 16:12:07 OK   001_create_tables.sql (9.77ms)
2024/04/18 16:12:07 goose: successfully migrated database to version: 1

$ export PGPASSWORD=deadbeef 
$ psql -h localhost -U postgres -d pwp-db -c "\dt"
Password for user postgres: 
              List of relations
 Schema |       Name       | Type  |  Owner   
--------+------------------+-------+----------
 public | devices          | table | postgres
 public | goose_db_version | table | postgres
 public | measurements     | table | postgres
(3 rows)
```

### Cleanup local environment

```shell
goose reset
```

## Migrate remote environment

```
# Finding some pod
$ kubectl -n ems-kit get pod
...
pwp-db-instance1-j7kc-0                          6/6     Running     0          7d4h
pwp-db-instance1-msh6-0                          6/6     Running     0          7d4h
...

# Finding the lead pod
kubectl -n ems-kit exec -it pwp-db-instance1-j7kc-0 -- patronictl list
 
+ Cluster: pwp-db-ha (7357907405805469858) ---------------------+---------+---------+----+-----------+
| Member                  | Host                                | Role    | State   | TL | Lag in MB |
+-------------------------+-------------------------------------+---------+---------+----+-----------+
| pwp-db-instance1-j7kc-0 | pwp-db-instance1-j7kc-0.pwp-db-pods | Leader  | running |  1 |           |
| pwp-db-instance1-msh6-0 | pwp-db-instance1-msh6-0.pwp-db-pods | Replica | running |  1 |         0 |
+-------------------------+-------------------------------------+---------+---------+----+-----------+

# Getting password
kubectl -n ems-kit get secret pwp-db-pguser-pwp-db -o jsonpath='{.data.password}' | base64 -d

# Portfowarding
kubectl -n ems-kit port-forward pod/pwp-db-instance1-j7kc-0 15432:5432

# Configuration
$ export GOOSE_DRIVER="postgres"
$ export GOOSE_DBSTRING="host=localhost port=15432 dbname=pwp-db user=pwp-db password=(real password) sslmode=disable"

$ goose status                                                
2024/04/23 12:37:53     Applied At                  Migration
2024/04/23 12:37:53     =======================================
2024/04/23 12:37:53     Pending                  -- 001_create_tables.sql

$ goose up
2024/04/23 12:38:03 OK   001_create_tables.sql (66.56ms)
2024/04/23 12:38:03 goose: successfully migrated database to version: 1
```

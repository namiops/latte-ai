# Powerporter DB mingration

## Prerequisite

Install [goose](https://github.com/pressly/goose).

    go install github.com/pressly/goose/v3/cmd/goose@latest

Set your working directory to here (`projects/ems-kit/background/db`).

## Local environment

### Create a fresh local environment

```shell
$ docker run -d --rm --name postgres -e POSTGRES_USER=v2h-db -e POSTGRES_PASSWORD=password -e POSTGRES_DB=v2h-db -p 5432:5432 postgres:14.7-bullseye

$ export GOOSE_DRIVER="postgres"
$ export GOOSE_DBSTRING="postgres://v2h-db:password@localhost:5432/v2h-db"

$ goose status
2024/07/03 10:03:04     Applied At                  Migration
2024/07/03 10:03:04     =======================================
2024/07/03 10:03:04     Pending                  -- 001_create_tables.sql

$ goose up
2024/07/03 10:03:33 OK   001_create_tables.sql (6.33ms)
2024/07/03 10:03:33 goose: successfully migrated database to version: 1

$ psql -h localhost -U v2h-db -d v2h-db -c "\dt"
Password for user v2h-db: 
             List of relations
 Schema |       Name       | Type  | Owner  
--------+------------------+-------+--------
 public | goose_db_version | table | v2h-db
 public | schedule         | table | v2h-db
(2 rows)
```

### Cleanup local environment

```shell
goose reset
```

## Migrate remote environment

```shell
# Finding some pod
$ kubectl -n ems-kit get pod
~~~
pwp-db-instance1-j7kc-0                 6/6     Running     0             86d
pwp-db-instance1-msh6-0                 6/6     Running     0             86d
~~~

# Finding the lead pod
$ kubectl -n ems-kit exec -it pwp-db-instance1-j7kc-0 -- patronictl list
+ Cluster: pwp-db-ha (7357907405805469858) ---------------------+---------+---------+----+-----------+
| Member                  | Host                                | Role    | State   | TL | Lag in MB |
+-------------------------+-------------------------------------+---------+---------+----+-----------+
| pwp-db-instance1-j7kc-0 | pwp-db-instance1-j7kc-0.pwp-db-pods | Leader  | running |  1 |           |
| pwp-db-instance1-msh6-0 | pwp-db-instance1-msh6-0.pwp-db-pods | Replica | running |  1 |         0 |
+-------------------------+-------------------------------------+---------+---------+----+-----------+

# Portfowarding
$ kubectl -n ems-kit port-forward pod/pwp-db-instance1-j7kc-0 15432:5432

# Configuration
$ export GOOSE_DRIVER="postgres"
$ export GOOSE_DBSTRING='host=localhost port=15432 dbname=v2h-db user=v2h-db password={password} sslmode=disable'

```
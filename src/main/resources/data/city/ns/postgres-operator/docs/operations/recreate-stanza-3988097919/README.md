# Stanza recreation

This operation aims to re-create Stanza configuration, 
which is the configuration for communication between PostgreSQL databases and repo host pods.

## Background

We found there are databases that hadn't correctly configured the pgBackrest Stanza for the repository, 
which is a necessary configuration for the communication between the repo host pod and database instance pods.

The lack of the configuration prevents clusterized feature, including PosgreSQL snapshot process managed by pgBackrest, which cleans up WAL logs.

## Operation targets

We can check the target database cluster by executing `pgbackrest info` from the inside of a database instance pod.
If the command shows `No stanzas exist in the repository.`, that means there is no Stanza configuration.

The operation manual includes a Python script to list the target databases.
```bash
$ python ns/postgres-operator/docs/operations/recreate-stanza-3988097919/list_target_databases.py --help
usage: Check PostgresCluster Stanza creation status [-h] [--debug]

This small script check every PostgresCluster resource whether its Stanza configuration was correctly configured or not. This script depends on "kubectl" and "jq" commands locally installed. Before running it, make sure "kubectl" uses the configuration of the target
K8S cluster.

optional arguments:
  -h, --help  show this help message and exit
    --debug
```

### Targets for the lab cluster (28th Feb 2023)

```bash
$ python ns/postgres-operator/docs/operations/recreate-stanza-3988097919/list_target_databases.py       
INFO:root:[brr-b] postgresql (postgresql-instance1-2n9b-0): Okay
INFO:root:[brr] postgresql (postgresql-instance1-w6dq-0): Okay
INFO:root:[brr] storage-poc-db (storage-poc-db-instance1-r4k5-0): Okay
INFO:root:[consent] spicedb-psql (spicedb-psql-instance-hln5-0): Timeout occurred
INFO:root:[data-privacy] postgresql (postgresql-instance-f97w-0): Okay
INFO:root:[id] postgresql (postgresql-instance1-vd6r-0): Okay
INFO:root:[postgresql-sample] database (database-instance1-qr2k-0): Okay
```

Only `spicedb-psql` in the `consent` namespace is the target.
The database seems not to have correct ServiceEntry for the pod-to-pod communication.
It will need the operation after correcting the pod-to-pod communication.

### Targets for the dev cluster (28th Feb 2023)

```bash
$ python ns/postgres-operator/docs/operations/recreate-stanza-3988097919/list_target_databases.py
INFO:root:[ac-qr-publishing] ac-qr-db (ac-qr-db-qr-db-1-p8p4-0): Timeout occurred
INFO:root:[ac-user-registration] ac-user-registration-db (ac-user-registration-db-registration-db-1-rs6g-0): Okay
INFO:root:[alt-authn-authz] a3-db-alt (a3-db-alt-a3-db-2-cs26-0): Okay
INFO:root:[alt-authn-authz] a3-policy-db: No database instance pod
INFO:root:[brr-b] postgresql (postgresql-instance1-dkf8-0): Okay
INFO:root:[brr] postgresql (postgresql-instance1-bgkv-0): Okay
INFO:root:[calendar] calendar-db (calendar-db-calendar-db-1-jk8m-0): No stanza configuration
INFO:root:[calendar] scheduler-db (scheduler-db-scheduler-db-1-r722-0): No stanza configuration
INFO:root:[data-privacy] postgresql (postgresql-instance-hvsz-0): Okay
INFO:root:[foodagri] foodagri-pgdb (foodagri-pgdb-foodagri-instance-zg9v-0): Okay
INFO:root:[id] postgresql (postgresql-instance1-grkz-0): Okay
INFO:root:[introspec] experiment-db (experiment-db-experiment-db-1-6s5q-0): Okay
INFO:root:[inventor-portal] acrete-db (acrete-db-acrete-db-1-x25v-0): No stanza configuration
INFO:root:[lifelog-natto] lifelog-natto-db (lifelog-natto-db-lifelog-natto-db-1-k6wh-0): Okay
INFO:root:[postgresql-sample] test-database (test-database-instance1-rs9w-0): Okay
INFO:root:[tsl] tsl-db (tsl-db-tsl-db-1-w7q9-0): Okay
INFO:root:[utility] map-service-db (map-service-db-instance1-5rmd-0): Okay
INFO:root:[utility] utility-db (utility-db-utility-db-1-mngw-0): No stanza configuration
INFO:root:[wip-showcase] showcase-dev-db (showcase-dev-db-showcase-dev-db-1-ctbm-0): No stanza configuration
INFO:root:[woven-chat] woven-chat-db (woven-chat-db-woven-chat-db-1-5s2d-0): Okay
INFO:root:[woven-passport] basket-db (basket-db-basket-db-1-pk85-0): Okay
INFO:root:[woven-passport] cpm-issuer-db (cpm-issuer-db-cpm-issuer-db-1-9cdk-0): Okay
INFO:root:[woven-passport] cpm-receiver-db (cpm-receiver-db-cpm-receiver-db-1-6jxz-0): Okay
INFO:root:[woven-passport] item-db (item-db-item-db-1-kpdb-0): Okay
INFO:root:[woven-passport] mail-link-invoice-db (mail-link-invoice-db-mail-link-invoice-db-1-4zf9-0): Okay
INFO:root:[woven-passport] payment-hub-db (payment-hub-db-payment-hub-db-1-v6bq-0): No stanza configuration
INFO:root:[woven-passport] sales-details-db (sales-details-db-sales-details-db-1-gnhn-0): Okay
INFO:root:[woven-passport] user-registrar-db (user-registrar-db-user-registrar-db-1-sfxk-0): Okay
INFO:root:[woven-passport] woven-passport-db (woven-passport-db-woven-passport-db-1-vlnj-0): Okay
INFO:root:[woven-testing-link] testing-link-dev-db (testing-link-dev-db-testing-link-dev-db-1-8gxx-0): Okay
INFO:root:[x-air-home] x-air-home-db (x-air-home-db-x-air-home-db-1-szmg-0): Okay
```

`ac-qr-db` in the `ac-qr-publishing` namespace hasn't correctly been configured pod-to-pod communication due to the wrong ServiceEntry.
It will need the operation after correcting the pod-to-pod communication.

`a3-policy-db` in the `alt-authn-authz` namespace seems not to have been handled by the PGO operator.
It needs the separated investigation apart from the operation.

## Procedure

Here are the procudure to create the Stanza configuration.

### 1. Get the leader database instance pod

We can get the pod name by executing `patronictl list` on one of the database instance pods.
The pod with the role `Leader` is the one of the leader database instance.

```bash
$ kubectl exec -n <namespace> -it <one of database instance pods> -- patronictl list
```

### 2. Manually take a backup dump file

Just in case, take dump file for every ddatabases in the target database cluster.

```bash
$ kubectl exec -n <namespace> -it <db leader instance pod> -- pg_dumpall --no-role-passwords > ~/<dump_file_name>.sql
```

### 3. Create Stanza configuration

We can create it by executing `pgbackrest stanza-create` on the leader database instance pod.

```bash
$ kubectl exec -n <namespace> <db leader instance pod> -it -- pgbackrest stanza-create --stanza=db --log-level-console=info
```

### 4. Confirm if the Stanza configuration was successfully created

We can check if the Stanza configuration was successfully created by executing `pgbackrest info`.

Here is a sample output of `pgbackrest info`.

```bash
$ kubectl exec -n woven-passport -it woven-passport-db-woven-passport-db-1-tv4h-0 -- pgbackrest info
stanza: db
    status: ok
    cipher: none

    db (current)
        wal archive min/max (14): 000000010000000000000001/0000000100000008000000C4

        full backup: 20230220-044732F
            timestamp start/stop: 2023-02-20 04:47:32 / 2023-02-20 04:50:29
            wal start/stop: 000000010000000000000004 / 000000010000000000000005
            database size: 125.3MB, database backup size: 125.3MB
            repo1: backup set size: 16.0MB, backup size: 16.0MB
```

Note: If the database doesn't have any snapshot backup, the `status` might be reported as `error`. 
But it's not a problem for the operation.

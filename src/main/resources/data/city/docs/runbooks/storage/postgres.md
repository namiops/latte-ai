<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated

- [Postgres Operator](#postgres-operator)
  - [Monitoring](#monitoring)
    - [Dashboards](#dashboards)
  - [Issues](#issues)
    - [Postgres disc reports full](#postgres-disc-reports-full)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Postgres Operator

| Last Update | 2024-04-25              |
|-------------|-------------------------|
| Tags        | Data, Storage, Postgres |

## Monitoring

### Dashboards

**Preprod Environment**

* [PostgreSQL Details Dashboard](https://athena.agora-dev.w3n.io/grafana/d/2cb3f68dc83a917801763f9f6b58d1310442a063/postgresql-details?orgId=1&refresh=5s&var-namespace=&var-cluster=a3-app-db&var-pod=All&var-datname=All)
* [PostgreSQL Service Health Dashboard](https://athena.agora-dev.w3n.io/grafana/d/0d3c182bc72b39ca56353851c1038f2a45efb43a/postgresql-service-health?orgId=1&refresh=5s)

## Issues

### Postgres disc reports full

**Symptoms**

Pod will report an issue something along the lines of unable to connect or
unable to write to table or tables

Check the usage of the disc by running `df` on the database pod and checking
the `Use%` of the `/pgdata` directory:

```shell
$ ksudo -n <namespace> exec <pod_name> -- df
Filesystem     1K-blocks     Used Available Use% Mounted on
overlay        104845292 22426768  82418524  22% /
tmpfs              65536        0     65536   0% /dev
tmpfs           16164592        0  16164592   0% /sys/fs/cgroup
/dev/nvme0n1p1 104845292 22426768  82418524  22% /tmp
/dev/nvme7n1    10153236   196644   9940208   2% /pgwal
/dev/nvme8n1   103019448    35640 102967424   1% /pgdata
tmpfs            5636096       16   5636080   1% /etc/patroni
tmpfs            5636096       24   5636072   1% /pgconf/tls
tmpfs            5636096       28   5636068   1% /dev/shm
tmpfs            5636096       24   5636072   1% /etc/database-containerinfo
tmpfs            5636096       24   5636072   1% /etc/pgbackrest/conf.d
tmpfs            5636096       12   5636084   1% /run/secrets/kubernetes.io/serviceaccount
tmpfs           16164592        0  16164592   0% /proc/acpi
tmpfs           16164592        0  16164592   0% /sys/firmware
```

If the usage is 97% or more, it suggests there's an issue with the database

The two main issues are:

* The Write Access Log is unable to make a connection: this can happen with
  legacy postgres clusters or clusters that are not headless and without a
  service entry
* The disc is too full and the size needs to be updated

**Fixes**

Check if the user is using the Postgres Operator Target, there should be a
target in the BUILD file where the postgres is being deployed

```build
load("//ns/postgres-operator/bazel:postgrescluster_build.bzl", "LAB2_CHART", "postgrescluster_build")

postgrescluster_build(
    name = "postgres",
    chart = LAB2_CHART,
    namespace = "<namespace_name>",
    values_file = "postgres.yaml",
)
```

If there isn't one, apply
the [Postgres Operator Target](../../../ns/postgres-operator/bazel/postgrescluster_build.bzl),
and submit a PR to update the Postgres

This should resolve the WAL issue. If the problem persists, increase the size of the disc

```yaml
spec:
  instances:
    - dataVolumeClaimSpec:
        accessModes:
          - ReadWriteOnce
        resources:
          requests:
            storage: 10Gi <---- INCREASE SIZE HERE
        storageClassName: storage-class
```

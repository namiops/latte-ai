# (Draft) Apply PostgresCluster manifest standard to the existing resources

**NOTES:**\
The runbook is a draft. 
We'll finalize it after testing the operations here with the Zebra Bazel manifest generation.

The runbook shows how we can apply the Agora PostgresCluster manifest standard to existing PostgresCluster resources.

The standard includes the configuration of StorageClass. Changing StorageClass requires the existing PostgresCluster to swap every PersistentVolume used by it.
The steps in this runbook intend to apply the standard by keeping the existing data over the PersistentVolume replacement.

## PostgresCluster manifest standard

The standard is templated in two Helm charts.
[agora-postgres-cluster](/infrastructure/helm/agora-postgres-cluster) is a chart for the standardized PostgresCluster manifest.
And [agora-postgres-storage-class](/infrastructure/helm/agora-postgres-storage-class) is one for the StorageClass manifest for PostgresCluster.

This runbook won't cover how to use them.
Please refer to the detailed instruction in [the Developer Portal document (TODO)](#TODO).

## How to proceed

Here is an example of a PostgresCluster deployment that is usual in the Agora cluster for now. 

![PostgresCluster deployment before the operation](./images/00.png)

A PostgresCluster usually uses PVs for the following, as described in the diagram.
- PostgreSQL instance pods
- Repo host pod for pgBackrest

The following steps handle the operation.
1. Deploy a new replica PostgreSQL cluster and a pgBackrest repo with the storage class.
2. Copy the existing backup snapshot data and switch over to the new PostgreSQL cluster.
3. Delete the original PostgreSQL cluster and the repo.

The operation for the database cluster is basically the same as the ones in the [official PGO documentation](https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/resize-cluster/#resize-pvc).

### 1. Deploy new replica PostgreSQL cluster and pgBackrest repo

Let's deploy the storage class and update the existing PostgresCluster resource 
to have a new replica database cluster and a new pgBackrest repo.

We can generate the manifest for the storage class using the [agora-postgres-storage-class](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/agora-postgres-storage-class).
Here is an example of `values.yaml` for the chart.

```yaml
namespace: id
```

Then, we can generate the manifest for PostgresCluster with [agora-postgres-cluster](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/agora-postgres-cluster).
Here is an example of `values.yaml` for PostgresCluster.
In the example, we deploy a new replica cluster named `instance2` in addition to `instance1`.
And we also deploy a new pgBackrest repo `repo2` in addition to `repo1`.

```yaml
backup:
  retention:
    full:
      length: 5
      type: time
  schedules: {}
instances:
  - name: instance1
    replicas: 1
    storage: 1Gi
    storageClass: standard
  - name: instance2
    replicas: 1
    storage: 1Gi
name: postgresql
postgresVersion: 13
repos:
  - name: repo1
    storage: 1Gi
    storageClass: standard
  - name: repo2
    storage: 1Gi
storageClass: id-postgresql
```

Notes: The repo name should follow the pattern of `repo[1-4]`.

Here is the deployment overview after deploying the above resources.
The added repo creates a new PersistentVolume and attaches it to the repo pod. 

![Deploy a new replica cluster and a pgBackrest repo](./images/01.png)

### 2. Copy existing backup data and do switchover

Next, we need to promote one of the database pods in the new PostgreSQL cluster if the leader is elected from the existing cluster.
We can see how to handle it in [this Developer Portal document (TODO)](#TODO).

Additionally, we must copy the existing backup data in the original repo to the new one.
pgBackrest deployed by PGO keeps the data under the `/pgbackrest/{repo name}`.
We can do the following in the example we’re using.

```bash
$ cp -rfv /pgbackrest/repo1/* /pgbackrest/repo02/
```

Here is the overview after doing the above operation.

![Copy the existing backup data and do switchover](./images/02.png)

### 3. Delete the original PostgreSQL cluster and the repo

Before deleting, let's take backup data manually, just in case.
We can see how to manually take the snapshot in [this Developer Portal document (TODO)](#TODO).

We can delete the original database cluster and the original pgBackrest repo by updating the `values.yaml` and applying the regenerated manifest.

```yaml
backup:
  retention:
    full:
      length: 5
      type: time
  schedules: {}
instances:
  - name: instance2
    replicas: 1
    storage: 1Gi
name: postgresql
postgresVersion: 13
repos:
    storage: 1Gi
storageClass: id-postgresql
```

The deployment after finishing the operation will become like this.

![Delete the original PostgreSQL cluster and the repo](./images/03.png)

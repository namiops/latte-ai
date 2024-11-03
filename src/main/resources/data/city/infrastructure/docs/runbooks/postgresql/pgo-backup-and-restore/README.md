# About PGO's backup/restore

This readme describes how to set up Backup and Restore for PGO.

If deploying to a local cluster (minikube), this application must be applied
manually; only those who want to test Backup and Restore need this, so this
application is not deployed by flux by default

# Common

Three common responses to backup and restore are required.
Detailed information is provided in [TN-0077 Managed RDB
consideration](https://docs.google.com/document/d/16eOE3MYcPH2Q7P1cbxYL51mPq0lEK69xVRnKdlsryWw/edit#).
- Pod-to-pod connectivity issue
- Timing issue around Istio sidecar proxy setup on the backup Job
- The backup Job never ends due to Istio sidecar proxy

## Pod-to-pod connectivity issue

PeerAuthentication mTLS mode "STRICT" service mesh disables requests from
inside a pod to another pod with that IP address.

The PostgreSQL database instance and the pod in the pgBackRest repository
communicate directly using IP addresses, and the IP address of the sidecar
Envoy proxy must be set.

The way to set it up is to define it in ServieEntry.
Even though it is not required by the Kubernetes API, the required port number
must be explicitly specified because it is needed for sidecar configuration.

Here are the port numbers that the pods currently use.
- PostgreSQL port (The default is 5432)
- SSH port used by pgBackRest (2022)
- TLS port on pgBackRest repository (8432)
- Patroni REST API (8008)

```yaml
# postgres-cluster.yaml
---
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  namespace: id
  name: postgresql-clone
spec:
  hosts:
  - postgresql-clone-pods.id.svc.cluster.local
  location: MESH_INTERNAL
  ports:
  - number: 5432
    name: tcp-postgresql
    protocol: TCP
    name: tcp-pgbackrest
    protocol: TCP
  - number: 8432
    name: tcp-pgbackrest-tls
    protocol: TCP
  resolution: NONE
  workloadSelector:
    labels:
      postgres-operator.crunchydata.com/cluster: postgresql-clone
  exportTo:
  - id
```

## Timing issue around Istio sidecar proxy setup on the backup Job

Set `holdApplicationUntilProxyStarts` to true to block all other container starts
until istio-proxy is ready.

```yaml
# postgres-cluster.yaml
---
  backups:
    pgbackrest:
      metadata:
        annotations:
          proxy.istio.io/config: '{ "holdApplicationUntilProxyStarts": true }'
```

## The backup Job never ends due to Istio sidecar proxy

We need to explicitly terminate the istio sidecar container corresponding to
the Job's pod when the Backup/Restore Job process is finished.

We create a custom container image for Job and add a process that terminates
the sidecar after the original process terminates.

We also add a `command` attribute to Job's pod to allow it to specify an entry
point and terminate the sidecar.

```yaml
# postgres-cluster.yaml
---
  backups:
    pgbackrest:
      command:
      - /opt/crunchy/bin/custom_entrypoint.sh
      - /opt/crunchy/bin/pgbackrest
      image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/postgresql/custom-pgbackrest:main-a0a12f21-2224
```

# Backup

## Scheduled Backups

PGO's backup/restore uses the pgBackRest backup/restore utility internally.
Periodic full or differential backups can be obtained. The backup schedule is
specified in cron notation.

The schedule for periodic backups is set in
`spec.backups.pgbackrest.repos.schedules`.
For example, the following configuration will perform a full backup every day
at 1:00 a.m. and a differential backup every 4 hours.

```yaml
# postgres-cluster.yaml
---
spec:
  backups:
    pgbackrest:
      repos:
      - name: repo1
        schedules:
          full: "0 1 * * *"
          incremental: "0 */4 * * *"
```

## One-Off Backups
To be able to trigger one-off backups, you need to add a section with the repo
Name and command line options for the backup script in
`spec.backups.pgbackrest.manual`.

### Example: trigger a full backup manually

```yaml
# postgres-cluster.yaml
---
spec:
  backups:
    pgbackrest:
      manual:
        repoName: repo1
        options:
         - --type=full
```
Then you can trigger a backup by running the following.

```sh
kubectl annotate postgrescluster <CLUSTER NAME> postgres-operator.crunchydata.com/pgbackrest-backup="$(date)"
```

If you need to run multiple one-off backups, use the  `--overwrite` option

```sh
kubectl annotate postgrescluster <CLUSTER NAME> --overwrite postgres-operator.crunchydata.com/pgbackrest-backup="$(date)"
```

# Restore

## Replicating a PostgreSQL Cluster

PGO allows you to [clone an existing PostgreSQL
cluster](https://access.crunchydata.com/documentation/postgres-operator/5.1.1/tutorial/disaster-recovery/#clone-a-postgres-cluster).

To duplicate an existing cluster, specify the name of the cluster from which to
copy and the name of the repository in which to store backups in
`spec.dataSource.postgresCluster`.

```yaml
# pgo-backup-and-restore/postgres-cluster-clone.yaml
---
spec:
  dataSource:
    postgresCluster:
      clusterName: postgresql
      repoName: repo1
```

The default is to restore to the most recent data, but it is possible to
[restore to a specific point in
time](https://access.crunchydata.com/documentation/postgres-operator/5.1.1/tutorial/disaster-recovery/#perform-a-point-in-time-recovery-pitr)
by specifying `spec.dataSource.postgresCluster.options`.

```yaml
# pgo-backup-and-restore/postgres-cluster-pitr.yaml
---
spec:
  dataSource:
    postgresCluster:
      clusterName: postgresql
      repoName: repo1
      options:
      - --type=time
      - --target="2022-06-23 14:50:00+09"
```

## Point-In-Time-Recovery(PITR) of an existing PostgreSQL cluster

[To restore a running PostgreSQL cluster to a previous point in
time](https://access.crunchydata.com/documentation/postgres-operator/5.1.1/tutorial/disaster-recovery/#perform-an-in-place-point-in-time-recovery-pitr),
set `spec.backups.pgbackrest.restore`. The cluster will be stopped and
recreated as the restore is performed in the existing data directory.

For example, to restore to the point in time 2022-06-23 14:50:00, perform the
following steps

First, edit and apply the YAML file that configures the PostgresCluster
resource.

The restore will not be performed at this point.

```yaml
# postgres-cluster.yaml
---
spec:
  backups:
    pgbackrest:
      restore:
        enabled: true
        repoName: repo1
        options:
        - --type=time
        - --target="2022-06-23 14:50:00+09"
```
```sh
kubectl apply -f infrastructure/k8s/local/id/postgres-cluster.yaml
```

Next, set the PostgresCluster annotation.
Once the PostgresCluster annotations are set, the restore will be performed.

```sh
kubectl annotate postgrescluster postgresql  -n id --overwrite \
  postgres-operator.crunchydata.com/pgbackrest-restore=id1
```

After the restore is complete, disable the restore settings.

```yaml
# postgres-cluster.yaml
---
spec:
  backups:
    pgbackrest:
      restore:
        enabled: false
```
```sh
kubectl apply -f infrastructure/k8s/local/id/postgres-cluster.yaml
```

# Shutdown and restart a database cluster

This page explains the way to shutdown and start a database cluster through the "shutdown" attribute of the Crunchy postgres-operator CRD.

## How it works

The Crunchy's postgres-operator create a StatefulSet for each database instance. For example, if we specify the number of database instances as 3 via `spec.instances.replica`, the operator will create three StatefulSets.

The shutdown operation deletes the pods of the database instances that aren't the leader by setting the replica number for the StatefulSets for them at first, then it deletes the pod for the leader instance with the same way ([The code that was checked to confirm the shutdown behavior](https://github.com/CrunchyData/postgres-operator/blob/4080690/internal/controller/postgrescluster/instance.go#L1211-L1232)).

Each database can be gracefully shut down by getting a TERM signal during the default Kubernetes grace period (30 seconds). (TODO: Need to confirm the actual behavior.) \

Related documents: 
- [crunchydata | PostgreSQL 12.7 Documentation | 18.5. Shutting Down the Server](https://access.crunchydata.com/documentation/postgresql12/12.7/server-shutdown.html)
- [Kubernetes | Pod Lifecycle | Termination of Pods](https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#pod-termination)

## Operations

We can shut down or restart a cluster using the `spec.shutdown` attribute of the PostgresCluster CRD as `true`. 
Related document: [PGO, the Postgres Operator from Crunchy Data | Tutorial | Administrative Tasks | Shutdown](https://access.crunchydata.com/documentation/postgres-operator/5.1.0/tutorial/administrative-tasks/#shutdown)

Here is the example to shut down a cluster.

```yaml
apiVersion: postgres-operator.crunchydata.com/v1beta1
kind: PostgresCluster
metadata:
  name: postgresql
  namespace: id
  annotations:
    proxy.istio.io/config: '{ "holdApplicationUntilProxyStarts": true }'
spec:
  shutdown: true
  patroni:
...
``` 

To restart it, we can set false to the `shutdown` attribute.

```yaml
apiVersion: postgres-operator.crunchydata.com/v1beta1
kind: PostgresCluster
metadata:
  name: postgresql
  namespace: id
  annotations:
    proxy.istio.io/config: '{ "holdApplicationUntilProxyStarts": true }'
spec:
  shutdown: false
  patroni:
...
``` 

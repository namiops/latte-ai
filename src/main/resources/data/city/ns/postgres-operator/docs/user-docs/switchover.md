# Switchover

We can promote a replica database instance to the leader one through the PostgresCluster manifest.
Please see [CrunchyData's documentation](https://access.crunchydata.com/documentation/postgres-operator/latest/tutorial/administrative-tasks/#changing-the-primary) describing how to trigger a switchover.

This document will explain how to do it through our PostgresCluster Helm chart [agora-postgres-cluster](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/agora-postgres-cluster).

## Trigger a switchover

We can add or edit the annotations for the generated PostgresCluster manifest via `annotations` in the chart `values.yaml`.

For example, we can trigger a switchover by adding an annotation with the following `values.yaml`.

```yaml
annotations: 
    postgres-operator.crunchydata.com/trigger-switchover: Thu Apr 13 16:47:40 JST 2023
name: test-postgresql
namespace: test
storageClass: test-storage-class
```

The `spec.patroni.switchover.enalbed` attribute described in the CrunchyData document is set as `true` in the generated manifest by the chart.
So, we don't need to explicitly set it. 

## Promote a specific pod to the leader

We can promote a specific pod by specifying the pod name as `switchoverTarget` in the `values.yaml` like this.

```yaml
annotations: 
    postgres-operator.crunchydata.com/trigger-switchover: Thu Apr 13 16:47:40 JST 2023
name: test-postgresql
namespace: test
storageClass: test-storage-class
switchoverTarget: some-pod-name
```

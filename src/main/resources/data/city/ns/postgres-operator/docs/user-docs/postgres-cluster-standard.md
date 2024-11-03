# PostgresCluster Standard

Agora Storage Team offers a standard of the PostgresCluster manifest.

The standard includes the following configurations.

- Clusterization with one leader and one replica
- StorageClass configuration
- Backup configuration
- Prometheus metrics exporter
- Container resource configuration
- Necessary configurations for Istio

Agora Storage Team released Helm charts to generate a PostgresCluster manifest that follows the standard.
We don't need to directly write complex manifests to configure the above using the charts with only necessary configurations specific to you.
See [Deploy PostgresCluster (TODO)](#TODO) to know how to deploy PostgresCluster with the Helm charts.

## Clusterization

The standard recommends deploying PostgresCluster with one replica database instance.

No infrastructure keeps running with no issues, such as a node or network issue.
If we deploy the replica instance, the effects of those unavoidable issues will be mitigated by failover implemented in a PostgresCluster resource.
And the architecture will also be helpful to minimize the downtime for some maintenance, such as upgrading the PostgresCluster version by switchover mechanism.

Here is the overview of the deployed resources through the standard.

![Deployed resources through the standard](images/deployment_overview.png)

## StorageClass configuration

[StorageClass](https://kubernetes.io/docs/concepts/storage/storage-classes/) holds the configurations of how to provision a volume for [PersistentVolume](https://kubernetes.io/docs/concepts/storage/persistent-volumes/).

We strongly recommend creating and using a StorageClass dedicated to your database.
It will allow us to apply the database-specific volume configuration when we need it in the future.

StorageClass is a cluster-wide resource,
so the standard puts a naming convention and several annotations to help us to identify which namespace the StorageClass is for.

Agora Storage Team released a Helm chart to generate a StorageClass manifest.
Here is an example of the generated manifest.

```yaml
---
# values.yaml
namespace: test
---
# Generated StorageClass manifest
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: test-postgresql
  labels:
    agora/namespace: test
allowVolumeExpansion: true
parameters:
  encrypted: "true"
  fsType: ext4
  type: gp3
  tagSpecification_1: "woven:app=cityos"
  tagSpecification_2: "woven:deployment=other"
  tagSpecification_3: "woven:env=dev"
  tagSpecification_4: "woven:org-code=AC810"
  tagSpecification_5: "Name={{ .PVName }}"
  tagSpecification_6: woven:auto-remediation=false
provisioner: ebs.csi.aws.com
reclaimPolicy: Retain
volumeBindingMode: WaitForFirstConsumer
```

## Backup configuration

The standard includes the following backup configuration.

- Daily full backup taken every 2 am that is retained for 3 days
- Hourly incremental backup taken every zero minute

The configuration can be changed through `values.yaml` for the Helm chart.

## Prometheus metrics exporter

The standard configure a Prometheus metrics exporter for the database instance pods.
We can see the collected metrics in [the Grafana dashboard](https://observability.cityos-dev.woven-planet.tech/grafana/dashboards/f/fPEHsCJ4k/postgresql).
And Agora Storage Team is working on configuring Prometheus Alerts based on the metrics to help users quickly recognize some problems.

## Container resource configuration

The standard includes [the resource `limit` and `request` configurations](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) for containers deployed via PostgresCluster.

The resource configurations are important for Kubernetes resource management,
and we strongly recommend appropriately configuring them.

Here are the default configurations offered by the standard.
If your application requires more database performance, you need to adjust them.

| Pod | Container | Request (CPU) | Request (memory) | Limit (CPU) | Limit (memory) |
| --- | --- | --- | --- | --- | --- |
| Database instance | database | 100m | 64Mi | 2 | 2Gi |
| Database instance | pgbackrest | 100m | 64Mi | 2 | 2Gi |
| Backup job | pgbackrest | pgbackrest | 100m | 64Mi | 2 | 2Gi |
| Repo | pgbackrest | 100m | 64Mi | 2 | 2Gi |
| Database instance, Repo | pgbackrest-config | 1m | 8Mi | 200m | 128Mi |
| Database instance | replication-cert-copy | 1m | 8Mi | 200m | 128Mi |
| Database instance | exporter | 1m | 8Mi | 200m | 128Mi |

## Configurations for running with Istio

The standard includes the following additional configurations and deployment to run PostgresCluster inside the Istio service mesh.

- [An Istio annotation](https://istio.io/latest/docs/ops/common-problems/injection/#pod-or-containers-start-with-network-issues-if-istio-proxy-is-not-ready) for the backup job to start its process after launching the Istio sidecar proxy
- [ServiceEntry](https://istio.io/latest/docs/reference/config/networking/service-entry/) to enable accessibility between the pods of the database instance pods and the pgBackrest repo pod

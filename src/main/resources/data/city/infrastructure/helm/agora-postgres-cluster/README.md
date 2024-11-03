# agora-postgres-cluster

![Version: 0.3.4](https://img.shields.io/badge/Version-0.3.4-informational?style=flat-square)

The chart for deploying a PostgresCluster resource, which is the custom resource offered by Crunchy Data's postgres-operator, to the Agora platform. The chart applies several descriptions to the PostgresCluster manifest to enable some common features such as monitoring and make the database cluster correctly work inside the Istio service mesh. The manifests generated by this chart include Istio's custom resource, and we need to deploy Istio before using this chart.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| annotations | object | `nil` | Annotations for the PostgresCluster resource |
| backup | object | `{"retention":{"full":{"length":3,"type":"time"}},"schedules":{"full":"0 2 * * *","incremental":"0 * * * *"}}` | Backup configuration. |
| backup.retention | object | `{"full":{"length":3,"type":"time"}}` | Backup retention configuration The value should be an object including keys of one or more in "full", "differential", or "incremental".  The value is an object including "length", which is a number of days or the count of backups, and "type", which is "time" or "count". The values wiil be converted to ".spec.backup.pgbackrest.global" attributes for PostgresCluster. See https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/backup-management/#managing-backup-retention |
| backup.schedules | object | `{"full":"0 2 * * *","incremental":"0 * * * *"}` | Schedule backup configurations See https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/backup-management/#managing-scheduled-backups |
| backupImage | string | `"crunchydata.artifactory-ha.tri-ad.tech/crunchydata/crunchy-pgbackrest:ubi8-2.41-4"` |  |
| backupImageCommand | array | `nil` | Specify the entrypoint for the pgBackrest image for the backup K8S job. The value is available only when specifying the patched pgBackrest image in ".backupImage", otherwise the deployment will be failed. |
| backupJobResources | object | `{"limits":{"cpu":"2","memory":"2Gi"},"requests":{"cpu":"100m","memory":"64Mi"}}` | Backup job containers' resource configurations See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecbackupspgbackrestjobsresources |
| backupJobResources.limits | object | `{"cpu":"2","memory":"2Gi"}` | Maximum CPU and memory for a pod |
| backupJobResources.requests | object | `{"cpu":"100m","memory":"64Mi"}` | Minimum required CPU and memory for a pod |
| databaseInitSQL | object | `{"key":null,"name":null}` | The ConfigMap containing custom SQL that will be executed when the database is deployed. See https://access.crunchydata.com/documentation/postgres-operator/latest/references/crd/#postgresclusterspecdatabaseinitsql |
| deployReaperServiceAccount | bool | `true` | Whether to deploy service accounts for the pod termination sidecar for K8S jobs |
| instanceImage | string | `"crunchydata.artifactory-ha.tri-ad.tech/crunchydata/crunchy-postgres:ubi8-14.7-0"` | Container image for database instance |
| instanceResources | object | `{"limits":{"cpu":"2","memory":"2Gi"},"requests":{"cpu":"100m","memory":"64Mi"}}` | Database instance containers' resource configurations See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecinstancesindexresources |
| instanceResources.limits | object | `{"cpu":"2","memory":"2Gi"}` | Maximum CPU and memory for a pod |
| instanceResources.requests | object | `{"cpu":"100m","memory":"64Mi"}` | Minimum required CPU and memory for a pod |
| instances | list | `[{"name":"instance1","replicas":2,"skipWALVolume":false,"storage":"10Gi","storageClass":null,"walStorageClass":null}]` | Value corresponding to [PostgresCluster.spec.instances](https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecinstancesindex) |
| instances[0].name | string | `"instance1"` | Name of the instance |
| instances[0].replicas | int | `2` | Number of the database instances |
| instances[0].skipWALVolume | bool | `false` | Whether attaching a PV for WAL logs or not |
| instances[0].storage | string | `"10Gi"` | Storage size for the data volume |
| monitoring | bool | `true` | Whether to enable the Prometheus monitoring integrated with Agora or not |
| name | string | `"postgresql"` | Name of the PostgresCluster resource |
| pgbackrestConfigResources | object | `{"limits":{"cpu":"200m","memory":"128Mi"},"requests":{"cpu":"1m","memory":"8Mi"}}` | Resource configurations for the sidecar container for pgBackrest configuration See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecbackupspgbackrestsidecarspgbackrestconfigresources |
| pgbackrestConfigResources.limits | object | `{"cpu":"200m","memory":"128Mi"}` | Maximum CPU and memory for a pod |
| pgbackrestConfigResources.requests | object | `{"cpu":"1m","memory":"8Mi"}` | Minimum required CPU and memory for a pod |
| pgbackrestResources | object | `{"limits":{"cpu":"2","memory":"2Gi"},"requests":{"cpu":"100m","memory":"64Mi"}}` | pgBackrest sidecar containers' resource configurations See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecbackupspgbackrestsidecarspgbackrestresources |
| pgbackrestResources.limits | object | `{"cpu":"2","memory":"2Gi"}` | Maximum CPU and memory for a pod |
| pgbackrestResources.requests | object | `{"cpu":"100m","memory":"64Mi"}` | Minimum required CPU and memory for a pod |
| pgmonitorResources | object | `{"limits":{"cpu":"200m","memory":"128Mi"},"requests":{"cpu":"1m","memory":"8Mi"}}` | pgMonitor containers' resource configuration See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecmonitoringpgmonitorexporterresources |
| pgmonitorResources.limits | object | `{"cpu":"200m","memory":"128Mi"}` | Maximum CPU and memory for a pod |
| pgmonitorResources.requests | object | `{"cpu":"1m","memory":"8Mi"}` | Minimum required CPU and memory for a pod |
| postgresVersion | int | `14` | Deployed PostgreSQL version. The available versions are listed [here](https://access.crunchydata.com/documentation/postgres-operator/v5/references/components/#components-compatibility). |
| replicaCertResources | object | `{"limits":{"cpu":"200m","memory":"128Mi"},"requests":{"cpu":"1m","memory":"8Mi"}}` | Replica sidecar containers' resource configuration See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecinstancesindexsidecarsreplicacertcopyresources |
| replicaCertResources.limits | object | `{"cpu":"200m","memory":"128Mi"}` | Maximum CPU and memory for a pod |
| replicaCertResources.requests | object | `{"cpu":"1m","memory":"8Mi"}` | Minimum required CPU and memory for a pod |
| repoHostResources | object | `{"limits":{"cpu":"2","memory":"2Gi"},"requests":{"cpu":"100m","memory":"64Mi"}}` | Repo host containers' resource configurations See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecbackupspgbackrestrepohostresources |
| repoHostResources.limits | object | `{"cpu":"2","memory":"2Gi"}` | Maximum CPU and memory for a pod |
| repoHostResources.requests | object | `{"cpu":"100m","memory":"64Mi"}` | Minimum required CPU and memory for a pod |
| repos | list | `[{"name":"repo1","retention":null,"schedules":null,"storage":"20Gi","storageClass":null}]` | Value corresponding to [PostgresCluster.spec.backups.pgbackrest.repos](https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecbackupspgbackrestreposindex) |
| repos[0].name | string | `"repo1"` | Name of the repo The name must be repo[1-4]. See: https://github.com/CrunchyData/postgres-operator/blob/master/pkg/apis/postgres-operator.crunchydata.com/v1beta1/pgbackrest_types.go#L314-L317 |
| repos[0].retention | string | `nil` | Backup retention configuration When it's omitted, ".backupConfs.retention" is used. The value should be an object including keys of one or more in "full", "differential", or "incremental".  The value is an object including "length", which is a number of days or the count of backups, and "type", which is "time" or "count". The values wiil be converted to ".spec.backup.pgbackrest.global" attributes for PostgresCluster. See https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/backup-management/#managing-backup-retention |
| repos[0].schedules | string | `nil` | Schedule backup configurations When it's omitted, ".backupConfs.schedules" is used. See https://access.crunchydata.com/documentation/postgres-operator/v5/tutorial/backup-management/#managing-scheduled-backups |
| repos[0].storage | string | `"20Gi"` | Storage size for the data volume |
| storageClass | string | `nil` | Storage class for persistent volumes. It's used for PostgresCluster's ".spec.instances[].dataVolumeClaimSpec.storageClassName", And it's also used for ".spec.instances[].walVolumeClaimSpec.storageClassName" and ".spec.backups.pgbackrest.repos[].volume.volumeClaimSpec.storageClassName" when "walStorageClass" and "backupStorageClass" aren't specified respectively. |
| storageClassConfiguration | object | `{"ebsVolumeType":"gp3","provisioner":"ebs.csi.aws.com","volumeBindingMode":"WaitForFirstConsumer","wovenLabels":{"app":"cityos","deployment":"other","env":"dev","orgCode":"AC810"}}` | Storage class configuration A storage class is created unless an existing storage class is explictly specified. |
| storageClassConfiguration.wovenLabels | object | `{"app":"cityos","deployment":"other","env":"dev","orgCode":"AC810"}` | Woven tags and labels for resource management. For now, the chart uses labels for the Agora team. See: https://security.woven-planet.tech/information-security-policy/asset-tagging-standard/#default-labelstags |
| storageClassConfiguration.wovenLabels.app | string | `"cityos"` | Application name |
| switchoverTarget | string | `nil` | Switchover target pod name. The value is passed to PostgresCluster.spec.patroni.switchover.targetInstance. See: https://access.crunchydata.com/documentation/postgres-operator/latest/references/crd/#postgresclusterspecpatroniswitchover |
| targetIPv6 | bool | `false` | Whether deploying to an IPv6 environments or not |
| users | list | `nil` | Users configuration See: https://access.crunchydata.com/documentation/postgres-operator/v5/references/crd/#postgresclusterspecusersindex |

## Usage

We can deploy a PostgreSQL cluster with the default values in the above table through [Helm CLI](https://helm.sh/docs/intro/install/) like the following.

```bash
# target namespace
(base) ➜  cityos git:(hoka-pgo-manifest-template-CITYPF-1813) ✗ kubectl get ns test
NAME   STATUS   AGE
test   Active   17h

# deploy a PostgresCluster named "test-postgres-cluster"
(base) ➜  cityos git:(hoka-pgo-manifest-template-CITYPF-1813) ✗ helm install -n test test-postgres-cluster infrastructure/helm/agora-postgres-cluster --set namespace=test --set name=test-postgres-cluster
NAME: test-postgres-cluster
LAST DEPLOYED: Wed Nov 16 11:28:07 2022
NAMESPACE: test
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
The PostgresCluster has been successfully deployed.
You can check the resource by executing the following command.

$ kubectl get -n test postgresclusters.postgres-operator.crunchydata.com test-postgres-cluster

# check the PostgresCluster resource
(base) ➜  cityos git:(hoka-pgo-manifest-template-CITYPF-1813) ✗ kubectl get -n test postgresclusters.postgres-operator.crunchydata.com test-postgres-cluster
NAME                    AGE
test-postgres-cluster   8s

# list the resources deployed by Crunch Data's postgres-operator
(base) ➜  cityos git:(hoka-pgo-manifest-template-CITYPF-1813) ✗ kubectl get po -n test --selector postgres-operator.crunchydata.com/cluster=test-postgres-cluster
NAME                                      READY   STATUS      RESTARTS   AGE
test-postgres-cluster-backup-mp2m-9ht9j   0/2     Completed   0          3m57s
test-postgres-cluster-instance1-6fd7-0    5/5     Running     0          4m18s
test-postgres-cluster-instance1-9wbv-0    5/5     Running     0          4m18s
test-postgres-cluster-instance1-ld99-0    5/5     Running     0          4m18s
test-postgres-cluster-repo-host-0         2/2     Running     0          4m18s
```

## Generate README

The folder uses [helm-docs](https://github.com/norwoodj/helm-docs) to generate README.md.

We can regenerate the document by executing the follwoing command from the root folder of the repository.

```bash
$ helm-docs -c infrastructure/helm/agora-postgres-cluster --template-files=infrastructure/helm/agora-postgres-cluster/README.md.gotmpl
```

Integrating the document generation with Bazel is a future task.

## Generate sample manifests

To support PR reviews, generated manifests with sample `values.yaml` files are stored under the `generated_manifests`.

We can regenerate the manifests by executing `generated_manifests/generate_sample_manifests.sh`. The script uses [kubectl-slice](https://github.com/patrickdappollonio/kubectl-slice).

Integrating it with the CI pipeline is a future task.
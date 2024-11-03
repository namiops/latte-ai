# PostgreSQL sample deployment

This namespace includes a sample deployment of a PostgreSQL cluster using the templates offered by the Agora storage team.

## Templates

Two templates are currently offered as Helm charts. To follow the instructions here, we need to install [Helm](https://helm.sh/docs/intro/install/).

* [agora-postgres-cluster](/infrastructure/helm/agora-postgres-cluster) for generating manifests for a PostgresCluster resource.
* [agora-postgres-storage-class](/infrastructure/helm/agora-postgres-storage-class) for generating a StorageClass manifests.

## How to generate manifests

We strongly recommend deploying a StorageClass dedicated for your PostgreSQL cluster to avoid the risk of reuse of the PersistentVolume used for storing data. We can generate the template using the `agora-postgres-storage-class` Helm chart.

```
$ helm template storage-class https://artifactory-ha.tri-ad.tech:443/artifactory/helm/wcm-cityos/agora-postgres-storage-class/agora-postgres-storage-class-0.1.1%2B58d65b60.tgz --set name={storage class name} --set namespace={target namespace} > {file path for the generated manifest}
```

The actual name of the storage class in the manifest is prefixed with the namespace and becomes `{target namespace}-{storage class name}`.

Next, we can generate the manifests for PostgresCluster using the StorageClass using the `agora-postgres-cluster` Helm chart.

```
$ helm template database https://artifactory-ha.tri-ad.tech:443/artifactory/helm/wcm-cityos/agora-postgres-cluster/agora-postgres-cluster-0.1.3%2B89db4391.tgz -n {target namespace} --set name={name} --set defaultStorageClass={storage class name} > {file path for the generated manifest}
```

The Helm chart generate manifests of PostgresCluster and ServiceEntry, and `{name}` is used for the both resource names.

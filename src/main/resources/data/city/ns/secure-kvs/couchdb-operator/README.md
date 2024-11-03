
# CouchDB Operator
This is a [Kubernetes operator](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/) for [Apache CouchDB](https://docs.couchdb.org/en/stable/intro/index.html). It introduces the CouchDBCluster object, which can spin up a CouchDB cluster along with backup and restore functionalities.

Here's an example CouchDBCluster resource creates a CouchDB cluster with three replicas, restored from the provided volume snapshot, and periodically creates a volume snapshot as the backup functionality.

```
apiVersion: couchdbcluster.woven-city.global/v1alpha1
kind: CouchDBCluster
metadata:
  name: couchdbcluster-sample
  namespace: couchdb
spec:
  restore:
    snapshot:
      source: "couchdbcluster-sample-database-storage-couchdbcluster-sample-backup-0-20230120004800"
  cluster:
    statefulSet:
      replicas: 3
      volumeClaimTemplates:
        - metadata:
            name: database-storage
          spec:
            storageClassName: "csi-hostpath-sc"
  backup:
    retention:
      expires: 1h
    schedule: "*/2 * * * *"
    snapshot:
      volumeSnapshotClassName: "csi-hostpath-snapclass"

```

Apart from restoring from a volume snapshot, it also supports restore from an existing CouchDB cluster, through replication. Refer to [samples](./config/samples) for more examples.

## Technical Notes
- [TN-0223 - CouchDB Operator Design](https://docs.google.com/document/d/1MH49_Aek8tAEPPFmu9VLpOxmU55u71s3w5J9Ub_PKeU/edit)
- [TN-0171 - Secure KVS Backup Restore](https://docs.google.com/document/d/1O0SPLJbgJzQ3mgh_HnFyrAbCt0iiVC4a5iZQ4DCg0Og/edit#heading=h.oxavkyg1lb97)

## Generate code and manifests
```
make generate
make manifests
```

## Setup local development 

### Setup minikube

- Configure minikube cluster and CSI component with csi-hostpath driver.
  
```
minikube start 4 13000
minikube addons enable volumesnapshots
minikube addons enable csi-hostpath-driver
kubectl config use-context minikube
kubectl delete csidriver hostpath.csi.k8s.io
kubectl apply -f ./local/hostpath.yaml
```

### Deploy Cert manager

```
kubectl create ns cert-manager
kubectl apply -k  ../../../infrastructure/k8s/common/cert-manager/cert-manager-v1.12.2-agora1/crd
kubectl apply -k  ../../../infrastructure/k8s/common/cert-manager/cert-manager-v1.12.2-agora1/operator
```

### Deploy CouchDB operator and cluster

* Build CouchDB operator docker image.

```
bazel run //ns/secure-kvs/couchdb-operator:image.tar
```

* Load image into minikube 

```
minikube image load ns/secure-kvs/couchdb-operator:image
```

* Deploy the CouchDB operator and manifests.

```
make deploy IMG=ns/secure-kvs/couchdb-operator:image
```

* Deploy a CouchDB cluster.

```
kubectl apply -k config/samples
```

* Inspect CouchDB cluster resource.

```
kubectl get couchdbcluster -n couchdb couchdbcluster-sample -o json
```

* Inspect operator logs.

```
kubectl logs -f `kubectl get pods -n couchdb-operator-system -o json | jq -r '.items[].metadata.name'` -n couchdb-operator-system
```

* Write some sample documents to `couchdbcluster-sample` cluster in the `couchdb` namespace.

```
./local/write_documents.sh couchdbcluster-sample couchdb
./local/query_documents.sh couchdbcluster-sample couchdb
```

* Run controller tests.

```
bazel run //ns/secure-kvs/couchdb-operator/controllers:controllers_test
```

### Apply local changes to the operator

* If no crd changes 

```
bazel run //ns/secure-kvs/couchdb-operator:image
kubectl rollout restart deployment couchdb-operator-controller-manager -n couchdb-operator-system
```

* If crd changed

```
make generate
make manifests
bazel run //ns/secure-kvs/couchdb-operator:image
kubectl rollout restart deployment couchdb-operator-controller-manager -n couchdb-operator-system
```

### Clean up
```
kubectl delete -k config/samples
make undeploy
minikube delete
```

## Copy manifests to infra/k8s/common/couchdb-operator-system
```
make kustomize-build-and-copy VER=x.y.z
```

## Master/Master replication

This script is used to (test) [master-master replication](https://docs.couchdb.org/en/stable/replication/intro.html#master-master-replication) between CouchDB clusters. This will be moved to the operator.

### Test run

```sh
kubectl config use-context lab2-worker1-west
kubectl port-forward svc/agora-kvs-couchdb -n secure-kvs 5984:5984 &
export COUCHDB_PASSWORD=`kubectl get secret -n secure-kvs agora-kvs-couchdb -o json | jq -r .data.adminPassword | base64 -d`   
export COUCHDB_USER=`kubectl get secret -n secure-kvs agora-kvs-couchdb -o json | jq -r .data.adminUsername | base64 -d`
```

#### West to East
```sh
bash configure.sh -t http://agora-kvs-east.secure-kvs.mesh.internal.com:5984 -s http://agora-kvs-couchdb:5984 -e http://user:password@localhost:5984 -p e-w -m create
```

#### East to West
```sh
bash configure.sh -t http://agora-kvs-couchdb:5984 -s http://agora-kvs-east.secure-kvs.mesh.internal.com:5984 -e http://user:password@localhost:5984 -p e-w -m create
```

### Verify
```
curl -s http://user:password@localhost:5984/_replicator/_all_docs | jq . 

{
  "total_rows": 2,
  "offset": 0,
  "rows": [
    {
      "id": "e-w-secure-kvs-test_testing-bidirectional",
      "key": "e-w-secure-kvs-test_testing-bidirectional",
      "value": {
        "rev": "1-80ccd8cf797487696dfd568f1adbd7e0"
      }
    },
    {
      "id": "w-e-secure-kvs-test_testing-bidirectional",
      "key": "w-e-secure-kvs-test_testing-bidirectional",
      "value": {
        "rev": "1-2f47f98c3fe8a5b39d5b826909df92ca"
      }
    }
  ]
}
```


#### Clean up
```sh
bash configure.sh -e http://user:passord@localhost:5984 -p test -m delete
bash configure.sh -e http://user:passord@localhost:5984 -p test -m delete
unset COUCHDB_PASSWORD
unset COUCHDB_USER
lsof -i :5984 | tail -n1 | awk '{print $2}' | xargs kill -9
```

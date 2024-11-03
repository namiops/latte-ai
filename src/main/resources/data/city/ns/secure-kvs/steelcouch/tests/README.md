# Integration tests for Steelcouch

This folder includes test codes of the integration test for Steelcouch and CouchDB.

The container image of the test is used to define [a chart test](https://helm.sh/docs/topics/chart_tests/) for [the Secure KVS Helm chart](infrastructure/helm/secure-kvs).

## Configuration

The test codes take configurations through the following environment variables.

| Environment variable | Notes |
| -------------------- | ----- |
| COUCHDB\_ENDPOINT | The endpoint of CouchDB cluster |
| STEELCOUCH\_ENDPOINT | The endpoint of Steelcouch |
| DB\_USER | The root user name of CouchDB cluster |
| DB\_PASSWORD | The root password of CouchDB cluster |

## How to locally execute the test codes

```bash
COUCHDB_ENDPOINT=http://<CouchDB endpoint> STEELCOUCH_ENDPOINT=http://<Steelcouch endpoint> DB_USER=$(kubectl get secret -n <Secure KVS namespace> <CouchDB credential secret name> -o json | jq -r '.data.adminUsername' | base64 -d) DB_PASSWORD=$(kubectl get secret -n <Secure KVS namespae> <CouchDB credential secret name> -o json | jq -r '.data.adminPassword' | base64 -d) bazel run //ns/secure-kvs/steelcouch/test
```

The secret structure for CouchDB cluster is described in [the NOTES.txt](/infrastructure/helm/secure-kvs/templates/NOTES.txt) of the Secure KVS Helm chart, and it's shown after finishing `helm install` or `helm upgrade`.

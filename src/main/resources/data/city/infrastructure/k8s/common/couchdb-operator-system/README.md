# couchdb-operator-system for common

The directories here are generated with the following commands:

```shell
cd path/to/ns/secure-kvs/couchdb-operator
make kustomize-build-and-copy VER=<TARGET_VERSION>
```

## Changes

### 0.1.0
- CouchDB operator initial release

### 0.3.0
- Exposed resources fields so that container's (CouchDB/Prom exporter's) resources can be configured using CouchDBCluster object.

### 0.4.1
- Added CouchDBDatabase Kind.

### 0.4.2
- Updated the resource limits for CDO deployment

### 0.5.0
- Added missing functionality and webhooks for CouchDBDatabase resource

### 0.5.1
- Added missing `namespace` field in the CRD

### 0.6.0
- Added support for OCI images
- Added support for cluster resize

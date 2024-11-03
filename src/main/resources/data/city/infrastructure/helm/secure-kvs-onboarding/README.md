# secure-kvs-onboarding

![Version: 0.2.1](https://img.shields.io/badge/Version-0.2.1-informational?style=flat-square) ![AppVersion: 0.1.0](https://img.shields.io/badge/AppVersion-0.1.0-informational?style=flat-square)

The chart for creating a database on Secure KVS (Steelcouch and CouchDB). The chart first creates a database on the target CouchDB cluster, then deploys an AuthorizationPolicy to allow the registered service accounts to access the database. If the database creation fails, including the case it has already existed, the release will fail and be paused. The chart requires the pre-existed Secure KVS deployment and the Istio deployment.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| couchdb.credentials.passwordSecretKey | string | `"adminPassword"` |  |
| couchdb.credentials.secretName | string | `"couchdb-agora-kvs"` |  |
| couchdb.credentials.userSecretKey | string | `"adminUsername"` |  |
| couchdb.labels.app | string | `"couchdb-agora-kvs"` |  |
| couchdb.labels.type | string | `"db"` |  |
| couchdb.port | int | `5984` |  |
| couchdb.serviceName | string | `"couchdb-agora-kvs"` |  |
| couchdb.targetPort | int | `5984` |  |
| database | string | `nil` |  |
| databaseCreationJob.enabled | bool | `true` |  |
| databaseCreationJob.image.pullPolicy | string | `"IfNotPresent"` |  |
| databaseCreationJob.image.repository | string | `"docker.artifactory-ha.tri-ad.tech/wcm-cityos/core/secure-kvs-administration/create_database"` |  |
| databaseCreationJob.image.tag | string | `"main-d442947f-6313"` |  |
| databaseCreationJob.resources.limits.cpu | string | `"500m"` |  |
| databaseCreationJob.resources.limits.memory | string | `"128Mi"` |  |
| databaseCreationJob.resources.requests.cpu | string | `"100m"` |  |
| databaseCreationJob.resources.requests.memory | string | `"64Mi"` |  |
| databaseCreationJob.runInIstioServiceMesh.enabled | bool | `true` |  |
| databaseCreationJob.runInIstioServiceMesh.envoyPort | int | `15020` |  |
| databaseCreationJob.serviceAccount | string | `"agora-kvs"` |  |
| encryption | bool | `true` |  |
| namespace | string | `nil` |  |
| serviceAccounts | list | `[]` |  |
| steelcouch.labels.app | string | `"agora-kvs-couchdb-agora-kvs"` |  |
| steelcouch.labels.release | string | `"couchdb-agora-kvs"` |  |
| steelcouch.labels.type | string | `"steelcouch"` |  |
| steelcouch.port | int | `5984` |  |
| steelcouch.serviceName | string | `"steelcouch-agora-kvs-couchdb-agora-kvs"` |  |
| steelcouch.targetPort | int | `8000` |  |

## Generate README

The folder uses [helm-docs](https://github.com/norwoodj/helm-docs) to generate README.md.

We can regenerate the document by executing the follwoing command from the root folder of the repository.

```bash
$ helm-docs -c infrastructure/helm/secure-kvs-onboarding --template-files=infrastructure/helm/secure-kvs-onboarding/README.md.gotmpl
```

## Generate sample manifests

To support PR reviews, generated manifests with sample `values.yaml` files are stored under the `generated_manifests`.

We can regenerate the manifests by executing `generated_manifests/generate_sample_manifests.sh`.
The script uses [helm](https://helm.sh/docs/intro/install/), [kubectl-slice](https://github.com/patrickdappollonio/kubectl-slice), and [yamlfmt](https://github.com/google/yamlfmt).

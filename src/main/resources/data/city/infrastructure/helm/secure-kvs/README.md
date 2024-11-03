# secure-kvs

![Version: 0.7.3](https://img.shields.io/badge/Version-0.7.3-informational?style=flat-square) ![AppVersion: 3.2.0](https://img.shields.io/badge/AppVersion-3.2.0-informational?style=flat-square)

CouchDB deployment for Agora. This chart is based on the official CouchDB Helm chart and it adds some modification and additional deployments such as Prometheus for monitoring purpose. The original CouchDB Helm chart can be seen here. https://github.com/apache/couchdb-docker

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## What will be deployed

The deployment overview of the current version chart is depicted [here](https://docs.google.com/drawings/d/1HpgL_57jkkDZzWpCGeLSdbSLykwNCg0Bm6U8qo17w9A/edit?usp=sharing).

## How to use

See the [Secure KVS 101](https://developer.woven-city.toyota/docs/default/component/securekvs-tutorial/en/01_getting_started/#2-installing-secure-kvs) document.

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| couchDBCluster.operatorPrincipal | string | `"cluster.local/ns/couchdb-operator-system/sa/couchdb-operator-controller-manager"` | Principal of the service account for CouchDB operator |
| couchDBCluster.spec | object | `{"backup":{"volumeSnapshot":{"retention":{"expires":"90m"},"schedule":"*/30 * * * *","snapshot":{"volumeSnapshotClassName":"csi-hostpath-snapclass"}}},"cluster":{"statefulSet":{"replicas":3,"volumeClaimTemplates":[{"metadata":{"name":"database-storage"},"spec":{"storageClassName":"csi-hostpath-sc"}}]}}}` | CouchDBCluster resource spec See: TODO: put link of documents of CouchDBCluster specs |
| runWithIstio.deployAuthorizationPolicy | bool | `false` | Flag to decide whether deploy Istio AuthorizationPolicy |
| runWithIstio.enabled | bool | `false` | Flag to decide whether enable Istio support |
| runWithIstio.envoyPort | int | `15020` |  |
| steelcouch.cipher | string | `"A256GCM"` | Encryption algorithm. Supported values: A128GCM, A256GCM |
| steelcouch.image.container | string | `"steelcouch"` | Steelcouch's container image |
| steelcouch.image.port | int | `8000` |  |
| steelcouch.image.pullPolicy | string | `"IfNotPresent"` |  |
| steelcouch.image.repository | string | `"docker.artifactory-ha.tri-ad.tech/wcm-cityos/core/steelcouch"` | Respository for Steelcouch's container image |
| steelcouch.image.tag | string | `"main-d22755cd-16303"` |  |
| steelcouch.livenessProbe.enabled | bool | `true` | Whether add the livenes probe setting |
| steelcouch.livenessProbe.failureThreshold | int | `3` |  |
| steelcouch.livenessProbe.initialDelaySeconds | int | `0` |  |
| steelcouch.livenessProbe.periodSeconds | int | `10` |  |
| steelcouch.livenessProbe.successThreshold | int | `1` |  |
| steelcouch.livenessProbe.timeoutSeconds | int | `1` |  |
| steelcouch.localKeySecretName | string | `nil` | A name of a preexisted secret can be specified to use it for the root key. The secret must have the key "testKey" holding the key value. If the attribute is empty, the root key is generated with a random value. |
| steelcouch.originDeterminationType | string | `"xfcc-uri"` | How Steelcouch determines the origin. Supported values: xfcc-uri, {static: somestring} |
| steelcouch.readinessProbe.enabled | bool | `true` | Whether add the readiness probe setting |
| steelcouch.readinessProbe.failureThreshold | int | `10` |  |
| steelcouch.readinessProbe.initialDelaySeconds | int | `0` |  |
| steelcouch.readinessProbe.periodSeconds | int | `10` |  |
| steelcouch.readinessProbe.successThreshold | int | `1` |  |
| steelcouch.readinessProbe.timeoutSeconds | int | `1` |  |
| steelcouch.replicas | int | `1` | Number of replicas for Steelcouch deployment |
| steelcouch.resources.limits.cpu | int | `1` |  |
| steelcouch.resources.limits.memory | string | `"2Gi"` |  |
| steelcouch.resources.requests.cpu | string | `"100m"` |  |
| steelcouch.resources.requests.memory | string | `"128Mi"` |  |
| steelcouch.rootKey.fileName | string | `"root.key"` |  |
| steelcouch.rootKey.source | string | `"LOCAL"` | The source refers to the location from where root key is retrieved. The values can be either LOCAL (default, when it's from k8s cluster) or VAULT |
| steelcouch.rootKey.volumePath | string | `"/key"` | The path in the steelcouch container where the root key secret will be rendered |
| steelcouch.service.annotations | string | `nil` |  |
| steelcouch.service.enabled | bool | `true` | Whether deploy a K8S service for Steelcouch's endpoint |
| steelcouch.service.externalPort | int | `5984` |  |
| steelcouch.service.name | string | `nil` | Name of the service. The same name as the Steelcouch Deployment is used as the default value. |
| steelcouch.service.type | string | `"ClusterIP"` |  |
| steelcouch.serviceAccount.annotations | string | `nil` |  |
| steelcouch.serviceAccount.name | string | `nil` |  |
| steelcouch.serviceAccount.skipCreation | bool | `false` |  |
| steelcouch.vault.authPath | string | `"auth/kubernetes"` | Authentication path for kubernetes auth |
| steelcouch.vault.enableBase64Decode | bool | `false` | Set this to true if the root key retrieved from vault needs to be base64 decoded. |
| steelcouch.vault.namespace | string | `"ns_dev/ns_cityos_platform"` | The Vault namespace to be used when requesting secrets from Vault |
| steelcouch.vault.role | string | `"steelcouch-app"` | The Vault role used by the Vault agent auto-auth method |
| steelcouch.vault.secretPath | string | `"steelcouch/rootkey"` | The path in Vault where root key secret is located |
| test.couchDBCredentialSecretName | string | `nil` |  |
| test.image.pullPolicy | string | `"IfNotPresent"` |  |
| test.image.repository | string | `"docker.artifactory-ha.tri-ad.tech/wcm-cityos/core/secure-kvs-integration-test"` | Container repository for the image of the integration test triggered by the Helm `test` chart hook |
| test.image.tag | string | `"main-95791c93-5504"` |  |
| test.resources.limits.cpu | int | `1` | CPU request for the integration test  |
| test.resources.limits.memory | string | `"512Mi"` |  |
| test.resources.requests.cpu | string | `"100m"` | CPU request for the integration test  |
| test.resources.requests.memory | string | `"128Mi"` |  |

## Generate README

The folder uses [helm-docs](https://github.com/norwoodj/helm-docs) to generate README.md.

We can regenerate the document by executing the follwoing command from the root folder of the repository.

```bash
$ helm-docs -c infrastructure/helm/secure-kvs --template-files=infrastructure/helm/secure-kvs/README.md.gotmpl
```

## Generate sample manifests

To support PR reviews, generated manifests with sample `values.yaml` files are stored under the `generated_manifests`.

We can regenerate the manifests by executing `generated_manifests/generate_sample_manifests.sh`. The script uses [kubectl-slice](https://github.com/patrickdappollonio/kubectl-slice).

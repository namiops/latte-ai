# agora-redis

![Version: 0.0.0](https://img.shields.io/badge/Version-0.0.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 1.2.4](https://img.shields.io/badge/AppVersion-1.2.4-informational?style=flat-square)

This chart deploys a RedisFailover resource, a custom resource offered by Spotahome's redis operator to the Agora platform. The chart applies some descriptions to the RedisFailover manifest to enable required functionality such as the headless service for accessing the cluster under the service mesh and node announcement for allowing failover under the service mesh.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| City OS Platform | <wcm-city-os-platform@woven-planet.global> |  |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| bootstrapNode | object | `{"allowSentinels":null,"host":null,"port":null}` | BootstrapNode manifest options. See: https://github.com/spotahome/redis-operator#bootstrapping-from-pre-existing-redis-instances |
| containerSecurityContext | object | `{"allowPrivilegeEscalation":false,"capabilities":{"drop":["ALL"]},"privileged":false,"readOnlyRootFilesystem":true}` | Custom containerSecurityContext for the redis cluster. These default values with https://security.woven-planet.tech/standards/cloud-and-kubernetes/k8s-configuration-standard/#pod-security |
| passwordSecretName | string | `nil` | Secret name with a password field to enable auth: |
| redis.affinity | string | `nil` | Kubernetes affinity options for the redis cluster |
| redis.customConfig | string | `nil` | Custom configuration for the redis cluster |
| redis.image | string | `"redis:6.2.6-alpine"` | Redis image to use. |
| redis.imagePrepend | string | `"docker.artifactory-ha.tri-ad.tech"` | Path to prepend to the image, defaults to artifactory. |
| redis.name | string | `"redis"` | Name of the Redis resource |
| redis.podAnnotations | string | `nil` | Pod annotations for the redis cluster |
| redis.protectedMode | bool | `true` | Whether to use protectedMode or not |
| redis.replicas | int | `3` | Number of replicas for the redis cluster |
| redis.resources | object | `{"limits":{"cpu":"500m","memory":"500Mi"},"requests":{"cpu":"100m","memory":"100Mi"}}` | Resource requests and limits for the redis cluster. |
| redis.serviceAnnotations | string | `nil` | Service annotations for the redis cluster |
| redisFailoverLabelWhitelists | list | `[".*"]` | labelWhitelist for RedisFailover. Details: https://github.com/spotahome/redis-operator/blob/master/example/redisfailover/control-label-propagation.yaml |
| redisFailoverLabels | object | `{}` | Labels for RedisFailover. These labels are propagated into spawned Pods by default. When using Speedway, add "security.istio.io/tlsMode: istio" |
| securityContext | object | `{"fsGroup":1000,"runAsGroup":1000,"runAsNonRoot":true,"runAsUser":1000,"seccompProfile":{"type":"RuntimeDefault"}}` | Custom security context for the redis cluster. See: https://github.com/spotahome/redis-operator#custom-securitycontext These default values align with https://security.woven-planet.tech/standards/cloud-and-kubernetes/k8s-configuration-standard/#pod-security |
| sentinel.affinity | string | `nil` | Kubernetes affinity options for the sentinel cluster |
| sentinel.customConfig | string | `nil` | Custom configuration for the sentinel cluster |
| sentinel.image | string | `"redis:6.2.6-alpine"` | Redis image to use. |
| sentinel.imagePrepend | string | `"docker.artifactory-ha.tri-ad.tech"` | Path to prepend to the image, defaults to artifactory. |
| sentinel.name | string | `"sentinel"` | Name of the Sentinel resource |
| sentinel.podAnnotations | string | `nil` | Pod annotations for the sentinel cluster |
| sentinel.protectedMode | bool | `true` | Whether to use protectedMode or not |
| sentinel.replicas | int | `3` | Number of replicas for the sentinel cluster |
| sentinel.resources | object | `{"limits":{"cpu":"500m","memory":"500Mi"},"requests":{"cpu":"100m","memory":"100Mi"}}` | Resource requests and limits for the sentinel cluster. |
| sentinel.serviceAnnotations | string | `nil` | Service annotations for the sentinel cluster |
| targetEnv | string | `nil` | Useful when deploying on Speedway to create extra resources needed for Operator to connect to Sentinel - Accepted values are `speedway-prod` and `speedway-dev` respectively for Speedway Prod and Dev |

## Usage

We can deploy a Redis cluster with the default values in the above table through [Helm CLI](https://helm.sh/docs/intro/install/) like the following.

```bash
# target namespace
$ kubectl get ns redis-test
NAME         STATUS   AGE
redis-test   Active   61s

# deploy a Redis cluster named "test-redis"
$ helm install -n redis-test test-redis infrastructure/helm/agora-redis --set namespace=redis-test --set name=test-redis
NAME: test-redis
LAST DEPLOYED: Wed Aug 30 06:54:25 2023
NAMESPACE: redis-test
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
The Redis cluster has been successfully deployed.
You can check the resource by executing the following command.

$ kubectl get -n redis-test redisfailovers.databases.spotahome.com test-redis-redis

# check the PostgresCluster resource
$ kubectl get -n redis-test redisfailovers.databases.spotahome.com test-redis-redis
NAME               NAME               REDIS   SENTINELS   AGE
test-redis-redis   test-redis-redis   3       3           2m48s

# list the resources deployed by Spotahome's redis-operator
$ kubectl get po -n redis-test
NAME                                    READY   STATUS    RESTARTS   AGE
rfr-test-redis-redis-0                  1/2     Running   0          4m30s
rfr-test-redis-redis-1                  0/2     Pending   0          4m29s
rfr-test-redis-redis-2                  0/2     Pending   0          4m28s
rfs-test-redis-redis-5f966c84c5-7m8xn   0/2     Pending   0          4m29s
rfs-test-redis-redis-5f966c84c5-8zjhh   2/2     Running   0          4m29s
rfs-test-redis-redis-5f966c84c5-q8jz7   0/2     Pending   0          4m28s
```

## Generate README

The folder uses [helm-docs](https://github.com/norwoodj/helm-docs) to generate README.md.

We can regenerate the document by executing the follwoing command from the root folder of the repository.

```bash
$ helm-docs -c infrastructure/helm/agora-redis --template-files=infrastructure/helm/agora-redis/README.md.gotmpl
```

Integrating the document generation with Bazel is a future task.

## Generate sample manifests

To support PR reviews, generated manifests with sample `values.yaml` files are stored under the `generated_manifests`.

We can regenerate the manifests by executing `generated_manifests/generate_sample_manifests.sh`. The script uses [kubectl-slice](https://github.com/patrickdappollonio/kubectl-slice).

Integrating it with the CI pipeline is a future task.

## Additional Info 

You can refer this for more details : https://github.com/spotahome/redis-operator


# Deploy applications on Speedway using ArgoCD

NOTE: Very WIP. This is intended for devrel to follow for creating namespaces in Speedway.

## 1. monitor for PR

lok for a PR for your given namespace like this <https://github.tri-ad.tech/TRI-AD/mtfuji-namespaces/pull/1288> and wait for it to be merged

## 2. Apply Active Clusters, vCluster Labels and Resource Quotas

you must FORK <https://github.tri-ad.tech/TRI-AD/mtfuji-namespaces/>

```bash
devjson="{\"user_labels\": {\"vcluster.loft.sh/managed-by\": \"agora-control-plane-dev-x-dev\"}}"
prodjson="{\"user_labels\": {\"vcluster.loft.sh/managed-by\": \"agora-control-plane-prod-x-prod\"}}"
activeclusters="[\"gc-0-apps-ap-northeast-1\", \"gc-0-apps-prod-ap-northeast-1\",\"ml-0-apps-ap-northeast-1\", \"ml-0-apps-prod-ap-northeast-1\"]"

yq e ".module.mod_${1}.active_clusters = ${activeclusters}" -i -I=0 -o=json ./namespaces/$1.tf.json
yq e ".module.mod_${1}.configuration.dev = ${devjson}" -i -I=0 -o=json ./namespaces/$1.tf.json
yq e ".module.mod_${1}.configuration.prod += ${prodjson}" -i -I=0 -o=json ./namespaces/$1.tf.json

if [ $# -eq 3 ]
    then
    resourcejson="{\"resource_quotas\": {\"limits.cpu\": \"$2\",\"limits.memory\": \"$3Gi\",\"requests.cpu\": \"$2\",\"requests.memory\": \"$3Gi\"}}"
    yq e ".module.mod_${1}.configuration.prod += ${resourcejson}" -i -I=0 -o=json ./namespaces/$1.tf.json
fi
```

run it from your fork root using `./vcluster-labels.sh "<project-name>" <cpu> <memory>`. you can omit cpu and memory if they were not requested. example call:`namespaces/vcluster-labels.sh "agora-gatekeeper-system" 30 30`

do not format the json (on request of SMC team).
squash commits (on request of SMC team)
create a pr using your fork, and post to #kubernetes

## 3. Add NS to proxy config

The directory structure for adding namespaces to the proxy configuration is as follows:

- GC DEV Cluster: `infra/configs/dev/agora-control-plane/gc/namespaces`

- ML DEV Cluster: `infra/configs/dev/agora-control-plane/ml/namespaces`

- GC PROD Cluster: `infra/configs/prod/agora-control-plane/gc/namespaces`

- ML PROD Cluster: `infra/configs/prod/agora-control-plane/ml/namespaces`

## 4. Restart vCluster and Proxy

Note: This step is no longer necessary. When the changes made in step 3 above are applied, the proxy will automatically
detect them and will reload the namespace list.

If, after applying the changes, something has not synced within a reasonable time, please contact the infra team for
investigation of the issue.

# `id-test` workspace

## Overview
This is a test purpose workspace for `id` namespace component. Namespaces and kustomizations of this workspace are disabled by default for resource conservation.

## Namespaces
### Management Cluster
- `id-test`
- `id-test-drako-v1`

### Worker1 Cluster
- `id-test-drako-v1`

## Enable `id-test` workspace in flux reconcilation tree
You need to add all related namespace to flux reconcilation tree. You can do it manually but here is the script for convinence.
```bash
#!/bin/bash

LOCAL_CLUSTERS_DIR=./infrastructure/k8s/environments/local/clusters
# Add `id-test` and `id-test-drako-v1` into mgmt-east
yq e '
    del(.resources[] | select(. == "id-test.yaml" or . == "id-test-drako-v1.yaml")) |
    .resources +=  ["id-test.yaml", "id-test-drako-v1.yaml"]
' -i ./$LOCAL_CLUSTERS_DIR/mgmt-east/flux-system/kustomizations/services/kustomization.yaml

# Add `id-test-drako-v1` into worker1-east
yq e '
    del(.resources[] | select(. == "id-test-drako-v1.yaml")) |
    .resources +=  ["id-test-drako-v1.yaml"]
' -i ./$LOCAL_CLUSTERS_DIR/worker1-east/flux-system/kustomizations/services/kustomization.yaml
```


## Manually build and apply
Incase you don't want to wait for flux reconcilation. Just build the kustomization manually. Here is all related flux kustomization file for `id-test`
```bash
#!/bin/bash

MGMT_CONTEXT=kind-mgmt-east
WORKER1_CONTEXT=kind-worker1-east
LOCAL_CLUSTERS_DIR=./infrastructure/k8s/environments/local/clusters
DIR=/tmp/id-test
MGMT_OUT=/tmp/id-test/mgmt.yaml
WORKER1_OUT=/tmp/id-test/worker1.yaml

rm -rf $DIR
mkdir -p $DIR

flux build --context=$MGMT_CONTEXT kustomization id-test --path $LOCAL_CLUSTERS_DIR/mgmt-east/id-test --kustomization-file $LOCAL_CLUSTERS_DIR/mgmt-east/flux-system/kustomizations/services/id-test.yaml >> $MGMT_OUT
echo "---" >> $MGMT_OUT
flux build --context=$MGMT_CONTEXT kustomization id-test-drako-v1 --path $LOCAL_CLUSTERS_DIR/mgmt-east/id-test-drako-v1 --kustomization-file $LOCAL_CLUSTERS_DIR/mgmt-east/flux-system/kustomizations/services/id-test-drako-v1.yaml >> $MGMT_OUT

flux build --context=$WORKER1_CONTEXT kustomization id-test-drako-v1 --path $LOCAL_CLUSTERS_DIR/worker1-east/id-test-drako-v1 --kustomization-file $LOCAL_CLUSTERS_DIR/worker1-east/flux-system/kustomizations/services/id-test-drako-v1.yaml >> $WORKER1_OUT

```

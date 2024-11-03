**Table of Contents**

<!-- vim-markdown-toc GFM -->

- [About this document](#about-this-document)
- [TL;DR](#tldr)
- [Upgrade Istiod](#upgrade-istiod)
  - [Generate Istiod CRD](#generate-istiod-crd)
  - [Generate Istiod Operator](#generate-istiod-operator)
  - [Verify changes](#verify-changes)
  - [Rollout](#rollout)
    - [Testing](#testing)
    - [Finish roll-out](#finish-roll-out)
      - [For example:](#for-example)
  - [Cleanup](#cleanup)
    - [policy](#policy)
    - [restarting workloads](#restarting-workloads)
    - [verify](#verify)
    - [Example](#example)
- [Istio Gateways](#istio-gateways)
  - [Default Gateways](#default-gateways)
  - [other Gateways](#other-gateways)
- [Other dependencies](#other-dependencies)
  - [Kiali](#kiali)
    - [Example](#example-1)
  - [Jaeger](#jaeger)
- [Post upgrade manual check](#post-upgrade-manual-check)
- [What could go wrong / post mortem](#what-could-go-wrong--post-mortem)

<!-- vim-markdown-toc -->

## About this document

This document outlines the procedure to upgrade Istiod in our clusters. 
After the upgrade, we support the usage of one prior version of Istio for up to one month.
The upgrades order is `local` -> `lab` -> `ci` -> `dev`

The following variables are used in this document to make the command lines generic.
If you set them accordingly, you should be able to copy and paste the command lines.
You are assumed to use a standard shell environment (bash, zsh, ...)

Variables in this doc:
  - `cluster`: set it to the corresponding sub-folder under
    `city/infrastructure/k8s/` (`local`,`lab`,`ci`,`dev`)
  - `version`: set it to the _new_ Istiod version that you want to install
    (e.g., 1.16.3)

## TL;DR
Here is the short version.
Update the corresponding script in `city/infrastructure/k8s/${cluster}/bin/`,
then create and merge the following PRs in the correct order.
Test in between each merge.

1. Update the Istio CRD
2. Add the new Istio version
3. Update Istio GW (e|ingress)
4. Update other dependencies (E.g., publicGW, Kiali, ...)

## Upgrade Istiod

:bell: Important notes:
- Upgrading across more than two minor versions (e.g., 1.6.x to 1.9.x) in one step is not officially tested or recommended.
- Update [istioctl CLI](https://github.com/istio/istio/releases) in your
  environment to version >= Istio version you want to upgrade
- Istiod upgrades order is `local` -> `lab` -> `ci` -> `dev`

### Generate Istiod CRD

- Update the `ISTIO_VERSION` variable in the `istio-crds` script to the _latest
  minor_ version of the major version that you want to update to.
  For example, 1.15.5 to 1.16.3 instead of 1.16.1
- run the script to generate the new version of the CRD.
```
city/infrastructure/k8s/${cluster}/bin/istio-crds
```

### Generate Istiod Operator
- Create the new operator script
  `city/infrastructure/k8s/${cluster}/bin/istiod-${version}` by copying an older
  version or a version from a different cluster root. Then adjust  the following
  inside the script itself:
  - ensure the correct version of Kubernetes is used in the variable `KUBE_VERSION`
  - change `ISTIO_VERSION` to the same version as before in the `istio-crds` script.
  - make sure `ISTIO_REVISION_TAG` is unique; otherwise, it will throw an
    error like:
      ```sh
      Error: accumulating resources: accumulation err='merging resources from 'istiod-1-XX-X.yaml': may not add resource with an already registered id:
      ```
  - If you are testing the same version of Istio with a different
      configuration, ensure that the variable `ISTIO_REVISION` is also
      changed.
- run the new script to generate the new version of the istiod deployment.
```sh
city/infrastructure/k8s/${cluster}/bin/istiod-${version}
```

- Append the new istiod-${version}.yaml to the kustomization.yaml _without removing the old version_.
```sh
$EDITOR city/infrastructure/k8s/${cluster}/istio-system/kustomization.yaml
```
### Verify changes
```sh
istioctl version
```
__You should see the new version and (at least) the current version listed.__

```sh
istioctl -n istio-system analyze city/infrastructure/k8s/${cluster}/istio-system/istiod-${version}.yaml
```
__You should get a message similar to the below__
```
✔ No validation issues found when analyzing city/infrastructure/k8s/${cluster}/istio-system/istiod-${version}.yaml.
```
### Rollout
Create a PR and merge it

#### Testing
Test the new version by creating a namespace using the `ISTIO_REVISION_TAG`
variable previously defined in
`city/infrastructure/k8s/${cluster}/bin/istiod-${version}`
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: httpbin
  labels:
    istio.io/rev: default # change this according to ISTIO_REVISION_TAG
```
Test again by running `istioctl analyze --all-namespaces`

Ideally, make some sort of connectivity test to ensure that all connections
work as expected with the new version of Istio.

#### Finish roll-out
After making sure the update is stable, point the `default` tag to the new version
```sh
$EDITOR city/infrastructure/k8s/${cluster}/bin/default_tag
```
- Generate default tag
```sh
city/infrastructure/k8s/${cluster}/bin/default_tag
```
Submit the generated changes and merge them.

After reconciliation, check that the new version is associated with the `default` tag.
```sh
istioctl tag list
```
##### For example:
```
$ istioctl tag list
TAG      REVISION NAMESPACES
default  1-16-3   ns-a,ns-b
prod-old 1-15-5
prod     1-16-3
```

### Cleanup
Clean up old versions of istiod.

More importantly. we must also check that the older version (two versions prior, i.e., `1.15.5`) is not used before cleaning up. We can check by executing the following:
```sh
istioctl ps | grep 1.15.5
```

#### policy

The infrastructure team will restart the following infrastructure core services after each upgrade:
  - observability
```sh
for n in observability; do for d in $(kubectl -n $n get deployment -o name); do kubectl -n $n rollout restart $d; done; done
```

We support using one prior version of Istio for up to one month. If namespaces
still use the old version after one month, the Infra team will restart
the workloads within reason. We will announce this in the appropriate Slack
channel(s) before it happens.

#### restarting workloads

```sh
kubectl -n <NAMESPACE> rollout restart deployment deployment.apps/<DEPLOYMENT>
```

#### verify
Verify that the old version is no longer in use
```sh
istioctl version
```
You should see at least two istiod versions here (old and new).
Check that all proxy `data plane version:` are on the new version.

#### Example

If the old version is still used in (a) namespace(s), restart the deployment(s)
in question.

```
$ istioctl version
client version: 1.16.3
istiod version: 1.15.5
istiod version: 1.15.5
istiod version: 1.15.5
istiod version: 1.16.3
istiod version: 1.16.3
istiod version: 1.16.3
data plane version: 1.15.5 (96 proxies), 1.16.3 (33 proxies)
```

## Istio Gateways

### Default Gateways

- The process is similar to the above, but without using any revision
- Create a new version of the Ingress and Egress script
  `infrastructure/k8s/${cluster}/bin/egress-gateway`
  `infrastructure/k8s/${cluster}/bin/ingress-gateway`
- Execute the script and run testing
```
export K8S_DIR="infrastructure/k8s/${cluster}"
istioctl analyze $K8S_DIR/city-egress/deployment.yaml $K8S_DIR/city-egress/deployment.yaml
istioctl analyze $K8S_DIR/city-egress/deployment.yaml $K8S_DIR/city-ingress/deployment.yaml
```
- Restart the gateway deployment
```
kubectl rollout restart <DEPLOYMENT NAME>
```

### other Gateways

If you have other Istio gateways, update them as well.
__Especially `city-public-ingress` where applicable (`dev`/`ci`)__


References:
- https://istio.io/latest/docs/setup/upgrade/
- https://istio.io/latest/docs/ops/diagnostic-tools/istioctl-analyze/


## Other dependencies

### Kiali
Kiali uses Envoy filters, and thus there might be incompatibilities with the new version of Istio.

:warning: __Important: Check the [Kiali prerequisites] and upgrade Kiali if needed.__

Some hard-coded values in Kiali must be updated __before__ the old Istio version gets removed.

  - Create a new `values` file under
    /infrastructure/k8s/common/observability/bin by copying the old version and
    changing the Istio version-related values.
  - Create a new version form that `values` file.
  - Run `infrastructure/k8s/common/observability/bin/import -t kiali -f kiali-values-<version>-<agora_number>.yaml -v <helm_chart_version> -r <agora_number>` to generate new folder.
  - Update the kustomizations.

#### Example
```diff
--- kiali-values-1.60-agora1.yaml       2023-04-07 11:40:23.000000000 +0900
+++ kiali-values-1.60-agora2.yaml       2023-05-10 11:05:40.000000000 +0900
@@ -18,9 +20,9 @@
     in_cluster_url: http://grafana:3000/grafana
     url: https://observability.${cluster_domain}/grafana
   istio:
-    config_map_name: istio-1-14-1
-    istio_sidecar_injector_config_map_name: istio-sidecar-injector-1-14-1
-    istiod_deployment_name: istiod-1-14-1
+    config_map_name: istio-1-16-3
+    istio_sidecar_injector_config_map_name: istio-sidecar-injector-1-16-3
+    istiod_deployment_name: istiod-1-16-3
     root_namespace: istio-system
   prometheus:
     url: http://prometheus:9090
```

### Jaeger
Jaeger needs to be restarted after updating Istio.

```sh
kubectl -n observability rollout restart deployment jaeger
```

<!-- Below are the links used in the document -->
[Kiali prerequisites]:https://kiali.io/docs/installation/installation-guide/prerequisites/

## Post upgrade manual check

Go to kustomization of Flux-system and check that istio-system is up to current commit.

```code
kubectl get kustomizations -n flux-system
```

Go to cluster namespace of `istio-system` and `observability` and check that all pods are UP and Ready and contains correct version of image.

```code
kubectl get pods -n istio-system
kubectl get pods -n observability
```

Go to any page which is exposed by gateways which you changed and check that it's reachable. 

Examples for gateways:

1) Ci - https://id.agora-ci.woven-planet.tech/
2) LAB - https://id.agora-lab.woven-planet.tech/
3) DEV - https://id.cityos-dev.woven-planet.tech/

## What could go wrong / post mortem

- Enabling DNS proxy in mesh config
  - Date: 18 Oct 2023
  - Impact: DNS failure in postgres
  - Mitigation/fix: https://github.com/wp-wcm/city/pull/10091
  - Link: https://wovencity.monday.com/boards/3890482591/pulses/5347806046
- Sidecar injector webhook image is not using artifactory
  - Date: 17 Oct 2023
  - Impact: Many pods cannot be scheduled because docker.io throttled image pull rate limit
  - Mitigation/fix: https://github.com/wp-wcm/city/pull/10065
  - Link: https://woven-by-toyota.slack.com/archives/C02USLDU1U3/p1697530288799849?thread_ts=1697506279.397779&cid=C02USLDU1U3
- Proxy protocol is not enabled in either NLB or istio gateway pods
  - Date: 22 Sep 2023
  - Impact: Services exposed in city public ingress are inaccessible
  - Mitigation/fix: https://github.com/wp-wcm/city/pull/8437
  - Link: https://docs.google.com/document/d/13Iedn1GqDlxMkfMk3SJwRlLkgP39iuIMh1d-f8ilorA/edit#heading=h.baf0k7whhb94


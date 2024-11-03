Block network traffic from and to the Internet
==============================================

Block network ingress from the Internet and block network egress to the network (Change gateway selector by patch)

# Pre-requirements
This Run-book assumes that you are already authenticated to the relevant cluster
and that you have sufficient permissions.

# Ingress

## suspend city-public-ingress namespace in flux
FluxCD needs to be suspended for the namespace, so that it does not
automatically revert the manual changes that will follow below.

```sh
flux suspend kustomization city-public-ingress
```
## patch the gateway

### Check the gateways in namespace
```
kubectl -n city-public-ingress get gateways.networking.istio.io 
```
### Dump the current gateway
```
kubectl -n city-public-ingress get gateways.networking.istio.io GATEWAY_NAME -o yaml > gateway.yaml
```
### Apply patch

```sh
cat <<EOF > kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - gateway.yaml
patches:
  - gateway-patch.yaml
EOF
```
Following will change the Istio selector and remove any hosts from the servers list.
This is the example for the _ingress-https_ gateway. Change the name to
_ingress-mtls_ if the application uses the mtls gateway.

For CI change the name accordingly to _ingressgateway_.

```sh
cat <<EOF > gateway-patch.yaml
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: ingress-https
  namespace: city-public-ingress
spec:
  selector:
    istio: city-public-ingress-disabled
  servers:
    - hosts: []
EOF
```

```sh
kubectl kustomize . > gateway-disabled.yaml
```

Check that the result is as expected.
```sh
cat gateway-disabled.yaml
```

```sh
kubectl apply -f gateway-disabled.yaml
```

# Egress

## suspend the istio-system namespace in flux
FluxCD needs to be suspended for the istio-system namespace, so that it does not
automatically revert the manual changes that will follow below.

```sh
flux suspend kustomization city-egress
```
## patch the gateway
### Dump the current gateway
```sh
kubectl -n istio-system get serviceentries.networking.istio.io -o yaml > serviceentries.bak.yaml
```
### Delete the service entry in question
__Take a note of the service entry that you need to delete in the next step__
```sh
kubectl -n istio-system get serviceentries.networking.istio.io
```
__Replace <SERVICEENTRY> with the service entry that you discovered in the step above__

```sh
kubectl -n istio-system delete serviceentries.networking.istio.io <SERVICEENTRY>
```
### Verify
```sh
kubectl -n istio-system get serviceentries.networking.istio.io -o yaml > serviceentries.new.yaml
diff -u serviceentries.bak.yaml serviceentries.new.yaml
```
__Verify that the service entry has been deleted as expected__

### Example - deleting grafana-dependencies
Here an example how to delete the __grafana-dependencies__ service entry.

```sh
$ kubectl -n istio-system get serviceentries.networking.istio.io -o yaml > serviceentries.bak.yaml

$ kubectl -n istio-system get serviceentries.networking.istio.io

NAME                             HOSTS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   LOCATION        RESOLUTION   AGE
external-app-sec-vault           ["dev.vault.tmc-stargate.com"]                                                                                                                                                                                                                                                                                                                                                                                                                                                          MESH_EXTERNAL   DNS          14d
external-github                  ["github.com"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                          MESH_EXTERNAL   DNS          346d
external-login-microsoftonline   ["login.microsoftonline.com","graph.microsoft.com"]                                                                                                                                                                                                                                                                                                                                                                                                                                     MESH_EXTERNAL   DNS          281d
external-svc-msk                 ["kafka.generated"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                     MESH_EXTERNAL   NONE         359d
external-woven-it-vault          ["dev.vault.w3n.io"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                    MESH_EXTERNAL   DNS          274d
figma-sense-api                  ["api.figma.com"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                       MESH_EXTERNAL   DNS          3d16h
firebase                         ["oauth2.googleapis.com","www.googleapis.com","fcm.googleapis.com"]                                                                                                                                                                                                                                                                                                                                                                                                                     MESH_EXTERNAL   DNS          302d
grafana-dependencies             ["grafana.com","storage.googleapis.com","raw.githubusercontent.com"]                                                                                                                                                                                                                                                                                                                                                                                                                    MESH_EXTERNAL   DNS          135d
internal-github                  ["github.tri-ad.tech"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                  MESH_EXTERNAL   DNS          311d
line                             ["api.line.me","api-data.line.me","access.line.me"]                                                                                                                                                                                                                                                                                                                                                                                                                                     MESH_EXTERNAL   DNS          266d
loki-amazon                      ["sts.amazonaws.com","sts.ap-northeast-1.amazonaws.com","wcm-agora-dev-loki-admin-aws-835215587209.s3.amazonaws.com","wcm-agora-dev-loki-chunks-aws-835215587209.s3.amazonaws.com","wcm-agora-dev-loki-ruler-aws-835215587209.s3.amazonaws.com","wcm-agora-dev-loki-admin-aws-835215587209.s3.ap-northeast-1.amazonaws.com","wcm-agora-dev-loki-chunks-aws-835215587209.s3.ap-northeast-1.amazonaws.com","wcm-agora-dev-loki-ruler-aws-835215587209.s3.ap-northeast-1.amazonaws.com"]   MESH_EXTERNAL   DNS          135d
slack-hooks                      ["hooks.slack.com"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                     MESH_EXTERNAL   DNS          308d
smtp                             ["smtp.gmail.com"]                                                                                                                                                                                                                                                                                                                                                                                                                                                                      MESH_EXTERNAL   DNS          311d
soc-sentinel                     ["aadf003e-4c28-4f5b-a64e-262c8bf290ce.ods.opinsights.azure.com"]                                                                                                                                                                                                                                                                                                                                                                                                                       MESH_EXTERNAL   DNS          53d

$ kubectl delete -n istio-system serviceentries.networking.istio.io grafana-dependencies
serviceentry.networking.istio.io "grafana-dependencies" deleted

$ kubectl -n istio-system get serviceentries.networking.istio.io -o yaml > serviceentries.new.yaml

$ diff -u serviceentries.bak.yaml serviceentries.new.yaml

--- serviceentries.bak.yaml     2023-03-18 09:52:40.000000000 +0900
+++ serviceentries.new.yaml     2023-03-18 09:53:21.000000000 +0900
@@ -167,32 +167,6 @@
 - apiVersion: networking.istio.io/v1beta1
   kind: ServiceEntry
   metadata:
-    creationTimestamp: "2022-11-02T02:09:53Z"
-    generation: 2
-    labels:
-      kustomize.toolkit.fluxcd.io/name: istio
-      kustomize.toolkit.fluxcd.io/namespace: flux-system
-    name: grafana-dependencies
-    namespace: istio-system
-    resourceVersion: "408770978"
-    uid: 86e779e7-2f16-4793-a0a5-7616927a9b63
-  spec:
-    exportTo:
-    - observability
-    - observability-system
-    hosts:
-    - grafana.com
-    - storage.googleapis.com
-    - raw.githubusercontent.com
-    location: MESH_EXTERNAL
-    ports:
-    - name: https
-      number: 443
-      protocol: TLS
-    resolution: DNS
-- apiVersion: networking.istio.io/v1beta1
-  kind: ServiceEntry
-  metadata:
     creationTimestamp: "2022-05-10T09:21:14Z"
     generation: 3
     labels:
```

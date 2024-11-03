# envoy-filters-0.3.0 changes

Removed header copy, added a sanitization to an OUTBOUND context, this should simplify XFCC handling and escape potential in-cluster forging.

```diff
diff -up /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0/BUILD /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0/BUILD
--- /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0/BUILD     2024-05-09 14:34:21.579996336 +0900
+++ /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0/BUILD     2024-06-11 12:32:54.662799802 +0900
@@ -1,9 +1,8 @@
 load("//ns/bazel_k8s/k8s:defs.bzl", "k8s_kustomize")
 
 k8s_kustomize(
-    name = "envoy-filters-0.2.0",
+    name = "envoy-filters-0.3.0",
     srcs = [
-        "knative-activator-xfcc-copy.yaml",
         "knative-activator-xfcc-sanitize.yaml",
         "kustomization.yaml",
     ],
Only in /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0: README.md
Only in /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0: knative-activator-xfcc-copy.yaml
diff -up /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0/knative-activator-xfcc-sanitize.yaml /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0/knative-activator-xfcc-sanitize.yaml
--- /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0/knative-activator-xfcc-sanitize.yaml      2024-05-09 14:34:21.579996336 +0900
+++ /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0/knative-activator-xfcc-sanitize.yaml      2024-06-11 12:33:48.892777566 +0900
@@ -10,7 +10,7 @@ spec:
   configPatches:
     - applyTo: NETWORK_FILTER
       match:
-        context: SIDECAR_INBOUND
+        context: SIDECAR_OUTBOUND
         listener:
           filterChain:
             filter:
@@ -20,4 +20,5 @@ spec:
         value:
           typed_config:
             "@type": type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager
-            forward_client_cert_details: APPEND_FORWARD
+            # https://www.envoyproxy.io/docs/envoy/latest/api-v3/extensions/filters/network/http_connection_manager/v3/http_connection_manager.proto
+            forward_client_cert_details: ALWAYS_FORWARD_ONLY
diff -up /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0/kustomization.yaml /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0/kustomization.yaml
--- /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.2.0/kustomization.yaml        2024-05-09 14:34:21.579996336 +0900
+++ /home/oleks/woven-repo/city/infrastructure/k8s/common/knative-serving/envoy-filters-0.3.0/kustomization.yaml        2024-06-11 12:32:45.902804268 +0900
@@ -2,4 +2,3 @@ apiVersion: kustomize.config.k8s.io/v1be
 kind: Kustomization
 resources:
 - knative-activator-xfcc-sanitize.yaml
-- knative-activator-xfcc-copy.yaml
```

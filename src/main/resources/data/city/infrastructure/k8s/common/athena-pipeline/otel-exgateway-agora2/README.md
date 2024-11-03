# Diff with otel-exgateway-agora1

```diff
diff -rup otel-exgateway-agora1/BUILD otel-exgateway-agora2/BUILD
--- otel-exgateway-agora1/BUILD 2024-03-12 15:08:51.718536694 +0900
+++ otel-exgateway-agora2/BUILD 2024-04-12 11:02:45.870787825 +0900
@@ -1,7 +1,7 @@
 load("//ns/bazel_k8s/k8s:defs.bzl", "k8s_kustomize")
 
 k8s_kustomize(
-    name = "otel-exgateway-agora1",
+    name = "otel-exgateway-agora2",
     srcs = [
         "kustomization.yaml",
         "opentelemetrycollector-otel-exgateway.yaml",
@@ -10,6 +10,7 @@ k8s_kustomize(
         "otel-exgateway-config-processors.yaml",
         "otel-exgateway-config-receivers.yaml",
         "otel-exgateway-config-service.yaml",
+        "otel-exgateway-service-account.yaml",
     ],
     visibility = ["//infrastructure/k8s:__subpackages__"],
 )
diff -rup otel-exgateway-agora1/kustomization.yaml otel-exgateway-agora2/kustomization.yaml
--- otel-exgateway-agora1/kustomization.yaml    2024-02-01 10:16:26.462368403 +0900
+++ otel-exgateway-agora2/kustomization.yaml    2024-04-12 11:01:33.960813998 +0900
@@ -2,6 +2,7 @@ apiVersion: kustomize.config.k8s.io/v1be
 kind: Kustomization
 resources:
   - opentelemetrycollector-otel-exgateway.yaml
+  - otel-exgateway-service-account.yaml
 configMapGenerator:
   - name: otel-exgateway-config-receivers
     files: [otel-exgateway-config-receivers.yaml]
diff -rup otel-exgateway-agora1/opentelemetrycollector-otel-exgateway.yaml otel-exgateway-agora2/opentelemetrycollector-otel-exgateway.yaml
--- otel-exgateway-agora1/opentelemetrycollector-otel-exgateway.yaml    2024-02-01 10:16:26.462368403 +0900
+++ otel-exgateway-agora2/opentelemetrycollector-otel-exgateway.yaml    2024-04-12 10:30:32.481491229 +0900
@@ -7,6 +7,8 @@ metadata:
   namespace: athena-pipeline
 spec:
   mode: deployment
+  # service account for IRSA.
+  serviceAccount: otel-exgateway-collector-sa
   affinity:
     nodeAffinity:
       requiredDuringSchedulingIgnoredDuringExecution:
Only in otel-exgateway-agora2/: otel-exgateway-service-account.yaml
```

- Ref PR: https://github.com/wp-wcm/city/pull/23321
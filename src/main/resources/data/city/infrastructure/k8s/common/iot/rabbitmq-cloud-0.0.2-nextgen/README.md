### This is a cleaned-up version of `rabbitmq-cloud-0.0.1-nextgen`

```diff
diff -up /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.1-nextgen /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.2-nextgen
diff -up /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.1-nextgen/BUILD /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.2-nextgen/BUILD
--- /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.1-nextgen/BUILD        2024-07-02 16:27:38.413518028 +0900
+++ /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.2-nextgen/BUILD        2024-07-02 16:32:05.743482589 +0900
@@ -1,21 +1,11 @@
 load("//ns/bazel_k8s/k8s:defs.bzl", "k8s_kustomize")
 
-filegroup(
-    name = "files",
-    srcs = glob(["**/*.yaml"]) + ["erl_inetrc"],
-    visibility = ["//visibility:public"],
-)
-
 k8s_kustomize(
-    name = "rabbitmq-cloud-0.0.1-nextgen",
+    name = "rabbitmq-cloud-0.0.2-nextgen",
     srcs = [
-        "erl_inetrc",
         "kustomization.yaml",
         "service-entry.yaml",
         "virtual-service.yaml",
-        "//infrastructure/k8s/common/iot/rabbitmq-0.0.8:rabbit-cluster.yaml",
-        "//infrastructure/k8s/common/iot/rabbitmq-0.0.8:rabbit-pdb.yaml",
-        "//infrastructure/k8s/common/iot/rabbitmq-0.0.8:users.yaml",
     ],
     visibility = ["//infrastructure/k8s:__subpackages__"],
 )
Only in /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.2-nextgen: README.md
Only in /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.1-nextgen: erl_inetrc
diff -up /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.1-nextgen/kustomization.yaml /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.2-nextgen/kustomization.yaml
--- /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.1-nextgen/kustomization.yaml   2024-07-02 16:27:38.413518028 +0900
+++ /home/oleks/woven-repo/city/infrastructure/k8s/common/iot/rabbitmq-cloud-0.0.2-nextgen/kustomization.yaml   2024-07-02 16:28:47.863509903 +0900
@@ -3,41 +3,3 @@ kind: Kustomization
 resources:
   - service-entry.yaml
   - virtual-service.yaml
-  # adding lab2 manifests for easier rollback, don't want RabbitMQ operator to suddenly delete something.
-  # this is temporary, once everything is proven to work well - below can be deleted.
-  - ../rabbitmq-0.0.8/rabbit-cluster.yaml
-  - ../rabbitmq-0.0.8/rabbit-pdb.yaml
-  - ../rabbitmq-0.0.8/users.yaml
-patches:
-  # IPv6 support based on https://www.rabbitmq.com/networking.html#distribution-ipv6
-  - patch: |
-      - op: add
-        path: "/spec/override/statefulSet/spec/template/spec/containers/0/env"
-        value:
-          - name: RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS
-            value: "-kernel inetrc '/etc/rabbitmqconfig/erl_inetrc' -proto_dist inet6_tcp"
-          - name: RABBITMQ_CTL_ERL_ARGS
-            value: "-proto_dist inet6_tcp"
-      - op: add
-        path: "/spec/override/statefulSet/spec/template/spec/containers/0/volumeMounts"
-        value:
-          - name: rabbitmqconfig
-            mountPath: "/etc/rabbitmqconfig"
-            readOnly: true
-      - op: add
-        path: "/spec/override/statefulSet/spec/template/spec/volumes"
-        value:
-          - name: rabbitmqconfig
-            configMap:
-              name: rabbitmqconfig
-    target:
-      kind: RabbitmqCluster
-      namespace: iot
-      name: rabbitmq
-configMapGenerator:
-  - name: rabbitmqconfig
-    namespace: iot
-    files:
-      - erl_inetrc
-    options:
-      disableNameSuffixHash: true
```

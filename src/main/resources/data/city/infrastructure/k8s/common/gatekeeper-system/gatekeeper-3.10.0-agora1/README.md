Gatekeeper 3.10.0
================

```
curl -L https://raw.githubusercontent.com/open-policy-agent/gatekeeper/v3.10.0/deploy/gatekeeper.yaml | yamlfmt - | kubectl slice -o ./
kustomize create --autodetect . && yamlfmt kustomize.yaml

```

# Manual changes #

## service-gatekeeper-webhook-service.yaml ##

### Patch to add Name-Space ###
```sh
patch <<EOF
diff --git a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/kustomization.yaml b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/kustomization.yaml
index 49d29d49d..118774ff6 100644
--- a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/kustomization.yaml
+++ b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/kustomization.yaml
@@ -1,5 +1,6 @@
 apiVersion: kustomize.config.k8s.io/v1beta1
 kind: Kustomization
+namespace: gatekeeper-system
 resources:
   - clusterrole-gatekeeper-manager-role.yaml
   - clusterrolebinding-gatekeeper-manager-rolebinding.yaml
EOF
```

### Apply the following patch to enable metrics: ###
```sh
patch <<EOF
diff --git a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/service-gatekeeper-webhook-service.yaml b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/service-gatekeeper-webhook-service.yaml
index 3bf7db952..b654be495 100644
--- a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/service-gatekeeper-webhook-service.yaml
+++ b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/service-gatekeeper-webhook-service.yaml
@@ -10,6 +10,9 @@ spec:
     - name: https-webhook-server
       port: 443
       targetPort: webhook-server
+    - name: http-metrics
+      port: 8888
+      targetPort: metrics
   selector:
     control-plane: controller-manager
     gatekeeper.sh/operation: webhook
EOF
```
### Apply the following patch to enable metrics: ###
```sh
patch <<EOF
diff --git a/kustomization.yaml b/kustomization.yaml
index 118774ff6..13b53135e 100644
--- a/kustomization.yaml
+++ b/kustomization.yaml
@@ -25,4 +25,5 @@ resources:
   - secret-gatekeeper-webhook-server-cert.yaml
   - service-gatekeeper-webhook-service.yaml
   - serviceaccount-gatekeeper-admin.yaml
+  - servicemonitor-gatekeeper-controller-manager.yaml
   - validatingwebhookconfiguration-gatekeeper-validating-webhook-configuration.yaml
diff --git a/servicemonitor-gatekeeper-controller-manager.yaml b/servicemonitor-gatekeeper-controller-manager.yaml
new file mode 100644
index 000000000..cf7a2ce6a
--- /dev/null
+++ b/servicemonitor-gatekeeper-controller-manager.yaml
@@ -0,0 +1,18 @@
+apiVersion: monitoring.coreos.com/v1
+kind: ServiceMonitor
+metadata:
+  name: gatekeeper-controller-manager
+  labels:
+    gatekeeper.sh/system: "yes"
+    agora-prom-injected: "true"
+spec:
+  selector:
+    matchLabels:
+      gatekeeper.sh/system: "yes"
+  endpoints:
+    - port: http-metrics
+      interval: 5s
+      relabelings:
+        - replacement: \${agora_environment_cluster}
+          targetLabel: cluster
+      scheme: http
EOF
```
### Enable generator resource expansion feature
```sh
patch <<EOF
diff --git a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-audit.yaml b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-audit.yaml
index 125c4b656..300db6189 100644
--- a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-audit.yaml
+++ b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-audit.yaml
@@ -30,6 +30,7 @@ spec:
             - --logtostderr
             - --disable-opa-builtin={http.send}
             - --disable-cert-rotation
+            - --enable-generator-resource-expansion
           command:
             - /manager
           env:
diff --git a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-controller-manager.yaml b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-controller-manager.yaml
index 6fcd14f97..c9850fb72 100644
--- a/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-controller-manager.yaml
+++ b/infrastructure/k8s/common/gatekeeper-system/gatekeeper-3.10.0-agora1/deployment-gatekeeper-controller-manager.yaml
@@ -42,6 +42,7 @@ spec:
             - --operation=webhook
             - --operation=mutation-webhook
             - --disable-opa-builtin={http.send}
+            - --enable-generator-resource-expansion
           command:
             - /manager
           env:
EOF
```

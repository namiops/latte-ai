## Changes
- Addressed gatekeeper violations

### Diff

```diff
--- /tmp/main   2023-04-05 20:30:33.000000000 +0900
+++ /tmp/iota-gatekeeper-violation-fixes        2023-04-05 20:30:22.000000000 +0900
@@ -10,7 +10,7 @@ metadata:
   name: iot
 ---
 apiVersion: v1
-automountServiceAccountToken: true
+automountServiceAccountToken: false
 kind: ServiceAccount
 metadata:
   name: iota
@@ -75,6 +75,7 @@ spec:
       labels:
         app: iota
     spec:
+      automountServiceAccountToken: true
       containers:
       - env:
         - name: IDP_URL
@@ -125,11 +126,32 @@ spec:
           value: "true"
         image: docker.artifactory-ha.tri-ad.tech:443/wcm-cityos/services/iota:main-6a822010-1680571035
         imagePullPolicy: Always
+        livenessProbe:
+          httpGet:
+            path: /livez
+            port: 8081
+          initialDelaySeconds: 20
+          periodSeconds: 20
         name: iota
+        readinessProbe:
+          httpGet:
+            path: /readyz
+            port: 8081
+          initialDelaySeconds: 20
+          periodSeconds: 10
         resources:
           limits:
             cpu: "1"
             memory: 200Mi
+        securityContext:
+          allowPrivilegeEscalation: false
+          capabilities:
+            drop:
+            - ALL
+          readOnlyRootFilesystem: true
+          runAsGroup: 1000
+          runAsNonRoot: true
+          runAsUser: 65532
       serviceAccountName: iota
 ---
 apiVersion: apps/v1
```
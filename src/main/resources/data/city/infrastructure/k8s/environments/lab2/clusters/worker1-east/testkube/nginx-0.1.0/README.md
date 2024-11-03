This directory should be moved under the "common" directory for the Lab2 environment once it is available.

The reason why we have a version here is to define and use the CR `RouteTable` instead of `VirtualService`.

`AuthorizationPolicy` is also disabled for the mean time to unblock access to Testkube's dashboard

diff:
```diff
$ diff -rup ../../../../../../common/testkube/nginx-0.1.3 .
Only in .: README.md
diff -rup ../../../../../../common/testkube/nginx-0.1.3/kustomization.yaml ./kustomization.yaml
--- ../../../../../../common/testkube/nginx-0.1.3/kustomization.yaml    2023-03-28 10:02:41.288818503 +0900
+++ ./kustomization.yaml        2023-07-25 17:27:39.131075347 +0900
@@ -1,11 +1,11 @@
 apiVersion: kustomize.config.k8s.io/v1beta1
 kind: Kustomization
 resources:
-  - authorizationpolicy.yaml
+  # TODO: temporarily disable to unblock access to testkube dashboard
+  # - authorizationpolicy.yaml
   - configmap-nginx-conf.yaml
   - deployment-nginx.yaml
   - service-nginx.yaml
-  - virtualservice.yaml
 images:
   - name: nginx
     newName: docker.artifactory-ha.tri-ad.tech/nginx

Only in ../../../../../../common/testkube/nginx-0.1.3: virtualservice.yaml
```
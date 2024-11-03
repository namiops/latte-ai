# Guide

Run the following command to generate the `out/allure-testops` folder.

```
bazel run //infrastructure/k8s/common/qa-nexus/allure:allure-testops.copy
```

## Required secrets:

This secrets needs to be created manually since they should not be managed by flex

```yaml
# Source: allure-testops/templates/infra/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: allure-testops-secrets
  namespace: qa-nexus
type: Opaque
data:
  admin-username:
  admin-password:
  jwtSecret: 
  cryptoPass: 
  s3AccessKey: 
  s3SecretKey: 
---
apiVersion: v1
kind: Secret
metadata:
  name: allure-credential
  namespace: qa-nexus
type: kubernetes.io/dockerconfigjson
data:
  .dockerconfigjson:
```

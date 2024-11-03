# Deploying an application on Speedway

## Overview

This document provides steps to guide you with deploying a simple application on the Speedway environment.

## Prerequisites

- Before you start, please ensure that your have completed setup of the namespace for your application with [CityCD](/docs/default/Component/agora-migrations-tutorial/speedway/citycd/)
- Confirm your application's folder structure

    ```plain
    ./infra/k8s/
    └── agora-<namespace>/
        └── speedway/
            ├── common/
            │   └── kustomization.yaml
            ├── dev/
            │   └── kustomization.yaml
            └── prod/
                └── kustomization.yaml
    ```

- Optional: confirm your project namespace on [ArgoCD](https://argocd.agora-dev.w3n.io/applications)

## Steps

Explanation about the folder structure before we start:

- `common` folder where we store base manifest files reusable for different environment such as: dev and prod
- `dev` and `prod` are folders where we store patches files and specific config for every app namespace env such as: `agora-your-app-dev` and `agora-your-app-prod`

### Step 1: Preparing `common` manifest files

Add the files below to `./agora-<namespace>/speedway/common/`

```yaml title="deployment.yaml"
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: <your-appname>
  name: <your-appname>
spec:
  replicas: 1
  selector:
    matchLabels:
      app: <your-appname>
  template:
    metadata:
      labels:
        app: <your-appname>
    spec:
      containers:
        - name: <your-appname>
          image: <your-image-location>
          ports:
            - containerPort: <your-app-port>
              name: http
              protocol: TCP
```

```yaml title="service.yaml"
apiVersion: v1
kind: Service
metadata:
  name: <your-appname>
spec:
  selector:
    app: <your-appname>
  ports:
    - port: 8000
      name: http
      protocol: TCP
```

```yaml title="kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yaml
  - service.yaml
```

### Step 2: Load patch and config files for the environments

Add deployment patch files, this is required by the [SMC k8s configuration standard](https://security.woven-planet.tech/standards/cloud-and-kubernetes/k8s-configuration-standard/):

```yaml title="./dev/patches/patch-deployment.yaml"
- op: replace
  path: /spec/template/spec/securityContext
  value:
    runAsUser: 1000
    runAsGroup: 1000
    fsGroup: 1000
    runAsNonRoot: true
    seccompProfile:
      type: RuntimeDefault

- op: replace
  path: /spec/template/spec/containers/0/securityContext
  value:
    readOnlyRootFilesystem: true
    allowPrivilegeEscalation: false
    capabilities:
      drop:
        - ALL

- op: replace
  path: /spec/template/spec/containers/0/resources
  value:
    requests:
      memory: "1Gi"
      cpu: "256m"
    limits:
      memory: "2Gi"
      cpu: "512m"
```

```yaml title="./dev/kustomization.yaml"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../common
patches:
- path: ./patches/patch-deployment.yaml
  target:
    kind: Deployment
images:
  - name: traffic-signal-placeholder
    newName: docker.artifactory-ha.tri-ad.tech/wcm-backend/<your-app-image>
    newTag: <your-app-image-tag>
```

Repeat and add the same config files above to the `prod` environment.
The files structure will look like below:

```plain
./infra/k8s/
└── agora-<namespace>/
    └── speedway/
        ├── common/
        │   └── deployment.yaml
        │   └── service.yaml
        │   └── kustomization.yaml
        ├── dev/
        │   └── patches/
        │       └── patch-deployment.yaml
        │   └── kustomization.yaml
        └── prod/
            └── patches/
                └── patch-deployment.yaml
            └── kustomization.yaml
```

### Step 3: Finish

Run `bazel run //:gazelle` and create a new branch from `main`, and don't forget commit the BUILD files.
Create a pull request and ask for reviewal.

## Additional documents for application setup

- [Exposing a private service in Speedway](/docs/default/component/agora-migrations-tutorial/speedway/expose-private-service/)
- [Drako](/docs/default/component/agora-migrations-tutorial/speedway/drako/)
- [KeyCloak and SecureKVS](/docs/default/component/agora-migrations-tutorial/speedway/keycloak-and-securekvs/)

## Known Issues and Solutions

### Cannot connect to Service

**Case 1:** Request not able to reach the application container and there is a `0 NR filter_chain_not_found` error in `istio-proxy`
**Case 2:** 2 services within one namespace cannot communicate with each other, envoy returns `503 Service Unavailable`, in `istio-proxy` logs there is: `0 NR filter_chain_not_found`

**Solution**:

1. Add `security.istio.io/tlsMode: istio` label to target deployment template labels ([example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl/speedway/common/tsl-frontend-business/deployment.yaml#L16)).
1. If it’s between 2 namespaces need to add namespace to sidecar ([example](https://github.com/wp-wcm/city/pull/34323/files))
1. If using a ServiceEntry, may need to add DNS annotations on pod ([example](https://github.com/wp-wcm/city/blob/1a7c0f96b401615e1eaf49f2794ad15bcfcdeea3/infra/k8s/agora-iot/speedway/common/iota-0.0.11/iota.yaml#L30-L33))

### Resource Quota hits limit reached

Pods have below type of logs

```shell
Warning  SyncError  1s (x5 over 5s)  pod-syncer         Error syncing to physical cluster: pods "keycloak-kijo-1" is forbidden: exceeded quota: default, requested: limits.cpu=1, used: limits.cpu=11050m, limited: limits.cpu=12
```

**Solution:**
Contact ***@agora-devrel*** on slack

# Kustomize Controller

> The kustomize-controller is a Kubernetes operator, specialized in running
continuous delivery pipelines for infrastructure and workloads defined with
Kubernetes manifests and assembled with Kustomize.

Kustomization is a confusing concept to explain due to the decision to overload
the definition of the Kustomization kind. In projects that use flux you can
find references to kubernetes manifests that use the kind Kustomization, but
there are two seperate api specifications for that kind:
kustomize.toolkit.fluxcd.io and kustomize.config.k8s.io. The flux version is
a resource that is used to describe control of the behavior of a controller
(the Kustomize controller). The k8s version is a resource that is used to
mark multiple files and optionally patches them using JSON 6202 patches
after render.

Here is an sample flux Kustomization yaml taken from the Agora repository:

```yaml
---
apiVersion: kustomize.toolkit.fluxcd.io/v1beta2
kind: Kustomization
metadata:
  name: services
  namespace: flux-system
spec:
  interval: 1m0s
  path: ./infrastructure/k8s/ci/flux-system/kustomizations/services
  dependsOn:
    - name: system
  prune: true
  sourceRef:
    kind: GitRepository
    name: cityos
```

The spec.sourceRef above points to the specific type of VCS and the name
of that repository. The Kustomization builds the YAML manifests located at
the specified spec.path, validates the objects against the Kubernetes API,
and finally applies them on the cluster.

The k8s Kustomization on the other hand looks like the following:

```yaml
---
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- services.yaml
- system.yaml
- tenants.yaml
patches:
  - patch: |
      - op: add
        path: /spec/serviceAccountName
        value: kustomize-controller
      - op: add
        path: /spec/postBuild
        value:
          substituteFrom:
            - kind: ConfigMap
              name: cluster-vars
      - op: replace
        path: /spec/interval
        value: 10m0s
    target:
      kind: Kustomization
```

We can more easily see the result of the build YAML step by using the kustomize
command line tool directly.

```shell
$ cd infrastructure/k8s/local/flux-system
$ kustomize build .
apiVersion: v1
kind: Namespace
metadata:
  labels:
    app.kubernetes.io/instance: flux-system
    app.kubernetes.io/part-of: flux
    app.kubernetes.io/version: v0.38.2
    pod-security.kubernetes.io/warn: restricted
    pod-security.kubernetes.io/warn-version: latest
  name: flux-system
...
```

Kustomize renders the stream and reorders our objects in dependency order. Then
Flux will attempt to dry run the built YAML (the validation step previously
mentioned) and then finally applies the changes.

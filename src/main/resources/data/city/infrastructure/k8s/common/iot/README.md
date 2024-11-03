# Namespace iot
This folder contains base kustomize files for iot namespace. Common folder contains the base file for other stages e.g. lab, dev. in the path <workspace>/city/infrastructure/k8s/<environment>/iot/kustomization.yaml. Add them to the `resources` section as needed.

## Development 

We bump up version every change so that each environment can refer to the desired version. This can be done by Copy the latest iota-x.x.x folder and bump up version as appropriate.

### Testing
Make changes to kustomization files and preview the result with kubectl

go to environment's kustomization folder and run
```
cd <workspace>/city/infrastructure/k8s/<lab or dev>/iot 
kubectl kustomize 
```
This will output the Kubernetes's configuration and we can compare it with the original output before the changes.

Post the PR with diff in both Kubernetes's configuration diff and kustomize file change between 2 versions.

configuration diff
```
git checkout main
kubectl kustomize /path/to/env/iot > /tmp/existing.yaml
git checkout feature-branch
kubectl kustomize /path/to/env/iot > /tmp/new.yaml

diff -up /tmp/existing.yaml /tmp/new.yaml
```

kustomization diff
```
diff -up iota-0.0.5 iota-0.0.6
```
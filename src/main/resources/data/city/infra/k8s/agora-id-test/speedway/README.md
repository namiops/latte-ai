# Agora ID Test namespace
This namespace contains all Agora ID test application. 
for example

- Minimum setup of application behind drako authentication and authorization.
- Communication between namespace to namespace

## Build kustomize
```bash
$ ./infra/k8s/namespaces/agora-id/speedway/bin/expand.sh ./infra/k8s/agora-id-test/speedway/dev
```

## How to test locally
### Use legacy `local1` cluster
```bash

$ kubectl apply -f - <<EOF
apiVersion: v1
kind: Namespace
metadata:
  name: agora-id-test-dev
  labels:
    istio.io/rev: default
EOF

$ kubectl apply -n id -f ./infra/k8s/agora-id-test/speedway/dev/keycloakclient-agora-id-test-dev.yaml

$ kubectl apply -k ./infra/k8s/agora-id-test/speedway/dev

# add `https://dev.id-test.woven-city.local` to `hostfile`
# and access `https://dev.id-test.woven-city.local` via browser
```

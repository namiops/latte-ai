# How to run message-backend on local cluster

## Run deploy.sh
projects/xrh/dashboard-frontend/k8s/local/deploy.sh

## Expose the service port to the cluster port
kubectl port-forward --address 0.0.0.0 service/xrh-dashboard-frontend 8080:8080

## bash into the pod
kubectl exec --stdin --tty <podname> -- /bin/bash

## Login to artifactory
https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/bazel/#set-up-access-to-artifactory

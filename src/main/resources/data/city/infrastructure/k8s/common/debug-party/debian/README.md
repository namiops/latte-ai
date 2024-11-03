# Debug Party Pod Debian
These deployments are for debug purpose.

## Deploy
On many clusters you will require `sudo` permissions.

```sh
kubectl apply -f deployment.yaml
kubectl apply -f deployment-injected.yaml
kubectl apply -f serviceentry.yaml
```

## Start a shell
```sh
kubectl exec -it debian-deployment-... -- /bin/bash
```

## Cleanup
```sh
kubectl delete deployment ...
```

# Debug Party Pod
These deployments are for debug purpose.

## Deploy
You may need `sudo` permissions.

```sh
kubectl apply -f deployment.yaml
kubectl apply -f deployment-injected.yaml
kubectl apply -f serviceentry.yaml
```

## Start a shell
```sh
kubectl exec -it ubuntu-deployment-... -- /bin/bash
```

## Cleanup
```sh
kubectl delete deployment ...
```

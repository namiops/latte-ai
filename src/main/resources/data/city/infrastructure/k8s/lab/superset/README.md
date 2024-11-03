### Set up

Manually copying Keycloak secret from `id` namespace is required. Here's the command assuming [kubectl neat](https://github.com/itaysk/kubectl-neat) is installed.

```sh
kubectl get secret -n id keycloak-client-secret-superset-external -o yaml | sed 's/namespace: id/namespace: superset/' | kubectl neat | kubectl apply -f -
```

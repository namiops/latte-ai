Shut down application
=====================

Shut down an application by scaling down

# Pre-requirements
This Run-book assumes that you are already authenticated to the relevant cluster
and that you have sufficient permissions.

# Scale deployments to 0

## suspend city-public-ingress namespace in flux
FluxCD needs to be suspended for the namespace, so that it does not
automatically revert the manual changes that will follow below.

## Change to the namespace of the application
```sh
kubectl config set-context --current --namespace city-public-ingress
```

## Scale down all statefulsets, replicasets and deployments to 0

Check current status
```sh
T=()
for i in replicasets statefulsets deployments; do
    T+=$(kubectl get ${i}.apps -o name)
done
kubectl get $T
```

```sh
kubectl scale --replicas=0 $T
```

Check that no more pods are running
```sh
kubectl get pods
```

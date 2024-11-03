# Local Apicurio

## Installation using Flux

Please uncomment the following lines:

In `infrastructure/k8s/local/flux-system/kustomizations/system/kustomization.yaml`

```yaml
- kafka.yaml
- kafka-operator-system.yaml
```

In `infrastructure/k8s/local/flux-system/kustomizations/services/kustomization.yaml`

```yaml
- apicurio.yaml
```

Then push the commit to your branch.

## Quick test

You can check Apicurio with following commands:

1. Forward apicurio port to localhost

```bash
$ minikube service -n apicurio apicurio

$ kubectl get services -n apicurio
NAME   TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
pds    NodePort   <IP ADDRESS>    <none>        8080:<NODE PORT>/TCP   147m

$ minikube ip
<MINIKUBE IP>
$ curl <MINIKUBE IP>:<NODE PORT>
# OR
$ kubectl port-forward svc/apicurio -n apicurio 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
$curl 127.0.0.1:8080
```

2. Try to create artifact to Apicurio

```bash
$curl -X POST -H "Content-type: application/json; artifactType=AVRO" -H "X-Registry-ArtifactId: share-price" --data '{"type":"record","name":"price","namespace":"com.example","fields":[{"name":"symbol","type":"string"},{"name":"price","type":"string"}]}' http://localhost:8080/api/artifacts
# You will see document itself as output
> {"name":"price","createdBy":"","createdOn":1668145281660,"modifiedBy":"","modifiedOn":1668145281660,"id":"share-price","version":1,"type":"AVRO","globalId":1,"state":"ENABLED"}%
```

# messaging-topology-operator-1.7.1

```
curl -LO https://github.com/rabbitmq/messaging-topology-operator/releases/download/v1.7.1/messaging-topology-operator-with-certmanager.yaml
kustomize create --autodetect
```

This manifest must be edited to remove the Namespace object, we have attempted to use kustomize this in the past but kustomize ends up merging the entire stream, and permanently deletes the Namespace whenever from the final stream. 


TODO figure out a way to automate this that isn't overly complex

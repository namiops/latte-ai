# cluster-operator 2.2.0

```
curl -LO https://github.com/rabbitmq/cluster-operator/releases/download/v2.2.0/cluster-operator.yml
kustomize create --autodetect
```

A patch to the `rabbitmq-system` Namespace object has been added to enable istio injection on this namespace

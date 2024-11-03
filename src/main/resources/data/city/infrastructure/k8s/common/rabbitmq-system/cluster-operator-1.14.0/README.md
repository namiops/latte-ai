# cluster-operator 1.14.0

```
curl -LOhttps://github.com/rabbitmq/cluster-operator/releases/download/v1.14.0/cluster-operator.yml
kustomize create --autodetect
```
A patch to the `rabbitmq-system` Namespace object has been added to enable istio injection on this namespace

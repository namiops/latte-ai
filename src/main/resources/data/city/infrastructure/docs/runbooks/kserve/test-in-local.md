# Test InferenceEngine in a Local Cluster

## Pre-requisites
1. Run minikube's network tunnel
```
$ minikube tunnel
```
2. Open another terminal and get minikube exposed IP address of ingress
```
export CLUSTER_DOMAIN="woven-city.local"
export INGRESS_IP=`kubectl get svc -n city-ingress  --output jsonpath='{.items[0].spec.clusterIP}'`
```

## Sklearn model
The model is deployed by `infrastructure/k8s/common/kserve-sample/inference-service/sklearn-iris-0.1.0`. 

### To test
1. Check healthy of the service.
```
$ curl -H "Host: sklearn-iris-predictor-default.kserve-test.$CLUSTER_DOMAIN" http://$INGRESS_IP/v1/models/mnist
{"name": "mnist", "ready": true}
```
2. Run model inference with a sample input.
```
curl -X POST -H "Host: sklearn-iris-predictor-default.kserve-test.$CLUSTER_DOMAIN" http://$INGRESS_IP/v1/models/sklearn-iris:predict -d @./sample-input/iris.json
```

## Pytorch Model
The model is deployed by `cityos/infrastructure/k8s/common/kserve-sample/inference-service/pytorch-mnist-0.1.0` file. 

### To test
1. Check healthy of the service.
```
$ curl -H "Host: pytorch-mnist-predictor-default.kserve-test.$CLUSTER_DOMAIN" http://$INGRESS_IP/v1/models/mnist
{"name": "mnist", "ready": true}
```
2. Run model inference with a sample input.
```
curl -X POST -H "Host: pytorch-mnist-predictor-default.kserve-test.$CLUSTER_DOMAIN" http://$INGRESS_IP/v1/models/mnist:predict -d @./sample-input/mnist.json
{"predictions": [[2]]}
```

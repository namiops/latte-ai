# Test InferenceEngine in a Local Cluster

## Pre-requisites
1. Run minikube's network tunnel
```
$ minikube tunnel
```
2. Open another terminal and get minikube exposed IP address of ingress
```
export CLUSTER_DOMAIN="agora-lab.woven-planet.tech"
export INGRESS_IP=`kubectl get svc -n city-ingress  --output jsonpath='{.items[0].spec.clusterIP}'`
```

## Sklearn model
The model is deployed by `inferenceengine-sklearn.yaml` file. 

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
The model is deployed by `inferenceengine-pytorch-mnist.yaml` file. 

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

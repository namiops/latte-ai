# Local Kafka Monitor

## Installation:

After [the local kafka cluster](../kafka) is installed, run the following commands:

```sh
kubectl apply -k .

minikube service -n kafka-monitor kafka-monitor
```

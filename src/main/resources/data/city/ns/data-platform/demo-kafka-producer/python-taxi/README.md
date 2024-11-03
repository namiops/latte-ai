# sample python taxi data producer

## run locally

Download the dummy data:
```shell
wget https://toyotaglobal.enterprise.slack.com/files/U056RFWNPS9/F05QH0RAECX/taxi.zip
unzip taxi.zip
```

```shell
minikube start --cpus=max --memory=max

kubectl apply -k ../../../../infrastructure/k8s/local/cert-manager # repeat until all resources are created
kubectl apply -k ../../../../infrastructure/k8s/local/kafka        # repeat until all resources are created
kubectl apply -f apicurio-local.yaml

```

Then register the schema (`infrastructure/k8s/common/data-platform/demo-kafka-producer/0.0.1/taxi-example-schema.avsc`) with the following setting manually using Apicurio UI
- group: null
- name: `data-platform-demo.taxi-example-value`

Start skaffold!

```shell
skaffold dev
```

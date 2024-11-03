# Vault-on-Demand Helm Chart

This directory contains the helm chart for a small demo on how to integrate with Hashicorp Vault via the API.
This chart assumes a **local, NOT remote, minikube deployment of Vault**

For more information on how to use Vault and set it up in your local minikube for testing, please refer to the 
**[Administrator Example README](/infrastructure/k8s/local/vault-example/administrator/README.md)**

For more information on the example and how it works please refer to the **[Vault on Demand README](/infrastructure/k8s/local/vault-example/on-demand/README.md)**

For the source code, please refer to the **[Source Repository](/ns/demo/vault-on-demand/README.md)**

## How to use the Chart
```shell
# from local directory, use --create-namespace to have Helm create it for you
helm install vault-on-demand -n on-demand [--create-namespace] . 
```
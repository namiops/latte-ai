# Setting Up With Kubernetes

!!! Warning
    **This method is deprecated**, and only supported in legacy environments (`dev`, `dev2`).

Perform the following steps to set up your Kubernetes Authentication Engine

## 1. Use the correct Kubernetes context

You can do this with either:

* `kubectl config use-context <environment>`
* `kubectx <environment>`

## 2. Extract the Kubernetes Host

Run the following:

```shell
KUBE_HOST=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.server}')
```

## 3. Extract the Kubernetes CA Certificate

```shell
KUBE_CA_CERT=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.certificate-authority-data}' | base64 --decode)
```

## 4. Extract the Kubernetes CA Issuer URL

You need to refer to the [Infra Team's Document](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/README.md) on our EKS Clusters and find the **EKS OpenID Connect provider URL**

Once you have it set the following:

```shell
ISSUER=<ISSUER_URL>
```

## 5. Set up the Kubernetes Engine

Run the following with the information from Steps 2-4:

!!! Note
  The following instructions are using a placeholder `<PATH>` for simplifying the instructions.
  `PATH` is a string you set. It is **Recommended** that, to help you understand what Auth Engine is tied to what Agora Cluster, you replace `<PATH>` with a pattern of `kubernetes-<cluster>`.
  For example, `kubernetes-dev` signals this is the Kubernetes Engine for Dev

```shell
$ vault auth enable -path=<PATH> kubernetes 

$ vault write auth/<PATH>/config \
kubernetes_host="${KUBE_HOST}" \
kubernetes_ca_cert="${KUBE_CA_CERT}" \
issuer="${ISSUER}" \
disable_iss_validation=true
```

This will set up an Engine to allow Vault to authenticate for your namespace to Agora

## 6. Set up the Kubernetes Auth Engine Role

Run the following to associate a role for your Kubernetes Auth Engine:

```shell
$ vault write auth/<PATH>/role/my_role \
bound_service_account_names="my-service-account" \
bound_service_account_namespaces="my-k8s-namespace" \
policies="my_policy"
Success! Data written to: auth/<PATH>/role/my_role
```

# Getting Started
Here we will try to run the actual storage solution and the API, so get excited.

## Prerequisites
There are some things you will need to get everything working.

* Minikube: Refer to the [getting started](https://minikube.sigs.k8s.io/docs/start/) page.
* Helm: We will use a helm chart to install the Secure KVS. You can check how to install Helm in the [official documentation](https://helm.sh/docs/intro/install/).
* Deployment files: You can find them in the source of this project [here](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/securekvs-101).
* Access to Artifactory. Refer to [Woven IT Documentation](https://docs.woven-planet.tech/engineering_software/artifactory/support/Docker-Registry/) to learn how to set it up.

## Installing
### 1. Creating a Namespace
First, let's make a namespace where we will put our Secure KVS and the API. To do so, simply run

```shell
$ kubectl create namespace securekvs-101
namespace/securekvs-101 created
```

### 2. Installing Secure KVS
The Agora storage team has made a nice helm chart for us to use to help us install an instance of Secure KVS. That's what we will use today.

To install Secure KVS, run:

```shell
$ helm install test-secure-kvs https://artifactory-ha.tri-ad.tech:443/artifactory/helm/wcm-cityos/secure-kvs/secure-kvs-0.6.2%2B249255a1.tgz -n securekvs-101 --set "steelcouch.originDeterminationType=\{static: test\}"
NAME: test-secure-kvs
LAST DEPLOYED: Wed Oct  5 09:59:40 2022
NAMESPACE: securekvs-101
STATUS: deployed
REVISION: 1
NOTES:
Agora KVS is starting. Check the status of the Pods using:

  kubectl get pods --namespace securekvs-101 -l "app=agora-kvs-test-secure-kvs,release=test-secure-kvs"

You can get the CouchDB admin user's user name and password from the secret "db-agora-kvs-test-secure-kvs".

  Admin's user name
  kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminUsername}' | base64 -d

  Admin's user password
  kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminPassword}' | base64 -d
```

We use the `--set` command to override the `originDeterminationType`, which we do so Secure KVS can run locally. For more on it, refer to [How Steelcouch Works](https://developer.woven-city.toyota/docs/default/component/steelcouch/MANUAL/).

You can also see some helpful commands that will come in handy when we want to see the data.

### 3. Deploying the API
Finally, we need to deploy the API. To do that, navigate to where you have the downloaded source code and run:

```shell
$ kubectl apply -k kubernetes
service/securekvs-101 created
deployment.apps/securekvs-101 created
```

This will create an API and a Service. Let's make sure that everything is running correctly. We can do that by checking on our pods.

```shell
$ kubectl get pods -n securekvs-101
NAME                                                    READY   STATUS    RESTARTS   AGE
db-agora-kvs-test-secure-kvs-0                          2/2     Running   0          3h15m
db-agora-kvs-test-secure-kvs-1                          2/2     Running   0          3h15m
db-agora-kvs-test-secure-kvs-2                          2/2     Running   0          3h15m
securekvs-101-67c6594954-gvqbq                          1/1     Running   0          3h14m
steelcouch-agora-kvs-test-secure-kvs-59fd774cb4-t75k2   1/1     Running   0          3h15m
```

As you can see all our pods are running.

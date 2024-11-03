# Getting Start with Testkube

## Table of contents

- [Getting Started with Testkube](#getting-started-with-testkube)
  - [Preparation](#Prerequisite)
  - [Bring up testkube](#Bring-up-testkube)
  
## 1. Prerequisite
Before you get on with testkube local setup,  please ensure 
- You have cloned city code repo to current machine

  Since you are here,  assume this step is completed :)
- You have minikube and docker setup in your current machine

  Please follow the minikube tutorial on developer portal [here](https://developer.woven-city.toyota/docs/default/Component/minikube-tutorial) 

- Your VPN is connected

  Please run a quick test command to testify your VPN connection
  ```sh
  docker pull docker.artifactory-ha.tri-ad.tech/alpine:3.19.1 
  ```
  Succeed in executing above command means you are ready to go next.  

## 2. Bring up local testkube
### 1). Preconfig serviceEntry for accessing SUT(Service Under Test)
You maybe want this testkube local cluster talk to Agora environment,  which require serviceEntry configuration.  Feel free to modify the default value in  
```../local/config/serviceentry-testkube-service-entry.yaml``` to your own perference, replace ```<your-host>``` with your own. 
```yaml
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: testkube-service-entry
  namespace: testkube
spec:
  hosts:
    - <your-host>.agora-lab.w3n.io
    - <your-host>.agora-dev.w3n.io
    - artifactory-ha.tri-ad.tech
  location: MESH_EXTERNAL
  ports:
    - name: https
      number: 443
      protocol: HTTPS
  resolution: DNS
  exportTo:
    - testkube
```
If you see

`namespaces "istio-system" is forbidden: User "aad:your.name@woven-planet.global" cannot patch resource "namespaces" in...`

This means that you are using K8S on cloud, instead of the local one. So please check you kubeconfig by `cat  ~/.kube/config`, if you see error.

and see what is it pointing to local context.

You need to run `kubectx <your-local-kube-context>`


### 2). Startup testkube cluster in local

Under the code repo directory that has this README.MD. issue below command 

```sh
../local/bin/bootstrap
```

It will take about 2 minutes to complete the whole setup process, if you see `Congratulations!..` in the end,  then you are ready to go next step. 

> **Notice1：** The above bootstrap script has been tested with minikube `v1.33.0` on `Darwin 14.3 (arm64)`, the base image: `gcr.io/k8s-minikube/kicbase:v0.0.43`, Docker Engine `v26.1.1`, Docker Desktop version: `4.30.0 (149282)`. 

> **Notice2：** if bootstrap run end with failure `Some CRDs still not be created yet`,  please rerun bootstrap command, it should pass through in 2nd run. 

> **Notice3：** if bootstrap stuck, or failed with error like `exceeded its progress deadline`,  then check logs of the error pod.  if you see message like `exec format error`,that indiciate an image arch incompatible issue on your laptop, can reach us on slack channel #wcm-agora-testkube.

### 3). Do port-forward config
You need to do port forward to make the testkube service accessible on your current machine. 

Assume you are on your Mac laptop, 

- Do port forward for testkube-dashboard service
```sh
# In terminal 1
kubectl -n testkube port-forward svc/testkube-dashboard 8080:8080
```

- Do port forward for testkube-api-server
```sh
# In terminal 2
kubectl -n testkube port-forward svc/testkube-api-server 8088:8088
```

### 4). Access testkube dashboard
Now you are ready to access testkube dashboard now !

Open browser, input the url `http://localhost:8080` , this will land you to the testkube dashboard page. 

The 1st time you open the dashboard,  you will be prompted with a popup dialog asking for testkube server API endpoint, please input the url  `http://localhost:8088/results/v1`, then click OK to close the popup. 


Arriving at here,  you should be able to naviate testkube function tab,  and ready to add your tests.  

Congratulations! 

## 3. Set up your own test case   

Now you maybe are owning a service that need to be tested for a quality confidence of merging or releasing. then here testkube will help you achieve it.  

### 1) Develop test and run locally
In some cases,  you maybe want to run test **locally** against service either in local or Agora cluster, typical test is end2end test and load test, then we recommend you setup test with testkube official executor,  please refer to guide here [Setup local tests](basic_run_local_test.md) 

### 2) Develop test and run inside Agora cluster
In most cases,  the test has to be run in Agora cluster against particular service in same cluster, eg, lab2 or dev2, then we need to make the test injected then able to communicate within service mesh. here, please refer to guide. [Setup injected tests](basic_run_injected_test.md) 

## Reference

For more details on the dashboard, refer to [Testkube dashboard documentation](https://docs.testkube.io/articles/testkube-dashboard/).

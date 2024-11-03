# Minikube Quickstart Tutorial


## Overview
<!---
Brief introduction or summary of the document – what are you trying to do and what is it used for? This should be short (no more than 1-2 paragraphs) and give a general overview of the topic. No need to expand on specific concepts – use inline links instead, or redirect readers to more detailed documents in the “More information” section at the end.
--->

This quickstart tutorial shows you how to deploy a very simple service to minikube for sending and receiving requests. It aims to present one of the many "building blocks" that Agora uses to offer its services to other teams.


## What you'll learn
<!---
List of steps that make up the tutorial. Ideally they should be linkable to the relevant sections if available. Use the imperative voice and try to be a bit descriptive – what is the purpose of performing each step?
--->

From this tutorial, you will learn how to deploy your first Kubernetes service and send and receive requests between your applications and your services. You will learn how to:

* [1. Create namespaces to organize your resources](#1-create-namespaces-to-organize-your-resources)
* [2. Group your applications using a service](#2-group-your-applications-using-a-service)
* [3. Set up a deployment to manage your application](#3-set-up-a-deployment-to-manage-your-application)
* [4. Reach your application in a minikube](#4-reach-your-application-in-a-minikube)
* [5. Verify the state of your application and its response](#verify-the-response)


## What you’ll need
<!---
List of pre-requisites or implementations you need to have completed before starting this tutorial. Link to previous tutorials/documents if available.
--->

Before starting this tutorial, you should already have the following tools and files in place. If you haven't done so, use the links below to get started on the respective pre-requisites:

* [**minikube**](https://minikube.sigs.k8s.io/docs/start/)
    * Requires a [Docker](https://minikube.sigs.k8s.io/docs/drivers/docker/) backing driver
* **kubectl** (See "3. Interact with your cluster" of the [minikube installation](https://minikube.sigs.k8s.io/docs/start/)
* [**source directory**](https://github.com/wp-wcm/city/tree/main/ns/tutorial/minikube-101) containing the following YAML files:
    * a namespace definition (`namespace.yaml`),
    * a service definition (`service.yaml`),
    * a deployment definition (`deployment.yaml`).


## Steps

### 1. Create namespaces to organize your resources
<!---
Step titles should match the “What You’ll Learn” section above. You can split a step into further sub-steps if necessary.
The contents of the steps should include example inputs and outputs to help users check if they done things correctly and got the expected outcomes. You can also include points to take note of while performing the step.
--->

First, we need to create a [_namespace_](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) to organize resources within a Kubernetes cluster. Agora will use namespaces to separate resources by team.

From the working directory, run the `kubectl create` specifying the namespace file:

```shell
kubectl create -f namespace.yaml
```

!!! Tip
    Alternatively, you can also use `kubectl apply -f namespace.yaml` to run the same file. Both commands perform the same function, but `create` is an _imperative command_ that can be used for a clean minikube system, whereas `apply` presumes existing resources and may lead to potential issues if not used carefully.

#### Verify your namespace

To verify the namespace we created above, run the following command:

```shell
kubectl get namespaces
```

You should see the output below:

```shell
NAME              STATUS   AGE
default           Active   2d3h
kube-node-lease   Active   2d3h
kube-public       Active   2d3h
kube-system       Active   2d3h
landing-page      Active   5s
```

!!! Note
    You might notice some namespaces already in your minikube environment. These are default namespaces for the resources minikube generates for you when you first start it up. You don't need to worry about these for now.


### 2. Group your applications using a service

Next, we need a [_service_](https://kubernetes.io/docs/concepts/services-networking/service/) to group [pods](https://kubernetes.io/docs/concepts/workloads/pods/) and host them under a single DNS name. This enables the pods to send and receive traffic via exposed endpoints.

To deploy your service, run the command:

```shell
kubectl create -f service.yaml
```

You should get the following output:

```
service/landing-page created
```

#### Verify your service

We can verify the service in a few ways. 

##### a. Using a namespace

One way is using the namespace we created above. When you run the command:

```shell
kubectl get all -n landing-page
```

The expected output should be something like this:

```
NAME                   TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
service/landing-page   NodePort   10.104.233.183   <none>        80:31501/TCP   XXXs
```

##### b. Using `describe`

We can also use the `describe` kubectl command:

```shell
kubectl describe service landing-page -n landing-page
```

When you run the above, you should get something similar to the following:

```
Name:                     landing-page
Namespace:                landing-page
Labels:                   <none>
Annotations:              <none>
Selector:                 app=landing-page
Type:                     NodePort
IP Family Policy:         SingleStack
IP Families:              IPv4
IP:                       10.104.233.183
IPs:                      10.104.233.183
Port:                     http  80/TCP
TargetPort:               80/TCP
NodePort:                 http  31501/TCP
Endpoints:                <none>
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

A few points to note here:

* As you can see, we don't have any `Endpoints` yet, because we don't have any currently running applications that we want exposed from the cluster. In the next step, we will deploy our application, which will generate an endpoint entry the next time we describe the service.
* The `Type` of the service is `NodePort`, which is a setting to expose the IP of each node at a static port on the node. This is a good choice to use for minikube.
* Later on in Kubernetes, you will come across other types of services such as `LoadBalancer`, which leverages a cloud provider's load balancer to help assign and expose ports. You might see this used often in Agora.


### 3. Set up a deployment to manage your application

Now we need a workload resource called a [_deployment_](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) to push our applications to a cloud environment and manage them there. We will be deploying two main components:
* A [ReplicaSet](https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/): a template of your manifest to be deployed. The ReplicaSet tells Kubernetes what application, how many copies, and any other configs you want to run with it. As its name suggests, the ReplicaSet also tells Kubernetes to make new copies of pods in cases of failure.
* The application itself in a pod (or pods in cases of multiple copies)

As in the previous steps, run `kubectl create` to set up your deployment:

```shell
kubectl create -f deployment.yaml
```

This should produce the following:

```
deployment.apps/landing-page created
```

#### Verify your deployment

To check if the deployment worked, run the commands:

```shell
kubectl get all -n landing-page
```

You should see something like this:

```
NAME                              READY   STATUS    RESTARTS   AGE
pod/landing-page-6867cb6d-k6rmt   1/1     Running   0          68s

NAME                   TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
service/landing-page   NodePort   10.104.233.183   <none>        80:31501/TCP   36m

NAME                           READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/landing-page   1/1     1            1           68s

NAME                                    DESIRED   CURRENT   READY   AGE
replicaset.apps/landing-page-6867cb6d   1         1         1       68s
```

!!! Tip
    The naming format displayed in the output is resource/<resource_name>.

As you can see from the above output, in addition to your service, you now have a deployment, a ReplicaSet, and a pod running in the cluster.

!!! Tip
    Your resources may be displaying a ready status of `0/1` due to Kubernetes processing time lags. If this happens, wait a few seconds and try running the command again.

#### Check your pod

Let's check on our pod and see what it's doing. We can do that in a few ways.

First, describe the pod to identify any issues:

```shell
kubectl describe pod <resource_name> -n landing-page
```

You should see something akin to the following at the end of the output:

```
Events:
  Type    Reason     Age    From               Message
  ----    ------     ----   ----               -------
  Normal  Scheduled  4m12s  default-scheduler  Successfully assigned landing-page/landing-page-6867cb6d-k6rmt to minikube
  Normal  Pulled     4m11s  kubelet            Container image "docker.artifactory-ha.tri-ad.tech:443/wcm-cityos/landing-page:main-96735d63-990" already present on machine
  Normal  Created    4m11s  kubelet            Created container landing-page
  Normal  Started    4m11s  kubelet            Started container landing-page
```

Here, we can see that Kubernetes pulled our image as per the deployment manifest, created a container, and then started it up. 

We can also check the logs to see if the application is up and running:

```shell
kubectl logs <resource_name> -n landing-page
```

You should see something like:

```
. . .
172.17.0.1 - - [07/Jul/2022:22:22:13 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:22:23 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:22:23 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:22:33 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
```


### 4. Reach your application in a minikube

At this stage, we now have all the pieces for our application, and we can finally reach the service and talk to it by port forwarding.

#### Port forwarding

From the above outputs, you can see that the service and pods are both listening on port 80. We can forward to this port if we want to talk to our application. As an example, we will use 8888 as our start port, but you can choose whatever port you wish.

```shell
kubectl port-forward service/landing-page -n landing-page 8888:80
```

You should get the following response:

```
Forwarding from 127.0.0.1:8888 -> 80
Forwarding from [::1]:8888 -> 80
```

Now we are hooked to the service and can send traffic. Since our application is a simple Nginx server that is hosting a simple HTTP page, we can just hit it with a curl command:

```shell
curl http://localhost:8888
```

This should give the output below:

```
<!doctype html>
  <html lang="en">
  <head>
    <meta charset="utf-8">
    <title>CityOS</title>
  </head>
  <body>
    <h2>Welcome to CityOS</h2>
  </body>
</html>
```

Congrats! You now have a response from the application! 

#### Verify the response

You can verify the status and responses to your application by checking the logs of the pod:

```shell
kubectl logs <resource_name> -n landing-page
```

You should see:

```
. . .
172.17.0.1 - - [07/Jul/2022:22:58:33 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:58:43 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
127.0.0.1 - - [07/Jul/2022:22:58:48 +0000] "GET / HTTP/1.1" 200 165 "-" "curl/7.79.1" "-"
172.17.0.1 - - [07/Jul/2022:22:58:53 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
. . .
```

As you can see from the above, you got a hit from localhost (127.0.0.1) via `curl` and a 200 response.


## Conclusion
<!---
Congratulate the reader for finishing the tutorial and list next steps or related links if available. You can also use this section to link to more in-depth documents to expand on specific points in the tutorial, or overview conceptual docs if you feel like more explanation is needed to help the user understand what they're implementing.
--->

Now you have learned how to deploy a simple service and send/receive requests using minikube! To learn more about using XYZABC in Kubernetes, see:
* [Link]() 
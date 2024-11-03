# Step 2: Deploying a Service

## What is a Service

In Kubernetes, a [_service_](https://kubernetes.io/docs/concepts/services-networking/service/)
is an abstraction that allows you to group multiple pods and have them hosted
as under a single DNS name. A [_pod_](https://kubernetes.io/docs/concepts/workloads/pods/)
(as in a pod of whales or pea pod) is a group of one or more containers, with
shared storage and network resources, and a specification for how to run the
containers. Each pod exposes an endpoint that is managed by the service, which
then can send traffic to each pod as you desire.

![Service](./assets/MinikubeService.png)

A service is a good way to set up your application in a variety of ways that
can solve some issues for you such as

* Figuring out how your 'front end' components can find your 'back end'
  components without needing to know the exact IPs of your pods, because
  services provide DNS names.
* How to load balance your application. A service can be configured to work
  like a load balancer, and host as many instances as you wish, all under the
  same DNS name.

## How to deploy the Service

Continuing from Step 1, perform the following command:

```shell
$ kubectl create -f service.yaml
service/landing-page created
```

## Verify the Service

We can check the service in a few ways, in the previous lesson we were able to
make a namespace, so lets use it!

```shell
kubectl get all -n landing-page
NAME                   TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
service/landing-page   NodePort   10.104.233.183   <none>        80:31501/TCP   XXXs
```

You should see some output similar to the above.

Lets dig a little deeper here, and use another kubectl command: **describe**

```shell
kubectl describe service landing-page -n landing-page
```

You should get something similar to below:

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

There are a few things here to note here

* We have no `Endpoints` currently. That's okay! That's because we don't have
  any applications running yet that we want exposed from the cluster. In the
  next step we'll see that when we deploy our application, there will be an
  endpoint entry here next time we describe the service.
* The `Type` of Service is `NodePort` which is a way to set the Service to
  expose each Node's IP at a static port on the Node. In minikube this is
  likely the type you want to use for now

!!! Note
    You will find as you go along in your Kubernetes journey that, there's a
    few other types of Service, such as `LoadBalancer` which leverages a Cloud
    provider's load balancer to help with assigning ports and exposing them.
    You might see this used in Agora mostly, but you don't need to worry about
    this now, but it helps to know about it!

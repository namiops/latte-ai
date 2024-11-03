# Step 3: Deploying a Deployment

## What is a Deployment

In Kubernetes, a [_deployment_](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)
is a _Workload Resource_, which helps manage your
application. A deployment is one of many types of Workload Resources, but a
deployment is a good starting point to learn how to push your applications
to a cloud environment.

![Deployment](./assets/MinikubeDeployment.png)

In a deployment two main components are deployed

* A [_replica set_](https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/):
  * This is essentially a template of your manifest that you want deployed;
    this is the part that lets Kubernetes know "what application and how many
    copies?", along with any additional config you wish to run with your
    application
  * What happens is, should a pod go down or break down, the replica set helps
    by telling Kubernetes that it needs to make a new copy to meet the number
    of pods you requested run: if you ask for 3 copies and 1 copy fails, the
    replica set tells Kubernetes to make a new copy to replace the old one.
* The application itself in a Pod, or Pods if you ask for multiple copies

## How to deploy the Deployment

Same as in the previous steps we will run kubectl create

```shell
$ kubectl create -f deployment.yaml
deployment.apps/landing-page created
```

## Verify the Deployment is up

Let's check to see if the deployment worked

```shell
kubectl get all -n landing-page
```

You should get something akin to the following

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
    If one of your resources is showing `0/1` it might just be due to having
    caught Kubernetes still processing things. If you try again after a couple
    of seconds it should be okay

We can see now that, in addition to our service, we now have a deployment,
replicaset, and pod running in the cluster.

## Checking on our Application

Let's check on our pod and see what its doing. We can do that in a few ways.

First we'll describe the pod and check to see if there are any issues with it

```shell
kubectl describe pod <name of the pod> -n landing-page
```

You should see something akin to the following at the bottom of the output.
There's a lot here, but you don't need to worry about that yet for this lesson

```
Events:
  Type    Reason     Age    From               Message
  ----    ------     ----   ----               -------
  Normal  Scheduled  4m12s  default-scheduler  Successfully assigned landing-page/landing-page-6867cb6d-k6rmt to minikube
  Normal  Pulled     4m11s  kubelet            Container image "docker.artifactory-ha.tri-ad.tech:443/wcm-cityos/landing-page:main-96735d63-990" already present on machine
  Normal  Created    4m11s  kubelet            Created container landing-page
  Normal  Started    4m11s  kubelet            Started container landing-page
```

We can see here that Kubernetes pulled our image per the deployment manifest,
created a container, and then started it up for us. We can also check the logs
to see if the application is up and running

```shell
kubectl logs <name of the pod> -n landing-page
```

```
. . .
172.17.0.1 - - [07/Jul/2022:22:22:13 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:22:23 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:22:23 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:22:33 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
```

We can see that there are some log Messages inside the application

!!! Note
    **What is calling my application?**

    The messages above are coming from Kubernetes itself. Kubernetes has a
    feature called "Liveness and Readiness Probes" that allow it to check on
    your service to see if it is listening for traffic. If the pod isn't
    responding, Kubernetes can then take some actions to make your service
    alive and responsive again, such as restarting the pod, or terminating the
    pod and replacing it with a fresh one.

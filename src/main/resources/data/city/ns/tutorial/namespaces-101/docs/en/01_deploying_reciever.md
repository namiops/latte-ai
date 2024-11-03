# Step 1: Deploying a Service in Our Minikube

The first thing to do is to deploy a service that listens to requests,
processes them, and then respond appropriately. In this step we'll deploy our
service, and then send some traffic from the outside to the service.

## Deploying the Receiver Service

The service we are using is provided for you; the only thing you need to do is
deploy the files to your minikube environment. We'll deploy the following:

* The namespace for the receiver to live in
* The service for the application
* The deployment of the application itself

First, deploy the namespace

```
cd receiver/kubernetes
kubectl apply -f _namespace.yaml
```

```
namespace/receiver created
```

Next, deploy the service

```
kubectl apply -f service.yaml
```

```
service/receiver-service created
```

And lastly, the deployment of the application itself
```
kubectl apply -f deployment.yaml
```

```
deployment.apps/receiver-deployment created
```

We can verify with `kubectl`

```
kubectl get all -n receiver
```

```
NAME                                       READY   STATUS    RESTARTS   AGE
pod/receiver-deployment-57998d9c6b-ttf2j   1/1     Running   0          28s

NAME                       TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)    AGE
service/receiver-service   ClusterIP   None         <none>        8080/TCP   47s

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/receiver-deployment   1/1     1            1           28s

NAME                                             DESIRED   CURRENT   READY   AGE
replicaset.apps/receiver-deployment-57998d9c6b   1         1         1       28s
```

Now lets check our application and verify that things work via `port-forward`
and `curl`

First lets use `port-forward`.

```
kubectl port-forward receiver-deployment-xxx -n receiver 8080:8080
```

This command will **not** return, and that's okay, because it's keeping the tunnel open.

```
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

Open a new terminal and run the following. You can replace `<name>` with a string
```
curl http://localhost:8080/hello/<name>
```

```
curl http://localhost:8080/hello/yshtola
{"message":"Hello there yshtola!"}
```

We can see we got a response back which means the receiver is listening.

# Step 4: Reaching the application

At this stage we have all our pieces for our application, and we can reach it.
This next step to finally reach the service and talk to it.

## Talking to Services and Pods in Minikube

There are a few methods you can try but for the sake of this tutorial we'll
focus on one: port-forward. Port-forward allows you to make a tunnel external
to the cluster that can connect to inside your cluster.

So let's try it, we'll start with looking at our setup, per our configuration
files, we have the service listening on port 80, and the pods are also
listening on 80. This means that if we want to talk to our application, we can
port-forward to the service at port 80. We'll use 8888 on our side, but you can
use whatever port you wish

```shell
$ kubectl port-forward service/landing-page -n landing-page 8888:80
Forwarding from 127.0.0.1:8888 -> 80
Forwarding from [::1]:8888 -> 80
```

So now we are hooked to our Service. Now we can send traffic. Our application
is a simple Nginx server that is hosting a simple HTTP page. So let’s just hit
it with a curl command

```shell
$ curl http://localhost:8888

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

Congrats! We now have a response from the application! We can verify this if we
check the logs of the pod.

```shell
$ kubectl logs <name of the pod> -n landing-page
. . .
172.17.0.1 - - [07/Jul/2022:22:58:33 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
172.17.0.1 - - [07/Jul/2022:22:58:43 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
127.0.0.1 - - [07/Jul/2022:22:58:48 +0000] "GET / HTTP/1.1" 200 165 "-" "curl/7.79.1" "-"
172.17.0.1 - - [07/Jul/2022:22:58:53 +0000] "GET / HTTP/1.1" 200 165 "-" "kube-probe/1.24" "-"
. . .
```

We can see we got a hit from localhost (127.0.0.1) via `curl` and a 200
response.

## Congratulations

You've successfully finished your first Kubernetes service and are able to send
it requests and get responses back from it. In this Tutorial you learned how to:

* How to organize your applications with use of Namespaces
* How to set your applications under a single DNS with a Service
* How to set up your application with a Deployment
* How to reach your application in a minikube
* How to find out about your Application and its state via basic kubectl commands

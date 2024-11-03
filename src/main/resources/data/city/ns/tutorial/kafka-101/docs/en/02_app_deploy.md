# Step 2: Deploying Our Applications

Next, now that our Kafka is set up and running in a healthy state, we can
deploy our applications

## Deploying the Producer

First we'll make sure we're back to the **project root**

```shell
cd cityos/ns/tutorial/kafka-101/
```

Next we'll deploy the producer. We'll run the following `kubectl` after moving
to `producer/kubernetes`

```shell
$ cd producer/kubernetes
$ kubectl apply -f _namespace.yaml
namespace/producer created

$ kubectl apply -f deployment.yaml
deployment.apps/producer created
```

We can check to see if the pod is running by running more `kubectl`

```shell
$ kubectl get po -n producer
NAME                        READY   STATUS    RESTARTS   AGE
producer-5b49b66cf7-zbzcx   1/1     Running   0          35s

# You need to change <pod_name> with the name of the pod in your minikube (in this example, it is producer-5b49b66cf7-zbzcx)
$ kubectl logs <pod_name> -n producer

.
.
[GIN-debug] GET    /health                   --> github.com/wp-wcm/city/ns/tutorial/kafka-101/producer/internal/http.NewGin.func1 (4 handlers)
[GIN-debug] POST   /butter                   --> github.com/wp-wcm/city/ns/tutorial/kafka-101/producer/internal/http.NewGin.func2 (4 handlers)
[MAIN] Server running[GIN] 2022/10/17 - 06:01:31 | 200 |      13.762µs |      172.17.0.1 | GET      "/health"
[GIN] 2022/10/17 - 06:01:31 | 200 |        33.8µs |      172.17.0.1 | GET      "/health"
[GIN] 2022/10/17 - 06:01:39 | 200 |      56.734µs |      172.17.0.1 | GET      "/health"
.
.
```

If you're getting log messages like this and the state of the pod is `Running`
then the deployment was successful.

We can even send a message: let's try and send a post. First we need to expose
the service from the minikube. We'll use `port-forward`:

```shell
$ kubectl port-forward <pod_name> -n producer 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

Now let's send a `curl` to get a POST request to the pod

```shell
$ curl -X POST http://localhost:8080/butter
Butter Sent
```

!!! Note

    You might get an error when you send this message, akin to the following:

    ```
    Error sending butter: [5] Leader Not Available: the cluster is in the middle of a leadership election and there is currently no leader for this partition and hence it is unavailable for writes
    ```

    This is okay; Kafka is just performing a reconsiliation and the cluster
    will be ready in a moment, you can just re-send the same `curl` request and
    it should be okay

## Deploying the Consumer

Next we'll deploy our consumer. The process is very similar.

First we'll make sure we're back to the **project root**

```shell
cd cityos/ns/tutorial/kafka-101/
```

Next we'll deploy the consumer. We'll run the following `kubectl` after moving
to `consumer/kubernetes`

```shell
$ cd consumer/kubernetes
$ kubectl apply -f _namespace.yaml
namespace/consumer created
$ kubectl apply -f deployment.yaml
deployment.apps/consumer created
```

Next we can check the pod and see if the consumer is running

```shell
$ kubectl get po -n consumer
NAME                        READY   STATUS    RESTARTS   AGE
consumer-5db4b478cf-nbw8l   1/1     Running   0          50s

# You need to change <pod_name> with the name of the pod in your minikube (in this example, it is consumer-5db4b478cf-nbw8l)
$ kubectl logs <pod_name> -n consumer

.
.
[GIN-debug] GET    /health                   --> github.com/wp-wcm/city/ns/tutorial/kafka-101/consumer/internal/http.NewGin.func1 (4 handlers)
2022/10/17 06:18:00 [Kafka] Reader started, consuming...
[MAIN] Server running[GIN] 2022/10/17 - 06:18:09 | 200 |      15.899µs |      172.17.0.1 | GET      "/health"
[GIN] 2022/10/17 - 06:18:10 | 200 |      29.593µs |      172.17.0.1 | GET      "/health"
[GIN] 2022/10/17 - 06:18:19 | 200 |     318.259µs |      172.17.0.1 | GET      "/health"
.
.
```

We can see that not only did the consumer start up correctly, but it was able
to consume the butter we sent it. Let's send another one and check the consumer again:

```shell
$ curl -X POST http://localhost:8080/butter
Butter Sent
$ curl -X POST http://localhost:8080/butter
Butter Sent

# You need to change <pod_name> with the name of the pod in your minikube
$ kubectl logs <pod_name> -n consumer

.
.
[GIN-debug] GET    /health                   --> github.com/wp-wcm/city/ns/tutorial/kafka-101/consumer/internal/http.NewGin.func1 (4 handlers)
2022/07/26 04:30:35 [Kafka] Reader started, consuming...
[MAIN] Server running[GIN] 2022/10/17 - 06:18:09 | 200 |      15.899µs |      172.17.0.1 | GET      "/health"
[GIN] 2022/10/17 - 06:18:10 | 200 |      29.593µs |      172.17.0.1 | GET      "/health"
[GIN] 2022/10/17 - 06:18:19 | 200 |     318.259µs |      172.17.0.1 | GET      "/health"
.
.
[GIN] 2022/10/17 - 06:26:19 | 200 |      21.313µs |      172.17.0.1 | GET      "/health"
2022/10/17 06:26:22 [Kafka] Message read: Topic: butter, Value: 2249276d2053656e64696e67204275747465722122
[GIN] 2022/10/17 - 06:26:29 | 200 |      56.115µs |      172.17.0.1 | GET      "/health"
.
.
```

## Congratulations

By reaching this point we have successfully finished our tutorial and learned
the following:

* A small example of using asynchronous programming
* What Kafka is and why Agora uses it
* How to run Kafka on a local minikube cluster
* How to get two services to talk to each other via Kafka

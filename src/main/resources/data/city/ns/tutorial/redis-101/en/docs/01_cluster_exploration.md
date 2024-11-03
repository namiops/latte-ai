# Cluster Exploration

Let's get a cluster up an running to try out the operator. We will start this
cluster on a local Kubernetes cluster using Minikube (or equivalent).

## Prerequisites

This tutorial assumes a number of prerequisites that are required before you
will be able to successfully run it.

1. Access to the [city](https://github.com/wp-wcm/city) monorepo so that you
can stand up a local cluster.
2. Minikube. Please refer to the [Minikube-101](https://developer.woven-city.toyota/docs/default/Component/minikube-tutorial) to learn how to set it up.
3. Kubectl. You can find instructions on how to install
[**here**](https://minikube.sigs.k8s.io/docs/start/) for Windows, Mac, and Linux
4. Finally this tutorial assumes you are working in a running Agora local
cluster.

See

```shell
infrastructure/k8s/local/README.md
```

for details on setting up a local cluster

The tutorial also presumes you're working from the **source project root**
which for our purposes is:

```shell
/ns/tutorial/redis-101/
```

## Deploy Redis

Once the cluster is completely up and running deploy the test cluster by running:

```shell
$ kubectl apply -k deploy/01_cluster_exploration/
namespace/redis-test created
service/redis-headless created
service/redis-sentinel-headless created
redisfailover.databases.spotahome.com/redisfailover created
```

We can check the status of the pods with the command

```shell
$ kubectl get pods -n redis-test
NAME                                 READY   STATUS            RESTARTS   AGE
rfr-redisfailover-0                  0/2     PodInitializing   0          13s
rfr-redisfailover-1                  0/2     PodInitializing   0          12s
rfr-redisfailover-2                  0/2     PodInitializing   0          12s
rfs-redisfailover-669c846b46-cwv2t   0/2     Init:0/2          0          12s
rfs-redisfailover-669c846b46-fmcnb   0/2     Init:0/2          0          13s
rfs-redisfailover-669c846b46-llwbq   0/2     Init:0/2          0          12s
```

Once all your pods are "Running" your redis cluster is up and running.

## Deploy a test container

Now that we have a running Redis cluster we also need a way to interact with
that cluster. To do this we will run another redis container from artifactory
interactively.

```shell
$ kubectl run -i --tty test --image=docker.artifactory-ha.tri-ad.tech/redis -n redis-test -- sh
If you don't see a command prompt, try pressing enter.

#
```

Great, we are now on the Redis container, in the same namespace as the running
cluster.

## Try out Redis-CLI

Finally lets run some commands against the Redis cluster to make sure that it's
working. The cli is already on the container we are running so just run

```shell
redis-cli -h redis-headless
redis-headless> 
```

The redis-headless is the name of the service we are using so that istio can
properly route to our Redis cluster.

Since we are now connected to the redis cluster. We can now try out some
commands:

```shell
redis-headless> PING
PONG

redis-headless> SET mykey "Hello"
"OK"
redis-headless> GET mykey
"Hello"
redis-headless> SET mykey "New Hello"
"OK"
redis-headless> GET mykey
"New Hello"
redis-headless> SET newkey "10"
"OK"
redis-headless> INCR newkey
(integer) 11
redis-headless> GET newkey
"11"
```

We can alternatively run commands directly through the redis-cli command

```shell
redis-headless:6379> exit
#
redis-cli -h redis-headless SET mykey "Hello"
"OK"
redis-cli -h redis-headless GET mykey
"Hello"
```

During either of these connection attempts you may see

```shell
# redis-cli -h redis-headless SET mykey "Hello"
(error) READONLY You can't write against a read only replica.
```

This is because, exactly as stated, you have connected to a read-only replica
and are trying to write. The redis-headless service is connecting you and
randomly assigning you a backend to connect to. In the next section we will see
how to use a Sentinel aware client to ensure we are always connecting to the
correct pod for writing (or any pod for reading).

Feel free to run whatever commands you would like to test out for the redis
cluster. If you need ideas you can check out the [Redis Commands](https://redis.io/commands/) list on redis.io.

## Teardown

When you're done with the cluster testing let's teardown the redis cluster

```shell
$ kubectl delete -k deploy/01_cluster_exploration
namespace "redis-test" deleted
service "redis-headless" deleted
service "redis-sentinel-headless" deleted
redisfailover.databases.spotahome.com "redisfailover" deleted
```

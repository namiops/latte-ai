# Step 1: Setting Up Our Kafka

The first thing to do for our tutorial project is setting up Kafka.

## How To Deploy Kafka

### Our Kafka Deployment

For our Kafka deployment, we will follow
[**Strimzi quick start**](https://strimzi.io/quickstarts/) *up to*
**Provision the Apache Kafka cluster**.

First, let's create the minikube cluster (skip if you have already created one):

```shell
minikube start --memory=4096 --driver=docker
```

if you fail to create `minikube`, configure Docker settings to make sure there
is enough memory (>= 5GB) then run the above command again.

Now let's create a `kafka` namespace

```shell
kubectl create namespace kafka
```

Then apply Strimzi installation file

```shell
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
```

!!! Note

    You might get a (bunch of) ** Error from server (AlreadyExists) **, for example

    ```
    Error from server (AlreadyExists): error when creating "https://strimzi.io/install/latest?namespace=kafka": clusterroles.rbac.authorization.k8s.io "strimzi-kafka-client" already exists
    ```

    Which is fine to ignore as it will not effect the set up for our Kafka.

Now let's create a small Apache Kafka Cluster

```shell
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka
```

And wait for Kubernetes setting up our cluster:

```shell
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka
```

This is the expected output, but if you encounter timeout, feel free to run the
above command again.

```shell
kafka.kafka.strimzi.io/my-cluster condition met
```

After the following we can check out our minikube cluster to see if everything
is in a healthy state. There are numerous resources that are deployed so this
might take a few moments to complete.

```shell
kubectl get all -n kafka
```

```
NAME                                              READY   STATUS    RESTARTS   AGE
pod/my-cluster-entity-operator-6bd798bcdd-pxh86   3/3     Running   0          4m34s
pod/my-cluster-kafka-0                            1/1     Running   0          4m59s
pod/my-cluster-zookeeper-0                        1/1     Running   0          5m44s
pod/strimzi-cluster-operator-5986447-gqwr5        1/1     Running   0          9m44s

NAME                                  TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)                               AGE
service/my-cluster-kafka-bootstrap    ClusterIP   10.104.111.5   <none>        9091/TCP,9092/TCP,9093/TCP            5m
service/my-cluster-kafka-brokers      ClusterIP   None           <none>        9090/TCP,9091/TCP,9092/TCP,9093/TCP   5m
service/my-cluster-zookeeper-client   ClusterIP   10.106.43.33   <none>        2181/TCP                              5m45s
service/my-cluster-zookeeper-nodes    ClusterIP   None           <none>        2181/TCP,2888/TCP,3888/TCP            5m45s

NAME                                         READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/my-cluster-entity-operator   1/1     1            1           4m34s
deployment.apps/strimzi-cluster-operator     1/1     1            1           9m44s

NAME                                                    DESIRED   CURRENT   READY   AGE
replicaset.apps/my-cluster-entity-operator-6bd798bcdd   1         1         1       4m34s
replicaset.apps/strimzi-cluster-operator-5986447        1         1         1       9m44s
```

Yay! We are half way done. Let's move to **Step 2: Deploying Our Applications**.
# Step 1: Setting Up Our Kafka

チュートリアルプロジェクトで最初に行うことは、Kafkaのセットアップです。

## How To Deploy Kafka

### Our Kafka Deployment

Kafkaのデプロイは、以下の手順で行います。[**Strimzi quick start**](https://strimzi.io/quickstarts/) で**Apache Kafka クラスターをプロビジョニング**します。

最初に、minikubeクラスターを作成しましょう（すでに作成済みの場合はスキップしてください）。

```shell
minikube start --memory=4096 --driver=docker
```

`minikube`の作成に失敗した場合は、Dockerの設定を行い、十分なメモリ（5GB以上）があることを確認した上で、上記コマンドを再度実行してください。

では、 `kafka` のネームスペースを作成してみましょう。

```shell
kubectl create namespace kafka
```

その後、Strimziのインストールファイルを適用します。

```shell
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
```

!!! Note

    ** Error from server (AlreadyExists) ** などのエラーが(たくさん)表示されるかもしれません。

    ```
    Error from server (AlreadyExists): error when creating "https://strimzi.io/install/latest?namespace=kafka": clusterroles.rbac.authorization.k8s.io "strimzi-kafka-client" already exists
    ```
    
    Kafkaのセットアップには影響しないので、これは無視してかまいません。

では、簡単なApache Kafka Clusterを作成してみましょう。

```shell
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka
```

そして、Kubernetesがクラスタをセットアップするのを待ちます。

```shell
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka
```

これが想定される出力ですが、もしタイムアウトが発生した場合はもう一度上記のコマンドを実行してください。

```shell
kafka.kafka.strimzi.io/my-cluster condition met
```

以下を実行することで、minikubeクラスターをチェックアウトして、すべてが正常な状態であるかどうかを確認できます。多数のリソースが展開しているため、この作業には時間がかかるかもしれません。

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

やりましたね。 これで準備は半分完了です。**ステップ2: アプリケーションのデプロイ**に進みましょう。

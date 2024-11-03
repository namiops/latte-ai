# Step 2: Deploying Our Applications

Kafkaのセットアップが完了し正常な状態で動作するようになったので、次にアプリケーションのデプロイを行います。

## Deploying the Producer

まず最初に、**プロジェクト・ルート**に戻っていることを確認します。

```shell
cd cityos/ns/tutorial/kafka-101/
```

次にプロデューサーをデプロイします。`producer/kubernetes` に移動した後、以下の `kubectl` を実行します。

```shell
$ cd producer/kubernetes
$ kubectl apply -f _namespace.yaml
namespace/producer created

$ kubectl apply -f deployment.yaml
deployment.apps/producer created
```

さらに `kubectl` を実行することで、ポッドが機能しているかどうかを確認することができます。

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

このようなログメッセージが表示され、ポッドの状態が `Running` であれば、デプロイは成功です。

メッセージの送信も可能です：試しにPOSTリクエストを送信してみましょう。まず、minikubeからサービスを公開する必要があります。 `port-forward`を使用します。

```shell
$ kubectl port-forward <pod_name> -n producer 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

では、 `curl` でポッドに POST リクエストを送信してみます。

```shell
$ curl -X POST http://localhost:8080/butter
Butter Sent
```

!!! Note

    このメッセージを送信する際に、以下のようなエラーが発生する場合があります。
    
    ```
    Error sending butter: [5] Leader Not Available: the cluster is in the middle of a leadership election and there is currently no leader for this partition and hence it is unavailable for writes
    ```

    これは問題ありません。Kafkaは再同期を実行しているだけで、クラスターはすぐに準備できます。同じ `curl` リクエストを再度送信してください。うまくいくはずです。

## Deploying the Consumer

次に、コンシューマーをデプロイします。このプロセスは非常に似ています。

まず、**プロジェクト・ルート**に戻っていることを確認します。

```shell
cd cityos/ns/tutorial/kafka-101/
```

次にコンシューマをデプロイします。 `consumer/kubernetes`に移動した後、以下の  `kubectl` を実行します。

```shell
$ cd consumer/kubernetes
$ kubectl apply -f _namespace.yaml
namespace/consumer created
$ kubectl apply -f deployment.yaml
deployment.apps/consumer created
```

次に、ポッドをチェックして、コンシューマーが実行されているかどうかを確認します。

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

コンシューマーが正しく起動しただけでなく、送ったバターを消費できたことがわかります。もう一回送って、再度コンシューマーを確認しましょう。

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

これでチュートリアルは無事終了です。このチュートリアルでは次のことを学びました。

* 非同期プログラミングを使用した小さな例
* Kafkaとは何か、なぜAgoraはKafkaを使用するのか
* ローカルのminikubeクラスタ上でKafkaを実行する方法
* Kafka を使って 2 つのサービスを対話させる方法

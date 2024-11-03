# Step 1: Deploying a Service in Our Minikube

まず最初に、リクエストの受付、処理、適切に応答するサービスをデプロイします。このステップでは、サービスをデプロイし、外部からサービスにいくつかのトラフィックを送信します。

## Deploying the Receiver Service

このサービスは、今回のチュートリアル向けに用意されたものです。ここで必要なのは、ファイルをminikubeの環境にデプロイするだけです。今回は以下をデプロイします。

* Receiverが存在するネームスペース
* アプリケーションのサービス
* アプリケーション本体のデプロイメント

まず、ネームスペースをデプロイします。

```
cd receiver/kubernetes
kubectl apply -f _namespace.yaml
```

```
namespace/receiver created
```

次に、サービスをデプロイします。

```
kubectl apply -f service.yaml
```

```
service/receiver-service created
```

そして最後に、アプリケーション本体のデプロイです。
```
kubectl apply -f deployment.yaml
```

```
deployment.apps/receiver-deployment created
```

`kubectl`で確認することができます。

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

それでは、アプリケーションをチェックし `port-forward`　と`curl` で動作することを確認してみましょう。

まず、 `port-forward`　を使用してみます。

```
kubectl port-forward receiver-deployment-xxx -n receiver 8080:8080
```

このコマンドは返って**きません**が、これによってトンネルが繋いだままになるので問題ありません。

```
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

新しいターミナルを起動して、以下を実行してください。`<name>`は文字列に置き換えてください。

```
curl http://localhost:8080/hello/<name>
```

```
curl http://localhost:8080/hello/yshtola
{"message":"Hello there yshtola!"}
```

応答が返ってくるということは、レシーバーがメッセージを受信していることを意味します。

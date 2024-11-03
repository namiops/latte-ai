# Step 3: Deploying a Deployment

## What is a Deployment

Kubernetesでは、[_deployment_](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)は _Workload Resource_ であり、アプリケーションを管理するのに役立ちます。デプロイは多くの種類のワークロードリソースの1つですが、アプリケーションをクラウド環境にプッシュする方法を学ぶための良い出発点です。

![Deployment](./assets/MinikubeDeployment.png)

デプロイの工程では、次の2つの主要なコンポーネントがデプロイされます。

* [_replica set_](https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/):
  * これは基本的に、デプロイしたいマニフェストのテンプレートです。Kubernetesに「どのアプリケーションをいくつコピーするか」を通知したり、その他にもアプリケーションと一緒に実行したい追加設定が含まれます。
  * もしポッドがダウンしたり機能停止した場合、レプリカセットはKubernetesに、リクエストしたポッドの数に合わせて新しいコピーを作成する必要があることを伝えることで、作業を支援します：もし3つのコピーをリクエストして、1つのコピーが失敗したらレプリカセットは、古いコピーを置き換えるために新しいコピーを作成するようKubernetesに指示します。
* アプリケーション本体をポッドに、または複数のコピーを必要とする場合には複数のポッドにします。

## How to deploy the Deployment

先ほどの手順と同じように、kubectl createを実行します。

```shell
$ kubectl create -f deployment.yaml
deployment.apps/landing-page created
```

## Verify the Deployment is up

デプロイが正しく動作したかどうか確認してみましょう。

```shell
kubectl get all -n landing-page
```

以下のような内容が表示されるはずです。

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
    もしリソースの1つが`0/1`と表示されたら、それはKubernetesがまだ処理中であることが原因の可能性があります。数秒後にコマンドを再実行すれば大丈夫なはずです。

ここでは、サービスに加え、デプロイ、レプリカセット、ポッドがクラスタで稼働していることがわかります。

## Checking on our Application

ポッドをチェックし、その動作を確認しましょう。それにはいくつかの方法があります。

まずはポッドを記述(describe)し、問題がないかを確認します。

```shell
kubectl describe pod <name of the pod> -n landing-page
```

出力の最後に、以下のようなものが表示されるはずです。
ここには多くのことが書かれていますが、このレッスンではまだ気にする必要はありません。

```
Events:
  Type    Reason     Age    From               Message
  ----    ------     ----   ----               -------
  Normal  Scheduled  4m12s  default-scheduler  Successfully assigned landing-page/landing-page-6867cb6d-k6rmt to minikube
  Normal  Pulled     4m11s  kubelet            Container image "docker.artifactory-ha.tri-ad.tech:443/wcm-cityos/landing-page:main-96735d63-990" already present on machine
  Normal  Created    4m11s  kubelet            Created container landing-page
  Normal  Started    4m11s  kubelet            Started container landing-page
```

ここでは、Kubernetesがデプロイメントマニフェストに従ってイメージを取得し、コンテナを作成し、それを起動させていることがわかります。また、ログをチェックして、アプリケーションが稼働しているかどうかを確認することもできます。

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

アプリケーションの中に、いくつかのログメッセージがあることがわかります。

!!! Note
    **私のアプリケーションを呼んでいるのは何でしょうか？**
    
    上記のメッセージは、Kubernetes自身から発信されているものです。Kubernetesには「Liveness and Readiness Probes」という機能があり、あなたのサービスが正常にトラフィックを待ち受けているかどうかをチェックすることができます。ポッドが応答していない場合、Kubernetesは、ポッドを再起動したり、ポッドを終了して新規のポッドに置き換えたりするなど、そのサービスが再起動して正常に応答するように何らかのアクションを取ることができます。

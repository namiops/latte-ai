# Step 2: Deploying a Service

## What is a Service

Kubernetesでは、 [_service_](https://kubernetes.io/docs/concepts/services-networking/service/)は、複数のPodをグループ化し、それらを一つのDNS名でホストさせることができるように抽象化したものです。 [_pod_](https://kubernetes.io/docs/concepts/workloads/pods/)（クジラのポッドやサヤエンドウのポッド）は、1つまたは複数のコンテナのグループで、ストレージとネットワークリソースを共有し、コンテナの運用方法に関する仕様を備えたものです。各ポッドは、サービスによって管理されるエンドポイントを公開し、サービスはそのエンドポイントから必要に応じて各ポッドにトラフィックを送信できます。

![Service](./assets/MinikubeService.png)

サービスは、アプリケーションを様々な方法で設定することで、次のような問題を解決することができます。

* サービスはDNS名を提供するため、ポッドの正確なIPを知らなくても、「フロントエンド」コンポーネントが「バックエンド」コンポーネントを見つけることができます。
* アプリケーションの負荷分散が行えます。サービスは、ロードバランサのように機能し、同じDNS名でいくつものインスタンスをホストするように構成することができます。

## How to deploy the Service

手順1に引き続き、以下のコマンドを実行します。

```shell
$ kubectl create -f service.yaml
service/landing-page created
```

## Verify the Service

サービスを確認する方法はいくつかありますが、前回のレッスンでは、ネームスペースを作成することができましたので、それを使ってみましょう!

```shell
kubectl get all -n landing-page
NAME                   TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
service/landing-page   NodePort   10.104.233.183   <none>        80:31501/TCP   XXXs
```

上記のような出力が表示されるはずです。

ここでもう少し掘り下げて、別のkubectlコマンドを使用してみましょう。
**describe**

```shell
kubectl describe service landing-page -n landing-page
```

以下のようなメッセージが表示されるはずです。

```
Name:                     landing-page
Namespace:                landing-page
Labels:                   <none>
Annotations:              <none>
Selector:                 app=landing-page
Type:                     NodePort
IP Family Policy:         SingleStack
IP Families:              IPv4
IP:                       10.104.233.183
IPs:                      10.104.233.183
Port:                     http  80/TCP
TargetPort:               80/TCP
NodePort:                 http  31501/TCP
Endpoints:                <none>
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

ここで注意すべき点がいくつかあります。

* 現在、`エンドポイント`はありません。大丈夫です! これは、まだクラスタから公開したいアプリケーションが実行されていないからです。次のステップで、アプリケーションをデプロイした際、サービスを記述するとエンドポイントのエントリがあることを確認します。
* サービスの `Type` は `NodePort` となっています。これは各ノードのIPをノードの静的なポートで公開するようにサービスを設定する方法です。 minikubeでは、とりあえずこのタイプでいいと思われます。

!!! Note
    Kubernetesの利用を進めていくと、他にもいくつかのタイプのサービスがあることがわかります。例えば、クラウドプロバイダーのロードバランサーを利用して、ポートの割り当てと公開を支援する`LoadBalancer` などです。
    これは主にアゴラで使われているのを見かけるかもしれませんが、今は気にする必要はありません。しかし、知っておくと役に立ちます!

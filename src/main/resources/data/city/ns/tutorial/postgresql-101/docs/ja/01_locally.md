# Getting Started on Kubernetes
このセクションでは、Minikube（または同等のもの）を使ってKubernetes上でアプリを起動させ、それを使って実際に実行できるようにします。

## Prerequisites
このコードを実行するには、事前準備として必要なものがいくつかあります。しかし、良いことに、アプリを起動させることができれば、Agora プラットフォーム上でのアプリ実行が既に半分以上終わっています。

1. Minikubeです。設定方法については、 [Minikube-101](/docs/default/component/minikube-tutorial)をご参照ください。
2. PostgreSQL オペレータです。インストールは、こちらの[ガイド](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/quickstart/)に従ってください。
3. このTutorialで使用されているコードは、 [こちら](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/postgresql-101)で確認できます。このコードは必ずしも必要ではありません。ご自分のアプリで代用できます。

## Run
すべてのインストールと準備ができたら、コードのあるフォルダーに移動します。

!!!Note
    Kubernetesのコンテキストが複数ある場合は、 `kubectl use-context <context name>` コマンドで正しいコンテキストに切り替えてください。

起動するには、以下を実行します:

```Shell
$ kubectl apply -k deploy
namespace/postgresql-101 created
service/postgresql-101 created
deployment.apps/postgresql-101 created
postgrescluster.postgres-operator.crunchydata.com/hippo created
```

実行すると、作成されたポッドを確認することができます:

```Shell
$ kubectl get po -n postgresql-101
NAME                             READY   STATUS              RESTARTS   AGE
hippo-instance1-fkfx-0           0/4     Init:0/2            0          23s
hippo-repo-host-0                0/2     Init:0/2            0          23s
postgresql-101-97f59c5c7-hm26w   0/1     ContainerCreating   0          24s
```

!!!Warning
    データベースポッド（ `hippo`と表示されているもの）が初期化に失敗する場合は、リモートリポジトリからイメージを引き出す際にタイムアウトすることが原因である可能性が高いです。この回避策として、イメージをローカルにプルする方法があります。これは、`minikube ssh docker pull {image}` を実行することでうまくいきます。しかしながら、`postgres.yaml`にあるイメージはタイムアウトする可能性が高いです。 

最終的には、このように3つのポッドがすべて正常に動作していることが確認できるはずです:

```Shell
$ kubectl get po -n postgresql-101
NAME                             READY   STATUS      RESTARTS        AGE
hippo-backup-bq64-79m8f          0/1     Completed   0               107s
hippo-instance1-9rqm-0           4/4     Running     0               6m44s
hippo-repo-host-0                2/2     Running     0               6m44s
postgresql-101-97f59c5c7-hm26w   1/1     Running     6 (3m41s ago)   6m44s
```

再起動のことはあまり気にしなくても問題ありません。再起動は、すべてが一度に初期化されるために起こります。データベースインスタンスの初期化に時間がかかるため、APIポッドがデータベースへの初期接続に失敗します。 

## View
最後に、Podsが動作していることを確認し、APIにアクセスして何が起きているかを確認することができます。

そのために、ローカルポートをクラスタのポートにポート転送してみましょう。

```Shell
$ kubectl port-forward service/postgresql-101 -n postgresql-101 8888:80
Forwarding from 127.0.0.1:8888 -> 80
Forwarding from [::1]:8888 -> 80
```

ポートを転送すると、`localhost:8888`を使用してAPIにアクセスできるようになります。

また、APIにはあらかじめswaggerが搭載されており、APIの定義を確認したり、テストすることも可能です。swaggerへのアクセスはこちら（ブラウザで）です：http://localhost:8888/swagger
自由に動かしてみてください。

また、 `curl`を使用してAPIにアクセスすることもできます。これを実行することで、Todo項目を追加することができます:

```Shell
$ curl -X 'POST' 'http://localhost:8888/TodoItem/Add?item=yourTodoItem'
```

このように、追加した項目を表示させることができます:

```Shell
$ curl -X 'GET' 'http://localhost:8888/TodoItem/GetAll'
```

これらの呼び出しはすべて、オペレータが作成した実際のデータベース・インスタンスで実行されます。
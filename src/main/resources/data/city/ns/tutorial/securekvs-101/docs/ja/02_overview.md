# Overview
さて、インストールが成功したところで、出来上がったものをすべて見てみましょう。

## Services
合計で4つのサービスがデプロイされているはずです。Helm チャートからの 3 つと、API デプロイメントからの 1 つです。

```shell
$ kubectl get services -n securekvs-101
NAME                                    TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
db-agora-kvs-test-secure-kvs            ClusterIP      10.109.183.35   <none>        5984/TCP                     3h35m
headless-db-agora-kvs-test-secure-kvs   ClusterIP      None            <none>        5984/TCP,4369/TCP,9100/TCP   3h35m
securekvs-101                           LoadBalancer   10.96.36.187    <pending>     8080:32445/TCP               3h34m
steelcouch-agora-kvs-test-secure-kvs    ClusterIP      10.98.150.35    <none>        5984/TCP                     3h35m
```

では、これらのサービスにはどのようなものがあるのでしょうか？ひとつひとつ見ていきましょう。

* `db-agora-kvs-test-secure-kvs` は、CouchDBのエンドポイントです。
* `headless-db-agora-kvs-test-secure-kvs` は、内部CouchDBポッド通信のためのサービスです。
* `securekvs-101` は、APIのエンドポイントになります。
* `steelcouch-agora-kvs-test-secure-kvs` は、SteelCouchのエンドポイントになります。

APIのデプロイメントファイルを見れば、`steelcouch-agora-kvs-test-secure-kvs` サービスを使用して、環境変数を通じてAPIへのエントリポイントを導入することがわかります。

## Secrets
複数のシークレットが作成されますが、ここでは `db-agora-kvs-test-secure-kvs` という名前のシークレットにのみ注目する必要があります。このシークレットには、データベースのユーザー名とパスワードが保存されています。Steelcouchは、それらが同じであることを理由として、CouchDBに資格情報を転送します。

もう一度APIのデプロイメントファイルを見ると、環境変数を使ってデータベースの認証情報をAPIに渡していることがわかります。
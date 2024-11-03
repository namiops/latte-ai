# Step 1: Deploying a Namespace

## What is a Namespace

 [_ネームスペース_](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) は、Kubernetesにおいて、リソースをラベル付けして整理するための方法です。関連するリソースを同じネームスペースにまとめることができ、異なるネームスペースのリソースは互いに分離されます。これは、コードをパッケージやモジュールに名前を付けて整理し、コードファイルをあるべきパッケージの下にグループ化するのと似ています。

ネームスペースは、Agora がチームごとにリソースを区分けする方法なので、使い方を知っていると便利です!

## How to deploy the namespace

作業ディレクトリから、namespaceファイルを指定して `kubectl create` を実行するだけで良いです。

```shell
kubectl create -f namespace.yaml
```

!!! Tip
    同様のことは`kubectl apply -f namespace.yaml`で実行することもできます。
    なぜ、`apply`ではなく、`create`なのでしょうか？　Createは、クリーンなminikubeシステムを使用しているため、ここで使用することができる_命令型のコマンド_です。
    一方、`apply`の使用は、既存のリソースがあることを前提としており、慎重に使用しなければ潜在的な問題につながる可能性があります。どちらも自由に使うことができます。同じ効果が得られますが、このチュートリアルが想定する環境では`create`は正しく動作します。

## Verify the Namespace is there

次の `kubectl` コマンドを実行することで、そこにネームスペースがあることを確認することができます。

```shell
kubectl get namespaces
```

このような出力になるはずです。

```shell
NAME              STATUS   AGE
default           Active   2d3h
kube-node-lease   Active   2d3h
kube-public       Active   2d3h
kube-system       Active   2d3h
landing-page      Active   5s
```

!!! Note
    あなたのminikube環境には、すでにいくつかのネームスペースがあることにお気づきでしょう。Kubernetesには、minikubeが初回起動時に生成するリソースのためのネームスペースがいくつかあります。これらを気にする必要はありませんが、Minikubeを起動したときに、すぐに使えるリソースがあることを知っておくのは良いことです。

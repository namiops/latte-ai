# Step 2: Deploying our Application With Vault

最初のステップでは、我々のアプリケーションと、機密データを扱うKubernetes Secretsを使用する際の潜在的な注意点を紹介しました。このステップでは、Vaultに機密データを処理させ、Vaultがどのように役立つかを紹介します。

## How Vault Works

以下は、我々のアプリケーションでVaultがどのように機能するかを示した非常にシンプルな図です。
これは全てのプロセスではありませんが、チュートリアルのため、余計なことは省略しています。

![simple](./assets/vault-agent-simple.png)

このセットアップで行うのは、Vaultに我々のsecretを保存しておくことです。Vault は現在、secretとそれを読むことができる人を管理しています。我々のアプリケーションに対して、Vaultが secret の読み取りを許可されていることを示す正しいクレデンシャルを提供します。
Vaultは、Kubernetes Secretと同じような方法で、我々のためにそれをデプロイに配置します。

## Deploying our application with Vault

### Setting up Our Secret Code in Vault

Kubernetesのシークレットを設定したのと同じように、保存するシークレットがあることをVaultに伝える必要があります。これはチュートリアルのため、また物事をシンプルに保つためにAgoraチームによって前もって行われています。

我々のシークレットとVaultに保存したものには、以下のような若干の違いがあります。

* Vaultでのsecretの名前は、 `my-secret-word` ではなく、`vault-tutorial`です。
* 我々のsecretの鍵は `secret-key`という名前ですが、Vaultでは `secret_key` という名前になっています。

### Deploying our Application

ここで、 `vault-deployment.yaml` ファイルを見てみましょう。

```yaml
# これは、Vaultを使用する場合のアプリケーションの配置です。
# Vault は Agoraクラスタにある Vaultエージェントを通じて、自動的にデプロイメントにフックされます。
# このファイルでは、より細かい部分について説明します。
apiVersion: apps/v1
kind: Deployment
metadata:
  name: vault-101
  namespace: vault-101
  labels:
    app: vault-101
spec:
  replicas: 1
  selector:
    matchLabels:
      app: vault-101
  template:
    metadata:
      labels:
        app: vault-101
      # AgoraでVaultを使用するために、Vaultエージェントにデプロイを依頼します。
      # Vault Agent は、アノテーションによって指定されたデプロイメントを待ち受けるよう要求することができます。
      # Vault Agentに提供できるすべてのさまざまなアノテーションの詳細については、以下を参照してください。      https://www.vaultproject.io/docs/platform/k8s/injector/annotations
      annotations:
        # 最初のアノテーションは、Vaultがsecretをデプロイに注入することを希望していることを示します。
        vault.hashicorp.com/agent-inject: 'true'
        #これは、Vault Agentを最初に起動し、その作業を行う必要があることをデプロイに伝えるものです。
        #そして、デプロイに複数のコンテナまたはサイドカーがある場合に必要になることがあります。
        # Agoraでは、これらのサイドカーはあなたのために自動的に行われます。これは単にVaultを先に起動させるだけのものです。
        vault.hashicorp.com/agent-init-first: 'true'
        # Vault Agentサイドカーには、他の多くのアプリケーションと同様にログがあり、ここでログレベルを設定することができます。
        # この例では、「trace」に設定しています。
        vault.hashicorp.com/log-level: 'trace'
        # Vaultは、システムのマルチテナントを可能にします。これにより、複数のVaultインスタンスを持つことなく、複数のチームが同じVaultで作業できるようになります。Agoraチームは、このようなネームスペースを一つもっており、このアノテーションはそれを宣言しています。
        # これにより、Vault Agentは、secretのようなリソースのために正しいネームスペースを探すことができます。
        vault.hashicorp.com/namespace: 'ns_dev/ns_cityos_platform'
        # Vaultでは、デプロイに対してRoleを宣言する必要があります。Roleは、Vaultが読み取り可能なSecretを決定するためのものです。このチュートリアルでは、Roleを設定しましたので、ここで宣言するだけです。
        vault.hashicorp.com/role: 'vault-tutorial-dev'
        # Vaultでは、読み取りたいAPIパスにあたる'path'に基づいてsecretを埋め込むことができます。
        # 我々の場合、secretはパス 'kv-dev/vault-tutorial' の下にあるので、ここで宣言するのはそのパスです。
        vault.hashicorp.com/agent-inject-secret-config: 'kv-dev/vault-tutorial'
        # Vaultでは、コードが期待するものと一致するようにsecretを書式化（テンプレート化）することができます。
        # 例えば、PostgresデータベースのURLをフォーマットしてアプリケーション内に設定すれば、自分で組み立てなくても、URLの全文を読み取ることができます。
        # 我々のアプリケーションのためには、secretはそれだけでもファイル内にあることが必要なのです。
        vault.hashicorp.com/agent-inject-template-config: |-
          {{ with secret "kv-dev/vault-tutorial" -}}
            {{ .Data.data.secret_key }}
          {{- end }}
    spec:
      serviceAccountName: vault-tutorial
      containers:
        - name: vault-101
          image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/tutorials/vault-101:main-2e325fc0-5337
```

このファイルの `spec` 部分は、ステップ1の`secret-deploymnet.yaml` ファイルにあるものと似ています。ただし、ボリュームマウントの宣言はしていません。そのようにしないのは、Vaultがこれをやってくれるからです。Vault は、我々がYAMLに追加したアノテーションを使って、我々のためにこの作業を行います。

!!! Note
    **では、そのsecretはどこにあるのでしょうか？**

    SecretはVaultによって処理され、Vaultがそれを注入する方法は、Secretsの例のボリュームマウントと同様のファイルを作成することによって行われます。
    違いは、Secretは**デプロイ時**のみ存在するボリュームマウントに保存され、デプロイが解除または削除されると、Secretも一緒に消えてしまうことです。
    
    我々は開発者として、Vaultの設定によって、secretがどこにあるのかわかります。つまり、Vaultに「デフォルト」のパスを設定させるか（これは我々のアプリケーションが使用しているものです）、あるいは、Vaultにsecretを置く場所とその形式を指示することができるのです。このチュートリアルだけでは説明しきれないことがたくさんありますが、このようなものが存在し、それを利用することができるということをご理解ください。

`vault-tutorial` secretの読み取りが許可されていることを知らせるためにVaultに提供する情報は、 `spec`の下に追加した **serviceAccountName**です。これは、Vaultにこのアカウントを使用して、我々がアクセス要求しているSecretの読み取りが許可されているかどうかを判断するように指示します。

まず、Vaultが必要とするアカウントがあるかどうかを確認する必要があります。 これを行うには、 `service-account.yaml` ファイルをクラスタにデプロイします。

```shell
$ kubectl apply -f service-account.yaml  
serviceaccount/vault-tutorial created
clusterrolebinding.rbac.authorization.k8s.io/vault-tutorial-auth-delegator created
```

このアプリケーションをVaultでデプロイしてみましょう。ステップ1で実行したのと同じコマンドを使います。

```shell
$ kubectl apply -f vault-deployment.yaml 
deployment.apps/vault-101 created
```

そして、同じコマンドを実行して、同じように動作することを確認することができます。

```shell
$ kubectl get all -n vault-101
NAME                             READY   STATUS    RESTARTS   AGE
pod/vault-101-7bc5f8b67b-9gtw7   1/1     Running   0          6s   <--- Our Application

NAME                        READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/vault-101   1/1     1            1           6s

NAME                                   DESIRED   CURRENT   READY   AGE
replicaset.apps/vault-101-7bc5f8b67b   1         1         1       6s

$ kubectl logs vault-101-7bc5f8b67b-9gtw7 -n vault-101 -f
Hey there, the word is banana
Oops! I let the secret word out, better run!
```

## What Vault Does Differently

これで、Kubernetes SecretsとVaultを使用して、サンプルであるblabbermouthアプリケーションを2回デプロイすることに成功しました。 では、Vaultに任せることで、我々は一体何ができるようになったのでしょうか？

### There is no Secret deployed to the cluster

Vaultが我々のためにSecretを処理してくれるため、デプロイの一部としてSecretを宣言する必要がありません。つまり、我々のGitOps/Infrastructure As Code (IAC) というアプローチで、すべてのインフラをリポジトリで安全に宣言するという目標に合致しているのです。

### The Secret is in a single place that we can control access to

Vaultには、誰が何を求めているかを知る必要があります。デプロイ時に、我々が誰で、何へのアクセスを求めているかを伝えるために、Vaultにサービスアカウントトークンを提示しました。
このアクセス制御は、必要に応じて拡張したり、取り消したりすることができます。もし単純に `vault-deployment.yaml` からトークンを削除してしまうと、Vault は我々が secret の読み取りを許可されていないことを伝えます。

### Cleaning up our Namespace

このチュートリアルを終えるにあたり、今後の学習者の競合を避けるため、我々は今回作成したものをすべて削除しましょう。

```shell
$ kubectl delete -f vault-deployment.yaml
deployment.apps "vault-101" deleted
$ kubectl delete -f _namespace.yaml
secret "vault-101" deleted
```

## Congratulations

ここまでくれば、以下のような基本的な理解はできているはずです。

* Vaultとは何か、機密データを保護するためにどのように機能するか
* Vaultを使ったアプリケーションのデプロイ方法
* Kubernetes Secrets を使用する場合と比較して、Vault では何ができるのか？

次のステップは、ご自身で実際に試してみることです。Vaultをアプリケーションで使用する際のベストプラクティスや詳細については、[Agora Developer Documentation]() を参照してください。

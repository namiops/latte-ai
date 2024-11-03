# Step 1: Deploying our Application Without Vault

まず、Vaultが何をしてくれるのか、どのように利用できるのかを理解するために、Vaultを利用しないアプリケーションのデプロイを行います。ここでは、アプリケーションのサンプルと、Vaultを使用しない場合の潜在的な問題点について説明します。

## Our Sample Application

このサンプルは、秘密鍵を取り込み、皮肉にもログに表示する小さなコードの一部分です。

```rust
/// これがアプリケーションの全体像です。
/// 指定されたパスからファイルを読み込み、そのままメッセージを表示します。
/// 60秒後にアプリケーションは終了します。
fn main() {
    let mut file = File::open(FILE_PATH).expect("Oops! Couldn't find the file");
    let mut secret_word = String::new();
    file.read_to_string(&mut secret_word)
        .expect("Oops! Something happened reading file");

    println!("Hey there, the word is {}", secret_word);

    let sixty_seconds = Duration::from_secs(60);
    sleep(sixty_seconds);

    println!("Oops! I let the secret word out, better run!")
}
```

このアプリケーションは、ファイルパスからデータを読み取っています。このファイルは、いくつかの方法でデプロイすることができますが、ここでは2つの方法について説明します。最初に説明するのは、Kubernetes Secretsの利用です。

## What is a Kubernetes Secret

[Secret](https://kubernetes.io/docs/concepts/configuration/secret/) とは、Kubernetesのリソースで、パスワード・トークン・キーなどの機密データを保持するためのものです。Secretの背景には、機密性の高いデータはいかなる攻撃者からも容易に読み取られてはいけないという考え方があります。

Secretsは、アプリケーションのデプロイに依存しない形でリソースを作成し、デプロイ時に機密データが含まれるリソースにアクセスできるようにすることでこの問題を解決しようとしています。これを実証するために、Secretを利用したデプロイをクラスタ内に作成します。

## Deploying our application

### Setting up the Namespace

まず、アプリケーションをデプロイするためのネームスペースを作成します。これによって チュートリアルのすべてのリソースが論理的に整理されます。次に、プロジェクトの  `kubernetes` フォルダに移動します。

```shell
$ cd kubernetes
```

そして、`kubectl` を使って以下のマニフェストを適用します。

```shell
$ kubectl apply -f _namespace.yaml
namespace/vault-101 created
```

### Setting up the Deployment's Secret

続いて、secretをデプロイします。ここで`secret.yaml`ファイルを簡単に見てみましょう。

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-secret-word
  namespace: vault-101
type: Opaque
data:
  secret-key: YmFuYW5h
```

ここで、このファイルをクラスタに適用すると、`secret-key`というキーが1つ配備されることがわかります。それでは試してみましょう。

```shell
$ kubectl apply -f secret.yaml
secret/my-secret-word created
```

### Deploying the Application

最後に、アプリケーションのデプロイをセットアップします。
それでは、`secret-deployment.yaml`を見てみましょう。

```yaml
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
    spec:
      containers:
        - name: vault-101
          image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/tutorials/vault-101:main-2e325fc0-5337
          # ここでは、secretをボリュームとしてマウントすることができます。
          # これは基本的に、アプリケーションが読み取ることを許可されたファイルとディレクトリをデプロイメントにマウントするように動作します。
          # このアプリケーションは、パス /vault/secrets からファイルを読み込もうとしています。
          # そこで、そのパスで読まれるようにsecretを設定することにします。
          
          volumeMounts:
            - mountPath: "/vault/secrets"
              name: my-secret
              readOnly: true
      volumes:
        # ここで、我々の secret をデプロイに添付することができます。
        - name: my-secret
          secret:
            # これは secret.yaml ファイルで宣言した名前です。
            secretName: my-secret-word
            # ここで、我々のSecretがどのように表示されたいかをKubernetesに伝えることができます。
            # 今回は、我々のsecretにはキーがあり、それを「config」というファイル名で表示させたいと考えています。
            # これにより、我々のアプリケーションはそれを読み取ることができるようになります。   
            items:
              - key: secret-key
                path: "config"
```

ここでは、secretをボリュームマウントとしてアタッチし、ファイルパスのように見せています。secretはファイルのようにマウントされ、これはアプリケーションが想定しているとおりなのでうまく動作します。このファイルは、アプリケーションで設定したパス `/vault/secrets/config` にデプロイごとにマウントされます。

```rust
const FILE_PATH: &str = "/vault/secrets/config";

/// これがアプリケーションの全体像です。
/// 与えられたパスからファイルを読み込み、そのままメッセージを表示します。
/// 60秒後にアプリケーションは終了します。
fn main() {
    let mut file = File::open(FILE_PATH).expect("Oops! Couldn't find the file");
    //code omitted
}
```

これからアプリケーションをデプロイします。

```shell
$ kubectl apply -f secret-deployment.yaml
deployment.apps/vault-101 created
```

### Verifying our Application Works

正常に動作しているかどうかを確認するために、デプロイのログを表示させてみましょう。
まず、ポッドの名前が必要です。これは、`kubectl get all`で見つけることができます。

```shell
$ kubectl get all -n vault-101
NAME                             READY   STATUS    RESTARTS   AGE
pod/vault-101-7bc5f8b67b-9gtw7   1/1     Running   0          6s   <--- Our Application

NAME                        READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/vault-101   1/1     1            1           6s

NAME                                   DESIRED   CURRENT   READY   AGE
replicaset.apps/vault-101-7bc5f8b67b   1         1         1       6s
```

ポッドの名前が決まったら、`kubectl logs` で `-f` を付けてポッドを追跡することができます。このアプリケーションが動作することを確認します。アプリケーションはsecretを表示し、そして、その間違いに気づき、1分ほどで終了してしまいます。

```shell
$ kubectl logs vault-101-7bc5f8b67b-9gtw7 -n vault-101 -f
Hey there, the word is banana
Oops! I let the secret word out, better run!
```

## Why Secrets Aren't so Secret

このように、私たちのアプリケーションは動作しますが、デプロイにSecretsだけを使用することには、いくつかの欠点があります。

### The Secret still needs to be deployed somewhere that can be handled by Agora and our infrastructure

Agora チームは、オートデプロイの処理をするためにいくつかのツールを使用して作業しています。 我々のインフラを再現可能にする手段を持ち、変更を監査し追跡する方法を持つために、我々はGitOpsとInfrastructure As Code (IAC)をポリシーとして使用しています。これは、我々のデプロイでSecretsを使用するための2つのことを意味します。

1) **私たちのgitリポジトリの中にSecretsを置くことはできません。** これはセキュリティ上の問題です。もしsecretがリポジトリ内に存在すると、それを追跡・発見され、悪用される可能性があります。

2) **手作業でのデプロイや、目に見える場所でのSecretに機能を持たせることはできません。** GitOpsとIACの一部は、コード内のすべてのインフラを見ることができます。そのため、我々の知らない、あるいは見ることができないSecretがあると、追跡や再現が難しく、その用途を把握することも難しくなります。

以上のような理由から、Secretsはインフラの長期的な将来を考えると、守ることができないものなのです。

### Secrets can be found easily

[こちらのドキュメント](https://kubernetes.io/docs/concepts/configuration/secret/)に記載のSecretは、APIサーバーの基礎となるデータストアである`/etcd`に暗号化されずに保存されます。また、ネームスペースにポッドを作成できる人なら、デプロイのような間接的な方法も含めて、誰でもそのSecretを見たり読んだりすることができます（これは私たちがやったことです）。つまり、「Secret」を手に入れて、単に読む手段があるのです。

さらにSecretsはBase64でエンコードされているだけなので、「本質的に」全く暗号化されていないことになります。もし、ある人がSecretにアクセスできれば、Secretの中にある秘密鍵を簡単に解読することができます。

### Cleaning up our Namespace

次のステップでは少々異なるものをデプロイしますが、このプロセスを簡単にするために、これまでの作業内容を消去しましょう。必要なのはデプロイメントとsecretを削除するだけです。

```shell
$ kubectl delete -f secret-deployment.yaml 
deployment.apps "vault-101" deleted
$ kubectl delete -f secret.yaml 
secret "my-secret-word" deleted
```

## What's Next
次のステップでは、Vaultで何ができるのか、そしてなぜSecretsの代替としてVaultを推奨するのかを見ていきます。同じアプリケーションをデプロイしますが、今回はSecretの処理と、アプリケーションで読めるようにする方法をVaultに任せます（いずれにしてもSecretを表示してしまうおしゃべりなアプリですが...）。

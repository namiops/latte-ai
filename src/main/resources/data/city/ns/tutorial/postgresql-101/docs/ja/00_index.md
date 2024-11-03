# Introduction
このチュートリアルは、Agora プラットフォームで Postgres オペレータを使用する方法の例です。これは、Todo 項目を追加したり、表示したりすることができるシンプルな Todo リスト API が提供されています。この API は ASP.NET Core 6 で書かれており、データベースと連携するために Entity Framework を使用しています。API はEntity Frameworkのマイグレーションのヘルプを用いて、起動時に実際のデータベーステーブルを作成します。つまり、このチュートリアルでは、データベースのテーブルを管理する方法はカバーしません。

また、このチュートリアルは [official Postgres operator documentation](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/quickstart/)の代わりではありません。そのため、必ずそちらをご確認ください。

## Why Crunchy PostgreSQL Operator?
Postgresインスタンスを管理するために、オペレータを使用する必要はありません。Agoraプラットフォーム上で、好きなようにインスタンスを作成することができます。しかし、Agoraチームは、Postgresインスタンスの作成と管理にオペレータを使用することを強く推奨しています。

では、オペレータは何をしてくれるのでしょうか？そこで、オペレータの開発者に説明してもらいましょう。

> GitOpsのワークフローに合わせて設計されているため、PGOを使えばPostgres on Kubernetesを簡単に使い始めることができます。 高可用性、障害復旧、監視を備えたプロダクショングレードのPostgresクラスタを、安全なTLS通信で、わずかな時間で構築することができます。さらに、PGOではPostgresクラスタを簡単にカスタマイズして、あなたの業務量に合わせることができます!
>
> PGOは、Postgresクラスタのクローン、ダウンタイムを最小限に抑えたローリングアップデートの使用などの便利な機能によって、お使いのリリースパイプラインのあらゆる段階でPostgresデータをサポートする準備ができています。回復力と稼働率のために設計されたPGOは、あなたが心配する必要がないように、ご希望のPostgresを必要な状態に保つことができます。
>
> PGOは、Kubernetes上でのPostgres管理の自動化に関して長年の運用経験をもとに開発されており、ユーザーのデータを常に利用可能にするシームレスなクラウドネイティブPostgresソリューションを提供します。

これだけでは十分な理解を得られない場合は、サンプルAPIを見て、いかに簡単にセットアップできるかを実感してください。

## Supported Versions
Agora プラットフォームには、Postgres オペレータがプリインストールされています。つまり、そのオペレータは最新のバージョンではない可能性があります。そのため、Postgresデータベースインスタンスのすべてのバージョンがサポートされているわけではありません。

このチュートリアルを記載している時点では、アゴラプラットフォームのバージョンは以下のものを使用しています。

```
Crunchy PostgreSQL Operator 5.0.4
```

サポートされているPostgresインスタンスの一覧は[こちら](https://access.crunchydata.com/documentation/postgres-operator/v5/releases/5.0.4/)をご覧ください。また、特定のバージョンに対応したDocker Imageは [こちら](https://www.crunchydata.com/developers/download-postgres/containers)で入手することができます。

## File Structure Overview
この章では、フォルダー構成と重要なファイルの概要を説明します。

### deploy
kubernetesへのデプロイに必要な `yaml` ファイルが格納されているフォルダです。

#### namespace.yaml
名前空間を作成します。 `postgresql-101`.

#### postgres.yaml
ここにメインファイルがあります。このファイルには、オペレータが作成する必要のあるPostgresのインスタンスが記述されています。このファイルでは、Postgresインスタンスをカスタマイズすることができます。Postgresインスタンスの仕様の多くはデフォルトを使用しているため、設定ファイルにはそのようなものを含んでいません。 すべての仕様の一覧は、[official documentation](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/tutorial/customize-cluster/)を参照してください。もしくは、Postgresオペレータがインストールされている場合、このコマンドを使用することができます。 

```Shell
$ kubectl explain postgresclusters
```

これは、Postgresオペレータの公式サンプルで使用されている標準ファイルで、1つだけ小さな変更が加えたものです。では、その変更点を見てみましょう。

```yml
apiVersion: postgres-operator.crunchydata.com/v1beta1
kind: PostgresCluster
metadata:
  name: hippo
  namespace: postgresql-101
spec:
  # Redacted
  ...
  users:
    - name: exampleuser
      databases:
        - TodoItems
      options: "SUPERUSER"
  # Redacted
  ...
    
```

ここでは、`exampleuser`  ユーザーを作成し、そのユーザーに  `TodoItems` データベースへのアクセス権を与えています。このユーザーはテーブルを作成する必要があるので、`SUPERUSER`の権限を与えています。 作成したユーザー名をメモしておいてください。後で使用します。また、`metadata.name`もメモしておいてください。

#### api.yaml
このファイルにより、ASP.NET Core APIを設定します。いくつか注意点があります。まず、コンテナイメージはArtifactoryに保存しています。また、Agoraプラットフォームに発行できるようにするには、Artifactoryにイメージを保存する必要があります。Artifactoryへの接続方法は、こちらの [guide](https://docs.woven-planet.tech/engineering_software/artifactory/support/Docker-Registry/)を参照してください。接続が完了したら、イメージにタグを付けてプッシュする必要があります。

次に注意が必要なのは、環境変数です。このようにして、Postgresクラスタの詳細がAPIに組み込まれるのです。パスワード変数を詳しく見てみましょう。

```yml
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: hippo-pguser-exampleuser
      key: password
```

ここで注意しなければならないのは、secret key refです。`secretKeyRef` はこのように取得することができます。

まず、データベースクラスタ名、この場合は `hippo` を指定します。次に `pguser` です。最後に、先ほど説明したユーザー名、この場合は `exampleuser` です。 `hippo` と`exampleuser`はどちらも`postgres.yaml`に設定されています。これについては、[こちら](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/tutorial/user-management/)の公式ドキュメントに詳しく書かれています。

#### service.yaml
このファイルには `service` の設定が含まれています。ここでは、APIと同じように `postgresql-101` というサービス名にします。

### docs
このフォルダには、あなたがご覧になっているこれらのドキュメントが含まれています。

### src
ここには、実際のAPIのソースコードが格納されています。かなり標準的なASP.NET CoreのAPIが含まれています。実際に、ご覧になってください。ただ、APIを起動する際に、このコードがここにある一方で、この特定のコードを使用しているわけではないことに留意してください。 `api.yaml` ファイルを調べたときに表示されたartifactory上のコンテナイメージを使用しているのです。

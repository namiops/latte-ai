# Introduction
このチュートリアルでは、Secure KVS の基本的な使い方を説明します。Go で書かれた TodoAPI のサンプルが付属しています。このチュートリアルの終わりには、Secure KVSに接続するAPIが実行されているはずです。また、CouchDBインスタンスを表示することにより、暗号化された値を見ることができるようになります。

このチュートリアルは、Secure KVS を用いたアプリケーションをローカルでテストしたい人のためのものです。Agora プラットフォームにアプリケーションを導入する場合は、Secure KVS  [onboarding process](https://developer.woven-city.toyota/docs/default/component/steelcouch/onbaording/) を参照してください。

## What is Secure KVS
Secure KVSは、Agoraプラットフォーム上にデータを安全に保存するための社内ソリューションです。Secure KVSはSteelcouchを使用してデータを暗号化し、そのデータをCouchDBのインスタンスに格納します。

!!!Warning
    Secure KVSには、常にSteelcouchエンドポイントからアクセスすることを想定しています。

## What is Steelcouch?
Steelcouchは、（アプリケーションから見て）CouchDBインスタンスの手前にある暗号化プロキシです。Secure KVSでは、これを利用してデータを暗号化して保存しています。CouchDBにHTTPコールをするのと同じやり方で、SteelcouchにHTTPコールを行います。普通にCouchDBのAPIを呼び出すのと同じように、SteelcouchのAPIにHTTPのリクエストを行います。

例えば、特定のデータでSteelcouchにPUTリクエストを出すと、Steelcouchはそのデータを受け取り、それを暗号化します。その後、後方のCouchDBインスタンスに渡され、暗号化された文字列として格納されます。そして、GETリクエストを出すと、SteelcouchはCouchDBからデータを取得し、それを復号して返します。

Steelcouchはまだベータ版であり、完全にCouchDBのすべての機能をサポートしていません。Steelcouchは、APIコールをサポートしていない場合、そのAPIコールをCouchDBに渡します。データベース中のデータが暗号化されているため、このAPIコールは期待どおりに動作しないかもしれません。

サポートされているCouchDB APIコールの情報は、[こちら](https://developer.woven-city.toyota/docs/default/component/steelcouch/onbaording/#supported-couchdb-apis-by-steelcouch)を参照してください。

## What is CouchDB?
CouchDBは、オープンソースのNoSQLデータベースです。JSONを利用してデータを保存し、HTTP APIでアクセスすることができます。CouchDBは、オープンソースのNoSQLデータベースです。JSONを利用してデータを保存し、HTTP APIでアクセスすることができます。Secure KVSは、データを格納するためにCouchDBインスタンスを使用します。あなたは必ずしもこのチュートリアルを利用するためにCouchDBについて多くを知る必要はありませんが、あなたがCouchDBのインスタンスを使って色々試してみることで、何が起こっているかをよりよく理解するのに役立つでしょう。公式の [Getting Started](https://docs.couchdb.org/en/3.2.2-docs/intro/tour.html) をご参照ください。

### Supported CouchDB version
Steelcouchは、後方のCouchDBインスタンスを使用するため、SteelcouchはCouchDBのいくつかのバージョンをサポートしていない可能性があります。

なお、このチュートリアルを執筆時点（2022/09/21）では、Steelcouchは以下のバージョンのCouchDBと動作します。

```
CouchDB Version 3.2.2
CouchDB Version 3.2.1
CouchDB Version 3.2.0

CouchDB Version 3.1.x
```

公式の[release note](https://docs.couchdb.org/en/stable/whatsnew/index.html)をご覧ください。

## Moving on to Agora clusters
このチュートリアルを終えて、Agora プラットフォーム上での利用を始めたい場合、Secure KVS  [onboarding section](https://developer.woven-city.toyota/docs/default/component/steelcouch/onbaording/) を読んで、Agora プラットフォームにデータベースをリクエストする手順を学ぶことができます。

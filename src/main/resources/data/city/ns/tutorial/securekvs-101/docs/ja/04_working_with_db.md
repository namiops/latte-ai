# Working with DB
さて、いよいよSecure KVSの動きを見てみましょう。

## Looking at the data through Steelcouch
データを表示するためにCouchDBに付属のダッシュボードを使用することができます。 ご存じのように、Steelcouchは単なるプロキシなので、SteelCouchで処理できないリクエストはそのままCouchDBに転送されますし、処理できるものに関してはSteelCouchが対応します。両方の組み合わせることで、CouchDBのダッシュボードを使用して復号されたデータを表示することができます。

まず、ユーザー名とパスワードを見つける必要があります。以前のHelm Chartにあった便利なコマンドを利用します。

```shell
$ kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminUsername}' | base64 -d
admin
$ kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminPassword}' | base64 -d
{some randomly generated string}
```

それにアクセスするために、Steelcouchのサービスをこのようにポートフォワードする必要があります。

```shell
$ kubectl port-forward service/steelcouch-agora-kvs-test-secure-kvs -n securekvs-101 5984:5984
```

これで、 `localhost:5984/_utils/` のリンクに行き、以前に見つけた認証情報を使ってログインすることができます。ここから、`todo-items` のデータベースに行き、自分たちが作ったエントリーを見ることができます。例えば、以下のようなものです。

```json
{
  "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
  "Item": "this will be encrypted!",
  "CreatedAt": "2022-10-05T02:40:01.470738318Z",
  "_rev": "1-28ab6c558aae56604ea1808d8ee75a84"
}
```

## Seeing the encrypted data on CouchDB
上記の手順で学んだのと同じ原理で、暗号化された値も見ることができます。ただ、CouchDBのエンドポイントサービスをポートフォワードするだけです。 

```shell
kubectl port-forward service/db-agora-kvs-test-secure-kvs -n securekvs-101 5984:5984
```

ここで、`localhost:5984/_utils/`にアクセスしてログインすると、コールはSteelcouchを経由しないので、データは単なる暗号化された文字列であることがわかります。以下のような感じです。

```json
{
  "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
  "_rev": "1-3aefb31b23a312d83454152d879d3ac4",
  "jwe": "eyJraWQiOiJublExenlMRW9LN2lmSW53bHNhLXBWMHpoSFJ5Uk9CeFZRbkt5UFhMakN3IiwiZW5jIjoiQTI1NkdDTSIsIl9vcmlnaW4iOiJ0ZXN0IiwiX2RvbWFpbiI6ImVwb2NoLTIwMjIwNDAxIiwiX2hhc2giOiJTSEEyXzUxMiIsIl9kaWdlc3QiOiJKOGRHY0syM1VIWDYwRmpWenE5N0lNVG5lR3lEdXVpakwySnZsNEt2Tk1talBDQkc3MkQ5S25oNDAzamluLXlGR0FhNzJhWjRlUE9wOGMya2d3ZGpfUSIsImFsZyI6ImRpciJ9..wLyiYIu1IyGD3g3C.g_VXiJQ5I-rQxdnXTGIkBSKYP4vQlXgz77zw31WPMmk35-qcHSzTN8WuCUMKHV2CUl9ghrdYIslnEd8NJJUsG9XgGY9P7Vlc7MKNzuNFNQ.uGbNwqF9oRX2Ai2UtEqCww"
}
```

これで、Secure KVSを利用するのに十分な情報を得ることができたと思います。
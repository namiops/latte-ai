# Adding data using the API
APIを通じてデータを追加し、後でデータベースを見たときに中のデータを見ることができるようにします。

そのためには、APIをポートフォワードして、アクセスできるようにする必要があります。APIサービスを使って、ポートフォワードしてみましょう。

```shell
$ kubectl port-forward service/securekvs-101 -n securekvs-101 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

現在、APIには3つのエンドポイントがあります。

* データを追加するには、`/TodoItem` で `POST` リクエストを実行します。

```shell
$ curl http://localhost:8080/TodoItem -X POST -d '{ "text": "this will be encrypted!" }'
```

* すべてのデータは、 `/TodoItem`で`GET` リクエストすることで見ることができます。

```shell
$ curl http://localhost:8080/TodoItem -X GET
[
    {
        "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
        "Item": "this will be encrypted!",
        "CreatedAt": "2022-10-05T02:40:11.140294566Z"
    }
]
```

* 特定の項目を表示するには、`/TodoItem/:id` に `GET` リクエストを実行します。

```shell
$ curl http://localhost:8080/TodoItem/d838d8ea-828d-4327-b99e-3b0339fece24 -X GET
{
    "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
    "Item": "this will be encrypted!",
    "CreatedAt": "2022-10-05T02:40:11.140294566Z"
}
```
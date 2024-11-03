# Personal data storage

## Run PDS in your local machine.

1. VSCodeをデバッグに使用する場合、以下のコンフィグが役に立つかもしれません。

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            "program": "${workspaceFolder}/main.go",
            "envFile": "${workspaceFolder}/.env",
            "args": []
        }
    ]
}
```

2. PDSは`docker compose`や`minikube`で実行することができます。以下の各セクションの説明を参照してください。 

3. `test_request/pds.postman_collection.json`でデータベースを作成します。
    - コレクションをPostmanにインポートします。詳細は[こちら](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/)。
    - コレクション変数を以下のように設定します。注：異なるデータベース名を選択したい場合は、`local-k8s/couch-db-deployment.yaml`と`local-k8s/pds-deployment.yaml`の環境変数を変更する必要があります。
      - `PDS_DATABASE_NAME=pds_database`
    - コレクションから新しいデータベースPDSの作成リクエストを実行します。

## Run local environment with K8s

1. minikubeの起動
`minikube start`

2. bazelのビルドとgazelle templatesの作成
`bazel build`
`bazel run //:gazelle`

3. イメージをdockerレジストリにプッシュ。
```bash
bazel run //ns/pds:push 
```

4. 必要なKubernetesリソースの作成
デプロイメントマニフェストで指定されているタグを使用していることを確認してください。
`kubectl apply -f local-env/k8s`

5. minikubeのIPを経由してPDSにアクセスするか、ローカルホストにポートを転送
```bash
$kubectl get services pds
NAME   TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
pds    NodePort   <IP ADDRESS>    <none>        8095:<NODE PORT>/TCP   147m
$ minikube ip
<MINIKUBE IP>
$curl <MINIKUBE IP>:<NODE PORT>
# OR
$kubectl port-forward svc/pds 8095:8095
Forwarding from 127.0.0.1:8095 -> 8080
Forwarding from [::1]:8095 -> 8080
$curl 127.0.0.1:8095
```

6. steelcouch経由でデータベースを作成するとよいでしょう。
```bash
$kubectl exec <PDS POD NAME> -- curl -X PUT http://admin:password@steel-couch:15984/pds_database
{"ok":true}
```

## Run local environment with docker compose

1. docker-compose upを実行

```bash
docker-compose -f local-env/docker/docker-compose.yaml up
```

## Test endpoints with Postman
PostmanはAPIを可視化し、テストケースとしても利用できます。

### Run sequential test 

#### Run test in local cluster

##### Pre condition
- PDSを以下のコマンドで実行する必要があります。
  `kubectl -apply -k ns/privacy/k8s`
  `kubectl -apply -k infrastructure/k8s/local/pds`

- ローカルクラスターマシンに「testkube」をインストールする必要があります。
   https://kubeshop.github.io/testkube/installing/

- Istioでtestkubeのネームスペースをインジェクトする必要があります。
   `kubectl label namespace testkube istio-injection=enabled --overwrite`

- デプロイメントポッドの再起動またはポッドの強制終了

```bash
$kube get deployment -n testkube
NAME                                   READY   UP-TO-DATE   AVAILABLE   AGE
testkube-api-server                    1/1     1            1           21h
testkube-dashboard                     1/1     1            1           21h
testkube-minio-testkube                1/1     1            1           21h
testkube-mongodb                       1/1     1            1           21h
testkube-operator-controller-manager   1/1     1            1           21h
```

- `kubectl rollout restart deployment -n testkube`

##### Run test in K8s cluster
注：現在、Istioのインジェクションに問題があり、OSSコミュニティでこの問題を解決しています。
https://github.com/kubeshop/testkube/issues/1761

1. ローカルクラスタにpdsの統合テストを作成
   `kubectl testkube create test --file ns/pds/tests/pds_contract_test.json --type postman/collection --name pds-test`

2. テストの実行
   `kubectl testkube run test`

3. 結果チェック
   `kubectl testkube get execution 6103a45b7e18c4ea04883866`

もし、SteelCouchが正常に動作していることを確認したい場合、
"steelcouch"に対して`local-env/test_request/steel_couch_api.json`でテストを実行することができます。さらに、以下のように"steelcouch"コレクションにダミーデータを作成します。
`newman run local-env/test_request/pds.postman_collection.json --folder create_resources`

#### Local machine test
「ローカル環境」でテストを実行する必要がある場合は、「newman」をインストールする必要があります。
注：PDS K8sのドメイン名は、テストでのPDSエンドポイントに使用されています。「ローカル環境」については、変更した方がいいかもしれません。

```bash
# TODO: テストスクリプトやpostmanコレクションは、このような場合に大いに役に立つのでしょうか？
$ npm install -g newman
# brew install newman
$ newman run tests/pds_contract_test.json
┌─────────────────────────┬───────────────────┬──────────────────┐
│                         │          executed │           failed │
├─────────────────────────┼───────────────────┼──────────────────┤
│              iterations │                 1 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│                requests │                 9 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│            test-scripts │                13 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│      prerequest-scripts │                11 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│              assertions │                 6 │                0 │
├─────────────────────────┴───────────────────┴──────────────────┤
│ total run duration: 440ms                                      │
├────────────────────────────────────────────────────────────────┤
│ total data received: 1.4kB (approx)                            │
├────────────────────────────────────────────────────────────────┤
│ average response time: 21ms [min: 5ms, max: 103ms, s.d.: 29ms] │
└────────────────────────────────────────────────────────────────┘
```

## Run PDS in the local cluster

## How to put test data for PDS (Temporary solution, to be removed)

1. 管理者認証情報を取得する

```bash
#  クライアントとしてCouchDBを使用（一時的な解決策）
$kubectl exec -it db-agora-kvs-pds-pds-secure-kvs-1 /bin/bash -n pds

# ユーザー名とパスワードの確認
$echo $COUCHDB_PASSWORD
<adminPassword>
$echo $COUCHDB_USER
<adminUser>

# データベースが存在しない場合、SteelCouchを通じてデータベースを作成
# DB名を変更する場合は、pds.yamlファイルにも変更を加えてください
$curl -X PUT http://$COUCHDB_USER:$COUCHDB_PASSWORD@steelcouch-agora-kvs-pds-pds-secure-kvs:5984/pds_database

{"ok":true}
```

2. PDSへのデータ投入、データ取得

```bash
# SteelCouchを通じてダミーデータを追加
$curl -X PUT http://$COUCHDB_USER:$COUCHDB_PASSWORD@steelcouch-agora-kvs-pds-pds-secure-kvs:5984/pds_database/5ebc50b8-96ad-401d-a470-c487bddba4fb__vehicle -d '{".color":"#3f304b","registrationNumber":0,"motorIdNumber":0,".holder":"5ebc50b8-96ad-401d-a47^Cc487bddba4fb",".document_type":"vehicle",".created":1656492147}'
{"ok":true,"id":"5ebc50b8-96ad-401d-a470-c487bddba4fb__vehicle","rev":"1-2546bd4347b003430b4291b25b36d6e2"}

# SteelCouchを通じてダミーデータを追加
$curl -X PUT http://$COUCHDB_USER:$COUCHDB_PASSWORD@steelcouch-agora-kvs-pds-pds-secure-kvs:5984/pds_database/5ebc50b8-96ad-401d-a470-c487bddba4fb__workaddress -d '{ "_id": "912f88de-0b61-4e7c-93b0-a85272c140ed__workaddress", ".holder": "912f88de-0b61-4e7c-93b0-a85272c140ed", ".document_type": "workaddress", "attributes": { "state": "Tokyo", "city": "Nihonbashi", "street": "3-2-1 Muromachi Mitsui Tower", "other": "16F" }, ".attributes": { ".country": "Japan" }, ".created": "1648801800000", ".updated": "1652149222000" }'
{"ok":true,"id":"5ebc50b8-96ad-401d-a470-c487bddba4fb__vehicle","rev":"1-2546bd4347b003430b4291b25b36d6e2"}

# PDSへのアクセス、Woven IDリストの入手
$curl pds/holderlist
```

## TODO

1. PoCサーバーの準備ができたら、docker envを削除します。 `local-env/docker`
2. コーナーケースを含むテストケースをさらに追加します。
3. PDS APIのテストケースの作成します。 `local-env/test_request/pds.postman_collection.json`
4. データを保存する構造をCouchDBに変更します。
   現在、予約済み属性とユーザーの動的属性は同じレベルにあります。例えば、updated_date、=holder、work_addressなどです。
   このような状況で動的属性をシリアライズするには、少し醜い静的コードが必要です。
   `client/couch_client.go:240`
   ユーザー属性を1階層ネストされたjson構造に格納することで、より構造化され、シリアライズが簡単になると思います。
   ```json
   {
    ".updated": "",
    "attributes": [
      {
        "encrypted_attr": "encrypted_value"
      },
    ],
    ".attributes": [
      {
        "plain_attr": "plan_value"
      },
    ],
   }
   ```
   しかし、一つ問題があるとすれば、属性のキーや値で検索する必要がある場合、ネストした検索が遅くなる可能性があることです。
5. 特に必須属性については、バリデーションロジックを実装する必要があります。
   - 予約済み属性値
   - ドキュメント作成に必要なCouchClient側の必須値
6. パッケージ名を更新し、プロジェクトの構造を変更することで、利便性を向上させます。
   - `message` パッケージは `documents.go`と一致しません。
   - `server` フォルダーに `pds` パッケージが保管されます。

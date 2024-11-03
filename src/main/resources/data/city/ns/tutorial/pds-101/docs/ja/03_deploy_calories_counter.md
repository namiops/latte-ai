# Step 2: Deploy our app

pds と privacy サービスがクラスタで動作するようになりましたので、pds を使ってデータにアクセスする簡単な go アプリをデプロイしてみましょう。

## Before deployment

先に進む前に、私たちのサービス導入に伴う実用的な観点から、プライバシーサービスについて説明しましょう。以下のコンフィグマップはすでに存在しませんが（Data Privacy Serviceの実用的なバージョンより前にデータを入力するために使用されていたため）、DPS内部でサービス、data-kinds、ユーザー情報がどのように相互作用するかを説明するのに役立ちます。

```yaml
  config.yaml: |
    "_clientMap":
      birthday-service-ai-client: birthday-service
      birthday-service-frontend: birthday-service
      oidc-rp: demo-service
      calorie-counter: weightloss-service #NEW
    "_dataMap":
      birthday:write: personal-details
      birthday:read: personal-details
      personal-details:read: personal-details
      weightloss_data:read: personal-details #NEW
      weightloss_data:write: personal-details #NEW
    "_userConsentMap":
      tal:
        personal-details:
          - birthday-service
      atsushi:
        personal-details:
          - birthday-service
      healthy_user: #NEW
        personal-details: #NEW
          - weightloss-service #NEW
```

clientMap は、クライアント名とそれに関連するサービスを含みます。dataMapは、data-kindsにマッピングされた個々のリソースを含みます。その data-kind の権限を持つリソースは、マッピングに従って、クライアントがそのデータを読み書きするためにアクセスすることができます。_userConsentMapはwoven-idと複数のdata-kinds、そしてそれらがマッピングされるサービスを備えています。

このデータは、DPS が現在機能しているため、起動時にデータが入力されるのではなく、DPS のエンドポイントに POST することで追加されます。

```shell
curl -X POST http://data-privacy-admin.data-privacy/admin/service_mapping/weightloss-service/calorie-counter
curl -X POST http://data-privacy-admin.data-privacy/admin/data_mapping/personal-details/weightloss_data:read
curl -X POST http://data-privacy-admin.data-privacy/admin/data_mapping/personal-details/weightloss_data:write
curl -X POST http://data-privacy-fe.data-privacy/consents/user/healthy_user/kind/weightloss_data:read/party/weightloss-service
curl -X POST http://data-privacy-fe.data-privacy/consents/user/healthy_user/kind/weightloss_data:write/party/weightloss-service
```

しかし、私たちのセットアップでは、これはgolangによって実現されています。以下のセットアップエンドポイントは、これらのコールのセットアップを担当します。

## Deploy Calorie Counter

まず、**project root** に移動しましょう。

```shell
$ cd cityos/ns/tutorial/pds-101/calorie-counter
```

Calorie Counterアプリケーションをデプロイしてみましょう。

```shell
$ kubectl apply -f ./kubernetes
namespace/calorie-counter created
deployment.apps/calorie-counter created

$ kubectl get pods -n calorie-counter
NAME                              READY   STATUS    RESTARTS   AGE
calorie-counter-cf74ddcfd-sz5ld   0/1     Running   0          11s
```

Podが `Running`(起動)したら、メッセージを送信してみましょう。まず、 `port-forward` を使ってminikubeからサービスを呼び出します。Port-Forwardはフォアグラウンドにいるので、必ず別のシェルを開いておいてください。

```shell
$ kubectl port-forward <pod_name> -n calorie-counter 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

私たちのアプリは*非常に*シンプルで、ユーザーのデータ、この場合は1日に食べた分のカロリーやTDE（Total Daily Expenditure、基本的に1日に必要なカロリー数）を保存し、そのデータも取得することが可能です。また、もう一つのGETエンドポイントでは、その日のうちに食べた分のカロリーを取得することができます。また、サービスに必要な権限を最初に設定するために使用する、管理用エンドポイントも用意されています。

まず、管理用エンドポイントについて説明します。

```shell
$ curl -X POST http://localhost:8080/setup
```

PodにPOSTリクエストを送るために `curl` を送信しましょう。

```shell
$ curl -X POST http://localhost:8080/tde -d '{"tde":"1900","caloriesEaten":"1500"}'
```

また、いくつかのGETリクエストも同様です。

```shell
$ curl -X GET http://localhost:8080/tde
{"caloriesEaten":"1500","created":"0","document_type":"weightloss_data","holder":"healthy_user","id":"healthy_user__weightloss_data","tde":"1900","updated":"0"}

$ curl -X GET http://localhost:8080/caloriesLeft
You can still eat 400 calories today.
```

`別のユーザーのデータを取得したい場合はどうすればよいでしょうか？あるいは同じユーザーから別のデータを取得したい場合は？もし私が別のクライアントで、同じユーザーのデータにアクセスしようとしたら？`

これらはすべて、アクセスしているURLや、Webサーバに渡された認証によって制御されています。

[ここ](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/pds-101/calorie_counter/internal/http/gin.go)では、calorie_counterアプリのginサーバーを定義するために使用されたgoコードを見ることができます。healthy_userは、データを要求しているユーザーで、パスで証明されます。また、データの種類（weightloss_data、上のマップで定義したようなもの）も同様にパスに入っています。最後に、データにアクセスするクライアントを認証に記載します（weightloss-clientとして）。

最後に、追加した以前のアクセスを削除するための teardown エンドポイントがあります。

```shell
$ curl -X POST http://localhost:8080/teardown
```

## Congratulations

これで、PDSの概念的な理解、ローカルクラスタへの展開方法、PDS内のデータへのアクセス方法などの基本的な知識を得ることができました。

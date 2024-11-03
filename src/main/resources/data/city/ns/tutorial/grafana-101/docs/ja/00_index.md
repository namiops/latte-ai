# Grafana 101

Grafana 101へようこそ!

## What This Tutorial Covers

このチュートリアルでは、Agora が提供するサービスのメトリクスを可視化するための主要なツールである Grafana を紹介します。

また、サンプルサービスをモニタリングするためのGrafanaダッシュボードを設定するハンズオンも体験していただきます。

## Grafana Quickly

[Grafana](https://grafana.com/docs/grafana/latest/) は、Agoraにおいて Prometheus メトリクスを視覚化する第一の（primary）ツールです。このようにクールなダッシュボードを設定することで、各サービスの状況を把握することができます。

![Grafana Example Dashboard](./assets/graphana-ex-dashboard.png)

!!! Tip
    正確には、GrafanaはPrometheus以外にも様々な [data sources](https://grafana.com/docs/grafana/latest/datasources/#data-sources) に対応していますが、本稿執筆時点ではAgoraにおけるGrafanaはPrometheusにのみ接続しています。

公式ドキュメントの [Dashboard feature overview](https://grafana.com/docs/grafana/latest/dashboards/use-dashboards/#dashboard-feature-overview) のセクションをご覧いただくことで、使われているアイコン/ウィジェットが何のためにあるのか確認できます。

## Grafana Hands-on

このページでは、Agora プラットフォームでサンプル Grafana ダッシュボードを作成する方法を説明します。

### Pre-requisites For this Hands-on

* **Kubectl**
  * Windows版、Mac版、Linux版のインストール方法は[**こちら**](https://kubernetes.io/docs/tasks/tools/) をご覧ください。
  *  [dev env](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/dev)へのアクセスを設定します。
* Access to [Agora Grafana](https://observability.cityos-dev.woven-planet.tech/grafana/)
  * アクセス権がない場合は、Slackの#wcm-agora-team-amaで「Grafanaにアクセスしたい」とご連絡いただければ、アクセス権を付与します。

### Log in to Grafana

https://observability.cityos-dev.woven-planet.tech/grafana/ に移動し、*Or sign in with* の下にある `WovenPlanet` ボタンを押してください。

### How to Set up A Dashboard for Your App in Grafana

!!! Warning
    備考： Agora チームは Grafana で ダッシュボードを設定する方法についてメジャーアップデートを計画しており、これがアップデートされると Agora チームは各チームにご自身で ダッシュボード設定を移行するように要請する必要があるかもしれません。

以下、全体の流れを説明します。

1. 一時的にダッシュボードを手動で設定し、そのJSONデータを取得します。
2. データの永続性と移植性を高めるために、ダッシュボードの設定をコード化します。

では、それぞれの手順を詳しく見ていきましょう。

#### Configure a Temporary Dashboard Manually to Get the Dashboard JSON

まず、サンプルとしていくつかのパネルを持つダッシュボードを作成します。ダッシュボードはあくまで一時的に設定し（保存せずに破棄し）、ダッシュボードがどのように設定されているかを記述したJSONを取得します。次にそのJSONをリポジトリに登録し、異なる環境間での永続性と移植性を確保できるようにします。

!!! Danger
    一時的に作成されたダッシュボードは永続的ではないので、Agoraが提供するGrafana Podが再起動するとダッシュボードは消えてしまいます。そのため、必ずすべての手順に従ってダッシュボードを永続化するようにしてください。

Podのステータスパネルの追加:

1. 左のサイドバーの `+` ボタンにカーソルを合わせ、 `Dashboard`をクリックします。
2. `Add a new panel`　をクリックします。
3. `Metrics browser` のテキストボックスに、`kube_pod_status_phase{namespace="httpbin",pod=~"httpbin-.+",phase="Running"}`と入力します。 `Shift+Enter` でグラフを見ることができます（`1` は良い状態であることを意味します）。
4. `Metrics browser`の下にある `Instant`スイッチをオンにします。
5. 右側の設定を以下のようにします（上から順に）:
    1.  `Time series` を `Stat`　に変更します。
    2. タイトルを `Pod Status`　に設定します。
    3. 記述を `Pod status for my app.`　に設定します。
    4. `Add value mappings`:
        - 1 => Healthy, Green
        - 0 => Unhealthy, Red
6. 画面右上の `Apply` ボタンを押してください。

!!! Tip
    実際の使用例では、ご自身のアプリに設定する際に、ネームスペースとPodクエリを変更してください。

!!! Tip
     適用後に設定を編集したい場合は、パネルタイトル（ここでは`Pod Status` ）⇒ `Edit`をクリックします。

CPU使用率パネルの追加:

1. 上部のアイコンの中から、 `Add Panel` ボタンを見つけて、クリックします。
2.  `Add a new panel` をクリックします。
3.  `Metrics browser` のテキストボックスに、 `rate(container_cpu_usage_seconds_total{namespace="httpbin",pod=~"httpbin-.*"}[5m])`　と入力します。
4. 右側の設定で（上から順に）:
    1. タイトルを `CPU Usage`　に
    2. 記述を `CPU usage for my app.`　に設定します。
5. 画面右上の `Apply` ボタンを押してください。

!!! Tip
    1つのPodに複数の行が表示されるのは、Pod内で複数のコンテナが動作しているため（サイドカーとして）、その使用量の合計もあわせて表示されています。
    ご自分のアプリのデータだけを表示したい場合は、ラベルフィルターに `container="<your-app-name>"` を追加してください。
    使用量の合計だけを表示したい場合は、ラベルフィルターに `container=""` を追加してください。

メモリ使用状況パネルの追加:

1. 上部のアイコンの中から、 `Add Panel` ボタンを見つけて、クリックします。
2.  `Add a new panel`.をクリックします。
3.  `Metrics browser` のテキストボックスに、 `container_memory_working_set_bytes{namespace="httpbin",pod=~"httpbin-.*"}`　と入力します。
4. 右側の設定で（上から順に）:
    1. タイトルを `Memory Usage`　に
    2. 記述を `Memory usage for my app.`　に変更します。
5. 画面右上の `Apply` ボタンを押してください。

パネルを自由にドラッグしてください。最終的なダッシュボードは以下のような感じになります。

![Grafana Hands-on Dashboard](./assets/graphana-handson-dashboard.png)

JSONモデルの保存：

1. 上部のアイコンの中から、`Dashboard Settings` ボタンを見つけてクリックします。
2. 左側のナビゲータで `JSON Model` を選択し、JSONの内容をお使いのローカルコンピュータに一つのファイルとして保存します（ここでは`httpbin-dashboard.json` と名付けましょう）。

!!! Warning
    これは非常に重要なステップです。これを怠ると、上記のステップをもう一度やり直さなければなりません。

ダッシュボードを保存せずにGrafanaのブラウザタブを閉じます。

#### Codify your Dashboard Configuration for Persistence and Portability

ここでは、Agora Kubernetes クラスタに ConfigMap を作成して、ダッシュボード JSON を使用できるようにします。

!!! Note
    実際のユースケースでは、AgoraでKubernetesリソースを作成する際に、リポジトリに変更を適用してFluxが自動で差分を反映します（慣れていない方にはこちらが非常にお勧めのコンテンツです：[Deployment Application on Agora](https://developer.woven-city.toyota/docs/default/component/agora-deployment-tutorial/03_deployment/))。このチュートリアルでは、わかりやすくするために、アドホックリソースを手動で一時的に作成する手順を説明します。

!!! Note
    手動で作成したダッシュボードをそのまま使うのではなく、このようなワークフローを設計しているのは、他のInfrastructure as Codeの取り組みと同様に、再現性を重視しているからです。エラーが発生しがちな手作業に頼ることなく、設定を異なる環境に移植することができます。

1. PCの任意の場所に `my-httpbin-dashboard` というフォルダを作成します。
2. 先ほど作成した `httpbin-dashboard.json`　をフォルダの下に置きます。
3. フォルダの下に、以下のテキストで `kustomization.yaml` を作成します。
    ```yaml
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    namespace: tutorial # In real case, put <your-namespace>
    configMapGenerator:
    - name: httpbin-grafana-dashboard
        files:
        - httpbin-dashboard.json
    commonLabels:
    grafana_dashboard: "1"
    commonAnnotations:
    grafana_folder: tutorial # In real case, put <your-namespace>
    ```
4. 以下のコマンドを実行します。
    ```sh
    cd <path-to>/my-httpbin-dashboard
    
    # Check the manifest
    kubectl apply -k . --dry-run=client -o yaml
    
    # Apply the manifest
    kubectl apply -k .
    ```
5. 1～2分待って、https://observability.cityos-dev.woven-planet.tech/grafana/dashboards、あなたのダッシュボードがそこにあることを確認してください。

!!! Danger
    実際に使用する際には、`grafana_folder` が所有するネームスペースに設定されていることを確認してください。フォルダ名とダッシュボード名が矛盾していると、他のチームが所有している既存のダッシュボードを上書きしてしまう可能性があります。この点については、予防策を講じる予定ですが、現在はまだ実施していません。

よくできました。これでダッシュボードの作成は終了です。前述の通り、公式ユースケースではあなたのリポジトリで変更を確認することになります。
では、一時的に作成したリソースをクリーンアップしてみましょう。

1. `kubectl delete -k .`

### How to Set up Alerts

申し訳ございませんが、アラートの設定は現時点では正式にサポートされていません（近日中に設定される予定です）。もし、今すぐ必要な方は、#wcm-agora-team-amaまでご連絡ください。

## Where to Go Next

- [Observability 101 -> Where to Go Next](https://developer.woven-city.toyota/docs/default/Component/observability-tutorial/#where-to-go-next) のセクションに戻り、続きの文章をお読みください。

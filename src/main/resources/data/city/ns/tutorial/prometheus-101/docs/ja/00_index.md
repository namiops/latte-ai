# Prometheus 101

プロメテウス101へようこそ！

## What This Tutorial Covers

このチュートリアルでは、Agoraが提供するサービスのメトリクスを収集するための主要なツール群の一つ、[Prometheus](https://prometheus.io/) についてご紹介します。

## Prometheus Quickly

[Prometheus](https://prometheus.io/)は、イベントの監視やアラートに使用されるフリーソフトウェアです。HTTPプルモデルで構築された時系列データベース（高次元対応）にリアルタイムでメトリクスを記録し、柔軟なクエリーとリアルタイムのアラートを提供します。以下のアーキテクチャは、その全体構成を示しています（ [the official docs](https://prometheus.io/docs/introduction/overview/)より抜粋）。

![Prometheus Architecture](./assets/prom-arch.png)

Agoraチームは、Prometheusと他のすべての関連するバックエンドを設定し、それらがサービスのメトリクスを収集し、バックグラウンドでクエリ可能になるようにします。

### PromQL

[PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/) は、Prometheusに特化したクエリ言語です。Grafanaでモニタリングダッシュボードを定義したり、アラート基準を設定したりする際に使用します。Prometheusは、クエリを素早く試し、視覚化するための基本的なWebUIも提供しており、これはすぐにご覧いただけます。

Prometheusスタックはクエリ言語を理解したときに威力を発揮するため、上記のPromQL公式ドキュメントをご覧になることをお勧めしますが、ここではいくつかのクエリ例を紹介し、その過程で基本概念を説明します。

これらの例では、この外部サービス [Promlab's demo env](https://demo.promlabs.com/graph)を利用することができます。

#### PromQL Example: Gauge

まずはシンプルに、次のように入力してみましょう。

`node_filesystem_avail_bytes`

このクエリをPrometheus WebUI内の*Expression*テキストボックスに入力します。これはすでに有効なPromQL式で、ディスクの空き容量を取得するためのクエリの1つです。ファジー検索でオートコンプリートの候補が出ていることに気付いたと思いますが、これは非常に便利です（Ctrl+Spaceを押して強制的に表示させていない場合）。`Enter` キーを押してクエリを実行し、`Graph`タブに切り替えます。すると、このように表示されるはずです。

![Filesystem query result](./assets/prom-filesystem-result.png)

グラフにいくつかの線が表示されるはずです。これらの線はすべて個々の [**metric**](https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels) （この場合は`node_filesystem_avail_bytes` ）についてであり、各線は同じ[**labels**](https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels)のセットを持つ [**samples**](https://prometheus.io/docs/concepts/data_model/#samples) の時系列に対応しています。特定のタイムスタンプに垂直線を引くと、複数のサンプルが得られるはずです（グラフに複数の線があるため）。このサンプルセットを [**instant vector**](https://prometheus.io/docs/prometheus/latest/querying/basics/#expression-language-data-types)と呼びます。

凡例を見てみましょう。 `device`, `fstype`, `instance`, `job`,  `mountpoint` は、すべてメトリクスに付けられたラベルです。以下の方法で、クエリ結果をラベルの値でフィルタリングすることができます。

- `node_filesystem_avail_bytes{device="/dev/vda1"}`
- `node_filesystem_avail_bytes{device="/dev/vda1",mountpoint="/"}` (複数のラベルで絞り込む)
- `node_filesystem_avail_bytes{device=~"/dev/.+"}` (正規表現の使用)
- `node_filesystem_avail_bytes{device!="/dev/vda1"}` (否定)

これをデモで試してみると、出来上がったグラフの線が少なくなり、入力中にエクスプローラが可能なラベル値の候補を再び表示してくれることに気がつくでしょう。

[functions](https://prometheus.io/docs/prometheus/latest/querying/functions/)を使って、時系列に対するいくつかの演算を行うことができます。

`sum(node_filesystem_avail_bytes)`

これは、全てのラベルに共通する`node_filesystem_avail_bytes`メトリック値の合計値を提供します (実際には何を意味するかはあまり気にしないでください。ここでは、メトリックに対していくつかの[functions](https://prometheus.io/docs/prometheus/latest/querying/functions/) を適用できる構文上の例を示しているだけです)。

また、 [operators](https://prometheus.io/docs/prometheus/latest/querying/operators/)を使って複数のメトリクスに対して演算を行うこともできます。例えば、ファイルシステムの利用可能領域のパーセンテージを取得したいとします。次のように入力します。

`node_filesystem_avail_bytes / node_filesystem_size_bytes * 100`

 `node_filesystem_avail_bytes`の最初のクエリ結果と同じ行数であることに注意してください。このことから想像できるように、`/` (divided by) operatorは、全く同じラベルを持つベクトルのペアに適用され、それぞれのペアの結果のベクトルを作成します。 (例： `node_filesystem_avail_bytes{device="/dev/vda1", fstype="ext4", instance="node-exporter:9100", job="node", mountpoint="/"}` マッチし、 `node_filesystem_size_bytes{device="/dev/vda1", fstype="ext4", instance="node-exporter:9100", job="node", mountpoint="/"}` そして、出来上がったのが `{device="/dev/vda1", fstype="ext4", instance="node-exporter:9100", job="node", mountpoint="/"}` グラフにした場合).
Operatorが一致するベクトルを見つけようとする方法を [**vector matching**](https://prometheus.io/docs/prometheus/latest/querying/operators/#vector-matching)と言います。

`node_filesystem_avail_bytes`のように、単一の数値で任意に上下できるメトリックタイプを[**Gauge**](https://prometheus.io/docs/concepts/metric_types/#gauge)と呼びます。

#### PromQL Example: Counter

Prometheusが提供するもう一つの指標は [**Counter**](https://prometheus.io/docs/concepts/metric_types/#counter)で、単調に増加する単一のカウンターを表す累積指標であり、その値は増加するか、再起動時にゼロにリセットされるだけです。

カウンターは、[Range Vector Selectors](https://prometheus.io/docs/prometheus/latest/querying/basics/#range-vector-selectors) といくつかの関連するfunctionsで慣用的に使用されます。例を見てみましょう。

`node_cpu_seconds_total`と入力すると、CPU時間の合計が表示されます。各行が単調に増加しているのがわかります。

![CPU query result](./assets/prom-cpu-result.png)

!!! Tip
    もし、全てがフラットに見える場合は、凡例の一つをクリックして、一つの時系列だけを表示させてみてください。

単調に増加するグラフは分かりにくいかもしれません。CPUの総使用時間ではなく、CPU負荷のスパイクを表示したいと思うかもしれません。スパイクは2つのタイムスタンプでの値の差が大きいことを意味するので、1）その2つのタイムスタンプ間の時間長を指定し、2）その値の差を取る必要があります。以下のクエリは、私たちが望むことを実行してくれます。

`increase(node_cpu_seconds_total[5m])`

`[5m]` （mは分）の部分は1）に対応し、`increase` 機能は2）に対応しています。時間範囲を変えて、何が起こるか自由に試してみてください。

#### PromQL Example: Histogram

[**Histogram**](https://prometheus.io/docs/concepts/metric_types/#histogram) は、もう一つの強力なメトリクスタイプです。これを使用して、「直近 5 分間のリクエストレイテンシの 90 パーセンタイルは？」といった質問に答えます。デモ環境で以下のクエリを入力してみてください。

`histogram_quantile(0.9, rate(demo_api_request_duration_seconds_bucket{path="/api/foo"}[5m]))`

![Histogram query result](./assets/prom-histo-result.png)

#### How to Know the List of Metrics?

メトリック名のリストはどうやって知るのでしょうか？それは、クラスタにインストールされている [exporters](https://prometheus.io/docs/instrumenting/exporters/) に依存するため、やや難しいです。この文章を書いている時点では、[Node exporter](https://github.com/prometheus/node_exporter) と [kube-state-metrics](https://github.com/kubernetes/kube-state-metrics) がインストールされていますので、それらのドキュメントを確認するのが良いでしょう。

この質問に答えるもう一つの理由は、Prometheus WebUIまたはGrafanaで式テキストボックスに入力するだけで、オートコンプリートのためのかなり良いファジィな提案を提供してくれるからです。

参考までに、Kubernetes Podのモニタリングに便利なクエリ例をご紹介します。

- **Detecting Down Pods**: `kube_pod_status_phase{namespace="<your-namespace">,pod~="<app-name>-.+",phase="Running"}`
- **Kubernetes ReplicasSet mismatch**: `kube_replicaset_spec_replicas != kube_replicaset_status_ready_replicas`
- **CPU Usage**: `rate(container_cpu_usage_seconds_total{namespace="<your-namespace">,pod~="<app-name>-.+"}[5m])`
- **Memory Usage**: `container_memory_working_set_bytes{namespace="<your-namespace">,pod~="<app-name>-.+"}`

## Where to Go Next

- [Observability 101 -> Where to Go Next](https://developer.woven-city.toyota/docs/default/Component/observability-tutorial/#where-to-go-next) のセクションに戻り、続きをご参照ください。

## Further Reference

- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [Awesome Prometheus alerts](https://awesome-prometheus-alerts.grep.to/): PromQLの良い例、実用的な例を示しています。

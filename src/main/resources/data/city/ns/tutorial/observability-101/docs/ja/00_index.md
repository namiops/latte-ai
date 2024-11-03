# Observability 101

Observability 101へようこそ!

## What This Tutorial Covers

このチュートリアルは、システムの状態を理解し、問題を診断し、より信頼性とパフォーマンスの高いサービスを構築するために、 Observabilityに関する基本的な概念と、AgoraでObservabilityを利用するための実用的なステップを提供することを目的としています。

## What is Observability and Why is it Important?

> Observability（観測可能性）とは、あるシステムの外部出力に関する情報から、そのシステムの内部状態をいかにうまく推測できるかを示す指標です。([Wikipedia](https://en.wikipedia.org/wiki/Observability))

`observability` とは、制御理論で生まれた用語をIT業界に応用したもので、一般的にシステムの出力するデータ（ログ、メトリクス、トレースなど）をもとに、エンジニアがシステムの現在の状態、特にその健康状態（健全性）をいかに理解できるかを意味しています。

!!! Tip
    `observability` という言葉は、一般に `o11y`と略されます。

Observabilityが重要なのは、複雑なシステムをより良くコントロールできるようにするために必要だからです。
現代のソフトウェアシステムは、分散したチームによって開発され短期間で納品されており、これまで以上に複雑化しています。Woven Cityのサービスも例外ではありません。
適切なツールがなければ、このような複雑なシステムの内部で何が起こっているのかを明らかにすることは、ほとんど不可能になりました。問題が大きくなるまで気づかないこともありますし、気づいたとしても何が原因かを正確に突き止めるのは困難です。
高いObservability(観測能力)を持つことで、「なぜXが壊れているのか」「今、遅延の原因となっているものは何か」といった質問に効果的に答えることができ、エンジニアは問題を早期に発見、診断し、より信頼性とパフォーマンスの高いサービスを構築することができるようになるのです。

## Why use Agora for Observability?

システムをゼロから構築する場合、以下のことに対して独自のソリューションを構築しなければなりません。

* CPU使用率、メモリ消費量、ディスク使用量など、インフラストラクチャとアプリケーションの測定値を収集します。
* 運用している様々なサービスのログを集計し、検索可能にします。
* 測定値やログに基づき、特定の条件下でアラートを発します。
* あるリクエストがどのサービスを経由し、それぞれにどれだけの時間がかかったかを検証する方法を提供します。
* これらの重要な装置を高い信頼性で実現します。
* ...更に続きます

Agoraには、これらの利点はあらかじめ組み込まれています。サービスをデプロイすると、プラットフォームはすでにメトリクスを収集しています。
ダッシュボードをカスタマイズして、サービスを安定に稼働させるための指標を可視化する方法が用意されています。
ニーズに合わせてアラートのルールを設定することができます。
また、サービスの内部や外部の依存関係のトポロジーを可視化し、実行時に通過するトラフィックを表示し、トレースを調べて遅延を診断するために深く掘り下げることも可能です。

これらの機能を具体的にどのように利用するかは、このチュートリアルの後半で説明します。

## Observability Three Pillars

Observabilityで使用される主なデータ種類は、ログ、メトリクス、トレースです。これらを合わせて " Observability の3本柱 " と呼ぶこともあります。

> * ログ：ログはアプリケーション・イベントの、詳細な、タイムスタンプ付きの、完全かつ不変の記録であり、3つの形式：`プレーンテキスト`、`構造化データ`、`バイナリー`で提供されます。
> * メトリクス：メトリクス（時系列メトリクスとも呼ばれる）とは、ある一定期間におけるアプリケーションとシステムの安定性を測る基本的な指標です。例えば、あるアプリケーションが5分間にどれだけのメモリやCPUを使用したか、あるいは使用量が急増したときにどれだけの遅延が発生したかなどを測定します。
> * トレース：トレースは、UIやモバイルアプリから分散アーキテクチャ全体を経由してユーザーに戻るまで、すべてのユーザーリクエストのエンドツーエンドの「ジャーニー」を記録します。
>
> [IBM: What is observability?](https://www.ibm.com/cloud/learn/observability).

Agoraは、ログ収集のための [Loki](https://grafana.com/logs/) 、メトリクスのための [Prometheus](https://prometheus.io/) と [Grafana](https://grafana.com/grafana/) 、トレースのための [Jaeger](https://www.jaegertracing.io/) など、これらの柱に対応する一連のツールを提供します。

## Where to Go Next

- メトリクス収集プラットフォーム [Prometheus](https://prometheus.io/)については、 [Prometheus 101](https://developer.woven-city.toyota/docs/default/Component/prometheus-tutorial) をご覧ください。
- メトリクス可視化ツール [Grafana](https://grafana.com/grafana/)については、 [Grafana 101](https://developer.woven-city.toyota/docs/default/Component/grafana-tutorial) をご覧ください。
- ログ収集プラットフォーム[Loki](https://grafana.com/logs/)については、Loki 101 (TBA) をご覧ください。
- 分散トレースシステム [Jaeger](https://www.jaegertracing.io/)については、Jaeger 101 (TBA) をご覧ください。
- サービスメッシュ管理コンソール[Kiali](https://kiali.io/)については、Kiali 101（予定）をご覧ください。

## Further Reference

- [New Relic: What is observability?](https://newrelic.com/topics/what-is-observability): o11yとは何か、モニタリングとの違いは何か、誰が何の利益を得るのか、などをまとめた優れた記事です。
- [Splunk: What is observability? A Beginners Guide](https://www.splunk.com/en_us/data-insider/what-is-observability.html): こちらは、また少し長いコンテンツですが、優れたobservabilityの記事です。

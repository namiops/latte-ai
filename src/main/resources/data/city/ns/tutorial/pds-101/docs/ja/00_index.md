# Welcome

このチュートリアルは、パーソナルデータストア（PDS）を理解するためのものです。
Agoraは、PDSを複数のCityOSサービスからアクセスされる可能性のある、あらゆる個人情報の一元的な保管場所して提供されます。またこのチュートリアルでは、実際にPDSからデータを取得するために必要不可欠なConsentサービス、特に「データ保護サービス（DPS）」について最低限の説明をします。

## What This Tutorial Covers

このチュートリアルでは、PDSの概要とPDSに関連するConsentとDPSのサービスの簡単な概要について説明します。また、Kubernetes上で実行可能なPDSの最小限構成をインストールする手順も記載しています。最後に、PDSからデータを取得するための小さなサンプルアプリケーションを紹介します。

## Pre-requisites For The Tutorial

このチュートリアルを実行するには、以下のものがローカルマシンにインストールされている必要があります。

* **Minikube**
  * Windows版、Mac版、Linux版ともに、 [**minikube site**](https://minikube.sigs.k8s.io/docs/start/)にて操作方法が紹介されています。
  * Minikubeはそれを動作させるドライバが必要ですが、このチュートリアルでは**Docker**を使用しています。
    Dockerをドライバとして使用するには[**こちら**](https://minikube.sigs.k8s.io/docs/drivers/docker/)をご覧ください。
* **Kubectl**
  * Windows版、Mac版、Linux版のインストール方法は[**こちら**](https://minikube.sigs.k8s.io/docs/start/) をご覧ください。

このチュートリアルでは、**ソースプロジェクトのルート**で作業することを想定しています（以下の場所になります）。

```shell
/ns/tutorial/pds-101/
```

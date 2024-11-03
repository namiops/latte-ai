# Introduction

## Overview

このチュートリアルでは、minikubeにサービスをデプロイするために使用する、シンプルで基本的なファイルを紹介します。このチュートリアルの目的は、Agoraが他のチームにサービスを提供するために使用する、多くの'ビルディングブロック'の一つを提示することです。

### Who this tutorial is for

このチュートリアルの対象者は、KubernetesとDockerを初めて使う人、あるいはプロダクション環境においてどちらも使ったことがない人です。
これには、例えば、以下の方々が含まれます。

* 新規開発者
* クラウド開発に初めて触れる開発者
* Kubernetesの基本を知りたいプロジェクトマネージャー

### Files for this tutorial

このチュートリアルでは、この[ソースディレクトリ](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/minikube-101/)から作業を始めることを想定しています。

このディレクトリには、 [Nginx](https://www.nginx.com/)を使ってきわめてシンプルなHTTPページをデプロイするためのYAMLファイルセットが含まれています。  
具体的には、以下のようなものです。

* ネームスペースの定義 (`namespace.yaml`),
* サービスの定義 (`service.yaml`),
* デプロイメントの定義 (`deployment.yaml`).

これらのファイルは、自由にお使いください。

### Pre-requisites for this tutorial

このチュートリアルでは、minikubeといくつかのツールのセットアップが完了していることを前提にしています。

* **Minikube**
  * Windows版、Mac版、Linux版ともに、 [minikube site](https://minikube.sigs.k8s.io/docs/start/)にて、セットアップ手順をご覧いただけます。
  * Minikubeはバッキングドライバを必要としますが、このチュートリアルでは**Docker**を使用しています。
    手順は [こちら](https://minikube.sigs.k8s.io/docs/drivers/docker/)でご覧いただけます。
* **Kubectl**
  * Windows、Mac、Linuxのインストール方法は [こちら](https://minikube.sigs.k8s.io/docs/start/) をご覧ください。

### Note on Kubernetes terms

このチュートリアルでは、*ネームスペース、ポッド、サービス* など、Agora のコンテキストでよく使われるいくつかの Kubernetes 用語を紹介し、使用します。

また、Kubernetesドキュメントの関連部分へのリンクも追加しますが、これらは「参考文献」と考えて、興味のある方はご覧ください。Kubernetesのドキュメントを読むことは、このチュートリアルを進めていく上で必須ではありません。

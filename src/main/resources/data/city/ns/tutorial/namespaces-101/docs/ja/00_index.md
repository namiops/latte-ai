# Namespaces 101

このチュートリアルでは、Kubernetesクラスタにおけるネームスペースの動作の基本的な例を紹介します。ここではネームスペースについて、その効果的な使用方法と、ネームスペースがどのようにKubernetesリソースの整理に役立つかを説明します。

## Pre-requisites for This Tutorial

このチュートリアルでは、Minikubeのインスタンスを使用してコードを実行します。

* インストール方法はこちらをご覧ください。 [here](https://minikube.sigs.k8s.io/docs/start/)

なお、このチュートリアルはKubernetesの使用方法を理解していることが前提となります。

* Agoraチームでは、KubernetesとMinikubeの基本に関する簡単なチュートリアルを [Minikube-101](https://developer.woven-city.toyota/docs/default/component/minikube-tutorial) で提供しています。

## What a Namespace Is

 [Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) からの抜粋では

> Kubernetesでは、ネームスペースは一つのクラスタ内でリソースのグループを分離するためのメカニズムを提供します。リソースの名前はネームスペース内で一意である必要がありますが、ネームスペース間はその必要はありません。ネームスペースに基づくスコープは、ネームスペースのあるオブジェクト（Deployments, Serviceなど)にのみ適用され、クラスタ全体のオブジェクト（StorageClass、Nodes、PersistentVolumesなど）には適用されません。

ネームスペースは、主に複数のチームやプロジェクトにまたがる複数のユーザーがいる環境で使用されます。それらは、名前のスコープを提供するためのものです。Kubernetesでは、リソースはネームスペース内で固有の名前を付ける必要がありますが、ネームスペース**間では**その必要はありません。これは、複数のチームがネームスペースを超えて同様の名前のリソースを持つことができるということです。

Agoraでは、ネームスペースは主にサービスチームに対して彼らのアプリケーションのためのスペースを確保するために利用されます。各チームに対して、サービスを展開するための最適な方法を決定するためのスペースが提供されます。

## How to Read This Tutorial

このチュートリアルは[tutorial source directory](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/namespaces-101)で作業していることを前提にしています。
前提となるすべてのコマンドは、ソース・ルートから実行する場合と同じように従ってください。

```
cd ns/tutorial/namespaces-101
```

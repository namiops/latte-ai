# Getting Started
ここでは、実際のストレージソリューションとAPIを実行してみますのでお楽しみください。

## Prerequisites
このチュートリアルを実施するために、以下が必要となります。

* Minikube: [getting started](https://minikube.sigs.k8s.io/docs/start/) を参照してください。

* Helm:  Secure KVSをインストールするために、Helmチャートを使用します。Helmのインストール方法は、[official documentation](https://helm.sh/docs/intro/install/)で確認することができます。
* Deployment files: このプロジェクトのソースは、[こちら](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/securekvs-101) に格納されています。
* Access to Artifactory. 設定方法については、[Woven IT Documentation](https://docs.woven-planet.tech/engineering_software/artifactory/support/Docker-Registry/) をご参照ください。

## Installing
### 1. Creating a Namespace
はじめに、Secure KVS と API を配置する名前空間を作成します。 これを行うには、以下を実行するだけです。

```shell
$ kubectl create namespace securekvs-101
namespace/securekvs-101 created
```

### 2. Installing Secure KVS
Agoraストレージチームが、Secure KVSのインスタンスをインストールする際に役立つ、便利なヘルムチャートを作ってくれました。今回はこれを使用することにします。

Secure KVSをインストールするには、以下を実行します。

```shell
$ helm install test-secure-kvs https://artifactory-ha.tri-ad.tech:443/artifactory/helm/wcm-cityos/secure-kvs/secure-kvs-0.6.2%2B249255a1.tgz -n securekvs-101 --set "steelcouch.originDeterminationType=\{static: test\}"
NAME: test-secure-kvs
LAST DEPLOYED: Wed Oct  5 09:59:40 2022
NAMESPACE: securekvs-101
STATUS: deployed
REVISION: 1
NOTES:
Agora KVS is starting. Check the status of the Pods using:

  kubectl get pods --namespace securekvs-101 -l "app=agora-kvs-test-secure-kvs,release=test-secure-kvs"

You can get the CouchDB admin user's user name and password from the secret "db-agora-kvs-test-secure-kvs".

  Admin's user name
  kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminUsername}' | base64 -d

  Admin's user password
  kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminPassword}' | base64 -d
```

ここでは`--set` コマンドで `originDeterminationType` を上書きします。これは Secure KVS をローカルで実行できるようにするために行います。詳細は、 [How Steelcouch Works](https://developer.woven-city.toyota/docs/default/component/steelcouch/MANUAL/)を参照してください。

また、実際にデータを見たいときに参考になるコマンドも紹介されています。

### 3. Deploying the API
そして最後に、APIをデプロイする必要があります。これを行うには、ダウンロードしたソースコードがある場所にアクセスして、以下を実行します。

```shell
$ kubectl apply -k kubernetes
service/securekvs-101 created
deployment.apps/securekvs-101 created
```

これでAPIとServiceが作成されます。すべてが正しく実行されていることを確認しましょう。それには、Podをチェックします。

```shell
$ kubectl get pods -n securekvs-101
NAME                                                    READY   STATUS    RESTARTS   AGE
db-agora-kvs-test-secure-kvs-0                          2/2     Running   0          3h15m
db-agora-kvs-test-secure-kvs-1                          2/2     Running   0          3h15m
db-agora-kvs-test-secure-kvs-2                          2/2     Running   0          3h15m
securekvs-101-67c6594954-gvqbq                          1/1     Running   0          3h14m
steelcouch-agora-kvs-test-secure-kvs-59fd774cb4-t75k2   1/1     Running   0          3h15m
```

ご覧の通り、すべてのPodが起動しています。

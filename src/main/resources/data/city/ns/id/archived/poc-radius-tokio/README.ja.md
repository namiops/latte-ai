# RADIUS POC server
このコードは、ネットワークへの接続時に要求される認証の実現方法を検討するPoCのために作られたコードである。
このPoCはCity Networkを設計するICT teamと、Woven IDを提供するCity Platformによって行われる。両者の間を接続するRADIUS proxyサーバーを開発し、相互の接続方法を検証する。

## PoCの構成
ICT teamでは認証認可を管理するネットワークアプライアンスとしてCisco ISEの導入を予定している。このRADIUSサーバーはそのCisco ISEからRADIUSプロトコルで認証リクエストを受け付け、City Platformが提供するWoven IDにOpen ID Connectプロトコルで認証問い合わせを行う。 
![arch](architecture.svg)
 
## サービスの起動方法（開発モード）
開発モードでは、docker-composeを利用してローカルマシンで下記の全てのサービスを起動する。
- RADIUSサーバー
- Keycloakサーバー
- RADIUSクライアント

1. プロジェクトディレクトリに移動する。
このREADMEが置かれているディレクトリがプロジェクトディレクトである

2. プロジェクトをbuildする
```
docker-compose build`
```
Rustのコンパイラを含むdocker imageをダウンロードしbuildするため、数分かかる。

3. プロジェクトを起動する
```
docker-compose up
```
Keycloakの起動に数十秒かかる。その後、Radius Serverが起動し、最後にRadius clientが起動する。

4. RADIUSクライアントの実行結果を確認する
しばらくすると、下記のような実行結果が表示される。
```
keycloak-server  | 16:22:18,596 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
keycloak-server  | 16:22:18,596 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
radius-server    | [2022-11-07T16:22:24Z INFO  poc_radius_tokio] serve is now ready: 0.0.0.0:1812
radius-server    | [2022-11-07T16:22:30Z INFO  poc_radius_tokio] Login suceeded.(user: alice)
radius-client    | connecting to radius-server
radius-client    | Sending authentication request
radius-client    | Access accepted
radius-client    | Attributes returned by server:
radius-client exited with code 0
```
RADIUSクライアントがRADIUSサーバーに認証リクエストを送信し、Accessに成功していることを確認できる。

## RADIUSサーバーへの接続
上記の起動手順を実行すると、localhostの1812ポートでRADIUSサービスがexposeしている。
Cisco ISEなどから接続する場合は、docker-composeを実行しているマシンのIPアドレスやホスト名とポート番号1812を指定して接続することができる。

## サービスの起動方法（実験モード）
実験モードでは、Woven ID dev環境へ接続し実際のID providerとの接続方法を確認することができる。Dev環境に接続するためには、RADIUS SERVERの下記の3つの環境変数を書き換える必要がある。
- OPENID_TOKEN_ENDPOINT
- OPENID_CLIENT_ID
- OPENID_CLIENT_SECRET

この実験のためには、Dev環境にClientを登録し、Secretを取得することが必要になる。Agoraチームに#wcm-agora-team-amaチャンネルで依頼することで作成することができる。


## git登録方法

* https://git-scm.com/download/win からgitクライアントをダウンロード・インストール
    * 設定は基本デフォルトでよいが、一点だけ変える
    * choosing HTTPS transport backend のところ
        * `Use the OpenSSL library` ではなく、 `Use the native Windows Secure Channel library` を選択。
* gitbashを起動 (GUIもあるのでそちらでも可)
* httpsを使う(セキュアファット等)
    * nulabアカウント保持者かつ二段階認証の人
        * アイコンメニュー > 個人設定 > 2段階認証時のパスワード
        * ここで発行したパスワードを作成して控えておく
    * 実行 `git clone https://projectmcu.backlog.com/git/DEMIA_NTTCOM/sim-spec`
    * パスワード入力画面が出てくるので、控えたパスワードを入力
* sshを使う
    * 公開鍵・秘密鍵をどこかで作っておく
    * 公開鍵を アイコンメニュー > 個人設定 > SSH公開鍵 に登録。
    * 以下のような感じの設定を ~/.ssh/config に行う

```
    Host projectmcu.git.backlog.com
        HostName projectmcu.git.backlog.com
        IdentityFile ~/.ssh/backlog-com-ntt-sim
        User projectmcu
```

* 実行 `git clone projectmcu@projectmcu.git.backlog.com:/IDEMIA_NTTCOM/sim-spec.git`

## git操作

* gitbashでCLI操作
* vscode の extension(git graph)を使用


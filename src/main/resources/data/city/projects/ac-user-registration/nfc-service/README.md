# ac-nfc-service (namespace: ac-user-registration)

## 環境構築

- ローカル開発環境 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151643955)を参照。
- Monorepo 環境準備 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207123700)を参照。
- Monorepo での開発 : [こちら](https://confluence.tri-ad.tech/display/FSPA3/Development+on+Monorepo+-+Daily+Work+Commands) を参照。

---

## コーディング規約

[こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207110822)を参照。

---

## Repository Rules

### Branch Name

**ac-user-registration/_branch title_**

      *branch title* : ブランチ名を記述。英数子文字のみ。単語は -(ハイフン) 区切り。
      例 : ac-user-registration/100-hoge-huga
      (100 は JIRA チケット番号, 対応チケット無い場合は省略)

※ ブランチは main へマージ後に削除されます

### Commit Comment

[こちら](https://confluence.tri-ad.tech/display/FAC/Development+Rule#DevelopmentRule-%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8:~:text=%C2%A0*/-,%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8,-%E8%8B%B1%E8%AA%9E%E3%81%A7%E8%A8%98%E8%BC%89) を参考に。

### Pull Request

      Title : same as branch name. ac-user-registration/ が先頭についていることを確認する。コミットが1つだけの場合、自動的にコミットコメントになるので、注意。
      Comment : use pull_request_template.md as a template

---

## デバッグ環境構築

### 事前設定

- golang の依存パッケージをインストール
```bash
go mod tidy
```

### Setup env file for debugging

- Copy `.local_debug/template.env` as `.local_debug/local_debug.env`. env file is used only for local debugging. On Agora cluster, env variables are given via deployment.yaml setting.
- Create `.local_debug/postgres.env` to configure local database for debugging.
- Fill `KEYCLOAK_CLIENT_SECRET` value in `local_debug.env` (CAUTION: Do NOT change `template.env`). You can find `KEYCLOAK_CLIENT_SECRET` value on k8s secret or keycloak admin console.


### Description os environment variables
- `.local_debug/template.env`
**TODO: define environment variables to template.env**
```bash
# user name of DB, set same value as POSTGRES_USER in postgres.env
POSTGRES_USER=postgres
# password of DB, set same value as POSTGRES_PASSWORD in postgres.env
POSTGRES_PASSWORD=password
# host of DB, set same value as POSTGRES_HOST in postgres.env
POSTGRES_HOST=localhost
# port of DB, docker-compose.yaml exports postgres DB port(5432) as 65433 to outside of container
POSTGRES_PORT=65433
# database name of DB, use default database name (postgres)
POSTGRES_DB=postgres
# Auto migration
AUTO_MIGRATE=1
```


- `.local_debug/postgres.env`
```bash
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
POSTGRES_HOST=postgres
POSTGRES_PORT=5432
```

### Debug with VSCode

- Create a file `/projects/ac-user-registration/nfc-service/.vscode/launch.json` and copy below json into it.

```json
{
      // Use IntelliSense to learn about possible attributes.
      // Hover to view descriptions of existing attributes.
      // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
      "version": "0.2.0",
      "configurations": [
            {
                  "name": "Launch",
                  "type": "go",
                  "request": "launch",
                  "mode": "debug",
                  "program": "${workspaceFolder}/cmd",
                  "args": [],
                  "envFile": "${workspaceFolder}/.local_debug/local_debug.env"
            }
      ]
}
```

- Set your current directory on `/project/ac-user-registration/nfc-service`.
- Reopen VSCode on that directory : `code . -r`
- Open any go source file and start debugging(F5).

### Run with the docker image created via bazel build

- Set your current directory on monorepo root.
- Build bazel image.

```bash
bazel build //projects/ac-user-registration/nfc-service/...
```

- Run bazel image on docker

```bash
ENV_FILE_PATH="$(git rev-parse --show-toplevel)/projects/ac-user-registration/nfc-service/.local_debug/local_debug.env"
bazel run //projects/ac-user-registration/nfc-service/cmd:image.load
docker run --rm --network="host" -it -p 8080:8080 --env-file $ENV_FILE_PATH projects/ac-user-registration/nfc-service/cmd:image
```

---

## テスト用 mock ファイル更新

/projects/ac-user-registration/nfc-service へカレントディレクトリを移動後

```bash
bash ./script/mockgen.sh
```

## Stringer による enum 生成

**TODO: update sample file**
authn/model/authn_method.go の様に記述した後、下記コマンドを実行。

```bash
go generate ./...
```

---

## Bazel commands

### Build

```bash
bazel build //projects/ac-user-registration/nfc-service/...
```

### Run unit tests

```bash
bazel test //projects/ac-user-registration/nfc-service/...

# run tests under the specified directory
$ bazel test //projects/ac-user-registration/nfc-service/{folder}

# run tests with log outputs --test_output=all
$ bazel test --test_output=all //projects/ac-user-registration/nfc-service/...

# re-run tests ignoring cache
$ bazel test --cache_test_results=no //projects/ac-user-registration/nfc-service/...
```

### Update BUILD.bazel

```bash
bazel run //:gazelle
```


### Run buildifier (Bazel Lint)

```bash
bazel run //:buildifier_check
```

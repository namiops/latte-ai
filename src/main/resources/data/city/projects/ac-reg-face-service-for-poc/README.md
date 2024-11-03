# ac-reg-face-service-for-poc (namespace: ac-reg-visitor-personal)

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

**ac-reg-visitor-personal/_branch title_**

      *branch title* : ブランチ名を記述。英数子文字のみ。単語は -(ハイフン) 区切り。
      例 : ac-reg-face-service-for-poc/100-token-verification
      (100 は JIRA チケット番号, 対応チケット無い場合は省略)

※ ブランチは main へマージ後に削除されます

### Commit Comment

[こちら](https://confluence.tri-ad.tech/display/FAC/Development+Rule#DevelopmentRule-%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8:~:text=%C2%A0*/-,%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8,-%E8%8B%B1%E8%AA%9E%E3%81%A7%E8%A8%98%E8%BC%89) を参考に。

### Pull Request

      Title : same as branch name. ac-reg-visitor-personal/ が先頭についていることを確認する。コミットが1つだけの場合、自動的にコミットコメントになるので、注意。
      Comment : use pull_request_template.md as a template

---

## デバッグ環境構築

### 事前設定

- golang の依存パッケージをインストール

      go mod tidy

// TODO This README.md was copied from A3 project. Edit later.

### Setup env file for debugging

- Copy `.local_debug/template.env` as `.local_debug/local_debug.env`. env file is used only for local debugging. On Agora cluster, env variables are given via deployment.yaml setting.
- Fill `KEYCLOAK_CLIENT_SECRET` value in `local_debug.env` (CAUTION: Do NOT change `template.env`). You can find `KEYCLOAK_CLIENT_SECRET` value on k8s secret or keycloak admin console.

  - Description os environment variables

            # user name of DB, set same value as POSTGRES_USER in postgres.env
            DATABASE_USERNAME=postgres
            # password of DB, set same value as POSTGRES_PASSWORD in postgres.env
            DATABASE_PASSWORD=YOUR_PASSWORD
            # host of DB, set same value as POSTGRES_HOST in postgres.env
            DATABASE_HOST=localhost
            # port of DB, docker-compose.yaml exports postgres DB port(5432) as 65432 to outside of container
            DATABASE_PORT=65432
            # database name of DB, use default database name (postgres)
            DATABASE_NAME=postgres
            # Set 1 to load test policy data on launching. Test data is read from policy_dummy_data.csv. If the casbin database is not empty, loading will be skipped even if 1 is set.
            LOAD_CASBIN_TEST_DATA=1
            # Max count which GET /rules returns
            CASBIN_GET_RULES_MAX_COUNT=1000
            # Keycloak API end-point. On local debugging = is outside of Agora, so set public end-point.
            KEYCLOAK_END_POINT=https://id.cityos-dev.woven-planet.tech
            # Keycloak client id of alt-authn-authz
            KEYCLOAK_CLIENT_ID=alt-authn-authz
            # Keycloak client secret of alt-authn-authz. You can find it on k8s secret or keycloak admin console.
            KEYCLOAK_CLIENT_SECRET=KEYCLOAK_CLIENT_SECRET

### Debug with VSCode

- Create a file `/projects/ac-reg-face-service-for-poc/.vscode/launch.json` and copy below json into it.

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

- Set your current directory on `/project/ac-reg-face-service-for-poc`.
- Reopen VSCode on that directory : `code . -r`
- Open any go source file and start debugging(F5).

### Run with the docker image created via bazel build

- Set your current directory on monorepo root.
- Build bazel image.

      bazel build //projects/ac-reg-face-service-for-poc/...

- Run bazel image on docker

      ENV_FILE_PATH="$(git rev-parse --show-toplevel)/projects/ac-reg-face-service-for-poc/.local_debug/local_debug.env"
      bazel run //projects/ac-reg-face-service-for-poc/cmd:cmd -- --env-file $ENV_FILE_PATH

---

## テスト用 mock ファイル更新

/projects/alt-auth-authz へカレントディレクトリを移動後

      bash ./script/mockgen.sh

## Stringer による enum 生成

authn/model/authn_method.go の様に記述した後、下記コマンドを実行。

      go generate ./...

---

## Bazel commands

### Build

      bazel build //projects/ac-reg-face-service-for-poc/...

### Run unit tests

      bazel test //projects/ac-reg-face-service-for-poc/...

      # run tests under the specified directory
      $ bazel test //projects/ac-reg-face-service-for-poc/{folder}

      # run tests with log outputs --test_output=all
      $ bazel test --test_output=all //projects/ac-reg-face-service-for-poc/...

      # re-run tests ignoring cache
      $ bazel test --cache_test_results=no //projects/ac-reg-face-service-for-poc/...

### Update BUILD.bazel

      bazel run //:gazelle


### Run buildifier (Bazel Lint)

      bazel run //:buildifier_check

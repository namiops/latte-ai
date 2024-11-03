# a3-service (namespace: alt-authn-authz)

## 環境構築

- ローカル開発環境 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151643955)を参照。
- Monorepo 環境準備 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207123700)を参照。
- Monorepo での開発 : [こちら](https://confluence.tri-ad.tech/display/FSPA3/Development+on+Monorepo+-+Daily+Work+Commands) を参照。

---

## コーディング規約

[こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207110822)を参照。

---

## API spec editing tool

- To edit `A3-API-v1.yaml`, recommend to use [Stoplight Studio](https://stoplight.io/studio)

---

## Repository Rules

### Branch Name

**alt-authn-authz/_branch description_**

      *branch title* : ブランチ名を記述。
      例 : alt-authn-authz/100_token_verifiation
      (100 は JIRA チケット番号, 対応チケット無い場合は省略)

※ ブランチは main へマージ後に削除されます

### Commit Comment

[こちら](https://confluence.tri-ad.tech/display/FAC/Development+Rule#DevelopmentRule-%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8:~:text=%C2%A0*/-,%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8,-%E8%8B%B1%E8%AA%9E%E3%81%A7%E8%A8%98%E8%BC%89) を参考に。

### Pull Request

      Title : same as branch name
      Comment : use pull_request_template.md as a template

---

## デバッグ環境構築

### 事前設定

- golang の依存パッケージをインストール

      go mod tidy

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
            # port of DB, docker-compose.yaml exports postgres DB port(5432), as 65432 is mapped for from the host machine side.
            DATABASE_PORT=5432
            # database name of DB, use default database name (postgres)
            DATABASE_NAME=postgres
            # Set 1 to load test policy data on launching. Test data is read from policy_dummy_data.csv. If the casbin database is not empty, loading will be skipped even if 1 is set.
            LOAD_CASBIN_TEST_DATA=1
            # Max count which GET /rules returns
            CASBIN_GET_RULES_MAX_COUNT=1000
            # Keycloak API end-point. On local debugging = is outside of Agora, so set public end-point.
            KEYCLOAK_API_ENDPOINT=https://id.cityos-dev.woven-planet.tech
            # Keycloak client id of alt-authn-authz
            KEYCLOAK_CLIENT_ID=alt-authn-authz
            # Keycloak client secret of alt-authn-authz. You can find it on k8s secret or keycloak admin console.
            KEYCLOAK_CLIENT_SECRET=KEYCLOAK_CLIENT_SECRET
            # NFC API end-point
            NFC_API_ENDPOINT=https://ac-access-control.cityos-dev.woven-planet.tech/nfc/api/v1
            # VisionAI API end-point. On local debugging = is outside of Agora, so set public end-point.
            VISION_AI_API_ENDPOINT=https://utility.cityos-dev.woven-planet.tech/face-identify/api/v1
            # BRR API end-point. On local debugging = is outside of Agora, so set public end-point. (BRR public API will be unavailable after a debugging feature on Agora cluster provided)
            BRR_API_ENDPOINT=https://brr.cityos-dev.woven-planet.tech/api/v1alpha
            # Threshold of face identification similarity. If the returned similarity is less than the threshold, the authentication fails.
            VISION_AI_SIMILARITY_THRESHOLD=0.5
            # Local CouchDB end-point for logging.
            SECURE_KVS_ENDPOINT=http://admin:password@localhost:5984/

### Debug with VSCode

- Create a file `/projects/alt-authn-authz/a3-service/.vscode/launch.json` and copy below json into it.

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

- Set your current directory on `/project/alt-authn-authz/a3-service`.
- Reopen VSCode on that directory : `code . -r`
- Open any go source file and start debugging(F5).

### Run with the docker image created via bazel build

- Set your current directory on monorepo root.
- Build bazel image.

      bazel build //projects/alt-authn-authz/a3-service/...

- Load the //projects/alt-authn-authz/a3-service/cmd:cmd target image into your local Docker client

      bazel run //projects/alt-authn-authz/a3-service/cmd:image.load

  - for macOS user:

            bazel run //projects/alt-authn-authz/a3-service/cmd:image.load --platforms=@io_bazel_rules_go//go/toolchain:linux_amd64 -- --norun

- Run docker image built by Bazel
  - Set your current directory to /projects/alt-authn-authz/a3-service/.local_debug and run the following command.

            docker compose up --build
    - for rancher desktop user on macOS:
      see https://github.com/rancher-sandbox/rancher-desktop/issues/1209#issuecomment-1370181132 to resolve docker-compose binding mount.

### Set up CouchDB for Authz and Operation logging

- After you run docker compose, run following script to setup CouchDB databases. (Need to change current directory to `/projects/alt-authn-authz/a3-service/.local_debug` and run the following command.)

      bash setup_couchdb.sh

- You can access local CouchDB admin page via `http://localhost:5984/_utils`. see `docker-compose.yaml` about user name and password for login.

---

## Scripts

### テスト用 mock ファイル更新

/projects/alt-auth-authz/a3-service へカレントディレクトリを移動後

      bash ./.script/mockgen.sh

### Stringer による enum 生成

authn/model/authn_method.go の様に記述した後、下記コマンドを実行。

      go generate ./...

### Collect coverage data of integration tests

- Set your current directory to /projects/alt-authn-authz/a3-service
- Run the command below tests of `/handler` package and generates coverage result as `cover.html`

      bash ./.script/cover.sh

### Log collection

Integrated log of authentication, authorization, and inspection can be collected by executing the following command.

      bash $(git rev-parse --show-toplevel)/projects/alt-authn-authz/a3-service/.script/auth_integration_log.sh "2024/04/04 12:00:00" "2024/04/04 14:00:00" accessToken

- The first and second parameters are the time ranges to be retrieved.
- Set the user access token as the third parameter.
- It is output as a csv file.

      "serviceName","method","wovenId","reason","timeMilliseconds","resource","action","permission","similarityThreshold","similarity","probability","photoUrl"
      "access-control-external","face","xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx","",1712206749769,"publicDoor-test2","enter","true",0.4,0.7822262,0.98643917,"https://alt-authn-authz.cityos-dev.woven-planet.tech/api/v1/admin/access-control-external/inspection-log/face-image-auth/yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy/image"

- The token is required to access the photo URL.

---

## Bazel commands

### Build

      bazel build //projects/alt-authn-authz/a3-service/...

### Run unit tests

      bazel test //projects/alt-authn-authz/a3-service/...

      # run tests under the specified directory
      $ bazel test //projects/alt-authn-authz/a3-service/service/...

      # run tests with log outputs --test_output=all
      $ bazel test --test_output=all //projects/alt-authn-authz/a3-service/...

      # re-run tests ignoring cache
      $ bazel test --cache_test_results=no //projects/alt-authn-authz/a3-service/...

### Update BUILD.bazel

      $ bazel run //:gazelle 

      # after running gazelle, run buildifier to lint bazel files
      # if there are linting warnings in bazel files, monorepo CI reports an error
      $ bazel run //:buildifier

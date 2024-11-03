# ac-reg-visitor-public-service (namespace: ac-reg-visitor-public)

## 環境構築

- ローカル開発環境 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151643955)を参照。
- Monorepo 環境準備 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207123700)を参照。
- Monorepo での開発 : [こちら](https://confluence.tri-ad.tech/display/FSPA3/Development+on+Monorepo+-+Daily+Work+Commands) を参照。

// TODO This README.md was copied from A3 project. Edit later.

---

## コーディング規約

[こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207110822)を参照。

---

## Repository Rules

### Branch Name

**ac-reg-visitor-public-service/_branch description_**

      *branch title* : ブランチ名を記述。
      例 : ac-reg-visitor-public-service/100_token_verifiation
      (100 は JIRA チケット番号, 対応チケット無い場合は省略)

※ ブランチは main へマージ後に削除されます

### Commit Comment

[こちら](https://confluence.tri-ad.tech/display/FAC/Development+Rule#DevelopmentRule-%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8:~:text=%C2%A0*/-,%E3%82%B3%E3%83%9F%E3%83%83%E3%83%88%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8,-%E8%8B%B1%E8%AA%9E%E3%81%A7%E8%A8%98%E8%BC%89) を参考に。

### Pull Request

      Title : same as branch name
      Comment : use pull_request_template.md as a template

---

## 本サービス経由で登録されるユーザーについて

### Keycloak User Sample

      {
            username:  "FssAccessControl0000_YY-MM-DD-HHmmss",
            firstName: "Fss",
            lastName:  "AccessControl",
            attributes: {
                  "temporary_user": [
                        "for_FSS_demo"
                  ],
                  "expiration_dates": [
                        "20230830"
                  ]
            },
            enabled: "true"
      }

- `firstName` and `lastName` are given by the frontend
- `username` is `firstName` + `lastName` + `phoneNumber` + "_" + YY-MM-DD-HHmmss (`phoneNumber` is given by the frontend)
- `attributes` is given by the environment variable : KEYCLOAK_USER_ATTRIBUTE

### BURR Attributes

      {
            "basicInfo": {
                  "name": {
                        "normative": {
                              "givenNames": [
                                    "AccessControl"
                              ],
                              "primaryName": "Fss"
                        }
                  }
            },
            "userTypes": [
                  "visitor"
            ]
      }

### Find Users with Keycloak Admin API

You can list users registered via this backend with he script below.

      # put your CLIENT_ID and CLIENT_SECRET here
      # The service account used by this script requires "user-admin" role of "realm-management" client.
      CLIENT_ID=ac-reg-visitor-public
      CLIENT_SECRET=[put the secret of ac-reg-visitor-public]

      SERVER_DOMAIN="https://id.cityos-dev.woven-planet.tech"

      # Get access token for the service account
      ACCESS_TOKEN=$(
      curl -sS --request POST --url "$SERVER_DOMAIN/auth/realms/woven/protocol/openid-connect/token" \
            --header 'Accept: */*' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data client_id="$CLIENT_ID" \
            --data client_secret="$CLIENT_SECRET" \
            --data scope="openid" \
            --data grant_type=client_credentials \
      | jq ".access_token" -r
      )

      QUERY_PARAMS="?q=temporary_user:for_FSS_demo"
      RESULT=$(curl -sS --request GET --url "$SERVER_DOMAIN/auth/admin/realms/woven/users$QUERY_PARAMS" --header 'Accept: */*' --header 'Content-Type: application/json' --header 'Authorization: Bearer '$ACCESS_TOKEN | jq ".")
      echo $RESULT | jq

---

## デバッグ環境構築

### 事前設定

- golang の依存パッケージをインストール

      go mod tidy

### Setup env file for debugging

- Copy `.local_debug/template.env` as `.local_debug/local_debug.env`. env file is used only for local debugging. On Agora cluster, env variables are given via deployment.yaml setting.
- Fill `KEYCLOAK_CLIENT_SECRET` value in `local_debug.env` (CAUTION: Do NOT change `template.env`). You can find `KEYCLOAK_CLIENT_SECRET` value on k8s secret or keycloak admin console.

  - Description os environment variables

            # Keycloak API end-point. On local debugging = is outside of Agora, so set public end-point.
            KEYCLOAK_ENDPOINT_DOMAIN=https://id.cityos-dev.woven-planet.tech
            # BRR API end-point. On local debugging = is outside of Agora, so set public end-point.
            BRR_ENDPOINT_DOMAIN=https://brr.cityos-dev.woven-planet.tech
            # Face ID API end-point. On local debugging = is outside of Agora, so set public end-point.
            FACE_ID_ENDPOINT_DOMAIN=http://common-backend.tri-ad.tech
            # Keycloak client id of ac-reg-visitor-public
            KEYCLOAK_CLIENT_ID=ac-reg-visitor-public
            # Keycloak client secret of ac-reg-visitor-public. You can find it on k8s secret or keycloak admin console.
            KEYCLOAK_CLIENT_SECRET=KEYCLOAK_CLIENT_SECRET
            # User attribute of keycloak user. Set "attribute key, attribute value" if you need it. White spaces are trimmed.
            KEYCLOAK_USER_ATTRIBUTE=temporary_user, for_nihonbashi_demo | expiration_dates, 20221231 


### Debug with VSCode

- Create a file `/projects/ac-reg-visitor-public-service/.vscode/launch.json` and copy below json into it.

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

- Set your current directory on `/project/ac-reg-visitor-public-service`.
- Reopen VSCode on that directory : `code . -r`
- Open any go source file and start debugging(F5).

### Run with the docker image created via bazel build

- Set your current directory on monorepo root.
- Build bazel image.

      bazel build //projects/ac-reg-visitor-public-service/...

- Run bazel image on docker

      ENV_FILE_PATH="$(git rev-parse --show-toplevel)/projects/ac-reg-visitor-public-service/.local_debug/local_debug.env"
      bazel run //projects/ac-reg-visitor-public-service/cmd:image.load
      docker run --rm --network="host" -it -p 8080:8080 --env-file $ENV_FILE_PATH projects/ac-reg-visitor-public-service/cmd:image

---

## テスト用 mock ファイル更新

/projects/ac-reg-visitor-public-service へカレントディレクトリを移動後

      bash ./script/mockgen.sh

---

## Bazel commands

### Build

      bazel build //projects/ac-reg-visitor-public-service/...

### Run unit tests

      bazel test //projects/ac-reg-visitor-public-service/...

      # run tests under the specified directory
      $ bazel test //projects/ac-reg-visitor-public-service/service

      # run tests with log outputs --test_output=all
      $ bazel test --test_output=all //projects/ac-reg-visitor-public-service/...

      # re-run tests ignoring cache
      $ bazel test --cache_test_results=no //projects/ac-reg-visitor-public-service/...

### Update BUILD.bazel

      bazel run //:gazelle

# ac-user-registration-backend <!-- omit in toc -->

See also [city Golang development guide](https://github.com/wp-wcm/city/tree/main/docs/development/go)

## Table of Contents <!-- omit in toc -->

- [Expected environment](#expected-environment)
- [Set up development environments](#set-up-development-environments)
- [Daily Operations](#daily-operations)
  - [Launch docker container for local debugging](#launch-docker-container-for-local-debugging)
    - [Each service coverage collection](#each-service-coverage-collection)
  - [Send a request to the HTTP API on the dev environment](#send-a-request-to-the-http-api-on-the-dev-environment)
    - [Create Postman collections from OpenAPI](#create-postman-collections-from-openapi)
    - [List of the `baseUrl`s on the dev environment](#list-of-the-baseurls-on-the-dev-environment)
    - [Retrieve user credentials](#retrieve-user-credentials)
    - [Inject secrets](#inject-secrets)
    - [Troubleshooting tips on Agora Deployment](#troubleshooting-tips-on-agora-deployment)

## Expected environment

windows 10 & WSL2 (ubuntu 20.04)

## Set up development environments

TODO: Move documents to github

- ローカル開発環境 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151643955)を参照。
- Monorepo 環境準備 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207123700)を参照。
- Monorepo での開発 : [こちら](https://confluence.tri-ad.tech/display/FSPA3/Development+on+Monorepo+-+Daily+Work+Commands) を参照。

## Daily Operations

### Launch docker container for local debugging

- Set your current directory to `projects/ac-access-control/backend/.local_debug` and run the following command. The postgresDB for local debugging will launch.
  
```bash
docker compose up
```

- after debugging finishes, run

```bash
docker compose down
```

- When re-building the container, run

```bash
docker compose up --build

### Generate/Update mocks

```bash
go install go.uber.org/mock/mockgen@latest # Run this line if you have not installed mockgen CLI yet
go generate ./...
```

See for details: [gomock](https://github.com/golang/mock)

#### Each service coverage collection

Execute ./projects/ac-user-registration/backend/scripts/cover.sh with the root directory path of the service as the argument.

```bash
cd ./projects/ac-user-registration/backend/scripts

# visitor api server
./cover.sh ../visitor/api-server
# cron job
./cover.sh ../cronjob/get-id-results/
# resident api server
./cover.sh ../resident/api-server
# worker api server
./cover.sh ../worker/api-server
# outputting coverage of common libraries under projects/ac-access-control/backend/internal/
./cover.sh ../
```

After execution, the following file is output

- cover.html : HTML file that displays the coverage range of the source
- cover.out : Raw data file of coverage collection results
- cover_all.txt : File that records the coverage rate for each function
- cover_directories.txt : File outputting coverage rate per directories

### Send a request to the HTTP API on the dev environment

#### Create Postman collections from OpenAPI

1. Download [Postman](https://www.postman.com/) and install it to your environment
1. Import a yaml file `backend/doces/api/*.yaml` as a collection.
    - Click the `Import` button
    ![import button](./images/readme/import_button.png)
    - Drug and drop the file
    ![import yaml](./images/readme/import_yaml.png)
1. Click the folder `AC XXX API`, and set `baseUrl` to the desired value.

#### List of the `baseUrl`s on the dev environment

| Project Name                                         | baseUrl                                                                    |
| :--------------------------------------------------- | :------------------------------------------------------------------------- |
| ac-user-registration-visitor-registration-api-server | `https://ac-user-registration.cityos-dev.woven-planet.tech/api/visitor/v1` |

#### Retrieve user credentials

The backend API restricts access by means of an `Authorization` header.
You can obtain an access token according to the following steps.

1. Access to [httpbin](https://httpbin.cityos-dev.woven-planet.tech/headers).
2. Copy the value of `X-Auth-Request-Access-Token`.
3. Set the token to the `Authorization` header in each request.

#### Inject secrets

See [the document (for ac-access-control namespace)](../../ac-access-control/backend/README.md#configure-serviceaccounts-to-access-vault-from-kubernetes-cluster)

#### Troubleshooting tips on Agora Deployment

If you have trouble getting things to work properly when deploying to Agora, the following information may help you resolve the issue.

1. If it is not working properly when after updated kubernetes configuration file,
   it is possible that the information in the configuration file is incorrect.
   In that case,checking for any kind of error in slack channel of [wcm-city-os-bots](https://tri-ad-global.slack.com/archives/C02RD6HTJG5) may help.

   This channel can get lost quickly over time, so it may be easier to find it by filtering through Slack's search as follows

   `ac-user-registration in:#wcm-city-os-bots`

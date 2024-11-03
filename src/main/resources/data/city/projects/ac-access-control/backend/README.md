# ac-access-control-backend <!-- omit in toc -->

See also [city Golang development guide](https://github.com/wp-wcm/city/tree/main/docs/development/go)

## Table of Contents <!-- omit in toc -->

- [Dashboards](#dashboards)
- [Expected environment](#expected-environment)
- [Set up development environments](#set-up-development-environments)
  - [Set up local Kubernetes environment](#set-up-local-kubernetes-environment)
  - [Install development tools](#install-development-tools)
    - [redis-cli](#redis-cli)
- [Daily Operations](#daily-operations)
  - [Launch docker container for local debugging](#launch-docker-container-for-local-debugging)
    - [Set up CouchDB](#set-up-couchdb)
  - [Edit API definitions](#edit-api-definitions)
  - [Generate/Update mocks](#generateupdate-mocks)
  - [Run unit tests](#run-unit-tests)
  - [Coverage collection](#coverage-collection)
    - [Setup environment for coverage collection](#setup-environment-for-coverage-collection)
    - [Each service coverage collection](#each-service-coverage-collection)
    - [Set coverage collection exclusions](#set-coverage-collection-exclusions)
  - [Send a request to the HTTP API on the dev environment](#send-a-request-to-the-http-api-on-the-dev-environment)
    - [Create Postman collections from OpenAPI](#create-postman-collections-from-openapi)
    - [List of the `baseUrl`s on the dev environment](#list-of-the-baseurls-on-the-dev-environment)
  - [Configure a mock MQTT client for manual testing](#configure-a-mock-mqtt-client-for-manual-testing)
    - [How to configure local MQTT client](#how-to-configure-local-mqtt-client)
    - [MQTTX Configurations](#mqttx-configurations)
  - [Update deployment settings on Kubernetes cluster](#update-deployment-settings-on-kubernetes-cluster)
    - [Update Grafana dashboard](#update-grafana-dashboard)
    - [Set up secret values on a pod](#set-up-secret-values-on-a-pod)
      - [Configure ServiceAccounts to access Vault from kubernetes cluster](#configure-serviceaccounts-to-access-vault-from-kubernetes-cluster)
      - [Register a secret to our Vault namespace](#register-a-secret-to-our-vault-namespace)
      - [Create vault roles and policies](#create-vault-roles-and-policies)
      - [Inject a secret using Vault sidecar](#inject-a-secret-using-vault-sidecar)
    - [Troubleshooting tips on Agora Deployment](#troubleshooting-tips-on-agora-deployment)

## Dashboards

See [this section](#update-grafana-dashboard) to know how to update dashboard layouts.

- [namespace dashboards](https://observability.cityos-dev.woven-planet.tech/grafana/dashboards/f/ABwMsypSk/ac-access-control-backend)
- managed dashboards
  - [postgres cluster](https://observability.cityos-dev.woven-planet.tech/grafana/d/2cb3f68dc83a917801763f9f6b58d1310442a063/postgresql-details?orgId=1&refresh=5s&var-namespace=ac-access-control&var-cluster=auth-db&var-pod=All&var-datname=All)
  - [loki logs per namespace](https://observability.cityos-dev.woven-planet.tech/grafana/d/5ba7bab980d2cd898f81604d113b231b163cd461/loki-logs-per-namespace?orgId=1&var-namespace=ac-access-control&var-pod=All&var-container=All&var-host=All&var-mystream=All)
    - Recommend to use `Backend Logs` in the namespace dashboard to see application logs.

## Expected environment

windows 10 & WSL2 (ubuntu 20.04)

## Set up development environments

TODO: Move documents to github

- ローカル開発環境 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151643955)を参照。
- Monorepo 環境準備 : [こちら](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=207123700)を参照。
- Monorepo での開発 : [こちら](https://confluence.tri-ad.tech/display/FSPA3/Development+on+Monorepo+-+Daily+Work+Commands) を参照。

### Set up local Kubernetes environment

Local kubernetes environment is not required, but useful for experimenting Kubernetes settings.  
See [the demo project](./docs/minikube_demo/README.md) for details.

### Install development tools

#### redis-cli

```bash
sudo apt install redis-tools
```

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
```

#### Set up CouchDB

- After you run docker compose, run following script to setup CouchDB databases. (Need to change current directory to `projects/ac-access-control/backend/.local_debug` and run the following command.)

```bash
bash setup_couchdb.sh
```

- You can access local CouchDB admin page via `http://localhost:55984/_utils`. see `docker-compose.yaml` about user name and password for login.

### Edit API definitions

We strongly recommend using [Stoplight Studio](https://stoplight.io/welcome) to edit OpenAPI `.yaml` files.
Since Stoplight studio cannot open directories in WSL, please follow these steps to edit files on your host machine.

- Install Git on your host machine. [DL link](https://git-scm.com/download/win)
- Clone this repository into your host machine.
- Open `projects/ac-access-control/backend/docs` with Stoplight Studio.
- Push the change with Git Bash (any method is fine).

### Generate/Update mocks

```bash
go install go.uber.org/mock/mockgen@latest # Run this line if you have not installed mockgen CLI yet
go generate ./...
```

See for details: [gomock](https://github.com/golang/mock)

### Run unit tests

```text
# run tests
bazel test //projects/ac-access-control/...

# run tests with log outputs --test_output=all
$ bazel test --test_output=all //projects/ac-access-control/...

# re-run tests ignoring cache
$ bazel test --cache_test_results=no //projects/ac-access-control/...
```

### Coverage collection

#### Setup environment for coverage collection

- Run the following command to install `gocovmerge`

```bash
go install github.com/wadey/gocovmerge@latest # Run this line if you have not installed gocovmerge yet
```

If you are using `asdf` version control tool and have installed go, please execute the following command.

```bash
asdf reshim golang
```

#### Each service coverage collection

Execute ./projects/ac-access-control/backend/scripts/cover.sh with the root directory path of the service as the argument.

```bash
cd ./projects/ac-access-control/backend/scripts

./cover.sh ../auth/api-server
./cover.sh ../log/api-server
./cover.sh ../management/api-server
./cover.sh ../management/worker
# outputting coverage of common libraries under projects/ac-access-control/backend/internal/
./cover.sh ../
```

After execution, the following file is output

- cover.html : HTML file that displays the coverage range of the source
- cover.out : Raw data file of coverage collection results
- cover_all.txt : File that records the coverage rate for each function
- cover_directories.txt : File outputting coverage rate per directories

#### Set coverage collection exclusions

If there are source files that are difficult or unnecessary to run unit test and you do not want to include them in the coverage, please write the relative path of that file in projects/ac-access-control/backend/scripts/cover_ignore .

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

TODO: update urls after dev2 cluster is available

| Project Name                            | baseUrl                                                                    |
| :-------------------------------------- | :------------------------------------------------------------------------- |
| ac-access-control-management-api-server | `https://ac-access-control.cityos-dev.woven-planet.tech/management/api/v1` |
| ac-access-control-auth-api-server       | `https://ac-access-control.cityos-dev.woven-planet.tech/auth/api/v1`       |
| ac-access-control-log-api-server        | `https://ac-access-control.cityos-dev.woven-planet.tech/log/api/v1`        |
| ac-access-control-car-gate-api          | `https://ac-access-control.cityos-dev.woven-planet.tech/car-gate/api/v1`   |

### Configure a mock MQTT client for manual testing

The following instruction shows how to connect to the MQTT broker in the Agora development environment with an MQTT client application [MQTTX](https://mqttx.app/).

#### How to configure local MQTT client

1. Download [MQTTX](https://mqttx.app/) and install it to your local environment
2. Create a device record and generate certificates
   1. (if needed) Create a new OTA group by calling `POST /management/api/v1/ota-groups`
   2. Create a new device by calling `POST /management/api/v1/devices`. The specified OTA group name should be the one created in Step 1.
   3. Generate certificates of the device by the CLI tool `iotactl`.  
    See [the confluence document](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=433522015) to install `iotactl`
    After running the command `iotactl provision`, certification files will be generated in `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/`.
    **TODO**: Update the section after the provision API is implemented.

    ```bash
    iotactl refresh provision-secret -t ac-access-control-host {ota-group-name} # reflesh provision secret
    iotactl provision {device-name} -t ac-access-control-host -g {ota-group-name} --provision-secret {provision-secret}
    ```

3. Run MQTTX and create a new configuration. The configurations are listed in the section [MQTTX-Configurations](#mqttx-configurations).  
   After the configuration, clicking the button `Connect` will establish an MQTT connection.
   ![connect button](images/readme/connect_button.png)

#### MQTTX Configurations

| Item                    | Value                                     | Notes                                                                                                              |
| :---------------------- | :---------------------------------------- | :----------------------------------------------------------------------------------------------------------------- |
| General                 |                                           |                                                                                                                    |
| Name                    | (Arbitrary value)                         |                                                                                                                    |
| Client ID               | (Arbitrary value)                         |                                                                                                                    |
| Host                    | mqtts://iot.cityos-dev.woven-planet.tech  |                                                                                                                    |
| Port                    | 8883                                      |                                                                                                                    |
| Username                | ac-access-control-host:{broker-user-name} | The value of {broker-user-name} can be found in `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/{device-name}_broker.json`.    |
| Password                | {broker-access-token}                     | The value of {broker-access-token} can be found in `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/{device-name}_broker.json`. |
| SSL/TLS                 | on                                        |                                                                                                                    |
| SSL Secure              | off                                       | Disable it to skip the configuration                                                                               |
| Certificate             | Self signed                               |                                                                                                                    |
| Certificates            |                                           |                                                                                                                    |
| CA File                 | `\path\to\{device-name}_ca.pem`           | If the file is in WSL, it can be referred directly via `\\wsl.localhost`.                                          |
| Client Certificate File | `\path\to\{device-name}_crt.pem`          |                                                                                                                    |
| Client Key File         | `\path\to\{device-name}_key.pem`          |                                                                                                                    |
| Advanced                |                                           |                                                                                                                    |
| MQTT Version            | 3.1                                       |                                                                                                                    |

This image is a configuration example.
![connection setting](images/readme/connection_setting.png)

### Update deployment settings on Kubernetes cluster

The following folders contain deployment settings for Agora.

| Destination                                        | Description                                                  |
| :------------------------------------------------- | :----------------------------------------------------------- |
| `infra/k8s/dev/ac-access-control`                  | A root folder for all deployment settings in this namespace. |
| `projects/ac-access-control/backend/k8s/dev/`      | Shared settings in this namespace                            |
| `projects/ac-access-control/backend/**/**/k8s/dev` | Dedicated settings for each backend project                  |

#### Update Grafana dashboard

[Tutorial on Agora](https://developer.woven-city.toyota/docs/default/Component/grafana-tutorial/en/00_index/#grafana-hands-on)

1. Open the dashboard to be updated.
   1. Open [this folder](https://observability.cityos-dev.woven-planet.tech/grafana/dashboards/f/g77kKDmIz/ac-access-control-backend).
   2. Select a dashboard. Then you will see a dashboard like this. ※ The panels can be different except header icons.
   ![dashboard](images/readme/dashboard.png)
2. Edit panels on the dashboard.
   - See [the official documentation](https://grafana.com/docs/grafana/latest/panels-visualizations/panel-editor-overview/) to learn how to edit panels.
3. On the dashboard page, click `Dashboard settings` icon on the top right -> Click `JSON Model` on the left.
4. Copy the JSON and paste it to a dashboard setting file.
    - Setting files are in `projects/ac-access-control/backend/k8s/dev/observability/`.
5. Commit the change and create a PR.

#### Set up secret values on a pod

[Tutorial on Agora](https://developer.woven-city.toyota/docs/default/Component/vault-tutorial/en/00_index/)
[Documentation written by Agora team](https://github.com/wp-wcm/city/tree/main/ns/vault/docs)

Secret values are managed with [Vault](https://www.vaultproject.io/).
This section provides how to manage secret values and use them in our service pod.

The overview flow is as follows:

- Configure ServiceAccounts to access Vault from kubernetes cluster.
- Register a secret to our Vault namespace.
- Create vault roles and policies.
- Inject the secret using Vault agent sidecar.

##### Configure ServiceAccounts to access Vault from kubernetes cluster

If you use a new `ServiceAccount`, attach a ClusterRole `system:auth-delegator` to it first.

- [Configuration file link](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/flux-tenants/lps/namespaces/ac-access-control/vault-auth.yaml)

##### Register a secret to our Vault namespace

- Sign in to [Vault UI](https://dev.vault.w3n.io/ui/vault/auth?with=gac%2F) with OIDC Provider.
- Move to [Our namespace page](https://dev.vault.w3n.io/ui/vault/secrets?namespace=ns_stargate%2Fns_dev_wcmshrd_acaccesscontrol).
- Click `ac-access-control/`
- Click `Create secret`
- Fill in `Path for this secret` and `Secret data`, then click `Save` button.

##### Create vault roles and policies

- Move to our namespace page (see the link in the previous section).
- Edit policy
  - Click `Policies` on the left sidebar.
  - Click `Create ACL policy` to add a new one, or click the row and click `Edit policy` to edit existing ones.
  - See [the official documentation](https://developer.hashicorp.com/vault/docs/concepts/policies#policy-syntax) to learn policy syntax.
- Edit role
  - Click `Access` on the left sidebar.
  - Click `kubernetes-dev/`
  - Click `Create role` to add a new one, or click the row and click `Edit role` to edit existing ones.
  - Edit `Generated Token's Policies` to attach new policies to the role.

##### Inject a secret using Vault sidecar

To use Vault secrets in a pod, Vault sidecar is a good option.
You can inject secrets just by adding annotation settings to the deployment template of the pod.
[The official documentation](https://developer.hashicorp.com/vault/docs/platform/k8s/injector) describes how to configure the sidecar.

Example settings:
With the following settings, a Vault sidecar automatically retrieves secrets and mounts them to `/vault/secrets/` in the pod.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ac-access-control-management-api-server
  labels:
    app: ac-access-control-management-api-server
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ac-access-control-management-api-server
  template:
    metadata:
      labels:
        app: ac-access-control-management-api-server
      annotations:
        vault.hashicorp.com/agent-inject: "true"
        vault.hashicorp.com/agent-init-first: "true"
        vault.hashicorp.com/namespace: "ns_stargate/ns_dev_wcmshrd_ac_access_control"
        # Specify a role name configured in the section `Create Vault roles and policies`
        vault.hashicorp.com/role: "ac-access-control-management-api-server" 
        # Annotation name => agent-inject-secret-{secret-name}
        # Specify the name of a secret to be injected.
        # In this setting, values of the secret "ac-access-control/my_secret" will be mounted to the path "/secrets/my_injected_secret".
        vault.hashicorp.com/agent-inject-secret-my_injected_secret: "ac-access-control/my_secret"
        # Annotation name => agent-inject-template-{secret-name}
        # The agent mounts the secret with transforming it according to the template.
        # In this example, the secret `{ "key": "secretValue" }` will be transformed into `export MY_SECRET="secretValue"`.
        vault.hashicorp.com/agent-inject-template-my_injected_secret: |-
          {{ with secret "ac-access-control/my_injected_secret" -}}
            export MY_SECRET="{{ .Data.data.key }}"
          {{- end }}
  spec:
   # OMITTED
```

With this example configuration, the secret `my_secret` is available as a file `/vault/secret/my_injected_secret` in the pod.

#### Troubleshooting tips on Agora Deployment

If you have trouble getting things to work properly when deploying to Agora, the following information may help you resolve the issue.

1. If it is not working properly when after updated kubernetes configuration file,
   it is possible that the information in the configuration file is incorrect.
   In that case,checking for any kind of error in slack channel of [wcm-city-os-bots](https://woven-by-toyota.slack.com/archives/C02RD6HTJG5) may help.

   This channel can get lost quickly over time, so it may be easier to find it by filtering through Slack's search as follows

   `ac-access-control in:#wcm-city-os-bots`

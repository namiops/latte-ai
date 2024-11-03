# Grafana operator setup

## Helm
We use helm with a script _the usual way_.
See the `bin/import`.

## connection to Grafana Cloud

For the operator to be able to manage dashboards, alerts, ... on Grafana cloud,
a service account must be created and the API key synced.

### service account creation on Grafana Cloud
Log into the grafana cloud stack and follow these steps.
Create the service account in [grafana](https://wcmagoraprod.grafana.net)
  - `Administration` -> `Users and access` -> `Service Accounts` -> `Add service account`
  - `Display name`: `grafana-operator-prod`
  - `Roles` (all):
    - `Fixed Roles`
      - `Alerting`
      - `Dashboards`
      - `Data sources`
      - `Folders`
    - `Apply`
  - `Create`
  - `Add service account token` -> `create a token` (leave Display name auto generated)
  - Copy the token

2.) Add the token to [vault](https://vault.tmc-stargate.com)
Add the token to the [script](../vault-grafana-operator-secret.sh) and run it.

3.) The secret is imported via the external secrets operator in [externalsecret-grafana-cloud-credentials.yaml](../../secrets/externalsecret-grafana-cloud-credentials.yaml)

### grafana instance

The connection is configured in the [grafana instance](../../grafana/grafana-wcmagoraprod.yaml)

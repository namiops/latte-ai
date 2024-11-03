# Secrets

## external secrets

### Grafana Cloud Service Account

1.) Create the service account in [grafana](https://wcmagoralab2.grafana.net)
  - `Administration` -> `Service Accounts` -> `Add service account`
  - `display name`: `grafana-operator-lab2`
  - `Roles`:
    - `Fixed Roles`
      - `Dashboards`
        - Creator
        - Reader
        - Writer
      - `Data sources`
        - Creator
        - Explorer
        - Reader
        - Writer
      - `Folders`
        - Creator
        - Reader
        - Writer
  - `create`
  - `Add service account token` and `create a token`
  - Copy the token

2.) Add the token to [vault](https://dev.vault.tmc-stargate.com)
  - NS: ns_dev/ns_cityos_platform
  - secret store:  kv-agora-lab2-worker-1-east
  - path: grafana-cloud-credentials


# Agora Identity WovenID Grafana Cloud

This is the [terraform module](https://developer.hashicorp.com/terraform/tutorials/modules) to install dashboards and alerts on the target [grafana cloud stack](https://grafana.com/docs/grafana-cloud/account-management/cloud-stacks/).

## Features

### Metrics Dashboard
- Display the metrics of WovenID database cluster instances.
### Logs Dashboard
- Display the recent log summaries and events of WovenID database cluster instances.
### Alerts
The alert rule which has been exceeded threshold for 5 minutes is considered to be in firing state.

- CPU utilization > 80%
- Free Local Storage < 2GB
- TBD: Max connections > 80%
- TBD: Database [Read/Write] Query Latency > 5000ms

## Setup

### Grafana Cloud

- Choose the target stack
- Create cloud access policy with the permissions below
- Access from the left pane of the stack page, Administration > Cloud access policies > Create access policy
- Create token on the cloud access policy
- Note the token to store it on Vault

```
accesspolicies: [write]
stacks: [read]
stack-service-accounts: [write]
alerts: [write]
```

### Vault

- Add secret on Vault

```sh
export VAULT_MOUNT="speedway-dev"
export ENV_NAME="agora-wovenid-dev"
vault kv put -mount=$VAULT_MOUNT aws-$ENV_NAME/grafana_cloud ACCESS_TOKEN=xxxxxxxxx
```

## How to use

- Define inside the terraform workspace

```tf
...
module "agora_id_wovenid_grafanacloud" {
  source      = "path/to/agora_id_wovenid_grafanacloud"
  stack_slug  = "wovenidexperiments"
  vault_mount = "speedway-dev"
  env_name    = "agora-wovenid-dev"
}
```

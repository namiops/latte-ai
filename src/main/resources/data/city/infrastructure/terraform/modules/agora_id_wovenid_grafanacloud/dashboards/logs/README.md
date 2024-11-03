# Logs Dashboard

Logs dashboard is generated via [Jsonnet](https://jsonnet.org).

## Jsonnet Library

Logs dashboard uses [Grafonnet](https://github.com/grafana/grafonnet)

## How to start developing the dashboard

- Apply the changes in `dashboard.jsonnet`
- Run `bazel run :dashboard.gen`
- Run `terraform apply` or `terragrunt apply` for the specific terraform direcotry which depends on `agora_id_wovenid_grafanacloud`

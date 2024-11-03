# Notes about setting up Grafana Cloud

## TL;DR
We use _our usual way_ of compiling the Helm chart `grafana-k8s-monitoring` and
patching it with `kustomize` were required.

See the values files in the bin directory.

## Q/A

  - Q: Where is the connection to Grafana Cloud configured?
    - A: The destination hosts are defined in the `\*-k8s-monitoring` secrets.
    These secrets are stored in vault, so if you need to update the connection,
    just change the secret in Vault.
  - Q: What are the host-names for the private link setup?
    - A: See this table. The host names are the same for all the stacks.
         |     Service     |                            FQDN                              |
         | --------------- | -----------------------------------------------------------  |
         | Loki (logs)     | loki-prod-030-cortex-gw-ap-northeast-1-vpce.grafana.net      |
         | Mimir (metrics) | mimir-prod-49-cortex-gw-ap-northeast-1-vpce.grafana.net      |
         | Tempo (Traces)  | tempo-prod-20-cortex-gw-ap-northeast-1-vpce.grafana.net      |
         | Profiles        | profiles-prod-019-cortex-gw-ap-northeast-1-vpce.grafana.net  |

## Tools

  - [ally-configurator](https://github.com/grafana/alloy-configurator): A tool to generate alloy config.

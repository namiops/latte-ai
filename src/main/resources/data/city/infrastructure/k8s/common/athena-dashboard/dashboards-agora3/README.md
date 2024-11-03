# Grafana Dashboards

## Updates

  - change namespace from istio-system to observability
  - kiali: external\_services
  - 2023.11.11 New version started for using in Lab2
  - 2024.01.16 Copy to athena-dashboard for new environments

## Sources

Below the sources for each dashboard

|  dashboard                           | Source                                                            |
| ------------------------------------ | ----------------------------------------------------------------- |
| F2T                                  | Foodagri                                                          |
| couchdb                              | examples/grafana/dashboards/Grafana\_Dashboard.json[^1]           |
| demo                                 | Agora[^8] Infra                                                   |
| fluent-bit                           | Helm-Chart                                                        |
| flux                                 | FluxCD[^9] and Agora[^8]                                          |
| gatekeeper                           | Agora[^8]                                                         |
| istio-grafana                        | grafana dashboards[^4]                                            |
| istio-services-grafana-dashboards    | istio/samples/addons/grafana.yaml[^2]                             |
| kafka-grafana-dashboard              | Agora[^8]                                                         |
| keycloak-grafana-dashboard           | /keycloak-metrics-spi[^3]                                         |
| keycloak-grafana-dashboard           | aerogear[^7] and grafana dashboards[^4]                           |
| loki                                 | /13186-loki-dashboard[^4]                                         |
| loki-mixin-compiled                  | Grafana Loki (?)                                                  |
| lps                                  | Agora[^8]                                                         |
| network-performance                  | Agora[^8]                                                         |
| node-exporter-full-grafana-dashboard | /1860-node-exporter-full[^4]                                      |
| pod-topology-spread                  | Agora[^8] Infra                                                   |
| postgresql                           | /postgresql-operator-examples/kustomize/monitoring/dashboards[^5] |
| rabbit-dashboard                     | /rabbitmq-prometheus.git[^6]                                      |
| scheduler                            | Agora[^8]                                                         |



### External resources

[^1]: [gesellix / couchdb-prometheus-exporter](https://github.com/gesellix/couchdb-prometheus-exporter)
[^2]: [istio github repository](https://github.com/istio/istio)
[^3]: [aerogear github repository](https://github.com/aerogear/keycloak-metrics-spi)
[^4]: [grafana dashboards]( https://grafana.com/grafana/dashboards)
[^5]: [CrunchyData postgres operator repository](https://github.com/CrunchyData)
[^6]: [RabbitMQ repository](https://github.com/rabbitmq)
[^7]  [aerogear on github](https://github.com/aerogear/keycloak-metrics-spi)
[^8]: Written by Agora team member
[^9]: [flux2](https://github.com/fluxcd/flux2/)

## Further notes

For details about how to build the individual dash-boards, please refer to the
README.md file in each corresponding sub-directory.


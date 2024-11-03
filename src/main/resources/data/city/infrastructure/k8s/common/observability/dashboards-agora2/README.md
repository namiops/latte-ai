# Grafana Dashboards

# Sources

Below the sources for each dashboard

|  dashboard                           | Source                                                            |
| ------------------------------------ | ----------------------------------------------------------------- |
| couchdb                              | examples/grafana/dashboards/Grafana\_Dashboard.json[^1]           |
| fluent-bit                           | Helm-Chart                                                        |
| flux                                 | Agora[^8]                                                         |
| istio-grafana                        | 7645.json[^7]                                                     |
| istio-services-grafana-dashboards    | istio/samples/addons/grafana.yaml[^2]                             |
| lps                                  | Agora[^8]                                                         |
| kafka-grafana-dashboard              | Agora[^8]                                                         |
| keycloak-grafana-dashboard           | /keycloak-metrics-spi[^3]                                         |
| loki                                 | /13186-loki-dashboard[^4]                                         |
| node-exporter-full-grafana-dashboard | /1860-node-exporter-full[^4]                                      |
| postgresql                           | /postgresql-operator-examples/kustomize/monitoring/dashboards[^5] |
| rabbit-dashboard                     | /rabbitmq-prometheus.git[^6]                                      |
| scheduler                            | Agora[^8]                                                         |

## External resources

[^1]: [gesellix / couchdb-prometheus-exporter](https://github.com/gesellix/couchdb-prometheus-exporter)
[^2]: [istio github repository](https://github.com/istio/istio)
[^3]: [aerogear github repository](https://github.com/aerogear/keycloak-metrics-spi)
[^4]: [grafana dashboards]( https://grafana.com/grafana/dashboards)
[^5]: [CrunchyData postgres operator repository](https://github.com/CrunchyData)
[^6]: [RabbitMQ repository](https://github.com/rabbitmq)
[^7]  [7645_revxxx.json](https://grafana.com/grafana/dashboards/7645-istio-control-plane-dashboard/)
[^8]: Written by Agora team member

# Further notes

For details about how to build the individual dash-boards, please refer to the
README.md file in each corresponding sub-directory.

# Updates

  - change namespace from istio-system to observability
  - kiali: external\_services
  - 2023.11.11 New version started for using in Lab2

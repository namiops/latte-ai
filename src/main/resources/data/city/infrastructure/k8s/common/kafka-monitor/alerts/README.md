# monitoring & alerting for kafka-related resources

This is the implementation of [TN-0256 Monitoring & Alerting System for Kafka Admin - Google Docs](https://docs.google.com/document/d/1DR8_4cQHZqRIk1T-8uzXBnuCRCXC4A1gqjLUiaY_RtY/edit#)

Please update the alerts with the following steps to keep `kafka-related-resource-alerts.csv` up to date. 

1. update `kafka-related-resource-alerts.csv`
2. run `$ python csv_to_yaml.py`
3. update the matchers in kafka-related-alert-manager-config.yaml if you add a new group


**Note**

Currently(Feb 23, 2023), `Alertmanager` is supported only in `observability-dev` namespace and not in `observability` so you can view the  Prometheus UI with the following URL:

- Lab: https://observability-dev.agora-lab.woven-planet.tech/prometheus/alerts?search=
- Dev: https://observability-dev.cityos-dev.woven-planet.tech/prometheus/alerts?search=

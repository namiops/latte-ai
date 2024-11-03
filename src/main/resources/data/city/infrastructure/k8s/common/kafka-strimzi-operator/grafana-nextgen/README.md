# Strimzi Dashboard

## Version History

_Note: The version numbers below refer to the `agora_version` in `grafanadashboard-<foo>.yaml`._

### Strimzi Operator

* v1: copied from [the official example](https://github.com/strimzi/strimzi-kafka-operator/blob/main/examples/metrics/grafana-dashboards/strimzi-operators.json)

### Kafka Connect

* v2: added topic level byte rate metrics (producer, consumer), and topic level error rate (producer)
* v1: customized [the official example dashboard](https://github.com/strimzi/strimzi-kafka-operator/blob/main/examples/metrics/grafana-dashboards/strimzi-kafka-connect.json) because `ServiceMonitor` is used instead of `PodMonitor`. The diff is in [this commit](https://github.com/wp-wcm/city/pull/185/commits/1a4c06bb66d53e0433d6961a41c0665e71d40d55#diff-921c9062e27ae900c62adaefc0e6a6058dd2061b5db752d75cc42e79f5ba66fc)

## How to update the dashboard

* Edit the dashboard in the Grafana UI.
* Export the dashboard JSON with "for sharing externally" option:
    * On the dashboard, click the "share" icon next to the dashboard name.
    * In the dialog, switch to the "Export" tab, and turn on the "Export for sharing externally" option.
    * Click "Save to file" to download the JSON.
* Replace the content of `<>-dashboard.json` with the content of the file you just downloaded
* Check the Git diff for any unexpected changes, and make adjustments if
  necessary. For example:
    * You may have changed the time range (`.time`) or refresh interval
      (`.refresh`) of the dashboard without knowing this would be saved. Reset it
      to the committed values unless you want to change it.
    * The `.version` value (near the bottom) was probably exported as whatever
      internal version Grafana currently has of the board. This value isn't
      significant for us, feel free to set it to the next higher number from the
      committed value (e.g. if last committed version was `11`, set it to `12`).
* Update the change log in `README.md` to explain what
  you're changing. Use the same version number as in the YAML and JSON files above.
* Commit your changes and open a PR.

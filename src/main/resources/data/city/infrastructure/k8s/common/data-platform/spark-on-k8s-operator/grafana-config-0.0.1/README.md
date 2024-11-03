# Spark Applications Dashboard

## How to update the dashboard

The general procedure is:

* Edit the dashboard in the Grafana UI.
* Export the dashboard JSON with "for sharing externally" option:
    * On the dashboard, click the "share" icon next to the dashboard name.
    * In the dialog, switch to the "Export" tab, and turn on the "Export for sharing externally" option.
    * Click "Save to file" to download the JSON.
* Replace the content of `<dashboard>.json` with the content of the file you just downloaded
* Check the Git diff for any unexpected changes, and make adjustments if
  necessary. For example:
    * You may have changed the time range (`.time`) or refresh interval
      (`.refresh`) of the dashboard without knowing this would be saved. Reset it
      to the committed values unless you want to change it.
    * The `.version` value (near the bottom) was probably exported as whatever
      internal version Grafana currently has of the board. This value isn't
      significant for us, feel free to set it to the next higher number from the
      committed value (e.g. if last committed version was `11`, set it to `12`).
* Update the `agora_version` in `grafanadashboard-<target-app>.yaml`
  to the same (next higher) version you used in `<dashboard>.json`.
* Update the change log in `README.md` to explain what
  you're changing. Use the same version number as in the YAML and JSON files above.
* Commit your changes and open a PR.


### Version History

_Note: The version numbers below refer to the `agora_version` in `grafanadashboard-spark-application.yaml`._

* v4: fix a mistake in the variable 
* v3: fix a mistake in the variable and update the tag 
* v2: fix a mistake in the variable 
* v1: customized the public dashboard: [Apache Spark - Performance Metrics | Grafana Labs](https://grafana.com/grafana/dashboards/7890-spark-performance-metrics/)

# BURR Dashboard

## Version History

_Note: The version numbers below refer to the `agora_version` in `grafanadashboard-brr.yaml`._

* v25: Update links to log views with nicer queries
* v24: Fix "source namespace" widget not using BURR namespace variable
* v23: Add request source widget, and improve error logs widget
* v22: Use replica count instead of non-ready pods for status overview
* v21: Update link to Istio envoy proxy logs dashboard
* v20: Update dashboard widget labels to include pod name
* v19:
  * Update query criteria to use social-connection-.*
  * Remove API logs and add Core logs and Social connection logs
* v18: Add basic metrics for social service
* v17:
  * Update link for "API logs" to include `msg` field and namespace
  * Add link to logs view of disregarded consent checks result
* v16: Fix golang runtime metrics
* v15: Add link
  to [Istio-Proxy Envoy Access Logs dashboard](https://observability.cityos-dev.woven-planet.tech/grafana/d/3gsSiy-4z/istio-proxy-envoy-access-logs?orgId=1)
* v14:
  * Enable "shared crosshair" cursor setting
  * Collapse "golang runtime" row because we don't have any metrics there right now
* v13: Add link to explore view showing API service's logs
* v12: Fix pod status panels for Postgres backup pods because they're short-lived
* v11:
  * Add pod status badges at the top for quick overview
  * Show API readiness history instead of pod phase pie chart
  * For Postgres pods, show readiness in addition to pod phase pie charts
* v10: Hide "backup" Postgres Pod Phase pie chart as it has become obsolete
* v9:
  * Hide k8s probes (livez/readyz) from request rate and request duration charts
  * Add a pie chart with API pod status to quickly see if the pod is OK
* v8: Select only the `api` pod for the request stats (to avoid mixing in results
  from other API pods such as a PoC deployment)
* v7: Include PG Backrest pods in postgres pod phase time series, and add pie charts
  for pod phases of postgres components
* v6: Add dashboard variable to switch between "brr" and "brr-b" namespaces
* v5: Add table of HTTP 4xx/5xx error logs
* v4: Add golang runtime metrics charts (and debugging for dashboard deployment)
* v3: Add "Persistent Volume Claim" table (and debugging for dashboard deployment)
* v2: Add BURR service API metrics charts
* v1: Migrate to BURR's namespace from old Grafana version in observability namespace

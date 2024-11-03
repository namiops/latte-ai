# Observability setup for Speedway

Agora utilizes the Grafana Cloud service to deliver an observability stack for Agora users on Speedway. This enables users to fully leverage Grafana's functionalities for logging, metrics, and tracing.

## Access

| Environment    | Vault Address (VAULT_ADDR)           |
|----------------|--------------------------------------|
| Dev            | <https://wcmagoradev.grafana.net/>   |
| Production     | <https://wcmagoraprod.grafana.net/>  |

You can sign in to Grafana using the Woven By Toyota sign in method.

![Grafana Login](../assets/grafana-login.png)

## Logging

!!! Note
    The `Explore` tab currently requires editor access in Grafana Cloud. Please reach out to #wcm-agora-ama to gain access.

Logging in Speedway is automatically ingested into Grafana Cloud. The logs can be accessed using the explore tab in grafana, and the `grafanacloud-wcmagoraprod-logs` data source.

![Grafana Datasource](../assets/grafana-logs.png)

## Metrics

### Enabling Metrics

Metrics can be enabled on your deployments with the following annotations. Please note, you only need to update the following 2 annotations according to your needs:

`prometheus.io/port: "9092"` and `prometheus.io/path: /metrics`

all the other annotations should remain same.

```yaml
prometheus.io/scrape: "true"
prometheus.io/port: "9092"        <---- change according to your needs
prometheus.io/path: /metrics      <---- change according to your needs
prometheus.istio.io/merge-metrics: "true"
k8s.grafana.com/scrape: "true"
k8s.grafana.com/metrics_path: "/stats/prometheus"
k8s.grafana.com/metrics_portNumber: "15020"
```

Example Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
  namespace: my-namespace
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-deployment
  template:
    metadata:
      labels:
        app: my-deployment
        # Speedway: Mandatory for istio service-to-service communication when deploying to the vCluster.
        security.istio.io/tlsMode: istio
      annotations:
        # Speedway: Grafana allow metrics scraping:
        prometheus.io/scrape: "true"
        prometheus.io/port: "9092"
        prometheus.io/path: /metrics
        prometheus.istio.io/merge-metrics: "true"
        k8s.grafana.com/scrape: "true"
        k8s.grafana.com/metrics_path: "/stats/prometheus"
        k8s.grafana.com/metrics_portNumber: "15020"
```

Explanation:

The `prometheus.io/` annotations are used to tell the Istio sidecar proxy (istio-proxy) to forward the metrics from the main container through itself. Essentially, `pod[ main-container:ORIG_PORT -> istio-proxy:15020 ]`:

- The main container exposes its metrics on a certain port (ORIG_PORT).
- The Istio proxy, running alongside the main container, collects these metrics and combines them with its own metrics.
- Both sets of metrics (from the main container and the Istio proxy) are then exposed on port `15020` and path `/stats/prometheus` of the istio-proxy.

Next, the `k8s.grafana.com/` annotations tell Grafana where to scrape the metrics, specifically from the path `/stats/prometheus` and `15020` port, which includes both Istio’s metrics and the main container’s metrics aggregated together.

### Viewing in Grafana

Metrics can be viewed under the explore tab using the `grafanacloud-wcmagoraprod-prom` data source

![Grafana Explore](../assets/grafana-metrics-explore.png)

The Metrics tab is also useful to get an overview of all the metrics being scraped in your deployments.

![Grafana Metrics](../assets/grafana-metrics.png)

## Dashboards

Creating Dashboards via Infrastructure as Code is currently work in progress. For now, you can import dashboards manually by using the GUI directly

![Grafana Dashboard](../assets/grafana-dashboards-create.png)

## Traces

To setup traces, please follow below document:

- [Telemetry Collector](https://developer.woven-city.toyota/docs/default/component/telemetry-collector/)

For support related to Traces please reach out to @agora-services team in [AMA channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7) on Slack.

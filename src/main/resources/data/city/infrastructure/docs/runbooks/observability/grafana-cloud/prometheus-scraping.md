# Adding Prometheus scraping to istio enabled pods

To add /metrics from pods to Grafana Cloud, there are several labels that need to be added to the pods in order to be scraped by Grafana Alloy.

## Annotations

There are 7 annotations required on the pods to enable scraping on pods with istio proxy. All annotations have a fixed value except for `prometheus.io/path` and `prometheus.io/port`.

```yaml
prometheus.io/scrape: "true"
prometheus.io/path: "<metrics_path>"
prometheus.io/port: "<metrics_port>"
prometheus.istio.io/merge-metrics: "true"
k8s.grafana.com/scrape: "true"
k8s.grafana.com/metrics_path: "/stats/prometheus"
k8s.grafana.com/metrics_portNumber: "15020"
```

## Example

Example deployment with Prometheus scraping enabled

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: fake-metrics-generator
  namespace: agora-build-dev
spec:
  replicas: 1
  selector:
    matchLabels:
      app: fake-metrics-generator
  template:
    metadata:
      annotations:
        # 7 Required annotations for pod scraping by grafana alloy
        prometheus.io/scrape: "true"
        prometheus.io/path: "/metrics"
        prometheus.io/port: "5000"
        prometheus.istio.io/merge-metrics: "true"
        k8s.grafana.com/scrape: "true"
        k8s.grafana.com/metrics_path: "/stats/prometheus"
        k8s.grafana.com/metrics_portNumber: "15020"
    spec:
      containers:
        - image: docker.artifactory-ha.tri-ad.tech/carash/fake-metrics-generator:latest
          imagePullPolicy: IfNotPresent
          name: fake-metrics
          ports:
          - containerPort: 5000
            name: http-metrics
```

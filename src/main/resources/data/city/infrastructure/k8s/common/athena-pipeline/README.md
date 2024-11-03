# Athena Pipeline

This namespace is the home of the OpenTelemetry [OTEL] related things.
All the objects are generated by the [OpenTelemetry Operator for Kubernetes]
provided in the [athena-system] namespace.

## TL;DR
There are four components here:
  - otel-agent: Runs as a `daemonset` on each node.
    - worker clusters:
      - The `otel-agent` sends the collected data to the `otel-gateway`.
      - The configuration for the `exporter` and `TLS` is cluster environment
        specific and is __NOT in common__.
  - otel-gateway:
    - management clusters:
      - receive the metrics
    - worker clusters:
  - otel-target-allocator: Used to enable auto collection of metrics (optional
    component)
    - depends on the [CRD for pod and service monitors from Prometheus]
  - otel-clustermetrics:
    - A deployment that collects k8s.cluster metrics

## References
  - [CHANGELOG.md](CHANGELOG.md)
  - XXX TODO XXX: Add Link to current TN

## Configuration
The configuration of the components follow the same idea: Split the yaml file
into sections and load them via the `configMapGenerator`.  This allows for
relatively easy patching and better readability in the tree.

See the `config` section of the `OpenTelemetryCollector` object.
See how the yaml files are included.
To patch / replace any of the sections, just add a custom `configmap` into your
environment.

:farmer: :warning: We are using [FluxCD post build variable substitution] and
the YAML Inclusion with ${} Syntax, so we have to use a special syntax using
`$$`. This unfortunately means, that we cannot apply this `kustomization`
manually.

### Manual changes
The following things have to be configured manually for the time being:

  - Route53 for adding route for otel-gateway LB

### collector

#### processors

##### References
  - Doc about the [attributesprocessor]
  - Doc about the [k8sattributesprocessor]

#### exporters
We are using the following exporters in our setup:

  - [Loki Exporter](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/exporter/lokiexporter/README.md) To send logs to LOKI
  - OTLP Exporter: To send data from the collectors and gateways to the gateways (OTEL internal)
  - OTLPHTTP Exporter: To send Metric data to Mimir and Traces data to Tempo.

### TLS
The communication between the clusters is encrypted using mTLS.
This needs to be configured in each cluster.
For now, check the [configuration of lab2] as an example.

#### No-TLS
In our setup, we expect that the certificates for TLS are mounted under
`/etc/tls-config`.  If you want to run this deployment (e.g. in local) without
TLS, you have to remove any exporter in `otel-*-config-exporters.yaml` that has
any ca files configured (or provide some file), otherwise you will face errors,
even if you are NOT using this exporter.

## Development

### Helm deployment
We also use the exporters from [Prometheus Community Helm Charts] in the usual
fashion to deploy the following components:
  - [Event Exporter]
  - [Kube State Metric]
  - [Node Exporter]
  - [DCGM Exporter]
  - [Prometheus Blackbox Exporter]

For development purpose, there are currently also the dumped helm-chart for the
three OTEL components. They can be removed once we have finished developing the [OTEL]
pipeline.

<!-- Below are the links used in the document -->
[CRD for pod and service monitors from Prometheus]:../athena-system/prom_monitors-0.68.0/
[DCGM Exporter]:https://github.com/NVIDIA/dcgm-exporter
[Event Exporter]:https://github.com/resmoio/kubernetes-event-exporter
[FluxCD post build variable substitution]:https://fluxcd.io/flux/components/kustomize/kustomizations/#post-build-variable-substitution
[Kube State Metric]:https://github.com/kubernetes/kube-state-metrics
[Node Exporter]:https://github.com/prometheus/node_exporter
[OTEL]:https://opentelemetry.io/
[OpenTelemetry Operator for Kubernetes]:https://github.com/open-telemetry/opentelemetry-operator#opentelemetry-operator-for-kubernetes
[Prometheus Blackbox Exporter]:https://github.com/prometheus/blackbox_exporter
[Prometheus Community Helm Charts]:https://github.com/prometheus-community/helm-charts/
[athena-system]:../athena-system/
[attributesprocessor]:https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/fa262b405e5e10ed398379db680e58438e6778f9/processor/attributesprocessor
[configuration of lab2]:../../environments/lab2/clusters/mgmt-east/athena-pipeline/kustomization.yaml
[k8sattributesprocessor]:https://pkg.go.dev/github.com/open-telemetry/opentelemetry-collector-contrib/processor/k8sattributesprocessor
# OTEL Cluster Metrics Collector

All this deployment does, is collect the _cluster_ specific metrics and
resource attributes.

## TL;DR
Collect the `k8s_cluster` metrics, change the dots in the names to underscore
(. -> _ ), append some labels and send the metrics to the local `otel-gateway`.

## Detailed changes

  - Metric renamed, replacing dots with underscore. E.g. k8s.node.status -> k8s_node_status
  - Following labels amended :
    - aws_region: `$agora_environment_region` (flux cluster var)

## doc
  - [k8s_cluster receiver on github](https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/main/receiver/k8sclusterreceiver)
  - [k8s_cluster receiver doc](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/receiver/k8sclusterreceiver/documentation.md)
  - [k8s_cluster receiver metadata](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/receiver/k8sclusterreceiver/metadata.yaml) (same as doc, but quicker to parse)

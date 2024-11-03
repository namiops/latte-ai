# Changelog
## Upcoming
  - kube-state-metrics-5.6.4-agora1: New version for Kubernetes 1.26
  - otel-gateway-agora2

## Changelog
  - otel-agent-agora5:
    - Disable debug logs
  - otel-targetallocator-agora2
    - Disable debug logs
  - otel-gateway-agora5:
    - Increase Memory limit to 6GB
    - Increase Max Message Size for GRPC to 128MB
  - otel-agent-agora4
    - Increase Memory limit to 2GB to prevent it from being OOM'ed.
  - otel-gateway-agora4:
    - Enable endpoints for tracing.
    - add http endpoint back (for tracing)
  - otel-agent-agora3:
    - `reloader` added. Auto-reload pods when configmaps are changed.
    - remove http endpoint
  - otel-gateway-agora3:
    - `reloader` added. Auto-reload pods when configmaps are changed.
    - Bump of hardware limits, as previous one already runs out of resources on lab.
  - kube-state-metrics-4.29.0-agora1: Version up to address deprecation of API in the next Kubernetes version.

## Initial versions
These are the versions when "going live".

  - dcgm-exporter-3.2.0-agora1
  - event-exporter-0.1.0-agora1
  - kube-state-metrics-4.20.3-agora1
  - node-exporter-4.24.0-agora1
  - otel-agent-agora2
  - otel-clustermetrics-agora1 _(currently not used)_
  - otel-exgateway-agora1
  - otel-gateway-agora2
  - otel-targetallocator-agora1
  - targets-agora1

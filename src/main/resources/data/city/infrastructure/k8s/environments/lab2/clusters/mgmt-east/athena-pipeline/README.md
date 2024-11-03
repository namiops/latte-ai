# MGMT athena-pipeline

This is the `athena-pipeline` in the mgmt cluster.
There are some differences to the pipeline in default and worker configuration.

## TLS
The certificates for mtls encryption are provided by the `ExternalSecret` operator.
See [otel-tls-config/](otel-tls-config).

## MGMT cluster specific settings

### LoadBalancer

For the worker nodes to be able to connect, we run a LB and expose the following ports:
  - 4317 otel grpc
  - 4318 otel http

external DNS:
  - otel-gateway.mgmt-east.agora-lab.w3n.io
  - otel-gateway.mgmt-west.agora-lab.w3n.io

### Gateway
The gateways from the worker cluster connect to the gateways in the mmgt cluster via external DNS.


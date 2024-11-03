# MGMT athena-pipeline

This is the `athena-pipeline` in the mgmt cluster.
There are some differences to the pipeline in default and worker configuration.

## TLS
The certificates for mtls encryption are provided by the `ExternalSecret` operator.
See [otel-tls-config/](otel-tls-config).

## MGMT cluster specific settings

### Gateway
The gateways from the worker cluster connect to the gateways in the mmgt cluster via external DNS.


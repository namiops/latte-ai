# MGMT athena-pipeline

This is the `athena-pipeline` in the mgmt cluster.
There are some differences to the pipeline in default and worker configuration.

## TLS
The certificates for mtls encryption are provided by the `ExternalSecret` operator.
See [otel-tls-config/](otel-tls-config).

## MGMT cluster specific settings

### Gateway
The gateways from the worker cluster connect to the gateways in the mmgt cluster via external DNS.

# Athena External Gateway

This gateway is provisioned so that external services can forward telemetry data to the loki/mimir/tempo stack.

To use this gateway:

1. Add an entry to terraform to allow the `serviceaccount` and `namespace` as an audience [here](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/dev2/base/vault-o11y-pki.tf#L114)
2. Add `serviceaccount`, `externalsecret` and `vaultdynamicsecret` to your namespace to issue a TLS certificate from vault PKI (example TBA)
3. Configure your application to mount MTLS config using the TLS cert, key, and CA cert
4. Send the telemetry data to `otel-exgateway-collector.athena-pipeline:4317` (grpc) or `otel-exgateway-collector.athena-pipeline:4318` (http) using the `otlp` protocol, seen [here](https://opentelemetry.io/docs/specs/otlp/)

## Example

Add an entry in the terraform code for vault like such

```hcl
        worker1-east-yournamespace = { # Update this field accordingly
          backend             = module.worker1_east_eks.vault_jwt_auth_endpoint
          k8s_namespace       = "yournamespace"      # Update this field
          k8s_service_account = "yourserviceaccount" # Update this field
        }
        worker1-west-yournamespace = { # Update this field accordingly
          backend             = module.worker1_west_eks.vault_jwt_auth_endpoint
          k8s_namespace       = "yournamespace"      # Update this field
          k8s_service_account = "yourserviceaccount" # Update this field
        }
```

Add a `vaultdynamicsecret` and `externalsecret` along with the required `serviceaccount` as the audience

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: yourserviceaccount # Update this field
  namespace: yournamespace # Update this field
```

```yaml
apiVersion: generators.external-secrets.io/v1alpha1
kind: VaultDynamicSecret
metadata:
  name: o11y-external-pki-issuer
  namespace: yournamespace # Update this field
spec:
  method: POST
  resultType: Data
  path: /o11y-pki-dev2-o11y-external/issue/o11y-pki-dev2-o11y-external-role
  parameters:
    alt_names: '*.yournamespace' # Update this field, along with any alternate domain names of your choice
    common_name: o11y-pki-dev2-o11y-external-role
    ttl: 26h
  provider:
    version: v2
    server: https://dev.vault.tmc-stargate.com
    namespace: ns_dev/ns_cityos_platform
    auth:
      jwt:
        path: jwt-dev2-worker1-east
        role: o11y-pki-dev2-o11y-external-worker1-east-yournamespace-role # Update accordingly
        kubernetesServiceAccountToken:
          serviceAccountRef:
            audiences:
              - https://kubernetes.default.svc
            name: yourserviceaccount # Update this field
            namespace: yournamespace # Update this field
```

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: o11y-external-tls-config
  namespace: yournamespace # Update this field
spec:
  dataFrom:
    - sourceRef:
        generatorRef:
          apiVersion: generators.external-secrets.io/v1alpha1
          kind: VaultDynamicSecret
          name: o11y-external-pki-issuer
  target:
    name: o11y-external-tls-config
    creationPolicy: Owner
    deletionPolicy: Retain
    template:
      engineVersion: v2
      mergePolicy: Replace
      data:
        ca.crt: '{{ .issuing_ca }}'
        tls.crt: '{{ .certificate }}'
        tls.key: '{{ .private_key }}'
  refreshInterval: 24h
```

After generating the kubernetes `secret` using `externalsecret`, mount the secret into your service

```yaml
  - patch: |
      - op: add
        path: /spec/template/spec/containers/0/volumeMounts/-
        value:
          name: tls-external-config
          mountPath: /etc/tls-external-config
          readOnly: true
      - op: add
        path: /spec/template/spec/volumes/-
        value:
          name: tls-external-config
          projected:
            sources:
              - secret:
                  name: o11y-external-tls-config
                  items:
                    - key: ca.crt
                      path: ca.crt
                    - key: tls.crt
                      path: tls.crt
                    - key: tls.key
                      path: tls.key
    target:
      kind: Deployment
      name: otel-exgateway
```

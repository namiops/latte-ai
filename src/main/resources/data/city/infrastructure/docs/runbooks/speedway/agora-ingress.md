# Agora Ingress Operation

Currently, there are two kinds of ingress gateways implemeted by Istio Gateway:

- [agora-city-private-ingress](../../../../infra/k8s/agora-city-private-ingress/)
- [agora-city-public-ingress](../../../../infra/k8s/agora-city-public-ingress/)

## Manage a TLS certificate by ExternalSecret

Istio Gateway's pod read a TLS certificate from Secret which is set per hosts on `Gateway.networking.istio.io`. For example, in `agora-city-private-ingress`, these three Secrets (`kubernetes.io/tls` type) are used for Stable URLs, and are deployed by ExternalSecret:

- agora-stable-url-adm-cert: `*.woven-city-adm.toyata`
- agora-stable-url-api-cert: `*.woven-city-api.toyata`
- agora-stable-url-default-cer: `*.woven-city.toyata`

If any teams need to deploy a custom TLS certificate for your own service exposed to one of those Gateway above, Infra team recommends to deploy the certificate through ExternalSecret. 
Benifits of using ExternalSecret are the followings: 

- Reproducibility
- Automatic Secret update
- Your team can manage a value of your TLS certificate from your Vault namespace without waiting for Infra team

The followings explain steps to perform this operation:

- Vault setup (recommend to do it by Terraform)
- SecretStore and ExternalSecret setup

**NOTE:** All examples are based on Speedway DEV GC environment. Make sure to adjust some values for Production or ML cluster.

### 1 Create a secret in your Vault namespace

**NOTE:** This assumes that you have already created KV engine. If you have not, see the example of `module "agora_vault_kv_engine"` at [dev2/vault/infra/main.tf](../../../terraform/environments/dev2/vault/infra/main.tf)

It is recommend to upload to Secret to a common path so that Secret can be shared between ingress names. For example, you may use a path `common/city-ingress/my-awesome-cert`.

According to [Key Formats](https://istio.io/latest/docs/tasks/traffic-management/ingress/secure-ingress/#key-formats), your secret MUST have 2 keys as follows:

- `tls.crt`
- `tls.key`

In case of mTLS, the addtional key `ca.crt` MUST be added.

### 2 Create a Vault authentication engine

You need to create JWT authentication engine so ServiceAccount in K8S can authenticate with Vault. See example of `agora_vault_jwt_auth_bootstrap` module at at [dev2/vault/infra/main.tf](../../../terraform/environments/dev2/vault/infra/main.tf).

**NOTE:** More details are described in [Authentication Engine for SMC clusters](../../../terraform/environments/prod/vault/README.md#authentication-engine-for-smc-clusters)

### 3 Create a Vault role

Then, create Vault role to allow new ServiceAccounts in ingress namespaces to get your Secret. See example of `agora_vault_jwt_backend_role/kv_engine_read_only` module at at [dev2/vault/infra/main.tf](../../../terraform/environments/dev2/vault/infra/smc_eks_gc_0_east-role.tf).

1. `agora-city-private-ingress-dev` NS: `<your_cert_name>-secret-reader` ServiceAccount
2. `agora-city-public-ingress-dev` NS: `<your_cert_name>-secret-reader` ServiceAccount

For example:

1. `agora-city-private-ingress-dev` NS: `my-awesome-cert-secret-reader` ServiceAccount
2. `agora-city-public-ingress-dev` NS: `my-awesome-cert-secret-reader` ServiceAccount

### 4 Create SecretStore, ExternalSecret, and ServiceAccount

**NOTE:** 
- You can see working example at [stable-urls](../../../../infra/k8s/agora-city-private-ingress/speedway/common/tls-certs/stable-urls)
- You can generate these files with Helm chart [helm/vault-external-secrets](../../../../infrastructure/helm/vault-external-secrets)

Create a new folder under [agora-city-private-ingress/speedway/common/tls-certs](../../../../infra/k8s/agora-city-private-ingress/speedway/common/tls-certs). For example, `agora-city-private-ingress/speedway/common/tls-certs/my-custom-cert`

Then, create SecretStore object like below:

```yaml
---
# Source: vault-external-secrets/templates/secret-store.yaml
apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: my-awesome-cert-store
spec:
  provider:
    vault:
      server: https://dev.vault.tmc-stargate.com
      namespace: <your_vault_namespace>
      path: <your_kv_engine_used_in_step_1>
      version: "v2"
      auth:
        jwt:
          path: <your_jwt_auth_from_step_2>
          role: <your_role_from_step_3>
          kubernetesServiceAccountToken:
            serviceAccountRef:
              name: <your_cert_name>-secret-reader
            audiences:
              - vault
            expirationSeconds: 600

```

Next, create ExternalSecret object like:

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: my-awesome-cert-store
spec:
  refreshInterval: 15m
  secretStoreRef:
    # The name of SecretStore from above
    name: my-awesome-cert-store
    kind: SecretStore
  target:
    # The name of generated Secret
    name: my-awesome-cert-store
    template:
      type: kubernetes.io/tls
  data:
    - secretKey: tls.crt
      remoteRef:
        # Secret path from step 1
        key: common/city-ingress/my-awesome-cert-store
        # A key in Secret from step 1
        property: tls.crt
    - secretKey: tls.key
      remoteRef:
        key: common/city-ingress/my-awesome-cert-store
        property: tls.key
```

Lastly, create a ServiceAccount

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: <your_cert_name>-secret-reader
  namespace: agora-city-private-ingress-dev
```

### 5 Deploy them to the ingress namespace

Modify [kustomization.yaml](../../../../infra/k8s/agora-city-private-ingress/speedway/dev/kustomization.yaml) to add your new folder.

```
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../common/city-private-ingress-istio/auth-policies
  - ../common/city-private-ingress-istio/ingress-0.0.1
  - ../common/tls-certs/stable-urls
  - ../common/tls-certs/my-awesome-cert    # <---- Add your folder here
```

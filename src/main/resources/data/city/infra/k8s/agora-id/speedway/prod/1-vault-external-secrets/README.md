# Speedway prod cluster vault setup
follow the Vault Setup document [here](/ns/vault/docs/vault_setup.md) with these env
## Setup speedway production vault
```bash
export VAULT_ADDR="https://vault.tmc-stargate.com"
export VAULT_NAMESPACE="ns_stargate/ns_prod_agoraid"
export VAULT_JWT_PATH="jwt-gc-0-apps-prod-ap-northeast-1"
export ISSUER="https://oidc.eks.ap-northeast-1.amazonaws.com/id/B74F6B2B9B1A56C04FC1C8817F02A3B4"
export SA_NAME="agora-id-credential-secrets"
export CLUSTER_NAME="speedway-prod"
export K8S_NAMESPACE="agora-id-prod"
```


## Generate vault from template
```
export cluster=prod
helm template agora-id-credential ./infrastructure/helm/vault-external-secrets --output-dir /tmp/speedway/$cluster/ --values ./infra/k8s/agora-id/speedway/$cluster/1-vault-external-secrets/values.yaml
cp -LR /tmp/speedway/$cluster/vault-external-secrets/. ./infra/k8s/agora-id/speedway/$cluster/1-vault-external-secrets/
```
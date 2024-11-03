# Identity credential vault setup

- Generate new vault external secrets template
- Setup Vault

## Generate new vault external secrets template
Agora Devrel team provide a helm chart to generate a template of external secret. 
```
export cluster=dev
helm template agora-id-system-credential ./infrastructure/helm/vault-external-secrets --output-dir /tmp/speedway/$cluster/ --values ./infra/k8s/agora-id/speedway/$cluster/1-vault-external-secrets/values.yaml
cp -LR /tmp/speedway/$cluster/vault-external-secrets/. ./infra/k8s/agora-id/speedway/$cluster/1-vault-external-secrets/
```
## Setup Vault
### Prepare variable
```
export VAULT_NAMESPACE="ns_stargate/ns_dev_agoraid"
export CLUSTER_NAME=speedway-dev
export VAULT_JWT_PATH=jwt-gc-0-apps-ap-northeast-1
export ISSUER=https://oidc.eks.ap-northeast-1.amazonaws.com/id/F11A5118565811C24A9FE43601C7323D
export SA_NAME=agora-id-system-credential-secrets
export K8S_NAMESPACE=agora-id-system-dev
```


### Create new KV engine
```
vault secrets enable -path=speedway-dev kv-v2
```

### generate or write any secret to vault
Example
```
vault kv put -mount=speedway-dev oauth2client-operator\
    CLIENT_SECRET=<client-secret>
   
```


### Create policy
```
vault policy write read-id-system - <<EOF
path "speedway-dev/oauth2client-operator" {
  capabilities = [ "read" ]
}
EOF
```

### Create auth method
```
$ vault auth enable -path=$VAULT_JWT_PATH jwt
$ vault write auth/$VAULT_JWT_PATH/config oidc_discovery_url="${ISSUER}"
```


### Bind auth method with policy
```
vault write auth/$VAULT_JWT_PATH/role/read-all-${K8S_NAMESPACE} \
    role_type="jwt" \
    bound_audiences="vault" \
    user_claim_json_pointer=true \
    user_claim="/kubernetes.io/namespace" \
    bound_subject="system:serviceaccount:${K8S_NAMESPACE}:${SA_NAME}" \
    policies="default,read-id-system" \
    ttl="1h"
```

## References
* [Vault documentation available at developer portal](https://developer.woven-city.toyota/docs/default/Component/vault-docs/)
* [Best practices](https://github.com/wp-wcm/city/blob/main/ns/vault/docs/best_practices.md)

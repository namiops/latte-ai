# Secret Vault in Identity Stack
## What we are using?
HashiCorp Vault provided by EnTech. All environment share the same vault URL

```
https://dev.vault.w3n.io/ui/vault/dashboard
```

You should be able to login with your Azure AD account (Woven Planet Account)

> You suppose to see a namespace of `ns_starget/ns_dev_wcmshared_agora_identity` 

Here is some visualize explaination
```
vault namespace:
ns_stargate/ns_dev_wcmshrd_agora_identity


    │     ┌────────────────────────────────────┐
    │     │  secret engine:                    │
    │     │  `dev`                             │
    │     │                                    │
    │     │ ┌───────────────────────────┐      │
    │     │ │   keycloak-woven-aad-sso  │      │
    │     │ └──────────────────▲────────┘      │
    ├─────┤                    │               │
    │     │  ┌──────────┐   secret             │
    │     │  │   drako  │◄──path               │
    │     │  └──────────┘                      │
    │     │                                    │
    │     └────────────────────────────────────┘
    │
    │     ┌──────────────────────────────────┐
    │     │   secret engine:                 │
    │     │  `dev2-worker1-east`             │
    │     │                                  │
    │     │  ┌───────────────────────────┐   │
    │     │  │   keycloak-woven-aad-sso  │   │
    │     │  └───────────────────────────┘   │
    │     │                       ▲          │
    ├─────┤  ┌──────────┐         │          │
    │     │  │   drako  │◄───── secret       │
    │     │  └──────────┘       path         │
    │     │                                  │
    │     └──────────────────────────────────┘
    │
    │     ┌──────────────────────────────────┐
    │     │   secret engine:                 │
    │     │  `lab2-worker1-east`             │
    │     │                                  │
    │     │   ┌──────────────────────┐       │
    │     │   │keycloak-woven-aad-sso│       │
    │     │   └──────────────────────┘       │
    │     │                      ▲           │
    │     │   ┌──────────┐       │           │
    └─────┤   │   drako  │◄── secret         │
          │   └──────────┘    path           │
          │                                  │
          └──────────────────────────────────┘
```



## How to setup Vault?
Here, several commands for setup a vault. According to https://github.com/wp-wcm/city/blob/main/ns/vault/docs/setup.md#how-to-set-up-vault

```
## enable secret key-value store for each separate path by cluster
vault secrets enable -path=lab2-worker1-east kv-v2
vault secrets enable -path=dev2-worker1-east kv-v2
vault secrets enable -path=dev kv-v2

## initialize kv store
vault kv put -mount=lab2-worker1-east keycloak-woven-aad-sso AAD_TENANT_ID=placeholder AAD_APPLICATION_ID=placeholder AAD_CLIENT_SECRET=placeholder
vault kv put -mount=lab2-worker1-east drako COOKIE_SECRET=placeholder 

vault kv put -mount=dev2-worker1-east keycloak-woven-aad-sso AAD_TENANT_ID=placeholder AAD_APPLICATION_ID=placeholder AAD_CLIENT_SECRET=placeholder
vault kv put -mount=dev2-worker1-east drako COOKIE_SECRET=placeholder

vault kv put -mount=dev keycloak-woven-aad-sso AAD_TENANT_ID=placeholder AAD_APPLICATION_ID=placeholder AAD_CLIENT_SECRET=placeholder
vault kv put -mount=dev drako COOKIE_SECRET=placeholder

## Create policy to access kv store
vault policy write "agora_lab2_worker1_east_keycloak_secret_read" - <<EOF
    path "lab2-worker1-east/data/keycloak-woven-aad-sso" {
        capabilities = [ "read" ]
    }
EOF

vault policy write "agora_lab2_worker1_east_drako_secret_read" - <<EOF
    path "lab2-worker1-east/data/drako" {
        capabilities = [ "read" ]
    }
EOF

vault policy write "agora_dev2_worker1_east_keycloak_secret_read" - <<EOF
    path "dev2-worker1-east/data/keycloak-woven-aad-sso" {
        capabilities = [ "read" ]
    }
EOF

vault policy write "agora_dev2_worker1_east_drako_secret_read" - <<EOF
    path "dev2-worker1-east/data/drako" {
        capabilities = [ "read" ]
    }
EOF

## Setup authentication from Agora cluster
vault auth enable -path=kubernetes-lab2-worker1-east kubernetes
vault auth enable -path=kubernetes-dev2-worker1-east kubernetes
vault auth enable -path=kubernetes-dev kubernetes

# switch kubectx to lab2-worker1-east 
export KUBE_HOST=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.server}')
export KUBE_CA_CERT=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.certificate-authority-data}' | base64 --decode)
export ISSUER=${OIDC from lab2 worker1 east cluster}
vault write auth/kubernetes-lab2-worker1-east/config \
    kubernetes_host="${KUBE_HOST}" \
    kubernetes_ca_cert="${KUBE_CA_CERT}" \
    issuer="${ISSUER}" \
    disable_iss_validation=true

# switch kubectx to dev2-worker1-east 
export KUBE_HOST=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.server}')
export KUBE_CA_CERT=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.certificate-authority-data}' | base64 --decode)
export ISSUER=${OIDC from dev2 worker1 east cluster}
vault write auth/kubernetes-dev2-worker1-east/config \
    kubernetes_host="${KUBE_HOST}" \
    kubernetes_ca_cert="${KUBE_CA_CERT}" \
    issuer="${ISSUER}" \
    disable_iss_validation=true

# switch kubectx to dev 
export KUBE_HOST=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.server}')
export KUBE_CA_CERT=$(kubectl config view --raw --minify --flatten --output='jsonpath={.clusters[].cluster.certificate-authority-data}' | base64 --decode)
export ISSUER=${OIDC from dev cluster}
vault write auth/kubernetes-dev/config \
    kubernetes_host="${KUBE_HOST}" \
    kubernetes_ca_cert="${KUBE_CA_CERT}" \
    issuer="${ISSUER}" \
    disable_iss_validation=true


# bind policy to kubernetes auth service account
vault write auth/kubernetes-lab2-worker1-east/role/agora-id-secretstore \
    bound_service_account_names="vault-id" \
    bound_service_account_namespaces="id" \
    policies="agora_lab2_worker1_east_keycloak_secret_read,agora_lab2_worker1_east_drako_secret_read"


vault write auth/kubernetes-dev2-worker1-east/role/agora-id-secretstore \
    bound_service_account_names="vault-id" \
    bound_service_account_namespaces="id" \
    policies="agora_dev2_worker1_east_keycloak_secret_read,agora_dev2_worker1_east_drako_secret_read"

vault write auth/kubernetes-dev/role/agora-id-secretstore \
    bound_service_account_names="vault-id" \
    bound_service_account_namespaces="id" \
    policies="agora_dev_keycloak_secret_read,agora_dev_drako_secret_read"

```
## K8S manifest example

### secret store (pre-prod cluster)
```

apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: stargate-vault-id
  namespace: id
spec:
  provider:
    vault:
      server: "${external_vault_address}"
      namespace: "ns_stargate/ns_dev_wcmshrd_agoraidentity"
      path: "lab2-worker1-east" # Path to "SECRET ENGINE"
      version: "v2"
      auth:
        kubernetes:
          mountPath: "kubernetes-lab2-worker1-east" # Path of auth
          role: "agora-id-secretstore" # Name of auth Role
          serviceAccountRef:
            name: vault-id
---
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: stargate-vault-id-keycloak-aad
  namespace: id
spec:
  refreshInterval: "15s"
  secretStoreRef:
    name: stargate-vault-id
    kind: SecretStore
  target:
    name: stargate-vault-id-keycloak-woven-sso-aad
  data:
  - secretKey: AAD_TENANT_ID
    remoteRef:
      key: keycloak-woven-aad-sso 
      property: AAD_TENANT_ID
  - secretKey: AAD_APPLICATION_ID
    remoteRef:
      key: keycloak-woven-aad-sso
      property: AAD_APPLICATION_ID
  - secretKey: AAD_CLIENT_SECRET
    remoteRef:
      key: keycloak-woven-aad-sso
      property: AAD_CLIENT_SECRET



```

### vault injection (dev cluster)

```

```



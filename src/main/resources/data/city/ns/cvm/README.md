# Certificate Vending Machine

## Running in VS code in debugging mode

For debugging CVM code in VC code, you have to configure both server side and sample client golang app.
> **Note**  Following configuration uses lab cluster.

### Prepare configuration for launch.json

```json
{
    "name": "Launch CVM Server for debug000",
    "type": "go",
    "request": "launch",
    "mode": "auto",
    "program": "cvm/cmd/main.go",
    "env": {
        "CONFIG_PATH": "/path/to/cvm_local_server_config.json",
        "SKIP_CONFIG_SAFE_PATH": "true", // For MacOS users only(see `Note` for more details)
    }
}
```

Local server configuration file `cvm_local_server_config.json`:
lab
```json
{
    "PKI_FLAGS": {
        "PKI_PROVIDER_TYPE": "vault",
        "VAULT_PKI_FLAGS": {
            "VAULT_PKI_URL": "https://dev.vault.w3n.io:8200/v1/pki-cvm-lab",
            "VAULT_PKI_TOKEN_FILE": "/vault/token-cvm-lab",
            "VAULT_PKI_NAMESPACE": "ns_stargate/ns_dev_wcmshrd_agoraservices",
            "VAULT_PKI_ROLE": "cvm-2024"
        },
        "PCA_PKI_FLAGS": {
            "PCA_PKI_REGION": "ap-northeast-1",
            "PCA_PKI_PRIVATE_CA_ARN": "arn:aws:acm-pca:ap-northeast-1:your-account-number:certificate-authority/2a9904e6-26ba-4890-b54d-36609f9a8da4",
            "ROLE_TO_ASSUME": "arn:aws:iam::your-account-number:role/EC2-SSM-Access-Role",
            "CERTIFICATE_RETRIES": 15
        }
    },
    "IDP_FLAGS": {
        "IDP_CLIENT_ID": "cvm",
        "IDP_CLIENT_SECRET": <IDP_CLIENT_SECRET>,
        "IDP_URL": "https://id.agora-lab.woven-planet.tech",
        "IDP_ALLOWED_CLIENTS": [
            "cvm-test-pub","admin-cli"
        ]
    },
    "SERVER_FLAGS": {
        "SRV_PORT": "8081"
    }
}
```
lab2
```json
{
    "PKI_FLAGS": {
        "PKI_PROVIDER_TYPE": "vault",
        "VAULT_PKI_FLAGS": {
            "VAULT_PKI_URL": "https://dev.vault.w3n.io:8200/v1/pki-cvm-agora-lab2-worker-1-east",
            "VAULT_PKI_TOKEN_FILE": "/vault/token-cvm-lab2",
            "VAULT_PKI_NAMESPACE": "ns_stargate/ns_dev_wcmshrd_agoraservices",
            "VAULT_PKI_ROLE": "cvm-2024"
        }
    },
    "IDP_FLAGS": {
        "IDP_CLIENT_ID": "cvm",
        "IDP_CLIENT_SECRET": <IDP_CLIENT_SECRET>,
        "IDP_URL": "https://id.agora-lab.w3n.io",
        "IDP_ALLOWED_CLIENTS": [
            "cvm-test-pub","admin-cli"
        ]
    },
    "SERVER_FLAGS": {
        "SRV_PORT": "8081"
    }
}
```
AWS PCA
```json
{
    "PKI_FLAGS": {
        "PKI_PROVIDER_TYPE": "aws_pca",
        "PCA_PKI_FLAGS": {
            "PCA_PKI_REGION": "ap-northeast-1",
            "PCA_PKI_PRIVATE_CA_ARN": "arn:aws:acm-pca:ap-northeast-1:your-account-number:certificate-authority/2a9904e6-26ba-4890-b54d-36609f9a8da4",
            "ROLE_TO_ASSUME": "arn:aws:iam::your-account-number:role/EC2-SSM-Access-Role",
            "CERTIFICATE_RETRIES": 15
        }
    },
    "IDP_FLAGS": {
        "IDP_CLIENT_ID": "cvm",
        "IDP_CLIENT_SECRET": <IDP_CLIENT_SECRET>,
        "IDP_URL": "https://id.agora-lab.woven-planet.tech",
        "IDP_ALLOWED_CLIENTS": [
            "cvm-test-pub","admin-cli"
        ]
    },
    "SERVER_FLAGS": {
        "SRV_PORT": "8081"
    }
}
```

IDP_CLIENT_SECRET can be obtained from lab cluster:

The kubectl commands are not needed if you are using AWS PCA
```
# lab
$ kubectl config use-context lab
$ kubectl get secrets -n id keycloak-client-secret-cvm -o json | jq -r .data.CLIENT_SECRET | base64 -d
# lab2
$ kubectl config use-context lab2-worker1-east
$ kubectl get secrets -n id keycloak-client-secret-cvm -n cvm -o json | jq -r .data.CLIENT_SECRET | base64 -d
```

`token-cvm-lab` file can create as following:
```
# lab
$ kubectl config use-context lab
$ kubectl exec -it "$(kubectl get pod -n cvm -l app=cvm -o json | jq -r '.items[].metadata.name')" -n cvm -c vault-agent -- cat /vault/token > /vault/token-cvm-lab
# lab2
$ kubectl config use-context lab2-worker1-east
$ kubectl exec -it "$(kubectl get pod -n cvm -l app=cvm -o json | jq -r '.items[].metadata.name')" -n cvm -c vault-agent -- cat /vault/token > /vault/token-cvm-lab2
```


> **Note** If your OS (MacOS) does not allow to create `/vault` directory on root directory, set `SKIP_CONFIG_SAFE_PATH` as true.



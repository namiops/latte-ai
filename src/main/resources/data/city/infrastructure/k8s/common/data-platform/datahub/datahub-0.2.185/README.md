# datahub in common

Our Datahub depends on the followings:

- Kafka (MSK)
- Postgresql (PGO)
- Elastic Search (datahub prerequisites)
- Entech's Hashcorp Vault

These prerequisites should be deployed before the main datahub component so the directory is divided into two.

## Manual operation

We need to create the secret first.

### Non-local env (e.g. dev2/lab2)

The secret is stored in Vault.
Please refer to https://wovencity.monday.com/docs/5348359962?blockId=8b84c167-3ff4-4755-8737-4743b03aa346.

### Local env

```shell
kubectl create ns datahub
kubectl label namespaces datahub name=datahub
kubectl label namespaces datahub istio.io/rev=default

kubectl create secret generic datahub-gms-secret --from-literal=datahub.gms.secret=<PASSWORD> -n datahub

kubectl create secret generic datahub-encryption-secrets --from-literal=encryption_key_secret=<PASSWORD> -n datahub
```

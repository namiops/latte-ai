# Thermopylae CRD

Thermopyle CRD is the library/binary for configuring third party identity providers.

## Apply generated manifest

```shell
bazel run //ns/id/thermopylae/crd:gen_thermopylae_crd | kubectl -n id apply -f -
```

## Manifest example

```
apiVersion: id.woven-city.global/v1alpha1
kind: IdentityProvider
metadata:
    namespace: id
    name: federation-ext-google
spec:
    oidc:
        issuer_url: https://accounts.google.com
        client_id: federation-ext-google
        secret_reference_name: federation-ext-google  

## Create secret(currently only for local environment)

Create a secret which name is same as `secret_reference_name`

```shell
kubectl create secret generic federation-ext-google -n id --from-literal=client_secret=<client-secret-value>
```




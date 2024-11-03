# ADR-BE-0001 Store Provision Secret Securely

| Status            | Last Updated |
| ----------------- | ------------ |
| Drafted, Approved | 2023-10-06   |

## Context and Problem Statement

IoTA returns a secret called `provision secret` when we create a device group.
This value is required to generate certificates when initializing devices.
Due to the following reasons, the backend must store provision secret securely and provide it to users whenever needed.

- It will be needed in multiple timings and places.
- We can generate device certificates with it, thus the value must not be known to anyone other than the workers.

From the context above, this ADR mentions how to store secrets securely.

## Considered Options

Two storage options:

- SecureKVS
- Postgres Cluster

### SecureKVS Pros and Cons

- Pros
  - Managed encryption (We don't have to encrypt/decrypt secrets manually)
- Cons
  - The location of data about OTA group is dispersed.
  - The number of components will increase as Management API Server hasn't introduced SecureKVS yet.

### Postgres Cluster Pros and Cons

- Pros
  - We can store `provision secret`s in the existing table `ota_groups`.
- Cons
  - We have to encrypt/decrypt secrets manually.
  - This option also increases a `Vault` component to store encryption secrets securely.
    - However, this is a sidecar component and does not have much impact on the application.

## Decision Outcome

We chose **the latter option (Store provision secrets in the existing table in our Postgres Cluster)** to avoid data dispersing.

### System changes

- Create a vault namespace `ns_dev/ns_wcm_infra/ac_access_control`
- Add an encryption secret `kv-dev/provisioning_secret_enc_key`

Format:
`secret_value` is generated by the command `openssl enc -aes-256-cbc -k secret -P -md sha1`.

```json
{
  "key": "{secret_value}"
}
```

- Configure [Vault agent sidecar injector](https://developer.hashicorp.com/vault/docs/platform/k8s/injector) to inject the secret to service pods.
- Service pods store provisioning secrets with encryption when users create a new OTA group (that corresponds to device groups in IoTA).

## Note

- 2023-10-06 : Drafted, Approved Originator: Hajime Miyazawa
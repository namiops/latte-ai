# vault-external-secrets

![Version: 0.8.1](https://img.shields.io/badge/Version-0.8.1-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: argo-0.0.1](https://img.shields.io/badge/AppVersion-argo--0.0.1-informational?style=flat-square)

Generates Manifests to allow the use of the External Secrets Operator (https://external-secrets.io/latest/)

## Supported Environments

This chart is currently supported in the following environments

* Preproduction (dev2)
* Speedway - Non-production
* Speedway - Production

## Pre-requisites

You **MUST** be using Vault to leverage the use of this chart. 
Please refer to our [Vault Documentation](../../../ns/vault/docs/README.md) for more information on how to set it up

## Values

For more details refer to the example [values YAML](values.yaml)

| Key                                       | Type     | Default                    | Required | Description                                                                                                                             |
|-------------------------------------------|----------|----------------------------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|
| argo.enabled                              | bool     | `false`                    | yes      | Indicates that ArgoCD is used to enable Chart Management (See 'Using Argo' for details)                                                 | 
| secretStore.isProduction                  | bool     | `false`                    | yes      | Indicates if production, if set to `true` then `jwt` is set as the default Vault Auth Engine                                            |
| secretStore.secretStoreType               | string   | `"jwt"`                    | yes      | Indicates the type of Vault Auth Engine associated with the SecretStore [`jwt`, `kubernetes`]                                           |
| secretStore.storeName                     | string   | `""`                       | yes      | The metadata name of the SecretStore                                                                                                    |
| secretStore.vaultAuthEnginePath           | string   | `""`                       | yes      | The name of the Vault Authentication Engine                                                                                             |
| secretStore.vaultNamespace                | string   | `""`                       | yes      | The name of the Vault Namespace                                                                                                         |
| secretStore.vaultSecretEnginePath         | string   | `""`                       | yes      | The name of the Vault Secret Engine                                                                                                     |
| namespace                                 | string   | `""`                       | no       | The namespace for the manifest deployment                                                                                               |
| secretStore.vaultRoleName                 | string   | ""                         | no       | The name of the Vault Role attached to the Vault Auth Engine                                                                            | 
| externalSecrets                           | object[] | []                         | no       | Array of External Secrets to generate                                                                                                   |
| externalSecret.interval                   | string   | 1h                         | no       | The amount of time to wait before values are read from the Secret Store. Valid time units are “ns”, “us” (or “µs”), “ms”, “s”, “m”, “h” | 
| externalSecret.secretKeys                 | string[] | []                         | no       | Array of Keys to be added to the Kubernetes Secret                                                                                      |
| externalSecret.secretKeys.name            | string   | `""`                       | no       | The Kubernetes Secret key name                                                                                                          |
| externalSecret.secretKeys.vaultSecretKey  | string   | `""`                       | no       | The Vault Secret underlying Key                                                                                                         |
| externalSecret.secretKeys.vaultSecretName | string   | `""`                       | no       | The Vault Secret Name                                                                                                                   |
| externalSecret.templated                  | object[] | []                         | no       | Sets a template for your secret format. See "Templating a Secret" for more details                                                      |
| externalSecret.secretName                 | string   | `""`                       | no       | The generated Kubernetes Secret name                                                                                                    |
| serviceAccount.create                     | bool     | `true`                     | no       | Specifies whether a service account should be created                                                                                   |
| serviceAccount.name                       | string   | `"<MY_NAMESPACE>-secrets"` | no       | The name of the service account to link to the secret store, if blank a default is provided                                             |
| serviceAccount.annotations                | object   | `{}`                       | no       | Annotations to add to the service account                                                                                               |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.13.1](https://github.com/norwoodj/helm-docs/releases/v1.13.1)

## Usage

Execute from the directory with this README

```shell
helm template my-external-secret . --output-dir ./path/to/output/dir --values ./path/to/values.yaml
```

See our [examples directory](./examples) for examples of usage and results

## Templating a Secret

The chart allows you to leverage the use of templated values in your secret per the External Secret Operator specification. 
For more details on this please read [here](https://external-secrets.io/latest/guides/templating/)

This is useful if you have the need to format your secret in a specific manner such as a configmap or a JSON config file

### Example

With the following config file:

```yaml
datasource:
  - name: my-database
    url: "{{ .db_url }}"
    password: "{{ .db_password }}"
    user: "{{ .db_user }}"
```

You can set this in the values file:

```yaml title="external-secrets.yaml"
externalSecrets:
  - secretName: my-secret
    secretKeys:
      - name: db_url
        vaultSecretName: test_db
        vaultSecretKey: db_url
      - name: db_password
        vaultSecretName: test_db
        vaultSecretKey: db_password
      - name: db_user
        vaultSecretName: test_db
        vaultSecretKey: db_user
    templated:
      config: |
        datasource:
        - name: my-database
          url: "{{ .db_url }}"
          password: "{{ .db_password }}"
          user: "{{ .db_user }}"
```

This will allow you to map the secret like so:

```yaml
spec:
  # FIELDS OMITTED
  target:
    name: my-secret
    template:
      data:
        config: |
          datasource:
          - name: my-database
            url: "{{ .db_url }}"
            password: "{{ .db_password }}"
            user: "{{ .db_user }}"
  data:
    - secretKey: db_url
      remoteRef:
        key: test_db
        property: db_url
    - secretKey: db_password
      remoteRef:
        key: test_db
        property: db_password
    - secretKey: db_user
      remoteRef:
        key: test_db
        property: db_user
```

## Using Argo

Agora recommends to leverage ArgoCD when deploying to Speedway. This allows loose coupling between the chart and your configuration.

Enabling Agro requires 2 steps: 

1) In your namespace's `citycd.yaml` file, add configuration to add additional applications to your deployment.

```yaml title="citycd.yaml"
name: my-namespace
applications:
  vault-external-secrets-store:
    type: helm
    path: infrastructure/helm/vault-external-secrets
    targetRevision: main
    repoURL: https://github.com/wp-wcm/city
hosts:
  speedway:
    dev:
      additionalApps:
        - name: vault-external-secrets-store
```

This tells Argo to look for a file called `vault-external-secrets-store.values.yaml` in the related environment (`dev` or `prod`)

2) Enable Argo support in your `MY_NAMESPACE/speedway/[dev|prod]/vault-external-secrets-store.values.yaml` file:

```yaml title="values.yaml"
# Indicates if the use of ArgoCD is enabled
# When enabled, Agro manages the generation of manifests per the provided manifest values.yaml file
# Kustomization files are not created in this mode
# By Default, this is set to false
argo:
  enabled: true
```

When deployed a new ArgoCD Application is deployed with a name of `<MY_NAMESPACE>-Vault-External-Secrets-Store`

![argo-1](./assets/argo-1.png)

Clicking the resource will show a summary. If you click `Sources` you should see the list of values configured for your chart

![argo-2](./assets/argo-2.png)

## Contact

Please feel free to reach out to Agora via our [AMA Channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7)

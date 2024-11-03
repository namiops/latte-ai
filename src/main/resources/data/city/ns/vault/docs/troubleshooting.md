# Troubleshooting Secret Management

This document outlines recommended practices for troubleshooting issues when
setting up your secrets in Agora.

## How to Ask Questions to Agora

When you are experiencing an issue with your secret management, please follow
these steps:

* Submit your question to the [Agora AMA Channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).
* Provide the following information:
  * Your namespace
  * Your environment (Gen2/Gen3(dev or prod))
  * The output of the following commands
    * `vault read auth/<YOUR_AUTH_ENGINE_NAME>/<YOUR_ROLE_NAME>`
    * `vault policy read <YOUR_POLICY_NAME>`
    * `vault kv get <PATH_TO_YOUR_SECRET>`

This will help Agora determine the issue more quickly

## Common Issues With Secret Management

### Using Kubernetes

You can use your `kubectl` permissions to describe the following issues. Some common commands are:

* `kubectl -n <MY_NAMESPACE> describe secretstore <STORE_NAME>`
  * Gets information about the SecretStore
* `kubectl -n <MY_NAMESPACE> describe externalsecret <SECRET_NAME>`
  * Gets information about the ExternalSecret


### The Subject claim is incorrect

If you're seeing the following in your `describe`:

```shell
error validating token: invalid subject (sub) claim
```

This typically happens when there is something wrong with your policy and role configuration.
Please re-confirm the information is correct on your vault policy and vault role.

### External Secret cannot read keys

Please confirm on either the Vault UI or CLI if your secret paths are correct, and that your vault policy allows access to those paths.
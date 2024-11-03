# Vault

### Why Vault?

Hashicorp Vault is a secrets manager that allows developers to store and access secrets
programmatically in a safe and secure way. We chose to use vault over other secrets managers
because it would offer more advantages for us. Using Github Secrets for all services would 
eventually lead to many secrets stored in Github, which is hard to manage. Meanwhile kubernetes 
secrets would be harder to set up and automate given that we do not own the kubernetes cluster.
And using AWS Secrets manager would make it much more difficult to centralize the secrets
management.

### How It's Set Up

The monorepo's set up for Vault mainly follows the [guide](https://github.tri-ad.tech/information-security/vault-tools/tree/master/guides/github_actions)
provided by the security team. If you wish to know more, please check it out.

## Setup

This module requires access to the following web APIs

* AWS admin level access to AWS account 093116320723
* Maintainer level permissions to WIT Vault namespace ns_dev/ns_cityos_platform on dev.vault.tmc-stargate.com

### AWS setup

```shell
$ saml2aws login --idp-account=ci
$ export AWS_PROFILE=ci
```

### Vault setup

```shell
$ export VAULT_ADDR=https://dev.vault.tmc-stargate.com
$ vault login -method=oidc
$ export VAULT_TOKEN=$(cat ~/.vault-token)
$ export VAULT_NAMESPACE=ns_dev/ns_cityos_platform
```

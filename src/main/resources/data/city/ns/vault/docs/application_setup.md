# How To Set Up Your Application Deployment

This document explains how to set up your Kubernetes Deployments to use Vault as discussed in [How To Set Up Vault](vault_setup.md).

!!! Warning
    This document assumes that you are already using Agora, and have resources that are using Agora CD services.
    If you don't use Agora CD for your Infrastructure, you should contact the Agora Team for help with onboarding first via the [#wcm-org-agora-ama](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7) channel

## How To Use External Secrets

!!! Note
    This is only available in newer environments currently.
    If you're working in the `dev` cluster, this is not available.
    For how to use Vault in legacy environments refer to the [Vault Agent Documentation](./vault_agent.md)

The use of the External Secrets is an alternative way to leverage Vault and
provide the following benefits:

* Provide an easier configuration for communication with Vault
* Ability to work with Vault Secrets in an easier way via Kubernetes Secrets

### Using the Helm Chart

The recommended way to set up the use of External Secret Operator is to use Agora's Helm Chart.
This chart is built to help manage your Vault deployment in the cluster for you.
Please refer to the [Helm Chart Documentation](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/vault-external-secrets) for details

### Using IAM Roles for Service Accounts (IRSA) with External Secrets

If you are using both IRSA and Vault in your application deployments, you can do one of the recommended strategies

* Create a separate ServiceAccount for IRSA
  * This would allow you to have loose coupling and keep concerns separate at a cost of having an additional ServiceAccount
* Add the role annotation to your existing ServiceAccount
  * This allows you to use a single or fewer ServiceAccounts at the cost of having a ServiceAccount with high privileges
  * Use the Helm Chart to add the annotation to your Vault ServiceAccount

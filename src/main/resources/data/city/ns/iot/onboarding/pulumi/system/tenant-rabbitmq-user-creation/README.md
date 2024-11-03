# Remote Pulumi

## What?

This is a Pulumi program to create a RabbitMQ user and export it as Vault secret.

## Why?

User credentials have to be distributed to our tenants in a secure manner, this stack writes credentials to Vault,
then the tenants needs to [synchronize](#synchronizing-your-secrets) those credentials using bazel rules to their namespaces

## How?

This will be located and executed by the onboarding service.

Add your users to a file named `Pulumi.<ENV>_<TENANT-NAME>.yaml`.

File contents reference (`<VHOST>` in most cases should be equal to your tenant name):
```yaml
config:
  users:
    - sample-usr

  # sample-usr permissions, key format: <USER>:<VHOST>.<PERMISSION>
  sample-usr:sample.permissions:
    configure: ".*"
    write: ".*"
    read: ".*"

  # sample-usr topic permissions, key format: <USER>:<VHOST>.topic-permissions
  sample-usr:sample.topic-permissions:
    - exchange: "amq.topic"
      write: "^.*"
      read: "^.*"

  # a kubernetes namespace which will be allowed to access credentials in Vault, key format: <USER>:k8s-secret-namespace
  sample-usr:k8s-secret-namespace: tenant-namespace-prod

  # user tags, key format: <USER>:tags
  sample-usr:tags:
    - management
    - policymaker
    - monitoring

  # sample-usr:password-rotation-trigger: foo123 # a random string to trigger password rotation.
```

Optionally you may request a code ownership for your file to add additional users without the Services Team approval later.

If you want users to be bound to different vhosts - please use `Pulumi.<ENV>.yaml` file and push a PR to the Services Team.

## Synchronizing your secrets

In order to sync your credentials to your namespace please do the following:

1. In one of your BUILD files, next to your kubernetes manifests, please add the following lines:
    ```
    load("//ns/iot/iota/ytt:iota_speedway_ytt.bzl", "iota_rabbitmq_speedway_user_sync")
    
    iota_rabbitmq_speedway_user_sync(
        name = "iota_users",
        values = "iota-users.yaml",
    )
    ```
2. Then add `iota-users.yaml` file with the following contents next to it:
    ```
    #@data/values
    ---
    tenant: <YOUR-TENANT-NAME, ex: sample>
    iotaEnvironment: <TARGET-IOTA-ENV: dev/prod, ex: prod>
    users:
    - <YOUR-RMQ-USERS-LIST, ex: sample-usr>
    ```
3. Run this bazel from the same folder: `bazel run :iota_users.copy`
4. Add a generated file `z-iota-users-user-sync.yaml` to your `kustomization.yaml`
5. Run following commands to format your files properly:
   ```
   bazel run //:buildifier
   bazel run //:gazelle
   ```
6. And finally create and merge a PR; your secret will appear in the cluster in 15 min.

> [!NOTE]  
> You only need to do this once, for every next RabbitMQ user you add or remove from your [stack](#how), you simply add or remove it from `iota-users.yaml`.

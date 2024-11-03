# Agoractl IoTA

Plugin to generate IoTA tenants.

## Introduction

In order for a development team to use IoTA to provision IoT devices, it needs to have a _tenant_ registered with IoTA.
This enables the identification of the team's IoT devices and allows IoTA to partition them away from the devices
owned by other teams. This once rather tedious manual procedure has now been automated with this plugin.

For more information about Agora's IoTA services, please refer to the [Developer Portal](https://developer.woven-city.toyota/docs/default/Component/iota-service)

## Available sub-plugins

Currently, there is only one sub-plugin available for IoTA: `IoTATenant`

### Tenant plugin

Current capabilities of a `tenant` sub-plugin are:
- create: creates a tenant and adds users to it if any.
- delete: deletes a tenant.
- update: updates tenant's user list.
- get: prints tenant's information.

#### Available arguments:
```
    "operation", type=str, choices=["create", "get", "update", "delete"] -> "Operation to be done with a tenant."
    "name", type=str -> "Tenant name."
    "-l", "--keycloak-login", type=str -> "Keycloak user with administrative privileges."
    "-p", "--keycloak-password", type=str -> "Keycloak password for privileged user."
    "-e", "--environment", default="lab", choices=["lab", "dev", "lab2-worker1-east", "dev2-worker1-east"] -> "Environment name."
    "-au", "--add-users", action="append" -> "Users to be assigned to the tenant group."
    "-ru", "--remove-users", action='append' -> "Users to be removed from the tenant group."
```

#### Available environments:

- lab
- dev
- lab2-worker1-east
- dev2-worker1-east

This corresponds to cluster contexts here:
- https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/lab/README.md
- https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/README.md
- https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/README.md
- https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/README.md

#### Preparation:

Setup variables for plugin to use depending on a target cluster (ex: lab):

```
KEYCLOAK_ENV=lab
KEYCLOAK_LOGIN=$(kubectl --context=$KEYCLOAK_ENV -n id get secret credential-keycloak -o jsonpath='{.data.ADMIN_USERNAME}' | base64 -d)
KEYCLOAK_PASSWORD=$(kubectl --context=$KEYCLOAK_ENV -n id get secret credential-keycloak -o jsonpath='{.data.ADMIN_PASSWORD}' | base64 -d)
TARGET_TENANT=your-tenant-here
alias agoractl-iota="bazel run //ns/agoractl -- iota"
```

Note that for lab2 and dev2 envs you have to use `credential-keycloak-22` secret instead of `credential-keycloak`.

#### Creating a tenant (PR needed):

Execute following query, where `user1` and `user2` are the usernames you want to add to a tenant:

```
agoractl-iota tenant create $TARGET_TENANT -l=$KEYCLOAK_LOGIN -p=$KEYCLOAK_PASSWORD -e=$KEYCLOAK_ENV -au=user1 -au=user2
```

#### Updating a tenant:

Adding/removing additional users, where `user3` is the user you want to add and `user4` is the user you want to remove from a tenant:

```
agoractl-iota tenant update $TARGET_TENANT -l=$KEYCLOAK_LOGIN -p=$KEYCLOAK_PASSWORD -e=$KEYCLOAK_ENV -au=user3 -ru=user4
```

#### Describing a tenant:

```
agoractl-iota tenant get $TARGET_TENANT -l=$KEYCLOAK_LOGIN -p=$KEYCLOAK_PASSWORD -e=$KEYCLOAK_ENV
```

#### Deleting a tenant (PR needed):

```
agoractl-iota tenant delete $TARGET_TENANT -l=$KEYCLOAK_LOGIN -p=$KEYCLOAK_PASSWORD -e=$KEYCLOAK_ENV
```

# FAQ and Troubleshooting

This page is meant to go over some of the more common questions and issues that
people have brought to Agora over time. Please find more common problems and
possible solutions here.

## Types of Keycloak Clients

Agora is currently working with three types of clients that Keycloak provides

* Internal Confidential Clients
  * This is a client that can store or hold secrets in a secure manner. This is
    typically a backend API application or a normal web application that can be
    run on the server side (in this case Agora) and therefore, users are not
    typically given access to the code
  * When you deploy "CityService" manifest on the cluster, the platform will
    assign this internal confidential client for you.
  * The services deployed on the cluster can use this client to interact with
    ID provider for token introspection, and configuring permissions.
* External Confidential Clients
  * Similar to the Internal Confidential Clients, but these applications are external
    to Agora
* External Public Clients
  * This is for clients that cannot secure data or secrets from Agora

### Naming Conventions for Keycloak Clients

Agora has a naming convention for the client's names (`.spec.client.clientId`) we use

* For internal confidential clients(used by CityService and Drako): `<namespace>`
* For external confidential clients: `<namespace>/<service>-external`
* For external public clients: `<namespace>/<service>-public`

Keycloak client custom resource also have special field `id`, it used by database
and it must be UUID. Agora has a convention for `id`

* `id` must be `UUID` version `5`
* `UUID` namespace should be `NAMESPACE_URL`
* `UUID` name should be `.spec.client.clientId`

You can use `agora-id` to generate UUID, for example
```
agora-id keycloak uuid -n mynamespace -c myservice --public --profile lab2
```

## How to Set Up a Public Client for Keycloak

### Situation

Some applications cannot store or read sensitive data securely. A few examples:

* An HTML Single-Page Web application (SPA)
* A Native Mobile Application

With these applications, if secrets were sent to these applications they could
be easily read, due to the ability to see the code and values in the memory on
their machines. To allow these applications access to Agora, we use a Public
Client.

### Use Cases

* A native application or a single-page web application (SPA) needs to connect
  to Agora
* A service developer for a native application or single-page web application
  (SPA) wants to test and verify against Agora's Identity systems in a
  development environment

### How to create a public client

If you need a public client, please reach out to the Agora Team via the
[Agora AMA Chanel](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7).
All requests **must** be handled by the Agora team.

A list of current public clients is
visible [here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/id/public-clients.yaml)

### How to use the public client

To use the public client, an application needs to make sure it provides the
correct **client_id** of the Keycloak Client. Also, make sure that you are
hitting the following correct parameters for the Keycloak Client:

* Web Origin
  * **NOTE:** for native applications, web origins are not required as native
    applications cannot handle web origins
* Redirect URLS
  * **MUST** be a valid URL format for both full links and 'deep' links

These both are set up via the Keycloak Client. If you require a specific
redirect URL or web origin, please inform the Agora team when you make your
request.

As an example, a call your application can make to the External Public Client
would look like:

```
https://id.cityos-dev.woven-planet.tech/auth/realms/woven/protocol/openid-connect/login-status-iframe.html/init?client_id=<namespace>-<service>-public&webOrigin=<redirectURL>
```

## I no longer connect to my EC2 instance.

### Situation
I get below error on `ssh ec2-dev`

```
kex_exchange_identification: Connection closed by remote host
Connection closed by UNKNOWN port 65535
```

### Troubleshooting

- [ ] Have you authenticated `aws sso login --profile <profile>`?
- [ ] If yes, it's likely the instance ran out of storage.
```
- OSError: [Errno 28] No space left on device
```
- [ ] Please check with @agora-infra team on slack about the cause.
- [ ] If needed, follow below steps:
  1. Re-create your instance.

     a. [Remove your current instance, example PR](https://github.com/wp-wcm/city/pull/32482).

     b. [Re-create your instance, example PR](https://github.com/wp-wcm/city/pull/32582).
  2. Create a directory on a mounted volume that isn't the root volume and set `MINIKUBE_HOME` to that directory.

## Local Deployment: Flux(in ec2) is not tracking my branch.

### Situation
While [local cluster deployment](onboarding/local_cluster_deployment.md), it can happen that the flux points to main branch, not your local branch.

### Troubleshooting
* Check which branch flux is tracking.
  ```shell
    flux get sources git city
    ```
  Note: The `REVISION` must point to your branch's commit.
* After ensuring the Git repository is correct, apply the patch command (update `my-branch` below):
  ```shell
    kubectl patch -n flux-system gitrepository city --patch '{"spec": {"ref": {"branch": "my-branch"}}}'
    ```
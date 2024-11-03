# Thermopylae

Thermopylae is the federation service for the Woven City.

Named after the [narrow pass in Lamia, Greece](https://en.wikipedia.org/wiki/Thermopylae),
mostly known by the Greco-Perian wars (300 of sparta).

Thermopylae has multiple feature to enable easy-to-setup third-party authentication.

- IdentityProvider, a custom resource is utilized to configure third-party identity provider
- HTTP server which communicates with the client service to identify user basically following OIDC code flow. 
- gPRC server which communicates with drako to authenticate the identity of the incoming request.

## How to enable authentication with third-party identity provider
1. Set up the authentication application on third-party concole to obtain the credentials. For instance, you can refer to [the documentation](https://developers.google.com/identity/openid-connect/openid-connect) if you would like to set up signing in feature with google accounts.

2. Define IdentityProvider and Secret and apply. please read [README](crd/README.md) for details.

## How to run thermopylae

```shell
bazel run :image.load
```

```shell
kubectl apply -k manifests
```

# Allow requests to certain gRPC paths

**Question:** We want to limit access to our gRPC service through a pre-defined set of gRPC paths. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicyBinding` with the [request paths](../../../crd/DrakoPolicyBinding/#paths) matching your gRPC service definition.

!!! Info
    gRPC is a protocol that uses HTTP/2 under the hood.
    The messages get sent as HTTP/2 data frames and the HTTP method used is _always_ POST.
    Therefore, it's not necessary to specify the [methods](../../../crd/DrakoPolicyBinding/#http-methods) in the binding.

## How to

Suppose we have a service called "Consent" that has a gRPC function called "CheckConsent".

We create the following policy (let us call it `allow-all-policy`) if we want to allow every client to connect to our service `my-service-sa`.

```yaml
--8<--
authorization_scenarios/grpc/drakopolicy.yaml
--8<--
```

!!! Note
    We can also replace `allow-all-policy` with [other type of policies](../../../crd/DrakoPolicy/) if we want to limit access to our service.
    Please check [user authorization scenario](../limit_users/scenario.md) for limiting access to specific users and [client authorization scenario](../service_inout_cluster/scenario.md) for limiting access to specific clients.

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/grpc/drakopolicybinding.yaml
--8<--
```

We give the binding the name `grpc-binding`, specify `Oauth2` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to evaluate a request to allow or reject it.
This binding is matched (i.e. is used to evaluate a request) when a requests goes to one of the paths specified in `paths` on `my-service-sa`.
Though, the binding does not match requests going to `/agora.consent.v0.Consent/SubscribeConsentUpdatesByGroups` as specified in [excludePaths](../../../crd/DrakoPolicyBinding/#excludepaths).

The matched requests will then get evaluated according to `decisionStrategy`.

!!! Note
    If `paths` is not defined, the binding matches on all paths.

## What are the supported authentication modes for gRPC?

Drako supports authenticating gRPC requests with:

- `Oauth2`, `SingleUseToken`, or `None` [authentication mode](../../../crd/DrakoPolicyBinding/#authenticationmode)
- `SpiffeId`, `Oauth2ClientId`, and/or `None` [client authentication mode](../../../crd/DrakoPolicyBinding/#clientauthenticationmodes)

## How does gRPC client pass OAuth2 token in the request?

You can inject the OAuth2 token in the request metadata for every request sent by the client.
We provide external links that demonstrate how to inject the OAuth2 token into the request, depending on the language your client is written in.

- [Rust implementation](https://github.com/hyperium/tonic/blob/master/examples/src/authentication/client.rs)
- [Go implementation](https://github.com/grpc/grpc-go/blob/master/examples/features/authentication/client/main.go)
- [Java implementation](https://github.com/grpc/grpc-java/blob/master/examples/example-oauth/src/main/java/io/grpc/examples/oauth/AuthClient.java)
- [Python implementation](https://github.com/grpc/grpc/blob/master/examples/python/auth/token_based_auth_client.py)

## How does gRPC service retrieve headers set by Drako?

You can retrieve the headers set by Drako from the request metadata.
We provide external links that demonstrate how to retrieve headers from the gRPC request, depending on the language your service is written in.

- [Rust implementation](https://github.com/hyperium/tonic/blob/master/examples/src/authentication/server.rs#L36)
- [Go implementation](https://github.com/grpc/grpc-go/blob/master/examples/features/authentication/server/main.go#L101)
- [Java implementation](https://github.com/grpc/grpc-java/blob/master/examples/example-oauth/src/main/java/io/grpc/examples/oauth/OAuth2ServerInterceptor.java#L42)
- [Python implementation](https://github.com/grpc/grpc/blob/master/examples/python/auth/token_based_auth_server.py#L49)

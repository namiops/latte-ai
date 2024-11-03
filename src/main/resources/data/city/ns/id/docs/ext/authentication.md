---
title: Authentication
summary: About authentication in Woven City
authors:
  - maru@woven.toyota
date: 2024-09-26
---
# Authentication

Authentication is the process of identifying and validating the
identity of a client or user.

This document provides an overview of the available authentication
methods and a high-level description of the steps required to
add authentication support to your application.

## Authentication Mechanism

### Client Authentication

There are multiple ways to authenticate clients, most notably via
a certificate in a mTLS connection or via an OAuth2 token.

When authenticating clients running inside the same service mesh,
by far the easiest way of doing that is via the [XFCC] header, which
is managed by the mesh itself. This, however, requires you to do
some parsing and understand the particular format of the header set
by the mesh.

Another option is to authenticate clients using [OAuth 2.0], however
doing validation of the token can be tricky (leading to security vulnerabilities) and you must be aware
that you are either trusting the user with a public client, or trusting
a confidential client.

### User Authentication

For user authentication, we recommend using [OAuth 2.0]. There are other
flows, such as [SAML] or username/password behind session management,
but those are largely considered legacy and are discouraged in the city.

## Creating an OAuth 2.0 Client

We are currently finalizing the implementation of [Leonidas] as our
[OAuth 2.0] client management solution but it is not quite ready yet.

For now, please reach out to our developer relations teams via slack
and we will guide you through our temporary workaround process.

## Implementing OAuth 2.0 Authentication


### Frontend Applications

This is under construction, but while we work on it:

1. create a public oauth client.
1. initate the login flow from your frontend (redirect to the IdP).
1. from the authorization code get the access and refresh tokens.
1. refresh the token in the background while using a fresh access token in the header.

### Backend Applications

By far the easiest and safest way to add authentication to your application
is to use [drako], the authentication and authorization framework developed by us. This is only
possible if you are hosting your application at Agora and it is a REST application.
Neither gRPC nor GraphQL support planned as of right now.

For details, see the [authorization] page.

If you chose to implement yourself or use a framework, there is significant
overhead with extra steps you will need to take. For example, most 
frameworks do a poor job on token validation, skipping basic azp/client
and aud checks and introspection endpoint calls. If there is no other
option, however, here a few things you will need to make sure are done
properly:

1. Take a note of the important endpoints listed in the [well-known].
1. implement signature validation leveraging [JWKS].
1. call the [introspection] endpoint to make sure token is not revoked.
1. validate the value of azp/client and aud token fields.
1. only then do other authorization logic.

Those steps are required to make sure the token was not tempered with
without overloading the identity provider.

Also, when developing for the city, be aware that you will need to follow the
[Logging Standards][security-logs] provided by the cybersecurity team and 
also do proper SOC integration on your own.

You can still use [drakopolis] as a group management API for your
application when implementing authentication on your own.


[authorization]: ../authorization
[drako]: http://go/drako
[drakopolis]: http://go/drakopolis
[introspection]: https://www.oauth.com/oauth2-servers/token-introspection-endpoint/
[JWKS]: https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets
[Leonidas]: http://go/leonidas
[OAuth 2.0]: https://oauth.net/2/
[SAML]: https://en.wikipedia.org/wiki/Security_Assertion_Markup_Language
[security-logs]: https://security.woven-planet.tech/standards/network/network-security-logging-standard/ "Network Security Logging Standards"
[XFCC]: https://www.envoyproxy.io/docs/envoy/latest/configuration/http/http_conn_man/headers#x-forwarded-client-cert
[well-known]: http://go/woven-id-well-known

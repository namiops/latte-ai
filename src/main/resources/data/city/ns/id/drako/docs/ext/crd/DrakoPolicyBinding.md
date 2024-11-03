# DrakoPolicyBinding

The main function of a `DrakoPolicyBinding` is to bind a specific set of policies with an accompanying `decisionStrategy` to an endpoint. In the context of Drako, they are also called "bindings" for short. Bindings can also map data from the request in a way that can then be further used by the policies you configure.

Like other resources in Drako, bindings are namespace scoped. This means that by default they only refer to other resources within their namespace. For example, `destinationServiceAccounts` can only refer to service accounts on the same namespace, and `policies` can only refer to `DrakoPolicy` resources on the same namespace. Currently, we are designing a mechanism to make resources from other namespaces available in a secure way.

## Binding to an endpoint

You can select which binding applies to a specific request by specifying destination endpoint attributes.
Please make sure that a single request matches with only exactly one binding defined in your namespace.
The destination endpoint attributes that act as filters to get the applicable binding for each request are:

* (Mandatory) The [Kubernetes service account](https://kubernetes.io/docs/tasks/configure-pod-container/configure-service-account/) assigned to the pod that will receive the request, specified in `destinationServiceAccounts` field.
* (Optional) Request paths to be matched (specified in `paths` field) or excluded (specified in `excludePaths` field).
* (Optional) Request methods to be matched, e.g., for different bindings for PUT vs GET methods, specified in `methods` field.
* (Optional) Request destination hosts (`hostname` and `port`) to be matched, specified in `destinationHosts` field.

For example, the binding configuration snippet below applies to pods with the `echo-single-sa` service account located under `/user/list` and `/user/*/profile`.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: <MyBinding>
  namespace: <MyNamespace>
spec:
  destinationServiceAccounts:
    - echo-single-sa
  paths:
    - "/user/list"
    - "/user/*/profile"
  policies: [ ]
  decisionStrategy: ""
```

## `policies` and `decisionStrategy`

The `policies` and `decisionStrategy` attributes allow you to select what policies to apply and how to compute the final authorization results. Extending the previous example:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: <MyBinding>
  namespace: <MyNamespace>
spec:
  destinationServiceAccounts:
    - echo-single-sa
  paths:
    - "/user/list"
    - "/user/*/profile"
  policies:
    - "is-admin"
    - "is-owner"
    - "consent"
  decisionStrategy: "(is-owner || is-admin) && consent"
```

## `paths`

`paths` returns the list of paths used to filter the requests to be processed by this binding.

Before a request is received by your service, Drako searches for a `DrakoPolicyBinding` that matches the request path and the `destinationServiceAccounts`. If none of the paths in a binding matches the incoming request path, Drako will ignore it. A request is denied when Drako cannot find any binding that matches the request.

You can specify the list of acceptable request paths in `DrakoPolicyBinding`. Drako performs exact character matching when comparing request paths against the paths in `DrakoPolicyBinding`.

!!! Note
    If you have not specified `paths` in `DrakoPolicyBinding`, Drako will match it with all incoming requests.

<!-- ADD EXAMPLES AND RESULTS FOR EACH CASE -->

Pattern matching is also supported:

* `:` matches one path segment
* `*` matches zero or multiple path segments
* `+` matches one or multiple path segments

Below is an example of paths for a video sharing service:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: access-video-api
  namespace: tik-poc
spec:
  authenticationMode: Oauth2
  destinationServiceAccounts:
    - api-server
  policies:
    - allow
  decisionStrategy: allow
  paths:
    - '/api/v1/videos'
    - '/api/v1/likes'
    - '/api/v1/comments'
```

If we send a request with the path `/api/v1/videos`, Drako will select this binding because it contains an exact matching path.

However, if we send a request to `/api/v1/videos/dQw4w9WgXcQ` for information about a specific video, this binding will not be selected because the path is not an exact match. We can fix this by adding `/api/v1/videos/:` to the binding as follows:

```yaml
  paths:
    - '/api/v1/videos'
    - '/api/v1/videos/:'
    - '/api/v1/likes'
    - '/api/v1/comments'
```

!!! Note
    Do not remove `/api/v1/videos`, as Drako considers `/api/v1/videos` and `/api/v1/videos/:` to be different paths!

!!! Warning
    You may be tempted to combine `/api/v1/videos` and `/api/v1/videos/:` into one pattern using `*` (i.e., `/api/v1/videos*`), but please don't do this! Although this works with the above example, it would also match paths that are not expected by the service (e.g. `/api/v1/videos-drop-table-comments`), which may lead to security risks.

## `excludePaths`

This attribute specifies the list of paths to be avoided. Drako first checks the request path against [`paths`](#paths). If a match is found, it then scans for `excludePaths` that also match the request path. If a match is found in `excludePaths`, Drako does not select the given `DrakoPolicyBinding`.

In the example below, we want to match all `/api/*` except for `/api/v1`:

```yaml
  paths:
    - '/api/*'
  excludePaths:
    - '/api/v1'
    - '/api/v1/*'
```

This `DrakoPolicyBinding` will be selected for paths such as `/api/v2` or `/api/v3/user`, but not for any paths under `/api/v1`.

## `destinationHosts`

This field specifies the list of destination hosts to decide whether the binding will be applied to the incoming request or not.
Each destination host consists of:

* `hostname` (required): string
* `port` (optional): integer

If the request destination host matches with _one of_ the destination hosts specified in the binding, then the binding will be applied to the request.
The comparison is case insensitive.
To match the binding with any request destination host, simply remove this `destinationHosts` field from the binding.
If the `port` is not specified in the destination host, then only the request destination hostname will be matched against the `hostname` specified in the `destinationHosts`.
If the destination host in the request does not contain any port (e.g., echo-id-test.woven-city.toyota), we will assign a default port number depending on the scheme:

* HTTPS: 443
* HTTP: 80

For example, we want to apply different bindings that have different `authenticationMode` depending on the request destination host.
We need to make sure that each request destination hostname and port match with exactly one binding, otherwise, Drako will return Forbidden response if multiple bindings are found for a request.

* Apply the binding to the destination hostname devices-tsl-telescope.woven-city-api.toyota with None authentication mode.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: robot
  namespace: drako-test
spec:
  authenticationMode: None
  policies:
    - allow-all
  destinationServiceAccounts:
    - tsl-telescope-sa
  decisionStrategy: "allow-all"
  destinationHosts:
    - hostname: devices-tsl-telescope.woven-city-api.toyota
```

* Apply the binding to the destination hostname tsl-telescope.woven-city-api.toyota with Oauth2 authentication mode.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: operator
  namespace: drako-test
spec:
  authenticationMode: Oauth2
  policies:
    - allow-all
  destinationServiceAccounts:
    - tsl-telescope-sa
  decisionStrategy: "allow-all"
  destinationHosts:
    - hostname: tsl-telescope.woven-city-api.toyota
```

* Apply the binding to the destination hostname tsl-telescope-core-service and port 35002 with None authentication mode.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: internal
  namespace: drako-test
spec:
  authenticationMode: None
  policies:
    - allow-all
  destinationServiceAccounts:
    - tsl-telescope-sa
  decisionStrategy: "allow-all"
  destinationHosts:
    - hostname: tsl-telescope-core-service
      port: 35002
```

## `authenticationMode`

Authentication modes (configured via `authenticationMode` attribute) describe how the user should be authenticated when calling your backend. The available options are:

* **`Legacy`:** Session-based authentication completely managed by Drako through the session ID stored in the cookie. This mode can be used to host legacy systems that don't support native integration with OAuth.
* **`Oauth2`:** Bearer or token authentication. The client is expected to generate a valid bearer token and use it in the authorization header when making calls to your backend. The token can be re-used within the expiration period.
* **`SingleUseToken`:** Similar to Oauth2, but the token is automatically revoked once used, regardless of its expiration time. This is the recommended long-term solution for enhanced security.
* **`None`:** Bypasses authentication for anonymous users. You can only pair this authentication mode with policies that do not require a logged user, e.g., `Source`, `Origin`, `AllowAll`, and `DenyAll`. This mode can be used to grant/deny access to external IPs.

Generally, we recommend `SingleUseToken` for improving security and preventing replay attacks. `Oauth2` is an alternative when `SingleUseToken` is not possible. For applications that are not compatible with tokens, such as static content hosting and third-party tools, the use of `Legacy` is possible but not recommended.

!!! Warning
    `None` has serious security implications. In order to use it, a review by the Woven City Product Security Team is mandatory.

## `clientAuthenticationModes`

Client authentication modes describe how the client should be authenticated when calling your service. You can specify more than one client authentication modes through `clientAuthenticationModes` attribute defined in the `DrakoPolicyBinding`.

The available options are:

* **`SpiffeId`:** When `SpiffeId` is specified as one of the client authentication modes, Drako checks if the request contains a SPIFFE ID as the source principal with `spiffe://` prefix. If the requirement is not satisfied, Drako will skip authenticating with this mode. If the requirement is satisfied, Drako will authenticate with this mode by checking if the SPIFFE ID has a valid format: `spiffe://<cluster>/ns/<namespace>/sa/<service-account>`. You can pair this client authentication mode with one or more [ClientGroup DrakoPolicies](DrakoPolicy.md#clientgroup) to authorize a specific SPIFFE ID.

* **`Oauth2ClientId`:** When `Oauth2ClientId` is specified as one of the client authentication modes, the authenticator checks if the request contains header with `authorization` key. If the requirement is not satisfied, Drako will skip authenticating with this mode. If the requirement is satisfied, Drako will authenticate with this mode by checking if the bearer token in the authorization header is valid and contains either `azp` or `client_id` claim. You can pair this client authentication mode with one or more [ClientGroup DrakoPolicies](DrakoPolicy.md#clientgroup) to authorize a specific OAuth2 client identifier.

* **`None`:** When `None` is specified as one of the client authentication modes, and all other specified client authentication modes are skipped, meaning no client identifiers (e.g., SPIFFE IDs or OAuth2 client identifiers) can be captured, Drako will bypass client authentication. This is only useful when you want to authenticate requests from anonymous clients.

!!! Warning
    `None` client authentication mode has serious security implications. In order to use it, a review by the Woven City Product Security Team is mandatory.

Client authentication will be successful if at least one client authentication mode (including `None`) is not skipped and Drako successfully authenticates the client with that authentication mode. If `clientAuthenticationModes` is not specified in the `DrakoPolicyBinding`, Drako will assign `SpiffeId` and `Oauth2ClientId` by default.

## `scope` mapping

To map request methods to specific scopes, extend the previous configuration as follows:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: echo-single
  namespace: drako-test
spec:
  authenticationMode: SingleUseToken
  policies:
    - consent
    - keycloak-or
  destinationServiceAccounts:
    - echo-single-sa
  mapping:
    scope:
      GET: view
      POST: edit
  decisionStrategy: "keycloak-or && consent"
```

In the above example, Keycloak Authorizer will look for resources with the scope `view` when processing `GET` requests, and `edit` when processing `POST` requests. All other undefined methods will be unauthorized when scope mapping is enabled.

## `mapping`

Some policies can use data contained in the requests in their decision process. Drako calls this type of data _mapped data_.

Mapped data is only supported by the following policy types:

* `Consent` (uses mapped data to make decisions on the `dataattrs` needed to fulfill a particular request)
* `Group` (mapped values can be used to form the group name)
* `MappingComparison` (mapped values are checked against criteria defined in the policy)

Currently, we support mapped data from:

* Request headers
* Request path attributes (using the [path-tree](https://crates.io/crates/path-tree) syntax)
* Request query attributes

!!! Note
    Currently, mapping data from a request body is not supported due to privacy concerns. If your application requires this, please [contact us](https://toyotaglobal.enterprise.slack.com/archives/C032Z73091N) and we will collaborate with the Privacy Team to address your needs.

For example:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: echo-single
  namespace: drako-test
spec:
  authenticationMode: SingleUseToken
  policies:
    - consent
    - keycloak-or
  destinationServiceAccounts:
    - echo-single-sa
  mapping:
    scope:
      GET: view
      POST: edit
    paths:
      - "/user/:target/:action?"
    headers:
      x-user-id: "userId"
    queries:
      l: "limit"
  decisionStrategy: "keycloak-or && consent"
```

The above example binding can extract the following data depending on the request path:

| Path | Mapped data |
| -- | -- |
| **`/users/profile/view`** | `target == "profile"` and `action == "view"` |
| **`/users/profile`** | `target == "profile"` (`action` is undefined) |
| **`/users/`** | both `target` and `action` are undefined |

**Headers:**

The mapped value of the `userId` key is set to the value of the `x-user-id` header.

**Queries:**

The mapped value of the `limit` key is set to the query attribute `l`. For example, for `/users/list?limit=100`, `limit` is set to `100`.

### Default values

It is possible to define default values for values that cannot be mapped from the incoming request. In the previous example:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: echo-single
  namespace: drako-test
spec:
  authenticationMode: SingleUseToken
  policies:
    - consent
    - keycloak-or
  destinationServiceAccounts:
    - echo-single-sa
  mapping:
    scope:
      GET: view
      POST: edit
    defaultValues:
      target: timeline
    paths:
      - "/user/:target/:action?"
  decisionStrategy: "keycloak-or && consent"
```

For paths whose `target` cannot be mapped, the value would be set as `timeline`. In the same example:

* **/users/** `target == "timeline"` and `action` is undefined.

Default values can also be used to specify static values for authorizers.

## HTTP methods

You can limit the [HTTP request methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods) to which a policy binding applies by defining the `methods` array attribute.

By default, policy bindings apply to any HTTP request method sent by clients. However, when you define this attribute, the policy will be bound only if the client uses a matching HTTP request method.

In the following example, `OAuth2` authentication is required for all HTTP request methods except `OPTIONS`:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: mybinding-private
  namespace: mynamespace
spec:
  authenticationMode: Oauth2
  policies:
    - allow
  decisionStrategy: allow
  destinationServiceAccounts:
    - echo-single-sa
  methods:
    - HEAD
    - GET
    - POST
    - PUT
    - PATCH
    - DELETE
    - CONNECT
    - TRACE
---
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: mybinding-public
  namespace: mynamespace
spec:
  authenticationMode: None
  policies:
    - allow
  decisionStrategy: allow
  destinationServiceAccounts:
    - echo-single-sa
  methods:
    - OPTIONS
```

!!! Warning
    Applications built with Flutter perform preflight requests using the `OPTIONS` method prior to performing the intended requests.
    If you protect those resources through authentication without whitelisting
    `OPTIONS` (like in the above example), the application will likely encounter [HTTP 403 Forbidden](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/403) errors.

!!! Note
    You can combine the `methods` attribute with other attributes (e.g. `paths`).

## Inject headers

Drako sets headers in the HTTP requests after authorization has successfully completed. These headers may be useful to the destination service for handling the request. By default, the following headers are passed:

* `x-user-id` (required): includes a unique identifier for the logged-in user if authentication is required (authentication is required if `authenticationMode` is not `None`). If `authenticationMode` is `None`, this header will be set with the `anonymous` value.

* `x-auth-request-access-token` (optional): the Bearer authentication token used to authenticate the logged-in user. Guaranteed to be set if authentication is required. Not set otherwise.

* `x-user-name` (optional): the username of the logged-in user. Only set if authentication is required and if this information is passed by the authentication system.

* `x-email` (optional): the email of the logged-in user. Only set if authentication is required and if this information is passed by the authentication system.

In addition to these headers, it is possible to add optional headers by setting the `injectHeaders` property:

```yaml
spec:
  policies:
    - allow-all
  decisionStrategy: "allow-all"
  injectHeaders:
    - type: Group
      spec:
        group:
          policy: is-foo
    - type: Group
      spec:
        group:
          policy: imported-is-bar
    - type: ClientIds
  # ... other fields
```

The following headers can be added this way:

### x-drako-groups

A header that will inform the target service of which DrakoGroups the currently logged-in user belongs to. This header will be populated by adding `Group` items to the `injectHeaders` field. `Group` items can reference `Group` policies or `Import` policies that import a `Group` policy. For each `Group` item set in `injectHeaders`, Drako will check whether or not the caller belongs to the DrakoGroup associated with the item's policy and encode this information in the header value as a JSON string. The following is an example of such JSON string:

```json
{
  "namespaces": [
    {
      "name": "delivery-system",
      "in_group": [
        "drivers"
      ],
      "not_in_group": [
        "managers",
        "admins"
      ]
    },
    {
      "name": "inventory-system",
      "in_group": [
        "collaborators"
      ],
      "not_in_group": [
        "operators",
        "admins"
      ]
    }
  ]
}
```

The header contains a list of namespaces and, in each namespace, the names of the DrakoGroups the currently logged-in user belongs to (`in_group`) and does not belong to (`not_in_group`). In the above example, we can see that the currently logged-in user belongs to the `drivers` group of the `delivery-system` namespace and the `collaborators` group of the `inventory-system` namespace. We can also see groups the user does not belong to: `admins`, `managers` in the `delivery-system` namespace and `admins`, `operators` in the `inventory-system` namespace.

### x-drako-client-ids

A header that will inform the target service about all the client identifiers that were successfully authenticated when handling the request. The header value is a JSON array of authenticated client identifiers as shown below. If `ClientIds` type is specified in `injectHeaders` attribute and the client is successfully authenticated with `SpiffeId` or `Oauth2ClientId` [client authentication mode](#clientauthenticationmodes), Drako will append this header to the request. Each successful client authentication mode will have an entry in the array indicating (1) the client identifier type associated with the client authentication mode where the client identifier is extracted from and (2) the client identifier value, e.g.:

```text
"[{\"type\":\"Oauth2ClientId\",\"value\":\"agora-id-test-local\"},{\"type\":\"SpiffeId\",\"value\":\"spiffe://cluster.local/ns/agora-id-test-local/sa/echo-sa\"}]"
```

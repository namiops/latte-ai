## Rate limiter

<!-- vim-markdown-toc GFM -->

- [Deployment options](#deployment-options)
- [Configuration](#configuration)
  - [Requirements](#requirements)
  - [Setup](#setup)
  - [Example](#example)
- [FAQs](#faqs)
  - [Why does RateLimitServerSettings and RateLimitServerConfig look the same?](#why-does-ratelimitserversettings-and-ratelimitserverconfig-look-the-same)

<!-- vim-markdown-toc -->

### Deployment options

There are two ways to deploy rate limit server:

1. Shared rate limit server for each clusters
  - Pros: Simple. Put one rate limit server in city-gateway workspace and tenants do not need to setup their own
  - Cons:
    - Single point of failure although we can configure sidecar to allow/disallow traffic when rate limit server is down
    - There is no data isolation between workloads
    - (To be confirmed) rate limit server has no leader election meaning that only one redis instance is used.  
      This means sharing rate limit server will increase latencies when it is under a high load
2. Shared rate limit server for each workspace/namespace
  - Pros:
    - Each tenants can control and have less load on their server
    - Better data isolation
  - Cons:
    - Need to setup rate limit server for every clusters they deploy ratelimit policy
    - Same as option 1, no leader election

You can deploy ratelimit server by pointing your kustomization to [this directory](../../../k8s/common/gloo-mesh/gloo-platform-2.4.4-ratelimit1). Example:

```
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: <replace with your namespace>
resources:
- ../../../../../common/gloo-mesh/gloo-platform-2.4.4-ratelimit1
```

### Configuration

#### Requirements

- Ratelimit server's namespace must be istio injected
- Import-Export relations must be established between ratelimit server and worload (if they are in different workspace)

#### Setup

There are three main CRDs you need to configure:

- RateLimitServerSettings
  - This tells sidecar where to find rate limit server
  - This resource **MUST** be deployed in the same workspace as RateLimitPolicy
- RateLimitServerConfig
  - This is to tell ratelimit server to save state in redis using descriptor and ratelimit count
  - This resource **MUST** be deployed in the same workspace as RateLimitPolicy
- RateLimitPolicy
  - This is where policy is being enforced when matches

#### Example


```yaml
apiVersion: admin.gloo.solo.io/v2
kind: RateLimitServerSettings
metadata:
  name: httpbin
  namespace: httpbin
spec:
  destinationServer:
    port:
      name: grpc
    ref:
      cluster: worker1-east
      name: rate-limiter
      namespace: ratelimiter
---
apiVersion: admin.gloo.solo.io/v2
kind: RateLimitServerConfig
metadata:
  name: httpbin
  namespace: httpbin
spec:
  destinationServers:
  - ref:
      cluster: worker1-east
      name: rate-limiter
      namespace: ratelimiter
    port:
      name: grpc
  raw:
    setDescriptors:
      - simpleDescriptors:
          - key: organization
            value: solo.io
        rateLimit:
          requestsPerUnit: 3
          unit: MINUTE
---
apiVersion: trafficcontrol.policy.gloo.solo.io/v2
kind: RateLimitPolicy
metadata:
  name: httpbin
  namespace: httpbin
spec:
  applyToRoutes: # Which RouteTable to apply to
  - route:
      labels:
        ratelimited: "true"
  config:
    serverSettings: # RateLimitServerSettings
      name: httpbin
      namespace: httpbin
      cluster: worker1-east # if not specified, will search in the same cluster as RateLimitPolicy
    ratelimitServerConfig:
      name: httpbin
      namespace: httpbin
      cluster: worker1-east # if not specified, will search in the same cluster as RateLimitPolicy
    raw:
      rateLimits: # enforce when match
      - setActions:
        - requestHeaders:
            descriptorKey: organization
            headerName: X-Organization
---
apiVersion: networking.gloo.solo.io/v2
kind: RouteTable
metadata:
  name: httpbin
  namespace: httpbin
spec:
  http:
    - name: httpbin
      labels:
        ratelimited: "true"
      matchers:
      ...
      forwardTo:
      ...
```

After deploying above resources, you can test this by running:
```bash
for i in $(seq 1 10); do
  curl -sI -H "X-Organization: solo.io" httpbin.local | head -1
done

# should expect:
# HTTP/1.1 200 OK
# HTTP/1.1 200 OK
# HTTP/1.1 200 OK
# HTTP/1.1 429 Too Many Requests
# HTTP/1.1 429 Too Many Requests
# HTTP/1.1 429 Too Many Requests
# HTTP/1.1 429 Too Many Requests
# HTTP/1.1 429 Too Many Requests
# HTTP/1.1 429 Too Many Requests
# HTTP/1.1 429 Too Many Requests
```

### FAQs

#### Why does RateLimitServerSettings and RateLimitServerConfig look the same?

To apply a rate limit policy, two translations are needed:

- Client-side: One translation configures the Envoy filter to apply to the policy’s destinations or routes.
- Server-side: One translation configures the rate limit server.

If the server-side translation happens after the client-side translation, the rate limit configurations might not match.  
This conflict might cause unpredictable behavior on the traffic that is rate limited.  
To avoid such potential conflicts, Gloo Mesh separates the server configuration from policy application.  
  
Client-side: RatelimitServerSettings, RatelimitClientConfig, or in-lined settings on the RatelimitPolicy determine which server for the policy to use, along with the rate limiting actions on the routes or destinations.  
  
Server-side: RatelimitServerConfig configure rules and descriptors for the rate limit server. This resource is translated into an internal RatelimitConfig resource that is sent to the rate limit server. The RatelimitServerConfig is required before you can use a RatelimitPolicy.

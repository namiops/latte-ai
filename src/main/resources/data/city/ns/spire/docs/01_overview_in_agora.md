# Overview in Agora

Here's the overview of how each component is placed/inter-connected.

![agora-overview](./assets/agora-overview.png)

(Source: https://docs.google.com/drawings/d/1E8FN6FyvMgys6mLJVlKvrD3HFng8rQupD65o1UwkRzU/edit)

Let's go over some of the Agora-specific key points.

## Use of X.509 SVID over JWT SVID

As mentioned in the previous page, Spire supports X.509 certificates and JWT as the form of SVIDs. In Agora, X.509 SVID is dedicatedly used. This has 2 reasons.

1. As noted in [the official doc](https://spiffe.io/docs/latest/spire-about/use-cases/#authenticating-two-workloads-using-jwt-based-authentication), JWTs are susceptible to replay attacks, in which an attacker that obtains the token in transit can use it to impersonate a workload. The doc advises to use X.509-SVIDs whenever possible.
2. In Woven City project, [mTLS](https://www.cloudflare.com/learning/access-management/what-is-mutual-tls/) is the standard when it comes to service-to-service communication. X.509 SVIDs can be utilized for mTLS.

!!! tip

    If you encounter a valid use case where JWT SVID can be useful, please reach out to Agora team in [#wcm-org-agora-ama](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7).


## Only external workloads are issued SVIDs by Spire agents

Spire agents only run in the external nodes, and only external workloads are issued SVIDs by Spire agents.

Workloads running in Agora obtain X.509 certificates through a dedicated ClusterIssuer that shares the same PKI hierarchy with Spire stack.

## Trust domains share the same root CA

For the sake of better integration between services that run on different trust domains, all trust domains are supposed to sit under the same root CA.

This makes it possible, for example, that a workload under trust domain A can establish mTLS with trust domain B. If this is not a desired behavior, each workload developer needs to configure authorization rules properly.


## TLS passthrough at Gateway level

In ordinary Agora ingress traffic (such as browser access to Agora services), TLS is once terminated at the Gateway and a separate mTLS session is established between Gateway and the destination service.

In the context of service-to-service traffic, this is problematic. Because Gateway terminates the TLS session in the middle, the internal workload can only recognize Gateway as its interaction peer, not the external workload (vice-versa). This makes proper authorization impossible for both workloads.

To avoid this problem, these workloads utilize TLS passthrough port configured in Gateway, and the TLS termination is done right at the workload. In the following examples, it is the workload’s Istio-sidecar that does the termination inside Agora.

## [x509pop](https://github.com/spiffe/spire/blob/main/doc/plugin_agent_nodeattestor_x509pop.md) for Node attestation

As mentioned, Spire is a highly pluggable software, and how we attest node is one of the things we can configure among [official plugins](https://github.com/spiffe/spire/tree/main/doc).

Agora chose [x509pop](https://github.com/spiffe/spire/blob/main/doc/plugin_agent_nodeattestor_x509pop.md) for the default choice of Node attestation method. This will require a one-time per service onboarding:

1. (Agent-side) Create a keypair for the node that the SPIRE Agent runs on. These keys never leave the agent node. If the service environment already has a PKI implementation, this can be used.
2. (Server-side) Add the CA chain of public keys for this keypair to the Spire Server as trusted, if it is not already

!!! tip

    In the following hands-on, we’ll use a different node-attestation method (`join_token`) so that you can follow through the instructions without being blocked by Agora to approve changes, but keep in mind that in the production setup, x509pop is the method we adopt.


## Responsibility of Agora / service teams

Spire servers for all trust domains are managed by Agora team. Spire agents are run and managed by each of the service teams that run relevant workloads beside them.

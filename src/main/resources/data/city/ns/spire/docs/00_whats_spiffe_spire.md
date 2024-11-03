# What's SPIFFE/SPIRE?

[SPIFFE](https://spiffe.io/) is a set of standards for securely identifying software systems in dynamic and heterogeneous environments, and [SPIRE](https://spiffe.io/docs/latest/spire-about/) is a production-ready implementation of the SPIFFE APIs that performs node and workload attestation in order to securely issue identities to workloads, and verify the identities of other workloads, based on a predefined set of conditions.

## Introductory Video

There is this excellent [intro video](https://www.youtube.com/watch?v=Q2SiGeebRKY) (10mins, English) that talks you through the essentials of SPIFFE/SPIRE. Please have a moment to watch it and come back to this page.

## Key concepts

These terms are used in the following contents. You may skip this section and come back whenever you encounter unfamiliar words.

### [Trust Domain](https://spiffe.io/docs/latest/spiffe-about/spiffe-concepts/#trust-domain)

The trust domain corresponds to the trust root of a system. A trust domain could represent an individual, organization, environment or department running their own independent SPIFFE infrastructure. All workloads identified in the same trust domain are issued identity documents that can be verified against the root keys of the trust domain.

### [SPIFFE ID](https://spiffe.io/docs/latest/spiffe-about/spiffe-concepts/#spiffe-id)

A SPIFFE ID is a URI that uniquely and specifically identifies a workload, which takes the following format: `spiffe://{trust domain}/{workload identifier}`.

For example, `spiffe://acme.com/billing/payments`.

### [SVID](https://spiffe.io/docs/latest/spiffe-about/spiffe-concepts/#spiffe-verifiable-identity-document-svid)

SVID, or formally, _SPIFFE Verifiable Identity Document_, is the document with which a workload proves its identity to a resource or caller. An SVID contains a single SPIFFE ID, which represents the identity of the service presenting it. It encodes the SPIFFE ID in a cryptographically-verifiable document, in one of two currently supported formats: an X.509 certificate or a JWT token.

### [Registration Entry](https://spiffe.io/docs/latest/spire-about/spire-concepts/#workload-registration)

A registration entry is a mapping of an identity – in the form of a SPIFFE ID – and a set of properties that the workload must possess. This tells SPIRE how to identify the workload and which SPIFFE ID to give it.

## SPIRE Architecture Overview

Here's the architecture overview from the Spire official doc.

![spire-architecturfe-overview](./assets/spire-architecturfe-overview.png)

(source: https://spiffe.io/docs/latest/spire-about/spire-concepts/#spire-architecture-and-components)

_*WL = Workload_

_*The gray dotted box = Node_

### Components

#### Spire server

Responsible for managing and issuing all identities in its configured SPIFFE [trust domain](#trust-domain).

#### Spire agent

Runs on every node on which an identified workload runs, and exposes _Workload API_ that the workloads running on the same node use to obtain [SVIDs](#svid).

#### Workload

A single piece of software, deployed with a particular configuration for a single purpose (~= what we call _services_ in general).

### Flow

Here's the basic flow until your service (=workload) gets a reliable identity (i.e. [SVIDs](#svid)).

1. The Spire server starts up for a [trust domain](#trust-domain). It's configured to trust sets of nodes that have certain properties (e.g. node under a certain Kubernetes cluster or AWS account, or node that has a valid key-pair of a certain PKI hierarchy, etc).
2. The Spire agent starts up on the node that the workload is running on, and performs **node attestation**.
    - The agent inspects the information about the node it's running on (e.g. [AWS Instance Identity Document](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-identity-documents.html)) and provides that to the server over TLS so that the server can validate it.
3. You (as a workload developer) run your workload.
4. You (as a workload developer) create a [registration entry](#registration-entry) through Spire servier's _Registration API_ for your workload. This entry contains some properties/conditions about your workload (e.g. Labels of your Kubernetes pod, PID or UID of your process, etc.), and the [SPIFFE ID](#spiffe-id) that is to be assigned to the workloads that fulfill the predefined conditions. Registration entries are exchanged between Spire server and agent through the server's _Node API_.
5. The Spire agent keeps inspecting workloads that run on the same node and exposes _Workload API_.
6. When a workload request an identity (=[SVIDs](#svid)) to the Spire agent through _Workload API_, the agent performs **workload attestation**, namely examines whether the workload's inspecting result matches any of the [registration entries](#registration-entry), and returns the corresponding SVID if matches.

This is a high-level summary of [the flow explanation from the official doc](https://spiffe.io/docs/latest/spire-about/spire-concepts/#a-day-in-the-life-of-an-svid). For more detailed and precise flow, please refer to it.

### Features

#### Two forms of SVID, X.509 certificate and JWT

Spire natively supports two forms of identity document, X.509 certificate and JWT.
For the reasons we'll be discussing on the next page, we dedicatedly use X.509 SVIDs.

#### Short-lived keys

Once the trust is established between workloads - agent - server, secret keys are rotated in a frequent manner (e.g. 1 hour by default) all behind the scene. This makes the entire system extremely secure, even where workloads are distributed in heterogeneous environments.

#### Pluggable architecture

The earlier section explained the node attestation and workload attestation in an abstract way but didn't mention how exactly Spire servers and agents attest nodes or workloads. This is because all these mechanisms, together with other things (where you manage key, etc) are all pluggable.

We'll go over what plugins Agora adopts in each of the pieces on the next page.

#### Use of Unix Domain Socket

It is worth mentioning that the Spire agent's _Workload API_ makes use of [Unix Domain Socket](https://en.wikipedia.org/wiki/Unix_domain_socket). This means the Spire agent and the workloads querying its API open the same socket under the same file system. This is the reason the agent and the workloads need to run on the same node.

## References

- [Youtube: Introduction to SPIFFE and SPIRE Projects](https://www.youtube.com/watch?v=Q2SiGeebRKY)
- [PDF: Solving the Bottom Turtle](https://spiffe.io/pdf/Solving-the-bottom-turtle-SPIFFE-SPIRE-Book.pdf)
- [SPIFFE Concepts](https://spiffe.io/docs/latest/spiffe-about/spiffe-concepts/)
- [SPIRE Concepts](https://spiffe.io/docs/latest/spire-about/spire-concepts/)

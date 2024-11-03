# Comparison with CVM

[Certificate Vending Machine](https://developer.woven-city.toyota/docs/default/Component/cvm-service) (or CVM or short) is another mechanism that let software running outside Agora obtain X.509 certificates. This page compares Spire and CVM and discuss when to use which service.

## Comparison Matrix

|                                                            | Spire                                                                                                 | CVM                                                                                                                                         |
| ---------------------------------------------------------- | ----------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| _Agent_ required to be run beside the external workload    | Yes                                                                                                   | **NO!**                                                                                                                                      |
| Keys are short-lived / rotated automatically               | **YES!**                                                                                               | No                                                                                                                                          |
| Source of trust                                            | [Node attestation config](https://spiffe.io/docs/latest/spire-about/spire-concepts/#node-attestation) | Keycloak account (~= Woven ID)                                                                                                              |
| Who owns the source code                                   | [OSS/Community](https://spiffe.io/docs/latest/spiffe-about/community-presentations/)                  | [Agora Services team](https://docs.google.com/presentation/d/1aIKvJaDbJr_8zY5uHReSadfkEPOAgkK9d1WtLjK4ndI/edit#slide=id.g222b7d15180_2_279) |

The biggest pro of CVM is the simplicity of the model. It is as simple as letting the end-user log in to Agora KeyCloak, provide Certificate Signing Request with the given token and you'll get the certificate. Nothing like _agent_ required to run beside the client (external workload).

Spire's biggest benefit is its short-lived private keys. While it's possible in CVM to keep the private keys short-lived by implementing the app logic to rotate them periodically, in Spire it's done automatically.

Another Spire's selling point is it's [widely used / battle-tested in the industory](https://spiffe.io/docs/latest/spire-about/case-studies/).

## When to use which service?

It is RECOMMENDED to use Spire wherever possible for better security. Especially when it comes to service-to-service communication, Spire is STRONGLY RECOMMENDED way of getting X.509 certs and establishing mTLS.

In case running an agent beside the workload is not feasible, such as the following cases, CVM is a better fit.

- Mobile apps that end-users (with Woven ID) use
- IoT devices that barely have capacity to run Spire agent

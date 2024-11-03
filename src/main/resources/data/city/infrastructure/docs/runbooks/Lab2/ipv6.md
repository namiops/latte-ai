**Table of contents**
<!-- vim-markdown-toc GFM -->

* [Isolating IPv6 Issues](#isolating-ipv6-issues)
    * [Readiness / liveness probe is pointing to IPv4 address](#readiness--liveness-probe-is-pointing-to-ipv4-address)
    * [App is not binding to IPv6 address or port is not open](#app-is-not-binding-to-ipv6-address-or-port-is-not-open)
    * [App logic doesn’t support IPv6](#app-logic-doesnt-support-ipv6)

<!-- vim-markdown-toc -->

## Isolating IPv6 Issues

### Readiness / liveness probe is pointing to IPv4 address

It is very common that the pod shows failing because readiness / liveness probe
cannot reach the endpoint it is being configured. This leads to the assumption
that things are broken on the application side even though the application
itself is healthy. First, check if the probe is failing (by describing the
pod). Second, make sure the probe points to the IPv6 address when doing the
health check.

### App is not binding to IPv6 address or port is not open

Second most common error is when the container does not bind any IPv6
addresses. It may open to IPv4 only, or failing to open the port because it
does not expect IPv6. To check this issue, shell into the container to check if
the port is open (i.e. ss -tulpn) and confirm if it is opening the port (i.e.
using nc / curl / grpcurl).

### App logic doesn’t support IPv6

Related to the check above, after confirming that the port is open. We may or
may not be able to  access the service. This means we need to check if the
service itself supports IPv6 on the local environment before deploying to the
Kubernetes cluster. Another common sign is when the service runs fine on IPv4,
but fails on IPv6 cluster showing CrashLoopBackOff. When you spot this error,
always check the log and describe the pod.

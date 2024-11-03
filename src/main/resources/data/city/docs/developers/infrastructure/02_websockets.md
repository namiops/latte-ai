# Websocket Connections via the Service Mesh

The following is guidelines and general best practices that help applications
with web socket connectivity. This document is meant to be an addition to
the [Istio General Guidelines](01_istio.md) in the prior section.

## Websockets with Istio

Istio provides native websocket support. You can find an example of a web
application deployment on
the [Istio GitHub](https://github.com/istio/istio/blob/master/samples/websockets/README.md).
For most applications, there is no additional setup or considerations that need
to be made.

## Things to look out for and 'gotchas'

### Use `wss` instead of `ws` for external traffic

All traffic into Agora must be authenticated and secured. For traffic coming
into the ingress controller, please remember to use `wss` over `ws` for external
traffic.

### Ensure your application has websocket support

Applications will need to be built with the appropriate websocket support in
order to accept the connection correctly. If you have need to add a websocket
library to your application, please refer to the CI
documentation [here](../../development) for more information on how to add new
dependencies for your application builds.

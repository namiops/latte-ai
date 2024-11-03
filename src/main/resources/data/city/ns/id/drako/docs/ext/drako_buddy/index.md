# Drako Buddy

Drako Buddy is the name of a kubernetes operator that automatically enables [Drako](https://developer.woven-city.toyota/docs/default/Component/drako-service) for all
Agora-hosted HTTP services on [Speedway environment](http://go/tn-0432).

## Usage

With Drako Buddy, your services should be [protected by default](https://docs.google.com/document/d/1mVxoujSJqwdCr3kTTLZDhSE5cLZLq_tj3sg_09TaRsI/edit?usp=sharing) both internally (from other services in the cluster) and externally (from ingress).  
You need to specify the protocol of at least one port of your service as either `http`, `http2`, `grpc`, or `grpc-web` via [explicit protocol selection](https://istio.io/latest/docs/ops/configuration/traffic-management/protocol-selection/#explicit-protocol-selection). See [Example](#example)

## Exceptions

Drako Buddy also supports exceptions. If you have a strong reason not to use Drako and would like to make an exception for your service, please [talk to the Woven City Product Security Team](https://security.woven-planet.tech/processes/policy-exception-and-risk-acceptance-process/) about your use case.

Until we can safely roll out Drako Buddy, we are temporarily switching the default behavior. In order to use Drako Buddy, you need to explicitly define the following label on the target kubernetes service as follows:

```yaml
drako-buddy.woven-city.global/ignore: 'no' # or "false"
```

After the label is set as in the previous example, Drako Buddy will enable Drako for the target kubernetes service by creating the appropriate Istio configurations. If this is the first time using Drako and Drako Buddy, please make sure that you have the appropriate Drako authorization policies in place, otherwise all your requests will be rejected by default. If you haven't done so, please read the [Drako developer docs](https://developer.woven-city.toyota/docs/default/Component/drako-service).
In the future, Drako Buddy usage will be implicit on all deployed HTTP services, so you will not need to use this label unless for the opposite case where you want to disable Drako Buddy on a service and have an exception from the Woven City Product Security Team.

## Example

```yaml
--8<--
drako_buddy/label.yaml
--8<--
```

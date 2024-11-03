# Istio

| Last Update | 2024-08-20            |
|-------------|-----------------------|
| Tags        | Istio, Infrastructure |

## Content

* [Troubleshooting](#troubleshooting)
  * [How to connect to Istio Proxy](#how-to-connect-to-istio-proxy)
  * [FAQs](#faqs)
    * [Can Istio work with websocket connections?](#can-istio-work-with-websocket-connections)
  * [Issues](#issues)
    * [Unable to connect to services outside Agora](#unable-to-connect-to-services-outside-agora)
      * [Symptom](#symptom)
      * [Solution](#solution)
      * [Reference](#reference)
    * [Service Reporting No Healthy Upstream](#service-reporting-no-healthy-upstream)
      * [Symptom](#symptom-1)
      * [Solution](#solution-1)
    * [Service call returns upstream connect error or disconnect/reset before handlers](#service-call-returns-upstream-connect-error-or-disconnectreset-before-handlers)
      * [Symptom](#symptom-2)
      * [Potential Causes](#potential-causes)
      * [Solution](#solution-2)
      * [Reference](#reference-1)
    * [Cannot address to remote address in a specified IP range](#cannot-address-to-remote-address-in-a-specified-ip-range)
      * [Symptom](#symptom-3)
      * [Solution](#solution-3)
      * [References](#references)

## Troubleshooting

### How to connect to Istio Proxy

```shell
$kubectl logs <POD_NAME> -n <NAMESPACE> -c istio-proxy
```

## FAQs

### Can Istio work with websocket connections?

Yes, you can see an example quickstart
on [Istio's GitHub](https://github.com/istio/istio/blob/master/samples/websockets/README.md).
Remember to use `wss` instead of `ws` for connecting to the ingress gateway

## Issues

### Unable to connect to services outside Agora

#### Symptom

Typically a system will see a `connection reset by peer` of `connection reset`
error via the pod like in the example:

```json
{
  "level": "error",
  "msg": "Reading message failed. Error: read tcp [2406:da24:8:c201:6969::14]:40096-\u003e[2001:2::f0f0:89b7]:10002: read: connection reset by peer",
  "time": "2024-04-22T04:33:03Z"
}
```

To further check this run the following:

```shell
k -n <namespace> logs <pod_name> -c istio-proxy
```

If you see `BlackHoleCluster` in the logs this is a symptom

```shell
[2024-04-22T04:26:10.731Z] "- - -" 0 UH - - "-" 0 0 0 - "-" "-" "-" "-" "-" BlackHoleCluster - [2001:2::f0f0:89b7]:10002 [2406:da24:8:c201:6969::14]:45890 - -
[2024-04-22T04:26:15.736Z] "- - -" 0 UH - - "-" 0 0 0 - "-" "-" "-" "-" "-" BlackHoleCluster - [2001:2::f0f0:89b7]:10002 [2406:da24:8:c201:6969::14]:45894 - -
[2024-04-22T04:26:20.740Z] "- - -" 0 UH - - "-" 0 0 0 - "-" "-" "-" "-" "-" BlackHoleCluster - [2001:2::f0f0:89b7]:10002 [2406:da24:8:c201:6969::14]:58840 - -
[2024-04-22T04:26:21.216Z] "- - -" 0 UH - - "-" 0 0 0 - "-" "-" "-" "-" "-" BlackHoleCluster - [2001:2::f0f0:89b7]:10002 [2406:da24:8:c201:6969::14]:58850 - -
```

#### Solution

The main cause is a lack of a
proper [Service Entry](https://istio.io/latest/docs/reference/config/networking/service-entry/).

Create the ServiceEntry per the example. **Make sure the ServiceEntry is
exported to the namespace to prevent potential bad routing for the traffic**:

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  namespace: namespace
  name: external-github
spec:
  hosts:
    - github.com
  exportTo:
    - namespace
  location: MESH_EXTERNAL
  ports:
    - number: 443
      name: https
      protocol: TLS
  resolution: DNS
```

#### Reference

* [Istio: Service Entry](https://istio.io/latest/docs/reference/config/networking/service-entry/)

### Service Reporting No Healthy Upstream

#### Symptom

Calls to a service report a 503 status back

```shell
no healthy upstream
```

#### Solution

Check the status of the pod:

```shell
$ k get pods -n <namespace> 
# OR
$ k get pod <pod_name> -n <namespace>
```

The likely cause is the pod itself is unhealthy or not running. Check
the `READY`field

```
NAME                     READY   STATUS    RESTARTS         AGE
pod                      0/1     Running   1126 (20s ago)   10d
```

If the pod is not up, perform a restart:

```shell
$ k -n <namespace> get deployments
$ ksudo -n <namespace> rollout restart <name_of_deployment>
# OR
$ ksudo -n <namespace> delete pod <pod_name>
```

### Service call returns upstream connect error or disconnect/reset before handlers

#### Symptom

Calls to the service seem to return a 500 or an issue via an API call

Check the `istio-proxy` in the Pod with the following:

```shell
kubectl logs <POD_NAME> -n <NAMESPACE> -c istio-proxy  
```

You might see an entry in the istio-proxy container with something like the
following:

```
2024-05-29T02:46:11.488Z] "GET / HTTP/1.1" 503 UF upstream_reset_before_response_started{remote_connection_failure,delayed_connect_error:_111} - "delayed_connect_error:_111" 0 152 0 - "-" "curl/8.7.1" "b79e6275-8b8f-99a5-8dc2-d1875964f12e" "xr-light-class-management-server:8001" "[2406:da24:8:c202:7327::5]:8001" inbound|8001|| - [2406:da24:8:c202:7327::5]:8001 [2406:da24:8:c200:6d38::17]:35780 outbound_.8001_._.xr-light-class-management-server.xrl-dev.svc.cluster.local default
```

The code `503 UF upstream_reset_before_response_started` is a common status
Istio reports with a connection reset error

#### Potential Cause

One culprit can be the IP is not configured correctly on the Pod. This can
happen in one of two ways

* Pod is hardcoded to have a POD_HOST that will conflict with Istio's settings
* Pod is set to only listen for IPv4 Addresses only

To check for the following IPv4 you can run the following:

```shell
$ kubectl exec <POD_NAME> -n <NAMESPACE> -c istio-proxy -- ss -tulpn | grep 8001
tcp   LISTEN 0      2048         0.0.0.0:8001       0.0.0.0:*   
```

The `0.0.0.0` suggests a IPv4 address listener

#### Solution

Check the ports to see that the application is connected correctly to the
reported IP address of the Deployment. This is reported via Kubernetes and can
be set via the deployment manifest:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-service
  namespace: my-namespace
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-service
  template:
    # Fields Omitted
    spec:
      # Fields Omitted
      containers:
        - name: my-container
          env:
            - name: POD_HOST
              valueFrom:
                fieldRef:
                  fieldPath: status.podIP
```

#### Reference

* [Istio General Guidelines](../../../docs/developers/infrastructure/03_faq.md)

### Cannot address to remote address in a specified IP range

#### Symptom

Reports from Istio's Proxy Cache IP range should appear in the logs for the
local pod. For example:

```
[2024-05-09T00:23:52.990Z] \"- - -\" 0 - - - \"-\" 1233 10595 61089 - \"-\" \"-\" \"-\" \"-\" \"10.252.166.226:443\" outbound|443|apitwiliocom|egressgateway.city-egress.svc.cluster.local 10.252.254.205:55016 240.240.216.70:443 10.252.254.205:55670 api.twilio.com -
```

`240.240` IP range is Istio's DNS Cache. Seeing this would suggest that Istio is
attempting to proxy the connection outward

#### Solution

Submit a `ServiceEntry` for the affected Namespace and specify the range. The
following is an example that sets up a range of IP Addresses to reach out to
Twilio:

```
apiVersion: networking.istio.io/v1beta1
kind: ServiceEntry
metadata:
  name: egress-serviceentry-manual
  namespace: my-namespace
spec:
  addresses:
    # Twilio's IP range
    - 13.115.244.0/27
    - 54.65.63.192/26
    - 18.180.220.128/25
  exportTo:
    - .
    - city-egress
  hosts:
    - global.turn.twilio.com
  ports:
    - name: tls
      number: 443
      protocol: TLS
  resolution: STATIC
```

Also, confirm that the Deployment has set the Istio Proxy config to `enabled`.
This should be under `spec.templatee.metatdata.annotations`:

```yaml
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-service
  template:
    metadata:
      labels:
        app: my-service
      annotations:
        proxy.istio.io/config: |
          proxyMetadata:
            ISTIO_META_DNS_CAPTURE: "true"
            ISTIO_META_DNS_AUTO_ALLOCATE: "true"
```

#### References

* [Istio - DNS Proxying](https://istio.io/latest/docs/ops/configuration/traffic-management/dns-proxy/)


### Istio reports `NR filter_chain_not_found`

#### Causes

The error is a little vauge, but can refer to a few potential issues with

* An invalid `VirtualService`
* An invalid `DestinationRule`
* Lack of configuration with Istio


#### Solution

For VirtualService and DestinationRule errors, check the configuration of the host, port, protocol, and mesh location

Ensure that the problem workloads are in the mesh by making sure that the following annotation is added

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    security.istio.io/tlsMode: istio
```

#### References

* [Istio - Traffic Management Problems](https://istio.io/latest/docs/ops/common-problems/network-issues/)
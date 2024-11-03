# Table of contents

- [How to make an internal `VirtualService` in the Agora Software Platform utilizing Istio?](#how-to-make-an-internal--virtualservice--in-the-agora-software-platform-utilizing-istio-)
  * [Use-case](#use-case)
  + [Context](#context)
  * [Prerequisites](#prerequisites)
  * [Debug pod to verify if `VirtualService` works later on](#debug-pod-to-verify-if-virtualservice-works-later-on)
  * [Kubernetes API Objects you need to define](#kubernetes-api-objects-you-need-to-define)
    + [Service](#service)
    + [VirtualService](#virtualservice)
  * [Verify if internal `VirtualService` works](#verify-if-internal-virtualservice-works)
    + [Curl check](#curl-check)
    + [Envoy Logs](#envoy-logs)

# How to make an internal `VirtualService` in the Agora Software Platform utilizing Istio?

## Use-case
You could need to make a internal `VirtualService` in few different situations:
- When you need to have scallabe read and wirte services accessible by same FQDN internally on different
`path`
- When you need to have one single-FQDN for multiple underlying services on different or same ports
- When you need to add rewrite rules to your service without spanning new additional service
- When you need to add istio fault-injection to your services in single definition
- When you need to supply a different certificate to group of your services in single definition

## Context

For context, in the official Istio documentation, you cannot find a straightforward description of how to make an internal mesh `VirtualHost` with `Path-based` routing or any other type of routing. In this runbook, you will find an explanation of how to make it.

## Prerequisites

* Istio-injected namespace
* Installed istioctl and kubectl
* Exec permissions

## Debug pod to verify if `VirtualService` works later on

Before all operation, you need to have a `pod` for verification of all steps, you can create this by single-command:

```bash
kubectl run -i --tty debug --image=docker.artifactory-ha.tri-ad.tech/yauritux/busybox-curl -n your-namespace
```

Instead of this you can use any other Istio-injected pod with `curl`.

## Kubernetes API Objects you need to define 

### Service

First of all, you need to define a specific Service. Why? Because any FQDN TCP request must be resolved by DNS, and in the current Agora version we cannot modify or add entries to the Internal Kubernetes DNS or specify a new internal endpoint for Kubernetes or Istio Service Discovery. Although `ServiceEntry` should be an answer here, we found out that this does not work properly with `VirtualService` and in effect overrides the `VirtualService` routes in `Envoy` configurations across the internal mesh.

Following the [Istio DNS Proxy](https://Istio.io/latest/docs/ops/configuration/traffic-management/dns-proxy/) documentation, we decided to use `Class E- 240.240.0.0/16` reserved IPv4 pool of addresses - to avoid conflicts. You can check which IPv4 addresses are still possible to use by the following command, although you can assign same VIP IP for different `Services` but in this situation you could observe wrongly counted metrics, so we recommend to use unique VIP if possible.

```bash
$ kubectl get ep --all-namespaces | grep '240.240.'
```

For this we need to create non-headless `Service` without auto-detection of `Endpoints`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: name-of-service # Must be a valid DNS name and be unique per namespace
  namespace: your-namespace
spec:
  type: ClusterIP # DNS Resolver from pod must see Service IP for correct resolve request, with headless service this attempt will fail
  ports:
  - protocol: TCP
    name: http-my-precious-svc # This name should follow the Istio convention name so protocol as a prefix
    port: 3111 # Your port, must be different than port 80, and ports used by Envoy (https://Istio.io/latest/docs/ops/deployment/requirements/#ports-used-by-Istio) and must be unique per namespace as this is virtual service
  externalIPs:
    - 240.240.0.1 #VIP address as an ExternalIP to avoid situations where the Endpoint is overriden by some internal Kubernetes mechanism
```

After that, this `Service` will appear as a `name-of-service.your-namespace` and `FQDN` of this service will be `name-of-service.your-namespace.svc.cluster.local`

At this moment we can verify if your service exists from `Kubernetes` and `Istio` perspectives:

```bash
$ kubectl get svc -n your-namespace name-of-service -o wide
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE   SELECTOR
name-of-service   ClusterIP   10.102.240.109   240.240.0.1   3111/TCP   22m   <none>
```

```bash
$ istioctl proxy-config clusters -n your-namespace debug | grep name-of-service.your-namespace
name-of-service.your-namepsace.svc.cluster.local                                                      3111      -          outbound      EDS
```

As you can see we have here [`EDS`](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/upstream/service_discovery#arch-overview-service-discovery-types-eds), stands for `Endpoint Discovery Service`, so the `Service` is detected by `Envoy`.

Below is an explanation how detection works in `Istio Service Mesh`, `pilot` in this diagram is currently integral and assembled part of Istio control plane -`Istiod` in an container `discovery`:

> Istio does not provide [service discovery](https://en.wikipedia.org/wiki/Service_discovery), although most services are automatically added to the registry by [Pilot](https://Istio.io/docs/reference/glossary/#pilot) adapters that reflect the discovered services of the underlying platform (Kubernetes, Consul, plain DNS).
>
> Source: https://Istio.io/latest/docs/concepts/traffic-management/
>
> ![Discovery and Load Balancing](https://Istio.io/v0.5/docs/concepts/traffic-management/img/pilot/LoadBalancing.svg)
>
> Source of diagram: https://Istio.io/v0.5/docs/concepts/traffic-management/load-balancing.html 

We can also check in the Istiod when our service has been added to service register by `Istio`:

```bash
$ kubectl logs -n istio-system $(kubectl get pod -n istio-system | grep Istiod | awk '{print $1}') -c discovery | grep 'name-of-service.your-namespace.svc.cluster.local'
2022-10-16T16:48:12.146781Z	info	ads	Full push, new service your-namespace/name-of-service.your-namespace.svc.cluster.local
```

### VirtualService

So now we can define `VirtualService` - really important thing, we need to follow the same name convention and ports definitions we already created to create a working `path-based` routing utilizing `Istio Internal Mesh`, but you can also utilize other functionalities of `VirtualService` since once `DNS resolving` is possible, and `VirtualService` is configured correctly - this just works. 

First, verify which real services we would like to use:

```bash
$ kubectl get svc -n your-namespace | grep -E 'read|write'
svc-read             ClusterIP   10.102.150.217   <none>        3100/TCP,9095/TCP   2d7h
svc-write            ClusterIP   10.103.119.208   <none>        3100/TCP,9095/TCP   2d7h
```

Also, its good practice to check their endpoints:

```bash
$ kubectl get ep -n your-namespace | grep -E 'read|write'
svc-read             172.17.0.14:9095,172.17.0.14:3100   2d7h
svc-write            172.17.0.15:9095,172.17.0.15:3100   2d7h
```

And finally endpoints of them in Istio:

```bash
$ ~ istioctl pc endpoint -n your-namespace debug | grep -E 'read|write'
172.17.0.14:3100                                        HEALTHY     OK                outbound|3100||svc-read.your-namespace.svc.cluster.local
172.17.0.14:9095                                        HEALTHY     OK                outbound|9095||svc-read.your-namespace.svc.cluster.local
172.17.0.15:3100                                        HEALTHY     OK                outbound|3100||svc-write.your-namespace.svc.cluster.local
172.17.0.15:9095                                        HEALTHY     OK                outbound|9095||svc-write.your-namespace.svc.cluster.local
```

Now we can check if our `svc-read` and `svc-write` services work correctly:

```bash
$ kubectl exec -it -n your-namespace debug -- sh                           
/home # curl svc-read.your-namespace.svc.cluster.local:3100/loki/api/v1/tail -vvv
*   Trying 10.102.150.217:3100...
* Connected to svc-read.your-namespace.svc.cluster.local (10.102.150.217) port 3100 (#0)
> GET /loki/api/v1/tail HTTP/1.1
> Host: svc-read.your-namespace.svc.cluster.local:3100
> User-Agent: curl/7.81.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 401 Unauthorized # This is expected response
< content-type: text/plain; charset=utf-8
< x-content-type-options: nosniff
< date: Sun, 16 Oct 2022 16:50:38 GMT
< content-length: 10
< x-envoy-upstream-service-time: 3
< server: envoy
< 
no org id
/home # curl svc-write.your-namespace.svc.cluster.local:3100/loki/api/v1/push -vvv
*   Trying 10.103.119.208:3100...
* Connected to svc-write.your-namespace.svc.cluster.local (10.103.119.208) port 3100 (#0)
> GET /loki/api/v1/push HTTP/1.1
> Host: svc-write.your-namespace.svc.cluster.local:3100
> User-Agent: curl/7.81.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 405 Method Not Allowed # This is expected response
< date: Sun, 16 Oct 2022 16:50:52 GMT
< content-length: 0
< x-envoy-upstream-service-time: 0
< server: envoy
< 
* Connection #0 to host svc-write.your-namespace.svc.cluster.local left intact
```

So we can make this `VirtualService` now, as we know which `path-based` routes we want to use:

```yaml
apiVersion: networking.Istio.io/v1beta1
kind: VirtualService
metadata:
  name: name-of-vs
  namespace: your-namespace
spec:
  hosts:
    - "name-of-service.your-namespace.svc.cluster.local" # This is the most important part, as this name must point to a previously created Service
  gateways:
    - mesh # Not necessary as this is the default, but it is better to be explicit
  http:
    - name: svc-read
      match:
        - uri:
            prefix: "/svc/api/v1/tail" 
          port: 3111 # Must match the port used in the Service
      route:
        - destination:
            host: svc-read.your-namespace.svc.cluster.local # Prefer using the FQDN here to avoid issues. Can be a cross-namespace address.
            port:
              number: 3100 # This port must be described correctly in the underlying Service, eg. to have prefix in name as a protocol, or to have defined appProtocol parameter
    - name: svc-write
      match:
        - uri:
            prefix: "/svc/api/v1/push"
          port: 3111 # Must match the port used in the Service
      route:
        - destination:
            host: svc-write.your-namespace.svc.cluster.local # Prefer using the FQDN here to avoid issues. Can be a cross-namespace address.
            port:
              number: 3100 # This port must be described correctly in the underlying Service, eg. to have prefix in name as a protocol, or to have defined appProtocol parameter

```

At this moment we can verify this on `istioctl` perspective, locally:

```bash
$ istioctl analyze -n your-namespace file-with-your-vs.yaml

✔ No validation issues found when analyzing file-with-your-vs.yaml.
```

And after applying this `VirtualService` by Flux we can check what is created there::

```bash
$ istioctl proxy-config routes -n your-namespace debug | grep -E 'tail|push'
3111                                                                 name-of-service, name-of-service.your-namespace + 1 more...                                   /svc/api/v1/tail*     name-of-service.your-namespace
3111                                                                 name-of-service, name-of-service.your-namespace + 1 more...                                   /svc/api/v1/push*     name-of-service.your-namespace

```

So looks like everything is correct. We can also use  `-o yaml` option to see more details, without grep, but it is non-necessary as short option showed us existience of new defined `path-based` routing rules.

## Verify if internal `VirtualService` works

### Curl check

Ok so now we can verify if its really working:

```bash
$ kubectl exec -it -n your-namespace debug -- sh
/home # curl name-of-service.your-namespace.svc.cluster.local:3111/svc/api/v1/push -vvv
*   Trying 10.102.240.109:3111...
* Connected to name-of-service.your-namepsace.svc.cluster.local (10.102.240.109) port 3111 (#0)
> GET /svc/api/v1/push HTTP/1.1
> Host: name-of-service.your-namepsace.svc.cluster.local:3111
> User-Agent: curl/7.81.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 405 Method Not Allowed # This is expected response
< date: Sun, 16 Oct 2022 06:48:01 GMT
< content-length: 0
< x-envoy-upstream-service-time: 1
< server: envoy
< 
* Connection #0 to host name-of-service.your-namepsace.svc.cluster.local left intact
/home # curl name-of-service.your-namespace.svc.cluster.local:3111/svc/api/v1/tail -vvv
*   Trying 10.102.240.109:3111...
* Connected to name-of-service.your-namepsace.svc.cluster.local (10.102.240.109) port 3111 (#0)
> GET /svc/api/v1/tail HTTP/1.1
> Host: name-of-service.your-namepsace.svc.cluster.local:3111
> User-Agent: curl/7.81.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 401 Unauthorized # This is expected response
< content-type: text/plain; charset=utf-8
< x-content-type-options: nosniff
< date: Sun, 16 Oct 2022 06:48:47 GMT
< content-length: 10
< x-envoy-upstream-service-time: 5
< server: envoy
< 
no org id
* Connection #0 to host name-of-service.your-namepsace.svc.cluster.local left intact
```

### Envoy Logs 

Looks like we are getting right responses in previous check, but for double check we can look into Envoy `istio-proxy` container of debug pod logs, but before please look at the image below how to read these [logs](https://www.envoyproxy.io/docs/envoy/latest/configuration/observability/access_log/usage):

>  **How to read Envoy Logs?**
>
> ![Envoy Logs](https://res.cloudinary.com/practicaldev/image/fetch/s--pK6c-nVN--/c_limit%2Cf_auto%2Cfl_progressive%2Cq_auto%2Cw_880/https://pbs.twimg.com/media/EA6X3jiX4AYh5X_.jpg)
>
> Source of image:  [Megan O`Keefe](https://twitter.com/askmeegs)


And then we can check the logs:

```bash
$ kubectl logs -n your-namespace debug -c istio-proxy
[2022-10-16T06:48:01.007Z] "GET /svc/api/v1/push HTTP/1.1" 405 - via_upstream - "-" 0 0 1 1 "-" "curl/7.81.0" "8d995ccc-a88c-9f21-b871-efd8be1e4d07" "name-of-service.your-namepsace.svc.cluster.local:3111" "172.17.0.15:3100" outbound|3100||svc-write.your-namespace.svc.cluster.local 172.17.0.23:33528 10.102.240.109:3111 172.17.0.23:44034 - svc-write
[2022-10-16T06:48:47.358Z] "GET /svc/api/v1/tail HTTP/1.1" 401 - via_upstream - "-" 0 10 5 5 "-" "curl/7.81.0" "6d888a58-1f00-9819-9ee4-9e48fa1ad132" "name-of-service.your-namepsace.svc.cluster.local:3111" "172.17.0.14:3100" outbound|3100||svc-read.your-namespace.svc.cluster.local 172.17.0.23:60262 10.102.240.109:3111 172.17.0.23:44036 - svc-read
```

So, next check on  Envoy `istio-proxy` container of `svc-read` and `svc-write` pods:

```bash
$ kubectl logs -n your-namespace svc-read-0 -c istio-proxy
2022-10-16T06:48:47.363Z] "GET /svc/api/v1/tail HTTP/1.1" 401 - via_upstream - "-" 0 10 0 0 "-" "curl/7.81.0" "6d888a58-1f00-9819-9ee4-9e48fa1ad132" "name-of-service.your-namepsace.svc.cluster.local:3111" "172.17.0.14:3100" inbound|3100|| 127.0.0.6:60269 172.17.0.14:3100 172.17.0.23:60262 outbound_.3100_._.svc-read.your-namespace.svc.cluster.local default
$ kubectl logs -n your-namespace svc-write-0 -c istio-proxy
[2022-10-16T06:48:01.007Z] "GET /svc/api/v1/push HTTP/1.1" 405 - via_upstream - "-" 0 0 0 0 "-" "curl/7.81.0" "8d995ccc-a88c-9f21-b871-efd8be1e4d07" "name-of-service.your-namepsace.svc.cluster.local:3111" "172.17.0.15:3100" inbound|3100|| 127.0.0.6:58611 172.17.0.15:3100 172.17.0.23:33528 outbound_.3100_._.svc-write.your-namespace.svc.cluster.local default
```

Finally, looks like everything works perfect. We can also see `request-ids` are matching, and we can use them to look into our Kiali and Jaeger for these requests tracing.

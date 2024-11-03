# Istio Guidelines

The following is a set of guidelines to help teams with understanding the use
of Istio and how to do some general setup and troubleshooting. This guide will
grow as Agora and our development teams grow in their understanding of the use
of [Istio](https://istio.io/) and the Service Mesh

## Why we use Istio and a Service Mesh

The primary purpose of Istio and a Service Mesh is to offload and perform
routine functionality in a microservice architecture that helps to decouple
certain task from our primary application logic. Such tasks are:

* Traffic Routing
* Authentication
* Observability

By leveraging Istio we hope to remove the burden of these issues from service
developers and allow them to focus more on their applications. Agora takes on
the responsibility of managing the service mesh and enhancing it.

## General Guidelines for Use of the Mesh

### Use the CityService

The [CityService](https://developer.woven-city.toyota/docs/default/component/id-homepage/#cityservice)
Operator helps to alleviate some rough edges and pain points of integrating
into Agora. The use of the CityService is recommended to try and help with
setup and provides an easier abstraction to work with than to manage various
Istio Custom Resources (VirtualService, AuthorizationPolicy, etc).

### Applications should bind to the pod's reported IP Address

Applications should bind themselves to the pods IP address for the purposes of
being able to listen for traffic directed to the pods from Istio. This is
because `localhost` might use the underlying server's reporting IP address
which could prevent Istio and Envoy from being able to direct traffic
correctly.

An example of how to do this is as follows

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
        - name: notifier
          env:
            - name: POD_HOST
              valueFrom:
                fieldRef:
                  fieldPath: status.podIP
```

### Name ports explicitly

Istio tends to try map ports against specified ports. If multiple pod report a
port named `http` in the same namespace it can lead to traffic being pushed to
the wrong pod due to naming clashes. It's recommended to always name the ports
for your services explicitly. For example:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
  namespace: my-namespace
spec:
  ports:
    - name: http-my-service
      port: 8080
      protocol: TCP
  selector:
    app: my-service
```

### Expose a container port

In Kubernetes, a `Service` can expose a port for the container, but it's still
recommended to explicitly state the `containerPort` in your deployment:

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
        - name: notifier
          ports:
            - containerPort: 8080
              name: http-my-service
              protocol: TCP
```

### Export Service Entries only to your namespace

Service Entries are okay to use, however, please make sure that when you add one
that the entry itself is only being used in the namespaces that use it. This can
help prevent any unexpected behavior with Istio matching an entry intended for
your service with another team or team's service(s).

In addition, please ensure that your port protocol matches correctly with the
supported protocols per
the [Istio Documentation](https://istio.io/latest/docs/reference/config/networking/service-entry/#ServicePort)

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: my-service-entry
  namespace: my-namespace
spec:
  hosts:
    - www.myexternalservice.com
  # ensures only 'my-namespace' can see this entry
  exportTo:
    - my-namespace
  location: MESH_EXTERNAL
  ports:
    - name: https
      number: 443
      protocol: HTTPS
  resolution: DNS
```

## Troubleshooting the Service Mesh

### Kiali

[Kiali](https://kiali.io/) is an observability tool that describes itself as
the "console for the Service Mesh". Kiali allows you to troubleshoot traffic in
your mesh and find potential issues.

Kiali can be reached via the cluster at

```
https://observability.cityos-dev.woven-planet.tech/kiali/
```

**Note:** Access to this requires your user information to be tied to the
'observability' group to allow you to access to the observability spaces. If
you require this currently, please reach out to Agora.

### Jaeger

[Jaeger](https://www.jaegertracing.io/) is also part of the Service Mesh, used
by Kiali to handle tracing. With traces you can see if there are any issues in
regard to the traffic that is being sent to and from the Envoy pods that are
the primary mechanism for Istio. You can reach this via the trace links
provided by Kiali, described in more
detail [here](https://medium.com/kialiproject/trace-my-mesh-part-1-3-35e252f9c6a9)
and [here](https://kiali.io/docs/features/tracing/)

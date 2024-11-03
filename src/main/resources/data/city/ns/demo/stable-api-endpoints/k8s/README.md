# Infrastructure deployment

There are two strategies here displayed to help teams with setting up their
infrastructure with common API endpoints.

## Single Deployment

Under a single deployment all deployments are the same and treated as such: this
is a common default when starting out and having few services to host per
deployment.

The [Virtual Service](https://istio.io/latest/docs/reference/config/networking/virtual-service/)
is linked to the common Service and Deployment, which then handles the traffic
from the ingress:

```yaml
  hosts:
    - bakery.cityos-dev.woven-planet.tech
  http:
    - name: bakery-routes
      match:
        - uri:
            prefix: /bakery
      route:
        - destination:
            host: bakery-service.bakery.svc.cluster.local
            port:
              number: 8080
```

### Pros

* Simple to set up: everything flows to a common prefix across all instances of
  the Bakery Service
* Easy to scale: increasing the number of replicas in your deployment is easy to
  do

### Cons

* All traffic flows through to the Bakery Service via `/bakery` regardless of
  version.
* Deployments cannot be managed individually, which means versions of services
  stay static across all replicas of the deployment

## Version Deployment

Under a versioned or multiple deployment, deployments are flagged with versions.
Istio can help us to direct and manage traffic per version.

The [Deployment](./versioned-deployment/deployment.yaml) has additional tags to
help us label a `version`

```yaml
kind: Deployment
metadata:
  name: bakery-deployment-v1
  namespace: bakery
  labels:
    version: v1
```

This can then be used by
a [Destination Rule](https://istio.io/latest/docs/reference/config/networking/destination-rule/)
to help group version tags into `subsets`:

```yaml
spec:
  host: bakery-service.bakery.svc.cluster.local
  subsets:
    - name: v1
      labels:
        version: v1
    - name: v2
      labels:
        version: v2
```

This allows
the [Virtual Service](https://istio.io/latest/docs/reference/config/networking/virtual-service/)
to then route traffic under the same common DNS host and depending on
our `subset` direct traffic:

```yaml
  hosts:
    - bakery.cityos-dev.woven-planet.tech
  http:
    - name: bakery-routes-v1
      match:
        - uri:
            prefix: /bakery/v1
      route:
        - destination:
            host: bakery-service.bakery.svc.cluster.local
            subset: v1
            port:
              number: 8080
    - name: bakery-routes-v2
      match:
        - uri:
            prefix: /bakery/v2
      route:
        - destination:
            host: bakery-service.bakery.svc.cluster.local
            subset: v2
            port:
              number: 8080
```

## Pros

* Traffic can be split without having to make a new `host`. Leveraging the
  prefixes allows us to do this instead having a separate Virtual Service.
* Sets up a way to provide blue-green or canary testing. When `V3` of the
  service needs to be tested, having a new route matcher is sufficient along
  with a new deployment and subset to do the split.
* Allows for multiple versions of the service to be deployed, having separate
  deployments allows for multiple versions or experiments to be run for service
  enhancements

## Cons

* Requires additional configuration and maintenance in the form of version tags,
  destination rule subsets, and deployment manifests

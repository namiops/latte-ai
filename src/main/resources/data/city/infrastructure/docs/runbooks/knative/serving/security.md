# Agora KNative Serving Security

## Traffic Restriction per Namespace (Private Service)

KNative Serving introduces a shared proxy (`activator` pod) in `knative-serving` namespace, and incoming traffic for each KServices passes this proxy.
This is required for measuring the number of requests and auto-scaling mechanism.
The proxy forwards an incoming request to a specific KService by `Host` header.
For example, a request with the following header will be forwarded to KService `helloworld-go` in `lambda` namespace:

- `Host: helloworld-go.lambda`
- `Host: helloworld-go.lambda.svc`
- `Host: helloworld-go.lambda.svc.cluster.local`

We can restrict access to cluster-local Service (labeled by `networking.knative.dev/visibility: cluster-local`) per namespace by using AuthorizationPolicy.
We need to deploy two AuthorizationPolicy objects in `knative-serving` namespace and your namespace, since the `activator` pod is in `knative-serving` namespace.

### AuthorizationPolicy in knative-serving namespace

The first AuthorizationPolicy restricts the first part of traffic flow, incoming traffic to `knative-serving` namespace.

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: <your_namespace>-serverless
  namespace: knative-serving
spec:
  selector:
    matchLabels:
      app: activator
      app.kubernetes.io/component: activator
      app.kubernetes.io/name: knative-serving
  action: DENY
  rules:
  - from:
    - source:
        notNamespaces: 
          - knative-eventing
          - "<your_namespace>"
          - "<other_namespaces>"
    to:
    - operation:
        hosts: 
          # NOTE: These hosts are based on VirtualServices generated per KService.
          - "*.<your_namespace>"
          - "*.<your_namespace>.svc"
          - "*.<your_namespace>.svc.cluster.local"
```

Note that we need to allow traffic from `knative-eventing` namespace.
The reason is that when an event broker triggers KService, a request is sent from a broker pod (in a case of an in-memory channel broker) inside `knative-eventing` namespace.
You can remove `knative-eventing` namespace from the list, if you are not using an in-memory channel broker.

You can find another working sample at [knative-serving/auth-policies](../../../../k8s/environments/local/clusters/worker1-east/knative-serving/auth-policies)


### AuthorizationPolicy in your namespace

The second AuthorizationPolicy restricts the last part of traffic flow, incoming traffic to Serverless pods in your namespace.
This incoming traffic targets SVC with a `<revision>-private` prefix.
For example, `helloworld-go-00005-private.lambda.svc.cluster.local`.

The below example shows AuthorizationPolicy in your namespace.
We need to configure it per KService.

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: <your_kservice_name>-serverless
  namespace: <your_namespace>
spec:
  selector:
    matchLabels:
      serving.knative.dev/configuration: <your_kservice_name>
      serving.knative.dev/service: <your_kservice_name>
  action: DENY
  rules:
  - from:
    - source:
        notNamespaces: 
          - knative-serving
          - "<your_namespace>"
          - "<other_namespaces>"
    to:
    - operation:
        hosts: 
          # NOTE: These hosts are based on Services generated per Revision.
          - "*.<your_namespace>"
          - "*.<your_namespace>.svc"
          - "*.<your_namespace>.svc.cluster.local"
```

You can find another working sample at [lambda-sample/security/auth-policies/kservice](../../../../k8s/common/lambda-sample/security/auth-policies/kservice).

# Expose Service on Speedway

## Prerequisite

You should have applied for a [Stable URL](/docs/default/component/agora-migrations-tutorial/speedway/request-a-stable-url)

## Setup VirtualService and Gateway

You can expose your Service on Speedway using:

1. Virtual Service
1. An Entry in Gateway
1. Configure Sidecar to allow egress traffic

Example [VirtualService](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-ac-user-registration/speedway/prod/virtualservice.yaml), to be added in your own namespace. Please modify accordingly:

```yaml title="virtual-service.yaml"
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: ac-user-registration-httproute-dev
spec:
  gateways:
  - agora-city-private-ingress-prod/city-private-ingress-gateway
  hosts:
  - ac-user-registration.woven-city.toyota
  http:
  - match:
    - uri:
        prefix: /user-management
    route:
    - destination:
        host: ac-user-registration-user-management-ui
        port:
          number: 8080
```

Please make sure to attach correct Gateway in VirtualService for [Speedway Dev](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-city-private-ingress/speedway/dev/patches/gateway-city-private-ingress-gateway.patch.yaml) and [Speedway Prod](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-city-private-ingress/speedway/prod/patches/gateway-city-private-ingress-gateway.patch.yaml)

Modify Gateway and add your host ([Speedway Dev](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-city-private-ingress/speedway/dev/patches/gateway-city-private-ingress-gateway.patch.yaml) or [Speedway Prod](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-city-private-ingress/speedway/prod/patches/gateway-city-private-ingress-gateway.patch.yaml)). Example [Gateway Configuration](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-city-private-ingress/speedway/prod/patches/gateway-city-private-ingress-gateway.patch.yaml#L64-L73):

```yaml title="gateway-city-private-ingress-gateway.patch.yaml"
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: city-private-ingress-gateway
  namespace: agora-city-private-ingress
spec:
  servers:
  (...)
  - hosts:
    - ac-user-registration.woven-city.toyota
    port:
      name: ac-user-registration-https
      number: 443
      protocol: HTTPS
    tls:
      credentialName: agora-stable-url-default-cert
      httpsRedirect: true
      mode: SIMPLE
  (...)
```

Next, create `AuthorizationPolicy` to allow inbound traffic from to City Private Ingress's Service Account to your service pod.
The principals are different in Speedway environments:

- For Speedway Dev: `cluster.local/ns/agora-city-private-ingress-dev/sa/city-private-ingress`
- For Speedway Prod: `cluster.local/ns/agora-city-private-ingress-prod/sa/city-private-ingress`

[Example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-cvm/speedway/prod/authorizationpolicy-allow-city-private-ingress.yaml)

```yaml title="auth-policy-allow-city-private-ingress.yaml"
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-city-private-ingress
spec:
  selector:
    matchLabels:
      app: <your_app_label>
  action: ALLOW
  rules:
  - to:
    - operation:
        hosts:
          - "<your_hostname>"
  - from:
    - source:
        principals: 
          # For PROD
          - cluster.local/ns/agora-city-private-ingress-prod/sa/city-private-ingress
```

Finally, update your `Sidecar` to allow egress traffic to City Private Ingress namespace. `agora-city-private-ingress-prod/*` for Speedway prod and `agora-city-private-ingress-dev/*` for Speedway Dev. [Example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tsl-telescope/speedway/prod/sidecar.yaml)

```yaml title="sidecar.yaml"
apiVersion: networking.istio.io/v1beta1
kind: Sidecar
metadata:
  name: default
spec:
  egress:
    - hosts:
        - "./*"
        - "agora-city-private-ingress-prod/*"  <----------- add this
        - "agora-control-plane-prod/*"  
        - "agora-id-prod/drako-v1.agora-id-prod.svc.cluster.local"
        - "istio-system/*"
```

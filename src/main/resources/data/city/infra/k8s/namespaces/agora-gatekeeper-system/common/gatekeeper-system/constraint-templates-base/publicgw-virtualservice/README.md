# Description

1. In the virtual service object is the gateways definition:
```yaml
spec:
  gateways:
    - city-ingress/ingressgateway
    - mesh
```
2. In the gateway object is the selector definition:
```yaml
spec:
    selector:
      istio: ingressgateway
```
3. In the service object is the annotation definition

```yaml
metadata:
  annotations:
    service.beta.kubernetes.io/aws-load-balancer-internal: "true"
    service.beta.kubernetes.io/aws-load-balancer-subnets: User-Private-Subnet1, User-Private-Subnet2
    service.beta.kubernetes.io/aws-load-balancer-type: nlb-ip
```

# build
```sh
pk build rego/PublicGw.rego -o ./ && \
mv rego/PublicGw.yaml template.yaml
```

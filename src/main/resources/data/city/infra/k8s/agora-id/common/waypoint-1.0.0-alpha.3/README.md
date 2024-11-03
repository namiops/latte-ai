# Waypoint 🛣️
The Agora Identity traffic route package. 

## Configuration guide
### patch fqdn
depends on gateway and enviroment. the fqdn virtual gateway host should be change according to gateway listener.
```

  - target:
      kind: VirtualService
      name: (keycloak|statics)
    patch: |
      - op: add
        path: /spec/hosts/-
        value: id.woven-city.local
```

### patch gateway name
Each environment use a different reference to gateway. This package use annotation to distrinct which route should belong to different kind of gateway.

Patch example
```
  - target:
      annotationSelector: id.agora/gateway=private
    patch: |-
      - op: add
        path: /spec/gateways/-
        # add private gateway reference
        value: agora-city-private-ingress-prod/city-private-ingress-gateway

  - target:
      annotationSelector: id.agora/gateway=public
    patch: |-
      - op: add
        path: /spec/gateways/-
        # add public gateway reference
        value: agora-city-public-ingress-prod/city-public-ingress-gateway
```

### patch workload selector
by default workload selector is empty. you need to select a proper workload.
```
  - target:
        kind: Service
        name: waypoint-drako
    patch: |-
      - op: add
        path: /spec/selector/-
        value: 
          app.kubernetes.io/name: drako
          app.kubernetes.io/version: v1
```

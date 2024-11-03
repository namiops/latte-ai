# Testkube tenants directory

This directory deploys tenants resources to the Testkube namespace. The goal is to avoid flux kustomization blocking failures for the tenant namespaces during upgrades and maintenance windows.

(Internal note: This directory is applied via the flux kustomize resource located [here](../../flux-system/kustomizations/system/testkube-tenants.yaml))

## Running tests against Speedway hosts

To run tests against hosts exposed on Speedway dev [ingress](../../../../../../../../infra/k8s/agora-city-private-ingress/speedway/dev/patches/gateway-city-private-ingress-gateway.patch.yaml):

- Add the host to the AuthorizationPolicy [here](../../../../../../../../infra/k8s/agora-city-private-ingress/speedway/dev/allow-dev2-testkube.yaml#L24)
- Add the host to the ServiceEntry [here](../serviceentries-speedway-agora-city-private-ingress-dev.yaml)
- Create a new folder under [this](./) directory and deploy the test resources.

# city-public-ingress-1.17.2-agora1

Generated by "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/city-public-ingress/bin/import -t ingress-gateway -n city-public-ingress -r agora1 -v 1.17.2"
using Values from "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/city-public-ingress/bin/city-public-ingress-1.17.2-agora1-values.yaml"

## Manual changes

This release has manual changes.
When you want to re-generate it from Helm, make sure to copy the files from the
old version to the new version and update the `kustomization.yaml` accordingly.

### Patch spec.loadBalancerClass
The Helm chart does not support setting this.

Patch the service as follows by adding `deployment-lb-class.yaml` to the
`kustomization.yaml` as patch.

### Patch envoyfilter-proxy-protocol.yaml
This `EnvoyFilter` is needed because we are using `Proxy-Protocol` to be able
to see the original IP address inside the Envoy logs, as a part of Security
Requirements of Agora on Internet Epic.

Add the file to `kustomization.yaml`.

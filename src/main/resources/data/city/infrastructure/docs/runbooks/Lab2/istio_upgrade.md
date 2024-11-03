Table of content
<!-- vim-markdown-toc GFM -->

- [Upgrade steps](#upgrade-steps)
- [Post upgrade](#post-upgrade)

<!-- vim-markdown-toc -->

### Upgrade steps

- Install new version of istiod and gateways alongside current ones. [example](https://github.com/wp-wcm/city/pull/3086)
  - Make sure to adjust load balancer annotation name suffix according to the version
- Test new istiod by choosing istio version, example:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: bookinfo
  labels:
    istio.io/rev: <new-version>
```

- Switch istiod to new version by adjusting default revision values.
  - Update GLM: [example](https://github.com/wp-wcm/city/pull/3093)
  - Update default webhook:
    - [This script](infrastructure/k8s/environments/lab2/clusters/bin/istio-default-tag) is used to generate default tag.
    - example: `./istio-default-tag <cluster name> <istio version>`
    - Note: repeat for every clusters
- Verify istio default mutating webhook is pointing to new version
- Restart every workload that uses sidecar injection.
  - Note: This step is optional, because worker nodes will automatically be renewed by karpenter every 48 hours.
- Switch gateway to use new version of istio. [example](https://github.com/wp-wcm/city/pull/3097)

### Post upgrade

- **Caution**: before cleaning up older revision, make sure `istioctl ps` doesn't have any references to old istio version
- Clean up old gateways after making sure rollback is not needed anymore. [example](https://github.com/wp-wcm/city/pull/3101)
- Clean up old istiod after making sure rollback is not needed anymore. [example](https://github.com/wp-wcm/city/pull/3107)

References:
- https://docs.solo.io/gloo-mesh-enterprise/latest/setup/upgrade/gloo_mesh_managed_upgrade/

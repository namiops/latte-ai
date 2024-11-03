## Gloo Mesh Upgrade

- View changelog between version `https://docs.solo.io/gloo-mesh-enterprise/latest/reference/changelog/#compareversions_v2.2.3...v2.3.4`
- Test following steps on local first
- Upgrade Gloo CRDs on management cluster
  - Verify if there is any issues in the Gloo UI
- Upgrade Gloo CRDs on worker clusters
  - Verify if there is any issues in the Gloo UI
- Upgrade Gloo mgmt-server on management cluster
  - Verify if there is any issues in the Gloo UI
  - Check if management cluster has been updated `meshctl version --kubecontext MGMTCONTEXT`
- Upgrade Gloo agent on worker cluster
  - Verify if there is any issues in the Gloo UI
  - Check if management cluster has been updated `meshctl version --kubecontext WORKERCONTEXT`
- Repeat for every worker clusters
- Upgrade meshctl to match Gloo Mesh version

Refs: [official docs](https://docs.solo.io/gloo-mesh-enterprise/latest/setup/upgrade/upgrade/)

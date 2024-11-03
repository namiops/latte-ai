# Internal stable url routing

## If you want to access internal routing to other namespace
for instance, you need internal routing for `agora-id-dev` namespace.
1. Make sure `agora-id/speedway/dev/internal-stable-url-routing` package is existed
2. Import this kustomization package to your namespace kustomization
   ```
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    namespace: my-awesome-namespace-dev
    resources:
    - '../../../agora-id/speedway/dev/internal-stable-url-routing'
   ```
3. try to send the request


## If you want to provide internal routing to other namespace
1. Create a new kustomization package under your namespace environment. For example
    ```
    my-awesome-namespace/speedway/dev/internal-stable-url-routing
    ```
2. Use `common/internal-stable-url-routing/base` as a base kustomization package
3. Patch gateway endpoint to a proper internal gateway address.
   ```
   ## for dev cluster
   - address: city-internal-proxy.agora-city-internal-proxy-dev.svc.cluster.local
   ## for prod
   - address: city-internal-proxy.agora-city-internal-proxy-prod.svc.cluster.local
   ```
4. Patch host with your stable url
   ```
    - awesome-app.woven-city.toyota
    - awesome-api.woven-city-api.toyota
   ```
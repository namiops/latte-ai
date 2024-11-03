# Local Apicurio

## Installation using Flux

Please uncomment the following lines:

In `infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/system/kustomization.yaml`

```yaml
- kafka.yaml
- kafka-operator-system.yaml
```

In `infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml`

```yaml
- apicurio.yaml
```

Then push the commit to your branch.

## Quick test

* Through Command Line: Hit the top page to confirm the service is running (`172.19.201.201` is the city-ingress svc's External IP.)
    ```bash
    $ curl --insecure --resolve apicurio.woven-city.local:80:172.19.201.201 http://apicurio.woven-city.local/ui/
    ```
    * If you don't like the insecure flag, you can get the CA cert via
        ```bash
        kubectl -n city-ingress get secret public-ingress-cert -ojsonpath='{.data.ca\.crt}' | base64 -d
        ```
* Through Web Browser
    1. Complete the setup described in https://github.com/wp-wcm/city/blob/main/ns/id/docs/agora-engineers/01_keycloak_setup.md#how-to-access-to-keycloak-hosted-on-your-ec2.
    2. Access https://id.woven-city.local/. You should see some "In secure website" warning in the browser. Go ahead and force-load the page. Once you see the top page, just leave the site.
    3. Access https://apicurio.woven-city.local/. Sign in using the credentials below
        ```bash
        kubectl -n id get secret credential-woven-alice-id -ojsonpath='{.data.username}' | base64 -d
        kubectl -n id get secret credential-woven-alice-id -ojsonpath='{.data.password}' | base64 -d
        ```
    4. You should be directed to the Apicruio top page.

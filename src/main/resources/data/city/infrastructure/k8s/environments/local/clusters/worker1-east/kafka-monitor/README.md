# Local Kafka-monitor(AKHQ)

## Installation using Flux

Please uncomment the following lines:

In `infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/system/kustomization.yaml`

```yaml
- kafka.yaml
```

In `infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml`

```yaml
- kafka-monitor.yaml
```

Then push the commit to your branch.

## Quick test

* Through Web Browser
    1. Complete the setup described in https://github.com/wp-wcm/city/blob/main/ns/id/docs/agora-engineers/01_keycloak_setup.md#how-to-access-to-keycloak-hosted-on-your-ec2.
    2. Access https://id.woven-city.local/. You should see some "In secure website" warning in the browser. Go ahead and force-load the page. Once you see the top page, just leave the site.
    3. Create user and edit the group of user in https://id.woven-city.local/auth/admin
       - You can set your group as `agorans/<YOUR_NAMESPACE>` to view the resource in `<YOUR_NAMESPACE>`
    4. Access https://kafka-monitor.woven-city.local/. Sign in using the user created in Step3
        
   5. You should be directed to the AKHQ top page.


## How to debug 

1. Enable more logging: https://akhq.io/docs/debug.html#monitoring-endpoint
2. sometimes this error occurs... [HttpClient with OAuth2 Client Credentials doesn't work · Issue #752 · micronaut-projects/micronaut-security](https://github.com/micronaut-projects/micronaut-security/issues/752)
   - might need to access twice

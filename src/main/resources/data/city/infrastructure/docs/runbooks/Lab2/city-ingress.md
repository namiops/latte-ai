<!-- vim-markdown-toc GFM -->

- [City Ingress](#city-ingress)
  - [Expose service in city private ingress](#expose-service-in-city-private-ingress)
  - [Expose service in city public ingress](#expose-service-in-city-public-ingress)
  - [Expose service in port other than 443](#expose-service-in-port-other-than-443)
    - [Setup MTLS](#setup-mtls)

<!-- vim-markdown-toc -->

## City Ingress

### Expose service in city private ingress

Expose it in city private ingress which will be accessible over VPN.

- Make sure you have exported your workspace to city gateway workspace.
  [example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/bookinfo/workspacesettings-bookinfo.yaml#L13)
- Modify [VirtualGateway](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml#L18)
  in mgmt-east to route the traffic to your workspace.
- Create RouteTable in mgmt-east in your service's namespace to route the traffic within your workspace.
  [example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/bookinfo/routetable-private-ingress.yaml)
- After your PR is merged, your service will be accessible at `<service-name>.agora-lab.w3n.io` (over VPN).

### Expose service in city public ingress

Expose it in city public ingress which will be accessible over public internet, but restricted via
[Entec's IP allow list](https://github.tri-ad.tech/information-security/woven-snow-public-assets-pusher/blob/main/docs/ALLOW-LIST-README.md).

- Make sure you have exported your workspace to city gateway workspace.
  [example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/bookinfo/workspacesettings-bookinfo.yaml#L13)
- Modify [VirtualGateway](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-public-ingress/virtualgateway-default.yaml#L18)
  in mgmt-east to route the traffic to your workspace.
- Create RouteTable in mgmt-east in your service's namespace to route the traffic within your workspace.
  [example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/test/routetable-public-ingress.yaml)
- Update [this yaml file](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/lab2/common.yaml#L16) to create Route53 entry.
- After your PR is merged, your service will be accessible at `<service-name>.agora-lab.w3n.io` (using Entec's IP allow list).

### Expose service in port other than 443

- Add the port to the service.
  [example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/city-private-ingress/gloo-gateway-0.0.1/service-city-private-ingress.yaml#L17)
- Append new entry to [VirtualGateway](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml#L51).
- Append new entry to [ProxyProtocolPolicy](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/proxyprotocolpolicy-default.yaml#L9)

#### Setup MTLS

Setting up MTLS requires server certificate to present to the client and client ca certificate for the server to trust.  
  
To generate server certificate, you can deploy your own certificate or use agora certificate.  
This is a manual step at the moment. Example:

```yaml
apiVersion: networking.gloo.solo.io/v2
kind: VirtualGateway
metadata:
  name: default
  namespace: city-private-ingress
spec:
  workloads:
    - selector:
        labels:
          app: city-private-ingress
  listeners:
    - http: {}
      port:
        number: 443
      tls:
        mode: SIMPLE
        secretName: agora-lab-cert
      allowedRouteTables:
      ...
    - http: {}
      port:
        number: 6643
      tls:
        mode: MUTUAL
        secretName: httpbin-cvm-ingress-cert # <<<< This is just copying agora-lab-cert
        parameters:
          minimumProtocolVersion: TLSv1_3
      allowedRouteTables:
      ...
```

Client ca certificate is usually fetched from Vault after creating the backend.  
[This is an example module](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/modules/cityos_vault_cvm_backend) to create the backend in Vault.  
After creation, call the module. [Example in lab2](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/lab2/base/worker1_east-vault.tf#L48)  
  
To fetch the certificate from Vault and deploy to the cluster manually, follow these commands:

```bash
# get issuer id (only if you have multiple issuers)
$ vault list <pki name>/issuers -format=json
[
  "abc123"
]

# confirm certificate
# change "default" to issuer id when you have multiple issuers
$ vault read <pki name>/issuer/default -format=json | jq -r .data.certificate
"-----BEGIN CERTIFICATE-----..."

# fetch and deploy
$ kubectl create secret generic <certificate secret name appended by "-cacert"> \
  --from-literal=cacert="$(vault read <pki name>/issuer/default -format=json | jq -r .data.certificate)"
```

Another way of deploying this is by using external-secrets operator.  
This is a recommended way, because it follows gitops principle.

```yaml
apiVersion: generators.external-secrets.io/v1alpha1
kind: VaultDynamicSecret
metadata:
  name: myvault-vds
  namespace: xxx
spec:
  # change default to issuer id when you have multiple issuers
  path: /<pki name>/issuer/default 
  method: GET
  provider:
    server: https://dev.vault.tmc-stargate.com
    namespace: ns_dev/ns_cityos_platform
    auth:
      jwt:
        kubernetesServiceAccountToken:
          # you must add a role to allow this service account to fetch from vault
          # vault list auth/<access path>/role
          # vault read auth/<access path>/role/<role name>
          serviceAccountRef:
            audiences:
            - https://kubernetes.default.svc
            name: <service account name>
            namespace: <service account namespace>
        path: <access path>
        role: <role name>
---
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: myvault-es
  namespace: xxx
spec:
  dataFrom:
  - sourceRef:
      generatorRef:
        apiVersion: generators.external-secrets.io/v1alpha1
        kind: VaultDynamicSecret
        name: myvault-vds
  refreshInterval: 24h
  target:
    name: <secret name to be generated-cacert>
    template:
      data:
        cacert: '{{ .certificate }}'
```

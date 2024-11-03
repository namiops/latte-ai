Table of content
## Gen 1 Environments (Lab & Dev)
<!-- vim-markdown-toc GFM -->

* [Ordering a certificate](#ordering-a-certificate)
* [K8S Certificate installation](#k8s-certificate-installation)
* [Certificate renewal](#certificate-renewal)

<!-- vim-markdown-toc -->

### Ordering a certificate

- Go to this [link](https://now.woven.tech/now/nav/ui/classic/params/target/com.glideapp.servicecatalog_cat_item_view.do%3Fv%3D1%26sysparm_id%3D0787220a1bdfb050e8aadc24cc4bcb4f%26sysparm_link_parent%3De15706fc0a0a0aa7007fc21e1ab70c2f%26sysparm_catalog%3De0d08b13c3330100c8b837659bba8fb4%26sysparm_catalog_view%3Dcatalog_default%26sysparm_view%3Dcatalog_default) to create a ticket
- Fill in:
  - Summary: Certificate request for example.com and *.example.com
  - Category: Others
  - Description: Please create certificate for example.com and *.example.com
- Submit the form

### K8S Certificate installation

- After retrieving purchased certificates, unzip it, and the zip should contain AT LEAST

```ini

STAR_agora-<Environment-CertName>.crt
SectigoRSADomainValidationSecureServerCA.crt
<PrivateKeyFile>

```

- Create certificate containing intermediate CA cert

```bash
cat STAR_agora-<Environment-CertName>.crt SectigoRSADomainValidationSecureServerCA.crt > temp.crt
```

- Find out which gateway uses the Host the new certificate is configured for

``` bash

kubectl get gateway -A -o yaml

```
- Search for the relevant host and take note of the secret name used and which gateway its in (as well as the namespace). In the case of lab we found the following for *.agora-lab.woven-planet.tech:

``` YAML
  metadata:
    creationTimestamp: "2022-07-05T07:53:37Z"
    generation: 27
    labels:
      kustomize.toolkit.fluxcd.io/name: city-ingress
      kustomize.toolkit.fluxcd.io/namespace: flux-system
    name: ingressgateway
    namespace: city-ingress
    resourceVersion: "3107880261"
    uid: 731bedce-8ef5-4af0-83c6-b4794675acd6
  spec:
    selector:
      istio: ingressgateway
    servers:
    - hosts:
      - '*'
      port:
        name: https
        number: 443
        protocol: HTTPS
      tls:
        credentialName: city-ingress-certificate-oct2025
        httpsRedirect: false
        mode: SIMPLE
    - hosts:
      - iot.agora-lab.woven-planet.tech
      port:
        name: tcp-mqtts
        number: 8883
        protocol: TLS
      tls:
        credentialName: city-ingress-certificate-oct2025
        minProtocolVersion: TLSV1_2
        mode: MUTUAL

```
- Now we know the relevant gateway and namespaces (From the meta data of the above, it was the ingressgateway & knative-ingress-gateway in the city-ingress namespace) and can create a new secret here. The following requres the neat plugin for kubectl if you wish to create the secret using a dry run first.

``` bash
kubectl create secret tls --dry-run=client -n city-ingress --cert=temp.crt --key=<PrivateKeyFile> city-ingress-certificate-<Expiry> -o yaml          
        | kubectl neat \
        | kubectl apply -f -
```

- With the secret created, we can test it by temporarily suspending flux and editing the relevant gateways and replacing them with our new secret created above. You will need to make sure you have flux cli and istioctl cli installed
``` bash
flux --as sudo --as-group=aad:0f158ca2-948a-4d79-83b1-f21380bd16aa suspend ks city-ingress
kubectl edit gateway ingressgateway --as-group aad:0f158ca2-948a-4d79-83b1-f21380bd16aa --as sudo
kubectl edit gateway knative-ingress-gateway  --as-group aad:0f158ca2-948a-4d79-83b1-f21380bd16aa --as sudo
istioctl pc s -n city-ingress <pod_name>
```
- Once you have confirmed the cert is valid and being used from the istoctl command, renable flux
``` bash
flux --as sudo --as-group=aad:0f158ca2-948a-4d79-83b1-f21380bd16aa resume ks city-ingress
```
- Make a pull request replacing the secret of each relevant yaml gateway file (located in `city/infrastructure/k8s/<ENV>/<Namespace>`)
- Once merged, verify using the istoctl command again to confirm the changes have been applied
```bash
istioctl pc s -n city-ingress <POD NAME>
```

### Certificate renewal

- Double check the expiration date of current certificate
- Follow [installation step](#k8s-certificate-installation)

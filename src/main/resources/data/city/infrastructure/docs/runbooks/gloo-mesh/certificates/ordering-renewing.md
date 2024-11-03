Table of content
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

- After retrieving purchased certificates, unzip it, and you will get these files

```ini
AAACertificateServices.crt 
STAR_agora-lab_w3n_io.crt
SectigoRSADomainValidationSecureServerCA.crt
USERTrustRSAAAACA.crt
agora-lab.w3n.io.csr
agora-lab.w3n.io.pkey
```

- Create certificate containing intermediate CA cert

```bash
cat STAR_agora-lab_w3n_io.crt SectigoRSADomainValidationSecureServerCA.crt > example.crt
```

- Create secret in city-ingress or any gateway that will use this certificate

``` bash
kubectl create secret tls \
        --dry-run=client \
        -o yaml \
        -n city-ingress \
        --cert=example.crt \
        --key=agora-lab.w3n.io.pkey \
        city-ingress-certificate \
        | kubectl neat \
        | kubectl apply -f -
```

- Configure the gateway to use this secret
- Verify using istioctl

```bash
istioctl pc s -n city-ingress <POD NAME>
```

### Certificate renewal

- Double check the expiration date of current certificate
- Locate all kubernetes secrets that use the certificate
- Backup current certificate
- Follow [installation step](#k8s-certificate-installation)

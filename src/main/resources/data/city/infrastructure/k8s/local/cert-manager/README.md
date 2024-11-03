# cert-manager

cert-manager is a k8s system that handles creation and issuance of TLS certificates.

cert-manager requires a CA cert in order to set up the CA and issue certs. This can be bootstrapped with a self-signed certificate.

# Deployment 

Applying the cert-manager.yaml will set up the cert-manager CRDs and controllers. 

issuer.yaml contains a ClusterIssuer to allow all namespaces in the k8s cluster to request certificate issuance.

self-signed-secrets.yaml contains the CA cert and key required to set up the ClusterIssuer. These are b64 representations of the bootstrap-selfsigned-tls.crt and .key files, which were issued by the setup from bootstrap-self-signed-ca.yaml. These certs are valid for 10 years and are included for convenience. 

The bootstrap-self-signed-ca setup is included for reference only and should not be required unless the CA needs to be reinitialized.



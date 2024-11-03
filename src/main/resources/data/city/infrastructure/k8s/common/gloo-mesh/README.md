## Gloo Mesh

### What's needed to deploy mgmt server?
- License keys
- Secret to connect to vault for cert-manager issuer (to generate mgmt server cert)
- Relay token for agent to connect

### What's needed to deploy agent?
- Secret to connect to vault for cert-manager issuer (to generate client server cert)
- Relay token for agent to connect
- KubernetesCluster CR in mgmt server
- mgmt server cert to trust for mTLS

### Cert trust chains
- Vault
  - Root CA
    - Intermediate CA (pki_relay in the example)
      - mgmt server cert
      - client server cert

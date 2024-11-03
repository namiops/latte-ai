# spire-envoy-integration

## Deploy

1. Based on [this](https://github.com/wp-wcm/city/blob/main/ns/spire-x509-cert-generation/docs/README.md), generate `agent-x509pop.crt.pem` and `agent-x509pop.key.pem`, and put them under `./certs/secret`.
2. Run:
    ```sh
    kubectl apply -k ./certs
    kubectl apply -k .
    ```

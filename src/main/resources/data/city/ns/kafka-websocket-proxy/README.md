# kafka-websocket-proxy

**About this document**

Contents targeted to Agora members are written in this README.md.
Contents targeted to Agora members + non-Agora members are located in ./docs, so they will be picked up by [developer-portal](https://developer.woven-city.toyota/catalog/default/component/kafka-websocket-proxy).

## What is this

See [Developer Portal -> kafka-websocket-proxy](https://developer.woven-city.toyota/docs/default/Component/kafka-websocket-proxy#what-is-this)

## Design

Please refer to [TN-0185 External write access to Kafka for service teams](https://docs.google.com/document/d/1-1hN1HnmLqhQEog59hoAsTaRgfNlZ-US8g6wo_hhGck/edit#) for the design decisions.

Points to highlight:
- This proxy is a self-service component; each service team runs this in its own namespace and monitors it.
- AuthN/Z is handled outside of this software component. It is done on the infrastructure layer.

## Deployment (Target readers: Agora members + non-Agora members)

See [Developer Portal -> kafka-websocket-proxy](https://developer.woven-city.toyota/docs/default/Component/kafka-websocket-proxy#deploying-kafka-websocket-proxy)

## Development (Target readers: Agora members only)

### Running the proxy locally

#### Prerequisite

1. Run the local cluster along with Kafka
    1. Follow [local cluster README](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/README.md).
    2. Uncomment `- kafka.yaml` in [infrastructure/k8s/local/flux-system/kustomizations/system/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/flux-system/kustomizations/system/kustomization.yaml), commit the change and push it to your remote branch, and wait for flux to pick that up.

#### Run with Bazel

1. Port-forward Kafka bootstrap service.
    ```sh
    kubectl -n kafka port-forward svc/cityos-kafka-kafka-bootstrap 9092:9092
    ```
2. Use your favorite editor to add the following lines to your /etc/hosts file (sudo required).
    ```
    # For local Kafka testing
    127.0.0.1 cityos-kafka-kafka-0.cityos-kafka-kafka-brokers.kafka.svc
    ```
    (Not entirely sure why this is needed, but the proxy gives an error without this setting. FIXME if possible.)
3. Run the proxy with Bazel
    ```sh
    bazel run //ns/kafka-websocket-proxy/cmd -- --kafka-url localhost:9092 --kafka-topic <YOUR_TOPIC> --port <HTTP_SERVER_PORT_OF_YOUR_CHOICE>
    ```

Now the proxy is listening on `localhost:<HTTP_SERVER_PORT_OF_YOUR_CHOICE>`.

#### Run in the local cluster

1. Switch to the minikube docker env
    ```sh
    eval $(minikube docker-env)
    ```
2. Build your Docker image
    ```sh
    bazel run //ns/kafka-websocket-proxy:image
    ```
3. Make the following change to the k8s manifests, commit the change and push it to your remote branch, and wait for flux to pick that up.
    - Uncomment `- kafka-websocket-proxy.yaml` in [infrastructure/k8s/local/flux-system/kustomizations/services/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/flux-system/kustomizations/services/kustomization.yaml)
    - Follow [infrastructure/k8s/local/kafka-operator-system/README.md](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/kafka-operator-system/README.md)
    - In [infrastructure/k8s/local/kafka-websocket-proxy-sample/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/kafka-websocket-proxy-sample/kustomization.yaml), switch `newImage` and `newTag` pair to those for local dev.
4. When you update the code, you build the image & restart the pod.
    ```sh
    kubectl -n kafka-websocket-proxy-sample rollout restart deploy kafka-websocket-proxy
    ```

### Running the example client locally

#### Go-client

(Assuming you are running the proxy following `Run with Bazel`.)

1. After starting the proxy, run the following command
    ```sh
    bazel run //ns/kafka-websocket-proxy/examples/go-client -- --addr localhost:<HTTP_SERVER_PORT_OF_YOUR_CHOICE>
    ```
(Assuming you are running the proxy following `Run in the local cluster`.)

These steps will access kafka-websocket-proxy from outside Agora with mTLS.

1. Run `minikube tunnel`.
2. Get the ingressgateway's IP by `kubectl get svc -n city-ingress ingressgateway -ojsonpath='{.status.loadBalancer.ingress[0].ip}'`
3. Use your favorite editor to add the following lines to your /etc/hosts file (sudo required).
    ```sh
    # For local Kafka testing
    <GATEWAY_IP> kafka-websocket-proxy-sample.woven-city.local
    ```
4. Go to [ns/kafka-websocket-proxy/examples/go-client/tls/](https://github.com/wp-wcm/city/blob/main/ns/kafka-websocket-proxy/examples/go-client/tls/)
    ```sh
    cd <path-to>/examples/go-client/tls/
    ```
5. Save the client key & certs.
    ```sh
    kubectl get secret -n kafka-websocket-proxy-sample test-client-certs -o jsonpath='{.data.ca\.crt}' | base64 -d > ca.crt
    kubectl get secret -n kafka-websocket-proxy-sample test-client-certs -o jsonpath='{.data.tls\.crt}' | base64 -d > client.crt
    kubectl get secret -n kafka-websocket-proxy-sample test-client-certs -o jsonpath='{.data.tls\.key}' | base64 -d > client.key
    ```
6. Run the client
    ```sh
    bazel run //ns/kafka-websocket-proxy/examples/go-client -- --addr kafka-websocket-proxy-sample.woven-city.local:7029 --mtls
    ```

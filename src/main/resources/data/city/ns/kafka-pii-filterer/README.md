# kafka-pii-filterer

**About this document**

Contents targeted to Agora members are written in this README.md.
Contents targeted to Agora members + non-Agora members are located in ./docs.

## What is this

See https://docs.google.com/document/d/1_ySF-gpb7sJf5l43QOHSETnD5oL-GTjkEm8IAioi7I8/edit#heading=h.970wrr26nmmp.

## Design

Please refer to [TN-0353 Async messaging and privacy, consent - Filterer](https://docs.google.com/document/d/1_ySF-gpb7sJf5l43QOHSETnD5oL-GTjkEm8IAioi7I8/edit#) for the design decisions.

## Development

### Running filterer locally

#### Prerequisite

1. Run the local cluster and the depending components.
    1. Follow [local cluster README](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/local/README.md).
    2. Run `kafka`, `kafka-operator-system` by uncommeting them in [system/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/system/kustomization.yaml).
    3. Look at [kafka-pii-filterer.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kafka-pii-filterer.yaml) and run the filterer's dependent components by uncommenting them from [services/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml).
    4. Also recommended to run `kafka-admin` for easier debugging experience by uncommenting it in [services/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml).
2. Prepare the dummy consent data.
    1. Make sure consent service and up and running.
    2. Run the following commands
        ```sh
        kubectl -n consent port-forward svc/consent 8183:80 &

        curl -X POST http://localhost:8183/v2alpha/admin/service_mapping --data '{"clients":[{"client_id":"kafka-filterer-consumer"}],"service_name":"kafka-filterer-sample"}' -H "Content-Type: application/json"
        curl -X POST http://localhost:8183/v2alpha/consents --data '{"data_attributes":["PERSON_NAME"],"service_name":"kafka-filterer-sample","user_id":"woven-id-1"}' -H "Content-Type: application/json"
        curl -X POST http://localhost:8183/v2alpha/consents --data '{"data_attributes":["PERSON_NAME"],"service_name":"kafka-filterer-sample","user_id":"woven-id-3"}' -H "Content-Type: application/json"
        curl -X POST http://localhost:8183/v2alpha/consents --data '{"data_attributes":["PERSON_NAME"],"service_name":"kafka-filterer-sample","user_id":"woven-id-5"}' -H "Content-Type: application/json"
        curl -X POST http://localhost:8183/v2alpha/consents --data '{"data_attributes":["PERSON_NAME"],"service_name":"kafka-filterer-sample","user_id":"woven-id-7"}' -H "Content-Type: application/json"
        curl -X POST http://localhost:8183/v2alpha/consents --data '{"data_attributes":["PERSON_NAME"],"service_name":"kafka-filterer-sample","user_id":"woven-id-9"}' -H "Content-Type: application/json"

        # Spot testing it was configured properly.
        curl -X 'GET' 'http://localhost:8183/v2alpha/be/check_consent?client=kafka-filterer-consumer&dataattrs=PERSON_NAME&user=woven-id-3'
        # Expecting `{"status":"CONSENT_GRANTED"}`

        # Kill the port-forwarding
        fg
        # -> <Ctrl+C>
        ```

#### Run with Bazel

1. Port-forward services.
    ```sh
    kubectl -n kafka port-forward svc/cityos-kafka-kafka-bootstrap 9092:9092 &
    kubectl -n apicurio port-forward svc/apicurio 8080:8080 &
    kubectl -n consent port-forward svc/consent 9001:9001 &

    # Don't forget to kill them when you are done.
    ```
2. Use your favorite editor to add the following lines to your /etc/hosts file (sudo required).
    ```
    # For local Kafka testing
    127.0.0.1 cityos-kafka-kafka-bootstrap.kafka.svc cityos-kafka-kafka-0.cityos-kafka-kafka-brokers.kafka.svc
    ```
3. Run filterer with Bazel
    ```sh
    export KAFKA_BOOTSTRAP_SERVERS="cityos-kafka-kafka-bootstrap.kafka.svc:9092"
    export KAFKA_CONSUMER_GROUP="kafka-pii-filterer.simple-avro-maven-topic"
    export SOURCE_TOPIC="kafka-filterer-producer.simple-avro-maven-topic"
    export SINK_TOPIC="kafka-filterer-consumer.simple-avro-maven-topic"
    export SCHEMA_REGISTRY_URL="http://127.0.0.1:8080"
    export CONSENT_SERVICE_HOST="127.0.0.1"
    export CONSENT_SERVICE_PORT="9001"
    export CLIENT_NAME="kafka-filterer-consumer"
    export LOG_LEVEL="TRACE"

    bazel run //ns/kafka-pii-filterer:java_binary
    ```

#### Run in the local cluster

1. Build your Docker image
    ```sh
    bazel run //ns/kafka-pii-filterer:java_image
    export VERSION=<YOUR_VERSION> # e.g. `v0.0.1`
    export ARTIFACTORY_PATH=<YOUR_ARTIFACTORY_PATH> # e.g. `koh.satoh`
    docker tag bazel/ns/kafka-pi-filterer/src/main/java/global/wovencity/agora/kafka/privacy/filterer:Main docker.artifactory-ha.tri-ad.tech/${ARTIFACTORY_PATH}/kafka-pi-filterer:${VERSION}
    docker push docker.artifactory-ha.tri-ad.tech/${ARTIFACTORY_PATH}/kafka-pi-filterer:${VERSION}
    ```
2. Make the following change to the k8s manifests, commit the change and push it to your remote branch, and wait for flux to pick that up.
    - Uncomment `- kafka-pii-filterer.yaml` in [services/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml)
    - In [kafka-pii-filterer/kustomization.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/infrastructure/k8s/environments/local/clusters/worker1-east/kafka-pii-filterer/kustomization.yaml), switch `newImage` and `newTag` pair to those for local dev.
3. When you update the code, you build the image & restart the pod.
    ```sh
    kubectl -n kafka-pii-filterer rollout restart deploy kafka-pii-filterer
    ```

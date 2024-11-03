# Proximity Service Prototype for CLP3

* JIRA: https://jira.tri-ad.tech/browse/CLP-260
* Confluence: https://confluence.tri-ad.tech/x/Af8AKw

## Debug instructions

Add these line within go_image entry in main/BUILD (It doesn't work now)

```
    env = {
        "URL_LOCATIONSERVICE": "http://common-backend.tri-ad.tech/location-service/api/v1",
        "CONSENT_SERVICE_GRPC_HOST": "localhost:3001",
    },
```

Configure

    bazel run //:gazelle

Build

    bazel build //projects/proximity-service-clp3/main

Run (either of two)

    bazel run //projects/proximity-service-clp3/main
    go run main/main.go 

Local smoke test

    curl -v http://localhost:8001/api/v1/version

Remote smoke test

    curl -v https://proximity-service.cityos-dev.woven-planet.tech/api/v1/version

Push container image

    bazel build //projects/proximity-service-clp3:push_server_image

## Test environments

* Local endpoints
    * proximity-service-clp3: localhost:8080
    * consent-service: localhost:3001 (gRPC)
    * location-service: http://common-backend.tri-ad.tech
* GPS injector: https://clp3-kafka-data-sender.cityos-dev.woven-planet.tech/api/v1/upload
* Remote endpoints
    * proximity-service-clp3:  https://proximity-service.cityos-dev.woven-planet.tech
    * consent-service: consent.consent.svc.cluster.local:9001 (gRPC)
    * location-service: http://location-service.clp3-location-service.svc.cluster.local:8080
    * GPS injector: https://clp3-kafka-data-sender.cityos-dev.woven-planet.tech/api/v1/upload

## Local test instructions

Copy .env.example to .env

### VSCode launch.json entry

```json
        {
            "name": "proximity-service",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            "cwd": "${workspaceFolder}/projects/proximity-service-clp3",
            "program": "${workspaceFolder}/projects/proximity-service-clp3/main/main.go"
        }
```

### Unit test

    go test ./...

or

    bazel test //projects/proximity-service-clp3/...

## Configure Consent service

### Configure consent service on local

```shell
docker compose -f ../../ns/privacy/consent_101/docker-compose.yaml up
CONSENT='http://localhost:3000'
curl -i $CONSENT/readyz
```

### Configure consent service on remote

See [RPT: Consent service](https://confluence.tri-ad.tech/x/OPSYL) in detail

```shell
CONSENT='https://agora-ui.cityos-dev.woven-planet.tech/api/consent'
curl -i $CONSENT/readyz
```

### Consent Manager configuration for test

```shell
# Configure CoffeeApp service to rapid-prototyping client:
curl -i "$CONSENT/v2alpha/admin/service_mapping" \
    -H "Content-Type: application/json" \
    -d '{ "service_name": "coffee-service", "clients": [ {"client_id": "rapid-prototyping"},  {"client_id": "city-ingress"}] }'

# User 00000000-1111-2222-3333-444444444444 grants PERSON_PROXIMITY to CoffeeApp
curl -i $CONSENT/v2alpha/consents \
    -H "Content-Type: application/json" \
    -d '{ "user_id": "00000000-1111-2222-3333-444444444444", "service_name": "coffee-service", "data_attributes": [ "PERSON_PROXIMITY" ] }'

# Delete CoffeeApp service
curl -i -X DELETE "$CONSENT/v2alpha/admin/service_mapping/service/coffee-service"
```

### Local End to End test

```shell
echo device,00000000-1111-2222-3333-444444444444,os,ver,time,35.68902,139.77254,10.5, |
curl -X POST -F file=@- https://clp3-kafka-data-sender.cityos-dev.woven-planet.tech/api/v1/upload
```

```shell
curl -i 'http://localhost:8001/api/v1/filter/radius' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -H 'X-Forwarded-Client-Cert: URI=spiffe://cluster.local/ns/city-ingress/sa/ingressgateway'  \
  -d '{
  "lon": 139.77287,
  "lat": 35.68844,
  "radius": 100,
  "users": [
    "00000000-1111-2222-3333-444444444444", "00000000-0000-0000-0000-000000000000"
  ]
}'

{"users":["00000000-1111-2222-3333-444444444444"]}
```

## Remote End to End test

Call `clp3-kafka-data-sender` like above.

```shell
curl -i 'https://proximity-service.cityos-dev.woven-planet.tech/api/v1/filter/radius' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "lon": 139.77287,
  "lat": 35.68844,
  "radius": 100,
  "users": [
    "00000000-1111-2222-3333-444444444444", "00000000-0000-0000-0000-000000000000"
  ]
}'
```

## Bduilding .proto file for gRPC

```shell
brew install protobuf
go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.31
go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.2
(cd ../../ns/privacy/consent/proto/v0/ && protoc --go_out=. --go_opt=Mconsent_service.proto=../v0 --go-grpc_out=. --go-grpc_opt=Mconsent_service.proto=../v0 consent_service.proto)
```


# Backend-of-traffic-signal

## Dashboards

- [Over View](https://observability.cityos-dev.woven-planet.tech/grafana/d/7fea7e00ec343eb65a60c2bee21aee06c4d9cf46/backend-of-traffic-signal-dashboard?orgId=1&refresh=5m&var-severity=INFO&var-tag=All&var-keyword=)

## Set up development environments

### Install asdf

See [Local Environment Building for Windows - asdf (but will not be maintained any more)](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151770232#id-%E3%83%AD%E3%83%BC%E3%82%AB%E3%83%AB%E7%92%B0%E5%A2%83%E6%A7%8B%E7%AF%89forWindows-asdf)

### Install Go

```sh
asdf install golang 1.21.0
asdf global golang 1.21.0
```

#### How to check

```sh
go version
go version go1.21.0 linux/amd64
```

### Install library

See [here](https://github.com/wp-wcm/city/pull/22707#:~:text=https%3A//github.com/wp%2Dwcm/city/blob/main/docs/development/go/README.md%23updating%2Ddependencies).

```sh
bazel run @go_sdk//:bin/go -- get -u <some_dependency>@<version>
bazel run //:go_mod_tidy
```

### Download and place certs files

1. download certs files from 1PassWord for TrafficSignal Project.  (<span style="color: red; "> DO NOT upload these files to the repository. </span>)
2. place certs files as following folder structure.

<pre>
traffic-signal
   └─ certs/  
         ├─ iota/
         │    ├─ fss-ts-BTS{seqNo}_ca.pem
         │    ├─ fss-ts-BTS{seqNo}_crt.pem
         │    └─ fss-ts-BTS{seqNo}_key.pem
         ├─ iotcoreAdk/
         │    ├─ MQTT-ADK-AmazonRootCA1.pem
         │    ├─ MQTT-ADK-certificate.pem.crt
         │    └─ MQTT-ADK-private.pem.key
         └─ iotcoreBts/
               ├─ MQTT-TS-AmazonRootCA1.pem
               ├─ MQTT-TS-certificate.pem.crt
               └─ MQTT-TS-private.pem.key
</pre>

### Export Environment variables

```sh
export REAL_TS_BROKER_NAME=IOTCORE_TS   # change to IOTA in the future.
export SIM_TS_BROKER_NAME=MOSQUITTO     # change to IOTA in the future.
export EPALETTE_BROKER_NAME=IOTCORE_ADK # change to IOTA in the future.
export GUIDEMOBI_BROKER_NAME=IOTA
export EPALETTE_INSTANCE_NAME=VWC01
export GUIDEMOBI_INSTANCE_NAME=GM01
export MQTT_IOTCORE_ADK_ENDPOINT=ssl://a2o2dh20a7ylnn-ats.iot.ap-northeast-1.amazonaws.com:8883
export MQTT_IOTCORE_ADK_CLIENT_ID={You can assign any id}   # You need to edit.
export MQTT_IOTCORE_ADK_PATH_TO_ROOT_CA={Path to city}/projects/traffic-signal/certs/iotcoreAdk/MQTT-ADK-AmazonRootCA1.pem       # You need to edit.
export MQTT_IOTCORE_ADK_PATH_TO_CERTIFICATE={Path to city}/projects/traffic-signal/certs/iotcoreAdk/MQTT-ADK-certificate.pem.crt # You need to edit.
export MQTT_IOTCORE_ADK_PATH_TO_PRIVATE_KEY={Path to city}/projects/traffic-signal/certs/iotcoreAdk/MQTT-ADK-private.pem.key     # You need to edit.
export MQTT_IOTCORE_TS_ENDPOINT=ssl://aft8p97py49u8-ats.iot.ap-northeast-1.amazonaws.com:8883
export MQTT_IOTCORE_TS_CLIENT_ID={You can assign any id}    # You need to edit.
export MQTT_IOTCORE_TS_PATH_TO_ROOT_CA={Path to city}/projects/traffic-signal/certs/iotcoreBts/MQTT-TS-AmazonRootCA1.pem         # You need to edit.
export MQTT_IOTCORE_TS_PATH_TO_CERTIFICATE={Path to city}/projects/traffic-signal/certs/iotcoreBts/MQTT-TS-certificate.pem.crt   # You need to edit.
export MQTT_IOTCORE_TS_PATH_TO_PRIVATE_KEY={Path to city}/projects/traffic-signal/certs/iotcoreBts/MQTT-TS-private.pem.key       # You need to edit.
export MQTT_MOSQUITTO_ENDPOINT=cis.wcm.tri-ad.tech:1883
export MQTT_MOSQUITTO_CLIENT_ID={You can assign any id}     # You need to edit.
export MQTT_IOTA_ENDPOINT=mqtts://iot.cityos-dev.woven-planet.tech:8883
export MQTT_IOTA_CLIENT_ID={You can assign any id}          # You need to edit.
export MQTT_IOTA_PATH_TO_ROOT_CA={Path to city}/projects/traffic-signal/certs/iota/fss-ts-BTS00_ca.pem       # You need to edit.
export MQTT_IOTA_PATH_TO_CERTIFICATE={Path to city}/projects/traffic-signal/certs/iota/fss-ts-BTS00_crt.pem  # You need to edit.
export MQTT_IOTA_PATH_TO_PRIVATE_KEY={Path to city}/projects/traffic-signal/certs/iota/fss-ts-BTS00_key.pem  # You need to edit.
export MQTT_IOTA_TENANT_NAME=traffic-signal-iota
export MQTT_IOTA_USERNAME={refer 1PassWord}                 # You need to edit.
export MQTT_IOTA_PASSWORD={refer 1PassWord}                 # You need to edit.
export IS_REAL=false                                        # You can switch.
export IS_IN_AGORA=false
export TLS_CONFIG_INSECURE_SKIP_VERIFY=true
```

## Unit test

### Run unit test

```sh
bazel test //projects/traffic-signal/...
```

### Coverage collection

Measure test coverage.

```sh
go test -cover -count=1 ./...
```

Specifically display which rows are not covered.

```sh
rm cover.out
rm cover.html
go test -cover "-coverprofile=cover.out" "-coverpkg=./..." "./..."
go tool cover -html=cover.out -o cover.html
```

## Build and Run

### Build

```sh
bazel build //projects/traffic-signal/...
```

### Run

```sh
bazel run //projects/traffic-signal/internal/cmd
```

### Generate Mock Files

```sh
go generate ./...
```

#### Run only the first time for generating mock files

This project uses `go.uber.org/mock/mockgen` because `github.com/golang/mock/mockgen` has not been maintained anymore.

If `github.com/golang/mock/mockgen` is installed to your environment.

```bash
go clean -i -n go get github.com/golang/mock/mockgen
```

then, run command below.

```bash
go install go.uber.org/mock/mockgen@latest
```

## Test cases

See [here](https://docs.google.com/document/d/1KVkccIwZUffUOxhJRPYp2j4S67dbZcdoxiVLvk1GZPY/edit).
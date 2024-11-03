# Overview
Integration test for device consumer. It call provision device and send MQTT messages to log/trace consumers

## Development
### Setup
* Install ginkgo and gomega
  * https://onsi.github.io/ginkgo/#installing-ginkgo
* Create `tests_suite_test.go` (Already done)
```sh
cd <workspace>/ns/iot/iota-ota/integration
ginkgo bootstrap
```

### Create a new test spec
* run 
```sh
ginkgo generate <spec name>
```

### Running a test      
lab2
```
 kubectx lab2-worker1-east
export ID_USERNAME=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.username}' | base64 -d)
export ID_PASSWORD=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.password}' | base64 -d)
export ID_ENDPOINT="https://id.agora-lab.w3n.io"
export IOTA_ENDPOINT="https://iot.agora-lab.w3n.io"
export MQTT_ENDPOINT="tls://iot.agora-lab.w3n.io:8883
ginkgo
```

dev2
```
kubectx dev2-worker1-east
export ID_USERNAME=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.username}' | base64 -d)
export ID_PASSWORD=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.password}' | base64 -d)
export TOTP_KEY=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.otp-key}' | base64 -d)
export ID_ENDPOINT="https://id.agora-dev.w3n.io"
export IOTA_ENDPOINT="https://iot.agora-dev.w3n.io"
export MQTT_ENDPOINT="tls://iot.agora-dev.w3n.io:8883
ginkgo
```

Prod
```
kubectx dev2-worker1-east 
export IS_PUBLIC="false|true"
export ID_USERNAME=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.username}' | base64 -d)
export ID_PASSWORD=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.password}' | base64 -d)
export TOTP_KEY=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.otp-key}' | base64 -d)
export ID_ENDPOINT="https://id.woven-city.toyota"
export IOTA_ENDPOINT="https://iot.woven-city-api.toyota"
export MQTT_ENDPOINT="tls://iot.woven-city-api.toyota:8883"
ginkgo
```
* we don't have bob-secret in prod yet so use the same bob-credentials in dev2 for now

or run via bazel with
```sh
bazel run //ns/iot/iota/tests/iota_access:iota_access_test
```
* Run in testkube (CICD) 
  * https://testkube.agora-dev.w3n.io/tests/iota-consumer-e2e

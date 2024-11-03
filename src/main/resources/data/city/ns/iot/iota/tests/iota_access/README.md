# Overview
A simple test to test the IoTA's access



### Running a test      
Prod
```
kubectx dev2-worker1-east 
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
bazel run //ns/iot/iota/cmd/devicelog/integration:integration_test
```

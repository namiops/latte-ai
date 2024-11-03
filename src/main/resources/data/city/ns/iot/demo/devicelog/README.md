# IoTA python client.

This is a sample script to send logs via MQTT to Agora.

## Running a script:

#### SIGNAL KINDS
- logs
- traces

#### CLUSTER and HOST
    lab: iot.agora-lab.woven-planet.tech
    lab2: iot.agora-lab.w3n.io
    dev: iot.cityos-dev.woven-planet.tech

```bash
SIGNAL_KIND=<SIGNAL_KIND> \
TENANT_NAME=<TENANT> \
GROUP_NAME=<GROUP> \
DEVICE_NAME=<DEVICE> \
IOTA_CLUSTER=<CLUSTER> \
IOTA_BROKER_HOST=<HOST> \
IOTA_BROKER_PORT=8883 \
IOTA_PATH=/home/$USER/.iota/${IOTA_CLUSTER}/${TENANT_NAME}/${GROUP_NAME}/${DEVICE_NAME} \
IOTA_CA_PEM_FILE=${IOTA_PATH}_ca.pem \
IOTA_CRT_PEM_FILE=${IOTA_PATH}_crt.pem \
IOTA_KEY_PEM_FILE=${IOTA_PATH}_key.pem \
IOTA_BROKER_CREDENTIALS_JSON_FILE=${IOTA_PATH}_broker.json \
bazel run //ns/iot/demo/devicelog:run
```

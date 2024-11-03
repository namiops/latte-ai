# MQTT Load Test
This is the script for publishing MQTT messages with multiple thread with the specified rate.

## How to run this script

### Prerequists
Please prepare a device that is already provisioned.

####  adding a group

```
iotactl add group <group-name>
```

####  adding a device

```
iotactl add device <device-name> --group <group-name>
```

#### provision a device
```
iotactl provision <device-name> --provision-secret <provision secret(You will get it when creating a group)> --group <group-name>
```
After running these command, you will see the credentials are stored under the directory of `<base directory>/<context>/<tenant>/<group>/<device>/`

### Execution
Please run with these arguments

```
bazel run //ns/iot/demo/mqtt-load-test -- --num-clients 2 --num-messages 2 --context [context] --tenant <tenant> --group <group> --device <device> --mqtt-broker <broker url> --pause-between-messages <seconds of pausing messages ex:1s>
```

example command(SW dev):

```
bazel run //ns/iot/demo/mqtt-load-test -- --num-clients 2 --num-messages 4 --context sdev --tenant dev-test --group load-test --device test-device1 --mqtt-broker mqtts://dev-iot.woven-city-api.toyota:8883 --pause-between-messages 1s
```

Note: These arguments (`--context`, `--tenant`, `--group`, `--device`, `--mqtt-broker`) are required.

### After running test
Please cleanup the device and group if you created only for this test.

#### Delete a device

```
iotactl delete device <device-name> --group <group name> --tenant <tenant name>
```

#### Delete a group
```
iotactl delete group <group-name> --tenant <tenant name>
```
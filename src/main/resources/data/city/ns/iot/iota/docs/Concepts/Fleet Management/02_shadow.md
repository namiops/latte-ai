# Device Shadow

!!! Definition
    
    A device shadow is a persistent virtual representation of a device.

IoT devices face several problems related to connectivity and availability due to the nature of a device and its use (moving devices, battery powered devices,...) and therefore we need a mechanism to manage and update devices when they are offline without worrying about timing and synchronization.

This SHOULD happen in a transparent way to the owners / users.

To solve this problem we decided to adopt a common pattern and implement a virtual state representation of a device state, called device shadow.

## Device shadow synchronization flow

In order to synchronize a device to its shadow, we need to consider to main entry points:

1. A device boots and wants to synchronize
2. A user sets a state and the device needs to update itself

Both entry points converge in the same main flow described below, but we can summarize the flow in 3 main parts:

1. A device sends its current state on the `<groupname>/<device>/shadow/reported/` reserved topic
2. A user sets the **desired state** via API call
3. Backend calculates the difference between the desired state and reported state and sends this **delta** on the `<groupname>/<device>/shadow/delta/` reserved topic.

!!! Note
    The resulting delta that is sent to your device will be encoded for transport, we use [standard Go JSON encoding](https://pkg.go.dev/encoding/json#Marshal:~:text=String%20values%20encode,u2028%22%2C%20and%20%22%5Cu2029%22)
    this means that special characters will be encoded (e.g. `&` --> `\u0026`).
    You will need to instruct your JSON unmarshaller to decode these value, otherwise your URL will not work.

The flow is designed so that at any point of the flow if some message is not received a second message can recover the normal flow without manual intervention. A good rule of thumb is to have a device resending its reported state after a set timeout if no delta is received.

!!! Note
    
    IoTA will send an empty delta message `{}` to acknowledge that a reported state has been received and it matches with the desired state. In any other case, in order to avoid sending complete payloads on the broker, the delta message will be reporting only the properties that are different between the reported and the desired state respecting the nesting.

Agora's services and DB in lightgreen, external storage for software or firmware download in cyan.

=== "Successful flow"
    
    ```plantuml
    @startuml
    autonumber
    participant Device
    participant ShadowService as S #lightgreen
    participant DB #lightgreen
    participant Storage #cyan
    participant User

    Device -> S: subscribe to <i>groupX/d123/shadow/delta</i>

    alt Flow starts upon user update
    User -> S: PUT ...groupX/devices/{deviceName}/shadow {firmware:{version:1.2}}
    S -> DB: update **<font color="red">desired</font>** state
    else Flow starts upon device reporting state
    Device -> S: publish **<font color="blue">reported</font>** state on <i>groupX/d123/shadow/reported</i>
    note top
    {
    "firmware":{
        "version":"1.1"
        "url":"ftp://any",
        "protocol":"ftp"
    }
    }
    end note
    end alt
    S -> DB: retrieve shadow state
    alt reported != desired
    S -> DB: update **<font color="blue">reported</font>** state

    else reported == desired
    S -> S: nothing to update
    end alt
    S -> S: diff desired and reported state
    S -> Device: publish **<font color="orange">delta</font>** on <i>groupX/d123/shadow/delta</i>
    note right
    {
    "firmware":{
        "version":"1.2",
        "url":"ftp://any",
        "protocol":"ftp"
    }
    }
    end note
    opt
    Device -> Device: check local version and delta version and discard if not >
    end
    Device -> Storage: download firmware
    Device -> Device: update firmware
    Device -> S: publish **<font color="blue">reported</font>** state on <i>groupX/d123/shadow/reported</i>
    note left
    Restart from #4
    end note

    @enduml
    ```

=== "Flow with recovery"
    
    ```plantuml
    @startuml
    autonumber
    participant Device
    participant ShadowService as S #lightgreen
    participant DB #lightgreen
    participant Storage #cyan
    participant User

    Device -> S: subscribe to <i>groupX/d123/shadow/delta</i>

    alt Flow starts upon user update
    User -> S: PUT ...groupX/devices/{deviceName}/shadow {firmware:{version:1.2}}
    S -> DB: update **<font color="green">desired</font>** state
    else Flow starts upon device reporting state
    Device -[#red]>x S: <font color="red">failed publish **reported** state on <i>groupX/d123/shadow/reported</i> </font>
    Device -> S: publish **<font color="blue">reported</font>** state on <i>groupX/d123/shadow/reported</i>
    note top
    {
    "firmware":{
        "version":"1.1"
        "url":"ftp://any",
        "protocol":"ftp"
    }
    }
    end note
    end alt
    S -> DB: retrieve shadow state
    alt reported != desired
    S -> DB: update **<font color="blue">reported</font>** state

    else reported == desired
    S -> S: nothing to update
    end alt
    S -> S: diff desired and reported state
    S -[#red]>x Device: <font color="red">failed publish **delta** on <i>groupX/d123/shadow/delta</i> </font>
    note right
    {
    "firmware":{
        "version":"1.2",
        "url":"ftp://any",
        "protocol":"ftp"
    }
    }
    end note
    Device -> S: publish **<font color="blue">reported</font>** state on <i>groupX/d123/shadow/reported</i>
    note top
    {
    "firmware":{
        "version":"1.1"
        "url":"ftp://any",
        "protocol":"ftp"
    }
    }
    end note
    S -> DB: retrieve shadow state
    alt reported != desired
    S -> DB: update **<font color="blue">reported</font>** state

    else reported == desired
    S -> S: nothing to update
    end alt
    S -> S: diff desired and reported state
    S -> Device:publish **<font color="orange">delta</font>** on <i>groupX/d123/shadow/delta</i>
    note right
    {
    "firmware":{
        "version":"1.2",
        "url":"ftp://any",
        "protocol":"ftp"
    }
    }
    end note
    opt
    Device -> Device: check local version and delta version and discard if not >
    end
    Device -> Storage: download firmware
    Device -> Device: update firmware
    Device -> S: publish **<font color="blue">reported</font>** state on <i>groupX/d123/shadow/reported</i>
    note left
    Restart from #4
    end note

    @enduml
    ```

Although the flow above is the suggested flow, ultimately the device side behaviour MUST be decided and tuned by the device developer based on factors like battery life (we don't want to send frequent messages because they might drain the battery), mission critical payloads, state size, etc.

### A note on the topics

The topics `<groupName>/<deviceName>/shadow/reported` and `<groupName>/<deviceName>/shadow/delta` are reserved topics and MUST go only in 1 direction to avoid issues and conflicts. A device MUST report on the `reported` topic and consume from the `delta` topic, on which IoTA will be publishing updates.

## CLI commands for device shadow

In order to smoothly use the device shadow for a given device or group of devices we provide a set of CLI commands to interact with our [REST API](https://developer.woven-city.toyota/catalog/default/api/iota-api/definition#/).

Once a device is registered (check [Introducing iotactl](#introducing-iotactl) for more details) you can set the desired state of a device through the `iotactl set-shadow` command.

```shell
iotactl set-shadow device drone-1 -g drones -j '{"name": "broadcasting", "value": "idle"}'
```

and then retrieve it with

```shell
iotactl get-shadow device drone-1 -g drones
```

But we assumed also your group of devices might have the same virtual state, therefore to update all of them at once you can run

```shell
iotactl set-shadow group drones -j '{"name": "broadcasting", "value": "idle"}'
```

!!! Warning
    
    This is a destructive action: when you set the group shadow all the devices' shadows in the group will be updated to match the group default, but you can then update each device's shadow as you like without this rolling back to the group's state.

After setting a group state, it will be stored as the default state for devices
added to the group later. This single command can therefore be used to set the
state of all currently existing devices in a group, as well as future devices.

It is also possible to retrieve the shadow states of multiple devices in a group
using in a single command. Without any extra parameters, this returns a
pretty-printed list of all devices in the group.

```shell
iotactl get-shadow devices -g drones
```

This behavior may not be always desirable, especially for larger groups with
hundreds of devices. If you want to reduce the number of results, you can use
the pagination function using the `--page-size` and `--page-num` parameters.

```shell
iotactl get-shadow devices -g drones --page-size=10
```

This way, you will only receive the state of the first 10 devices in the given group.
In this case, the devices are sorted by their name, in an ascending ASCII order.

The `--page-num` parameter starts from `0`, which is its default value, and so
it does not need to be specified when checking the first page of results. In
order to access further pages, you simply increment this number.

```shell
iotactl get-shadow devices -g drones --page-size=10 --page-num=1
iotactl get-shadow devices -g drones --page-size=10 --page-num=2
# ...
iotactl get-shadow devices -g drones --page-size=10 --page-num=99
iotactl get-shadow devices -g drones --page-size=10 --page-num=100
```

Please note that each paginated command call is completely independent of the
previous ones. When a new device is created, or a previously existing device is
deleted, this can cause a race condition for the user viewing this list. The
result can be that a device is returned on two different pages, or that a device
is skipped completely and does not appear on any page.

For that reason, this feature and its corresponding API endpoint are not meant
for consumption by services that require perfect consistency of the returned
results across multiple calls. The primary purpose is to allow users to query a
subset of devices, to get a quick overview of the state of their group, and to
reduce unnecessary load on the IoTA service, as well as the underlying database.

### Merge and overwrite modes

By default, the `set-shadow` command operates in a _merge_ mode. This mode
merges the provided JSON into the existing state on server-side, which allows
the user to quickly set a subset of properties without affecting others. This is
especially useful when changing desired state properties across a group of
devices. In such case, only the explicitly declared properties are affected, and
other properties will retain their device-specific values. The `set-shadow
group` command is handled on a device-by-device basis, with each device's state
merged independently of the others.

In order to completely overwrite the desired state, the user can use the
`--overwrite` flag. This applies to both the `set-shadow device` and `set-shadow
group` subcommands. The new desired state will be replace the existing

The merging process has the following rules:

- If there is no previous desired state, the provided JSON document is used as
  is without any merging logic.
- Only JSON objects (AKA maps or dictionaries) are merged. The merging logic is
  infinitely recursive, and performed at every level of recursion, as long as
  the property keys exist on both sides.
- All other types, (numbers, strings, arrays and `null` values) are always
  overwritten if specified in the new partial desired state.
- If a property only exists in the old or the new state, it will be included in
  the resulting merged state.
- If a property exists on both sides, but its type differs, then the new value
  will be used regardless of the old value. The `null` value is considered its
  own type in this context.

If there is no previous desired state for a group or a device, the behavior will
be equivalent to using the `--overwrite` flag, and it will not result in any
error.

If the provided JSON document contains all properties from the existing state,
with the same key names, only differing in values, the result will also be
equivalent to using the `--overwrite`.

In the two aforementioned cases, the `--overwrite` flag is optional, but
recommended if the user's intention is to fully overwrite the state.

To better illustrate this functionality, assume the following existing desired
state of an imaginary camera device:

```json
{
    "enabled": true,
    "zoom": 1.0,
    "exposure": 0.02,
    "colorSensitivity": {
        "red": 0.5,
        "green": 0.5,
        "blue": 0.5
    }
}
```

In order to disable this device, we can run the following command:

```shell
iotactl set-shadow device camera-1 -g cameras -j '{ "enabled": false }'
```

The resulting merged desired state will look like this:

```json
{
    "enabled": false,
    "zoom": 1.0,
    "exposure": 0.02,
    "colorSensitivity": {
        "red": 0.5,
        "green": 0.5,
        "blue": 0.5
    }
}
```

The same result can be achieved by changing the value of the `"enabled"`
property in a JSON file on disk, and using the `--overwrite` flag instead.

```shell
iotactl set-shadow device camera-1 -g cameras -f '<JSON file path>' --overwrite
```

In this case, where no property was removed in the new JSON document's structure, omitting the `--overwrite` flag and performing this operation in the merge mode would have the exact same result.

Please note that using the `--overwrite` flag with an incomplete version of the
JSON document will completely overwrite the desired state of the device. If we
instead run

```shell
iotactl set-shadow device camera-1 -g cameras -j '{ "enabled": false }' --overwrite
```

then the resulting state will look like this:

```json
{
    "enabled": false
}
```

The other properties previously present in the desired state will be gone.
This behavior can be used to remove properties that are no longer desirable,
which cannot be otherwise achieved as an atomic operation in the merge mode.

The recursive logic can be used to change a subset of nested properties
without affecting others, such as by running the following command.

```shell
iotactl set-shadow device camera-1 -g cameras -j '{ "colorSensitivity": { "red": 1.0, "blue": 0.25 } }'
```

In this case, the resulting state will look like this:

```json
{
    "enabled": true,
    "zoom": 1.0,
    "exposure": 0.02,
    "colorSensitivity": {
        "red": 1.0,
        "green": 0.5,
        "blue": 0.25
    }
}
```

Please note that mistakenly setting a JSON object to another type will result in
a complete overwrite of its value.

```shell
iotactl set-shadow device camera-1 -g cameras -j '{ "colorSensitivity": 1.0 }'
```

This command will overwrite the previously nested JSON object with a number.

```json
{
    "enabled": true,
    "zoom": 1.0,
    "exposure": 0.02,
    "colorSensitivity": 1.0
}
```

Running the following command on the device's group instead can be used to
roll-out a configuration change across a fleet of devices, affecting only a
subset of properties in each of their desired states.

```shell
iotactl set-shadow group cameras -j '{ "exposure": 0.05, "colorSensitivity": { "green": 0.0 }, "interval": 1.5 }'
```

The desired state of other devices in the group, or even the default state of
the group will have no effect on the result of this operation. The example
device's new desired state will look like this:

```json
{
    "enabled": true,
    "zoom": 1.0,
    "exposure": 0.05,
    "colorSensitivity": {
        "red": 0.5,
        "green": 0.0,
        "blue": 0.5
    },
    "interval": 1.5
}
```

Please note that it is possible to set any number of properties in the merge
mode, regardless of their relative position in the JSON document structure. This
also includes new properties, as can be seen above with the addition of the new
`"interval"` property.

## Device Shadow Client

An example of Go client can be found in the [virtual device](../../../demo/virtual-devices/thermostat/cmd/main.go) example. To run the example set the JSON configuration file to spin as many devices as desired. An example:

```JSON
[
    {
        "group":"shadowtest",
        "name":"test-dev",
        "kind":"thermostat",
        "coordinates":[],
        "certPath":"<homedir>/.iota/test-dev_crt.pem",
        "keyPath":"<homedir>/.iota/test-dev_crt_key.pem",
        "cacertPath":"<homedir>/.iota/test-dev_crt_ca.pem",
        "url":"mqtts://iot.cityos-dev.woven-planet.tech:8883",
        "user":"<your-tenant>:shadowtest-abc123", // from provisioning
        "password":"<broker password>", // from provisioning
        "topic":"shadowtest/test-dev/telemetry"
    }
]
```

Once your device is running you can upload some file in a storage and save the following payload in a .json file in a directory of your choice (replace the url with your url)

```json
{
    "software": {
        "version": "1.0",
        "url": "http://someurlyoucanreach.com/firmware/version/1.0",
    }
}
```

 you can then run

```shell
iotactl set-shadow device <your device> -g <group> -f path/to/file.json
```

### A note about the delta calculation and state management

!!! Warning

    The device shadow mechanism will send only the differences between the desired state and the reported state.

Let's assume the following desired state is set by a user

```json
{
    "software":{
        "version":"1.0",
        "url":"http://somestorageurlyoucanreach.com/firmwares/asd123",
    },
    "lights":{
        "led1":{
            "status":"on",
            "brightness":80
        },
        "led2":{
            "status":"on",
            "brightness":20,
        }
    }
}
```

this will replace entirely the state on the database.
When a device sends the current state, a delta calculation will be performed by the state manager

```json
{
    "software":{
        "version":"0.1",
        "url":"http://somestorageurlyoucanreach.com/firmwares/bbb123",
    },
    "lights":{
        "led1":{
            "status":"on",
            "brightness":80
        },
        "led2":{
            "status":"on",
            "brightness":20,
        }
    }
}
```

and the resulting delta will look like

```json
{
    "software":{
        "version":"1.0",
        "url":"http://somestorageurlyoucanreach.com/firmwares/asd123",
    }
}
```

This payload will then be sent to the device over the `shadow/delta` topic and a device developer MUST make sure to write the correct logic and avoid assigning the deserialized delta directly to a locally stored state unless the other properties considered irrelevant.

### Device Shadow for OTA

A good application of Device Shadow is OTA update.

In this [technical note](https://docs.google.com/document/d/1ZZ55uXyhchEdBbx4uyUX_BzubWoOGvHhv2i7IYsKXKk/edit) we try to explain how the shadow architecture and flows can be modeled to exchange informations related to firmware and other softwares to install on the device and a fully working Python device simulator that performs device shadow synchronization and OTA can be found in this [demo code](https://github.com/wp-wcm/city/blob/main/ns/demo/temperature-svc/client/main.py).

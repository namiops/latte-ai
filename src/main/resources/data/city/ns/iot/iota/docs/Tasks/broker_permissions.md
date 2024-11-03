# Broker Permissions

The IoTA Permissions API allows authenticated and authorised IoTA users to
configure message broker permissions for IoT devices.

This API is designed for devices using the MQTT protocol for their messages,
with the concept of message topics, and the document focuses on such use-cases.

It is currently not guaranteed to be compatible with devices using the AMQP
protocol for their communication with the message broker.

At the moment, broker permissions can be configured with an IoTA device group
granularity. When setting the permissions for a given group, the new permissions
will be applied to all currently existing devices in the group, and stored as
the default value for that group. This default value will then be applied to
newly provisioned devices in that group in the future.

## Permissions configuration format

The permissions configurations are represented by JSON objects with the
following structure:

```json
{
    "tenant": {
        "configure": ".*",
        "read": ".*",
        "write": ".*"
    },
    "topic": {
        "exchange": "amq.topic",
        "read": "#",
        "write": "#"
    }
}
```

- `tenant` (optional) is passed unmodified to the `/api/permissions` RabbitMQ management API endpoint (see note 1 below)
  - `configure` (required) is a regular expression string
  - `read` (required) is a regular expression string
  - `write` (required) is a regular expression string
- `topic` (required) is processed by IoTA and passed to `/api/topic-permissions` RabbitMQ management API endpoint (see note 2 below)
  - `exchange` (optional) is a string that should be set to `amq.topic` for MQTT messages
  - `read` (required) is an MQTT topic path expression string
  - `write` (required) is an MQTT topic path expression string

### Note 1: Tenant permissions

See [the RabbitMQ documentation](https://www.rabbitmq.com/docs/access-control#authorisation)
for details on the individual fields' meaning. The regular expressions must
match the appropriate RabbitMQ resources (exchanges, queues, etc.). These fields
should be configured if further AMQP access restrictions are desired. Please
note that using these fields to deny access to the `amq.topic` exchange will
likely break MQTT functionality.

### Note 2: MQTT topic path expressions

To avoid some common issues with regular expressions, the `topic` permissions
are configured using the MQTT topic path syntax.

- `/` separates parts of the topic path
- `+` is a wildcard matching any single part of the topic path (between `/`)
- `#` is a wildcard matching any number of parts in the topic path (zero or more)

For more advanced uses, multiple topic path expressions can be combined together
using the `|` character, and sub-expressions can be wrapped in parentheses
(e.g., `{group}/({device}|broadcast)/#`). This syntax remains unmodified during
the conversion to regular expressions.

The topic path expressions will be converted to regular expressions according to
the following rules:

- `/` replaced with `\.`
- `+` replaced with `[^\.]+`
- `#` replaced with `.*`
- Add `^(` prefix and `)$` suffix to match the whole topic path and not just any substring

The purpose of this conversion and using MQTT topic path expressions instead of
regular expressions is to avoid the following bugs, which are prone to silently
causing major vulnerabilities, which tend to be extremely difficult to detect:

- Unescaped `.` instead of `\.`, accidentally matching any character
- Missing or misplaced `^` or `$`, allowing any substrings to match
- Any other unintentionally (un)escaped character, causing unexpected characters to match

### Variable substitution

The IoTA Permissions API supports the following variables, which will be
substituted when the permissions are being applied to the individual devices.

- `{tenant}` will be replaced with the name of the tenant
- `{group}` will be replaced with the name of the group
- `{device}` will be replaced with the name of the device

For example, an input MQTT topic path expression
`{tenant}/{group}/{device}/sensors/#` would look like this when applied to a
specific device after the variable substitution, before being converted into a
regular expression: `test-tenant/test-group-1/test-device-4/sensors/#` (assuming
the tenant name is `test-tenant`, the group name is `test-group-1`, and the
device is named `test-device-4`).

While it is the most common case, a variable does not necessarily need to fill
an entire part of the MQTT topic path. It is possible to add a prefix, a suffix,
or both, and the substitution will be performed just like before. It is also
possible to include multiple variables in a single part of the MQTT topic path.
For example, `qwer-{group}-asdf-{device}-zxcv/#` would become
`qwer-test-group-1-asdf-test-device-4-zxcv/#` for the device mentioned above.

Please note that, currently, variable substitution is performed only on the
`read` and `write` fields of the `topic` permissions, and is not performed on
any fields of the `tenant` permissions.

## Using `iotactl`

The `iotactl` utility provides the following commands to manage device broker
permissions via the IoTA Permissions API.

To set the permissions of a group via an inline permissions configuration JSON:

```sh
iotactl set-permissions group <Group Name> -j '{"topic":{"read":"#", "write":"#"}}'
```

To set the permissions of a group via a permissions configuration JSON file
stored on the disk:

```sh
iotactl set-permissions group <Group Name> -f '/path/to/permissions.json'
```

To get the current default permissions of a group, which will be applied to
newly added devices:

```sh
iotactl get-permissions group <Group Name>
```

The `--help` flag can be used to get additional information about the commands.

## Default permissions configuration

With all fields included, with optional fields set to their default (implicit) values:

```json
{
    "tenant": {
        "configure": ".*",
        "read": ".*",
        "write": ".*"
    },
    "topic": {
        "exchange": "amq.topic",
        "read": "#",
        "write": "#"
    }
}
```

The `tenant` permissions field is optional. If specified, all three of its
fields (`configure`, `read` and `write`) are required.

The `exchange` field under the `topic` permissions field is also optional and
generally not required when configuring the permissions for a device that only
uses the MQTT protocol for its messages.

A permissions configuration equivalent to the one above, without the optional
fields:

```json
{
    "topic": {
        "read": "#",
        "write": "#"
    }
}
```

By default, when a device is provisioned in a group where the permissions have
not been configured yet, the "topic" permissions will be unset (empty) on the
broker. This is functionally equivalent to the configuration above for MQTT
message traffic, giving full read and write access to all topics within the
tenant (vhost).

For MQTT messages, the `exchange` field should remain set to the default
`amq.topic` value. Therefore, omitting this field from permissions configuration
files is recommended, to avoid accidentally setting it to an invalid value.

## Examples

Allow access to all MQTT topics:

```json
{
    "topic": {
        "read": "#",
        "write": "#"
    }
}
```

Deny access to all MQTT topics:

```json
{
    "topic": {
        "read": "",
        "write": ""
    }
}
```

Allow all read access but deny all write access:

```json
{
    "topic": {
        "read": "#",
        "write": ""
    }
}
```

Allow write access to the MQTT topic sub-path of the device (as used by IoTA's
internal MQTT-based features) and read access to all the sub-paths of all
devices in the same group:

```json
{
    "topic": {
        "read": "{group}/+/#",
        "write": "{group}/{device}/#"
    }
}
```

Allow read and write access to IoTA device topic sub-path, write access to the
tenant-specific `sensors/` topic path prefix, and read access to the
tenant-specific `commands/` topic path prefix.

Here, we imagine a sensor device that writes some values to the
`sensors/{device}/+` topics and the whole group of devices receives group-wide
commands via the `commands/{group}` topic.

```json
{
    "topic": {
        "read": "{group}/{device}/#|commands/{group}",
        "write": "{group}/{device}/#|sensors/{device}/+"
    }
}
```

## Recommendations

It is strongly recommended that each device is allowed access to the
`{group}/{device}/#` topic path for smooth operation and full access to IoTA
functionality. This path contains sub-paths used for OTEL logs, traces and
metrics, as well as other IoTA features, such as the Device Shadow or Xenia
(device software update/version management). If none of this functionality is
required, it is possible to omit this topic path from your permissions
configuration.

Unless required for specific reasons, the default `tenant` permissions
configuration is recommended for all devices. Setting more restrictive values of
the `read` and `write` fields may result in conflicts with respective `read` and
`write` fields in the `topic` permissions configuration, according to internal
testing. The `configure` field does not appear to conflict the `topic`
permissions configuration.

```json
"tenant": {
    "configure": ".*",
    "read": ".*",
    "write": ".*"
}
```

## Caveats

There are a few potential pitfalls worth noting when using the IoTA Permissions.

### `tenant` vs `topic` permissions

As mentioned above, setting the `read` and `write` fields in the `tenant`
permissions configuration may conflict with the `read` and `write` fields in the
`topic` permissions configuration.

### MQTT client message publish handling

Some MQTT clients may exhibit unexpected behaviour when publishing a message to
a topic to which the user has denied access.

Even though the initial attempt to publish the message fails, no error may be
returned by the client to the calling code. Later, if access to the topic is
allowed, the client may suddenly publish these previously unsuccessful messages
at once. This delayed publishing can occur even a relatively long time (e.g., 15
minutes) after the initial failed attempt to publish the message.

More information on the observed behaviour relevant to this topic can be found
in the appendix below.

## Appendix: Observed IoTAD behaviour

During internal testing, IoTAD was observed having an unexpected behaviour
related to the MQTT topic permissions. Specifically, no error being returned
when a message is published to a topic, to which the device's user currently has
denied access (i.e., is unable to publish or subscribe to).

### Context

IoTAD uses an MQTT client from the `github.com/eclipse/paho.mqtt.golang` Golang
library. The following behaviour may or may not be specific to this MQTT client,
so please be aware of other clients possibly exhibiting the same behaviour.

when IoTAD receives a log/trace/metric message via Syslog or its internal HTTP
server, it attempts to push this message to the MQTT broker on its pre-assigned
topic `{group}/{device}/logs` (or `/traces`, `/metrics` for traces and metrics,
respectively).

### Details

When a message is published to an MQTT topic by IoTAD, the client waits for the
message to be handled, and then checks for an error. If there is any issue with
the connection to the MQTT broker, the certificates, etc., an error occurs.

However, when a message is published to a topic, to which the device's user on
the MQTT broker has currently denied access, no such error occurs.

This, in itself, was an unexpected behaviour for us. Furthermore, when the
permissions of the device's user are modified to allow access to the topic,
after IoTAD has already attempted to publish the message, waited for it to be
resolved, and checked for an error (which was empty), the message is then
published as soon as the new permissions are applied to the user.

During internal tests, the message was still published even after 15 minutes of
delay between the initial attempt and the setting of permissions to allow access
to the relevant topic. The initial attempt returns no error even if it fails.
This behaviour implies the existence of some hidden internal message buffer
managed by the MQTT client, which is currently out of the control of IoTAD's
code.

Restarting IoTAD negates this behaviour, and messages which could not have been
published due to denied access to a topic before the restart will not be
published after the restart even after the permissions are adjusted to allow
access to the topic.

An example scenario with this behaviour:

1. A device `dev1` exists in group `grp1`
2. Permissions are set via `iotactl set-permissions group 'grp1' -j '{"topic":{"read":"", "write":""}}'`,
   denying all MQTT topic access to the device
3. IoTAD is started on the device `dev1`
4. IoTAD receives a log message and attempts to push it to the `grp1/dev1/logs` topic
5. The message is not successfully published and will not be received on the backend
6. Permissions are set via `iotactl set-permissions group 'grp1' -j '{"topic":{"read":"#", "write":"#"}}'`,
   allowing access to all MQTT topics
7. The message is now suddenly published and received by the backend (OTEL Collector)

# IoTA Daemon `iotad`

In order to simplify and automate the device provisioning and certificate refresh processes, a small utility program is provided. This program, `iotad`, is designed to run as a daemon (background service) on the IoT device.

Using `iotad` removes the necessity for certificate management logic in your devices' applications. Once successfully started, it continues managing the device's certificate fully automatically.

## Prerequisites

In order to use `iotad`, it is first required that a device is created in the IoTA system. For the purpose of simplicity, this document assumes device creation via [the `iotactl` CLI](./iotactl.md).

Regardless of the device creation method, the following information is required before you can start using `iotad`:

- Name of tenant, group and device (chosen by the user)
- Provisioning secret of the device's group (obtained during group creation)
- IoTA service URL and context name (provided by the IoTA team)

## Operation flow

The following is a simplified overview of the tasks performed by `iotad`.

- When started, it will check for the presence of an existing certificate.
- If a certificate does not exist, it will use its configuration (see below) to perform the initial provisioning.
- When the device has a certificate, it will periodically check its expiration time against the configured threshold.
- Once the expiration threshold was reached, it will perform the certificate refresh flow to obtain a new certificate.

## Configuration

The `iotad` program expects a JSON configuration file located at `$HOME/.iota/iotad.json`. If this does not exist, it will not exit, but instead keep waiting for it in an infinite loop.

This means that you can run `iotad` on device start-up, and then add the configuration file to the device later via SSH/FTP/etc., and it will be automatically recognized and picked up by `iotad`.

### Template

The following is a sample configuration file containing all the currently existing fields.

```json
{
    "service_url": "https://iot.woven-city-api.toyota",
    "context": "prod",
    "tenant": "tenant-name",
    "group": "group-name",
    "device": "device-name",
    "provision_secret": "<GROUP PROVISIONING SECRET>",
    "ttl": 48,
    "cert_refresh_threshold": 24
}
```

!!! Note
    The provisioning secret is stored in `$HOME/.iota/iotad.json`, whereas the device secret will be saved in the `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/` directory upon completion of the provisioning process. In case of certificate refresh, while both the device secret and the provisioning secret can be used, the priority is given to the device secret. If the device secret does not exist, `iotad` will resort to using the provisioning secret to refresh the certificate.

#### Log, trace and metric message forwarding

The IoTA Daemon supports forwarding of log, trace and metric messages via Syslog
or OTEL-formatted messages via HTTP. Messages sent to these servers will be
forwarded to the appropriate MQTT topics for the configured device.

For the OTEL messages, the HTTP server handles the following two endpoints:

- `/v1/logs` for log messages
- `/v1/traces` for trace messages
- `/v1/metrics` for metric messages

Your OTEL library should have an HTTP exporter available that will allow you to
configure your device application to send its messages to the appropriate
endpoints of the IoTA Daemon HTTP server.

```json
{
    "service_url": "https://iot.woven-city-api.toyota",
    "context": "prod",
    "tenant": "tenant-name",
    "group": "group-name",
    "device": "device-name",
    "provision_secret": "<GROUP PROVISIONING SECRET>",
    "ttl": 48,
    "cert_refresh_threshold": 24,
    "logging": {
        "syslog_port": 1514,
        "mqtt_broker": "mqtts://iot.woven-city-api.toyota:8883",
        "http_server_port": 4318
    }
}
```

With the logging features enabled, upon starting the IoTA Daemon, you expect to
see the following log messages confirming a successful initialization of its
components. If the MQTT publisher fails to initialize (most likely due to an
expired, or otherwise invalid, certificate), it will automatically keep
retrying, so there is no need to restart IoTA Daemon manually in that case.

```log
2024/01/31 15:47:36 Device certificate is valid for 1.6 more hour(s); doing nothing...
2024/01/31 15:47:36 MQTT publisher successfully initialized
2024/01/31 15:47:36 Starting HTTP server on address "127.0.0.1:4318"
2024/01/31 15:47:36 Syslog server successfully listening and booted on "127.0.0.1:1514"
```

If you are unable to see the last three lines after adding the `logging`
configuration field to `iotad.json`, please confirm that you are using the
latest available version of the IoTA Daemon.

### Configuration file fields

The meaning of each configuration field is as follows.

- `service_url` is the URL of the IoTA service in your selected environment
- `context` is the name of the selected environment
- `tenant` is the name of your IoTA tenant
- `group` is the name of the device's group
- `device` is the name of the device
- `provision_secret` is the provisioning secret obtained when the device's group was created (or when the secret is force-refreshed), and it is shared by all devices in the group
- `ttl` is a duration in hours for which a newly generated certificate should be valid
- `cert_refresh_threshold` is a duration in hours, and the device's certificate will be refreshed when its remaining validity is lower than this threshold

All of the fields listed above are required by `iotad` in order to run, and their data types are strictly enforced.

The `context`, `tenant`, `group` and `device` fields are combined together to form the path, where the device's certificate and other files will be stored: `$HOME/.iota/<context>/<tenant>/<group>/<device>_*`.

#### Log level

IoTAD's default log level prevents debug-level messages from being printed to
the output stream (TTY, disk file, Syslog server, systemd journal, etc.).

Specifically, log messages indicating that its proxy servers have successfully
published a message on an MQTT topic are excluded at the default log level. This
helps to prevent infinite loops caused by directing IoTAD's own logs into one of
its own log servers (Syslog or HTTP).

Currently, IoTAD only distinguishes between log level `0` (the implicit, default
value) and any negative value (which enables debug-level logging).

To enable the debug-level logs, you can add a top-level field `log_level` into
the `iotad.json` configuration file and set its value to a negative integer.

The configuration should then look like this (unrelated fields omitted for brevity):

```json
{
    "service_url": "https://iot.woven-city-api.toyota",
    // ...
    "logging": {
        // ...
    },
    "log_level": -1
}
```

#### Extra `logging` fields

The `logging` section of the configuration is optional, but if included, the
individual fields are all required for the proxy features to function correctly.

It is currently NOT possible to individually enable only the Syslog or only the
HTTP server, so please make sure to configure the ports in a way that will not
cause any conflicts on the device.

Please note that both the Syslog and the HTTP server are configured to listen on
IPv4 loopback host (`127.0.0.1`) by default. If a different host binding is
required, the optional `server_bind_host` field needs to be set accordingly.

- `syslog_port` is the port on which the Syslog server will be listening
- `mqtt_broker` is the full URL of the target MQTT broker, to which the logged
  messages will be forwarded, and the domain part should match the `service_url` field
- `http_server_port` is the post on which the HTTP server will be listening, and
  the same port needs to be configured in your OTEL exporter
- `server_bind_host` (OPTIONAL) is the host to which the server is supposed to bind
  - `127.0.0.1` is the default, implicit value and recommended for most use-cases
  - In a Docker environment, the loopback interface may be insufficient for
    cross-container communication. In such cases, the field can be set to
    `0.0.0.0` to bind the server to all available network interfaces. Please
    note that this may have an undesirable effect and leave the device
    vulnerable if used outside of a container, on a publicly exposed device.
  - For IPv6 networking, it is recommended to wrap the host in square brackets.
    For loopback, `[::1]` can be used. To bind the server to all available
    IPv6-enabled network interfaces (e.g. in a Docker environment), `[::0]` can
    be used, with the same security caveat as above.

## Obtaining binaries

You can [download](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotad) iotad for your architecture and environment. Please be aware that the latest version of `iotad` is compatible only with the Speedway environment. For Dev and Pre-prod environments, you should continue using the legacy versions of `iotad`.

  | Cluster               | Mac (x86-64)         | Mac (Arm64)          | Windows (x86-64)     | Linux (x86-64)       | Linux (Arm64)        |
  |-----------------------|----------------------|----------------------|----------------------|----------------------|----------------------|
  | [Speedway](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotad/v1/) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/v1/iotad_v1_release_darwin_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/v1/iotad_v1_release_darwin_arm64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/v1/iotad_v1_release_windows_amd64-latest.exe) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/v1/iotad_v1_release_linux_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/v1/iotad_v1_release_linux_arm64-latest) |
  | [Dev and Pre-prod](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotad/) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/iotad_release_darwin_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/iotad_release_darwin_arm64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/iotad_release_windows_amd64-latest.exe) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/iotad_release_linux_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotad/iotad_release_linux_arm64-latest) |

Please note that the binaries listed in [Artifactory](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotad) are sorted alphabetically.

Alternatively, you can build the binaries yourself using Bazel by simply running the following command in the repository.

```sh
bazel build //ns/iot/iota/cmd/iotad
```

You will then be able to find the built binary file at `./bazel-bin/ns/iot/iota/cmd/iotad/iotad_/iotad`.

If you need to cross-compile for a platform different from your host machine, you can easily use Bazel as well. For example, for the `linux_amd64` target platform, you can use the following command.

```sh
bazel build //ns/iot/iota/cmd/iotad --platforms @io_bazel_rules_go//go/toolchain:linux_amd64
```

More information on this topic can be found in [the official Bazel cross-compilation documentation for Golang](https://github.com/bazelbuild/rules_go/blob/master/docs/go/core/cross_compilation.md#cross-compilation).

## Notes

It is possible to override the path prefix (`$HOME` by default) using the `IOTA_BASE_PATH` environment variable. However, for support purposes, this is not recommended, and keeping the default value is preferred. Please note that this environment variable will also apply to the path of the configuration file (`$HOME/.iota/iotad.json` by default).

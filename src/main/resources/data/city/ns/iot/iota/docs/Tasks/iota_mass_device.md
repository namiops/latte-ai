# IoTA Mass Device Creation and Provisioning

This document outlines the process of creating and provisioning IoT devices via
IoTA at a large scale. It outlines the general procedures, alternative
approaches, and potential pitfalls and how to avoid them.

## Prerequisites

All binary executables provided to IoTA users require a host system with Linux,
MacOS or Windows. The supported platforms are x86, AMD64, ARMv5+ and ARM64
(including Apple's M-series chips).

In order to create and provision devices via IoTA at a large scale, the steps
recommended in this document will require the following:

- A development host device capable of running [the `iotactl` CLI tool](./iotactl.md).
- Any number of IoT devices capable of running [the IoTA Daemon (IoTAD)](./iotad.md).

The "development host device" refers to a device used by the developers (e.g., a
laptop) used for the development process, not the actual IoT devices. Such
devices will always be explicitly referred to as "IoT device" to clearly
differentiate between them.

Before starting, the following steps are required:

1. To get basic understanding of IoTA, please check [the IoTA index document](../index.md)
2. Have access to your IoTA tenant (see step 1)
3. Download `iotactl`, make sure it runs on the development host device (see step 1), and configure it (see [the `iotactl` document](./iotactl.md))
4. Download the appropriate IoTAD binary for your IoT device platform (see [the IoTAD document](./iotad.md))

The steps in this document will be using `iotactl` and IoTAD to abstract some
complexity and provide a more future-proof flow in case of potential changes to
the raw API. However, if preferred, it is possible to access the IoTA API
directly (see [the IoTA index document](../index.md)).

## Group Creation

Using `iotactl`, you can create a new device group simply using the following
command.

```sh
iotactl add group <Group Name>
```

The name of a group can contain lowercase ASCII letters and number, with words
separated by dashes (`-`), e.g. `room2-lights`.

Please note that the `Provisioning Secret` printed by this command and store it
security. This value is later required for individual device provisioning.

## Device Creation

A single device can be created using the following `iotactl` command:

```sh
iotactl add device -g <Group Name> <Device Name>
```

Similar to the group name, the device name follows the same naming convention,
e.g. `test-device-123`.

In order to create multiple devices at once, multiple approaches are possible
based on the users' specific device manufacturing flow. The `iotactl add device`
command technically always creates a single device in the IoTA system, and there
is no supported API for creating multiple devices at once at the moment.
However, using a simple shell scrip, it is possible to adjust the device
creation process to the users' requirements.

Each device needs to be individually provisioned after being created. The
provisioning process is further described in the section below.

### Create multiple devices with unknown names

If you want to create a number of devices which do not have a specific name
ahead of time, you can use the following shell script. The created devices will
be numbered from 1 to `device_count` and these numbers will be appended to the
`device_prefix` (`test-device-1`, `test-device-2`, ..., `test-device-100`).

The group provision secret will be stored in the file specified by
`prov_secret_file`, unless you remove the `iotactl add group` command, and the
names on the individual devices create will be stored in the file specified by
`device_name_file`, one device name per line.

This flow assumes that the IoTA device creation process is performed before the
device manufacturing process, or at least before the step when configuration
files are distributed to the manufactured devices.

```sh
#!/bin/sh

device_count=100
device_group='<Group Name>'
device_prefix='test-device-'
device_name_file='./device_names.txt'
prov_secret_file='./prov_secret.txt'

set -e

# Device group creation (skip if group is already created).
# Provision secret is stored in the specified file.
iotactl add group "${device_group}" > "${prov_secret_file}"

j=1
while [ $j -le $device_count ]; do
        device_name="${device_prefix}${j}"
        # Individual device creation in the specified device group.
        # Device name is appended to the specified file.
        iotactl add device -g "${device_group}" "${device_name}"
        echo "${device_name}" >> "${device_name_file}"
        j=$(( j + 1 ))
done
```

### Create multiple devices with known names

This flow assumes that each device already has a specific name assigned to it,
and that the IoTA device creation happens after the manufacturing process, or at
a point when the names have already been assigned to the individual devices.

Instead of generating a list of numbered device names, it reads an existing list
of device names and creates matching devices in IoTA. The file must contain one
device name per line, just as described in the section above, and should contain
a final newline.

```sh
#!/bin/sh

device_group='<Group Name>'
device_name_file='./device_names.txt'
prov_secret_file='./prov_secret.txt'

set -e

# Device group creation (skip if group is already created).
# Provision secret is stored in the specified file.
iotactl add group "${device_group}" > "${prov_secret_file}"

grep '.' "${device_name_file}" | while IFS= read -r DEVICE_NAME; do
    # Individual device creation in the specified device group.
    iotactl add device -g "${device_group}" "${DEVICE_NAME}"
done
```

## Device provisioning

The recommended method of provisioning devices and ensuring that each device
maintains a valid certificate is to use [the IoTA Daemon (IoTAD)](./iotad.md).

This requires distributing the `iotad` binary file appropriate for the target
platform onto each IoT device, along with a configuration file specific to that
device.

The IoTAD document already explains its configuration in detail, so please check
the information provided there.

There is currently no time limit to the validity of the provision secret
(generated during the device group creation step), so it is possible for an
arbitrarily long time to pass between the creation of the group, creation of the
device and the device provisioning steps.

### Extra considerations for device provisioning

Alternatively, the `iotactl` utility is also capable of performing the device
provisioning step via the `iotactl provision` command. However, this is
generally not recommended because it requires either running `iotactl` on the
IoT device (`iotactl` is only supposed to run on the development host device,
not IoT devices) or transferring sensitive device credentials to the individual
IoT devices from the development host device. However, if you cannot use IoTAD
on the target IoT device for any reason, then this option is available.

While the provision secret has no time limit, the certificate created during the
device provisioning process is limited in validity and will expire if not
periodically refreshed. By using IoTAD, this process is completely automated,
and as long as the device is running in the configured certificate refresh
window, the user never has to worry about the certificate expiring.

The IoTA team currently does not provide any officially supported method of
generating IoTAD configuration for a specific IoTA device and its delivery onto
the target IoT device. Due to vastly varying device manufacturing flows, it is
impossible to provide a universal solution to this task, and each user has to
find a strategy appropriate to their circumstances. In general, we assume that
configuration files can be deployed to the target IoT devices via SSH, SFTP or a
similar data transfer protocol after the physical manufacturing process.

# AC Management Service Use Case Sequences

## Table of Contents <!-- omit in toc -->

- [AC Management Service Use Case Sequences](#ac-management-service-use-case-sequences)
  - [Overview Architecture](#overview-architecture)
  - [Glossary](#glossary)
  - [Scenarios](#scenarios)
    - [Get current device status](#get-current-device-status)
      - [Get single device shadow](#get-single-device-shadow)
      - [Get all device shadow by OTA group](#get-all-device-shadow-by-ota-group)
    - [**\[OLD\]** Apply OTA to devices](#old-apply-ota-to-devices)
      - [Upload application files for devices](#upload-application-files-for-devices)
      - [Apply OTA to a single device](#apply-ota-to-a-single-device)
      - [Apply OTA to devices in the same OTA group](#apply-ota-to-devices-in-the-same-ota-group)
    - [Detailed sequences on device side](#detailed-sequences-on-device-side)
      - [OTA process on a device](#ota-process-on-a-device)
        - [Apply scheduled application update](#apply-scheduled-application-update)
      - [Device rebooting](#device-rebooting)
    - [Send device log](#send-device-log)
    - [Send device command](#send-device-command)
    - [Share gate state among devices installed at the same Site Gate](#share-gate-state-among-devices-installed-at-the-same-site-gate)
    - [Send device event / device state](#send-device-event--device-state)

This document describes the main use case scenarios of AC Management Service with their sequences.

## Overview Architecture

See the figure in [the confluence link](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=447045438).

## Glossary

| Word       | Description                                                                                                                                                                                              |
| ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ACMS       | An abbreviation of Access Control Management Service.                                                                                                                                                    |
| API Server | An API server in ACMS.                                                                                                                                                                                   |
| Worker     | A worker server in ACMS. This server handles MQTT messages sent from devices.                                                                                                                            |
| Storage    | An object storage in ACMS.                                                                                                                                                                               |
| RabbitMQ   | A message broker in IoTA. ACMS also exchanges messages with devices through the broker. RabbitMQ converts from MQTT messages (device <=> RabbitMQ) to AMQP messages (RabbitMQ <=> ACMS), and vise versa. |

## Scenarios

This section describes the main use cases with their sequences.

In each sequence, the prefix `id:` indicates the operation ID defined in our document.
To check the detail of each operation, see [management_http.yaml](../api/management_http.yaml) for HTTP APIs, or [management_mqtt.yaml](../api/management_mqtt.yaml) for MQTT topics.

### Get current device status

#### Get single device shadow

ACMS retrieves the device shadow from IoTA.

![get single shadow](image/get_single_shadow.png)

#### Get all device shadow by OTA group

ACMS retrieves device shadows from IoTA.
Currently IoTA has no API to retrieve multiple device shadows at once. However, the API will be implemented in the near future.

![get all shadow by group](image/get_all_shadow_by_group.png)

### **[OLD]** Apply OTA to devices

In OTA application process, admin users can update devices' application software and some other settings.

#### Upload application files for devices

AC Management Service will PUT the files in the request body to the object storage.
The same applies to other file operations (GET, DELETE).

![upload application files](image/upload_application_files.png)

#### Apply OTA to a single device

Frontend calls the API to retrieve the current state. An admin user will edit the desired state based on it.
After submission by an admin user, API Server will update the desired state stored in IoTA. After the update, IoTA Device Shadow feature will notify changes to the target device asynchronously.

To see the detailed behavior of a device (sequence No.11), refer to [the section](#detailed-sequences-on-device-side).
This sequence is based on [the document about IoTA Device Shadow](https://developer.woven-city.toyota/docs/default/Component/iota-service/en/shadow/). Please also refer to it for a deeper understanding.

![update shadow](image/update_shadow.png)

#### Apply OTA to devices in the same OTA group

An admin user will edit the desired state based on a template.
The remaining application process is almost the same as the one to a single device.
![update shadow by group](image/update_shadow_by_group.png)

### Detailed sequences on device side

This section describes a couple of sequences focused on device side.

#### OTA process on a device

- If the message contains no updates to be applied to it, the device will just ignore the message.
- If the message contains any updates to be applied, the device will transit to **OTA mode** (naming is TBD), where any additional delta message will be ignored.

You can see the detailed reboot sequence in [the section](#device-rebooting).

![device update behavior](image/device_update_behavior.png)

##### Apply scheduled application update

The delta message can include the property `configuration.application.{appname}.canStartFrom`, which specifies the date when the device should start applying the update.
ACMS assumes that the device periodically publishes a **reported** message to IoTA Device Shadow and periodically receives a delta messages as a response.
Under this assumption, scheduled update is accomplished by the device checking the date each time a delta message is received.

#### Device rebooting

Before and after rebooting, devices sends the reported message to notify rebooting.
While rebooting process, any delta messages will be ignored.

![device rebooting](image/device_rebooting.png)

### Send device log

A device should publish a device log message to a dedicated topic. The worker will convert the format and put it to stdout. If the message is error one, the worker will send it to other component for notification.

![send log](image/send_log.png)

### Send device command

- Admin users can refer to the available commands of a device from a property `state.availableCommands` in its `reported` state.
- A device must send logs when a command is received or completed.

![device command](image/device_command.png)

There are two types of device commands.

1. reboot
2. manual door operations

The reboot process follows the sequence shown in [this section](#device-rebooting).
No specific sequence is defined for manual door operations. However, a device must publish a reported message when a reported state is changed by the command.

### Share gate state among devices installed at the same Site Gate

Devices obtain the dedicated topic from ACBE first, and then devices share the gate state by publishing / subscribing messages on the topic.
A leader device observes the gate state and publishes messages. On the other hand, follower devices only subscribe the topic and receive state updates.

![gate state sharing](image/gate_state_sharing.png)

### Send device event / device state

A device send events and state changes for itself and the installed door/elevator to a dedicated topic, respectively.

See [the figma](https://www.figma.com/board/hi5kj3nQIt1e3gd6IxP969/Door%2FDevice-State%2FEvent-%E3%81%AE-UI-%E3%81%B8%E3%81%AE%E9%80%9A%E7%9F%A5?node-id=0-1&t=XEiq3W16acUwVKeF-0) for details.

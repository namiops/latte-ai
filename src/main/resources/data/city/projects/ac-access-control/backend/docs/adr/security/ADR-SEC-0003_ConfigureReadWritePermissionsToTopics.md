# ADR-SEC-0003 Configure Read/Write Permissions to AMQP/MQTT Topics

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2024-08-22   |

## Context and Problem Statement

- Consider treatments to mitigate a threat described in [TH.FSS.ACS.021 - [125] Thread Model - WTC-FSS-ACS -Access Control](https://docs.google.com/spreadsheets/d/1T2oD2nE8UWyPfqrLnBf40AuN2spWQxQv70n-ga_7VZg/edit#gid=0&range=29:29)

- `TH.FSS.ACS.021` states...
  - Impersonation of a legitimate publisher asset due to improper configuration of a shared topic/queue
  - Unauthorized publishing of data to a topic/queue due to overly permissive access control on the broker
  - Example: SiteGate device A impersonates device B or the management service to inject fake information or issue device commands (e.g. emergency unlock)

### Given Conditions

- Use IoTA service to provision devices.
- Use mTLS on all communication paths.
- The Access Control backend knows which devices are valid with its device name.
- RabbitMq provides a feature which sets permissions that which source can pub/sub witch topics/queue.
- Access Control system will have about 700 devices when the city opens.

### Scope

- MQTT communication between devices and the broker
- AMQP communication between the backend and the broker

### Permissions to Topic/Queue

- `{deviceName}` must be unique among all groups in the tenant - ac-access-control-host.
- `{legitimate device}` is the device which has the name `{deviceName}`.
- One or two devices are bound to one `{doorId}`. These relationships are managed by A/C backend.
- Devices and doors will be registered via [A/C backend management API](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_http.yaml) during a setup phase.

| No  | Topic                                                                                          | can Publish from                                 | can Subscribe by                                           |
| --- | ---------------------------------------------------------------------------------------------- | ------------------------------------------------ | ---------------------------------------------------------- |
| 1   | `{groupName}`/`{deviceName}`/shadow/delta                                                      | - (*1)                                           | `{legitimate device}`                                      |
| 2   | `{groupName}`/`{deviceName}`/shadow/reported                                                   | `{legitimate device}`                            | - (*2)                                                     |
| 3   | `{prefix}`/device/`{deviceName}`/cmd                                                           | Backend                                          | `{legitimate device}`                                      |
| 4   | `{prefix}`/be/cmd/get-door/`{deviceName}`<br/>`{prefix}`/be/cmd/get-elv/`{deviceName}`         | `{legitimate device}`                            | Backend                                                    |
| 5   | `{prefix}`/be/cmd/get-door/`{deviceName}`/res<br/>`{prefix}`/be/cmd/get-elv/`{deviceName}`/res | Backend                                          | `{legitimate device}`                                      |
| 6   | `{prefix}`/device/`{deviceName}`/event                                                         | `{legitimate device}`                            | Backend                                                    |
| 7   | `{prefix}`/device/`{deviceName}`/allow-elv-flr-access                                          | Backend                                          | `{legitimate device}`                                      |
| 8   | `{prefix}`/device/`{deviceName}`/notify-alert                                                  | Backend                                          | `{legitimate device}`                                      |
| 9   | `{prefix}`/door/`{doorId}`/event<br/>`{prefix}`/elv/`{elevatorId}`/event                       | `{legitimate device}`s  bound to door:`{doorId}` | Backend                                                    |
| 10  | `{prefix}`/door/`{doorId}`/state                                                               | `{legitimate device}`s bound to door:`{doorId}`  | Backend<br>`{legitimate device}`s bound to door:`{doorId}` |

(*1) : /delta is published from IoTA service components, not from A/C components  
(*2) : /reported is subscribed by IoTA service components, not by A/C components

## Approach (not approved yet)

Use [IoTA Broker Permission](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/broker_permissions/) feature(released on June 28th, 2024). 

- It's possible to set topic permissions of publishing from/subscribed by devices about **No.1-8**. 
- It's **NOT** possible to set permissions of **No.9-10**, because IoTA system does not know about `{doorID}` and per device permission is not supported currently(July 26th).

### Permission of No.1-8

See [Permission Config Format](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/broker_permissions/#permissions-configuration-format) for json:read/write, json:topic path.

| No  | Topic                                             | json:read/write | json:topic path                           |
| --- | ------------------------------------------------- | --------------- | ----------------------------------------- |
| 1   | {groupName}/{deviceName}/shadow/delta             | read            | {group}/{device}/#                        |
| 2   | {groupName}/{deviceName}/shadow/reported          | write           | {group}/{device}/#                        |
| 3   | {prefix}/device/{deviceName}/cmd                  | read            | prod/device/{device}/cmd                  |
| 4-1 | {prefix}/be/cmd/get-door/{deviceName}             | write           | prod/be/cmd/get-door/{device}             |
| 4-2 | {prefix}/be/cmd/get-elv/{deviceName}              | write           | prod/be/cmd/get-elv/{device}              |
| 5-1 | {prefix}/be/cmd/get-door/{deviceName}/res         | read            | prod/be/cmd/get-door/{device}/res         |
| 5-2 | {prefix}/be/cmd/get-elv/{deviceName}/res          | read            | prod/be/cmd/get-elv/{device}/res          |
| 6   | {prefix}/device/{deviceName}/event                | write           | prod/device/{device}/event                |
| 7   | {prefix}/device/{deviceName}/allow-elv-flr-access | read            | prod/device/{device}/allow-elv-flr-access |
| 8   | {prefix}/device/{deviceName}/notify-alert         | read            | prod/device/{device}/notify-alert         |

### Permission of No.9-10

| No  | Topic                               | can Publish from                                     | can Subscribe by                                           |
| --- | ----------------------------------- | ---------------------------------------------------- | ---------------------------------------------------------- |
| 9-1 | `{prefix}`/door/`{doorId}`/event    | `{legitimate device}`s  bound to door:`{doorId}`     | Backend                                                    |
| 9-2 | `{prefix}`/elv/`{elevatorId}`/event | `{legitimate device}`s  bound to door:`{elevatorId}` | Backend                                                    |
| 10  | `{prefix}`/door/`{doorId}`/state    | `{legitimate device}`s bound to door:`{doorId}`      | Backend<br>`{legitimate device}`s bound to door:`{doorId}` |

In order to implement topic permissions of No.9-10, it's reasonable to **validate door-device relationships on the backend** because..

- The backend manages that relationships.
- The relationships are specific for Access Control System.
  
To validate door-device relationships on the backend, backend requires to know which device is published a message. But it seems not possible to device name of message publisher on the backend with current system structure (discussed [in this Slack thread](https://woven-by-toyota.slack.com/archives/C042AQ2TU4A/p1721889637662019?thread_ts=1721882286.996549&cid=C042AQ2TU4A)).

To achieve implementing No.9-10 permissions, the following changes are required.

#### Changes for Implement Topic Permissions of No.9-10

- Change topic path - Add `{deviceName}` to each topic.
    - With setting permissions including {device} blocks that a malicious device pretends a legitimate device.
    - **Impact**: Requires implementation changes of the Backend, Authenticator, NFC Controller and Elevator Access Controller.

- Validate door-device relationships on the backend.
    - It blocks a device publishes event or state to unrelated doors.
    - **Impact**: Requires implementation changes of the Backend.

- Transfer door state messages to the another devices of a door on the backend.
    - Door state is used for state sharing among devices belonging the same door.
    - To avoid infinite pub/sub loop in the backend, split topic No.10 into two topics. Each topic is read-only or write-only, respectively.
    - eg. where 'device-enter' and 'device-exit' are on 'door1'.
        - When the backend receives a message published from 'device-enter' on a topic '{prefix}/door/door1/state/device-enter', the backend publishes the received message to '{prefix}/door/door1/state/device-exit/shared'.
    - **Impact**: Requires implementation changes of the Backend.

| No   | Topic After Change                                 | Topic Before Change             | json:read/write | json:topic path                       |
| ---- | -------------------------------------------------- | ------------------------------- | --------------- | ------------------------------------- |
| 9-1  | {prefix}/door/{doorId}/event/`{deviceName}`        | {prefix}/door/{doorId}/event    | write           | {prefix}/door/+/event/{device}        |
| 9-2  | {prefix}/elv/{elevatorId}/event/`{deviceName}`     | {prefix}/elv/{elevatorId}/event | write           | {prefix}/elv/+/event/{device}         |
| 10-1 | {prefix}/door/{doorId}/state/`{deviceName}`        | {prefix}/door/{doorId}/state    | write           | {prefix}/door/+/state/{device}        |
| 10-2 | {prefix}/door/{doorId}/state/`{deviceName}`/shared | {prefix}/door/{doorId}/state    | read            | {prefix}/door/+/state/{device}/shared |
---

## Decision Outcome

Apply the approach above 

- Uses IoTA Broker Permission feature to restrict MQTT topics pub from/sub by devices. (No.1-8)
- Adds implementation for MQTT topics related {doorId}. (No.9-10)
- No restriction on the backend side.
- You can see review meeting minutes [here](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=952803655).

### Reason

- For MQTT topics related with {deviceName} (No.1-7), using IoTA Broker Permission feature is easy to configure and no implement is required on A/C side.
- For MQTT topics related with {doorId} (No.8-9), since IoTA does not know about {doorId}-{deviceName} relationships, it's reasonable to validate such relationships on the application level.

---

## Note

- 2024-08-22 : Add topic No.8 {prefix}/device/{deviceName}/notify-alert : Kohta Natori
- 2024-08-20 : Add topics for publishing events of elevators : Kohta Natori
- 2024-08-14 : Split topic No.9 into two topics : Hajime Miyazawa
- 2024-07-29 : Review meeting([minutes](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=952803655)), Added Decision Outcome and Reason : Kohta Natori
- 2024-07-26 : Added Approach : Kohta Natori
- 2024-02-13 : Drafted, Originator: Kohta Natori

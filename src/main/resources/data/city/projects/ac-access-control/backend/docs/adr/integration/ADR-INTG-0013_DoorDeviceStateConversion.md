# ADR-INTG-0013 Door/Device State Conversion

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2024-10-15   |

## Context

- This document states about Door State and Device State conversion on notification to [ac-management-ui](https://github.com/wp-wcm/city/tree/main/projects/ac-access-control/frontend/ac-management-ui).
- On notification, the annotation and conversion are required on the backend to provide additional information to ac-management-ui's users (security guards).
- This document defines how to annotate and convert states from door and device to states for the frontend.
- You can see an overview and discussions about the notification on [this Figma diagram](https://www.figma.com/board/hi5kj3nQIt1e3gd6IxP969/Door%2FDevice-State%2FEvent-%E3%81%AE-UI-%E3%81%B8%E3%81%AE%E9%80%9A%E7%9F%A5?node-id=16-1111&t=3kspmvGaPRxyytxg-1).

### References

- [ADR-INTG-0002 Door/Device States and Events Notification](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0002_DoorDeviceStateEventNotification.md)
- [ADR-CTRL-0001 Door Device State Event Notification](https://github.tri-ad.tech/R-D-WCM/ac-edge-controller-app/blob/main/docs/adr/ADR-CTRL-0001_DoorDeviceStateEventNotification.md)
- [ADR-AUTHAPP-0001 Door/Device States and Events Notification](https://github.tri-ad.tech/R-D-WCM/ac-authenticator-app/blob/main/docs/adr/ADR-AUTHAPP-0001_DoorDeviceStateEventNotification.md)
- [Overview of notifications(Figma)](https://www.figma.com/board/hi5kj3nQIt1e3gd6IxP969/Door%2FDevice-State%2FEvent-%E3%81%AE-UI-%E3%81%B8%E3%81%AE%E9%80%9A%E7%9F%A5?node-id=16-1111&t=3kspmvGaPRxyytxg-1)
- [management_mqtt.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_mqtt.yaml)
- [Woven City Physical Security Policy Standard Ver.1.0.0](https://drive.google.com/file/d/1K5LPWivpjZMUop-beFt4mRyeHfMrvuL1/view)
- [ADR-INTG-0013/0014 - Door/Device State Event Notification](https://docs.google.com/spreadsheets/d/1nh1LBT5XBVqSFwd3JTMcqBh_r5cymIvcQimnlRBa3YQ/edit?gid=0#gid=0)

---

## Decision Outcome

### State Code Assignment

Assigns unique code to each door/device state. The code is given on the edge(device) side or the backend. The backend identified state with this code.

#### Format

    S_{type}_{groupNo}{sequenceNo}

    eg.
     S_OP_0101
     S_EQ_0001
     S_LC_0001

    S_ : is a fixed part. represents State.

    {type} : represents State type. Upper-case 2 characters. One of the followings.
      OP : Operating State
      EQ : Equipment State
      LC : Local State
      DE : Device State

    {groupNo} : represents ota-group(door state) or device type(device state). 2 digit decimal number, starts from 00.
      00 : [door state] gate-nfc
      01 : [door state] public-door-auth
      02 : [door state] auto-door-auth
      20 : [device state] Authenticator/NFC Controller
      30 : [device state] (detected on the backend)

    {sequenceNo} : represents sequence number of each states in ota-group. 2 digit decimal number, starts from 01. It should be unique in each S_{type}_{groupNo}.

### Door State

Door State are retrieved on the topic  `{prefix}/door/{doorId}/state/{deviceName}` (Devices -> Backend) or `{prefix}/door/{doorId}/state/{deviceName}/shared` (Backend -> Devices). See [management_mqtt.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_mqtt.yaml) for details.

#### Attributes Stored for Each State Code on the Backend (Door State)

| No  | Attribute    | Description                                                                                                                          |
| --- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | Level        | Level represents severity of state, one of `normal`, `warning` and `error`. It corresponds to color of notification on the frontend. |
| 2   | Type         | Type represents a type of state. Type should be one of "Operating", "Equipment" and "Local".                                         |
| 3   | StateName_JP | StateName_JP represents Japanese notation of states. It is requires since main users of ac-management-ui will be Japanese.           |
| 4   | Dismiss      | Dismiss represents whether notification to the frontend is dismissed or not. Dismiss takes a boolean value.                          |

#### Door State List

| No  | Code          | StateName                                              | StateName JP                          | Type          | Level  | Dismiss  | gate-nfc | gate-auth | public-door-auth | auto-door-auth | private-home-auth | elevator-auth | elevator-elv |
| --- | ------------- | ------------------------------------------------------ | ------------------------------------- | ------------- | ------ | -------- | -------- | --------- | ---------------- | -------------- | ----------------- | ------------- | ------------ |
| 1   | S_OP_0001     | keepOpening                                            | ゲート開放中                          | Operating     | warn   | FALSE    | x        |           |                  |                |                   |               |              |
| 2   | S_OP_0002     | keepClosing                                            | ゲート通行禁止中                      | Operating     | warn   | FALSE    | x        |           |                  |                |                   |               |              |
| 3   | S_EQ_0001     | illegalEntry                                           | 不正通行                              | Equipment     | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 4   | S_EQ_0002     | wrongDirectionEntry                                    | 逆進入                                | Equipment     | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 5   | S_EQ_0003     | stayTooLong                                            | 滞留                                  | Equipment     | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 6   | S_EQ_0004     | tailGating                                             | 共連れ                                | Equipment     | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 7   | S_EQ_0005     | gateEquipmentError                                     | ゲート機器異常                        | Equipment     | -      | FALSE    | x        |           |                  |                |                   |               |              |
| 8   | S_EQ_0006     | gateControllerDown                                     | ゲートコントローラ―異常               | Equipment     | -      | FALSE    | x        |           |                  |                |                   |               |              |
| 9   | S_EQ_0007     | fireAlarm                                              | 火報検知                              | Equipment     | -      | FALSE    | x        |           |                  |                |                   |               |              |
| 10  | S_EQ_0008     | flapperOpened                                          | ゲートフラッパー開                    | Equipment     | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 11  | S_EQ_0009     | keepOpeningButtonPressed                               | ゲート開放ボタンON                    | Equipment     | -      | FALSE    | x        |           |                  |                |                   |               |              |
| 12  | ~~S_EQ_0010~~ | ~~(REMOVED)keepClosingButtonPressed~~ (*1)             | ~~(除外済み)ゲート通行禁止ボタンOFF~~ | ~~Equipment~~ | -      | ~~TRUE~~ | x        |           |                  |                |                   |               |              |
| 13  | S_LC_0001     | nfcAuthenticationError_enter                           | -                                     | Local         | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 14  | S_LC_0002     | nfcAuthenticationError_exit                            | -                                     | Local         | -      | `TRUE`   | x        |           |                  |                |                   |               |              |
| 13  | S_OP_0101     | fireAlarm                                              | 火報信号検知中                        | Operating     | error  | FALSE    |          |           | x                |                |                   |               |              |
| 14  | S_OP_0102     | openedTooLong                                          | 扉開タイムアウト                      | Operating     | warn   | FALSE    |          |           | x                |                |                   |               |              |
| 15  | S_OP_0103     | cannotUnlock                                           | 解錠異常                              | Operating     | error  | FALSE    |          |           | x                |                |                   |               |              |
| 16  | S_OP_0104     | cannotLock                                             | 施錠異常                              | Operating     | error  | FALSE    |          |           | x                |                |                   |               |              |
| 17  | S_OP_0105     | forcedOpen                                             | こじ開け                              | Operating     | error  | FALSE    |          |           | x                |                |                   |               |              |
| 18  | S_OP_0106     | electricLockWiringError                                | 電気錠異常                            | Operating     | error  | FALSE    |          |           | x                |                |                   |               |              |
| 19  | S_OP_0107     | keepUnlocked                                           | リモート解錠中                        | Operating     | warn   | FALSE    |          |           | x                |                |                   |               |              |
| 20  | S_OP_0108     | authDisabled                                           | 閉め切り中(認証無効)                  | Operating     | warn   | FALSE    |          |           | x                |                |                   |               |              |
| 21  | S_OP_0109     | oneShotUnlock                                          | リモート解錠(1回)                     | Operating     | warn   | FALSE    |          |           | x                |                |                   |               |              |
| 22  | S_EQ_0101     | doorClosed                                             | ドア閉                                | Equipment     | -      | FALSE    |          |           | x                |                |                   |               |              |
| 23  | S_EQ_0102     | doorUnlocked                                           | ドア解錠                              | Equipment     | -      | `TRUE`   |          |           | x                |                |                   |               |              |
| 24  | S_EQ_0103     | doorLocked                                             | ドア施錠                              | Equipment     | -      | FALSE    |          |           | x                |                |                   |               |              |
| 25  | S_LC_0101     | outUnlock_enter                                        | -                                     | Local         | -      | `TRUE`   |          |           | x                |                |                   |               |              |
| 26  | S_LC_0102     | outUnlock_exit                                         | -                                     | Local         | -      | `TRUE`   |          |           | x                |                |                   |               |              |
| 27  | S_OP_0201     | keepOpened                                             | リモート開放中                        | Operating     | warn   | FALSE    |          |           |                  | x              |                   |               |              |
| 28  | S_OP_0202     | authDisabled                                           | 自由通行中(認証無効)                  | Operating     | warn   | FALSE    |          |           |                  | x              |                   |               |              |
| 29  | -             | normal<br>(いずれの OperatingState も ON ではない場合) | 正常動作中                            | Operating     | normal | FALSE    | x        | x         | x                | x              | x                 | x             | x            |

You can use [ADR-INTG-0013/0014 - Door/Device State Event Notification](https://docs.google.com/spreadsheets/d/1nh1LBT5XBVqSFwd3JTMcqBh_r5cymIvcQimnlRBa3YQ/edit?gid=0#gid=0) when you update the list. But update this ADR at the same time.

*1: There is no Keep Closing Button on gates. Therefore `keepClosingButtonPressed` is removed.

---

### Device State

Device State are retrieved on the topic  `{groupName}/{deviceName}/shadow/reported`. See [management_mqtt.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_mqtt.yaml) for details.

#### Attributes Stored for Each State Code on the Backend (Device State)

| No  | Attribute    | Description                                                                                                                          |
| --- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | Level        | Level represents severity of state, one of `normal`, `warning` and `error`. It corresponds to color of notification on the frontend. |
| 2   | StateName_JP | StateName_JP represents Japanese notation of states. It is requires since main users of ac-management-ui will be Japanese.           |
| 3   | Dismiss      | Dismiss represents whether notification to the frontend is dismissed or not. Dismiss takes a boolean value.                          |

#### Device State List

| No  | Code      | Source         | StateName     | StateName JP | Level  | Dismiss |
| --- | --------- | -------------- | ------------- | ------------ | ------ | ------- |
| 1   | S_DE_2001 | reported state | normally      | 正常         | normal | FALSE   |
| 2   | S_DE_2002 | reported state | rebooting     | 再起動中     | warn   | FALSE   |
| 3   | S_DE_2003 | reported state | error         | エラー       | error  | FALSE   |
| 4   | S_DE_3001 | backend        | deviceOffline | 通信異常     | error  | FALSE   |

You can use [ADR-INTG-0013/0014 - Door/Device State Event Notification](https://docs.google.com/spreadsheets/d/1nh1LBT5XBVqSFwd3JTMcqBh_r5cymIvcQimnlRBa3YQ/edit?gid=0#gid=0) when you update the list. But update this ADR at the same time.

---

## Note

- 2024-10-15 : Remove S_EQ_0010: keepClosingButtonPressed.
- 2024-08-14 : Add description on `{prefix}/door/{doorId}/state/{deviceName}/shared`
- 2024-07-30 : Change topic name: `{prefix}/door/{doorId}/state` -> `{prefix}/door/{doorId}/state/{deviceName}`
- 2024-07-19 : Fix code of nfcAuthenticationError_exit
- 2024-07-03 : Fix code of Device State List
- 2024-07-02 : Add LOCAL door events of gate-nfc
- 2024-07-02 : Approved
- 2024-07-01 : Drafted, Originator: Kohta Natori

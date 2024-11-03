# ADR-INTG-0014 Door/Device Event Conversion

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2024-10-15   |

## Context

- This document states about Door Event and Device Event conversion on notification to [ac-management-ui](https://github.com/wp-wcm/city/tree/main/projects/ac-access-control/frontend/ac-management-ui).
- On notification, the annotation and conversion are required on the backend to provide additional information to ac-management-ui's users (security guards).
- This document defines how to annotate and convert events from door and device to events for the frontend.
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

### Conversion Key on the Backend (Door Event/Device Event)

Events from doors/devices contains `code`(see next section) and `sort`(detected/resolved). A combination of `code` and `sort` can identify an event.  

### Event Code Assignment

Assigns unique code to each door/device event. The code is given on the edge(device) side. The backend identified conversion info with this `code` and `sort`.

#### Format

    E_{groupNo}{sequenceNo}

    eg.
     E_0101

    E_ : is a fixed part. represents Event.

    {groupNo} : represents ota-group(door event) or device type(device event). 2 digit decimal number, starts from 00.
      00 : [door event] gate-nfc
      01 : [door event] public-door-auth
      02 : [door event] auto-door-auth
      03 : [door event] gate-auth
      04 : [door event] private-home-auth
      05 : [door event] elevator-auth
      10 : [door event] (detected on the backend)
      20 : [device event] Authenticator
      30 : [device event] NFC Controller/Elevator Access Controller
      40 : [device event] (detected on the backend)

    {sequenceNo} : represents sequence number of each states in ota-group. 2 digit decimal number, starts from 01. It should be unique in each E_{groupNo}.

### Attributes Stored for Each Event on the Backend (Door Event/Device Event)

| No  | Attribute    | Description                                                                                                                          |
| --- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | Level        | Level represents severity of state, one of `normal`, `warning` and `error`. It corresponds to color of notification on the frontend. |
| 2   | StateName_JP | StateName_JP represents Japanese notation of states. It is requires since main users of ac-management-ui will be Japanese.           |
| 3   | Dismiss      | Dismiss represents whether notification to the frontend is dismissed or not. Dismiss takes a boolean value.                          |

### Door Event

Door Events are retrieved on the topic `{prefix}/door/{doorId}/event/{deviceName}` on configurations except `elevator-auth`, `{prefix}/elv/{elevatorId}/event/{deviceName}` on `elevator-auth`.  
See [management_mqtt.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_mqtt.yaml) for details.

#### Door Event List

| No  | Code       | Sort         | StateName                                 | StateName JP                           | Level      | Dismiss  | IsPersistent | gate-nfc | public-door-auth | auto-door-auth | gate-auth | private-home-auth | elevator-auth | elevator-elv | backend |
| --- | ---------- | ------------ | ----------------------------------------- | -------------------------------------- | ---------- | -------- | ------------ | -------- | ---------------- | -------------- | --------- | ----------------- | ------------- | ------------ | ------- |
| 1   | E_0001     | detected     | illegalEntry                              | ゲート不正通行                         | warn       | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 2   | E_0001     | resolved     | illegalEntry                              | ゲート不正通行                         | normal     | `TRUE`   | TRUE         | x        |                  |                |           |                   |               |              |         |
| 3   | E_0002     | detected     | wrongDirectionEntry                       | ゲート逆進入                           | warn       | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 4   | E_0002     | resolved     | wrongDirectionEntry                       | ゲート逆進入                           | normal     | `TRUE`   | TRUE         | x        |                  |                |           |                   |               |              |         |
| 5   | E_0003     | detected     | stayTooLong                               | ゲート滞留                             | warn       | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 6   | E_0003     | resolved     | stayTooLong                               | ゲート滞留                             | normal     | `TRUE`   | TRUE         | x        |                  |                |           |                   |               |              |         |
| 7   | E_0004     | detected     | tailGating                                | ゲート共連れ                           | warn       | FALSE    | FALSE        | x        |                  |                |           |                   |               |              |         |
| 8   | E_0005     | detected     | gateEquipmentError                        | ゲート機器異常 発生                    | error      | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 9   | E_0005     | resolved     | gateEquipmentError                        | ゲート機器異常 復旧                    | normal     | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 10  | E_0006     | detected     | gateControllerDown                        | ゲートコントローラ―異常 発生           | error      | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 11  | E_0006     | resolved     | gateControllerDown                        | ゲートコントローラ―異常 復旧           | normal     | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 12  | E_0007     | detected     | fireAlarm                                 | 火報信号 ON                            | error      | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 13  | E_0007     | resolved     | fireAlarm                                 | 火報信号 OFF                           | normal     | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 14  | E_0008     | detected     | keepOpening                               | ゲート開放 ON                          | warn       | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 15  | E_0008     | resolved     | keepOpening                               | ゲート開放 OFF                         | normal     | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 16  | E_0009     | detected     | keepClosing                               | ゲート通行禁止 ON                      | warn       | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 17  | E_0009     | resolved     | keepClosing                               | ゲート通行禁止 OFF                     | normal     | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 18  | E_0010     | detected     | keepOpeningButtonPressed                  | ゲート開放ボタン ON                    | warn       | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 19  | E_0010     | resolved     | keepOpeningButtonPressed                  | ゲート開放ボタン OFF                   | normal     | FALSE    | TRUE         | x        |                  |                |           |                   |               |              |         |
| 20  | ~~E_0011~~ | ~~detected~~ | ~~(REMOVED)keepClosingButtonPressed~~(*1) | ~~(除外済み)ゲート通行禁止ボタン ON~~  | ~~warn~~   | ~~TRUE~~ | ~~TRUE~~     | ~~x~~    |                  |                |           |                   |               |              |         |
| 21  | ~~E_0011~~ | ~~resolved~~ | ~~(REMOVED)keepClosingButtonPressed~~(*1) | ~~(除外済み)ゲート通行禁止ボタン OFF~~ | ~~normal~~ | ~~TRUE~~ | ~~TRUE~~     | ~~x~~    |                  |                |           |                   |               |              |         |
| 22  | E_0012     | detected     | nfcCardError_enter                        | 不正アクセスエラー / NFCカード / 入場  | warn       | FALSE    | FALSE        | x        |                  |                |           |                   |               |              |         |
| 23  | E_0013     | detected     | nfcCardError_exit                         | 不正アクセスエラー / NFCカード / 出場  | warn       | FALSE    | FALSE        | x        |                  |                |           |                   |               |              |         |
| 24  | E_0014     | detected     | nfcAuthenticationError_enter              | 認証エラー / NFCカード / 入場          | warn       | FALSE    | FALSE        | x        |                  |                |           |                   |               |              |         |
| 25  | E_0015     | detected     | nfcAuthenticationError_exit               | 認証エラー / NFCカード / 出場          | warn       | FALSE    | FALSE        | x        |                  |                |           |                   |               |              |         |
| 26  | E_0101     | detected     | faceAuthenticationError_enter             | 認証エラー / 顔認証 / 入場             | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 27  | E_0102     | detected     | faceAuthenticationError_exit              | 認証エラー / 顔認証 / 出場             | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 28  | E_0103     | detected     | nfcCardError_enter                        | 不正アクセスエラー / NFCカード / 入場  | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 29  | E_0104     | detected     | nfcCardError_exit                         | 不正アクセスエラー / NFCカード / 出場  | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 30  | E_0105     | detected     | nfcAuthenticationError_enter              | 認証エラー / NFCカード / 入場          | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 31  | E_0106     | detected     | nfcAuthenticationError_exit               | 認証エラー / NFCカード / 出場          | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 32  | E_0107     | detected     | antiSpoofing_enter                        | スプーフィング / 顔認証 / 入場         | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 33  | E_0108     | detected     | antiSpoofing_exit                         | スプーフィング / 顔認証 / 出場         | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 34  | E_0109     | detected     | openedTooLong                             | 扉開タイムアウト 発生                  | warn       | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 35  | E_0109     | resolved     | openedTooLong                             | 扉開タイムアウト 復旧                  | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 36  | E_0110     | detected     | cannotUnlock                              | 解錠異常 発生                          | error      | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 37  | E_0110     | resolved     | cannotUnlock                              | 解錠異常 復旧                          | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 38  | E_0111     | detected     | cannotLock                                | 施錠異常 発生                          | error      | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 39  | E_0111     | resolved     | cannotLock                                | 施錠異常 復旧                          | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 40  | E_0112     | detected     | forcedOpen                                | こじ開け 発生                          | error      | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 41  | E_0112     | resolved     | forcedOpen                                | こじ開け 復旧                          | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 42  | E_0113     | detected     | electricLockWiringError                   | 電気錠異常 発生                        | error      | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 43  | E_0113     | resolved     | electricLockWiringError                   | 電気錠異常 復旧                        | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 44  | E_0114     | detected     | keepUnlocked                              | リモート解錠 ON                        | warn       | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 45  | E_0114     | resolved     | keepUnlocked                              | リモート解錠 OFF                       | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 46  | E_0115     | detected     | authDisabled                              | 閉め切り(認証無効) ON                  | warn       | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 47  | E_0115     | resolved     | authDisabled                              | 閉め切り(認証無効) OFF                 | normal     | FALSE    | TRUE         |          | x                |                |           |                   |               |              |         |
| 48  | E_0116     | detected     | oneShotUnlock                             | リモート解錠(1回のみ)                  | warn       | FALSE    | FALSE        |          | x                |                |           |                   |               |              |         |
| 49  | E_0201     | detected     | faceAuthenticationError_enter             | 認証エラー / 顔認証 / 入場             | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 50  | E_0202     | detected     | faceAuthenticationError_exit              | 認証エラー / 顔認証 / 出場             | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 51  | E_0203     | detected     | nfcCardError_enter                        | 不正アクセスエラー / NFCカード / 入場  | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 52  | E_0204     | detected     | nfcCardError_exit                         | 不正アクセスエラー / NFCカード / 出場  | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 53  | E_0205     | detected     | nfcAuthenticationError_enter              | 認証エラー / NFCカード / 入場          | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 54  | E_0206     | detected     | nfcAuthenticationError_exit               | 認証エラー / NFCカード / 出場          | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 55  | E_0207     | detected     | antiSpoofing_enter                        | スプーフィング / 顔認証 / 入場         | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 56  | E_0208     | detected     | antiSpoofing_exit                         | スプーフィング / 顔認証 / 出場         | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 57  | E_0209     | detected     | keepOpened                                | リモート開放 ON                        | warn       | FALSE    | TRUE         |          |                  | x              |           |                   |               |              |         |
| 58  | E_0209     | resolved     | keepOpened                                | リモート開放 OFF                       | normal     | FALSE    | TRUE         |          |                  | x              |           |                   |               |              |         |
| 59  | E_0210     | detected     | authDisabled                              | 自由通行(認証無効) ON                  | warn       | FALSE    | TRUE         |          |                  | x              |           |                   |               |              |         |
| 60  | E_0210     | resolved     | authDisabled                              | 自由通行(認証無効) OFF                 | normal     | FALSE    | TRUE         |          |                  | x              |           |                   |               |              |         |
| 61  | E_0211     | detected     | oneShotOpen                               | リモート開放(1回のみ)                  | warn       | FALSE    | FALSE        |          |                  | x              |           |                   |               |              |         |
| 62  | E_0301     | detected     | faceAuthenticationError_enter             | 認証エラー / 顔認証 / 入場             | warn       | FALSE    | FALSE        |          |                  |                | x         |                   |               |              |         |
| 63  | E_0302     | detected     | faceAuthenticationError_exit              | 認証エラー / 顔認証 / 出場             | warn       | FALSE    | FALSE        |          |                  |                | x         |                   |               |              |         |
| 64  | E_0401     | detected     | faceAuthenticationError_enter             | 認証エラー / 顔認証 / 入場             | warn       | FALSE    | FALSE        |          |                  |                |           | x                 |               |              |         |
| 65  | E_0402     | detected     | faceAuthenticationError_exit              | 認証エラー / 顔認証 / 出場             | warn       | FALSE    | FALSE        |          |                  |                |           |                   |               |              |         |
| 66  | E_0403     | detected     | nfcCardError_enter                        | 不正アクセスエラー / NFCカード / 入場  | warn       | FALSE    | FALSE        |          |                  |                |           | x                 |               |              |         |
| 67  | E_0404     | detected     | nfcCardError_exit                         | 不正アクセスエラー / NFCカード / 出場  | warn       | FALSE    | FALSE        |          |                  |                |           |                   |               |              |         |
| 68  | E_0405     | detected     | nfcAuthenticationError_enter              | 認証エラー / NFCカード / 入場          | warn       | FALSE    | FALSE        |          |                  |                |           | x                 |               |              |         |
| 69  | E_0406     | detected     | nfcAuthenticationError_exit               | 認証エラー / NFCカード / 出場          | warn       | FALSE    | FALSE        |          |                  |                |           |                   |               |              |         |
| 70  | E_0407     | detected     | antiSpoofing_enter                        | スプーフィング / 顔認証 / 入場         | warn       | FALSE    | FALSE        |          |                  |                |           | x                 |               |              |         |
| 71  | E_0408     | detected     | antiSpoofing_exit                         | スプーフィング / 顔認証 / 出場         | warn       | FALSE    | FALSE        |          |                  |                |           |                   |               |              |         |
| 72  | E_0409     | detected     | lockOperationFailure                      | 電気錠解錠失敗                         | error      | FALSE    | FALSE        |          |                  |                |           | x                 |               |              |         |
| 73  | E_0501     | detected     | nfcAuthenticationError                    | 認証エラー / NFCカード                 | warn       | FALSE    | FALSE        |          |                  |                |           |                   | x             |              |         |
| 74  | E_0503     | detected     | nfcCardError                              | 不正アクセスエラー / NFCカード         | warn       | FALSE    | FALSE        |          |                  |                |           |                   | x             |              |         |
| 75  | E_0505     | detected     | antiSpoofing                              | スプーフィング / 顔認証                | warn       | FALSE    | FALSE        |          |                  |                |           |                   | x             |              |         |
| 76  | E_0507     | detected     | faceAuthenticationError                   | 認証エラー / 顔認証                    | warn       | FALSE    | FALSE        |          |                  |                |           |                   | x             |              |         |
| 77  | E_1001     | detected     | bruteForceAttack                          | 総当たり攻撃 検出                      | error      | FALSE    | FALSE        |          |                  |                |           |                   |               |              | x       |
| 78  | E_1002     | detected     | AntiPassback                              | アンチパスバック                       | warn       | FALSE    | FALSE        |          |                  |                |           |                   |               |              | x       |

You can use [ADR-INTG-0013/0014 - Door/Device State Event Notification](https://docs.google.com/spreadsheets/d/1nh1LBT5XBVqSFwd3JTMcqBh_r5cymIvcQimnlRBa3YQ/edit?gid=948269631#gid=948269631) when you update the list. But update this ADR at the same time.

*1: There is no Keep Closing Button on gates. Therefore `keepClosingButtonPressed` is removed.

---

### Device Event

Device Events are retrieved on the topic  `{prefix}/device/{deviceName}/event`. See [management_mqtt.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_mqtt.yaml) for details.

#### Device Event List

| No  | Code   | Sort     | StateName                | StateName JP                    | Level  | Dismiss | IsPersistent | Authenticator | NFC Controller/Elevator Access Controller | backend |
| --- | ------ | -------- | ------------------------ | ------------------------------- | ------ | ------- | ------------ | ------------- | ----------------------------------------- | ------- |
| 1   | E_2001 | detected | otaFailure               | ソフト更新 失敗                 | error  | FALSE   | FALSE        | x             |                                           |         |
| 2   | E_2002 | detected | deviceWarn               | デバイス 警告                   | warn   | FALSE   | FALSE        | x             |                                           |         |
| 3   | E_2003 | detected | tamperAlert              | タンパーアラート検出            | error  | FALSE   | FALSE        | x             |                                           |         |
| 4   | E_2004 | detected | initializationFailure    | 初期化エラー                    | error  | FALSE   | FALSE        | x             |                                           |         |
| 5   | E_2005 | detected | mechanicalRelayLimitWarn | リレー動作回数 警告             | warn   | FALSE   | FALSE        | x             |                                           |         |
| 6   | E_2006 | detected | apiRequestError_enter    | 認証API エラー / 入場           | error  | FALSE   | FALSE        | x             |                                           |         |
| 7   | E_2007 | detected | apiRequestError_exit     | 認証API エラー / 出場           | error  | FALSE   | FALSE        | x             |                                           |         |
| 8   | E_2008 | detected | apiRequestError          | 認証API エラー                  | error  | FALSE   | FALSE        | x             |                                           |         |
| 9   | E_3001 | detected | otaFailure               | ソフト更新 失敗                 | error  | FALSE   | FALSE        |               | x                                         |         |
| 10  | E_3002 | detected | deviceError              | デバイス 動作異常               | error  | FALSE   | FALSE        |               | x                                         |         |
| 11  | E_3003 | detected | deviceWarn               | デバイス 警告                   | warn   | FALSE   | FALSE        |               | x                                         |         |
| 12  | E_3004 | detected | initializationFailure    | 初期化エラー                    | error  | FALSE   | FALSE        |               | x                                         |         |
| 13  | E_3005 | detected | apiRequestError_enter    | 認証API エラー / 入場           | error  | FALSE   | FALSE        |               | x                                         |         |
| 14  | E_3006 | detected | apiRequestError_exit     | 認証API エラー / 出場           | error  | FALSE   | FALSE        |               | x                                         |         |
| 15  | E_3007 | detected | answerBackError          | エレベータコントローラ 通信異常 | error  | FALSE   | FALSE        |               | x                                         |         |
| 16  | E_3008 | detected | unacceptableFloorName    | フロア設定異常                  | warn   | FALSE   | FALSE        |               | x                                         |         |
| 17  | E_4001 | detected | DeviceOffline            | 通信異常 発生                   | error  | FALSE   | FALSE        |               |                                           | x       |
| 18  | E_4001 | resolved | DeviceOffline            | 通信異常 復旧                   | normal | FALSE   | FALSE        |               |                                           | x       |

You can use [ADR-INTG-0013/0014 - Door/Device State Event Notification](https://docs.google.com/spreadsheets/d/1nh1LBT5XBVqSFwd3JTMcqBh_r5cymIvcQimnlRBa3YQ/edit?gid=1033818850#gid=1033818850) when you update the list. But update this ADR at the same time.

---

## Note

- 2024-10-15 : Remove S_EQ_0010: keepClosingButtonPressed.
- 2024-08-20 : Add a topic for publishing events of elevators - `{prefix}/elv/{elevatorId}/event/{deviceName}` : Kohta Natori
- 2024-07-30 : Change topic name: `{prefix}/door/{doorId}/event` -> `{prefix}/door/{doorId}/event/{deviceName}`
- 2024-07-03 : Fix code of Device Event List
- 2024-07-02 : Approved
- 2024-07-02 : Drafted, Originator: Kohta Natori

# ADR-INTG-0002 Door/Device States and Events Notification

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2024-10-15   |

## Context and Problem Statement

- How to notify states and events(error, alert etc) of door/devices to the backend.
- How to notify these to the Access Control Monitoring system (a.k.a 入退室監視システム).

### Given Conditions

- Use Agora IoTA mechanism to communicate with devices.
- On the site gate, only NFC Controller1(Leader) publishes a gate state and events.

### Requirements of Access Control Monitoring System(UI)

#### About Door(Gate) and Device

- A user can receive a notification, when some errors/alerts happen or they are resolved.
- A user can dismiss a notification after they confirm it.
- A user(or admin[TBD]) can configure which errors/alerts are notified.
- A user can see states of each door(gate)/device.
- A user can see errors/alerts happening/resolving history of the specified door(gate)/device. ([TBD]how long)

### Definition of Event and State

| Word    | Definition                                                                                                                                                                                                                                                                                                                                                                                  |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Event` | `Event` indicates a change of a state which might be notified.<br/> `Event` is converted as a notification on the backend. Then backend notifies it to A/C monitoring system. <br/> Devices can send an event `when it happens and it's resolved` or `only when it happens`.<br/> eg. Illegal entry(無札進入), Fire Alarm Detected(火災報知信号検知), Fire Alarm Resolved(火災報知信号解消) |
| `State` | `State` simply indicates a current state. Some of `State` are states which are sources of events.                                                                                                                                                                                                                                                                                           |

---

## Considered Options

- REST API
- MQTT - IoTA Device Shadow /reported topic
- MQTT - Dedicated topics

You can see an original discussion [here(but will not be maintained any more)](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=468818644).

---

## Decision Outcome

Choose option: `MQTT - Dedicated topics` for Event and State each.

### Reason

- This kind of notification requires no response and we will have many notifications in daily operations. Therefore, MQTT is more suitable rather than REST API from that point of views.
- IoTA Device Shadow /reported topic is basically designed as working with /desired topics. Therefore, using dedicated topics will be more simple and understandable.

### Message Format of MQTT

#### Shared Door **Event** Message

Topic : **{prefix}/door/{doorId}/event/{deviceName}**

Format:

```json
{
  "timestampMs": 123456789,
  // Include only events that have changed
  // 変化のあったイベントのみを含める
  "event": {
    "eventNameA": {
      // set unique code of the event, defined in other document(To Be Updated later PR)
      "code": "E0001",
      // set "detected" or "resolved" as its value
      "sort": "detected",
      // set detail of the event, if necessary. this field is optional.
      "detail": "Note additional information here.",
      // set true if the event will be resolved, otherwise false.
      "isPersistent": true
    },
    "eventNameB": {   
      "code": "E0002",
      "sort": "resolved",
      "detail": "error resolved",
      "isPersistent": true
    }
  }
}
```

See [ADR-INTG-0014 Door/Device Event Conversion](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0014_DoorDeviceEventConversion.md) for actual code assignment.

##### About `detail` field

- It's intended for giving detail information to Access Control Monitoring system's users.
- Information contained in the field can ...
  - help users to take a first countermeasure to resolve the issue.
  - enable to reduce inquiring to the development team in order to get detail information.
- The message contained in the field will be exposed to Access Control Monitoring system's users directly. Therefore the message should be short and simple.

Examples:

```json
// Site Gate: 無札進入発生
{
  "timestampMs": 123456789,
  "event": {
    // illegalEntry is a kind of pulse event.
    "illegalEntry": { 
      "code": "E_0001",  // this code is just for an example.
      "sort": "detected",
      "isPersistent": true
    }
  }
}

// Site Gate: 火災報知検知
{
  "timestampMs": 123456789,
  "event": {
    "fireAlarm": {
      "code": "E_0002",  // this code is just for an example. 
      "sort": "detected",
      "isPersistent": true
    }
  }
}

// Site Gate: 火災報知解消
{
  "timestampMs": 123456789,
  "event": {
    "fireAlarm": {
      "code": "E_0002",  // this code is just for an example. 
      "sort": "resolved",
      "isPersistent": true
    }
  }
}

// Site Gate - 不正な NFC カードが使用された
{
  "timestampMs": 123456789,
  "event": {
    "nfcCardError_enter": {
      "code": "E_0003",  // this code is just for an example. 
      "sort": "detected",
      "detail": "Unidentified NFC card was used.",
      "isPersistent": false
    }
  }
}

// Site Gate - 認証・認可 が NG
{
  "timestampMs": 123456789,
  "event": {
    "nfcAuthenticationError_enter": {
      "code": "E_0004",  // this code is just for an example. 
      "sort": "detected",
      "detail": "Authentication result was NG. Card ID:ID:1234567-ABC",
      "isPersistent": false
    }
  }
}

```

#### Shared Elevator **Event** Message

Elevator Event Messages' format are as same as Door Event Messages' format, but its topic name differs.

Topic : **{prefix}/elv/{elevatorId}/event/{deviceName}**

Format:

- Same as door event's format(above).

---

#### Shared Door **State** Message

Topics :

- Devices -> Backend : **{prefix}/door/{doorId}/state/{deviceName}**
- Backend -> Devices : **{prefix}/door/{doorId}/state/{deviceName}/shared**
  - If multiple devices are installed at the same door, the backend sends the message through this topic to the other devices sharing that door.

Remarks :  
Messages published to this topic will be processed by the PATCH operation. Received messages always will be merged with a previous state. This is from reasons below.

- On the site gate, NFC Controller2(follower) does not know its gate controller states. It sends only its NFC Authentication error.
- The backend needs to merge it with NFC Controller1(leader) state.

Format:

```json
{
  "timestampMs": 123456789,
  "state": {
    // always sends all states
    // State は常に全ての状態を送る
    "stateNameA": {
      // set unique code of the event
      "code": "S_OP_0001", 
      // All states has only 2 values(on/off).
      // 全ての状態は2値(on/off) のみを取る前提
      "isOn": true,
      // set one from "Operating", "Equipment" or "Local".
      // "Operating" represents Authentication device's operating status. eg. KeepClosed, AuthDisabled etc.
      // one or no operating state will be "isOn" = true in one payload.
      // "Equipment" represents equipment(door/gate) status. eq. DoorLocked, FlapperOpened etc.
      // "Local" represents local status only among devices. Backends can ignores these states.
      "type": "Operating"
    },
    "stateNameB": {
      "code": "S_OP_0002",
      "isOn": false,
      "type": "Equipment"
    }
  }
}
```

See [ADR-INTG-0013 Door/Device State Conversion](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0013_DoorDeviceStateConversion.md) for actual code assignment.

Examples:

Refer a [pin assignment of NFC Controller1](https://docs.google.com/spreadsheets/d/1214xiCUQtwauob7IWymDnFnDltTa6R-pG0AZ86URo3c/edit#gid=628058793&range=A83) to see all input signals from a site gate.

```json
{
  // Site Gate State from NFC Controller1 (Leader)
  "timestampMs": 123456789,
  "state": {
    // always sends all states
    // "code" are just for an example, not final one.
    "illegalEntry": {
      "code": "S_EQ_0001",
      "isOn": true,
      "type": "Equipment"
    },
    "wrongDirectionEntry": {
      "code": "S_EQ_0002",
      "isOn": false,
      "type": "Equipment"
    },
    "stayTooLong": {
      "code": "S_EQ_0003",
      "isOn": false,
      "type": "Equipment"
    },
    "tailGating": {
      "code": "S_EQ_0004",
      "isOn": false,
      "type": "Equipment"
    },
    "gateEquipmentError": {
      "code": "S_EQ_0005",
      "isOn": false,
      "type": "Equipment"
    },
    "gateControllerDown": { // Need to reverse DIO signal on the NFC controller app
      "code": "S_EQ_0006",
      "isOn": false,
      "type": "Equipment"
    },
    "fireAlarm": {
      "code": "S_EQ_0007",
      "isOn": false,
      "type": "Equipment"
    },
    "flapperOpened": {
      "code": "S_EQ_0008",
      "isOn": false,
      "type": "Equipment"
    },
    "keepOpeningButtonPressed": {
      "code": "S_EQ_0009",
      "isOn": false,
      "type": "Equipment"
    },
    "keepClosing": {
      "code": "S_OP_0002",
      "isOn": true,
      "type": "Operating"
    },
    // NFC authentication error is not a signal from the gate, but need to be shared with authenticator.
    // 'enter' is related with its deviceId and can be retrieved from the backend.
    "nfcAuthenticationError_enter": {
      "code": "S_LC_0001",
      "isOn": false,
      "type": "Local"
    }
  }
}
```

```json
{
  // Site Gate State from NFC Controller2 (Follower)
  "timestampMs": 123456789,
  "state": {
    // 'exit' is related with its deviceId and can be retrieved from the backend.
    "nfcAuthenticationError_exit": {
      "code": "S_LC_0002",
      "isOn": false,
      "type": "Local"
    }
  }
}
```

---

#### Device **Event** Message

Topic : **{prefix}/device/{deviceName}/event**

Format:
same as Shared Door Event Message format.

```json
{
  "timestampMs": 123456789,
  // Include only events that have changed
  // 変化のあったイベントのみを含める
  "event": {
    "eventNameA": {
      // set unique code of the event, defined in other document(To Be Updated later PR)
      "code": "E_1001",
      // set "detected" or "resolved" as its value
      "sort": "detected",
      // set detail of the event, if necessary. this field is optional.
      "detail": "Note additional information here.",
      // set true if the event will be resolved, otherwise false.
      "isPersistent": true
    },
    "eventNameB": {
      "code": "E_1002",
      "sort": "resolved",
      "isPersistent": false
    }
  }
}
```

See [ADR-INTG-0014 Door/Device Event Conversion](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0014_DoorDeviceEventConversion.md) for actual code assignment.

---

### Naming Convention of State/Event Name in MQTT Message

| Target       | Naming Convention                                                                 |
| ------------ | --------------------------------------------------------------------------------- |
| State, Event | camelCase. '_' is also available. <br/>Name an event as same as its source state. |

---

## Consequences

### TODO after approved

#### Backend

- [x] Enable to get `enter/exit` info related with deviceId (same manner as getting door shared state/event topics).
- [x] Document door shared state topic's operation is PATCH not PUT.
- [x] Design **device** event/state notification topic.
- [x] Consider how to configure notification(on/off) about each event.
- [x] Consider how to convert event/state names to display names.

#### Documentation

- [x] Document message formats including all system type like public door, auto door etc as an another document.
- [x] Remove (Proposed) prefix of this document.

---

## Note

- 2024-10-15 : Remove  keepClosingButtonPressed state from an example. Also fix state code in the example.
- 2024-08-21 : Add `Shared Elevator **Event** Message` section,
- 2024-08-14 : Correct topic names, split door state topic into two.
- 2024-07-02 : Add links to actual code assignment ADRs.
- 2024-06-26 : Update message definition of DoorState, DoorEvent and DeviceEvent. Update TODO after approved status (all done).
- 2023-09-21 : Add `detail` field to door events and device events.
- 2023-07-12 : Approved
- 2023-07-07 : Drafted, Originator: Kohta Natori

# ADR-INTG-0005 Naming Rule of IoTA Group Name

| Status | Last Updated |
|---|---|
|Approved| 2024-04-09 |

## Context and Problem Statement

- Define a rule to give the IoTA **group name** of each device group.
- The group name will be used in MQTT topic names, therefore only `alphabetical characters`, `digit numbers` and `- (hyphen)` are available.
  
---

## Considered Options

- Grouped by device type
  - Authenticator
  - NFC Controller
  - Elevator Access Controller
- Grouped by configuration which a device is installed (see [this confluence page](https://confluence.tri-ad.tech/pages/viewpage.action?spaceKey=CISAM&title=Authenticator+Device+List) for full list)
  - Gate
  - Public door
  - Auto door
  - Private home door (includes Terrace, Amenity and Hanare)
  - Elevator
- Grouped by a combination of configuration and device type
  - Gate - Authenticator
  - Gate - NFC Controller
  - Public door - Authenticator
  - Auto door - Authenticator
  - Private home door  - Authenticator
  - Elevator - Authenticator
  - Elevator - Elevator Access Controller

---

## Decision Outcome

- `Grouped by a combination of configuration and device type.`
- Actual group names of each combinations are below.

| Configuration     | Device Type                | Group Name |
|--|--|--|
| Gate              | Authenticator              | **gate-auth** |
| Gate              | NFC Controller             | **gate-nfc**  |
| Public Door       | Authenticator              | **public-door-auth**  |
| Auto Door         | Authenticator              | **auto-door-auth** |
| Private Home Door | Authenticator              | **private-home-auth** |
| Amenity           | Authenticator              | **private-home-auth** |
| Hanare            | Authenticator              | **private-home-auth** |
| Terrace           | Authenticator              | **private-home-auth** |
| Elevator          | Authenticator              | **elevator-auth** |
| Elevator          | Elevator Access Controller | **elevator-elv** |

### Reason

- Group name should be assigned per each OTA target group.
- Authenticator devices will be used on all configurations but software of each configuration will be different.
- We might want to update software settings per each configuration.

### General Rule (Regex)

- `[a-z0-9]+(-[a-z0-9]+)*`
  - Only lower case chars are available because a device name is used as a part of certificate CN and the certificate CN are not case sensitive.

### Syntax

 *`{configuration}-{device type}`*

#### *{configuration}*

- System configuration which the device is installed.

#### *{device type}*

- Prefix indicating a device type. See [ADR-0004](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0004_IoTADeviceNameNamingRule.md).

---

## Consequences

- The principle is `Group name should be assigned per each OTA target group`. Currently we assume that OTA will be ordered to combinations described above. If the assumption is not acceptable in real-world operation, we need to re-consider about device name naming.

---

## Note

- 2024-04-09 : Prohibit uppercase letters in group names.
- 2023-07-26 : Approved
- 2023-07-26 : Drafted, Originator: Kohta Natori

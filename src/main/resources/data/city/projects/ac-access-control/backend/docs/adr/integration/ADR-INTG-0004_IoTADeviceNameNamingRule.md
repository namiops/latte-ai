# ADR-INTG-0004 Naming Rule of IoTA Device Name

| Status | Last Updated |
|---|---|
|Approved| 2024-04-09 |

## Context and Problem Statement

- Define a rule to give the IoTA device name of each device.
- The device name will be used in MQTT topic names, therefore only `alphabetical characters`, `digit numbers` and `- (hyphen)` are available.

---

## Considered Options

- You can see the discussion on [this confluence page](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=451154051).

---

## Decision Outcome

### General Rule (Regex)

- `[a-z0-9]+(-[a-z0-9]+)*`

### Syntax

 *`{device type}-{device serial}`*

- Only lower case chars are available because a device name is used as a part of certificate CN and the certificate CN are not case sensitive.

#### *{device type}*

- Prefix indicating a device type. See the table below.
  
|Type|*{device type}*|
|--|--|
|Authenticator | `auth` |
|NFC Controller | `nfc` |
|Elevator Access Controller | `elv` |

#### *{device serial}*

- Serial number or device-specific string of a device. See pictures below.

#### Authenticator

- Use **MAC address** as {device serial}. It's printed on a device and can be retrieved in a program via API.
- MAC address format MUST be **no-hyphen** and **lower case**.

![Authenticator](./img/ADR-INTG-0004-img-01.png)

#### Serial Number of NFC Controller/Elevator Access Controller

![NFC Controller](./img/ADR-INTG-0004-img-02.png)

### Examples of Device Name

- `auth-0002651ff5f8`
- `nfc-0272685656`
  
---

## Consequences

- Need to consider how to input these serial no on device provisioning. Manual input might cause input mistake(typo).

---

## Note

- 2024-04-09 : [change] Prohibit uppercase letters in device names.
- 2023-08-24 : [change] Use MAC address for authenticator's device serial.
- 2023-07-26 : Approved
- 2023-07-26 : Drafted, Originator: Kohta Natori

# ADR-INTG-0007 Available Commands of Devices

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2024-03-11   |

## Context and Problem Statement

- Define **AvailableCommands** of each devices on each configurations.
- You can see a sequence of sending device command [here](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/sequences/overview_sequence.md#send-device-command).

---

## Considered Options

From specifications, required functions of each configurations are below.

| Function              | Command String    | Description                                                                                                         | Gate                   | Public Door        | Auto Door         | Private Home(*1) | Elevator |
| :-------------------- | :---------------- | :------------------------------------------------------------------------------------------------------------------ | :--------------------- | :----------------- | :---------------- | :--------------- | :------- |
| **Rebooting**         | `reboot`          | Reboot the device.                                                                                                  | ✓ (*2)                | ✓                 | ✓                | ✓               | ✓       |
| **Always Open**       | `alwaysOpen`      | Switch the device app mode's to `always-open`. User can pass through without authentication.                        | ✓ (keep flapper open) | ✓ (keep unlocked) | ✓ (*3)(can open) | N/A              | N/A      |
| **Always Close**      | `alwaysClose`     | Switch the device app mode's to `always-close`. Authenticators suspends authentication.                             | ✓                     | ✓                 | N/A               | N/A              | N/A      |
| **Revert to Standby** | `revertToStandby` | Revert from `Always Open` or `Always Close` to Standby                                                              | ✓                     | ✓                 | ✓                | N/A              | N/A      |
| **One Shot Open**     | `oneShotOpen`     | Unlock the door one time. If an user passes through or does not pass through in specified time, the door is locked. | N/A                    | ✓                 | ✓                | N/A              | N/A      |

*1 : includes **Terrace**, **Hanare** and **Amenity Space**.  
*2 : `✓` in the table means **available**. `N/A` means not available.  
*3 : `Always Open` on **Auto Door**  : An automatic door works as same as an automatic door without an authenticator. When an user approaches, it opens.

---

## Decision Outcome

- Each device app MUST send **available commands** below on `{groupName}/{deviceName}/shadow/reported` topic.

| Configuration    | Device Type                         | Available Commands                                                            |
| :--------------- | :---------------------------------- | :---------------------------------------------------------------------------- |
| Gate             | Authenticator                       | `reboot`                                                                      |
| Gate             | NFC Controller (Leader=Enter side)  | `reboot`, `keepOpen`, `keepClose`, `revertToStandby`                          |
| Gate             | NFC Controller (Follower=Exit side) | `reboot`                                                                      |
| Public Door      | Authenticator (Leader=Enter side)   | `reboot`, `keepUnlock`, `disableAuth`(*2), `revertToStandby`, `oneShotUnlock` |
| Public Door      | Authenticator (Follower=Exit side)  | `reboot`                                                                      |
| Auto Door        | Authenticator                       | `reboot`, `keepOpen`, `disableAuth`(*3),`revertToStandby`, `oneShotOpen`      |
| Private Home(*1) | Authenticator                       | `reboot`                                                                      |
| Elevator         | Authenticator                       | `reboot`                                                                      |
| Elevator         | Elevator Access Controller          | `reboot`                                                                      |

*1 : includes **Terrace**, **Hanare** and **Amenity Space**.  
*2 : authentication is disabled. A door keeps locked.  
*3 : authentication is disabled. An automatic door opens on sensor detection(= same as an automatic door without an authenticator).

---

## Note

- 2024-03-11 : Change `alwaysOpen` to `keepOpen`, `alwaysClose` to `keepClose` for consistency (Gate).
- 2024-03-11 : Update available commands on each device groups.
- 2023-08-24 : Approved
- 2023-08-24 : Drafted, Originator: Name

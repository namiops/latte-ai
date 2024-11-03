# ADR-SEC-0005 NFC Reader/Writer Secrets(Keys)

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2024-05-29   |

## Table of Contents

- [ADR-SEC-0005 NFC Reader/Writer Secrets(Keys)](#adr-sec-0005-nfc-readerwriter-secretskeys)
  - [Table of Contents](#table-of-contents)
  - [Context and Problem Statement](#context-and-problem-statement)
    - [Use Cases](#use-cases)
    - [Assumptions](#assumptions)
    - [FeliCa Group Service Key, User Service Key](#felica-group-service-key-user-service-key)
    - [Mutual Authentication Key (Limited to QK30-IC)](#mutual-authentication-key-limited-to-qk30-ic)
  - [Considered Options](#considered-options)
    - [Options : Key Storage - FeliCa Group Service Key, User Service Key](#options--key-storage---felica-group-service-key-user-service-key)
      - [Key Storage of UBio-X Face Pro](#key-storage-of-ubio-x-face-pro)
      - [Key Storage of QK30-IC](#key-storage-of-qk30-ic)
    - [Options : Key Storage - Mutual Authentication Keys](#options--key-storage---mutual-authentication-keys)
    - [Options : Key Uniqueness - Mutual Authentication Keys(Limited to QK30-IC)](#options--key-uniqueness---mutual-authentication-keyslimited-to-qk30-ic)
    - [Options : Key Transfer on Setup - FeliCa Group/Service Keys, Mutual Authentication Keys](#options--key-transfer-on-setup---felica-groupservice-keys-mutual-authentication-keys)
  - [Decision Outcomes](#decision-outcomes)
    - [Use Case No.1 - UBio-X Face Pro/Read-only](#use-case-no1---ubio-x-face-proread-only)
    - [Use Case No.2 - QK30-IC / Read-only](#use-case-no2---qk30-ic--read-only)
    - [Use Case No.3 - QK30-IC / Read/Write](#use-case-no3---qk30-ic--readwrite)
  - [Consequences](#consequences)
  - [References](#references)
  - [Note](#note)

## Context and Problem Statement

- Define secrets when the system `reads NFC ID from NFC cards` or `writes NFC ID into NFC cards`. NFC ID is unique UUID issued by [nfc-manager](https://github.com/wp-wcm/city/tree/main/projects/ac-access-control/nfc-manager) service. A relationship between NFC ID and WovenID is managed by nfc-manager service.
- Consider how to treat these secrets securely, both device setup stage and operation stage.

### Use Cases

| No  | Read/Write | Reader/Writer Device                                                                        | Device Controller         | Application                                                                                                                       | Device - Device Controller Comm Encryption | Card Key Encryption Method |
| --- | ---------- | ------------------------------------------------------------------------------------------- | ------------------------- | --------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------ | -------------------------- |
| 1   | Read-only  | [UBio-X Face Pro](https://drive.google.com/drive/folders/1PIPj8ZDRY3XCv8hxxmcKkeQBztJ-sfvw) | Same as left (*1)         | [ac-authenticator-app](https://github.tri-ad.tech/R-D-WCM/ac-authenticator-app)                                                   | N/A (*2)                                   | AES                        |
| 2   | Read-only  | [DENSO QK30-IC](https://drive.google.com/drive/folders/1mJxeUMXn506jjrAQgegvC3rMpsaB5YeV)   | Windows IoT Enterprise PC | [ac-edge-controller-app (Controller.Nfc)](https://github.tri-ad.tech/R-D-WCM/ac-edge-controller-app/tree/main/src/Controller.Nfc) | Triple-DES                                 | DES                        |
| 3   | Read/Write | [DENSO QK30-IC](https://drive.google.com/drive/folders/1mJxeUMXn506jjrAQgegvC3rMpsaB5YeV)   | Windows 11 Pro            | [ac-nfc-card-write-service](https://github.tri-ad.tech/R-D-WCM/ac-nfc-card-write-service)                                         | Triple-DES                                 | DES                        |

(*1): NFC reader module is embedded in the authenticator device(UBio-X Face Pro), therefore they can communicate directly without an encryption.  
(*2): Application can access to NFC reader module directly via the manufacture provided SDK.

![UseCases](images/ADR-SEC-0005/Usecases.png)

### Assumptions

- UBio-X Face Pro
  - Root user is disabled.
  - File transfer, USB-Debugging, ADB shell are disabled.
  - Number of devices is about 775 (includes spare units).

- DENSO QK-30-IC
  - A device and the device controller are connected via USB-A cable.
  - The device has non-volatile memory to store `Mutual Authentication Keys`, `Group Service Key`, `User service Key`. It's not possible to read these keys from the device. These are used for communication or accessing cards in the device.
  - Devices on Read-only configuration are embedded into Site Gate Casing.
  - Devices on Read/Write configuration are placed in a control room(防災センター).
  - Number of read-only devices is 37 (includes spare units).
  - Estimated number of read/write devices is up to 10 (not determined yet).

- Windows  IoT Enterprise PC
  - An authentication with a strong password is configured to login a device controller.
  - Hard drive is encrypted(eg. BitLocker) and can be accessed only with valid authentication.
  - The PC is managed by MDM software(eg. Intune)

- Windows 11 Pro PC
  - An authentication with a strong password is configured to login a device controller.
  - Hard drive is encrypted(eg. BitLocker) and can be accessed only with valid authentication.
  - The PC is managed by MDM software(eg. Intune)
  - Users of `ac-nfc-card-write-service` has WonveID and appropriate privileges.
  - The PC is placed in a control room(防災センター).

---

### FeliCa Group Service Key, User Service Key

- FeliCa Group Service Key, User Service Key is a `symmetric key`. It is used to access to secure area of NFC cards. This secure area is dedicated for Woven By Toyota.
- FeliCa cards used in the Woven city supports two types of encryption methods : `DES` and `AES`. AES is newer and preferable, but the system handle both methods since QK30-IC supports only DES to access to NFC cards.
- There are two service codes, one is for `Read-only` and an other is for `Read/Write`.
- In the result, we have keys below, all keys are degeneration key(縮退キー).

| No  | Encryption | Service Type | Key Type                                                 | Reader/Writer      |
| --- | ---------- | ------------ | -------------------------------------------------------- | ------------------ |
| 1   | DES        | Read-only    | Group Service Key (8 byte)<br/>User Service Key (8 byte) | QK30-IC            |
| 2   | DES        | Read/Write   | Group Service Key (8 byte)<br/>User Service Key (8 byte) | QK30-IC            |
| 3   | AES        | Read-only    | Group Service Key (16 byte)                              | UBio-X Face Pro    |
| 4   | AES        | Read/Write   | Group Service Key (16 byte)                              | NOT used currently |

**Available Options of Keys Storing Location**:

| No  | Reader/Writer Device | Location                      | Description                                                                |
| --- | -------------------- | ----------------------------- | -------------------------------------------------------------------------- |
| 1   | QK30-IC              | non-volatile memory (secured) | Set keys on the setup phase. Need to erase keys before disposing a device. |
| 2   | QK30-IC              | volatile memory               | Set keys on every power on from a device controller.                       |
| 3   | UBio-X Face Pro      | non-volatile memory (secured) | Set keys on the setup phase. Need to erase keys before disposing a device. |

**Impacts when the keys are compromised**:

- Keys of Read-only service type
  - An attacker can `read NFC ID in NFC cards`. To spoof authentication with stolen NFC ID, an attacker needs a legitimate device.
  - An attacker can NOT clone or forge NFC cards only with keys of Read-only service type.

- Keys of Read/Write service type
  - An attacker can read NFC ID in NFC cards.
  - An attacker can `clone or forge NFC cards` if they have legitimate NFC cards issued for Woven by Toyota by the card manufacture. Cloned or forged NFC cards will be treated as legitimate ones.
  - Associations between NFC ID and Woven ID are managed on the backend. Therefore an attacker needs to register that associations to the backend when they try to forges NFC cards.

**Possible countermeasures after the keys are compromise**:

- Change devices' and device controllers' configuration to use another(reserved) ID area's keys of NFC cards.
  - Re-configure new keys to all NFC readers/writers.
  - Re-write new ID to all NFC cards' another(reserved) ID area.

- or purchase new NFC cards with keys for an another security area from a card manufacture. It brings...
  - Re-configure new keys to all NFC readers/writers.
  - NFC cards with compromised keys(old cards) becomes unavailable.
  - Recall all issued(old) NFC cards and re-issue all ones with new NFC cards.

---

### Mutual Authentication Key (Limited to QK30-IC)

- Mutual Authentication Key is a `symmetric key`. It is used to communicate between a device controller and NFC Reader/Writer(QK30-IC) with an encryption. Consists of 8 byte length * 3 keys(Ka1, Ka2, Ka3)
- It is required to set(save) keys to a NFC Reader/Writer on setup stage. Keys are stored into non-volatile memory of the device.
- To communicate with a NFC reader/writer, a device controller MUST use keys as same as ones which are stored in the NFC reader/writer beforehand. There is no feature to read keys from the NFC reader/writer. If the keys are lost, the device becomes unavailable(nothing to do including reset keys).

**Impacts when the keys are compromised**:

- An attacker can use `a spoofing NFC reader/writer` with a legitimate device controller.
- An attacker can use a legitimate NFC reader/writer with `a spoofing device controller`.
- An attacker can decrypt communication(if they can intercept it) between a legitimate NFC reader/writer and a legitimate device controller.
- If an attacker has FeliCa Read-only group/user service keys or has a NFC Reader/Writer that the keys are stored in the device's non-volatile memory, an attacker can `read NFC ID in NFC cards`.
- If an attacker has FeliCa Read/Write group/user service keys or has a NFC Reader/Writer that the keys are stored in the device's non-volatile memory, an attacker can `clone or forge NFC cards`. Cloned or forged NFC cards will be treated as legitimate ones.

**Possible countermeasures after the keys are compromise**:

- Re-configure new mutual authentication keys to all NFC reader/writers and device controllers which used compromised keys.

---

## Considered Options

### Options : Key Storage - FeliCa Group Service Key, User Service Key

#### Key Storage of UBio-X Face Pro

| No    | Device          | Key Storage                                                                |
| ----- | --------------- | -------------------------------------------------------------------------- |
| A-1-1 | UBio-X Face Pro | Save keys to the device's `Non-volatile` memory (there is no other option) |

![A-1-1](images/ADR-SEC-0005/A-1-1.png)

**Pros:**

- The memory is protected at the hardware layer.

**Cons**:

- Need to erase keys before disposing a device.

  ---

#### Key Storage of QK30-IC

| No    | Device  | Key Storage                                     |
| ----- | ------- | ----------------------------------------------- |
| A-2-1 | QK30-IC | Save keys to the device's `Non-volatile` memory |

![A-2-1](images/ADR-SEC-0005/A-2-1.png)

**Pros:**

- The memory is protected at the hardware layer.

**Cons:**

- It's easier to steal the device(QK30-IC) itself than stealing the device controller(PC).
- When the device(QK30-IC) is stolen, it's impossible to disable the device remotely.
- Need to erase keys before disposing a device.

  ---

| No    | Device  | Key Storage                                                                                                              |
| ----- | ------- | ------------------------------------------------------------------------------------------------------------------------ |
| A-2-2 | QK30-IC | Save keys to the device controller's disk with encryption.<br/>Write it to device's `volatile` memory on every power-on. |

![A-2-2](images/ADR-SEC-0005/A-2-2.png)

**Pros:**

- The memory on the device is cleared when turning the device's power off.
- The device controller(PC) can be locked remotely by MDM software(eg. Intune) even if it is stolen.
- If the device controller(PC) is stolen, an attacker can not see the keys without valid authentication.
- Stealing the device controller(PC) is physically more difficult than stealing the device(QK30-IC).

**Cons**:

- If an attacker gets an authentication info of the device controller(PC), they can get the keys.

  ---

| No    | Device  | Key Storage                                                                                                                                                             |
| ----- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| A-2-3 | QK30-IC | Save keys to the backend.<br/>The device controller gets keys via network(eg. Web API) on every power-on.</br>Write it to device's `volatile` memory on every power-on. |

![A-2-3](images/ADR-SEC-0005/A-2-3.png)
**Pros:**

- The memory on the device is cleared when turning the device's power off.
- Stealing the device controller(PC) is physically more difficult than stealing the device(QK30-IC).
- The device controller(PC) can be locked remotely by MDM software(eg. Intune) even if it is stolen.

**Cons**:

- An API for getting the key on the backend is exposed with mTLS device authentication. The API to get keys can be the point where the key is compromised.

---

### Options : Key Storage - Mutual Authentication Keys

The keys MUST be saved to QK30-IC’s non-volatile memory. The belows are options where the keys are stored on the device controller(PC) side.

| No  | Key Storage                                                |
| --- | ---------------------------------------------------------- |
| B-1 | Save keys to the device controller's disk with encryption. |

![B-1](images/ADR-SEC-0005/B-1.png)

**Pros:**

- The device controller(PC) can be locked remotely by MDM software(eg. Intune) even if it is stolen.
- If the device controller(PC) is stolen, an attacker can not see the keys without valid authentication.

**Cons:**

- If an attacker fulfill the conditions belows, they can read from/write to NFC card secure area.
  - An attacker has the device(QK30-IC) paired(same mutual authentication keys) with the device controller(PC).
  - The paired device(QK30-IC) has FeliCa group/user service keys on its non-volatile memory.

  ---

| No  | Key Storage                                                                                               |
| --- | --------------------------------------------------------------------------------------------------------- |
| B-2 | Save keys to the backend.<br/>The device controller gets keys via network(eg. Web API) on every power-on. |

![B-2](images/ADR-SEC-0005/B-2.png)

**Pros:**

- Even if the device controller(PC) and paired the device are stolen, an attacker can not read from/write to NFC card secure area without valid network access.
- The device controller(PC) can be locked remotely by MDM software(eg. Intune) even if it is stolen.

**Cons:**

- An API for getting mutual authentication keys on the backend is exposed with mTLS device authentication. The API to get keys can be the point where the key is compromised.

---

### Options : Key Uniqueness - Mutual Authentication Keys(Limited to QK30-IC)

| No  | Option                       |
| --- | ---------------------------- |
| C-1 | Use same keys to all devices |

![C-1](images/ADR-SEC-0005/C-1.png)

**Pros:**

- Easy to manage keys and devices/device controllers.

**Cons:**

- If keys are compromised from a device/device controller (or a device/device controller is stolen), re-setup keys to all devices/device controllers is required.

  ---

| No  | Option                         |
| --- | ------------------------------ |
| C-2 | Use unique keys to all devices |

![C-2](images/ADR-SEC-0005/C-2.png)

**Pros:**

- Even if keys are compromised from a device/device controller (or a device/device controller is stolen), no counter-measure to other devices/device controllers is required.

**Cons:**

- The management of keys, devices/device controllers becomes complex.
- If keys of a device is lost, the device becomes unavailable (can not reset keys of a device).

---

### Options : Key Transfer on Setup - FeliCa Group/Service Keys, Mutual Authentication Keys

| No  | Option                    | Key Transmission | Original Key Storage    |
| --- | ------------------------- | ---------------- | ----------------------- |
| D-1 | Transfer keys via network | via network      | Secure store(eg. vault) |

![D-1](images/ADR-SEC-0005/D-1.png)

**Pros:**

- Keys are not seen to anyone during normal setup stage.
- Using backend's secure store can minimize people who can access to the keys.

**Cons:**

- An API for getting keys on the backend is exposed with mTLS device authentication.
- Device certificate is required to call the API.
- A setup program is needed for UBio-X Face Pro and QK30-IC's device controller.

  ---

| No  | Option                                 | Key Transmission    | Original Key Storage          |
| --- | -------------------------------------- | ------------------- | ----------------------------- |
| D-2 | Hard-code the key into the application | included in the app | developer's PC and build env. |

![D-2](images/ADR-SEC-0005/D-2.png)

**Pros:**

- Keys are not seen to anyone during normal setup stage.

**Cons:**

- Keys are seen to all developers.

  ---

| No  | Option                  | Key Transmission | Original Key Storage        |
| --- | ----------------------- | ---------------- | --------------------------- |
| D-3 | Enter manually on setup | human eys        | Secure store(eg. 1Password) |

![D-3](images/ADR-SEC-0005/D-3.png)

**Pros:**

- Keys are not appeared on network.

**Cons:**

- Keys are seen to setup operators.

## Decision Outcomes

### [Use Case](#use-cases) No.1 - UBio-X Face Pro/Read-only

**Decision - FeliCa group service key (AES, Read-only):**

| Key Storage on Device                                  | Key Transfer on Setup Phase       |
| ------------------------------------------------------ | --------------------------------- |
| A-1-1: Save keys to the device's `Non-volatile` memory | D-1: Transfer keys to via network |

![UseCase-1](images/ADR-SEC-0005/Usecase-1.png)

**Reason:**

- Key Storage
  - It's difficult to steal keys from secured non-volatile memory, even if a device is stolen.- Also there is no other option.
- Key Transfer
  - Number of people who can see the key should be minimized.
  - Getting and saving keys automatically are preferable to avoid human errors during the setup.

---

### [Use Case](#use-cases) No.2 - QK30-IC / Read-only

**Decision - FeliCa group/user service key (DES, Read-only):**

| Key Storage on Device                                  | Key Transfer on Setup Phase       |
| ------------------------------------------------------ | --------------------------------- |
| A-2-1: Save keys to the device's `Non-volatile` memory | D-1: Transfer keys to via network |

![UseCase-2-1](images/ADR-SEC-0005/Usecase-2-1.png)

**Reason:**

- Key Storage
  - It's difficult to steal keys from secured non-volatile memory, even if a device is stolen.
- Key Transfer
  - Number of people who can see the key should be minimized.
  - Getting and saving keys automatically are preferable to avoid human errors during the setup.

  ---

**Decision - Mutual authentication key:**

| Key Uniqueness                    | Key Storage                                                     | Key Transfer on Setup Phase       |
| --------------------------------- | --------------------------------------------------------------- | --------------------------------- |
| C-1: Use same keys to all devices | B-1: Save keys to the device controller's disk with encryption. | D-1: Transfer keys to via network |

![UseCase-2-2](images/ADR-SEC-0005/Usecase-2-2.png)

**Reason:**

- Key Uniqueness
  - Using same keys to reduce cost of key and device management.
  - QK30-IC: It's difficult to steal keys from secured non-volatile memory, even if a device is stolen.

- Key Storage
  - Device controller: It's difficult to steal keys from the disk without authentication.
  - Device controller: Even if a device controller is stolen, it can be locked by MDM software.
  
- Key Transfer
  - Number of people who can see the key should be minimized.
  - Getting and saving keys automatically are preferable to avoid human errors during the setup.

  ---

### [Use Case](#use-cases) No.3 - QK30-IC / Read/Write

**Decision - FeliCa group/user service key (DES, Read/Write):**

| Key Storage on Device                                                                                                           | Key Transfer on Setup Phase  |
| ------------------------------------------------------------------------------------------------------------------------------- | ---------------------------- |
| A-2-2: Save keys to the device controller's disk with encryption.<br/>Write it to device's `volatile` memory on every power-on. | D-3: Enter manually on setup |

![UseCase-3-1](images/ADR-SEC-0005/Usecase-3-1.png)

**Reason:**

- Key Storage
  - Read/Write key should be treated more securely rather than Read-only key since compromising of Read/Write key can make an attacker possible to clone or forge valid NFC cards. (an attacker also needs NFC card with secure area for Woven by Toyota to clone or forge).
  - It's impossible to track or lock the device(QK30-IC), if it is stolen. Therefore saving keys on non-volatile memory can bring key compromising, even its likelihood is very low.
  - Stealing the device controller(PC) is more difficult to steal the device(QK30-IC). The device controller can be locked remotely by MDM software, if it's stolen.
  - Estimated number of NFC read/write is few compared to number of NFC read-only devices.
  
- Key Transfer
  - Number of people who can see the key should be minimized.
  - An API for getting the key is a point where the keys are compromised.
  - Estimated number of NFC read/write is few compared to number of NFC read-only devices.

  ---

**Decision - Mutual authentication key:**

| Key Uniqueness                      | Key Storage                                                     | Key Transfer on Setup Phase  |
| ----------------------------------- | --------------------------------------------------------------- | ---------------------------- |
| C-2: Use unique keys to all devices | B-1: Save keys to the device controller's disk with encryption. | D-3: Enter manually on setup |

![UseCase-3-2](images/ADR-SEC-0005/Usecase-3-2.png)

**Reason:**

- Key Uniqueness
  - Use unique keys since read/Write device should be treated more securely rather than Read-only device.
  - Since number of NFC Writer will be very few compared to number of NFC Reader, key and device management cost is not expensive.

- Key Storage
  - Device controller: It's difficult to steam keys from the disk without authentication.
  - Device controller: Even if a device controller is stolen, it can be locked by MDM software.
  - Estimated number of NFC read/write is few compared to number of NFC read-only devices.

- Key Transfer
  - Number of people who can see the key should be minimized.
  - An API for getting the key is a point where the keys are compromised.
  - Estimated number of NFC read/write is few compared to number of NFC read-only devices.

## Consequences

After decision outcomes are approved, following actions are required...

**Backend:**

- Add an API to get FeliCa group/service keys for Read-only devices.
- Add an API to get mutual authentication keys for Read-only devices.

**ac-authenticator-app (UBio-X Face Pro, Read-only):**

- Modify the app to get FeliCa keys from the backend on the initial setup.

**ac-edge-controller (device controller of QK-30IC, Read-only):**

- Add a feature to get FeliCa keys and mutual authentication keys from the backend on the initial setup.
- Add a feature to save mutual authentication keys on the disk with an encryption.

**ac-nfc-card-write-service (device controller of QK30-IC, Read/Write):**

- Modify the app to use QK30-IC's volatile memory.
- Add a feature to save FeliCa keys and mutual authentication keys on the disk with an encryption.

## References

- [Confluence article : NFC (some sections are Japanese only, may not be maintained anymore)](https://confluence.tri-ad.tech/display/CISAM/NFC)
- [Confluence article : NFCについて (Japanese only, may not be maintained anymore)](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=411828934)
- [DENSO PR-700 FeliCa カード制御コマンドマニュアル](https://drive.google.com/file/d/1IPjdcnWHiqCrWSiwBjRtS5N1wrvNKf-m/view)
- [DENSO PR-700 通信セキュリティマニュアル](https://drive.google.com/file/d/1s58Q4VvGlNC7ASWO7CmI8X8-lfS9-K1g/view)
- [DENSO AID-Ⅱ リファレンスマニュアル](https://drive.google.com/drive/folders/1rGXmj7HV1pZORVHo2W9ywdu3ZQv9ukur)
- [DENSO QK30-IC(N) 取扱説明書](https://drive.google.com/drive/folders/1l9vHeM11jyk99zztIZNFOzA4-aL3Myw2)
- [Union Community SDK 仕様書 (Google drive folder)](https://drive.google.com/drive/folders/1PIPj8ZDRY3XCv8hxxmcKkeQBztJ-sfvw)
- [Original images(Figma)](https://www.figma.com/board/3PK89mbzqaSkszUSY39tzM/ADR-SEC-0005-Images?node-id=0%3A1&t=KFM6DQCyqGCKLtu3-1)

## Note

- 2024-05-29 : Approved
- 2024-05-23 : Review conducted, [minutes (Japanese only)](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=877596087)
- 2024-05-23 : Drafted, Originator: Kohta Natori

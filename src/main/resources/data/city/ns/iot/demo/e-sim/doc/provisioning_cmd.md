# Specification for STORE DATA Command for Provisioning

## Introduction

* In this applet, provisioning (writing secret keys, etc.) and remote management are carried out using the APDU command "STORE DATA".
    * You can write keys (public key, private key), certificates, and other arbitrary data (files), perform OBKG (On-board Key Generation), read public keys, and more.

* Since the command specification for provisioning is not defined in the standard specification, the DoCoMoBusiness specification is established for these parts, and the specifications are described in this document and in the source code.

* Commands for debugging are also described in this document.

## Prerequisites and References

* Standard Specifications
    * <https://www.gsma.com/iot/iot-safe/>
    * [GSMA IoT.04](https://www.gsma.com/iot/wp-content/uploads/2019/12/IoT.04-v1-Common-Implementation-Guide-1.pdf)
    * [GSMA IoT.05](https://www.gsma.com/iot/wp-content/uploads/2019/12/IoT.05-v1-IoT-Security-Applet-Interface-Description.pdf)

* The STORE DATA command is processed in the following method of this applet:
    `com.ntt.iotsafe.AppletStoreManager#processData`

    * The `AppletStore` and `AppletStoreManager` classes correspond to the concept 'The applet store' in GSMA IoT.05.

* Keys and files are uniquely identified by ID or label.
    * Therefore, IDs and labels must be assigned unique values for each key.

    * However, since public keys, private keys, and files are managed separately, it is acceptable to assign the same ID or label to, for example, both a public key and a private key.
        * This is in accordance with the following provision in GSMA IoT.05.

        ```text
        Inside a type of object (public key, private key, secret key, files) object reference (identifiers, labels) must be unique. This rule does not apply to objects of different type.
        ```

    * The exact use of IDs and labels is not strictly defined and can be operational, as long as the operational policy is agreed upon between the server side and the device side.
      This is in accordance with the following provision in GSMA IoT.05.

      ```text
      Note: It is assumed that label & ID policies set on the IoT security server are known on the device side. The device knows if IDs are used to identify containers or content and can thus use labels or IDs accordingly.
      ```

    * Labels are optional, so they can be omitted.

## Basic Structure of Commands

* The STORE DATA command payload contains one TLV. The T (tag) of the TLV is interpreted as the command number.
* If the TLV fits within one STORE DATA command (i.e., the length of the TLV is 247 bytes or less), set P1 to 0x80 or 0x81 (P1.b8=1) and P2 to 0.
* If the TLV is longer, set P1 and P2 appropriately and divide it into multiple STORE DATA commands.

| Code    | Length | Description           | Possible Value                                                                                              |
| ------- | ------ | --------------------- | ----------------------------------------------------------------------------------------------------------- |
| CLA     | 1      | Class byte            | 0x80                                                                                                        |
| INS     | 1      | STORE DATA            | 0xE2                                                                                                        |
| P1      | 1      | STORE DATA            | According to STORE DATA command specification<br/>b8: last block,<br/>b1: case 4 command (response data may be returned) |
| P2      | 1      | STORE DATA            | Block number                                                                                                |
| Lc      | 1      | Length of the payload |                                                                                                             |
| payload | Lc     |                       | Contains one TLV (Tag, Length, Value)                                                                       |
| Le      | 0 or 1 |                       | None or 0x00                                                                                                |

* Structure of payload (TLV)

|        | Length    | Description                                                                                   |
| ------ | --------- | --------------------------------------------------------------------------------------------- |
| Tag    | 1         | Command number. One of the constants `com.ntt.iotsafe.AppletConstants#IoTSA_TLV_INS_*`         |
| Length | 1, 2 or 3 | Length of the Value. Follows the BER-TLV specification (ETSI TS 101 220 Clause 7.1.2).         |
| Value  | Len       | Value. The content varies depending on the command number                                     |

* Encoding of the Length field in TLV (ETSI TS 101 220 Clause 7.1.2)

    * 0 <= Length <= 0x7f: 1 byte [Length]
    * 0x80 <= Length <= 0xff: 2 bytes [0x81, Length]
    * 0x100 <= Length <= 0xffff: 3 bytes [0x82, upper 8 bits of Length, lower 8 bits of Length]

---

## List of Commands

---
For the values of Command (Tag), refer to the source file `AppletConstants.java`.

| Command(Tag)                              | Function                                    |
|-------------------------------------------|---------------------------------------------|
| IoTSA_TLV_INS_CREATE_ECC_KEYPAIR          | Create a key pair (private key and public key) and initialize the keys randomly   |
| IoTSA_TLV_INS_CREATE_PRIVATE_KEY_SLOT     | Create an object to store the private key                                        |
| IoTSA_TLV_INS_UPDATE_PRIVATE_KEY          | Write the private key                                                            |
| IoTSA_TLV_INS_CREATE_PUBLIC_KEY_SLOT      | Create an object to store the public key                                         |
| IoTSA_TLV_INS_UPDATE_PUBLIC_KEY           | Write the public key                                                             |
| IoTSA_TLV_INS_CREATE_FILE_SLOT            | Create an object to store files (certificates, etc.)                             |
| IoTSA_TLV_INS_UPDATE_FILE                 | Write files (certificates or arbitrary data)                                     |
| IoTSA_TLV_INS_SELECT_OBJECT               | Select a storage object                                                          |
| IoTSA_TLV_INS_DELETE_OBJECT               | Delete private key/public key/file (storage object)                              |
| IoTSA_TLV_INS_READ_PUBLIC_KEY             | Read the public key                                                              |
| IoTSA_TLV_INS_SELECT_AND_READ_FILE        | Select and read the file                                                        |
| IoTSA_TLV_INS_GENERATE_CSR                | Generate a Certificate Signing Request(CSR) and write it to the file |
| IoTSA_TLV_INS_SELECT_AND_READ_PUBLIC_KEY  | Select and read the public key                                                   |
| IoTSA_TLV_INS_DEBUG                       | Obtain debug dump, toggle debug mode ON/OFF                                      |
| IoTSA_TLV_INS_RESET_APPLET_STORE          | Erase all keys, etc., and return to the state immediately after applet installation (for debugging) |

---

## Command Specifications

---

### IoTSA_TLV_INS_CREATE_PRIVATE_KEY_SLOT

---

* Creates an object to store the private key. The ID and label specified in the TLV Value will be assigned.
* The value of the private key is not created by this command. (It is written with IoTSA_TLV_INS_UPDATE_PRIVATE_KEY)

    * The TLV Value is GSMA IoT.05 Clause 2.14.4.2 (Private key information structure).
        * Private key identifier (tag 84h) and Key type (tag 4Bh) are mandatory fields.
            * Other fields are not required for the applet to function but are necessary to comply with GSMA IoT.05.
        * The key type should be 13h (NIST secp256r1 (persistent)) or 23h (BrainpoolP256r1 (persistent)).

---

### IoTSA_TLV_INS_CREATE_PUBLIC_KEY_SLOT

---

* Creates an object to store the public key. The ID and label specified in the TLV Value will be assigned.

* The value of the public key is not created by this command. (It is written with IoTSA_TLV_INS_UPDATE_PUBLIC_KEY)

* The TLV Value is GSMA IoT.05 Clause 2.14.4.3 Public key information structure.
    * Public key identifier (tag 85h) and Key type (tag 4Bh) are mandatory fields.
    * The key type should be 13h (NIST secp256r1 (persistent)) or 23h (BrainpoolP256r1 (persistent)).

---

### IoTSA_TLV_INS_CREATE_FILE_SLOT

---

* Creates an object to store files (certificates, etc.). The ID and label specified in the TLV Value will be assigned.

* The content of the file is not created by this command. (It is written with IoTSA_TLV_INS_UPDATE_FILE)

* The TLV Value is GSMA IoT.05 Clause 2.14.4.4 File information structure.

---

### IoTSA_TLV_INS_SELECT_OBJECT

---

* Selects a storage object.

* The storage object must be previously created by one of the following commands.
    * IoTSA_TLV_INS_CREATE_{PRIVATE_KEY|PUBLIC_KEY|FILE}_SLOT
    * IoTSA_TLV_INS_CREATE_ECC_KEYPAIR

* The TLV Value is one of the following TLVs.
    * File label (tag 73h)
    * Private key label (tag 74h)
    * Public key label (tag 75h)
    * File ID (tag 83h)
    * Private key identifier (tag 84h)
    * Public key identifier (tag 85h)

---

### IoTSA_TLV_INS_UPDATE_PRIVATE_KEY

---

* Writes the private key to the currently selected storage object.

* This command can only be executed immediately after one of the following commands. (The storage object must be in a selected state)
    * IoTSA_TLV_INS_CREATE_PRIVATE_KEY_SLOT
    * IoTSA_TLV_INS_SELECT_OBJECT

* The TLV Value is the following TLV.

    | Tag  | Length                                  | Value           |
    | ---- | --------------------------------------- | --------------- |
    | 0x47 | Depends on key type (32 for secP256r1)  | ECC Private key |

    * 0x47 = PRIVATE_TLV_TAG_ECC_PRIVATE_KEY (proprietary constant definition)

---

### IoTSA_TLV_INS_UPDATE_PUBLIC_KEY

---

* Writes the public key to the currently selected storage object.
* This command can only be executed immediately after one of the following commands. (The storage object must be in a selected state)
    * IoTSA_TLV_INS_CREATE_PUBLIC_KEY_SLOT
    * IoTSA_TLV_INS_SELECT_OBJECT
* The TLV Value is the ECC Public Key Format specified in GSMA IoT.05 Clause 2.5.7.

---

### IoTSA_TLV_INS_UPDATE_FILE

---

* Writes files (certificates or arbitrary data) to the currently selected storage object.
    * The intended uses of this command are as follows:
        * Distributing certificates. Create a PEM-formatted certificate by adding a CA signature to the public key (read with the IoTSA_TLV_INS_READ_PUBLIC_KEY command or generated in advance on the server side) and write it to the applet using this command to distribute it to the device.
        * Distributing certificate revocation information (CRL)
        * Distributing other arbitrary data

* This command can only be executed immediately after one of the following commands. (The storage object must be in a selected state)
    * IoTSA_TLV_INS_CREATE_FILE_SLOT
    * IoTSA_TLV_INS_SELECT_OBJECT

* The TLV Value is the file (certificate or arbitrary data).

* If the file size does not fit in a single STORE DATA command, set P1 and P2 appropriately and divide it into multiple STORE DATA commands.
    * P2 is the block number (ascending number starting from 0).
    * Set P1.b8 to 1 only for the last STORE DATA command.
    * When using GlobalPlatformPro, the `--store-data-chunk` option can be used to set P1 and P2 appropriately. (For examples, see `tools/iotsafe_installkeypair_for_test.bat`)

---

### IoTSA_TLV_INS_READ_PUBLIC_KEY (0x7a)

---

* Reads the public key from the storage object specified by ID or label.

* This command can only be executed immediately after the IoTSA_TLV_INS_SELECT_OBJECT command. (The storage object must be in a selected state)

* The TLV Length for this command is 0, and there is no Value.
    * The payload of the STORE DATA command is always [0x7a, 0x00].

* The Response APDU contains the ECC public key in the format output by the [javacard.security.ECPublicKey#getW method](https://docs.oracle.com/javacard/3.0.5/api/javacard/security/ECPublicKey.html#getW(byte[],%20short)) (ANSI X9.62 compressed or uncompressed form).
    * The ECC public key obtained by this command can be converted to DER and PEM formats using OpenSSL commands like `tools/hex_to_ec_public_key.sh`.

---

### IoTSA_TLV_INS_SELECT_AND_READ_PUBLIC_KEY (0x7b)

---

* Reads the public key from the storage object specified by ID or label.

* This command is a shorthand for performing `IoTSA_TLV_INS_SELECT_OBJECT` and `IoTSA_TLV_INS_READ_PUBLIC_KEY` simultaneously.

* The TLV Value is one of the following TLVs.
    * Public key label (tag 75h)
    * Public key identifier (tag 85h)

---

### IoTSA_TLV_INS_SELECT_AND_READ_FILE

---

* Reads the file from the storage object specified by ID or label.
    * The intended use of this command is as follows.
        * To get a Certificate Signing Request (CSR) from the server and save it to the applet.

* Only when P2=0, the TLV Value is one of the following TLVs. (Specifies the label or id of the file to be read.)
    * File label (tag 73h)
    * File identifier (tag 83h)

* When P2>0, the TLV Value is empty (Length=0).

* STORE DATA command can be issued in a sequence to read a file that does not fit in the Response APDU.
    * P2 is the block number (starting from 0 in ascending order).
    * For P2=0, the STORE DATA command responds with the beginning part of the file in the Response APDU.
    * For P2=1,2,3.., the STORE DATA command responds with the continuation of the file that was previously read in the previous STORE DATA command.
    * If the length of the Data field in the Response APDU is 0, it indicates that the end of the file has been reached.

* Below is an example of command execution.

```
# INSTALL [for personalization] APDU command and response (SW=9000)
>> 84E62000 16 000010A0000001157000000000000049534102000000 00
<< 9000

# STORE DATA command, P2=0
#  It contains a command 'IoTSA_TLV_INS_SELECT_AND_READ_FILE'(0x7E) to read
#  a file with label="CertificateSigningRequest".
>> 80E20100 1D 7E1B731943657274696669636174655369676E696E6752657175657374 00

# Response APDU (248 bytes, SW=9000). It reads first 248 bytes of the file.
<< 3082016F308201160201003081B53131302F06035504030C286465766963653132332E67726F75703435362E74656E616E743738392E6578616D706C652E636F6D3137303506092A864886F70D01090116286465766963653132332E67726F75703435362E74656E616E74373839406578616D706C652E636F6D31163014060355040B0C0D4954204465706172746D656E7431123010060355040A0C096D79436F6D70616E79310E300C06035504080C05546F6B796F310B3009060355040613024A503059301306072A8648CE3D020106082A8648CE3D0301070342000495F233F86AF6B8970D7BF559E9C94B6556C8E97F3A00D11D5457 9000

# STORE DATA command, P2=1, with no data (Lc=0)
>> 84E20101 00 00

# Response APDU (123 bytes, SW=9000). It reads following 123 bytes of the file.
<< C0C1329F6AC3535CC47EBA50E7CB880AF02568AFE82F7B09499D882A38F0FB249BE7F644D2B5300A06082A8648CE3D040302034700304402205400DE5693ACEFD18CFF8312758BE7AD49AF67F5B2EA0EED804A6E4A247F0B44022061F6951CD84C501D71D06E68AABD68EDAE6AC2B853213C33C3E9FB3B64698B39 9000

# STORE DATA command, P2=2, with no data (Lc=0)
>> 84E20102 00 00

# Response APDU (0 bytes, SW=9000). It indicates that there is no more data to be read from the file.
<< 9000

```

---

### IoTSA_TLV_INS_CREATE_ECC_KEYPAIR

---

* This command is used for the following two purposes:
    * Create a storage location for the private and public keys on the applet and generate random keys (On-board key generation)
    * Create a storage object for key pairs used by the Generate Key Pair command (GSMA IoT.05 Clause 2.11)

* The TLV Value contains a sequence of the following TLVs.

    | Tag                          | Length | Value             |
    | ---------------------------- | ------ | ----------------- |
    | Private key label (74h)      |        | Label (optional)  |
    | Private key identifier (84h) |        | ID                |
    | Public key label (75h)       |        | Label (optional)  |
    | Public key identifier (85h)  |        | ID                |
    | Key type (48h)               | 1      | Key type          |

* The values for key type are as follows.
    * 13h (NIST secp256r1 (persistent)) or 23h (BrainpoolP256r1 (persistent))
    * 14h (NIST secp256r1 (volatile)) or 24h (BrainpoolP256r1 (volatile))
        * For On-board key generation, specify persistent (13h or 23h).
        * When creating a key pair for the Generate Key Pair command, specify volatile (14h or 24h).

---

### IoTSA_TLV_INS_GENERATE_CSR

---

* Generates a Certificate Signing Request (CSR) for the key pair corresponding to the private key and public key.
    * The generated CSR is written to the file.

* The Subject and Attributes are passed as parameters to this command.
    * The private key signs the CSR with the public key.

* The label and ID of the storage file are fixed. (label="CertificateSigningRequest", id=0x80000000 )

* This command creates the file if it does not exist at the time of execution.
    * If the storage file already exists, the content of the file is overwritten with the generated CSR.

* The TLV Value contains the following TLVs.

    | Tag                          | Length | Value                           |          |
    | ---------------------------- | ------ | ------------------------------- | -------  |
    | Private key label (74h)      |        | label                           | (Note 1) |
    | Private key identifier (84h) |        | ID                              | (Note 1) |
    | Subject (50h)                |        | X.509 Name in form of ASN.1 DER | (Note 2) |
    | Attributes (51h)             |        | Attributes (optional)           | (Note 3) |

    Notes

    1. Specify either a label or ID.
       Create a CSR using the public key corresponding to the specified private key, and sign the CSR with the private key.

    2. The subject of the CSR. For more information, refer to RFC2986, ITU-T Recommendation X.501, etc.

       Creating the Subject is recommended to be done using existing software libraries.

    3. The attributes of the CSR. If not specified, a CSR without Attributes is generated.

       For more information, refer to RFC2986, etc.
       Attributes must start with "Context-Specific tag[0]" in ASN.1 DER, so the first byte of Attributes must be 0xA0.

       Creating Attributes is recommended to be done using existing software libraries.

* If the length of the TLV Value exceeds 248 bytes, the command can be divided into multiple STORE DATA commands.
* The length of the TLV Value is up to 766 bytes.

---

### IoTSA_TLV_INS_DELETE_OBJECT

---

* Deletes the storage object specified by ID or label.
* The TLV Value is one of the following TLVs.
    * File label (tag 73h)
    * Private key label (tag 74h)
    * Public key label (tag 75h)
    * File ID (tag 83h)
    * Private key identifier (tag 84h)
    * Public key identifier (tag 85h)

---

### IoTSA_TLV_INS_DEBUG (0x70)

---

* This command has the following functions.
    * Toggles debug mode ON/OFF.
        * When debug mode is ON, the exception type is recorded in `byte[] debugState` when an exception occurs within the applet.
    * Reads the value of debugState.
        * The content of `byte[] debugState` is dumped in the Response APDU of this command.

* Based on the payload content of the STORE DATA command, the following actions are performed.
    * `70 01 00` Turn off debug mode
    * `70 01 01` Turn on debug mode
    * `70 00` Read the value of `byte[] debugState`

---

### IoTSA_TLV_INS_RESET_APPLET_STORE (0x7f)

---

* Erases all provisioned keys, etc., and returns the applet to the state immediately after installation. This can be used for repeated debugging and testing with the key management server, etc.

* Based on the payload content of the STORE DATA command, the following actions are performed.
    * [ `0x7f, 0x08, 'c', 'l', 'e', 'a', 'r', 'a', 'l', 'l',` ] → Erase all keys, etc., and return the applet to the state immediately after installation.

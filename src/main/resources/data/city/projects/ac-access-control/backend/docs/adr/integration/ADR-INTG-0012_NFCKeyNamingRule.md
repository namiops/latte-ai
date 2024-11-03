# ADR-INTG-0012 NFC Key Naming Rule

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2024-06-26   |

## Context and Problem Statement

- Define the naming convention for NFC Keys to be stored in the `Vault`.
- There are two types of keys: `felica-key` and `mutual-authn-key`.
  - The `felica-key` must have a key for each `serviceCode` and `keyType` information.
    - The `serviceCode` is a code expressed in 4-digit Hexdecimal.
    - `keyType` can be either `AES` or `DES`.
      - If keyType is `AES`, it has only 32 bytes of `groupKey` information.
      - If keyType is `DES`, it has a 16-byte `groupKey` and a 16-byte `userKey`
  - `mutual-authn-key` manages different keys for different `version` number.
    - `mutual-authn-key` consists of three keys: `k1`, `k2`, `k3`

- See [this document](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/security/ADR-SEC-0005_SecretsOfNFCReaderWriter.md) for more information on each key

---

## Decision Outcome

- The `felica-key` is stored in the following path in `Vault`.

  `ac-access-control/nfc-felica-key`

  - The secret name will be as follows
    - Case of `keyType` is `AES`
      `nfc-key-{serviceCode}-AES`

      - Example:
        Case of serviceCode is 100A,
        `nfc-key-100A-AES`

    - Case of `keyType` is `DES`
      `nfc-key-{serviceCode}-DES-{serviceKeyType}`

      `serviceKeyType` is `group` or `user`.
      `group` is `groupKey`, `user` is `userKey`

      - Example:
        Case of `serviceCode` is 100A
        - `groupKey`'s secret name
          `nfc-key-100A-DES-group`
        - `userKey`'s secret name
          `nfc-key-100A-DES-user`

- The `mutual-authn-key` is stored in the following path in `vault`.

  `ac-access-control/nfc-mutual-auth-key`

  - The secret name will be as follows
    `nfc-key-{version}-ka1`
    `nfc-key-{version}-ka2`
    `nfc-key-{version}-ka3`

    - Example:
      Case of `version` is 1
      `nfc-key-1-ka1`
      `nfc-key-1-ka2`
      `nfc-key-1-ka3`

## Consequence

The settings to be retrieved from the vault are described in [this yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/auth/api-server/k8s/dev/deployment.yaml).
Defined the key name to include `-` (example nfc-key-100A-AES), however, be careful when using agent-inject-template to retrieve the value.
Since a simply stating it as is customary will result in an error, [this](https://github.com/wp-wcm/city/pull/31047) measures were implemented.

---

## Note

- 2024-06-26 : Drafted

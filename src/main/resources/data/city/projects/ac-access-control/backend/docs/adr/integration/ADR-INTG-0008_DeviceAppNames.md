# ADR-INTG-0008 Device App Names

| Status | Last Updated |
|---|---|
|Approved| 2023-08-25 |

## Context and Problem Statement

- Define **DeviceAppName** for an application of each device on each configuration.
- **DeviceAppName** is used on...
  
  - Logs outputted in device apps.

    ```json
        {
            "Timestamp": "1586960586000000000",
            "Resource": {
                "service.name": "{deviceAppName}", // <- here
                "service.version": "{deviceAppVersion}",
                "service.instance.id": "{groupName}.{deviceName}"
            },
            // :
            // :
        }
    ```

  - Configuration block of **"{groupName}/{deviceName}/shadow/delta"** (or /reported).

    ```json
    {
    "configuration": {
        "application": {
        "{deviceAppName}": {  // <- here
            "url": "https://storage/config/common/config-v1.0.0",
            "version": "1.0.0",
            "canStartFromMs": 1234567890123
        }
        },
        // :
    }
    }
    ```

---

## Considered Options

- Give specific name to each combination of **Device Type** and **Configuration**.
- It's same idea as **IoTA Group Name** documented in [the ADR:ADR-INTG-0005 Naming Rule of IoTA Group Name](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0005_IoTAGroupNameNamingRule.md)

---

## Decision Outcome

- Use **IoTA Group Name** as **`DeviceAppName`**. see [the table of this section](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0005_IoTAGroupNameNamingRule.md#decision-outcome).

---

## Note

- 2023-08-25 : Approved
- 2023-08-25 : Drafted, Originator: Name

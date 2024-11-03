# ADR-INTG-0006 Logging Device Logs

| Status | Last Updated |
|---|---|
|Approved| 2023-11-22 |

## Context and Problem Statement

- How to log application logs of devices (Authenticator, NFC Controller etc).
- Need to follow [Logging ADR](https://docs.google.com/document/d/13cBIn_ZjNeSfgvya5reRQB1h6fLRIyb1hksVZyqW5zc/edit#heading=h.km3hken3nrpj) from IA team, that means logs MUST be stored into Agora's Log Storage.

---

## Considered Options

- Use [the logging feature provided by IoTA](https://docs.google.com/document/d/1Klve81-4ktPK7-WLa1BHRjFKTG16hmgMtGDq6AFPEFE/edit) team. You can see QAs on [this Slack thread](https://woven-by-toyota.slack.com/archives/C042AQ2TU4A/p1690163591832959).
- Implement a logging feature by ourselves.

---

## Decision Outcome

- `Use the logging feature provided by IoTA team.`
  - Use MQTT publishing to output logs.
  - The logging dedicated topic name is **`<deviceGroupName>/<deviceName>/logs`**. See the [IoTA team document](https://developer.woven-city.toyota/docs/default/Component/iota-service/03_devicelog/) to get the latest topic name.

### Reason

- No extra cost to our team, not only on development but also on maintenance and operation.
- We don't have special requirements to the logging feature. It's enough to pass logs from devices to Agora's log-collector.

### Log Format

- Follow [Logging ADR Standard Log Format](https://docs.google.com/document/d/13cBIn_ZjNeSfgvya5reRQB1h6fLRIyb1hksVZyqW5zc/edit#bookmark=id.mtqqwq21mezo).

Example:

```json
{
    "Timestamp": "1586960586000000000",
    "Resource": {
        "service.name": "{deviceAppName}",
        "service.version": "{deviceAppVersion}",
        "service.instance.id": "{groupName}.{deviceName}"
    },
    "SeverityText": "INFO",
    "Body": {
        "text": "message content",
        // add belows if necessary
        "command": "reboot",
        "commandState": "command-received",
        "messageId": "messageId"
    },
    "Attributes": {
        // add attributes if necessary
    }
}
```

Currently, multiple logs in one message as an array is **NOT** supported via MQTT topic (supported only via HTTP logging API).

```json
// This format is NOT supported via MQTT logging topic
[
    {
        "Timestamp": "1586960586000000000",
        "Resource": {
            "service.name": "{deviceAppName}",
            "service.version": "{deviceAppVersion}",
            "service.instance.id": "{groupName}.{deviceName}"
        },
        "SeverityText": "INFO",
        "Body": {
            "text": "message content",
            // add belows if necessary
            "command": "reboot",
            "commandState": "command-received",
            "messageId": "messageId"
        },
        "Attributes": {
            // add attributes if necessary
        },
    },
    {
        // another log
    },
    :
    :
]
```

### How does a log look like on Grafana

The log collector provided by the IoTA team translates OTEL format log to Loki's format.

When a device send a log like below

```json
    {
        "Timestamp": "1586960586000000000",
        "Resource": {
            "service.name": "{deviceAppName}",
            "service.version": "{deviceAppVersion}",
            "service.instance.id": "{groupName}.{deviceName}"
        },
        "SeverityText": "INFO",
        "Body": {
            "text": "message content",
            // add belows if necessary
            "command": "reboot",
            "commandState": "command-received",
            "messageId": "messageId"
        },
        "Attributes": {
            // add attributes if necessary
        },
    }
```

The log on Grafana will look like

![Log on Grafana](./img/ADR-INTG-0006-img-01.png)

### Grafana Dashboard for ac-access-control-host Device Logs

You can see the dedicated dashboard for device logging from [this link](https://observability.cityos-dev.woven-planet.tech/grafana/d/cad09aabc3dc8b33681760782b98f272103ffa80/iota-device-logs?orgId=1&refresh=5s&from=1700435118169&to=1700607918169) (VPN connection required).

---

## Consequences

- Currently, collected logs are converted Loki's format (eg. severityText -> level). The IA team will consider how that conversion should be.

---

## Note

- 2023-11-22 : Updated about IoTA Device Logging
  - Updated the Device Logging topic name.
  - Mentioned that multiple logs in one message is not supported.
  - Added Grafana dashboard link.
- 2023-07-28 : Approved
- 2023-07-26 : Drafted, Originator: Kohta Natori

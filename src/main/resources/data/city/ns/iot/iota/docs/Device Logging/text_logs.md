# Text Logs

With this feature, your provisioned device can publish on a dedicated MQTT topic or via REST API. You can check the logs in the dedicated [Observability Dashboard](https://observability.cityos-dev.woven-planet.tech/grafana/d/569ef518df53e079e43c60b6f2fa95b00fd20b0b/log-collector-logs-per-tenant?orgId=1&from=now-7d&to=now&var-namespace=serverless&var-source=All&var-mystream=All&refresh=5s) (change the source to match your tenant name).


## Overview component
![Device logging diagram](diagrams/device-log.png)
[Lucid link](https://lucid.app/lucidchart/a4ef6d5e-b6af-401e-8c2a-adf291211460/edit?viewport_loc=-370%2C0%2C2333%2C802%2C0_0&invitationId=inv_81cbd3a4-ea18-4e07-9967-2fb132840048)

The device log component consists of 2 endpoints
    * REST API
    * MQTT messaging

## Why use this feature
Device logs and metrics collection are an important part of observability. Even if physically running outside a cluster, devices are still part of the overall system we want to observe and might generate relevant logs, both for diagnostics and business logic.


## Usage
### Sending logs via MQTT
#### Prerequisite
* You are onboard and have registered tenant
* Your devices are added and provisioned and have acquired the broker's secret

#### Sample payload
Sample log in open telemetry log format:
```
    {
        "timestamp": "%d",
        "resource": {
            "service.name": "test-service",
            "service.version": "1.0.0",
            "service.instance.id": "test-service-123"
        },
        "severityText": "INFO",
        "body": {
            "text": "message content",
            "command": "reboot",
            "commandState": "command-received",
            "messageId": "messageId"
        },
        "attributes": {
            "test.attribute": "test attribute"
        },
        "span_id": "dGVzdAo=",
        "trace_id": "dGVzdDIK",
        "scope_name": "test scope",
        "trace_flags": "dGVzdDIzNDU2Cg=="
    }
```

or

Custom format:
```
    {
        "severityText": "INFO",
        "log": "Log in custom format",
    }
```

And send it to the device log dedicated topic

```
<DeviceGroupName>/<DeviceName>/logs
```

For example, if your group is `autobots` and your device is `optimus-prime` your dedicated topic will be `autobots/optimus-prime/logs`

#### Sample code
Please see the [sample code](https://github.com/wp-wcm/city/tree/main/ns/iot/demo/devicelog) for the MQTT client and how to send MQTT messages.

### Sending logs via HTTP endpoint
The log can also be sent via HTTP endpoint. Please see the guide in [Telemetry Collector lambda](https://developer.woven-city.toyota/docs/default/component/telemetry-collector)

### Viewing logs
The log can be viewed in Grafana's Observability dashboard [lab](https://observability.agora-lab.woven-planet.tech/grafana/d/569ef518df53e079e43c60b6f2fa95b00fd20b0b/log-collector-logs-per-tenant?orgId=1&refresh=5s&var-namespace=serverless&var-source=test&var-mystream=All) | [dev](https://observability.cityos-dev.woven-planet.tech/grafana/d/569ef518df53e079e43c60b6f2fa95b00fd20b0b/log-collector-logs-per-tenant?orgId=1&from=now-7d&to=now&var-namespace=serverless&var-source=All&var-mystream=All&refresh=5s)

You can filter your tenant by selecting the `source` to match your tenant's name.

## Reference
This document describes the current implementation. For the concept and background of device logging please refer to [TN-0329 Device Logging and Metrics Collection](https://docs.google.com/document/d/1Klve81-4ktPK7-WLa1BHRjFKTG16hmgMtGDq6AFPEFE/edit#heading=h.5qm13wuvtiz9)

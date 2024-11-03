# ADR-BTS-0002 IoTA Broker Permission

| Status  | Last Updated |
|---------|--------------|
| Drafted | 2024-08-09   |

## Context and Problem Statement

We need to configure the broker permissions as per the guidelines provided in
the [Broker Permissions documentation](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/broker_permissions/#broker-permissions).

Currently, there are two distinct topics that need to be separated into two groups:

1. A topic where the Amano server sends state information about Cargate.
2. A topic where BCG aggregates information about Cargate and sends it to e-Palette (ad-center).

## Decision Outcome

We will configure the broker permissions as follows:

### 1. Amano Server to Cargate State Information

#### group=cargate

##### device=amano-server

##### Permission

```json
{
  "topic": {
    "read": "{group}/{device}/#",
    "write": "{group}/{device}/#|real/dt/{group}/+/status"
  }
}
```

##### MQTT Topic for Cargate State

| Name                                                                                                                   | Topic Name                                                                      | Reader           | Writer       |
|------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------|------------------|--------------|
| [car gate state](https://github.com/wp-wcm/city/blob/main/projects/backend-car-gate/docs/interface/car_gate_status.md) | old: real/data/cargate/{gateId}/status<br/>new: real/dt/cargate/{gateId}/status | BCG pod on agora | Amano server |

### 2. BCG to e-Palette Aggregated CarGate State Information

#### group={env}

e.g. VCM01

**Note:** BTS and BCG may use the same broker connection information (certificate) to distribute to e-Palette(
ad-center). Therefore, permissions for BCG need to be added to the group
for [BTS](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/adr/ADR-BTS-0007_IoTABrokerPermission.md#permission).
However, to avoid confusion, permissions are described here limited to BCG.

##### device=ad-center-epalette

##### Permission

```json
{
  "topic": {
    "read": "{group}/{device}/#|real/dt/cargate/aggregated/status",
    "write": "{group}/{device}/#"
  }
}
```

##### MQTT Topic for aggregated car gate state

| Name                                                                                                                                         | Topic Name                                                                          | Reader           | Writer           |
|----------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|------------------|------------------|
| [aggregated car gate state](https://github.com/wp-wcm/city/blob/main/projects/backend-car-gate/docs/interface/aggregated_car_gate_status.md) | old: real/data/cargate/aggregated/status<br/>new: real/dt/cargate/aggregated/status | AD-Center Server | BCG pod on agora |

## Note

- 2024-08-09 : Drafted, Originator: Katsuhiro Yuzawa

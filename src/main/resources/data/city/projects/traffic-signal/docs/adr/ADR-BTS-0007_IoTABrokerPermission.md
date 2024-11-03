# ADR-BTS-0007 IoTA Broker Permission

| Status  | Last Updated |
|---------|--------------|
| Drafted | 2024-08-06   |

## Context and Problem Statement

According to
the [security requirements](https://docs.google.com/document/d/1diqReRsCOaGb5y3-E_lv8KcswMMlNXJEmmVJFLnPl7o/edit), the
following requirements must be met:

- TH.TSS.005: The MQTT topics MUST be separated per each intersection to prevent devices from publishing data outside of
  their own environment.
- TH.TSS.006: Rights to send/publish data on the MQTT topics MUST be assigned following the least privilege principle (
  e.g. components that only need to subscribe cannot publish).

We need to configure the broker permissions as per the guidelines provided in
the [Broker Permissions documentation](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/broker_permissions/#broker-permissions).

There are two main categories of MQTT topics:

1. Topics published by vehicles (e-palette, guide-mobi) and subscribed by vehicles.
2. Topics published by traffic signals and subscribed by traffic signals.

For each category, we need to configure two groups.

**Note:** For the vehicle-related topics, changing existing topic names would require coordination with stakeholders and
incur additional costs, so it was decided not to pursue this option.

**Additional Note:** IoTA is registered with a hierarchy of tenant > group > device, and permissions can be set for each
group.
See [Using iotactl](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/broker_permissions/#using-iotactl)
for more detail.

## Considered Options

1. Separate MQTT topics for vehicles and traffic signals as per security requirements.
2. Assign permissions based on least privilege principle without changing existing vehicle-related topic names.

## Decision Outcome

We will configure the broker permissions as follows:

### 1. Vehicle-related Topics

**Note:** The topic name may be changed after the e-Palette team completes their consideration.

#### For e-Palette, group={ec2-instance-name}

e.g. VWC01

##### device=ad-center-epalette

##### Permission

```json
{
  "topic": {
    "read": "{group}/{device}/#|real/dt/traffic-light",
    "write": "{group}/{device}/#|{group}/dt/vehicle/+/location|{group}/dt/vehicle/+/route-info"
  }
}
```

##### MQTT Topic for e-Palette related info

| Name                                                                                                                             | Topic Name                                             | Reader           | Writer           |
|----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------|------------------|------------------|
| [100001_vehicle_location](https://github.tri-ad.tech/WCM-B-and-S/e-Palette_interface/blob/main/100001_vehicle_location.md) (TBU) | {ec2-instance-name}/dt/vehicle/{vehicle-id}/location   | bts pod on agora | ad-center server |
| [100007_route_info](https://github.tri-ad.tech/WCM-B-and-S/e-Palette_interface/blob/main/100007_route_info.md) (TBU)             | {ec2-instance-name}/dt/vehicle/{vehicle-id}/route-info | bts pod on agora | ad-center server |

##### MQTT Topic for traffic light related info

| Name                                                                                                                                   | Topic Name                           | Reader           | Writer           |
|----------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|------------------|------------------|
| [100006_trafficlight_status](https://github.tri-ad.tech/WCM-B-and-S/e-Palette_interface/blob/main/100006_trafficlight_status.md) (TBU) | {env}/dt/traffic-light <br/>e.g. real/dt/traffic-light | ad-center server | bts pod on agora |

#### For guide mobi, group={ec2-instance-name}

e.g. GM01

##### device=ad-center-guide-mobi

##### Permission

```json
{
  "topic": {
    "read": "{group}/{device}/#|real/dt/traffic-light",
    "write": "{group}/{device}/#|{group}/dt/vehicle/+/location"
  }
}
```

##### MQTT Topic for guide mobi related info

| Name                                                                                                                                          | Topic Name                                           | Reader           | Writer           |
|-----------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------|------------------|------------------|
| [vehicle location](https://docs.google.com/presentation/d/1y9P8FhKkoh-kdt-l9dyIlAZEV50U2zzUs5Tz-FDD_3U/edit#slide=id.g26ca7bb7202_0_11) (TBU) | {ec2-instance-name}/dt/vehicle/{vehicle-id}/location | bts pod on agora | ad-center server |

##### MQTT Topic for traffic light related info

| Name                                                                                                                                   | Topic Name                           | Reader           | Writer           |
|----------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|------------------|------------------|
| [100006_trafficlight_status](https://github.tri-ad.tech/WCM-B-and-S/e-Palette_interface/blob/main/100006_trafficlight_status.md) (TBU) | {env}/dt/traffic-light <br/>e.g. real/dt/traffic-light| ad-center server | bts pod on agora |

### 2. Traffic Signal-related Topics

#### For I2V, group=i2v

##### device=i2v-intersection-{i}, 1 <= i <=5

e.g. i2v-intersection-1

##### Permission

```json
{
  "topic": {
    "read": "{group}/{device}/#",
    // for OTEL logs, traces and metrics, as well as other IoTA features(Device Shadow or Xenia)
    "write": "{group}/{device}/#|real/dt/traffic-light/{group}/{device}/status"
  }
}
```

##### MQTT Topic for Kei-B signal related info

| Name              | Topic Name                                                                                                                                                         | Reader           | Writer        |
|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------|---------------|
| Kei-B signal info | old: fromTs/intersection{intersectionID}<br/>new: {env}/dt/{application}/{group}/{device}/{data-type}<br/>e.g. real/dt/traffic-light/i2v/i2v-intersection-1/status | bts pod on agora | miniPC device |

#### For TrafficSignalConverter(TSC), group=tsc

##### device=tsc-intersection-{i}, 1 <= i <=5

e.g. tsc-intersection-1

##### Permission

```json
{
  "topic": {
    "read": "{group}/{device}/#|real/cmd/traffic-light/{group}/{device}/green-light",
    "write": "{group}/{device}/#"
    // for OTEL logs, traces and metrics, as well as other IoTA features(Device Shadow or Xenia)
  }
}
```

##### MQTT Topic for traffic light command

| Name                | Topic Name                                                                                                                                                                | Reader        | Writer           |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|------------------|
| Green light request | old: fromTs/intersection{intersection ID}<br/>new: {env}/cmd/{application}/{group}/{device}/{req-type}<br/>e.g. real/cmd/traffic-light/tsc/tsc-intersection-1/green-light | miniPC device | bts pod on agora |

## Supplementary Notes

Below is an overview of the topic name changes that have occurred. The changes are summarized in the table below.

### Topic Name Changes

| Name                                                                                                                                                  | Old Topic Name                                           | New Topic Name                                                                              |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------|---------------------------------------------------------------------------------------------|
| [100006_trafficlight_status](https://github.tri-ad.tech/WCM-B-and-S/e-Palette_interface/blob/main/100006_trafficlight_status.md)                      | {ec2-instance-name}/data/trafficlight                    | {ec2-instance-name}/dt/traffic-light (TBD)                                                  |
| [100007_route_info](https://github.tri-ad.tech/WCM-B-and-S/e-Palette_interface/blob/main/100007_route_info.md)                                        | {ec2-instance-name}/data/vehicle/{vehicle-id}/route_info | {ec2-instance-name}/dt/vehicle/{vehicle-id}/route-info (TBD)                                |
| [green_light_request_real](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/green_light_request_real.md)               | fromBts/intersection{intersection ID}                    | real/cmd/traffic-light/tsc/tsc-intersection-1/green-light                                   |
| [green_light_request_simulation](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/green_light_request_simulation.md)   | dtp/intersection/{intersection ID}/green                 | sim/cmd/traffic-light/tsc/tsc-intersection-1/green-light                                    |
| [traffic_light_status_real](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/traffic_light_status_real.md)             | fromTs/intersection{intersection ID}                     | real/dt/traffic-light/i2v/i2v-intersection-1/status                                         |
| [traffic_light_status_simulation](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/traffic_light_status_simulation.md) | fromDtp/intersection                                     | sim/dt/traffic-light/i2v/i2v-intersection-1/status (TBD, it needs payload structure change) |

## Note

- 2024-08-06 : Drafted, Originator: Katsuhiro Yuzawa

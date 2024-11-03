# ADR-BTS-0006 Logging

| Status | Last Updated |
|---|---|
|approved| 2024-05-30 |

## Purpose of this document

We describe here what logs this app outputs.


## Decision Outcome


### Log Format

- Follow [Logging ADR Standard Log Format](https://docs.google.com/document/d/13cBIn_ZjNeSfgvya5reRQB1h6fLRIyb1hksVZyqW5zc/edit#bookmark=id.mtqqwq21mezo).

Example:

```json
{
    "Timestamp": "1586960586000000000",
    "Resource": {
        "service.name": "{AppName}",
        "service.version": "{AppVersion}",
    },
    "SeverityText": "INFO",
    "Body": "[{Tag}][{methodName}] {comment}",
    "Attributes": {
        // add attributes if necessary
    }
}

```


```json
{
    "Timestamp": "1586960586000000000",
    "Resource": {
        "service.name": "Backend-of-Traffic-Signal",
        "service.version": "0.0.1",
    },
    "SeverityText": "INFO",
    "Body": "[INITIALIZE][main()] app has been launched.",
    "Attributes": {
        // add attributes if necessary
    }
}
```
### Log output trigger

|Trigger|json - severityText|Tag|json - text|json - Attributes|
|--|--|--|--|--|
|When the boot process is completed.|INFO|INITIALIZE|app has been launched.|-|
|When the initialization process is initiated.|INFO|INITIALIZE|Start|-|
|When the initialization process is completed.|INFO|INITIALIZE|Completed|-|
|When configuration value are set.|INFO|INITIALIZE| env {key} = {value}|-|
|When MQTT connection is successful.|INFO|MQTT|{mqtt broker endpoint} is now successfully connected.|-|
|When publishing MQTT message.|INFO|MQTT|Published MQTT message.|message|
|When subscribing MQTT message.|INFO|MQTT|Subscribed MQTT message.|message|
|When traffic signal information held internally is updated (2Hz/1 intersection).|INFO|GREENLIGHT|Traffic signal information held internally is updated. {traffic signal information(*1)}|-|
|When Vehicle information is subscribed.|INFO|GREENLIGHT|Vehicle Info. (X,Y):({locationX},{locationY}) speed[mps]:{SpeedMetersPerSecond} stoppingTime[ms]:{StoppingTime} IntersectionIdsToBePassed:{IntersectionIdsToBePassed}|-|
|When a change green light request is triggered.|INFO|GREENLIGHT|ChangeGreenLightRequest is triggered. {algorithmTracker(*2)}|-|
|When a extend green light request is triggered.|INFO|GREENLIGHT|ExtendGreenLightRequest is triggered. {algorithmTracker(*2)}|-|
|When a retained Route Info are updated.|INFO|GREENLIGHT|Update the retained Route Info for routes[{RouteKey}]={[]IntersectionsToBePassed}. Now Route info for {[]RouteKeys} are retained.|-|
|When a green light request is not triggered.|DEBUG|GREENLIGHT|Not triggered. {algorithmTracker(*2)}|-|
|When the intersection is not listed in intersectionToBePassed.|DEBUG|GREENLIGHT|is not scoped intersection.  {algorithmTracker(*2)}|-|
|When the errors is occurred.|ERROR|{depends on occurred point}|{error details}|-|
|When the warning is occurred.|WARN|{depends on occurred point}|{warning details}|-|

- *1: traffic signal information has following information.
  - IntersectionID, ServiceApproachID, VehicleLightColor, remainingTimeMax,  remainingTimeMin, hasChanged, hasExtended
- *2: algorithmTracker is struct domain.AlgorithmTracker.

---

## Note


- 2023-03-28 : Add following 3 triggers, `When Vehicle information is subscribed` and `When a green light request is not triggered` and `When the intersection is not listed in intersectionToBePassed`.
- 2023-03-19 : Approved
- 2024-03-14 : Drafted, Originator: Yuchi Takahashi
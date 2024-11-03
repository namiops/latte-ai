# ADR-INTG-0011 Collecting Device Apps' Metrics

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2024-01-09   |

## Context and Problem Statement

To monitor whether device apps(Authenticator App, NFCC App, Elevator Access Controller App) work as expected(designed) and the device works healthy, collecting some numeric data like below is useful.

1. **Device apps runtime(process) status**

    eg.

    - Memory Usage
    - Thread Count

2. **The device itself status**

    eg.

    - Overall CPU usage
    - Free memory amount
    - Free storage space
    - Device temperature (if possible)

3. **Arguments or results of device apps' specific features**

    eg.

    - Round trip time of authn/authz request
    - Face size when the app starts authentication
    - Consuming time of app's each status (excepts status continuing long time, like Standby, KeepOpening etc.)

In this ADR, determines **how to collect and storage these data(metrics) from device apps and visualize it**.

---

## Considered Options

### Approach 1. Using IoTA Device Logging

- Pros
  - No extra implementation for new mechanism is needed. Just adding log messages contains metrics.

- Cons
  - Hard to extract and visualize the data using Grafana from the logs. It's not impossible but might be very tricky and is not recommended, especially for statistical analysis.
  - Collected device logs will turn over in some period. That means we can not keep metrics for desired period.

### Approach 2. Using [IoTA Metrics](https://developer.woven-city.toyota/docs/default/Component/iota-service/metrics/)

IoTA provides the feature which can collect device metrics via the dedicated MQTT topic (see the link above for details).

- Pros
  - It's a dedicated feature for collecting time-series metrics. We can visualize the collected data with Grafana easily and do some statistic analysis, also can set alerts with a threshold.
  - Need some extra implementation but it's only sending JSON data containing metrics to the dedicated MQTT topic like `{group}/{device}/telemetry`.

- Cons
  - Need to configure metrics mapping as a yaml file on the city repository. That means opening PR and merging are needed on each configuration changes.

---

## Decision Outcome

Approach 2. **Using IoTA Metrics**.

### Reason

- Collecting metrics as numeric data is more reasonable rather than collecting these as log strings then extract later. We can use Grafana statistic functions to analyze.
- Metrics data should be stored longer than logs for long-term analysis.

---

## Consequences

- IoTA Metrics was released recently. It's better to try it briefly before implementing.
- Need to define collecting concrete metrics.

---

## Note

- 2024-01-09 : Drafted, Originator: Kohta Natori

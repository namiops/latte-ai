# ADR-PREFIX-0004 Detection brute force attack, and notification to access control monitoring system

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2024-05-22   |

## Context and Problem Statement

- Consider detection and notification method to described in [SiteGate Security Requirements Status ID.4](https://docs.google.com/spreadsheets/d/1M9Xr8T7dyR6ucnlPlYrRmHhNXKnpBW214bWepPo75Kk/edit#gid=1069062888&range=B9)

- `SiteGate Security Requirements Status ID.4` states...
  - The system MUST be able to detect and report brute forcing (several failed attempts per time unit).
  - A security guard MUST be informed if a suspected brute force attack is detected, so physical verification can take place.

- Note.
  - This ADR basically consider to cases of an abnormal number of authentication requests come in within a unit time from correctly registration devices.
  - Attacks from unauthorized devices are not described this document.
    - If a large number of requests are received from spoofed devices, they will be rejected by mTLS certificate authentication.
    - If a request is received from a device that has a valid certificate but is not registered, an alert is reported at that point.

---

## Scope

[REST API](../../api/auth_http.yaml)s for authentication from devices to the backend, listed below

- `/auth/door/authz`
- `/auth/site-gate/authz`
- `/auth/elevator/allow-access`
- `/auth/private-home/authz`

---

## Consideration Points

- Criteria for judging a brute force attack  
- How to detect the number of requests per time unit  
- How to notify the access control monitoring system  

---

## Considered Options

Manage the state of detection in progress?

- Error is reported if the number of requests exceeding the threshold is detected each unit time
- Detect whether a brute force attack is still being detected or whether it is getting out of that state

---

## Decision Outcome

### About management of detection status

No need to manage detection status.
Only report an error if a request exceeding the threshold is detected every unit of time.

- If an error occurs, physical confirmation by security gurards will be performed, so it is unnecessary to report that the attack stopped.

### Criteria for judgement a brute force attack

- To identify where brute forcing is taking place for physical verification, check threshold number of request each requester(combination of deviceID, and authentication method) per time unit. If the threshold is exceeded, it is recognized that a brute force attack.
- The appropriate number of requests per unit of time is that the response time to a request depends on the authentication method (face or NFC) and therefore has a different threshold for each authentication method.
- The actual threshold is T.B.D. (unit time is also T.B.D for now, but assume by seconds)

---

### How to detection the number of requests per time unit

- Use Redis to store the number of requests.
- A string combining the deviceID, authentication method is used as the key. This key is [sorted set](https://redis.io/docs/latest/develop/data-types/sorted-sets/) values.
  - Key: `requestTimestamp:{deviceID}:{authMethod}`, e.g. requestTimestamp:some-device-id:face
  - Value: All timestamp(ms) values received within a unit of time. Both `score` and `value` should be set to a timestamp value.

- When a request is received, [ZADD](https://redis.io/docs/latest/commands/zadd/) to add timestamp on receipt.
- Obtains the number of timestamps registered in the range from the current time backward by a unit of time by [ZCOUNT](https://redis.io/docs/latest/commands/zcount/) as request number in unit time.
- Obtain the oldest value of timestamps registered in the range of unit time backward from the current time by [ZRANGE](https://redis.io/docs/latest/commands/zrange/), and obtain the elapsed time from the current time.
- If a timestamp remains that exceeds the unit time from the current timestamp by [ZCOUNT](https://redis.io/docs/latest/commands/zcount/), delete all but the current received timestamp by [ZREMRANGEBYSCORE](https://redis.io/docs/latest/commands/zremrangebyscore/).(This means resetting the count every unit of time)

- If threshold equal request number, report as Brute Force Attack detection.(threshold is change each authenticationMethod)

---

### How to notify the attack to access control monitoring system

- The same flow of error detection and reporting is done by device event, and since Brute Force Attack detection is also done by device, we want to do the same.
- T.B.D. for its details.

---

#### Error log schema and stored location

The log is saved at the same time the error is reported.

##### Error Log schema

Store the following information

| Field name               | Type   | Description                                                                   |
| :----------------------- | :----- | :---------------------------------------------------------------------------- |
| +deviceId                | string | Device ID of the authenticator that sent the request.                         |
| +authenticationMethod    | string | Authentication method                                                         |
| +timestampMs             | number | Timestamp by millisecond when the detected or cancelled to brute force attack |
| +requestedCountThreshold | number | Threshold number of requests been used as reporting criteria                  |
| +unitTimeMs              | number | Time unit by millisecond for totalizing requests for reporting                |
| +timeToExceedMs          | number | Time elapsed by millisecond unit before the count value exceeds threshold     |

##### consider options of stored location

- Extend existing AC logs (stored by secureKVS).
- store them in another DB

If case of extend existing AC logs,
error logs are recorded as part of ac log. refered to `ac-log-service` in [this document](../../data_design/README.md).
Add the following logs to `ac-log-service`.

| Log Type                | Description                                 |
| :---------------------- | :------------------------------------------ |
| rate_limit_exceeded_log | logs stored when the rate limit is exceeded |

`logType` indicates the type of the document. You can use `logType` to filter by a document type.

The log is stored in json and looks like this.

```json
{
  "+logType": "rate_limit_exceeded_log",
  "+logContent": {
    "+deviceId": "xxxxxxxx",
    "+authenticationMethod": "face",
    "+timestampMs": 1234567890123,
    "+requestedCountThreshold": 30,
    "+unitTimeMs": 10,
    "+timeToExceedMs": 5678
  }
}
```

---

## Note

- 2024-05-16 : Drafted, Originator: Tomohiro Bessyo
- 2024-05-21 : Review conducted, [minutes](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=866047262)
- 2024-05-22 : Drafted after review, Originator: Tomohiro Bessyo

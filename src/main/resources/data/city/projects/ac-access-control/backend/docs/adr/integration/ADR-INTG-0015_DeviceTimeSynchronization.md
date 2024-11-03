# ADR-INTG-0015 Device Time Synchronization with Agora Cluster

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2024-08-01   |

## Context and Problem Statement

- This document states about how to synchronize edge devices(Authenticator, NFC Controller, Elevator Access Controller) system time and services on Agora Gen3(Speedway) cluster.
- Time synchronization between edge devices and the cluster is required for log analysis or timestamp validation on authentication requests etc.

---

## Considered Options

- Using NTP server to sync edge devices' system time.
- We requested Agora an NTP service to Agora Infra team. The answers to the request were follows.

```text
Agora does not provide a dedicated NTP service, and the time of Agora services is synchronized by AWS natively.
Customers with devices outside Agora can synchronize their time with our services using time.aws.com.
Agora currently does not guarantee the accuracy of the time of services running in AWS; time is expected to vary by up to a few seconds.
If you have specific time accuracy requirements, please let us know.
```

```text
> do we expect "varying by up to a few seconds" only occurs in the worst case ?
Yes
> the time of Agora services is synchronized on a regular basis? or only synched on specific timing ? eg. only on virtual machine is booted
By default cluster nodes maintained by Stargate's SMC should check with the internal AWS ntp servers every few minutes but this is not maintained by Agora
```

---

## Decision Outcome

- Use NTP service `time.aws.com` to sync edge devices' system time.
- If it's difficult use `time.aws.com` on Android devices due to a manufacture limitation, it is possible to use default NTP service of Android.

---

## Note

- 2024-08-01 : Drafted, Originator: Kohta Natori

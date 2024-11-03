# ADR-SEC-0001 Prevent Replay Attacks

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2023-07-11   |

## Context and Problem Statement

- How to satisfy the security requirement : `Requests should include a mechanism to prevent replay attacks, such as a nonce which would only be valid once`, originally described in [Site Gate - Security Requirements Document](https://docs.google.com/document/d/1Q8_0Yt_KlxwBEw0SPycxnQaGjcbt51Xe0U6K9YoZaBM/edit) ID 6.
- The related JIRA ticket is [this](https://jira.tri-ad.tech/browse/FSSSECACT-68) on FSS Security Activity Board.

### Given Conditions

- mTLS will be used in all communication paths.
- System time of all devices and the backend will be synched with a NTP server (or something similar).

### Scope

[REST API](../../api/auth_http.yaml)s for authentication from devices to the backend, listed below

- `/auth/door/authz`
- `/auth/elevator/allow-access`
- `/log/pass-through`

---

## Considered Options

Contain one of followings in a request to prevent a replay attacks.

- Random value
- Counter value
- Timestamp

---

## Decision Outcome

Use `Timestamp`.  

- The backend MUST validate that `abs(CurrentTime - TimeStampInRequest) <= Threshold` is true. Otherwise, reject the request.  
- `Threshold` can be configured with an environment variable etc. The default value of the `Threshold` is `1.0 seconds`. But we could change it after development/testing.

### Reason

- In the case of a random value, the backend needs to store its value per devices.
- In the case of a counter value, the backend and devices need to store its value.

---

## Consequences

- We need to sync system time among the backend and devices even on debug or deployment environment. On these cases, adjust the `Threshold`.

---

## Note

- 2023-07-11 : Drafted, Originator: Kohta Natori

# ADR-BE-0002 Authentication and Authorization of A/C Backend APIs

| Status   | Last Updated |
| -------- | ------------ |
| Approved | 2023-10-25   |

## Context and Problem Statement

- Accesses to each backend APIs MUST be controlled based on their use cases.
- This ADR defines how to control accesses of each backend APIs.

---

## Considered Options

- We've already used IoTA service to communicate securely between backend and devices with mTLS.
- We can use Keycloak for user login.
- We can use [Drako](https://developer.woven-city.toyota/docs/default/component/drako-service/) for authorization.

---

## Decision Outcome

| API Path          | Exported Port No (*1)  | Who Can Access?                 | Authentication                       | Authorization             |
| ----------------- | ---------------------- | ------------------------------- | ------------------------------------ | ------------------------- |
| `/auth/api`       | 6645 (HTTPS with mTLS) | Provisioned device              | IoT ingress w/client certificate(*2) | Validating on backend(*3) |
| `/log/api`        | 443 (HTTPS)            | Security guards, A/C developers | Login with WovenID (Keycloak)        | Validating on Drako       |
| `/management/api` | 443 (HTTPS)            | A/C developers                  | Login with WovenID (Keycloak)        | Validating on Drako       |

*1 : Exported Port No here is for the `dev` cluster. It may change on the production cluster.  
*2 : You can see the current configuration of the dev cluster [here](https://github.com/wp-wcm/city/blob/5824546dca1497f49b0c5c2ef49c81092c6617ee/infrastructure/k8s/dev/city-ingress/gateway.yaml#L51).  
*3 : Validating on Backend : Make sure the source device is a registered and valid one. See [ADR-SEC-0002 Verify Request's Origin](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/security/ADR-SEC-0002_VerifyRequestsOrigin.md) for details.

### How to Configure

Create configuration using **virtual services** to implement above settings. The CityService is for simple web applications, so we can NOT use it in this case ([related Slack thread](https://woven-by-toyota.slack.com/archives/C042AQ2TU4A/p1696555499325669?thread_ts=1696548966.274219&cid=C042AQ2TU4A)).

---

## Consequences

### Development and Debugging of APIs

- After shutting 443(HTTPS without mTLS) endpoint of `/auth/api`, in order to use them for development/debugging, we need to provision our development PC using IoTA service. It might not be an issue in the development phase.
- In the production phase, we can NOT provision our development PC for that purpose. We will need to consider how to solve it on that time.

---

## Note

- 2023-10-25 : Approved
- 2023-10-19 : Drafted, Originator: Kohta Natori

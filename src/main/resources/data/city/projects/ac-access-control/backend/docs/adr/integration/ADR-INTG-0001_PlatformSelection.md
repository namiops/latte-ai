# ADR-ENT-0001 Platform Selection

| Status | Last Updated |
|---|---|
|Approved| 2023-07-12 |

## Context and Problem Definition

Select a platform we use for building our system.

### Given Conditions

We MUST follow policies and rules below.

#### Woven City Policies

- [Woven City Data Policy](https://security.woven-planet.tech/policies/woven-city/data-policy/)
- [Woven City Identity Policy](https://security.woven-planet.tech/policies/woven-city/identity-policy/)
- [Woven City IoT Policy](https://security.woven-planet.tech/policies/woven-city/iot-policy/)
- [Woven City Approved Platform Policy](https://security.woven-planet.tech/policies/woven-city/approved-platform-policy/)
- [Woven City IoT Security Standards](https://security.woven-planet.tech/standards/woven-city/iot-security-standard/)
- [Woven City Privacy Policy](https://docs.google.com/document/d/1UFVFMh5fUdCY5NY3uaRAXWQqvul5fVSL1hoTKNUYsyU/edit#heading=h.5qm13wuvtiz9)
- Other common policies on [Cybersecurity & Privacy Portal(Policies > Common)](https://security.woven-planet.tech/)

#### NFR/ADR

- [ADR Index](https://docs.google.com/presentation/d/1V6wkIXt5OfXcLcJEvfyMRXmaTKZU_1uQMbiH_wZGxhE/edit#slide=id.g15fa420ef33_0_0) (TCS IA team provided)
- [NFR Enabler backlog](https://docs.google.com/spreadsheets/d/1Q_J-ZG32zZYueIsIqDXAI6xQhRJClczjtjcPc5ZF0jo/edit#gid=1965949751)

### Status of Access Control Development Team

- A/C development team does not have infra or network dedicated engineers.
- Security and Availability are the most prioritize attributes for our system.

---

## Available Options

- Approved Platform : Agora
- Other third party cloud platform : AWS, GCP etc
  - When we use other third party cloud platform, we MUST ...
    - follow [Security Requirements for Services running on Non-Standard Platforms](https://docs.google.com/document/d/1fCt6CG79e97cvGPwFt64ctnb6Z1d2lcc8iC5KXqmLCs/edit)
    - be assessed and approved by Standing Platform Governance Committee (SPGC)

---

## Decision

- `Use the approved platform : Agora and maximize the use of the Agora platform`.

### Reason

- The Agora platform provides common features which modern applications/services needs with satisfying demanded policies and standards. [See detail](https://developer.woven-city.toyota/docs/default/Domain/agora-domain).
- We do not have to build a platform from a scratch by ourselves. It can save tons of time.
- A/C system must rely on some Agora services like Identify or BURR.

---

## Result of Decision

- We can mainly focus on our application development.
- The situation which the Agora team can not provide a feature we need is the one of the biggest risks. To avoid that situation, we must communicate closely with Agora team and make our requests clear.

---

## Note

- 2023-07-12 : Approved
- 2023-07-10 : Drafted, Originator: Kohta Natori

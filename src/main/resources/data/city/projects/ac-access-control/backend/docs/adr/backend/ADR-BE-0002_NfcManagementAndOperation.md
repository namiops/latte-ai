# ADR-BE-0002 NFC Management and Operation

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2023-10-24   |

## Context and Problem Statement

In rebuilding the NFC service, it is necessary to consider specifications such as shared ID cards, short-term users, and long-term users, which were not considered previously.

This ADR will describe NFC registration and operational procedures.

### Assumptions

- A shared NFC ID is associated with a Woven ID that is **not** associated with any specific individual.
- The association between the shared NFC ID and the Woven ID will never change.
  
## Considered Options

1. Set access authorization and NFC expiration date for each Woven ID to A3
2. Access authorization are set in advance for each NFC card (for each Woven ID) to A3, and the NFC service manages the linkage between NFC ID and Woven ID and the expiration date of NFC.

- Common points in the two
  - Register authorization rules to A3
  - Connect NFC ID and Woven ID with NFC service
  - Retain the NFC ID of the lost card in the NFC service

- Difference between the two

| Different items | No.1 | No.2 |
| --------------- | ---- | ---- |
| A3 authorization rule deadline | Set | Not set |
| Items to be updated when lending a card | A3 : authorization rule | NFC service : NFC expiration date |
| NFC ID expiration date storage location | A3 (Included in the duration of the rule) | NFC service |
| What to do when a lost card occurs | Change the duration of the A3 rule or delete the NFC ID registration | Delete NFC ID registration or make the validity period past time in the NFC service |

## Decision Outcome

We chose 2. because it is simple to manage and only requires setting the expiration date at the time of card distribution.

## Inspection

We checked to see if there were any obstacles to the method we selected for the currently envisioned use cases.

The use cases are based on slides No. 27-30 of [this document](https://docs.google.com/presentation/d/1Iexc1S1XrpNMtHG9G5x4wkhT-MMpk8Gz3IfvINvPREw/edit#slide=id.g26810e9f6d2_0_10).

As a result, we did not find any problematic cases, but we did find some points to keep in mind in operation, which will be discussed later.

### Use Cases Considered

| Attribute  | Category                   | Category Details                       | Term of Use | Card type   |
|:-----------|:-------------------------- |:---------------------------------------|:------------|:------------|
| Residents  | Head of the household      | -                                      | Long        | Personal ID |
| Residents  | Not head of household      | 18 years and older                     | Long        | Personal ID |
| Residents  | Not head of household      | 6 to 18 years old                      | Long        | Personal ID |
| Visitor    | General visitor            | Adult (18 years and older)             | Short       | shared ID   |
| Visitor    | Accompanies                | 6 to 18 years old (with parents)       | Short       | shared ID   |
| Visitor    | LC contractors             | Logistics providers already contracted | Long        | shared ID   |
| Visitor    | Related traders            | Contractors who come regularly         | Long        | shared ID   |
| Visitor    | Related traders            | Vendors with no NFC distributed        | Short       | shared ID   |
| Visitor    | Party of tourists          | Groups of 15 or more                   | Short       | shared ID   |
| Visitor    | quasi VIP                  | Not using the Guest House              | Short       | shared ID   |
| Visitor    | Inviter by Residents       | Parents, acquaintances, etc.           | Long        | shared ID   |
| Visitor    | Inviter by Worker          | Business Clients, etc.                 | Short       | shared ID   |
| Worker     | WCM Staff                  | Management Staff                       | Long        | Personal ID |
| Worker     | Contractors from WCM       | Security, cleaning, reception, etc.    | Long        | Personal ID |
| Worker     | Contractors with WCM       | Office and tenant workers              | Long        | Personal ID |
| Worker     | Inventor                   | -                                      | Long        | Personal ID |

### Operational Notes

#### Change access privileges

In the case of personal ID cards, it is better to change the A3 setting without replacing the card because the card has a picture of the face printed on it.

For short-term users with a shared ID card, the card should be replaced.
For long-term users with a shared ID card, the A3 setting should be changed.

#### Loss of card

For security reasons, the NFC ID of a lost card cannot be used thereafter.
Therefore, the NFC service (or somewhere else) can make the lost ID disabled and must retain the lost ID to prevent to issue a same ID again.

## Note

- 2023-10-24 : Drafted, Originator: Kenji Motoki

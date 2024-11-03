# ADR-FE-0001 Validation Rules

| Status | Last Updated |
|---|---|
|Approved| 2024-05-10 |

## Context and Problem Statement

In Car Gate UI, it is necessary to consider the validation rules for input items for the registrations of car gate authentication information.

This ADR describes the validation rules of these inputs.

### Preconditions
- Car Gate UI is the app for TWC staff not for general users.
- The specifications of BE that recieves the data are defined in [cargate_http.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/cargate_http.yaml)

## Considered Options

No options.

Since there is no clear requirement for validation at this stage, the rules should be the minimum to satisfy the specifications on the BE side.

## Decision Outcome

The validation rules are as follows.

### Worker/Resident Registration

|> | Item | Type | Rules |
|---|---|---|---|
|> | Woven Id | string | - required |
|> | Car Number | folowing 4 parameters | - optional |
| Car Number | Plate Region | string | - required<br>- full-width characters |
| ^ | Plate Code | string | - required<br>- full-width characters |
| ^ | Plate Symbol | string | - required<br>- full-width characters |
| ^ | Plate License | string | - required<br>- full-width characters |
|> | NFC IDM | string | - optional |
|> | User Type | string | - required |
|> | Registration Type | "worker" or "resident" | - no validation as no form input |

### Visitor Registration

|> | Item | Type | Rules |
|---|---|---|---|
|> | Woven Id | string | - required |
|> | Car Number | folowing 4 parameters | - optional |
| Car Number | Plate Region | string | - required<br>- full-width characters(less than 4) |
| ^ | Plate Code | string | - required<br>- full-width 3 characters |
| ^ | Plate Symbol | string | - required<br>- full-width 1 characters |
| ^ | Plate License | string | - required<br>- full-width 4 characters |
|> | NFC IDM | string | - required |
|> | User Type | string | - required |
|> | Date Time From | string | - required<br>- YYYY-MM-DD hh:mm(+/-)zz:zz |
|> | Date Time To | string | - required<br>- YYYY-MM-DD hh:mm(+/-)zz:zz |

### e-Palette Registration

| Item | Type | Rules |
|---|---|---|
| e-Palette Id | string | - required |
| WCN | string | - required<br>-  less 32 characters |

## Note

- 2024-05-10 : Approved
- 2024-04-26 : Drafted, Originator: Yuki Iwama
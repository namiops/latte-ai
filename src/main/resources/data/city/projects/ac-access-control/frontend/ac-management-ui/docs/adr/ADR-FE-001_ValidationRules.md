# ADR-FE-0001 Validation Rules

| Status | Last Updated |
|---|---|
|Approved| 2024-06-12 |

## Context and Problem Statement

In Ac management UI, it is necessary to consider the validation rules for input items for the registrations of access control information.

This ADR describes the validation rules of these inputs.

### Preconditions
- Ac management UI is the app for TWC staff not for general users.
- The specifications of BE that receives the data are defined in [management_http.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/api/management_http.yaml)

## Considered Options

No options.

Since there is no clear requirement for validation at this stage, the rules should be the minimum to satisfy the specifications on the BE side.

## Decision Outcome

The validation rules are as follows.

### Floor Map Image Registration

| Item | Type | Rules |
|---|---|---|
| Map Image | image/png or image/jpeg | - required<br>- less than 3MB |

### Resource Group Registration

| Item | Type | Rules |
|---|---|---|
| Name | string | - required<br>- pattern: `^rg:[A-Za-z0-9\-._:#]+$` |
| Note | string | - required |
| Doors | Array of string | - required<br>- total of doors, elevators and resource groups is more than 1 |
| Elevators | Array of string | - required<br>- total of doors, elevators and resource groups is more than 1 |
| Resource Groups | Array of string | - required<br>- total of doors, elevators and resource groups is more than 1 |

## Note

- 2024-06-12 : Approved
- 2024-06-12 : Drafted, Originator: Yuki Iwama
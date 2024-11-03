# ADR-FE-0001 Validation Rules

| Status | Last Updated |
|---|---|
|Approved| 2024-07-11 |

## Context and Problem Statement

In User Management UI, it is necessary to consider the validation rules for input items for user registration and NFC card issuance.

This ADR describes the validation rules of these inputs.

### Preconditions
- User Management UI is the app for TWC staff not for general users.
- The specifications of BE that recieves the data are defined in [resident_registration.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-user-registration/backend/docs/api/resident_registration.yaml) and [nfc_http.yaml](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/nfc-manager/docs/api/nfc_http.yaml)

## Considered Options

No options.

Since there is no clear requirement for validation at this stage, the rules should be the minimum to satisfy the specifications on the BE side.

## Decision Outcome

The validation rules are as follows.

### User Information

| Item | Type | Rules |
|---|---|---|
| Alphabetical Name | string | - required<br>- 150 or less characters(each name)<br>- pattern: <br>1. Alphabet: `a-zA-Z` <br>2. Latin: `\u00C0-\u0178` <br>3. Symbol: `-,.'\s` |
| Japanese Name | string | - required when "Alphabetical name only" is not checked<br>- 150 or less characters(each name)<br>- pattern: <br>1.  Hiragana: `\u3041-\u3096`<br>2. Katakana: `\u30A1-\u30FA`<br>3. Kanji:<br>- `々〻\u3007`<br>- `\u3400-\u9FFF`<br>- `\uF900-\uFAFF`<br>-Symbols: `()（）\s` <br>- **Variation Selectors are not supported**|
| Email | string | - optional but required for persons over 18 years old<br>- 254 or less characters(each name)<br>- mail address |
| Phone number | string | - required<br>- pattern: `^[+]+[0-9]{1,3}[\s]+[0-9]\d{1,14}$` |
| Date of birth | date | - required<br>- valid date |
| Name of emergency contact | string | - required<br>- 254 or less characters(each name) |
| Date of ID confirmation | date | - optional<br>- valid date |
| Date of traffic education | date | - optional<br>- valid date |
| Face image | string | - optional<br>- format: `jpeg` or `png`<br>- size: `< 5MB` |
| parents | string | - optional but required for persons under 18 years old<br>- format: `jpeg` or `png`<br>- size: `< 5MB` |

### Household Information

| Item | Type | Rules |
|---|---|---|
| Head of household | string | - required<br>- 18 years and older |
| City Address | string | - required<br>- No duplication with other households |
| Moving in date | date | - required<br>- valid date |
| Retirement date | date | - required<br>- valid date<br>- `>= Moving in date` |
| Housemate | string[] | - optional<br>- DO NOT include head of household |

### NFC Card

| Item | Type | Rules |
|---|---|---|
| IDM | string | - required |
| System code | string | - required |
| Card number | string | - required |
| User | string | - required |
| Start date | date | - optional/required if shared card<br>- valid date |
| End date | date | - optional/required if shared card<br>- valid date<br>- `>= Start date` |

### User Group

| Item | Type | Rules |
|---|---|---|
| User group name | string | - required<br>- 254 or less characters |
| Note | string | - required<br>- 254 or less characters |

### Corporate

| Item | Type | Rules |
|---|---|---|
| Corporate name | string | - required<br>- 254 or less characters |
| Representative | string | - required |

### Tenant

| Item | Type | Rules |
|---|---|---|
| Tenant name | string | - required<br>- 254 or less characters |
| Representative | string | - required |
| City Address | string | - required<br>- No duplication with other tenants |
| Effective date | date | - required<br>- valid date |
| Termination date | date | - required<br>- valid date<br>- `>= Effective date` |

### Worker

| Item | Type | Rules |
|---|---|---|
| worker | string | - required |
| business tenants | string[] | - required |
| Effective date | date | - required<br>- valid date |
| Termination date | date | - required<br>- valid date<br>- `>= Effective date` |

## Note

- 2024-08-02 : Added the validation rules of Corporate, Tenant, and Worker
- 2024-07-11 : Updated the validation rule of household
- 2024-07-08 : Updated the validation pattern of alphabetical Name
- 2024-03-27 : Approved
- 2024-03-27 : Drafted, Originator: Yuki Iwama
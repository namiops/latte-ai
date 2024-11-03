# Database design

## Visitor RDB

Database: Postgres cluster

### List of Enum Tables

Each table stores the list of specific enum values.

| Table name                     |
| :----------------------------- |
| identity_verification_statuses |

### List of Master Tables

| Table name                        |
| :-------------------------------- |
| identity_verification_submissions |
| keycloak_children                 |

### Table Schema - Enum Tables

This section describes the definitions of enum tables.
In enum tables, records are expected to be inserted initially, and not expected to be updated by applications.
Each table has the following schema.

| Column name | Type | PK   | FK   | Nullable | Description |
| :---------- | :--- | :--- | :--- | :------- | :---------- |
| name        | text | o    |      |          |             |

#### identity_verification_statuses

This table stores the list of possible identity verification states.

- submitting
- submitted
- finished
- failed
- urlExpired

### Table Schema - Master Tables

#### identity_verification_submissions

| Column name | Type   | PK   | FK                                   | Nullable | Description                                                                 |
| :---------- | :----- | :--- | :----------------------------------- | :------- | :-------------------------------------------------------------------------- |
| id          | text   | o    |                                      |          | This is the same value as association ID in eKYC.                           |
| woven_id    | text   |      |                                      |          | Woven ID of the submitter.                                                  |
| status      | text   |      | identity_verification_statuses(name) |          | Submission status of the verification.                                      |
| reason      | text   |      |                                      |          | The reason of the failed submission. Default: `''` (empty)                  |
| obsolete    | bool   |      |                                      |          | If this is true, it indicates that the request is not latest                |
| token       | string |      |                                      |          | A token to verify that the user finished application. Default: `''` (empty) |

##### identity_verification_submissions constraints

- Unique constraint: (woven_id, obsolete) where `obsolete` is `false`
- Check constraint: token must be `''` if `status != submitting`
- Check constraint: token must not be `''` if `status == submitting`

#### keycloak_children

It's a temporary table while the backend is communicating with BURR mock APIs.
While we use mock guardianship APIs, the backend stores each child's keycloak account in this table instead of associating with a parent user.
We will refer to this table to delete these created accounts.
We will remove this table after BURR implements guardianship APIs.

| Column name | Type | PK   | FK   | Nullable | Description              |
| :---------- | :--- | :--- | :--- | :------- | :----------------------- |
| id          | text | o    |      |          | keycloak ID (= woven ID) |

## Job DB

### Resident RDB

#### Resident Tables

| Table name                          |
| :---------------------------------- |
| household_bulk_registration_results |
| household_bulk_registration_images  |

#### Resident Table Schema

##### household_bulk_registration_results

This table stores the status of each bulk registration process.

| Column name | Type                    | PK   | FK   | Nullable | Description                                                                             |
| :---------- | :---------------------- | :--- | :--- | :------- | :-------------------------------------------------------------------------------------- |
| id          | text                    | o    |      |          | A unique identifier of the processing.                                                  |
| created_at  | timestamp with timezone |      |      |          |                                                                                         |
| updated_at  | timestamp with timezone |      |      |          |                                                                                         |
| data        | jsonb                   |      |      |          | Details of the process. The schema is defined in `docs/api/resident_registration.yaml`. |

index: created_at

##### household_bulk_registration_images

This table stores user images to be registered.
The correspondence between an image and a task is recorded in the `data` column of the associated `household_bulk_registration_results` record.

| Column name | Type | PK                                      | FK   | Nullable | Description                                    |
| :---------- | :--- | :-------------------------------------- | :--- | :------- | :--------------------------------------------- |
| id          | text | o                                       |      |          | A unique identifier of the image.              |
| result_id   | text | household_bulk_registration_results(id) |      |          | An identifier of the associated result record. |
| image       | blob |                                         |      |          | A face image.                                  |

index: result_id

### Worker RDB

#### Worker Tables

| Table name                       |
| :------------------------------- |
| worker_bulk_registration_results |
| worker_bulk_registration_images  |

#### Worker Table Schema

##### worker_bulk_registration_results

This table stores the status of each bulk registration process.

| Column name | Type                    | PK   | FK   | Nullable | Description                                                                           |
| :---------- | :---------------------- | :--- | :--- | :------- | :------------------------------------------------------------------------------------ |
| id          | text                    | o    |      |          | A unique identifier of the processing.                                                |
| created_at  | timestamp with timezone |      |      |          |                                                                                       |
| updated_at  | timestamp with timezone |      |      |          |                                                                                       |
| data        | jsonb                   |      |      |          | Details of the process. The schema is defined in `docs/api/worker_registration.yaml`. |

index: created_at

##### worker_bulk_registration_images

This table stores user images to be registered.
The correspondence between an image and a task is recorded in the `data` column of the associated `worker_bulk_registration_results` record.

| Column name | Type | PK                                   | FK   | Nullable | Description                                    |
| :---------- | :--- | :----------------------------------- | :--- | :------- | :--------------------------------------------- |
| id          | text | o                                    |      |          | A unique identifier of the image.              |
| result_id   | text | worker_bulk_registration_results(id) |      |          | An identifier of the associated result record. |
| image       | blob |                                      |      |          | A face image.                                  |

index: result_id

## Resident registration Log DB

The Resident registration Log database stores logs.

Database: Secure KVS
Database Name: resident-registration-logs
(The actual database name is namespace + database name, which is `ac-user-registration_resident-logs`.)

### Document schema

| Field name     | Type   | Available as search condition | Description                                        |
| :------------- | :----- | :---------------------------- | :------------------------------------------------- |
| +operationName | string | o                             | Indicates what operations were performed           |
| +requestId     | string | o                             | The value of `X-Request-ID` header in the request. |
| +timestampMs   | number | o                             | Timestamp when the log is stored                   |
| +operatorId    | string | o                             | Woven ID of the user who performed the operation.  |
| +userId        | string | o                             | Woven ID to be operated                            |
| detail         | object |                               | Detailed information about the operation.          |

### Content stored in operationName

The data content stored in `+operationName` is as follows.

| value                           | Description                               | ID stored in `+userId`                                                 |
| :------------------------------ | :---------------------------------------- | ---------------------------------------------------------------------- |
| CreateUser                      | Create a new user                         | Created user's wovenID                                                 |
| CreateChildUser                 | Create a new child user                   | Created child's wovenID                                                |
| UpdateGuardians                 | Update a list of guardian users           | Woven ID of the ward user                                              |
| DeleteGuardians                 | Delete a list of guardian users           | Woven ID of the ward user                                              |
| UpdateBasicInformation          | Update basic information                  | WovenID of user to be updated information                              |
| UpdateEmergencyContact          | Update an emergency contact               | WovenID of user to be updated information                              |
| UpdateFaceImage                 | Update a face image                       | WovenID of user to be updated information                              |
| UpdateIdVerification            | Update ID verification information        | WovenID of user to be updated information                              |
| UpdateTrainingQualificationInfo | Update training qualification information | WovenID of user to be updated information                              |
| CreateHousehold                 | Create a household                        | WovenID of representative registered as household or wovenID of member |
| AddHouseholdMembers             | Add household members                     | wovenID of member added to household                                   |
| RemoveHouseholdMembers          | Remove household members                  | wovenID of member removed to household                                 |
| DeleteHousehold                 | Delete a household                        | WovenID of representative registered as household or wovenID of member |
| UpdateHouseholdRepresentative   | Update household representative           | WovenID of representative registered as household                      |

### Object schema per operation stored in `detail`

#### CreateUser

| Field name | Type          | description                                 |
| ---------- | ------------- | ------------------------------------------- |
| items      | array[string] | List of items entered in `POST /users` API. |

The `item` will be a key defined in the `#/components/schemas/postUserBasicInformation` schema of [this document](../api/resident_registration.yaml)
The items to be listed are as follows.

- name.normative.primaryName
- name.normative.givenName
- name.phonetic.primaryName
- name.phonetic.givenName
- name.latin.primaryName
- name.latin.givenName
- dateOfBirth
- emailAddress
- phoneNumber

Ex:

``` text
[
    "name.normative.primaryName", 
    "name.normative.givenName", 
    "name.latin.primaryName",
    "name.latin.givenName",
    "dateOfBirth",
    "emailAddress",
    "phoneNumber"
]
```

#### CreateChildUser

| Field name | Type          | description                                                             |
| ---------- | ------------- | ----------------------------------------------------------------------- |
| guardianId | string        | ID specified in the `POST /users/:wovenId/children` API path parameter. |
| items      | array[string] | List of items entered in `POST /users/:wovenId/children` API.           |

The contents of `items` are the same as `CreateUser`

#### UpdateGuardians

| Field name  | Type          | description             |
| ----------- | ------------- | ----------------------- |
| guardianIds | array[string] | A list of guardian IDs. |

#### DeleteGuardians

| Field name  | Type          | description                     |
| ----------- | ------------- | ------------------------------- |
| guardianIds | array[string] | A list of deleted guardian IDs. |

#### UpdateBasicInformation

| Field name | Type          | description                                                      |
| ---------- | ------------- | ---------------------------------------------------------------- |
| items      | array[string] | PUT List of items updated by the /users/:wovenId/basicInfo` API. |

The contents of `items` are the same as `CreateUser`

#### UpdateEmergencyContact

| Field name | Type          | description                                                             |
| ---------- | ------------- | ----------------------------------------------------------------------- |
| items      | array[string] | PUT List of items updated by the /users/:wovenId/emergencyContact` API. |

The `item` will be a key defined in the `#/components/schemas/emergencyContact` schema of [this document](../api/resident_registration.yaml)
The items to be listed are as follows.

- name
- phoneNumber

#### UpdateFaceImage

This is not stored in `detail`.

#### UpdateIdVerification

Schema is the same as `#/components/schemas/idVerification` in [this document](../api/resident_registration.yaml)

#### UpdateTrainingQualificationInfo

Schema is the same as `#/components/schemas/trainingQualification` in [this document](../api/resident_registration.yaml)

#### CreateHousehold

Schema is the same as `#/components/schemas/household` in [this document](../api/resident_registration.yaml)

#### AddHouseholdMembers

| Field name  | Type   | description                                       |
| ----------- | ------ | ------------------------------------------------- |
| householdId | string | ID of the household to which the member was added |

#### RemoveHouseholdMembers

| Field name  | Type   | description                                           |
| ----------- | ------ | ----------------------------------------------------- |
| householdId | string | ID of the household from which the member was removed |

#### DeleteHousehold

| Field name  | Type   | description             |
| ----------- | ------ | ----------------------- |
| householdId | string | ID of deleted household |

#### UpdateHouseholdRepresentative

| Field name  | Type   | description                                               |
| ----------- | ------ | --------------------------------------------------------- |
| householdId | string | ID of the household from which the updated representative |

### How to generate DocID

Need to set unique DocID when writing logs in SecureKVS.
Set `Timestamp(Nano)` + "_" + {[0-9a-zA-Z]{4}} as DocID.
example: `1691627932000000000_Az09`

## Worker registration log DB

The Worker registration Log database stores logs.

Database: Secure KVS
Database Name: worker-audit-logs
(The actual database name is namespace + database name, which is `ac-user-registration_worker-audit-logs`.)

### worker-audit-logs document schema

| Field name        | Type   | Description                                                                                                            |
| :---------------- | :----- | :--------------------------------------------------------------------------------------------------------------------- |
| +operatorId       | string | Woven ID of the user who performed the worker registration service operation                                           |
| +method           | string | Method of the executed API                                                                                             |
| +host             | string | host of the executed API                                                                                               |
| +path             | string | Path of the executed API                                                                                               |
| +pathParameter    | object | Path parameters of the executed API                                                                                    |
| +resultCode       | number | Status code of the response to the API executed by the user                                                            |
| +timestampMs      | number | Unix timestamp (ms) of when the log was retrieved                                                                      |
| +requestId        | string | The value of `X-Request-ID` header in the request                                                                      |
| +corporationId    | string | The corporationId that was the target of the operation                                                                 |
| +businessTenantId | string | The businessTenantId that was the target of the operation. If the operation is not about a tenant, it will be omitted. |
| body              | object | Request body data of executed API. The content is an empty object `{}` if the request doesn't have a request body.     |

#### API list for logging target and log example

The APIs to be logged are as follows.

- POST /corporations
- PUT /corporations/{corporationId}
- POST /businessTenants
- PUT /businessTenants/{businessTenantId}
- PUT /corporations/{corporationId}/memberships/{wovenId}
- DELETE /corporations/{corporationId}
- DELETE /businessTenants/{businessTenantId}
- DELETE /corporations/{corporationId}/memberships/{wovenId}

Example:

```json
{
  "+operatorId": "01234567-0123-0123-0123-0123456789ab",
  "+method": "PUT",
  "+host": "https://ac-user-registration.cityos-dev.woven-planet.tech",
  "+path": "/api/worker/v1/corporations/:corporationId/memberships/:wovenId",
  "+pathParameter": {
      "corporationId": "corporation1",
      "wovenId": "11234567-0123-0123-0123-0123456789ab"
  },
  "+resultCode": 200,
  "+timestampMs": 1688020433123,
  "+requestId": "000011112222-3333-4444-5555-666677778889",
  "+corporationId": "corporation1",
  "+businessTenantId": "",
  "body": {
    {
      "businessTenantIds": [
        "string"
      ],
      "effectiveDate": "2024-06-03",
      "terminationDate": "2024-06-03"
    }
  }
}
```

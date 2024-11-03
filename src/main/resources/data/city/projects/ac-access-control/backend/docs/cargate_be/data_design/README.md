# Data Design

This document describes the design of data handled by AC Car gate registration service Backend.

## ToDo items in this document

- Notification reception of car gate open/close status from Amano server
- Notification car gete open/close status to e-Palette

## Registration Database design

Database: Postgres cluster

### List of Enum Tables

Each table stores the list of specific enum values.

| Table name         |
| :----------------- |
| user_types         |
| registration_types |
| car_gates          |
| job_statuses       |

### List of Master Tables

| Table name             |
| :--------------------- |
| car_gate_registrations |
| car_numbers            |
| e_palettes             |
| job_results            |

### Table Schema

#### Enum Tables

This section describes the definitions of enum tables.
In enum tables, records are expected to be inserted initially, and not expected to be updated by applications.
If not explicitly stated, each table has the following schema.

| Column name | Type | PK   | FK   | Nullable | Description |
| :---------- | :--- | :--- | :--- | :------- | :---------- |
| name        | text | o    |      |          |             |

##### user_types

This table stores the list of user types.

| Column name | Type   | PK   | FK   | Nullable | Description |
| :---------- | :----- | :--- | :--- | :------- | :---------- |
| id          | int    | o    |      |          |             |
| description | string |      |      |          |             |

Enum values and descriptions:

| id   | description           |
| :--- | :-------------------- |
| 1    | resident              |
| 2    | visitor               |
| 3    | worker 1              |
| 4    | worker 2              |
| 5    | pick up and drop off  |
| 6    | physical distribution |
| 7    | e-palette, shared car |
| 8    | moving                |
| 9    | VIP                   |
| 10   | master                |

##### registration_types

This table stores the list of registration types.

- facility-booking
- worker
- resident
- visitor
- e-palette

##### car_gates

This table stores the list of car gates.
ToDo. Once the actual value is determined, list it.

##### job_statuses

This table stores the list of job statuses.

- NotStarted
- Started
- Failed
- Succeeded

### Master Tables

#### car_gate_registrations

| Column name       | Type                     | PK   | FK                       | Nullable | Description                                               |
| :---------------- | :----------------------- | :--- | :----------------------- | :------- | :-------------------------------------------------------- |
| id                | text                     | o    |                          |          | Request ID                                                |
| registration_type | text                     |      | registration_types(name) |          | Registration type                                         |
| woven_id          | text                     |      |                          | o        | Woven ID of the user or shared NFC                        |
| car_number_id     | text                     |      | car_numbers(id)          | o        | Car number of the user                                    |
| nfc_idm           | text                     |      |                          | o        | NFC IDM of the user                                       |
| e_palette_id      | text                     |      | e_palettes(id)           | o        | e-Palette ID                                              |
| user_type         | int                      |      | user_types(id)           |          | User type to distinguish gates that can be passed through |
| date_time_from    | timestamp with time zone |      |                          | o        | Start date and time of use                                |
| date_time_to      | timestamp with time zone |      |                          | o        | End date and time of use                                  |

##### car_gate_registration constraints

If not specified, an item denotes a constraint that will be checked by a trigger function.

Constraints by `registration_type`

- `facility-booking`
  - `woven_id`, `car_number_id`, `date_time_from` and `date_time_to` must be non-null
  - `nfc_idm` and `e_palette_id` must be null
- `worker`, `resident`
  - Either `car_number_id` or `nfc_idm` must be non-null
  - `woven_id` must be non-null
  - `e_palette_id`, `date_time_from`, `date_time_to` must be null
- `visitor`
  - `woven_id`, `nfc_idm`, `date_time_from` and `date_time_to` must be non-null
  - `e_palette_id` must be null
- `e-palette`
  - `e-palette_id` must be non-null
  - `woven_id`, `car_number_id`, `nfc_idm`, `date_time_from`, `date_time_to` must be null

Constraints by each column

- `e_palette_id`: unique
- `car_number_id`, `nfc_idm`
  - Each row must not share the same `car_number_id` or `nfc_idm` with overlapping time periods. Here, we `date_time_from=null` as `date_time_from='-infinity'::timestamp` and `date_time_to` as `date_time_to='infinity'::timestamp`
    (Reference: [datatype-datetime](https://www.postgresql.org/docs/14/datatype-datetime.html))

#### car_numbers

| Column name   | Type | PK   | FK   | Nullable | Description                   |
| :------------ | :--- | :--- | :--- | :------- | :---------------------------- |
| id            | text | o    |      |          | ID to be assigned internally  |
| plate_region  | text |      |      |          | Issuing region                |
| plate_code    | text |      |      |          | 3-digit class number          |
| plate_symbol  | text |      |      |          | 1-digit class number          |
| plate_license | text |      |      |          | License number                |
| full_plate_id | text |      |      |          | A generated column for search |

- Generation rule for full_plate_id: `GENERATED ALWAYS AS (concat(plate_region,' ',plate_code,' ',plate_symbol,' ',plate_license))`
- Unique constraint: (plate_region,plate_code,plate_symbol,plate_license)

#### e_palettes

| Column name | Type | PK   | FK   | Nullable | Description          |
| :---------- | :--- | :--- | :--- | :------- | :------------------- |
| id          | text | o    |      |          | e-Palette ID         |
| wcn         | text |      |      |          | WCN of the e-Palette |

- Unique constraint: wcn

#### job_results

| Column name | Type | PK   | FK                 | Nullable | Description                 |
| :---------- | :--- | :--- | :----------------- | :------- | :-------------------------- |
| id          | text | o    |                    |          | Inquiry ID of the job       |
| status      | text |      | job_statuses(name) |          | Status of the job           |
| detail      | text |      |                    |          | Detailed results of the job |

## Log Database

This database logs events notified when a vehicle pass through a car gate. And logs operation.

Database: SecureKVS (CouchDB)
Database Name: cargate-logs
(The actual database name is namespace + database name, which is `ac-access-control_cargate-logs`.)

### Document schema

#### Common field

| Field name | Type   | Description        |
| :--------- | :----- | :----------------- |
| +type      | string | performed log type |

This table stores the list of type.

- notified
- operation

Other fields differ for each log type.

#### Notified Document schema

| Field name         | Type   | Description                               |
| :----------------- | :----- | :---------------------------------------- |
| +type              | string | performed log type (This is notified)     |
| +requestId         | string | RequestID of vehicle pass through         |
| +wovenId           | string | WovenID of Passenger in a passing vehicle |
| +ePaletteId        | string | e-PaletteID of the pass through e-Palette |
| +noticeTimestampMs | number | Timestamp of notified date time           |
| +passagePermit     | string | Whether passage is permitted or not       |
| +permitReason      | string | Reason for passage permit or not          |
| +passageResult     | string | passage result                            |
| +authResult        | bool   | authentication result                     |
| +authMedium        | string | authentication medium                     |
| +applyType         | string | application Type                          |
| +gateId            | string | ID of car gate                            |
| +inOutType         | string | Direction for enter and exit              |

#### Operation Document schema

| Field name            | Type   | Description                                                    |
| :-------------------- | :----- | :------------------------------------------------------------- |
| +type                 | string | performed log type (This is operation)                         |
| +operationName        | string | Indicates what operations were performed                       |
| +xRequestId           | string | The value of `X-Request-ID` header in the request.             |
| +operationTimestampMs | number | Timestamp of operation date time                               |
| +operatorId           | string | WovenID of the operator who performed the operation.           |
| +requestId            | string | RequestId of the created by the operation, or operated target. |
| detail                | string | Detailed information about the operation.                      |

##### Content stored in operationName

The data content stored in `+operationName` is as follows.

| value                | Description                                                           |
| :------------------- | :-------------------------------------------------------------------- |
| CreateWorkerResident | Register new car gate authorization information for workers/residents |
| UpdateWorkerResident | Update car gate information for workers/residents                     |
| CreateVisitor        | Register new car gate authorization information for visitor           |
| UpdateVisitor        | Update car gate information for visitor                               |
| CreateEPalette       | Register new car gate authorization information for an e-palette      |
| Delete               | Delete car gate information                                           |

##### Object schema per operation stored in `detail`

###### CreateWorkerResident

| Field name       | Type      | Description                                                   |
| :--------------- | :-------- | :------------------------------------------------------------ |
| userType         | string    | Registered userType                                           |
| registrationType | string    | Indicates whether the user registered is a worker or resident |
| nfcIdm           | string    | Registered NFCIdm                                             |
| carNumber        | carNumber | Registered Car Number                                         |

carNumber's schema is the same as `#/components/schemas/carNumber` in [this document](../../api/cargate_http.yaml)

###### UpdateWorkerResident

| Field name | Type      | Description        |
| :--------- | :-------- | :----------------- |
| userType   | string    | Updated userType   |
| carNumber  | carNumber | Updated Car Number |

###### CreateVisitor

| Field name   | Type      | Description                           |
| :----------- | :-------- | :------------------------------------ |
| wovenId      | string    | Woven ID associated with the NFC ID   |
| nfcIdm       | string    | Registered NFCIdm                     |
| carNumber    | carNumber | Registered Car Number                 |
| dataTimeFrom | string    | Registered start date and time of use |
| dateTimeTo   | string    | Registered end date and time of use   |
| userType     | string    | Registered userType                   |

###### UpdateVisitor

| Field name   | Type      | Description                        |
| :----------- | :-------- | :--------------------------------- |
| carNumber    | carNumber | Updated Car Number information     |
| dataTimeFrom | string    | Updated start date and time of use |
| dateTimeTo   | string    | Updated end date and time of use   |
| userType     | string    | Updated userType                   |

###### CreateEPalette

| Field name | Type   | Description           |
| :--------- | :----- | :-------------------- |
| ePaletteId | string | Registered ePaletteId |
| wcn        | string | Registered wcn        |

###### Delete

none

### How to generate DocID

Need to set unique DocID when writing logs in SecureKVS.
Set `type` + "_" + `Timestamp(Nano)` + "_" + {[0-9a-zA-Z]{4}} as DocID.
example: `notified_1691627932000000000_Az09`

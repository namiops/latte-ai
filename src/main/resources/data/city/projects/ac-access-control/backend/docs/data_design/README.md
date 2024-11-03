# Data Design <!-- omit in toc -->

This document describes the design of data handled by AC Backend.
This document contains the following definitions.

- database schema for Postgres cluster
- database definitions for SecureKVS
- the schema of desired / reported data stored in IoTA (TBU)

## Table of Contents <!-- omit in toc -->

- [AC Management Service RDB](#ac-management-service-rdb)
  - [List of Tables](#list-of-tables)
    - [List of Enum Tables](#list-of-enum-tables)
    - [List of Master Tables](#list-of-master-tables)
  - [Table Relationships](#table-relationships)
  - [Table Schema](#table-schema)
    - [Enum Tables](#enum-tables)
      - [authentication\_methods](#authentication_methods)
      - [device\_roles](#device_roles)
      - [device\_event\_code\_levels](#device_event_code_levels)
      - [device\_event\_code\_sorts](#device_event_code_sorts)
      - [device\_state\_code\_levels](#device_state_code_levels)
      - [directions](#directions)
      - [door\_event\_code\_levels](#door_event_code_levels)
      - [door\_event\_code\_sorts](#door_event_code_sorts)
      - [door\_state\_code\_levels](#door_state_code_levels)
      - [door\_state\_code\_types](#door_state_code_types)
      - [location\_types](#location_types)
    - [anti\_passback\_settings](#anti_passback_settings)
    - [buildings](#buildings)
    - [devices](#devices)
    - [device\_event\_codes](#device_event_codes)
    - [device\_state\_codes](#device_state_codes)
    - [doors](#doors)
      - [doors constraints](#doors-constraints)
    - [door\_authenticators](#door_authenticators)
      - [door\_authenticators constraints](#door_authenticators-constraints)
    - [door\_authentication\_methods](#door_authentication_methods)
      - [door\_authentication\_methods constraints](#door_authentication_methods-constraints)
    - [door\_event\_codes](#door_event_codes)
    - [door\_floor\_image\_settings](#door_floor_image_settings)
      - [door\_floor\_image\_settings constraints](#door_floor_image_settings-constraints)
    - [door\_state\_codes](#door_state_codes)
    - [elevators](#elevators)
      - [elevators constraints](#elevators-constraints)
    - [elevator\_access\_controllers](#elevator_access_controllers)
      - [elevator\_access\_controllers constraints](#elevator_access_controllers-constraints)
    - [elevator\_authentication\_methods](#elevator_authentication_methods)
      - [elevator\_authentication\_methods constraints](#elevator_authentication_methods-constraints)
    - [elevator\_authenticators](#elevator_authenticators)
      - [elevator\_authenticators constraints](#elevator_authenticators-constraints)
    - [elevator\_floors](#elevator_floors)
      - [elevator\_floors constraints](#elevator_floors-constraints)
    - [elevator\_floor\_image\_settings](#elevator_floor_image_settings)
      - [elevator\_floor\_image\_settings constraints](#elevator_floor_image_settings-constraints)
    - [floors](#floors)
    - [floor\_images](#floor_images)
      - [floor\_images constraints](#floor_images-constraints)
    - [ota\_groups](#ota_groups)
    - [resource\_event\_logs](#resource_event_logs)
      - [resource\_event\_logs indexes](#resource_event_logs-indexes)
- [AC Management Service Redis](#ac-management-service-redis)
  - [Door state](#door-state)
  - [Device state](#device-state)
  - [Door state (merged with device states)](#door-state-merged-with-device-states)
  - [door\_event](#door_event)
    - [door\_event indexes](#door_event-indexes)
    - [how to set and get door events](#how-to-set-and-get-door-events)
  - [device\_event](#device_event)
    - [device\_event indexes](#device_event-indexes)
    - [how to set and get device events](#how-to-set-and-get-device-events)
- [AC Auth Service RDB](#ac-auth-service-rdb)
  - [AC Auth Service List of Tables](#ac-auth-service-list-of-tables)
  - [AC Auth Service Table Schema](#ac-auth-service-table-schema)
    - [anti\_passback\_open\_logs](#anti_passback_open_logs)
      - [anti\_passback\_open\_logs constraints](#anti_passback_open_logs-constraints)
- [AC Auth Service NoSQL](#ac-auth-service-nosql)
  - [AC Auth Service NoSQL Documents](#ac-auth-service-nosql-documents)
    - [auth\_request\_sessions](#auth_request_sessions)
    - [auth\_request\_sessions for smart home linked door](#auth_request_sessions-for-smart-home-linked-door)
- [AC Log Service](#ac-log-service)
  - [List of documents](#list-of-documents)
  - [Document schema](#document-schema)
    - [auth\_log](#auth_log)
    - [pass\_through\_log](#pass_through_log)
    - [anti\_passback\_log](#anti_passback_log)
    - [management\_service\_audit\_log](#management_service_audit_log)
      - [body](#body)
        - [POST, PUT APIs](#post-put-apis)
        - [Other Operations](#other-operations)
    - [log\_service\_audit\_log](#log_service_audit_log)
  - [How to generate DocID](#how-to-generate-docid)
  - [How to find logs (as an addendum)](#how-to-find-logs-as-an-addendum)
- [Device Shadow](#device-shadow)
  - [Document Schema](#document-schema-1)
- [CSV output format](#csv-output-format)
  - [Output format of `POST /auth/logs/csv`](#output-format-of-post-authlogscsv)
    - [Output format of `auth_log`](#output-format-of-auth_log)
    - [Output format of `pass_through_log`](#output-format-of-pass_through_log)
    - [Output format of `anti_passback_log`](#output-format-of-anti_passback_log)
    - [auth logs sample csv](#auth-logs-sample-csv)
  - [Output format of `POST /resource-event/csv`](#output-format-of-post-resource-eventcsv)
    - [resource event sample csv](#resource-event-sample-csv)

## AC Management Service RDB

This section describes the database schema defined in AC Management Service.
Note that AC Auth Service also reads the database as a read-only client.

Database: Postgres cluster

### List of Tables

#### List of Enum Tables

Each table stores the list of specific enum values.
See [the section](#enum-tables) for details.

| Table name               |
| :----------------------- |
| authentication_methods   |
| device_roles             |
| device_event_code_levels |
| device_event_code_sorts  |
| device_state_code_levels |
| door_event_code_levels   |
| door_event_code_sorts    |
| door_state_code_levels   |
| door_state_code_types    |
| directions               |
| location_types           |

#### List of Master Tables

These tables store the metadata and the relationships between them. The following list is sorted by alphabetical order.

| Table name                      | Notes                                                                                 |
| :------------------------------ | :------------------------------------------------------------------------------------ |
| anti_passback_settings          |                                                                                       |
| buildings                       |                                                                                       |
| devices                         |                                                                                       |
| device_event_codes              |                                                                                       |
| device_state_codes              |                                                                                       |
| doors                           |                                                                                       |
| door_authenticators             | A subtype table for `devices`.                                                        |
| door_authentication_methods     | An intermediate table between `authentication_methods` and `door_authentications`.    |
| door_event_codes                |                                                                                       |
| door_floor_image_settings       | Mapping between door and building floor image                                         |
| door_state_codes                |                                                                                       |
| elevators                       |                                                                                       |
| elevator_access_controllers     | A subtype table for `devices`.                                                        |
| elevator_authentication_methods | An intermediate table between `authentication_methods` and `elevator_authenticators`. |
| elevator_authenticators         | A subtype table for `devices`.                                                        |
| elevator_floors                 | An intermediate table between `elevators` and `floors`.                               |
| elevator_floor_image_settings   | Mapping between elevator and building floor image                                     |
| floors                          |                                                                                       |
| floor_images                    |                                                                                       |
| ota_groups                      |                                                                                       |
| resource_event_logs             | Event logs of all doors/elevators/devices.                                            |

### Table Relationships

The following image shows the relationships between tables.  
Only foreign keys and primary keys are listed in the image.

![database relations](database_relations.png)

### Table Schema

#### Enum Tables

This section describes the definitions of enum tables.
In enum tables, records are expected to be inserted initially, and not expected to be updated.
Each table has the following schema.

| Column name | Type | PK   | FK   | Nullable | Description |
| :---------- | :--- | :--- | :--- | :------- | :---------- |
| name        | text | o    |      |          |             |

##### authentication_methods

This table stores the list of authentication methods that Access Control System supports.  
values:

- qr
- nfc
- face

##### device_roles

This table stores the list of available device roles.  
values:

- doorAuthenticator
- elevatorAuthenticator
- elevatorAccessController

##### device_event_code_levels

This table stores the list of possible device event levels.

- normal
- warn
- error

##### device_event_code_sorts

This table stores the list of possible state changes each event notifies.

- detected
- resolved

##### device_state_code_levels

This table stores the list of possible device state levels.

- normal
- warn
- error

##### directions

This table stores the list of directions where a door authenticator is installed.  
values:

- enter
- exit

##### door_event_code_levels

This table stores the list of possible door state levels.

- normal
- warn
- error

##### door_event_code_sorts

This table stores the list of possible state changes each event notifies.

- detected
- resolved

##### door_state_code_levels

This table stores the list of possible door state levels.

- normal
- warn
- error
- none

##### door_state_code_types

This table stores the list of possible door state types.

- Operating
- Equipment
- Local

##### location_types

This table stores the list of available location types.  
Auth Service will branch the door opening process according to this type.  
values:

- gate
- publicDoor
- collectiveEntrance
- privateHomeDoor
- terrace
- hanare
- amenitySpace

#### anti_passback_settings

| Column name      | Type                     | PK   | FK   | Nullable | Description                                                                                                             |
| :--------------- | :----------------------- | :--- | :--- | :------- | :---------------------------------------------------------------------------------------------------------------------- |
| id               | text                     | o    |      |          | Anti-passback setting ID                                                                                                |
| notes            | text                     |      |      |          | Additional Information                                                                                                  |
| is_bidirectional | boolean                  |      |      |          | If true, anti-passback will be validated bidirectionally. If false, anti-passback will be validated only for exit side. |
| enabled          | boolean                  |      |      |          | If true, anti-passback works. If false, anti-passback does not work.                                                    |
| created_at       | timestamp with time zone |      |      |          |                                                                                                                         |
| updated_at       | timestamp with time zone |      |      |          |                                                                                                                         |

#### buildings

| Column name | Type                     | PK   | FK   | Nullable | Description            |
| :---------- | :----------------------- | :--- | :--- | :------- | :--------------------- |
| id          | text                     | o    |      |          | Building ID            |
| notes       | text                     |      |      |          | Additional information |
| created_at  | timestamp with time zone |      |      |          |                        |
| updated_at  | timestamp with time zone |      |      |          |                        |

#### devices

| Column name  | Type                     | PK   | FK                 | Nullable | Description                                                                 |
| :----------- | :----------------------- | :--- | :----------------- | :------- | :-------------------------------------------------------------------------- |
| id           | text                     | o    |                    |          | Device ID                                                                   |
| notes        | text                     |      |                    |          | Additional information                                                      |
| ota_group_id | text                     |      | ota_groups(id)     |          | OTA group ID to which the device belongs                                    |
| role         | text                     |      | device_roles(name) |          | The role of the device                                                      |
| type         | text                     |      |                    |          | Free description of the device type. example: NFC Controller, Authenticator |
| created_at   | timestamp with time zone |      |                    |          |                                                                             |
| updated_at   | timestamp with time zone |      |                    |          |                                                                             |

#### device_event_codes

The records are based on [the ADR](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0014_DoorDeviceEventConversion.md#device-event).

| Column name   | Type | PK   | FK                             | Nullable | Description                                             |
| :------------ | :--- | :--- | :----------------------------- | :------- | :------------------------------------------------------ |
| code          | text | o    |                                |          | Event code.                                             |
| sort          | text | o    | device_event_code_sorts(name)  |          | detected or resolved.                                   |
| name          | text |      |                                |          | Event name in English.                                  |
| name_jp       | text |      |                                |          | Event name in Japanese.                                 |
| level         | text |      | device_event_code_levels(name) |          | Severity level of the event. Enum: normal/warn/error    |
| dismiss       | bool |      |                                |          | If true, the state will not be shown in the front end.  |
| is_persistent | bool |      |                                |          | Whether the event is temporal or persistent as a state. |

#### device_state_codes

The records are based on [the ADR](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0013_DoorDeviceStateConversion.md#device-state).

| Column name | Type | PK   | FK                             | Nullable | Description                                            |
| :---------- | :--- | :--- | :----------------------------- | :------- | :----------------------------------------------------- |
| code        | text | o    |                                |          | State code.                                            |
| name        | text |      |                                |          | State name in English.                                 |
| name_jp     | text |      |                                |          | State name in Japanese.                                |
| level       | text |      | device_state_code_levels(name) |          | Severity level of the state. Enum: normal/warn/error   |
| dismiss     | bool |      |                                |          | If true, the state will not be shown in the front end. |

#### doors

| Column name              | Type                     | PK   | FK                                               | Nullable | Description                                                   |
| :----------------------- | :----------------------- | :--- | :----------------------------------------------- | :------- | :------------------------------------------------------------ |
| id                       | text                     | o    |                                                  |          | Door ID                                                       |
| building_id              | text                     |      | Composite FK1                                    | o        | An identifier of the building to which the door is installed. |
| floor_name               | text                     |      | Composite FK1                                    | o        | Floor name. example: 4F, B2F                                  |
| location                 | text                     |      |                                                  |          | Free description of the door.                                 |
| location_type            | text                     |      | door_location_types(name)                        |          |                                                               |
| anti_passback_setting_id | text                     |      | anti_passback_settings(id)<br>on delete set null | o        | Anti-passback settings ID to which the door belongs           |
| notes                    | text                     |      |                                                  |          | Additional information                                        |
| created_at               | timestamp with time zone |      |                                                  |          |                                                               |
| updated_at               | timestamp with time zone |      |                                                  |          |                                                               |

##### doors constraints

- Composite FK1: (building_id, floor_name) references floors(building_id, name)

#### door_authenticators

| Column name | Type                     | PK   | FK                               | Nullable | Description                                             |
| :---------- | :----------------------- | :--- | :------------------------------- | :------- | :------------------------------------------------------ |
| device_id   | text                     | o    | devices(id)<br>on delete cascade |          | Device ID                                               |
| door_id     | text                     |      | doors(id)                        | o        | Door ID to which the device is installed                |
| direction   | text                     |      | directions(name)                 | o        | Direction of the door to which the device is installed. |
| is_leader   | boolean                  |      |                                  |          | If true, the device will be treated as a leader device. |
| created_at  | timestamp with time zone |      |                                  |          |                                                         |
| updated_at  | timestamp with time zone |      |                                  |          |                                                         |

##### door_authenticators constraints

- Check constraint: `devices.role` must be `doorAuthenticator`.
- Check constraint: both `door_id` and `direction` must be NULL, or both of them must be non-null.
- Unique constraint: (door_id, is_leader) where `is_leader` is `true`
- Unique constraint: (device_id, door_id, direction)
  - Required to be referred as foreign key

#### door_authentication_methods

This table indirectly refers to the door ID via `door_authenticators`, makes the door ID unique for each authentication device.

| Column name      | Type | PK   | FK                           | Nullable | Description                                 |
| :--------------- | :--- | :--- | :--------------------------- | :------- | :------------------------------------------ |
| authenticator_id | text | o    | Composite FK1                |          | Device ID                                   |
| door_id          | text | o    | Composite FK1                |          | Door ID                                     |
| direction        | text | o    | Composite FK1                |          | Direction to which the device is installed. |
| method           | text | o    | authentication_methods(name) |          | Authentication method                       |

##### door_authentication_methods constraints

- Composite FK1: (authenticator_id, door_id, direction) references door_authenticators(device_id, door_id, direction) on delete cascade

#### door_event_codes

The records are based on [the ADR](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0014_DoorDeviceEventConversion.md#door-event).

| Column name   | Type | PK   | FK                           | Nullable | Description                                             |
| :------------ | :--- | :--- | :--------------------------- | :------- | :------------------------------------------------------ |
| code          | text | o    |                              |          | Event code.                                             |
| sort          | text | o    | door_event_code_sorts(name)  |          | detected or resolved.                                   |
| name          | text |      |                              |          | Event name in English.                                  |
| name_jp       | text |      |                              |          | Event name in Japanese.                                 |
| level         | text |      | door_event_code_levels(name) |          | Severity level of the event. Enum: normal/warn/error    |
| dismiss       | bool |      |                              |          | If true, the state will not be shown in the front end.  |
| is_persistent | bool |      |                              |          | Whether the event is temporal or persistent as a state. |

#### door_floor_image_settings

| Column name | Type    | PK   | FK            | Nullable | Description                         |
| :---------- | :------ | :--- | :------------ | :------- | :---------------------------------- |
| door_id     | text    | o    | Composite FK1 |          | Door ID                             |
| building_id | text    | o    | Composite FK1 |          | Building ID                         |
| floor_name  | text    | o    | Composite FK1 |          | Floor name                          |
| x           | bigint  |      |               |          | Horizontal offset of a door object. |
| y           | bigint  |      |               |          | Vertical offset of a door object.   |
| width       | bigint  |      |               |          | Width from offset `x`               |
| height      | bigint  |      |               |          | Height from offset `y`              |
| rotation    | numeric |      |               |          | Rotation value of a door object.    |

##### door_floor_image_settings constraints

- Composite FK1: (door_id, building_id, floor_name) references doors(id, building_id, floor) on delete cascade

#### door_state_codes

The records are based on [the ADR](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0013_DoorDeviceStateConversion.md#door-state).

| Column name | Type | PK   | FK                           | Nullable | Description                                            |
| :---------- | :--- | :--- | :--------------------------- | :------- | :----------------------------------------------------- |
| code        | text | o    |                              |          | State code.                                            |
| name        | text |      |                              |          | State name in English.                                 |
| name_jp     | text |      |                              |          | State name in Japanese.                                |
| type        | text |      | door_state_code_types(name)  |          | State type. Enum:Operating/Equipment/Local             |
| level       | text |      | door_state_code_levels(name) |          | Severity level of the state. Enum: normal/warn/error   |
| dismiss     | bool |      |                              |          | If true, the state will not be shown in the front end. |

#### elevators

| Column name | Type                     | PK   | FK            | Nullable | Description                                                     |
| :---------- | :----------------------- | :--- | :------------ | :------- | :-------------------------------------------------------------- |
| id          | text                     | o    |               |          | Elevator ID                                                     |
| building_id | text                     |      | buildings(id) | o        | An identifier of the building to which the device is installed. |
| notes       | text                     |      |               |          | Additional information                                          |
| created_at  | timestamp with time zone |      |               |          |                                                                 |
| updated_at  | timestamp with time zone |      |               |          |                                                                 |

##### elevators constraints

- Unique constraint: (id, building_id)
  - Required to be referred as foreign key

#### elevator_access_controllers

| Column name | Type                     | PK   | FK                               | Nullable | Description                                  |
| :---------- | :----------------------- | :--- | :------------------------------- | :------- | :------------------------------------------- |
| device_id   | text                     | o    | devices(id)<br>on delete cascade |          | Device ID                                    |
| elevator_id | text                     |      | elevators(id)                    | o        | Elevator ID to which the device is installed |
| created_at  | timestamp with time zone |      |                                  |          |                                              |
| updated_at  | timestamp with time zone |      |                                  |          |                                              |

##### elevator_access_controllers constraints

- Check constraint: `devices.role` must be `elevatorAccessController`.

#### elevator_authentication_methods

This table indirectly refers to the elevator ID via `elevator_authenticators`, makes the elevator ID unique for each authentication device.

| Column name      | Type | PK   | FK                           | Nullable | Description           |
| :--------------- | :--- | :--- | :--------------------------- | :------- | :-------------------- |
| authenticator_id | text | o    | Composite FK1                |          | Device ID             |
| elevator_id      | text | o    | Composite FK1                |          | Elevator ID           |
| method           | text | o    | authentication_methods(name) |          | Authentication method |

##### elevator_authentication_methods constraints

- Composite FK1: (authenticator_id, elevator_id) references elevator_authenticators(device_id, elevator_id)

#### elevator_authenticators

| Column name | Type                     | PK   | FK                               | Nullable | Description                                  |
| :---------- | :----------------------- | :--- | :------------------------------- | :------- | :------------------------------------------- |
| device_id   | text                     | o    | devices(id)<br>on delete cascade |          | Device ID                                    |
| elevator_id | text                     |      | elevators(id)                    | o        | Elevator ID to which the device is installed |
| created_at  | timestamp with time zone |      |                                  |          |                                              |
| updated_at  | timestamp with time zone |      |                                  |          |                                              |

##### elevator_authenticators constraints

- Check constraint: `devices.role` must be `elevatorAuthenticator`.
- Unique constraint: (device_id, elevator_id)
  - Required to be referred as foreign key

#### elevator_floors

| Column name | Type | PK   | FK                           | Nullable | Description |
| :---------- | :--- | :--- | :--------------------------- | :------- | :---------- |
| elevator_id | text | o    | Composite FK1                |          | Elevator ID |
| building_id | text | o    | Composite FK1, Composite FK2 |          | Building ID |
| floor_name  | text | o    | Composite FK2                |          | Floor name  |

##### elevator_floors constraints

- Composite FK1: (elevator_id, building_id) references elevators(id, building_id) on delete cascade
- Composite FK2: (building_id, floor_name) references floors(building_id, name) on delete cascade

#### elevator_floor_image_settings

| Column name | Type    | PK   | FK                            | Nullable | Description                              |
| :---------- | :------ | :--- | :---------------------------- | :------- | :--------------------------------------- |
| elevator_id | text    | o    | Composite FK1                 |          | Elevator ID                              |
| building_id | text    | o    | Composite FK1,  Composite FK2 |          | Building ID                              |
| floor_name  | text    |      | Composite FK2                 |          | Floor name                               |
| x           | bigint  |      |                               |          | Horizontal offset of an elevator object. |
| y           | bigint  |      |                               |          | Vertical offset of an elevator object.   |
| width       | bigint  |      |                               |          | Width from offset `x`                    |
| height      | bigint  |      |                               |          | Height from offset `y`                   |
| rotation    | numeric |      |                               |          | Rotation value of an elevator object.    |

##### elevator_floor_image_settings constraints

- Composite FK1: (elevator_id, building_id) references elevators(id, building_id) on delete cascade
- Composite FK2: (building_id, floor_name) references floors(building_id, name) on delete cascade

#### floors

| Column name | Type | PK   | FK                                 | Nullable | Description                  |
| :---------- | :--- | :--- | :--------------------------------- | :------- | :--------------------------- |
| building_id | text | o    | buildings(id)<br>on delete cascade |          | Building ID                  |
| name        | text | o    |                                    |          | Floor name. example: 4F, B2F |

#### floor_images

| Column name | Type | PK   | FK            | Nullable | Description |
| :---------- | :--- | :--- | :------------ | :------- | :---------- |
| building_id | text | o    | Composite FK1 |          | Building ID |
| floor_name  | text | o    | Composite FK1 |          | Floor name  |
| image       | blob |      |               |          | Floor image |

##### floor_images constraints

- Composite FK1: (building_id, floor_name) references floors(building_id, name) on delete cascade

#### ota_groups

| Column name      | Type                     | PK   | FK   | Nullable | Description                                                                      |
| :--------------- | :----------------------- | :--- | :--- | :------- | :------------------------------------------------------------------------------- |
| id               | text                     | o    |      |          | OTA group ID                                                                     |
| provision_secret | bytea                    |      |      | o        | Provision secret for the group. The value is stored with encryption. Default: '' |
| notes            | text                     |      |      |          | Additional information                                                           |
| created_at       | timestamp with time zone |      |      |          |                                                                                  |
| updated_at       | timestamp with time zone |      |      |          |                                                                                  |

#### resource_event_logs

There are no foreign keys set in this table to preserve logs as they are.

| Column name   | Type                     | PK   | FK   | Nullable | Description                                                                                                                                                                                       |
| :------------ | :----------------------- | :--- | :--- | :------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| id            | text                     | o    |      |          | A unique identifier of the event. It expects a string with [UUID v7](https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html#section-5.2) format to optimize index update. |
| timestamp     | timestamp with time zone |      |      |          | A timestamp at which the event occurred. There can be multiple records with the same timestamp.                                                                                                   |
| building_id   | text                     |      |      |          | A unique identifier of the building to which the door/elevator is installed.                                                                                                                      |
| floor         | text                     |      |      |          | Floor name of the building to which the door/elevator is installed.                                                                                                                               |
| door_id       | text                     |      |      |          | A unique identifier of the door to which the device is installed.                                                                                                                                 |
| elevator_id   | text                     |      |      |          | A unique identifier of the elevator to which the device is installed.                                                                                                                             |
| device_id     | text                     |      |      |          | A unique identifier of the device.                                                                                                                                                                |
| code          | text                     |      |      |          | Event code.                                                                                                                                                                                       |
| level         | text                     |      |      |          | Severity level of the event.                                                                                                                                                                      |
| name          | text                     |      |      |          | English name of the event.                                                                                                                                                                        |
| name_jp       | text                     |      |      |          | Japanese name of the event.                                                                                                                                                                       |
| sort          | text                     |      |      |          | It denotes how a state has been changed.                                                                                                                                                          |
| detail        | text                     |      |      |          | Detailed information on the event.                                                                                                                                                                |
| dismiss       | bool                     |      |      |          | If true, the front-end application may omit to show this event.                                                                                                                                   |
| is_persistent | bool                     |      |      |          | Whether the state is persistent or not.                                                                                                                                                           |

##### resource_event_logs indexes

- timestamp
- (door_id, timestamp)
- (elevator_id, timestamp)

## AC Management Service Redis

This section describes document schema stored in Redis.
NOTE: we currently ignore update conflicts caused by multiple clients updating the same key.

### Door state

Type: String
Key: `door:{doorId}:state`

This document contains door states.
Each document will be updated whenever the backend receives a door state message.
Since the document follows [the message schema sent from devices](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/adr/integration/ADR-INTG-0002_DoorDeviceStateEventNotification.md#shared-door-state-message), see the doc for details.

| Field name                    | Type         | Description                                                                                                                                                                                       |
| :---------------------------- | :----------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| timestampMs                   | number       | Timestamp of when the message sent from devices. The value shows the latest time when the document is updated. Unit: milliseconds                                                                 |
| state                         | object       | Door states. See [the doc](https://github.tri-ad.tech/R-D-WCM/ac-edge-controller-app/blob/main/docs/adr/ADR-CTRL-0001_DoorDeviceStateEventNotification.md) for the detailed format.               |
| state.{stateName}.timestampMs | number       | In addition to the fields described in the above document, the backend adds a timestamp for each `stateName` key. This field shows the latest time when the value of the key is updated. Unit: ms |
| state.{stateName}.isOn        | boolean      | `isOn` indicates whether the corresponding functionality is enabled or the corresponding behavior is detected.                                                                                    |
| state.{stateName}.code        | string       | A unique code number of the state property.                                                                                                                                                       |
| state.{stateName}.type        | string(enum) | A state type of the state property. Enum values: `Operating, Equipment, Local`                                                                                                                    |

Example:

```json
{
  "timestampMs": 1234567890123,
  "state": {
    "stateNameA": {
      "timestampMs": 1234567890123,
      "code": "s0001",
      "isOn": true, 
      "type": "Operating"
    },
    "stateNameB": {
      "timestampMs": 1234567890000,
      "code": "s0002",
      "isOn": false,
      "type": "Equipment"
    }
  }
}
```

### Device state

Type: String
Key: `device:{deviceId}:state`

This document contains device states.
Each document will be updated whenever the backend receives a device state message, or by a periodic operation.

| Field name  | Type   | Description                                                                                         |
| :---------- | :----- | :-------------------------------------------------------------------------------------------------- |
| timestampMs | number | Timestamp of when the message was sent from devices. Unit: milliseconds                             |
| detail      | string | Details of the error state. It will be empty if the current state (`name` property) is not `error`. |
| code        | string | A unique code corresponding to the state.                                                           |

```json
{
  "timestampMs": 1234567890123,
  "detail": "details of the error",
  "code": "UNIQUE_CODE"
}
```

### Door state (merged with device states)

Type: String
Key: `door:{doorId}:merged-state`

This document is generated from both the state of the door and the state of the installed devices.
Each document will be updated whenever any of a source door state or a devices state is updated.
It is assumed that the door state data is sent to the front-end applications in this format.

| Field name                 | Type             | Description                                                                                                                       |
| :------------------------- | :--------------- | :-------------------------------------------------------------------------------------------------------------------------------- |
| type                       | string(constant) | Resource type. A constant value `Door` is set.                                                                                    |
| timestampMs                | number           | Timestamp of when the message sent from devices. The value shows the latest time when the document is updated. Unit: milliseconds |
| buildingId                 | string           | ID of the building where the door is installed.                                                                                   |
| floor                      | string           | Name of the floor where the door is installed.                                                                                    |
| operatingState             | object           | Operating state of the door.                                                                                                      |
| operatingState.code        | string           | A unique identifier of the current state.                                                                                         |
| operatingState.name        | string           | English name of the current state.                                                                                                |
| operatingState.nameJp      | string           | Japanese name of the current state.                                                                                               |
| operatingState.level       | string           | Severity of the current state.                                                                                                    |
| equipmentState             | array(object)    | General equipment state of the door other than operating state. Each item indicates the current status of a property.             |
| equipmentState[].code      | string           | A unique identifier of the property.                                                                                              |
| equipmentState[].name      | string           | English name of the property.                                                                                                     |
| equipmentState[].nameJp    | string           | Japanese name of the property.                                                                                                    |
| equipmentState[].dismiss   | bool             | If true, the front-end application may omit to show the property.                                                                 |
| equipmentState[].value     | bool             | Current state of the property.                                                                                                    |
| deviceState                | array(object)    | Statuses of devices installed at the door. Each element indicates the current status of a device.                                 |
| deviceState[].deviceId     | string           | Device ID.                                                                                                                        |
| deviceState[].direction    | string           | The direction where the device is installed.                                                                                      |
| deviceState[].state.code   | string           | A unique identifier of the current state.                                                                                         |
| deviceState[].state.name   | string           | English name of the current state.                                                                                                |
| deviceState[].state.nameJp | string           | Japanese name of the current state.                                                                                               |
| deviceState[].state.level  | string           | Severity of the current state.                                                                                                    |
| deviceState[].state.detail | string           | Details of the current state.                                                                                                     |

Example:

```json
{
  "type": "door",
  "timestampMs": 1234567890123,
  "buildingId": "building1",
  "floor": "1F",
  "doorId": "door1",
  "operatingState": {
    "name": "fireAlarm",
    "nameJp": "火報信号検知中",
    "level": "error",
    "code": "S_OP_0101"
  },
  "equipmentState": [
    {
      "name": "flapperOpened",
      "nameJp": "ゲートフラッパー開",
      "value": true,
      "dismiss": false,
      "code": "S_EQ_0008"
    }
  ],
  "deviceState": [
    {
      "deviceId": "gate-nfc-12345",
      "direction": "enter",
      "state": {
        "name": "normally",
        "nameJp": "正常",
        "level": "normal",
        "code": "S_DE_2001"
      }
    },
    {
      "deviceId": "gate-nfc-67890",
      "direction": "exit",
      "state": {
        "name": "error",
        "nameJp": "エラー",
        "level": "error",
        "code": "S_DE_2003",
        "detail": "An error occurred"
      }
    }
  ]
}
```

### door_event

This document contains various event of doors.

- Type: String
- Key: `door:{doorId}:event:{timestampMs}`

| Field name  | Type   | Description                                                                                                                                                                                                                                  |
| :---------- | :----- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| timestampMs | number | Timestamp of when the message sent from devices. Unit: milliseconds                                                                                                                                                                          |
| event       | object | Door events. See [the doc](https://github.tri-ad.tech/R-D-WCM/ac-edge-controller-app/blob/main/docs/adr/ADR-CTRL-0001_DoorDeviceStateEventNotification.md) for the detailed format.  It includes only events that have changed at this time. |

```json
{
  "timestampMs": 123456789,
  "event": {
    "eventNameA": {
      "sort": "detected"
    },
    "eventNameB": {   
      "sort": "resolved"
    }
  }
}
```

#### door_event indexes

Additionally, we define the following sorted sets as indexes.
[sorted sets reference](https://redis.io/docs/data-types/sorted-sets/)
  
index1

- Key: `index-door-event-all`
- Score: the value of timestampMs
- Value: `{doorId}:event:{timestampMs}`

index2

- key: `index-door-event-{doorId}`
- Score: the value of timestampMs
- Value: `{doorId}:event:{timestampMs}`

#### how to set and get door events

Set:

```redis
SET door:{doorId}:event:{timestampMs} '{json value}'
ZADD index-door-event-all {timestampMs} {doorId}:event:{timestampMs}
ZADD index-door-event-{doorId} {timestampMs} {doorId}:event:{timestampMs}
```

Get:

```redis
GET door:{doorId}:event:{timestampMs}
```

Get the list of all events for all devices:

```redis
ZRANGE index-door-event-all 0 -1
1) "value1"
2) "value2"
...

MGET door:{value1} doorId:{value2} ...
```

Get the list of all events for a device:

```redis
ZRANGE index-door-event-{deviceId} 0 -1
1) "value1"
2) "value2"
...

MGET door:{value1} door:{value2} ...
```

### device_event

This document contains various event of doors.

Key: `device:{deviceId}:event:{timestampMs}`

| Field name  | Type   | Description                                                                                                                                                                                                                                    |
| :---------- | :----- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| timestampMs | number | Timestamp of when the message sent from devices. Unit: milliseconds                                                                                                                                                                            |
| event       | object | device events. See [the doc](https://github.tri-ad.tech/R-D-WCM/ac-edge-controller-app/blob/main/docs/adr/ADR-CTRL-0001_DoorDeviceStateEventNotification.md) for the detailed format.  It includes only events that have changed at this time. |

```json
{
  "timestampMs": 123456789,
  "event": {
    "eventNameA": {
      "sort": "detected"
    },
    "eventNameB": {   
      "sort": "resolved"
    }
  }
}
```

#### device_event indexes

Additionally, we define the following sorted sets as indexes.
[sorted sets reference](https://redis.io/docs/data-types/sorted-sets/)
  
index1

- Key: `index-device-event-all`
- Score: the value of timestampMs
- Value: `{deviceId}:event:{timestampMs}`

index2

- key: `index-device-event-{deviceId}`
- Score: the value of timestampMs
- Value: `{deviceId}:event:{timestampMs}`

#### how to set and get device events

see the section [how to set and get door events](#how-to-set-and-get-door-events)

## AC Auth Service RDB

In Auth Service, most of master data is retrieved from the database for AC Management Service.  
However, transaction logs are stored in the database for Auth Service.

Database: Postgresql (TBD. SecureKVS is also acceptable for current use cases)

### AC Auth Service List of Tables

| Table name              | Description                               |
| :---------------------- | :---------------------------------------- |
| anti_passback_open_logs | A table to check anti-passback violation. |

### AC Auth Service Table Schema

#### anti_passback_open_logs

Since the table is separated from master data, any foreign key constraints are not set.

| Column name              | Type                     | PK   | FK   | Nullable | Description                                                             |
| :----------------------- | :----------------------- | :--- | :--- | :------- | :---------------------------------------------------------------------- |
| id                       | text                     | o    |      |          | Log ID (UUID)                                                           |
| woven_id                 | text                     |      |      |          | Woven ID of the user who have passed                                    |
| started_door_id          | text                     |      |      |          | Door ID of the door on which the anti-passback check was initiated      |
| finished_door_id         | text                     |      |      | o        | Door ID of the door for which the anti-passback check has been finished |
| anti_passback_setting_id | text                     |      |      |          | Anti-passback setting ID                                                |
| started_at               | timestamp with time zone |      |      |          | Timestamp of when the user entered.                                     |
| finished_at              | timestamp with time zone |      |      | o        | Timestamp of when the user exited.                                      |
| direction                | text                     |      |      |          | Direction of entered at start of check.                                 |

##### anti_passback_open_logs constraints

- Check constraint
  - `woven_id` and `anti_passback_setting_id` and `direction` combination must be unique if `finished_door_id` is null.
  - `finished_at` must be null if `finished_door_id` is null.
  - `finished_at` must not be null if `finished_door_id` is not null.

## AC Auth Service NoSQL

This section describes document schema stored in Redis.

### AC Auth Service NoSQL Documents

#### auth_request_sessions

This is a document schema to store the information of each auth request.
Each document will be created when Auth Service returns an auth result to clients, and will be referred when Auth Service processes the corresponding pass-through requests.
Each document expires 70 seconds after creation.
This takes into account the maximum time to detect dwell within the gate (60s) and the maximum time before a pass-through log is sent (≒16.5s).
It will be configured by the [EXPIRE command](https://redis.io/commands/expire/)

- Type: String
- Key: `authz:{requestId}`

| Field name | Type   | Description                                            | Notes                                 |
| :--------- | :----- | :----------------------------------------------------- | :------------------------------------ |
| wovenId    | string | The woven ID of the authenticated user                 | Required updating anti-passback state |
| deviceId   | string | The device ID of the device that sent the auth request | Required updating anti-passback state |
| doorId     | string | Door ID                                                | Required updating anti-passback state |
| direction  | string | The direction in which the user tried to pass through  | Required updating anti-passback state |

#### auth_request_sessions for smart home linked door

This is a document schema for storing information about the authentication request for smart home linked doors.
Each document is created when the Auth Service returns authentication results to the client and is referenced when processing door control requests.
The environment variable {SMART_HOME_REQUEST_SESSION_EXPIRATION_SEC} determines the expiration duration of each document.

- Type: String
- Key: `smartHomeAuthz:{requestId}`

| Field name | Type   | Description                                                            | Notes                           |
| :--------- | :----- | :--------------------------------------------------------------------- | :------------------------------ |
| token      | string | Token for authentication of the user who made the door control request | Used for Smart Home Backend API |
| deviceId   | string | The device ID of the device that sent the auth request                 |                                 |
| doorId     | string | Door ID                                                                |                                 |
| direction  | string | The direction in which the user tried to pass through                  |                                 |

## AC Log Service

The database for AC Log Service stores some logs that need to be stored with encryption or to be notified to the front end.
Other AC services send logs to the database directly.

Database: SecureKVS (CouchDB)  
Database Name: access-logs  
(The actual database name is namespace + database name, which is `ac-access-control_access-logs`.)

### List of documents

The following parameters MUST be set to all documents in the DB.

| Field name  | Type   | Description                              |
| :---------- | :----- | :--------------------------------------- |
| +logType    | string | Indicates the log type for this document |
| +logContent | object | Different log contents for each LogType  |

The logType has the following types.

| Log Type                     | Description                                                                 |
| :--------------------------- | :-------------------------------------------------------------------------- |
| auth_log                     | authentication log                                                          |
| pass_through_log             | pass-through log that corresponds to an auth_log document                   |
| anti_passback_log            | logs stored when an anti-passback violation is detected                     |
| management_service_audit_log | Logs stored when management service APIs other than GET are executed        |
| log_service_audit_log        | Logs stored when logs other than management_service_audit_log are retrieved |

`logType` indicates the type of the document. You can use `logType` to filter by a document type.

Example:

```json
{
  "+logType": "auth_log",
  "+logContent": {
    ... Different log content for each LogType
  }
}
```

The log contents for each logType will be described in the following sections.

### Document schema

The `+` prefix denotes that the field will be stored without encryption.

#### auth_log

| Field name            | Type   | Description                                                                                             |
| :-------------------- | :----- | :------------------------------------------------------------------------------------------------------ |
| +wovenId              | string | Woven ID of the user who tries to pass through.                                                         |
| +deviceId             | string | Device ID of the authenticator that sent the request                                                    |
| +doorId               | string | Door ID. It's undefined if the request is for an elevator.                                              |
| +elevatorId           | string | Elevator ID. It's undefined if the request is for a door.                                               |
| +buildingId           | string | A target building ID.                                                                                   |
| +floorName            | string | A target floor name.                                                                                    |
| +requestId            | string | The identifier of the authn / authz request                                                             |
| +direction            | string | The direction in which the user tried to pass through. It's undefined if the request is for an elevator |
| +timestampMs          | number | Timestamp when the log was stored                                                                       |
| +deviceTimestampMs    | number | Timestamp when the authenticator sent the request                                                       |
| +authenticationMethod | string | Authentication method                                                                                   |
| +result               | bool   | Authorization result                                                                                    |
| +errorType            | string | Type of authentication failure. `authn` or `authz` or `unknown` is set.                                 |
| +errorCode            | string | Identifier indicating the cause of the authorization result false                                       |
| +errorMessage         | string | Message indicating details of errorCode                                                                 |

Example:

- Authentication result is successful (door authentication)

```json
{
  "+logType": "auth_log",
  "+logContent": {
    "+wovenId": "01234567-0123-0123-0123-0123456789ab",
    "+deviceId": "xxxxxxxxxxxx",
    "+doorId": "sitegate-n-m",
    "+buildingId": "building-A",
    "+floorName": "2F",
    "+requestId": "01234567-0123-0123-0123-0123456789ab",
    "+direction": "enter",
    "+timestampMs": 1688020433123,
    "+deviceTimestampMs": 1688020433000,
    "+authenticationMethod": "face",
    "+result": true,
    "+errorType": "",
    "+errorCode": "",
    "+errorMessage": ""
  }
}
```

- Authentication result failed

```json
{
  "+logType": "auth_log",
  "+logContent": {
    "+wovenId": "01234567-0123-0123-0123-0123456789ab",
    "+deviceId": "xxxxxxxxxxxx",
    "+doorId": "sitegate-n-m",
    "+buildingId": "building-A",
    "+floorName": "2F",
    "+requestId": "01234567-0123-0123-0123-0123456789ab",
    "+direction": "enter",
    "+timestampMs": 1688020433123,
    "+deviceTimestampMs": 1688020433000,
    "+authenticationMethod": "face",
    "+result": false,
    "+errorType": "authn",
    "+errorCode": "E01002",
    "+errorMessage": "Undetected Face"
  }
}
```

- Authentication result is successful (elevator authentication)

```json
{
  "+logType": "auth_log",
  "+logContent": {
    "+wovenId": "01234567-0123-0123-0123-0123456789ab",
    "+deviceId": "xxxxxxxxxxxx",
    "+elevatorId": "elevator-x",
    "+buildingId": "building-A",
    "+floorName": "2F",
    "+requestId": "01234567-0123-0123-0123-0123456789ab",
    "+timestampMs": 1688020433123,
    "+deviceTimestampMs": 1688020433000,
    "+authenticationMethod": "face",
    "+result": true,
    "+errorType": "",
    "+errorCode": "",
    "+errorMessage": ""
  }
}
```


#### pass_through_log

| Field name         | Type   | Description                                           |
| :----------------- | :----- | :---------------------------------------------------- |
| +timestampMs       | number | Timestamp when the log is stored                      |
| +deviceTimestampMs | number | Timestamp when the authenticator sent the request     |
| +requestId         | string | Request ID of the corresponding authn / authz request |

Example:

```json
{
  "+logType": "pass_through_log",
  "+logContent": {
    "+timestampMs": 1688020433123,
    "+deviceTimestampMs": 1688020433000,
    "+requestId": "01234567-0123-0123-0123-0123456789ab"
  }
}
```

#### anti_passback_log

| Field name             | Type   | Description                                                                                      |
| :--------------------- | :----- | :----------------------------------------------------------------------------------------------- |
| +wovenId               | string | Woven ID of the user who tries to pass through.                                                  |
| +timestampMs           | number | Timestamp when the log is stored                                                                 |
| +deviceTimestampMs     | number | Timestamp when the authenticator sent the request                                                |
| +deviceId              | string | Device ID of the authenticator that sent the log                                                 |
| +doorId                | string | Door ID of the door to which the authenticator is installed                                      |
| +buildingId            | string | A target building ID.                                                                            |
| +floorName             | string | A target floor name.                                                                             |
| +antiPassbackSettingId | string | Anti-passback setting ID of the setting to which the door belongs                                |
| +direction             | string | Door passage direction.                                                                          |
| +authenticationMethod  | string | Authentication method                                                                            |
| +violationType         | string | Type of violation (Exit without valid entrance log, re-entrance with valid entrance log existed) |

Example:

```json
{
  "+logType": "anti_passback_log",
  "+logContent": {
    "+wovenId": "01234567-0123-0123-0123-0123456789ab",
    "+timestampMs": 1688020433123,
    "+deviceTimestampMs": 1688020433000,
    "+deviceId": "xxxxxxxxxxxx",
    "+doorId": "sitegate-n-m",
    "+buildingId": "building-A",
    "+floorName": "2F",
    "+antiPassbackSettingId": "sitegate",
    "+direction": "enter",
    "+violationType": "re-entrance with valid entrance log existed"
  }
}
```

#### management_service_audit_log

| Field name      | Type   | Description                                                         |
| :-------------- | :----- | :------------------------------------------------------------------ |
| +operatorId     | string | Woven ID of the user who performed the management service operation |
| +method         | string | Method of the executed API                                          |
| +host           | string | host of the executed API                                            |
| +path           | string | Path of the executed API                                            |
| +pathParameter  | object | Path parameters of the executed API                                 |
| +queryParameter | object | Query parameters of the executed API                                |
| +resultCode     | number | Status code of the response to the API executed by the user         |
| +timestampMs    | number | Unix timestamp (ms) of when the log was retrieved                   |
| +requestId      | string | The value of `X-Request-ID` header in the request                   |
| +body           | object | Request body data of executed API                                   |

##### body

###### POST, PUT APIs

If the API executed was the following, set the JSON data of the API request body to `+body`.

- POST /devices
- PUT /devices/{deviceId}
- PUT /devices/{deviceId}/shadow
- POST /devices/{deviceId}/command
- POST /doors
- PUT /doors/{doorId}
- POST /ota-groups
- PUT /ota-groups/{otaGroupId}
- PUT /ota-groups/{otaGroupId}/shadows
- POST /anti-passback-settings
- PUT /anti-passback-settings/{antiPassbackSettingId}
- POST /elevators
- PUT /elevators/{elevatorId}
- POST /buildings
- PUT /buildings/{buildingId}

Example:

```json
{
  "+logType": "management_service_audit_log",
  "+logContent": {
    "+operatorId": "01234567-0123-0123-0123-0123456789ab",
    "+method": "PUT",
    "+host": "https://ac-access-control.cityos-dev.woven-planet.tech",
    "+path": "/management/api/v1/devices/:deviceId",
    "+pathParameter": {
        "deviceId": "device1"
    },
    "+queryParameter": {
        "query1": ["val1","val2"]
    },
    "+resultCode": 200,
    "+timestampMs": 1688020433123,
    "+requestId": "000011112222-3333-4444-5555-666677778889",
    "+body": {
      "id": "authenticator-1",
      "notes": "entrance",
      "otaGroupId": "gate-authenticator",
      "role": "doorAuthenticator",
      "type": "Authenticator",
      "door": {
          "doorId": "door-1",
          "direction": "enter",
          "authMethods": [
              "face"
          ],
          "isLeader": false
      }
    }
  }
}
```

※Since this is an example, a `queryParameter` is set that is not actually set.

###### Other Operations

For the following APIs, set an empty value for body, since information is sufficient only for pathParameter or queryParameter.

- DELETE /devices/{deviceId}
- DELETE /doors/{doorId}
- DELETE /ota-groups/{otaGroupId}
- DELETE /anti-passback-settings/{antiPassbackSettingId}
- DELETE /elevators/{elevatorId}
- DELETE /buildings/{buildingId}

Example:

```json
{
  "+logType": "management_service_audit_log",
  "+logContent": {
    "+operatorId": "01234567-0123-0123-0123-0123456789ab",
    "+method": "DELETE",
    "+host": "https://ac-access-control.cityos-dev.woven-planet.tech",
    "+path": "/management/api/v1/devices/:deviceId",
    "+pathParameter": {
        "deviceId": "authenticator-1"
    },
    "+queryParameter": {
        "ignoreIota": ["true"]
    },
    "+resultCode": 204,
    "+timestampMs": 1688020433123,
    "+requestId": "000011112222-3333-4444-5555-666677778889",
    "+body": {}
  }
}
```

#### log_service_audit_log

| Field name      | Type   | Description                                                         |
| :-------------- | :----- | :------------------------------------------------------------------ |
| +operatorId     | string | Woven ID of the user who accessed the log                           |
| +requestUrl     | string | Full path of the executed API                                       |
| +pathParameter  | string | Path parameters of the executed API                                 |
| +queryParameter | string | Query parameters of the executed API                                |
| +resultCode     | number | Status code of the response to the logging API executed by the user |
| +timestampMs    | number | Unix timestamp (ms) of when the log was retrieved                   |
| +requestId      | string | The value of `X-Request-ID` header in the request                   |

Example:

```json
{
  "+logType": "log_service_audit_log",
  "+logContent": {
    "+operatorId": "01234567-0123-0123-0123-0123456789ab",
    "+requestUrl": "https://ac-access-control.cityos-dev.woven-planet.tech/log/api/v1/audit/logs",
    "+pathParameter": "",
    "+queryParameter": "",
    "+resultCode": 200,
    "+timestampMs": 1688020433123,
    "+requestId": "000011112222-3333-4444-5555-666677778889"
  }
}
```

### How to generate DocID

Need to set unique DocID when writing logs in SecureKVS.
Set `logType + "_" + Timestamp(Nano)` + "_" + {[0-9a-zA-Z]{4}} as DocID.
example: `pass_through_log_1691627932000000000_Az09`

### How to find logs (as an addendum)

When outputting logs, it is assumed that logs are output for each LogType and the value of a specific parameter of Content is used as the key.
In such cases, multiple search conditions can be specified and retrieved with `_find` API in couchDb.  
For example, to get only a specific deviceId in auth_log, specify as follows.

```json
{
  "selector": { 
    "+logType":"auth_log",
    "+logContent":{
      "+deviceId":"yyyyyyyyyyyy"
      }
    }, 
    "limit": 100,
    "skip": 0 
}
```

For example, to get the time range of timestamp, specify the following

```json
{
  "selector": { 
    "$and":[
      {
        "+logType":"pass_through_log",
        "+logContent":{
          "+deviceTimestampMs":{"$gte":1688020433000}
        }
      },
      {
        "+logType":"pass_through_log",
        "+logContent":{
          "+deviceTimestampMs":{
            "$lte":1688020436000
          }
        }
      }
    ]
  }, 
  "limit": 100,
  "skip": 0 
}
```

## Device Shadow

This section describes the json schema of device shadows stored in IoTA.
The document consists of two property keys.

1. `configuration`: properties in this key will be specified in the `desired` document.
2. `state`: properties in this key will not be specified in the `desired` document, but they will be reported from device side.

### Document Schema

The key `desired` contains documents specified as desired.
The key `reported` contains documents reported from the device as current configurations and states.
The document `doc/management_mqtt.yaml` defines the schema as a model `DeviceReported`.
Please refer to it for details.

Note: Since we could not find a suitable editor, we have defined the schema as an OpenAPI model instead of defining it as a JSON schema.

```json
{
  "desired":{
    "configuration":{},
  },
  "reported":{
    "configuration":{},
    "state":{},
  }
}
```

## CSV output format

The following API outputs log information in CSV format.

- POST /auth/logs/csv
- POST /resource-event/csv

This section describes the output fields and order.
Common to all CSV outputs, the first line outputs the field name.

### Output format of `POST /auth/logs/csv`

The following fields are output as CSV.

| Field name            | Type   | Description                                                                                             |
| :-------------------- | :----- | :------------------------------------------------------------------------------------------------------ |
| +timestampMs          | number | Timestamp when the log was stored                                                                       |
| logType               | string | Indicates the log type for this document. Either `auth_log`,`pass_through_log`,`anti_passback_log`      |
| deviceTimestampMs     | number | Timestamp when the authenticator sent the request                                                       |
| wovenId               | string | Woven ID of the user who tries to pass through.                                                         |
| normativeFirstName    | string | Normative first name of person corresponding to WovenID                                                 |
| normativeGivenName    | string | Normative given name of person corresponding to WovenID                                                 |
| latinFirstName        | string | Latin first name of person corresponding to WovenID                                                     |
| latinGivenName        | string | Latin given name of person corresponding to WovenID                                                     |
| email                 | string | Email address of person corresponding to WovenID                                                        |
| deviceId              | string | Device ID of the authenticator that sent the request                                                    |
| doorId                | string | Door ID. It's undefined if the request is for an elevator.                                              |
| elevatorId            | string | Elevator ID. It's undefined if the request is for a door.                                               |
| buildingId            | string | A target building ID.                                                                                   |
| floorName             | string | A target floor name.                                                                                    |
| requestId             | string | The identifier of the authn / authz request                                                             |
| direction             | string | The direction in which the user tried to pass through. It's undefined if the request is for an elevator |
| authenticationMethod  | string | Authentication method                                                                                   |
| result                | bool   | Authorization result                                                                                    |
| authErrorType         | string | Type of authentication failure. `authn` or `authz` or `unknown` is set.                                 |
| authErrorCode         | string | Identifier indicating the cause of the authorization result false                                       |
| authErrorMessage      | string | Message indicating details of errorCode                                                                 |
| antiPassbackSettingId | string | Anti-passback setting ID of the setting to which the door belongs                                       |
| violationType         | string | Type of violation (Exit without valid entrance log, re-entrance with valid entrance log existed)        |

The order is as follows

| timestampMs | logType | deviceTimestampMs | wovenId | normativeFirstName | normativeGivenName | latinFirstName | latinGivenName | email | deviceId | doorId | elevatorId | buildingId | floorName | direction | authenticationMethod | result | authErrorType | authErrorCode | authErrorMessage | requestId | antiPassbackSettingId | antiPassbackViolationType |
| :---------- | :------ | :---------------- | :------ | :----------------- | :----------------- | :------------- | :------------- | :---- | :------- | :----- | :--------- | :--------- | :-------- | :-------- | :------------------- | :----- | :------------ | :------------ | :--------------- | :-------- | :-------------------- | :------------------------ |

Since the fields used for each log type are different, the following information is provided for each log type.

#### Output format of `auth_log`

The fields used in the auth_log and examples are listed below

| timestampMs   | logType  | deviceTimestampMs | wovenId                              | normativeFirstName | normativeGivenName | latinFirstName | latinGivenName | email                 | deviceId   | doorId             | elevatorId        | buildingId | floorName | direction | authenticationMethod | result | authErrorType | authErrorCode | authErrorMessage | requestId                            | antiPassbackSettingId | antiPassbackViolationType |
| :------------ | :------- | :---------------- | :----------------------------------- | :----------------- | :----------------- | :------------- | :------------- | :-------------------- | :--------- | :----------------- | :---------------- | :--------- | :-------- | :-------- | :------------------- | :----- | :------------ | :------------ | :--------------- | :----------------------------------- | :-------------------- | :------------------------ |
| use           | use      | use               | use                                  | use                | use                | use            | use            | use                   | use        | use only gate/door | use only elevator | use        | use       | use       | use                  | use    | not use       | not use       | not use          | use                                  | not use               | not use                   |
| 1722573396000 | auth_log | 1722573395900     | 01234567-0123-0123-0123-0123456789ab | 鈴木               | 太郎               | suzuki         | taro           | suzuki.taro@woven.com | deviceXXXX | doorXXXX           | -                 | buildingA  | 1F        | enter     | face                 | true   | -             | -             | -                | ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaaa | -                     | -                         |
| 1722573396000 | auth_log | 1722573395900     | 01234567-0123-0123-0123-0123456789ab | 鈴木               | 太郎               | suzuki         | taro           | suzuki.taro@woven.com | deviceXXXX | -                  | elevatorXXXX      | buildingA  | 2F        | enter     | face                 | true   | -             | -             | -                | ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaab | -                     | -                         |
| 1722573396000 | auth_log | 1722573395900     | -                                    | -                  | -                  | -              | -              | -                     | deviceXXXX | -                  | doorXXXX          | buildingA  | 1F        | enter     | face                 | false  | authn         | E01003        | Too small face   | ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaab | -                     | -                         |

The actual CSV text output will look like this.

```csv
timestampMs,logType,deviceTimestampMs,wovenId,normativeFirstName,normativeGivenName,latinFirstName,latinGivenName,email,deviceId,doorId,elevatorId,buildingId,floorName,direction,authenticationMethod,result,authErrorType,authErrorCode,authErrorMessage,requestId,antiPassbackSettingId,antiPassbackViolationType
1722573396000,auth_log,1722573395900,01234567-0123-0123-0123-0123456789ab,鈴木 ,太郎 ,suzuki,taro,suzuki.taro@woven.com,deviceXXXX,doorXXXX,,buildingA,1F,enter,face,true,,,,ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaaa,,
1722573396000,auth_log,1722573395900,01234567-0123-0123-0123-0123456789ab,鈴木 ,太郎 ,suzuki,taro,suzuki.taro@woven.com,deviceXXXX,-,elevatorXXXX,buildingA,2F,,face,true,,,,ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaab,,
1722573396000,auth_log,1722573395900,,,,,,,deviceXXXX,doorXXXX,,buildingA,1F,enter,face,false,authn,E01003,Too small face,ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaac,,
:
```

#### Output format of `pass_through_log`

The fields used in the auth_log and examples are listed below

| timestampMs   | logType          | deviceTimestampMs | wovenId | normativeFirstName | normativeGivenName | latinFirstName | latinGivenName | email   | deviceId | doorId  | elevatorId | buildingId | floorName | direction | authenticationMethod | result  | authErrorType | authErrorCode | authErrorMessage | requestId                            | antiPassbackSettingId | antiPassbackViolationType |
| :------------ | :--------------- | :---------------- | :------ | :----------------- | :----------------- | :------------- | :------------- | :------ | :------- | :------ | :--------- | :--------- | :-------- | :-------- | :------------------- | :------ | :------------ | :------------ | :--------------- | :----------------------------------- | :-------------------- | :------------------------ |
| use           | use              | use               | not use | not use            | not use            | not use        | not use        | not use | not use  | not use | not use    | not use    | not use   | not use   | not use              | not use | not use       | not use       | not use          | use                                  | not use               | not use                   |
| 1722573396000 | pass_through_log | 1722573395900     | -       | -                  | -                  | -              | -              | -       | -        | -       | -          | -          | -         | -         | -                    | -       | -             | -             | -                | ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaaa | -                     | -                         |

The actual CSV text output will look like this.

```csv
timestampMs,logType,deviceTimestampMs,wovenId,normativeFirstName,normativeGivenName,latinFirstName,latinGivenName,email,deviceId,doorId,elevatorId,buildingId,floorName,direction,authenticationMethod,result,authErrorType,authErrorCode,authErrorMessage,requestId,antiPassbackSettingId,antiPassbackViolationType
1722573396000,pass_through_log,1722573395900,,,,,,,,,,,,,,,,,,ffffeeee-dddd-cccc-bbbb-aaaaaaaaaaaa,,
:
```

#### Output format of `anti_passback_log`

The fields used in the auth_log and examples are listed below

| timestampMs   | logType          | deviceTimestampMs | wovenId                              | normativeFirstName | normativeGivenName | latinFirstName | latinGivenName | email                 | deviceId   | doorId   | elevatorId | buildingId | floorName | direction | authenticationMethod | result  | authErrorType | authErrorCode | authErrorMessage | requestId | antiPassbackSettingId | antiPassbackViolationType                   |
| :------------ | :--------------- | :---------------- | :----------------------------------- | :----------------- | :----------------- | :------------- | :------------- | :-------------------- | :--------- | :------- | :--------- | :--------- | :-------- | :-------- | :------------------- | :------ | :------------ | :------------ | :--------------- | :-------- | :-------------------- | :------------------------------------------ |
| use           | use              | use               | use                                  | use                | use                | use            | use            | use                   | use        | use      | not use    | use        | use       | use       | not use              | not use | not use       | not use       | not use          | not use   | use                   | use                                         |
| 1722573396000 | pass_through_log | 1722573395900     | 01234567-0123-0123-0123-0123456789ab | 鈴木               | 太郎               | suzuki         | taro           | suzuki.taro@woven.com | deviceXXXX | doorXXXX | -          | buildingA  | 1F        | enter     | -                    | -       | -             | -             | -                | -         | antipassback-id       | re-entrance with valid entrance log existed |

The actual CSV text output will look like this.

```csv
timestampMs,logType,deviceTimestampMs,wovenId,normativeFirstName,normativeGivenName,latinFirstName,latinGivenName,email,deviceId,doorId,elevatorId,buildingId,floorName,direction,authenticationMethod,result,authErrorType,authErrorCode,authErrorMessage,requestId,antiPassbackSettingId,antiPassbackViolationType
1722573396000,anti_passback_log,1722573395900,01234567-0123-0123-0123-0123456789ab,鈴木 ,太郎 ,suzuki,taro,suzuki.taro@woven.com,deviceXXXX,doorXXXX,,buildingA,1F,enter,,,,,,,antipassback-id,re-entrance with valid entrance log existed
:
```

#### auth logs sample csv

[sample file](./sample/auth_log_sample.csv)

### Output format of `POST /resource-event/csv`

The output fields are as [this](#resource_event_logs)

The examples are listed below

| timestampMs   | type     | buildingId | floorName | deviceId   | doorId   | elevatorId   | eventCode | eventLevel | eventName               | eventNameJP         | eventSort | eventDetail        | eventIsPersistent |
| :------------ | :------- | :--------- | :-------- | :--------- | :------- | :----------- | :-------- | :--------- | :---------------------- | :------------------ | :-------- | :----------------- | :---------------- |
| 1234567890123 | door     | building1  | 1F        | deviceXXXX | doorXXXX | -            | E_0007    | error      | fireAlarm               | 火報信号 ON         | resolved  | -                  | true              |
| 1234567890123 | elevator | building1  | 1F        | deviceXXXX | -        | elevatorXXXX | E_0507    | warn       | faceAuthenticationError | 認証エラー / 顔認証 | detected  | detail information | false             |

The actual CSV text output will look like this.

```csv
timestampMs,type,buildingId,floorName,deviceId,doorId,elevatorId,eventCode,eventLevel,eventName,eventNameJP,eventSort,eventDetail,eventIsPersistent
1234567890123,door,building1,1F,deviceXXXX,doorXXXX,-,E_0007,error,fireAlarm,火報信号 ON,resolved,-,true
1234567890123,elevator,building1,1F,deviceXXXX,-,elevatorXXXX,E_0507,warn,faceAuthenticationError,認証エラー / 顔認証,detected,detail information,false
:
```

#### resource event sample csv

[sample file](./sample/resource_event_sample.csv)

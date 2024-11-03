# Car Gate Backend Sequences

- [Car Gate Backend Sequences](#car-gate-backend-sequences)
  - [Common sequences](#common-sequences)
    - [Alive monitoring of Amano Server](#alive-monitoring-of-amano-server)
    - [`GET` car gate registration information list](#get-car-gate-registration-information-list)
    - [`GET` car gate registration information](#get-car-gate-registration-information)
  - [Facility Booking integration sequences](#facility-booking-integration-sequences)
    - [`POST/PUT/DELETE` car gate information from Facility Booking](#postputdelete-car-gate-information-from-facility-booking)
    - [Notification from Amano server](#notification-from-amano-server)
  - [Security guard operation sequences](#security-guard-operation-sequences)
    - [Register (`POST`) residents'/workers' car gate registration information](#register-post-residentsworkers-car-gate-registration-information)
    - [Update (`PUT/DELETE`) residents'/workers' car gate registration information](#update-putdelete-residentsworkers-car-gate-registration-information)
    - [Get a list of user's registration information](#get-a-list-of-users-registration-information)
    - [`POST/PUT/DELETE` temporary company visitors' car gate registration information](#postputdelete-temporary-company-visitors-car-gate-registration-information)
    - [`POST/PUT/DELETE` e-palette car gate registration information](#postputdelete-e-palette-car-gate-registration-information)
    - [TBD: `GET` car gate log](#tbd-get-car-gate-log)
  - [Synchronize records with Amano Server](#synchronize-records-with-amano-server)

`Car Gate DB` may consist of a couple of different database systems.

## Common sequences

### Alive monitoring of Amano Server

Car Gate Backend monitors liveness of Amano Server.

```mermaid
sequenceDiagram
    participant be as Car Gate Backend
    participant amano as Amano Server

    loop Constant time interval
        critical
            be ->> amano: GET livez
            amano -->> be: Return OK
            be ->> be: alive = true
        option If Amano Server is not healthy
            be ->> be: alive = false
        end
    end
```

### `GET` car gate registration information list

Car Gate Backend returns registration information.
The frontend retrieves the necessary information (nfc idms, woven id etc.) by querying them in advance.
The backend also provides gateway endpoint to query external information like BURR.

WHY?:

- Though the UI requires additional information on the users and NFC cars, we don't want to duplicate control of such information.
- We've not decided how we integrate information from outside services into car gate registration information. As a temporary solution, we placed importance on consistent implementation.

```mermaid
sequenceDiagram
    participant client as Frontend
    box AC backend
        participant be as Car Gate Backend
        participant db as Car Gate DB
        participant ext as External Services<br/>(NFC BE, BURR etc.)
    end
    par for each query on external information
        client ->> be: Send query for an external information
        be ->> ext: Send query
        ext -->> be: Return a list of query keys (nfc idms, woven IDs, etc.)
        be -->> client: Return
    end
    client ->> be: GET registration information list with query keys
    be ->> db: Retrieve registration info by query
    db -->> be: 
    be -->> client: Return registration info

    par for each external information
        client ->> be: GET external information by ids<br/>(nfc idms, woven IDs, etc.)
        be ->> ext: GET external information
        ext -->> be: Return 
        be -->> client: Return
    end
```

### `GET` car gate registration information

Car Gate Backend returns registration information directly.

```mermaid
sequenceDiagram
    participant client as Client
    box AC backend
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    client ->> be: GET w/ request ID
    be ->> db: Retrieve registration info by request ID
    be -->> client: Return registration info
```

## Facility Booking integration sequences

### `POST/PUT/DELETE` car gate information from Facility Booking

Car Gate Backend handles the registration request with a single request ID.

```mermaid
sequenceDiagram
    participant fb as Facility Booking
    box AC backend
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    participant amano as Amano Server
    fb ->> be: POST/PUT/DELETE registration info<br>w/ woven ID, car info, available period
    opt If Amano Server is not healthy
        be -->> fb: Return 503
    end
    be ->> be: Create inquiry ID
    opt If the request is POST
        be ->> be: Create request ID
    end

    critical Update data
        be ->> db: Start transaction
        be ->> db: Save car info w/ request ID
        be ->> amano: Update car info w/ request ID
        Note over be, amano: POST/PUT: update API, DELETE: delete API
        be ->> db: Commit
    option Request for amano server failed
        be ->> db: Rollback
        be -->> fb: Return 500
    end
    alt If the request is POST
        be -->> fb: Return 201 with request ID
    else
        be -->> fb: Return 204
    end
```

### Notification from Amano server

Car Gate Backend records notifications and propagate them to other services if needed.
The backend returns DB error to Amano Server if it fails to record a log.
The backend doesn't return error if any propagation fails.

Notification target: e-palette, (Facility Booking)

TODO: Determine how the backend retry notification propagation.

```mermaid
sequenceDiagram
    participant svc as External Services
    box AC backend
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    participant amano as Amano Server
    amano ->> be: Notification of car passage
    critical
    be ->> db: Put a log
    be -->> amano: Return OK
    be ->> db: Retrieve request ID
    opt If notification propagation needed
        Note over svc, be: Detailed logic: TBD
        be -) svc: Send notification
    end
    option Log output failed
    be -->> amano: Return DB error
    amano ->> amano: Retry notification
    end
```

## Security guard operation sequences

### Register (`POST`) residents'/workers' car gate registration information

Considering the possibility of users registering multiple car numbers and NFC cards, the backend generates a request ID for each registration item.
The backend stores the information along with the users' woven ID.

```mermaid
sequenceDiagram
    box AC backend
    participant fe as Frontend App
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    participant amano as Amano Server

    fe ->> be: POST registration information<br>w/<br>woven ID<br>car numbers,<br>NFC IDMs
    opt If Amano Server is not healthy
        be ->> fe: Return 503
    end
    loop For each car number/NFC IDM
        par Register items separately
            be ->> be: Create request ID and inquiry ID
            critical Update data
                be ->> db: Start transaction
                be ->> db: Save car info w/ request ID
                be ->> amano: Update car info w/ request ID
                be ->> db: Commit
                be ->> be: Mark as success
            option Request for amano server failed
                be ->> db: Rollback
                be ->> be: Mark as failure
            end
        end
    end
    be ->> fe: Return:<br>request ID list<br>failed items
```

### Update (`PUT/DELETE`) residents'/workers' car gate registration information

Same as [the sequence for Facility Booking](#postputdelete-car-gate-information-from-facility-booking).

### Get a list of user's registration information

```mermaid
sequenceDiagram
    box AC backend
    participant fe as Frontend App
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    fe ->> be: GET a list of user's registration information
    be ->> db: Retrieve the user's registration items (request IDs)
    be -->> fe: Return request IDs
```

### `POST/PUT/DELETE` temporary company visitors' car gate registration information

Almost the same sequences as the ones for Facility Booking, but the APIs also accept an IDM for a shared card.

```mermaid
sequenceDiagram
    box AC backend
    participant fe as Frontend App
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    participant amano as Amano Server

    fe ->> be: POST registration information<br>w/<br>woven ID<br>NFC IDM (must)<br>car number (optional)<br>available period
    opt If Amano Server is not healthy
        be ->> fe: Return 503
    end
    be ->> be: Create inquiry ID
    opt If the request is POST
        be ->> be: Create request ID
    end
    critical Update data
        be ->> db: Start transaction
        be ->> db: Save car info w/ request ID
        be ->> amano: Update car info w/ request ID
        be ->> db: Commit
    option Request for amano server failed
        be ->> db: Rollback
        be ->> fe: Return 500
    end
    alt If the request is POST
        be -->> fe: Return 201 with request ID
    else
        be -->> fe: Return 204
    end
```

### `POST/PUT/DELETE` e-palette car gate registration information

Almost the same sequences as the ones for Facility Booking, but the APIs require e-palette ID and WCN instead of car numbers.

```mermaid
sequenceDiagram
    box AC backend
    participant fe as Frontend App
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    participant amano as Amano Server

    fe ->> be: POST registration information<br>w/<br>e-palette ID<br>WCN
    opt If Amano Server is not healthy
        be ->> fe: Return 503
    end
    be ->> be: Create inquiry ID
    opt If the request is POST
        be ->> be: Create request ID
    end
    critical Update data
        be ->> db: Start transaction
        be ->> db: Save car info w/ request ID
        be ->> amano: Update car info w/ request ID
        be ->> db: Commit
    option Request for amano server failed
        be ->> db: Rollback
        be ->> fe: Return 500
    end
    alt If the request is POST
        be -->> fe: Return 201 with request ID
    else
        be -->> fe: Return 204
    end
```

### TBD: `GET` car gate log

Car Gate Backend returns stored logs.

```mermaid
sequenceDiagram
    participant client as Client
    box AC backend
    participant be as Car Gate Backend
    participant db as Car Gate DB
    end
    client ->> be: GET w/ woven ID or e-palette ID
    be ->> db: Retrieve logs
    be -->> client: Return logs
```

## Synchronize records with Amano Server

The job is defined as [an Agora serverless function](https://developer.woven-city.toyota/docs/default/component/serverless-tutorial/).  
The job synchronizes registration information with Amano Server.  
The result of this operation is sent to Car Gate BE.  
The job is NOT used on a periodic basis.

```mermaid
sequenceDiagram
    actor op as Operator
    participant be as Car Gate BE
    participant job as Car Gate Job
    participant db as Car Gate DB
    participant amano as Amano Server
    op ->> +be: Run a job
    be -) +job: Run a job (via kafka)
    be -->> -op: Return
    job ->> job: Generate inquiryID
    job ->> db: Save a record with inquiryID<br>Status: NotStarted
    job ->> db: Get all registration information
    critical
        job ->> amano: Update all information at once
        activate amano
        amano -->> job: 
        job ->> db: Update a record<br>Status: Started
    option
        alt If result code is '102' AND<br>the number of request doesn't exceed a retry limit
            job ->> amano: Retry request
            amano -->> job: 
        else
            job ->> db: Update a record<br>Status: Failed
            job -) op: Notify job result
            Note over job, op: TBD: how?
        end
    end
    job ->> -job: Exit job
    amano -) amano: Update information
    amano ->> +be: Notify update result
    be ->> db: Update a record<br>Status: Succeeded OR Failed
    be -) op: Notify job result
    Note over be, op: TBD: how?
    be -->> -amano: 
    deactivate amano
```

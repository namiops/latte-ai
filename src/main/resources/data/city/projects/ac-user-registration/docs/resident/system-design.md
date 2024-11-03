# Resident Registration System Design

- [Resident Registration System Design](#resident-registration-system-design)
  - [Overview](#overview)
    - [Prerequisites](#prerequisites)
    - [Overview process](#overview-process)
  - [Sequence Diagrams](#sequence-diagrams)
    - [Sequence - Create a new account](#sequence---create-a-new-account)
    - [Sequence - Update other information on a user](#sequence---update-other-information-on-a-user)
    - [Sequence - Register new account and associate with a parent user](#sequence---register-new-account-and-associate-with-a-parent-user)
    - [Sequence - Retrieve users](#sequence---retrieve-users)
    - [Sequence - Set up a household and its members](#sequence---set-up-a-household-and-its-members)
    - [Sequence - Set up additional users as household members](#sequence---set-up-additional-users-as-household-members)

## Overview

### Prerequisites

The application will be used by administrative users.

### Overview process

The entier process is as listed.

- Register a user
  - Create a new account
  - Update other information on a user
- Register a child
  - Create a new account and associate with a parent user
- Set up a household
  - Retrieve users
  - Set up a household and its members
  - Set up additional users as household members

## Sequence Diagrams

TBD: Retry policies of sequential HTTP requests in a request.

### Sequence - Create a new account

~~*Discussion*: Is the `username` given or should the backend generate it?~~
=> The backend generates it to prevent users from conflicting usernames.

```mermaid
sequenceDiagram
  autonumber
  actor admin as Admin
  participant fe as FE
  participant be as BE
  participant burr as BURR
  participant kc as Keycloak

  admin ->> fe: Input user information
  Note over admin,fe: name(alphabetical/latin)<br/>name(kanji,kana/normative)<br/>email<br/>date of birth<br/>phone number
  fe ->> be: [POST] create a user
  be ->> be: Generate a username
  be ->> kc: [POST] create a user
  kc -->> be: 
  be ->> kc: [GET] retrieve a user from username
  kc -->> be: Return woven ID
  be ->> burr: [POST] /persons
  burr -->> be: 
  be -->> fe: Return woven ID
  fe -->> admin: Finish
```

### Sequence - Update other information on a user

This is an abstract sequence of how the application updates information other than the basic one.

~~**Discussion**: How the application (FE&BE) handle the situation when some update requests partially fail?~~=>Simply handle it by admins' retrial.

```mermaid
sequenceDiagram
  autonumber
  actor admin as Admin
  participant fe as FE
  participant be as BE
  participant burr as BURR
  participant ext as External Services

  admin ->> fe: Input user information
  Note over admin,fe: Any information other than the basic one
  fe ->> be: [POST] update information
  Note over fe,be: The API endpoint will be multiple<br/>according to the type of information.
  be ->> burr: [PUT/POST] update information
  burr -->> be: 
  opt if needed
    be ->> ext: [PUT/POST] update information
    ext -->> be: 
  end
  be -->> fe: 
  fe -->> admin: Finish
```

### Sequence - Register new account and associate with a parent user

```mermaid
sequenceDiagram
  autonumber
  actor admin as Admin
  participant fe as FE
  participant be as BE
  participant burr as BURR
  participant kc as Keycloak

  admin ->> fe: Input user information
  Note over admin,fe: parent's woven ID<br/>name(alphabetical/latin)<br/>name(kanji,kana/normative)<br/>email<br/>date of birth<br/>phone number
  fe ->> be: [POST] create a user
  be ->> burr: [GET] /persons/:wovenId/registrationSummary<br/> with the parent's woven ID
  burr -->> be: 
  opt if the parent account is `ward`
    be -->> fe: Return 400
  end
  be ->> be: Generate a username
  be ->> kc: [POST] create a user
  kc -->> be: 
  be ->> kc: [GET] retrieve a user from username
  kc -->> be: Return woven ID
  be ->> burr: [POST] /persons
  burr -->> be: 
  be ->> burr: [POST] /persons/:wovenId/guardianship<br/> with the child's woven ID
  burr -->> be: 
  be -->> fe: Return the child's woven ID
  fe -->> admin: Finish
```

### Sequence - Retrieve users

```mermaid
sequenceDiagram
  autonumber
  actor admin as Admin
  participant fe as FE
  participant be as BE
  participant burr as BURR

  Note over admin,burr: Retrieve wards (children)
  fe ->> be: [GET] retrieve wards of a parent user
  be ->> burr: [GET] /persons/:wovenId/wards
  burr -->> be: Return wards
  be -->> fe: Return wards
  fe -->> admin: Show wards
  Note over admin,burr: Retrieve other users
  admin ->> fe: Input user information
  Note over admin,fe: available queries:<br/>emailAddress
  fe ->> be: [GET] retrieve users
  be ->> burr: [GET] /persons/search
  burr -->> be: Return user list
  be -->> fe: Return user list
  fe -->> admin: Finish
```

### Sequence - Set up a household and its members

```mermaid
sequenceDiagram
  autonumber
  actor admin as Admin
  participant fe as FE
  participant be as BE
  participant burr as BURR

  Note over admin,burr: Retrieve city address ID
  admin ->> fe: Input address
  admin ->> fe: Retrieve city address ID
  fe ->> be: [GET] Retrieve city address ID
  be ->> burr: [GET] /cityAddresses/search
  burr -->> be: Return a list of the matched addresses
  be -->> fe: Return the list
  fe -->> admin: Show the list
  admin ->> fe: Select an address
  Note over admin,burr: Register a new household
  admin ->> fe: Retrieve household members (details are omitted)
  Note over admin,fe: See "Sequence - Retrieve users"<br/>for details.
  admin ->> fe: Input household information
  Note over admin,fe: representativeId<br/>memberIds<br/>cityAddressId<br/>effectiveDate<br/>terminationDate
  admin ->> fe: Register a household
  fe ->> be: [POST] register a household
  be -->> burr: [POST] /cityHouseholds
  burr -->> be: Return cityHouseholdId
  be -->> fe: Return cityHouseholdId
  fe -->> admin: Finish
```

### Sequence - Set up additional users as household members

```mermaid
sequenceDiagram
  autonumber
  actor admin as Admin
  participant fe as FE
  participant be as BE
  participant burr as BURR

  Note over admin,burr: Retrieve a household
  admin ->> fe: Access to a representative user page
  fe ->> be: [GET] retrieve city household info by woven ID
  be ->> burr: [GET] /persons/cityHousehold/cityAddress
  burr -->> be: Return city address info
  Note over be,burr: city address info includes<br/>user's basic info<br/>city address info<br/> only city address info will be returned to the frontend.
  be -->> fe: Return city address info
  fe -->> admin: Show the address
  fe ->> be: [GET] retrieve city household info by city address ID
  be ->> burr: [GET] /cityHouseholds
  burr -->> be: Return city household info
  Note over be,burr: city household info includes<br/>id<br/>representativeId<br/>memberIds<br/>cityAddressId<br/>effectiveDate<br/>terminationDate
  be -->> fe: Return city household info
  fe -->> admin: Show the household info
  admin ->> fe: Retrieve household members (details are omitted)
  Note over admin,fe: Retrieve users: See "Sequence - Retrieve users" for details
  Note over admin,burr: Set up users as household members
  admin ->> fe: Select a member
  fe ->> be: [PUT] update members
  be ->> burr: [PUT] /cityHouseholds/:cityHouseholdId/members
  burr -->> be: 
  be -->> fe: 
  fe -->> admin: Finish
```

# Visitor Registration System Design

- [Visitor Registration System Design](#visitor-registration-system-design)
  - [Overview](#overview)
  - [Data Flow Diagram](#data-flow-diagram)
    - [DFD - Create an account to Woven ID](#dfd---create-an-account-to-woven-id)
    - [DFD - Register user information (pre-registration method)](#dfd---register-user-information-pre-registration-method)
      - [Before eKYC](#before-ekyc)
      - [After eKYC](#after-ekyc)
    - [DFD - Identity verification (pre-registration method)](#dfd---identity-verification-pre-registration-method)
    - [DFD - Register user information (children)](#dfd---register-user-information-children)
    - [DFD - Registration of traffic course completion](#dfd---registration-of-traffic-course-completion)
  - [State Transition Diagram](#state-transition-diagram)
    - [State - Register user information and identity verification (pre-registration method)](#state---register-user-information-and-identity-verification-pre-registration-method)
    - [State - Create a child account](#state---create-a-child-account)
  - [Sequence Diagram](#sequence-diagram)
    - [Sequence - Register user information (pre-registration method)](#sequence---register-user-information-pre-registration-method)
    - [Sequence - Identity verification (pre-registration method)](#sequence---identity-verification-pre-registration-method)
      - [Access to the application page - complete application (not yet verified)](#access-to-the-application-page---complete-application-not-yet-verified)
      - [Get verification result - Update status](#get-verification-result---update-status)
    - [Sequence - Register additional personal information and face image](#sequence---register-additional-personal-information-and-face-image)
    - [Sequence - Register children](#sequence---register-children)
      - [Sequence - Create a child account](#sequence---create-a-child-account)
    - [Sequence - Registration of traffic course completion](#sequence---registration-of-traffic-course-completion)

## Overview

The entire process is as listed.

- Create a new woven ID
  - see the corresponding sequence in [this confluence page](https://confluence.tri-ad.tech/display/CISAM/Visitor+Registration+Sequence+for+adult+normal+case#VisitorRegistrationSequenceforadultnormalcase-LoginorcreateanaccounttoWovenID)
- Register user information (pre-registration)
- Identity verification (pre-registration)
- Register additional personal information and face image
- Register children
- Registration of traffic course completion

## Data Flow Diagram

### DFD - Create an account to Woven ID

- TBD: Other information to be stored in BURR
- TBD: KeyCloak may create a new record to BURR

```mermaid
flowchart LR
    subgraph FSS
        fe("Applicant (FSS FE)")
        be(FSS BE)
    end
    subgraph Agora Services
        burr(BURR)
        burrdb[(Woven ID)]
        kc(KeyCloak)
        kcdb[(first name<br/>last name<br/>email<br/>password<br/>woven ID)]
    end


    fe -- first name<br/>last name<br/>email<br/>password<br/>woven ID --> kc --> kcdb
    kc -- access token --> fe
    fe -- redirect with access token --> be
    be -- Woven ID --> burr --> burrdb
```

### DFD - Register user information (pre-registration method)

#### Before eKYC

```mermaid
flowchart LR
    subgraph FSS
        fe("Applicant (FSS FE)")
        be(FSS BE)
    end
    subgraph Agora Services
        burr(BURR)
        burrdb[(first name<br/>last name<br/>birthday)]
    end

    fe -- first name<br/>last name<br/>birthday --> be
    be -- first name<br/>last name<br/>birthday --> burr --> burrdb
```

#### After eKYC

The backend will register a front face image taken during eKYC to Face Identifier.
In this phase, the backend requests another face image if the registration above fails.

```mermaid
flowchart LR
    subgraph FSS
        fe("Applicant (FSS FE)")
        be(FSS BE)
    end
    subgraph Agora Services
        burr(BURR)
        face(Face Identifier)
        burrdb[(Other personal information)]
        facedb[(Vector representation of faces)]
    end

    fe -- "other personal information<br/>(op) face image" --> be
    be -- other personal information --> burr --> burrdb
    be -- "(op) face image" --> face --> facedb
```

### DFD - Identity verification (pre-registration method)

```mermaid
flowchart LR
    subgraph FSS
        fe("Applicant (FSS FE)")
        be(FSS BE)
        bedb[(association between woven ID and association ID<br/>application status)]
        face(Face Identifier)
        facedb[(Vector representation of faces)]
    end
    subgraph External Services
        ekyc(eKYC)
    end
    subgraph Agora Services
        burr(BURR)
        burrdb(woven ID<br/>verification status)
    end

    be -- association ID --> ekyc
    ekyc -- registration link --> be -- registration link --> fe
    fe -- verification document<br/>verification face image --> ekyc
    ekyc -- verification result --> be
    be -- verification result --> burr --> burrdb
    be --> bedb
    ekyc -- face image --> be -- face image --> face --> facedb
```

### DFD - Register user information (children)

- TBD: KeyCloak may create a new record to BURR

```mermaid
flowchart LR
    subgraph FSS
        fe("Applicant(FSS FE)")
        be(FSS BE)
    end
    subgraph Agora Services
        kc(KeyCloak)
        kcdb[("first name<br/>last name<br/>(op)password<br/>(op)email")]
        burr(BURR)
        burrdb[(children's information<br/>household information)]
        face(Face Identifier)
        facedb[(Vector representation of faces)]
    end

    fe -- children's information<br/>face images --> be
    be -- "first name<br/>last name<br/>(op) email" --> kc --> kcdb
    kc -- woven ID --> be
    be -- children's woven IDs<br/>children's information<br/>household information --> burr --> burrdb
    be -- face images --> face --> facedb
```

### DFD - Registration of traffic course completion

```mermaid
flowchart LR
    subgraph FSS
        fe("Person In Charge (FSS FE)")
        be(FSS BE)
        fb(FB BE)
    end
    subgraph Agora Services
        burr(BURR)
        burrdb[(lecture completion status)]
    end

    fb -- booking information --> be
    burr -- current lecture completion status --> be
    be -- current lecture completion status & booking information --> fe
    fe -- lecture completion status --> be -- lecture completion status --> burr <--> burrdb
```

## State Transition Diagram

### State - Register user information and identity verification (pre-registration method)

Each state is associated with an applicant's woven ID.

```mermaid
flowchart TB
    s((start))
    noRecord("No record<br/>(association ID has not been generated)")
    submitting(record: submitting)
    submitted(record: submitted)
    succeeded?{"identity verification (eKYC) succeeded?"}
    failed(record: failed or urlExpired)
    finished(record: finished<br/>BURR: verified = true)

    s -- Input first name, last name, birthday --> noRecord
    noRecord -- "Obtain eKYC application URL" --> submitting
    submitting -- "eKYC application finished<br/>(notified from FE)" --> submitted
    submitting -- Apply again --> submitting
    submitting -- The result arrived from eKYC<br/>without notification from FE --> succeeded?
    submitted -- The result arrived from eKYC --> succeeded?
    succeeded? -- Yes --> finished
    succeeded? -- No or URL expired --> failed
    failed -- Apply again --> submitting
```

### State - Create a child account

This state transition diagram shows the backend one during child account creation process.  
Each state is associated with an applicant's woven ID and a registration ID generated on the process.

```mermaid
flowchart TB
    s((Start))
    subgraph Normal states
        
        received(phase: requestReceived<br/>status: processing<br/>woven ID: null)
        suc1{Success?}

        created("phase: accountCreated<br/>status: processing<br/>woven ID: {ID}")
        suc2{Success?}

        burrsuccess("phase: householdUpdated<br/>status: completed<br/>woven ID: {ID}<br/>Temporal tokens will also be created in this phase.")
    end

    subgraph Sub-normal states
        failed("phase: accountCreationFailed<br/>status: failed<br/>reason: {reason}")
        burrfailed("phase: deletingFailedAccount<br/>status: failed<br/>woven ID: {ID}")
        suc3{Success?}
    end

    e((End))

    s -- request --> received
    received -- Create a new account --> suc1
    suc1 -- no --> failed
    suc1 -- yes --> created
    created -- Update BURR --> suc2

    suc2 -- yes --> burrsuccess
    suc2 -- no --> burrfailed

    burrfailed -- delete account --> suc3
    suc3 -- yes --> failed
    suc3 -- no --> burrfailed
    
    failed --> e
    burrsuccess --> e
```

## Sequence Diagram

The following sequences assumes that databases will never be disconnected during request processing.

### Sequence - Register user information (pre-registration method)

```mermaid
sequenceDiagram
    autonumber
    actor u as User

    box FSS
    participant fe as FE
    participant be as BE
    end

    box Agora Services
    participant burr as BURR
    end

    u ->>  fe: Access
    fe ->> be: [GET] user info
    be ->> burr: Get user status
    burr -->> be: 
    be -->> fe: Return user info
    Note over fe: This sequence assumes that the user information<br/>has not been registered in this phase.

    fe ->> u: Move to user information registration page
    u ->> fe: Input first name, last name, birthday
    fe ->> be: [POST] user info
    be ->> burr: Store user info
    burr -->> be: 
    be -->> fe: Return OK
    fe -->> u: Show process completion page
    opt If registration failed
        be -->> fe: Return 4XX, 5XX
        fe ->> u: Show process failed page
    end
```

### Sequence - Identity verification (pre-registration method)

#### Access to the application page - complete application (not yet verified)

```mermaid
sequenceDiagram
    autonumber
    actor u as User

    box FSS
    participant fe as FE
    participant be as BE
    participant db as DB
    end

    box Agora Services
    participant burr as BURR
    end 

    box External Services
    participant ekyc as eKYC
    end

    u ->> fe: Access
    fe ->> be: GET user info
    be ->> burr: Get user info
    burr -->> be: 
    be -->> fe: 
    Note over fe: This sequence assumes that required user information has been registered,<br/>but identity verification hasn't.
    fe ->> be: [GET] Retrieve identity verification status
    be ->> db: Get registration status
    db -->> be: Return null
    be -->> fe: Return "Not Applied"
    fe -->> u: Show submission page
    u ->> fe: Apply
    fe ->> be: [POST] Submit application
    be ->> be: Generate association ID<br/>callback token
    be ->> ekyc: [POST] Submit application
    note over be, ekyc: `redirect_url` in the application request contains as query parameter the callback token
    ekyc -->> be: Return application link
    be ->> db: Save association ID and woven ID<br/>status: submitting<br/>callback token: {token}
    db -->> be: 
    be -->> fe: Return application link
    fe -->> u: Show application link
    Note over u: Process registration

    u ->> ekyc: Submit identity verification documents
    ekyc -->> u: Redirect to FE
    note over ekyc, u: The redirect url contains callback token as query parameter
    u ->> fe: Access to "application completed" page
    fe ->> be: POST Notify application completion w/ callback token
    be ->> db: Update status to submitted
    db -->> be: 
    be -->> fe: Return
    Note over ekyc: Process verification
```

#### Get verification result - Update status

BE periodically calls "GET verification results" API and process results.

```mermaid
sequenceDiagram
    autonumber

    box FSS
    participant be as BE
    participant db as DB
    end

    box Agora Services
    participant burr as BURR
    participant notify as Notification
    participant face as Face Identifier
    end 

    box External Services
    participant ekyc as eKYC
    end

    be ->> ekyc: [GET] verification results
    ekyc -->> be: Return results
    loop For each result
        alt If the verification succeeded
            be ->> db: status: completed
            db -->> be: 
            be ->> burr: verification status: completed
            burr -->> be: 
            opt If BURR update failed
                be ->> be: Retry BURR update
            end
            be ->> notify: Send mail
            notify -->> be: 
            Note over be: Ignore error
            be ->> ekyc: [GET] photo images
            ekyc -->> be: Return images
            Note over be: Ignore error
            be ->> face: [POST] face image
            face -->> be: 
            opt If face registration succeeded
                be ->> burr: [POST] face registered=true
                burr -->> be: 
                opt IF BURR update failed
                    be ->> be: Retry BURR update
                end
            end
        else If verification failed
            be ->> db: status: failed
            db -->> be: 
            be ->> notify: Send mail
        end
    end

```

### Sequence - Register additional personal information and face image

This sequence doesn't assume any registration state.
Users can register additional information at any time.

```mermaid
sequenceDiagram
    autonumber
    actor u as User

    box FSS
        participant fe as FE
        participant be as BE
    end

    box Agora Services
        participant burr as BURR
    end

    u ->> fe: Access
    fe ->> be: [GET] personal information
    be ->> burr: [GET] personal information
    burr ->> be: 
    be ->> fe: 

    u ->> fe: Input personal information
    fe ->> be: [POST] personal information
    be ->> burr: [POST] personal information
    burr -->> be: 
    be -->> fe: 
    opt If face registered=false
        fe -->> u: Show face registration page
        u ->> fe: Input face image & submit
        fe ->> be: [POST] face image
        be ->> face: [POST] face image
        face -->> be: 
        be -->> fe: 
        fe -->> u: Show completion page
    end
```

### Sequence - Register children

TODO: follow the current implementation

```mermaid
sequenceDiagram
    autonumber
    actor u as User

    box FSS
    participant fe as FE
    participant be as BE
    participant db as DB
    end

    box Agora Services
    participant face as Face ID
    end

    loop For each child
        u ->> fe: Click "add a child"
        fe -->> u: Show registration form
        u ->> fe: Fulfill form, take a face image
    end

    loop For each child

    fe ->> be: [POST] Register child information
        be ->> be: Generate a registration ID
        be ->> db: Create a new registration record
        Note over db: phase: requestReceived<br/>status: processing<br/>wovenID: null
        db -->> be: 
        be ->> be: enqueue "Create a new account"
        be -->> fe: Return 202 + registration ID
        Note over be: See "Create a child account" for the details of processing.

        loop While the request is processing
            fe ->> be: [GET] registration status
            be ->> db: Retrieve registration status
            db -->> be: 
            alt If the token is not found
                be -->> fe: Return 404
            end
            opt If the status is "completed"
                be ->> db: Get temporal token
                db -->> be: 
            end
            be -->> fe: Return registration status + temporal token
            opt If the status is "failed"
                fe ->> u: Show error (account creation failed)
            end
        end

        fe ->> be: [POST] Register the child's face image<br/>the child's temporal token + face image
        be ->> db: Get woven ID from the temporal token
        db -->> be: 
        opt If the token is invalid
            be -->> fe: Return error
            fe -->> u: Show error (face registration failed)
        end
        be ->> face: [POST] Register face image
        opt If the registration failed
            face -->> be: Return error
            be -->> fe: Return error (face registration failed)
            Note over fe: Go to face registration step<br/>for the child.
        end
        face -->> be: Return OK
        be -->> fe: Return OK
    end
    
    opt If registration of some children failed
        fe -->> u: Show "error" status with their details
    end
    fe -->> u: Show "completed" status
```

#### Sequence - Create a child account

This is a sub sequence of children registration.

```mermaid
sequenceDiagram
    autonumber

    box FSS
    participant be as BE
    participant db as DB
    end

    box Agora Services
    participant kc as Keycloak
    participant burr as BURR
    end

    be ->> be: dequeue
    be ->> kc: [POST] Create a new account
    Note over be: BE will not request email verification on creation.
    kc ->> kc: Create
    opt if the email address is already registered
        kc -->> be: Return error
        be ->> db: Update registration record
        Note over db: phase: accountCreationFailed<br/>status: failed<br/>reason: the specified email is already used
        db -->> be: 
    end
    kc ->> burr: [POST] Create a record
    burr -->> kc: Return woven ID
    kc -->> be: Return woven ID
    be ->> db: Update registration record
        Note over db: phase: accountCreated<br/> status: processing<br/>wovenID: ID
    db -->> be: 

    be ->> burr: [POST] Register the child account as a household member
    burr -->> be: 
    alt If household registration succeeded
        be ->> db: start transaction
        db -->> be: 
        be ->> be: Generate a temporal token for the child
        be ->> db: Create association record
        Note over db: applicant's woven ID<br/>+ child's woven ID<br/>+ temporal token
        be ->> db: Update registration record
        Note over db: phase: householdUpdated<br/> status: completed<br/>account: ID
        db -->> be: 
        opt If the child has an email
            be ->> kc: [POST] send email
            kc -->> be: 
            Note over be: If email transmission failed, BE will ignore it.
        end
    else
        be ->> db: Update registration record
        Note over db: phase: deletingFailedAccount<br/> status: processing<br/>account: ID
        db -->> be: 
        be ->> kc: [DELETE] child account
        kc ->> burr: [DELETE]
        burr -->> kc: Return
        kc -->> be: Return
        opt If account deletion failed
            Note over be: Retry account deletion
        end
        be ->> db: Update registration record
        Note over db: phase: accountCreationFailed<br/> status: failed<br/>account: null<br/>reason: unexpected error
        db -->> be: 
    end
```

### Sequence - Registration of traffic course completion

```mermaid
sequenceDiagram
    participant u as Person In Charge
    participant fe as FE
    participant be as BE
    participant fb as FB BE
    participant burr as BURR

    u ->> fe: Access
    fe ->> be: [GET] user list
    be ->> fb: [GET] today's bookings
    fb -->> be: 
    be ->> burr: [GET] visitors' information
    burr -->> be: 
    be ->> be: Merge booking information and personal information
    be -->> fe: Return visitor list
    fe -->> u: Show user list
    u ->> fe: Mark visitors as course completed
    fe ->> be: [PUT] update visitor status
    loop For each visitor
        be ->> burr: [POST or PUT] update visitor status
        burr -->> be: 
        opt If update operation failed
            be ->> be: Add the visitor's woven ID to failed list
        end
    end
    be -->> fe: Return result
    Note over be: The result contains failed woven ID list.
```

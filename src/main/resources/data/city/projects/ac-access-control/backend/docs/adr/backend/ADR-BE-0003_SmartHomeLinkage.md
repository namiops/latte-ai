# ADR-BE-0003 Smart Home Linkage

| Status  | Last Updated |
| ------- | ------------ |
| Drafted | 2024-01-26   |

## Context and Problem Statement

At a meeting with the Smart Home team held in Q3 2023, It was decided to implement Poc phase 1 of Smart Home linked door control in mid-February 2024.
In line with this, it is necessary to design a communication sequence that takes into account the specifications of the Smart Home side.

This ADR describes the sequence between the Authenticator and the Access Control Backend, which reflects the content of previous meetings.

### Assumptions

- [System configuration diagram](https://www.figma.com/file/T0H41djRWelPhaF6zyHTSD/Access-Control---Smart-Home-door-lock-integration?type=whiteboard&node-id=1-546&t=WOGkFKLerIgz37ID-0)
- Door opening/locking is performed by touching the control buttons displayed on the Authenticator's UI.
  
## Considered Options

### Sequence 1

- Authentication, authorization, and door control with a single API
- The API response takes until the door control is completed or the SH side times out (about 30 seconds?)
- Operation button touch is the trigger, so the operation button must always be displayed on the UI.

```mermaid
sequenceDiagram
    actor User
    participant au as Authenticator
    participant acbe as ACBE Auth api server
    participant a3 as A3
    participant sh as SH Backend
    participant door as Electronic lock with Device Hub Controller

    User -->> au: Touch the operation buttons on the UI
        au ->> acbe: Request control of the door<br>New API<br>authentication data, Device ID, operation(open/close)
            activate acbe
            acbe ->> a3: authN/authZ query<br>POST /authn/permission
            activate a3
            a3 ->> acbe: Return of authN/authZ results
            deactivate a3
            opt authN/authZ error
                acbe -->> au: Return authN/authZ error
            end
            acbe ->> sh: Request door control<br>POST /device/operate
            activate sh
            sh ->> acbe: Return 202 accepted
            deactivate sh
                sh ->> door: lock or unlock
            loop Polling at 1 second intervals until processing is complete
                acbe ->> sh: Get Status<br>GET /asyncReq/{asyncReqId}
                activate sh
                sh ->> acbe: Returns the current status of received requests
                deactivate sh
            end
        acbe ->> au: Return of door control results<br>success/failure/timeout
        deactivate acbe
    au -->> User: Show door open/close
```

### Sequence 2

- Split the request of Authenticator in Sequence 1 into authentication authorization and door control.
- When the authentication authorization request is successful, ACBE issues and returns a request ID, and the door control API identifies the device ID based on that ID.
- Authenticator can only display operation buttons to those who have passed authentication authorization.

※The yellow background is the difference from sequence1

```mermaid
sequenceDiagram
    actor User
    participant au as Authenticator
    box Access control backend
    participant acbe as ACBE Auth api server
    participant db as ACBE DB
    end
    participant a3 as A3
    participant sh as SH Backend
    participant door as Electronic lock with Device Hub Controller

    rect rgb(255,255,224)
    User -->> au: put a face to a camera or use NFC card
        au ->> acbe: Authentication Request<br>New API<br>Device ID, Authentication data
        activate acbe
            acbe ->> a3: AuthN/AuthZ query<br> POST /authn/permission
            activate a3
            a3 ->> acbe: Return of authN/authZ results
            deactivate a3      
            acbe ->> db: Generate and save Request ID, user token, Door ID.
        acbe ->> au: Return the authentication result and Request ID
        deactivate acbe
    au -->> User: Operation button display
    User -->> au: Touch the operation buttons on the UI
        au ->> acbe: Request control of the door<br>New API<br>Request ID, operation(open/close)
        activate acbe
        acbe ->> db: Obtain user token and Device ID from Request ID
    end
            acbe ->> sh: Request door control<br>POST /device/operate
            activate sh
            sh ->> acbe: Return 202 accepted
            deactivate sh
                sh ->> door: lock or unlock
            loop Polling at 1 second intervals until processing is complete
                acbe ->> sh: Get Status<br>GET /asyncReq/{asyncReqId}
                activate sh
                sh ->> acbe: Returns the current status of received requests
                deactivate sh
            end
        acbe ->> au: Return of door control results<br>success/failure/timeout
        deactivate acbe
    au -->> User: Show door open/close
```

### Sequence 3

- Asynchronize Sequence 1 API
- Unnecessary if blocking until timeout on control failure in sequence 1 is not a problem.

```mermaid
sequenceDiagram
    actor User
    participant au as Authenticator
    participant acbe as ACBE Auth api server
    participant a3 as A3
    participant sh as SH Backend
    participant door as Electronic lock with Device Hub Controller

    User -->> au: Touch the operation buttons on the UI
        au ->> acbe: Request control of the door<br>New API<br>authentication data, Device ID, operation(open/close)
        activate acbe
            acbe ->> a3: authN/authZ query<br>POST /authn/permission
            activate a3
            a3 ->> acbe: Return of authN/authZ results
            deactivate a3
            opt authN/authZ error
                acbe -->> au: Return authN/authZ error
            end
            acbe ->> sh: Request door control<br>POST /device/operate
            activate sh
            sh ->> acbe: Return 202 accepted
            deactivate sh
        rect rgb(255,255,224)
        acbe ->> au: Return 200<br>request ID
        deactivate acbe
        loop Polling at 1 second intervals until processing is complete
        au ->> acbe: Get request status<br>New API<br>request ID
        activate acbe
            acbe ->> sh: Get Status<br>GET /asyncReq/{asyncReqId}
            activate sh
            sh ->> acbe: Returns the current status of received requests
            deactivate sh
        acbe ->> au: Return of door control results<br>success/failure/timeout/waiting
        deactivate acbe
        end
        end
    au -->> User: Show status of requests
```

---

## Decision Outcome

We chose sequence 2.
This is because it is possible to respond to any undecided specifications, such as whether or not authentication authorization is required when locking the door, and the display of the control buttons.

---

## Reference

- [Review minutes](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=740268220)

---

## Note

- 2024-01-26 : Drafted, Originator: Kenji Motoki

# Smart Home Linkage Sequence

## Table of Contents <!-- omit in toc -->

- [Smart Home Linkage Sequence](#smart-home-linkage-sequence)
  - [Overview Architecture](#overview-architecture)
  - [Glossary](#glossary)
  - [Scenarios](#scenarios)
    - [Smart Home Linked Door Control Sequence](#smart-home-linked-door-control-sequence)

This document describes the main use case scenarios of AC Management Service with their sequences.

## Overview Architecture

See this [figma](https://www.figma.com/file/T0H41djRWelPhaF6zyHTSD/Access-Control---Smart-Home-door-lock-integration?type=whiteboard&node-id=1-546&t=6ISPB73njOOg2Hfp-0).

## Glossary

| Word                  | Description |
| --------------------- | ----------- |
| Authenticator         | Authentication machine to be installed near the door. |
| ACBE Auth api server  | An API server in Access Control Backend. |
| A3                    | Services that provide authentication and authorization capabilities. |
| Device Hub Controller | Device control equipment controlled by Smart Home service. |

## Scenarios

This section describes the main use cases with their sequences.

### Smart Home Linked Door Control Sequence

Authentication authorization and door control are performed by separate APIs
When the authentication authorization request is successful, ACBE issues and returns a request ID, and the door control API identifies the device ID based on that ID.

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

# Re-identification sequence

What is re-identification?
=> see [`Note` section of this ticket](https://jira.tri-ad.tech/browse/CISAM-4159).

## Assumptions

- The authentication process follows the general door authentication process.
- This process is performed only when the authentication method is face authentication.
- The target facility is `Site Gate`.

## Sequence

```mermaid
sequenceDiagram
    autonumber
    participant auth as Authenticator
    box ac-access-control
    participant be as AC Auth API server
    participant sub as (sub process)
    end
    box alt-authn-authz
    participant a3 as A3
    end
    box In-house AWS
    participant faceid as Face Identification
    end

    auth ->> +be: put full body image and bounding box as well as auth info
    note over auth,be: Auth info includes:<br>deviceId<br>method(=`face`)<br>face image<br>timeStamp(millisecond)
    be ->> be: Retrieve the corresponding Site Gate
    be ->> +a3: Authenticate the user (omit the details)
    a3 -->> -be: Return a result
    opt if the user is not found
     be -->> auth: Return authentication failed
     note over be,auth: The same process as the general authentication process
    end
    be -) +sub: Create a sub process
    note over be,sub: Information to be handed over:<br/>full body image<br/>bounding box<br/>woven ID<br>Trace context is also inherited.
    be -->> -auth: Return the result
    note over auth,be: The result doesn't matter.
    sub ->> +faceid: Send re-identification information
    faceid -->> -sub: Return
    opt if faceid returned error
        sub ->> sub: Put error log
    end
    deactivate sub
```

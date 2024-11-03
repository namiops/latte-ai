# ADR-SEC-0002 Verify Request's Origin

| Status | Last Updated |
|---|---|
|Approved| 2023-07-19 |

## Context and Problem Statement

- How to satisfy the security requirement : `Requests to the APIs should verify the origin of the original request, for example by the cameras digitally signing the image and the camera ID. Then each step in the process (Web API, Authn/Authz Request, Auth/Authz,True Face and back to Door Control) could verify that the image came from an actual camera.` originally described in [Site Gate - Security Requirements Document](https://docs.google.com/document/d/1Q8_0Yt_KlxwBEw0SPycxnQaGjcbt51Xe0U6K9YoZaBM/edit) ID 6.
- The related JIRA ticket is [this](https://jira.tri-ad.tech/browse/FSSSECACT-66) on FSS Security Activity Board.

### Given Conditions

- mTLS will be used in all communication paths.
- A camera for authentication is under control of an Authenticator(UBio). Therefore subjects to be verified its origin are Authenticator devices.
- Same as for NFC card reader. Subjects to be verified are NFC controller edge PCs.
- User IoTA to provision and communicate with devices.
- The Access Control backend only knows which devices are valid. The connection from The backend to A3 is authorized by service authentication.

### Scope

- HTTPS API requests from devices to the backend API server.
- MQTT pub/sub communication between devices and the backend

---

## Decision Outcome

Use `Use IoTA mechanism`.  

### HTTPS API Requests

- Use [XFCC(x-forwarded-client-cert) header](https://www.envoyproxy.io/docs/envoy/latest/configuration/http/http_conn_man/headers#x-forwarded-client-cert) to vefity device ID(device name) of request's origin devices.
  
#### Sample of Header of Requests from Devices (May Change on Next Gen Cluster)

```json
{
  "headers": {
    "Accept": "*/*", 
    "Host": "httpbin-cvm.agora-lab.woven-planet.tech:6643", 
    "User-Agent": "curl/7.79.1", 
    "X-B3-Parentspanid": "1f89659449b9ed25", 
    "X-B3-Sampled": "1", 
    "X-B3-Spanid": "ec685c3bf8b70cc1", 
    "X-B3-Traceid": "8df7245d765b03071f89659449b9ed25", 
    "X-Envoy-Attempt-Count": "1", 
    "X-Envoy-Internal": "true", 
    "X-Forwarded-Client-Cert": "Hash=40654f107109896e03954fdf5261279e404708ce5b4c5e430781adf777a73626;
    Cert=\"-----BEGIN%20CERTIFICATE-----%0AMIID6zC...----END%20CERTIFICATE-----%0A\";
    Subject=\"CN=<deviceid>.<group>.<tenant>.iot.cityos-dev.woven-planet.tech\";
    URI=;DNS=hello.app.agora-lab.wph,By=spiffe://cluster.local/ns/cvm/sa/default;
    Hash=9b9c12e9850a1e287233b7d3143ef3b46baad7696fd671f5322424bef465248c;
    Subject=\"\";
    URI=spiffe://cluster.local/ns/city-ingress/sa/ingressgateway"
  }
}
```

CN=`<deviceid>`.`<group>`.`<tenant>`.iot.cityos-dev.woven-planet.tech

- The backend MUST verify `device ID`, `Group`, `Tenant` in the CN part is valid one.

### MQTT pub/sub communication

- Restrict access to topics with RabbitMQ feature to ensure each device can communicate only with each topics. (Prevent DeviceA pub/sub topics for DeviceB)

#### Reason

- No info like a XFCC heder.
- We will use a device serial number for its device ID. Therefore other device ID can be gussed from one device ID.

---

## Consequences

- We have topics shared among multiple devices like ones for sharing gate stautus. Considering those, the topic access restriction might become complex. We will talk about it with IoTA team.

---

## Note

- 2023-07-19 : Approved
- 2023-07-18 : Drafted, Originator: Kohta Natori

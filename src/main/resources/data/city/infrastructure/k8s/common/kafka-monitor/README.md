# kafka-monitor

kafka-monitor([AKHQ](https://akhq.io/) ) is the monitoring tool for service team.

Note that it behaves slightly unstably when a user attempts OIDC login after Pod restart. (to be addressed in https://wovencity.monday.com/boards/3813113014/pulses/4930238604)

## Keycloak/Drako integration

- To enable kafka-monitor to display only Kafka topics belonging to the user's namespace, it needs to retrieve information about the namespace the user belongs to. 
　　- in Lab1/Dev1, we use KeycloakGroup information because Drako Polis `list_groups_for_user` endpoint is not supported in the env
　　- In Lab2/Dev2/Preprod, we call [the Drako Polis API](https://developer.woven-city.toyota/catalog/default/api/drako-polis-api-v1alpha/definition) using the Keycloak user ID to fetch DrakoGroup information

Here is the sample response from Keycloak in Lab1 (this can be generated via e.g.[Keycloak UI in Lab1](https://id.agora-lab.woven-planet.tech/auth/admin/woven/console/#/realms/woven/clients/kafka-monitor-akhq-external/client-scopes/evaluate-scopes))

`sub` field is the user id (the woven id).

```json
{
  "exp": 1715310958,
  "iat": 1715310658,
  "jti": "6d8ccae5-f73d-4a23-88c1-1807ac86c469",
  "iss": "https://id.agora-lab.woven-planet.tech/auth/realms/woven",
  "sub": "379a441d-0c70-429d-8e83-8e3ef4150520",
  "typ": "Bearer",
  "azp": "kafka-monitor-akhq-external",
  "session_state": "c3ac4a07-99da-4660-b0b2-fd0f75784565",
  "scope": "openid email",
  "sid": "c3ac4a07-99da-4660-b0b2-fd0f75784565",
  "email_verified": false,
  "name": "Kohei Watanabe",
  "groups": [
    "/agorans/apicurio",
    "/agorans/iot",
    "/iot-test-group",
    "/agorans/kafka-admin",
    "/agorans/kafka-apicurio-sample",
    "/agorans/kafka-lab-test-resource",
    "/agorans/notification"
  ],
  "preferred_username": "kohei",
  "locale": "en",
  "given_name": "Kohei",
  "family_name": "Watanabe",
  "email": "kohei.watanabe@woven-planet.global"
}
```


## CHANGELOG

### 0.2.0 (used in Lab2, Dev2)

- [Sprint [Orc] - Replace Keycloak group of kafka-monitor with DrakoPolis](https://wovencity.monday.com/boards/3813113014/pulses/6462302925)
- Add `kustomize.toolkit.fluxcd.io/substitute: disabled` annotation to the configmap generator so that we can use `ksudo` more easily

### 0.1.4

- The keycloak operator is upgraded so the following changes are applied:

```diff
--- /tmp/before.yaml	2023-11-20 14:27:46
+++ /tmp/after.yaml	2023-11-20 14:27:52
@@ -66,7 +66,7 @@ data:
               client-id: kafka-monitor-akhq-external
               client-secret: $${KEYCLOAK_API_CLIENT_SECRET}
               openid:
-                issuer: http://keycloak-discovery.id.svc.cluster.local:8080/auth/realms/woven
+                issuer: http://keycloak-http.id.svc.cluster.local/auth/realms/woven
     akhq:
       html-head: |
         <style type="text/css">
@@ -263,7 +263,7 @@ apiVersion: keycloak.org/v1alpha1
   usages:
   - client auth
 ---
-apiVersion: keycloak.org/v1alpha1
+apiVersion: legacy.k8s.keycloak.org/v1alpha1
 kind: KeycloakClient
 metadata:
   labels:
```

### 0.1.3 (used in Lab1, Dev1)

For Lab2, the following changes are applied:

- Remove `VirtualService` 
- Add `KeycloakClient`

### 0.1.2
- Add `ServiceMonitor` to scrape [the AKHQ metrics](https://akhq.io/docs/debug.html#monitoring-endpoint) (replace `CityService` with `VirtualService` because `CityService` has [some issues in scraping the metrics.](https://woven-by-toyota.slack.com/archives/C032Z73091N/p1679468487005409))

### 0.1.1

- A user can only access to their deployed resource. (e.g. The namespace for the user is `ns-a`, the user can access the
  topic that start with `ns-a.`)
- In lab/dev environment, a user can read the topic content.

### 0.1.0

- Initial deployment
- A user can read all the topic names (not the topic content)

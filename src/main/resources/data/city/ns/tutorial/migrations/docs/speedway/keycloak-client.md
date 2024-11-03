# Keycloak client

## How to create new client

1. Please use one of templates to create your client
2. Publish the client in `dev2` cluster, client will be automatically synced with keycloak in speedway prod and pre-prod. This step is necessary since keycloak operator still in the process of migration to speedway.

### Confidential client
```yaml
apiVersion: legacy.k8s.keycloak.org/v1alpha1
kind: KeycloakClient
metadata:
  name: {{ NAME }}
spec:
  realmSelector:
    matchLabels:
      app: woven
  roles: []
  client:
    id: {{ NAME or UUIDv5 }}
    clientId: {{ NAME }}
    protocol: openid-connect
    attributes:
      use.refresh.tokens: "true"
      client.use.lightweight.access.token.enabled: "true"
    redirectUris:
      - {{ URI }}
    webOrigins:
      - {{ URI }}
    standardFlowEnabled: true
    serviceAccountsEnabled: true
    publicClient: false
    clientAuthenticatorType: client-secret
```

### Public client
```yaml
apiVersion: legacy.k8s.keycloak.org/v1alpha1
kind: KeycloakClient
metadata:
  name: {{ NAME }}
spec:
  realmSelector:
    matchLabels:
      app: woven
  roles: []
  client:
    id: {{ NAME or UUIDv5 }}
    clientId: {{ NAME }}
    protocol: openid-connect
    attributes:
      use.refresh.tokens: "true"
      client.use.lightweight.access.token.enabled: "true"
    redirectUris:
      - {{ URI }}
    webOrigins:
      - {{ URI }}
    standardFlowEnabled: true
    serviceAccountsEnabled: false
    publicClient: true
```

Fields:

- `name` should follow specs for kubernetes names https://kubernetes.io/docs/concepts/overview/working-with-objects/names/#names 
    * Example `agora-demonstration-example-service-prod`
- `clientId` Please use meaningful identifier for you client.
    * Example `agora-demonstration-example-service-prod`
- `id` please use same value as `clientId`. NOTE this field has size limit **36** symbols, please use UUIDv5 if your clientId size is bigger than 36 symbols. 
    * Example 
    * `# uuid -v5 ns:URL agora-demonstration-example-service-prod`
    * `8c6a26db-707a-5e8c-8cd6-895535fe24d9`
- `redirectUris` Valid URI pattern a browser can redirect to after a successful login. Simple wildcards are allowed such as `http://example.com/*`.
    * Example `https://demonstration-example-service.woven-city-api.toyota/*`
- `webOrigins` can be same as `redirectUris`, port and path parts of the Uris can be ommited.

### Drako specific settings
If a service in a namespace is protected by the Drako, Drako expects that in this namespace at least one keycloak client exists. ClientId of this keycloak client should match the name of the namespace.



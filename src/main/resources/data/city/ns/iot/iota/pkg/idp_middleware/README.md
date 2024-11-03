# Identity Provider(IDP) middleware

Identity Provider(IDP) middleware is a middleware for echo server. It is currently working with Keyclock JWT token and K8S AuthorizationPolicies to provide the authorization logic for IoTA services, which is allow to access resources based on tenant the user belongs too. 

## APIs
### IdpAuthNoTenantMiddleware
Use to validate the jwt token without validating tenant access for user. Generally used by API that does not have tenant parameter in path.

### IdpAuthMiddlewareInternal
Use to validate the jwt token with keycloak and check that user has right access to the requested tenant. Or allow service if they are in allow lists.

## Server
1. Apply middleware into echo server, 
    ```go
        config.AllowedInternalTenants := []string{"test", "rapid-prototyping-clp"}
        config.AllowedInternalServices := []string{"ns/iot/sa/iota-device-update-operator"}

        func (s *Server) idpAuthMiddleware(next echo.HandlerFunc) echo.HandlerFunc {
	        return im.IdpAuthMiddlewareInternal(next, config.AllowedInternalTenants, config.AllowedInternalServices, s.subjectParser)
        }

        func main() {
            // Override the global constant in Login.go
            iota.OverrideIDPConfig(config.Config.AllowedClients, config.Config.ClientID, config.Config.ClientSecret, config.Config.Url)

            subjectParser := iota.NewSubjectParser(config.AllowedInternalTenants) 
            s := NewServer(subjectParser)

            e := echo.New()
            e.Use(s.idpAuthMiddleware)
        }
    ```
2. To mix it with oapi spec, you can create security scheme type and apply it (ref: https://swagger.io/docs/specification/authentication/bearer-authentication/)
   * sample scheme type, https://github.com/wp-wcm/city/blob/86d6497adfc7e8b06ea46cff01f0777c7d83b5e1/ns/iot/iota/api/iota.yaml#L1364-L1388
   * sample usage in operation level https://github.com/wp-wcm/city/blob/86d6497adfc7e8b06ea46cff01f0777c7d83b5e1/ns/iot/iota/api/iota.yaml#L1137-L1138
   * sample usage in IoTA server: https://github.com/wp-wcm/city/blob/86d6497adfc7e8b06ea46cff01f0777c7d83b5e1/ns/iot/iota/internal/server/server.go#L1666-L168


## How to setup your request to get authentication
There are 3 usecases.
 * Request from keycloak user
 * Request from tenant's service
 * Request from iota internal service

1. Request from keycloak user: it will use JWT token, for direct user access, using one of the following
   * Generate token
    * Implement the keycloak login logic e.g. use iota SSO login method from iota package [ns/iot/iota/pkg/iota/login.go](https://github.com/wp-wcm/city/blob/363b48d440fabc6f2eb0d8d353a1bb8534dcb29f/ns/iot/iota/pkg/client/sso/auth.go#L129)
    * Retrieve `access_token` from direct call to keycloak, this is mostly used for manual testing
        ```sh
        curl --location 'https://id.agora-dev.w3n.io/auth/realms/woven/protocol/openid-connect/token' \
            --header 'Authorization: Basic' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'grant_type=password' \
            --data-urlencode 'username=<keycloak username>' \
            --data-urlencode 'password=<keycloak password>>' \
            --data-urlencode 'client_id=<keycloak client ID>' \ ## e.g. iota-client
            --data-urlencode 'scope=openid'
        ```

   * use token to APIs
    * via curl
    ```sh
        curl --location 'https://iot.agora-dev.w3n.io/tenants/test/groups' \
            --header 'Authorization: Bearer <token>'
    ```
    * Or for golang client, adding token on client request with oapi-codegen library, https://github.com/deepmap/oapi-codegen?tab=readme-ov-file#on-the-client 
    * Note that the token can be refreshed, so no need to retreive it every call


2. Request from tenant's service: Use AuthorizationPolicy and tenant allow list, without checking X-Forwarded-Client-Cert header set
    * Sample AuthorizationPolicy e.g. [authorization-policies.yaml](https://github.com/wp-wcm/city/blob/669f417d3f2b7ebe517d44ba95bad1886de2737c/infrastructure/k8s/environments/dev2/clusters/worker1-east/iot/authorization-policies.yaml#L1-L25)
    * then add tenants to the allowlist configuration to pass it to middleware, [Sample config](https://github.com/wp-wcm/city/blob/9610c0818ad14a3dcc6e2eca404198df509a8c44/infrastructure/k8s/environments/dev2/clusters/worker1-east/iot/iota/iota-env.patch.yaml#L13-L14)

3. Request from iota internal service: Use AuthorizationPolicy and service allow list, for iot own service by checking X-Forwarded-Client-Cert header.
    * [Sample AuthorizationPolicy](https://github.com/wp-wcm/city/blob/669f417d3f2b7ebe517d44ba95bad1886de2737c/infrastructure/k8s/environments/dev2/clusters/worker1-east/iot/authorization-policies.yaml#L27-L47)
    * then add service accounts to allow list [sample config](https://github.com/wp-wcm/city/blob/9610c0818ad14a3dcc6e2eca404198df509a8c44/infrastructure/k8s/common/iot/iota-gen2-0.0.10/iota.yaml#L95-L96)

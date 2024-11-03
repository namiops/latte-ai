# Changelog

All notable changes to the Custom Resource Definitions (CRDs) will be documented in this file.

## [Unreleased]

### Added

### Changed
- Initial specification for confidential and public clients has been added. You can find an example for both below
Public:
```yaml
apiVersion: woven-city.global/v1alpha1
kind: Oauth2Client
metadata:
  name: public-client
spec:
  clientId: agora-tenant.dev.pub.myapp1
  description: This is a public client
  sessionMaxLifetime: null
  defaultClientScopes: null
  optionalClientScopes: null
  useLightweightTokens: false
  publicClientSpec:
    authenticationFlow:
      authorizationCodeGrant:
        enabled: true
        redirectUris:
          - path/*
        rootUrl: https://mydomain.io/
      deviceAuthorizationCodeGrant: null 
```

Confidential:
```yaml
apiVersion: woven-city.global/v1alpha1
kind: Oauth2Client
metadata:
  name: confidential-client
spec:
  clientId: agora-tenant.dev.conf.myapp1
  description: This is a confidential client
  sessionMaxLifetime: 120000
  defaultClientScopes:
    - email
  optionalClientScopes:
    - oidc
  useLightweightTokens: null
  confidentialClientSpec:
    authenticationFlow:
      authorizationCodeGrant: null 
      deviceAuthorizationCodeGrant: 
        enabled: true
      oidcCibaGrant: 
        enabled: false # enabled: false has the same effect as not setting the property or setting it to null
      resourceOwnerPasswordCredentials: null
      clientCredentialsCodeGrant:
        enabled: true
        serviceAccountClientRoles:
          realm-management:
            - manage-clients
            - manage-users
        serviceAccountRealmRoles:
          - offline_access
    secret:
      name: my-secret
```

### Fixed

### Removed


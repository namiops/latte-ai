# Changelog

All notable changes to the Custom Resource Definitions (CRDs) will be documented in this file.

## [Unreleased]

### Added

### Changed

### Fixed

### Removed

## [0.15.0] - 2024-10-25

### Changed

- `SpiffeId` and `Oauth2ClientId` are now the default of `clientAuthenticationModes` attribute in `DrakoPolicyBinding` if not specified.

## [0.14.0] - 2024-09-26

### Added

- New `clientAuthenticationModes` attribute in `DrakoPolicyBinding` ([more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicyBinding/#clientauthenticationmodes)).
- New `ClientIds` type in `injectHeaders` attribute of `DrakoPolicyBinding` ([more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicyBinding/#x-drako-client-ids)).
- New `DrakoPolicy` of type `ClientGroup` ([more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicy/#clientgroup)).

### Changed

- Structure of `Group` type in `injectHeaders` attribute that belongs to `DrakoPolicyBinding`:

  Old structure:

  ```yaml
  spec:
    policies:
      - allow-all
    decisionStrategy: "allow-all"
    injectHeaders:
      - type: Group
        spec:
          policy: is-foo
    # ... other fields
  ```

  New structure:

  ```yaml
  spec:
    policies:
      - allow-all
    decisionStrategy: "allow-all"
    injectHeaders:
      - type: Group
        spec:
          group:
            policy: is-foo
    # ... other fields
  ```

## [0.13.0] - 2024-08-26

### Added

- New `DrakoPolicy` type `Audience` policy. This policy checks if at least one of `aud` claims of access token are present in the specified list.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicy
metadata:
  namespace: drako-test
  name: my-audience
spec:
  type: Audience
  spec:
    audience:
      - my-client-id
```

- `injectHeaders` field in DrakoPolicyBinding. When drako handles a request that uses a binding containing this field, drako will set certain HTTP headers in the request sent to the service protected by drako.

- `Group` type to `injectHeaders`. This new type sets the `x-drako-groups` header that contains group membership information for the logged-in user. Example:

```yaml
spec:
  policies:
    - allow-all
  decisionStrategy: "allow-all"
  injectHeaders:
    - type: Group
      spec:
        policy: is-foo
    - type: Group
      spec:
        policy: imported-is-bar
  # ... other fields
```

- `destinationHosts` field in DrakoPolicyBinding.
Users can configure which DrakoPolicyBinding applies to a request based on the destination host.
If the destination host of the request matches one of the destination hosts defined in the field, the DrakoPolicyBinding will be applied to that request.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: internal-access
spec:
  authenticationMode: None
  destinationHosts:
    - hostname: tsl-telescope-core-service
      port: 35002 # optional
```

## [0.12.0] - 2024-07-23

### Removed

- Support for `username` field in `DrakoGroup`.
  - Please migrate to `userId` field.

    In the future, you will be able to query the `userId` from [BURR API](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Person%20API/get_persons_search). We are still working on that. For temporary, you can query your own `userId` by accessing [our test app](https://id-test-drako-v1.agora-dev.w3n.io/) in dev2 under `x-user-id` field. If you have to query a bunch of user IDs, please kindly reach out to Identity team to help you with the process.

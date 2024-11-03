# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added

### Changed

### Fixed

### Removed

### Security

## [0.15.0] - 2024-10-25

### Security

- Enhance data protection by securing sensitive information and implementing
  secure memory erasure to prevent accidental leaks.

- Enhance JWT validation.

## [0.14.0] - 2024-09-26

### Added

- Client authentication via the new `clientAuthenticationModes` attribute in `DrakoPolicyBinding` ([more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicyBinding/#clientauthenticationmodes)).
- New `x-drako-client-ids` header containing the identifiers of the authenticated client, populated through `ClientIds` type in `injectHeaders` attribute of `DrakoPolicyBinding` ([more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicyBinding/#x-drako-client-ids)).
- New `DrakoPolicy` of type `ClientGroup` ([more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicy/#clientgroup)).

### Changed

- Structure of `Group` type in `injectHeaders` attribute that belongs to `DrakoPolicyBinding`:

  Old structure:

  ```yaml
  spec:
    injectHeaders:
      - type: Group
        spec:
          policy: is-foo
    # ... other fields
  ```

  New structure:

  ```yaml
  spec:
    injectHeaders:
      - type: Group
        spec:
          group:
            policy: is-foo
    # ... other fields
  ```

### Security

- Harden responses to Istio to prevent unauthorized manipulation (also known as
  _spoofing_) of Drako-owned headers (e.g., `x-user-id`).

## [0.13.0] - 2024-08-26

### Added

- New `DrakoPolicy` of type `MappingComparison`. This policy allows incoming requests if a mapped value extracted from them matches the criteria defined in the policy's spec.

- Comparison criteria `EqualsLoggedInUserId` compares a mapped value with the user id of the currently logged in user.

```yaml
  apiVersion: woven-city.global/v1alpha1
  kind: DrakoPolicy
  metadata:
    namespace: <target-namespace>
    name: <target-policy-name>
  spec:
    type: MappingComparison
    spec:
      mappingComparison:
        key: <mapped-value-key>
        type: EqualsLoggedInUserId
```

- New `DrakoPolicy` of type `Audience` ([refer to the documentation for more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicy/#audience)).

- Information regarding DrakoGroup affiliation of the logged-in user can be passed to the target service. The header will only be set in requests that match on a binding with the `injectHeaders` field. For more details on how to setup your `DrakoPolicyBinding` to enable this feature, please refer to the [CHANGELOG](/ns/id/drako_data/CHANGELOG.md) in drako_data. Once set up, Drako will set the `x-drako-groups` header in requests to the destination service. This header will contain a list of namespaces, and for each namespace the names of the DrakoGroups the caller user belongs to (`in_group`) and the ones they do not belong to (`not_in_group`).

Example:

```json
{
  "namespaces": [
    {
      "name": "delivery-system",
      "in_group": [
        "drivers"
      ],
      "not_in_group": [
        "managers",
        "admins"
      ]
    },
    {
      "name": "inventory-system",
      "in_group": [
        "collaborators"
      ],
      "not_in_group": [
        "operators",
        "admins"
      ]
    }
  ]
}
 ```

In the above example, we can see that the currently logged-in user belongs to the `drivers` group of the `delivery-system` namespace and the `collaborators` group of the `inventory-system` namespace. We can also see groups the user does not belong to: `admins`, `managers` in the `delivery-system` namespace and `admins`, `operators` in the `inventory-system` namespace.

- `destinationHosts` field in DrakoPolicyBinding ([refer to the documentation for more details](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicyBinding/#destinationhosts)).

### Changed

- `username` claim of access token is not used anymore. Introspection result field `username` is used now to get user's `username`.
- Allowing services to use the `None` authentication mode is now managed through a Gatekeeper policy instead of a configuration parameter in Drako. In practice, this means that services that will use `authenticationMode: None` in their `DrakoPolicyBinding` need to be declared as follows in the `enforce-drakopolicybinding-authmode-none.yaml` file for the corresponding environment (e.g. [in dev2](/infrastructure/k8s/environments/dev2/clusters/worker1-east/gatekeeper-system/constraints/id/enforce-drakopolicybinding-authmode-none.yaml))

```yaml
apiVersion: constraints.gatekeeper.sh/v1beta1
kind: IdConstraintDrakoPolicyBindingAuthModeNone
metadata:
  name: drakopolicybindingauthmodenone
spec:
  parameters:
    allowedServiceAccounts:
      - namespace: service-namespace
        serviceAccounts:
          - a-service-account-name
          - another-service-account-name
```

## [0.12.0] - 2024-07-23

### Removed

- Support for `username` field in `DrakoGroup`.
  - Please see [CHANGELOG](/ns/id/drako_data/CHANGELOG.md) in drako_data for more details.

### Fixed

- Rare case where requests could be incorrectly denied if the IdP does not include certain claims in the OIDC introspection response which are considered required by drako but optional per OIDC specification. After this fix, drako will not rely on the OIDC introspection response for fields other than `active` (and `client_id` if `authenticationMode: SingleUseToken` is used in the `DrakoPolicyBinding`). Other fields are extracted from the access token itself.

## [0.11.0] - 2024-07-11

### Added

- New DrakoPolicy of type "Import". This type of policy allows you to import a DrakoPolicy into the namespace of the policy (source) from another (target) namespace. Please note that if a policy refers to other resources (e.g. a DrakoGroup), those will be imported as well.
  In the source namespace, define a DrakoPolicy of type "Import" to import any type of DrakoPolicy from a target namespace by specifying the `namespace` and `name` of the target DrakoPolicy, for example:

  ```yaml
  apiVersion: woven-city.global/v1alpha1
  kind: DrakoPolicy
  metadata:
    namespace: <source-namespace>
    name: <source-policy-name>
  spec:
    type: Import
    spec:
      import:
        namespace: <target-namespace>
        name: <target-policy-name>
  ```

  In the target namespace, export the target DrakoPolicy to the source namespace by specifying the source namespace in the `exportTo` field, for example:

  ```yaml
  apiVersion: woven-city.global/v1alpha1
  kind: DrakoPolicy
  metadata:
    namespace: <target-namespace>
    name: <target-policy-name>
  spec:
    exportTo:
      - namespace: <source-namespace>
    type: Group
    spec:
      group:
        name: <group-name>
  ```

- Validations of DrakoPolicy of type "Import":
  1. It should not import DrakoPolicy from the same namespace.
  2. It should not be exported to other namespaces (nested import is not allowed).
  3. The target DrakoPolicy to be imported should exist in the target namespace.
  4. The target DrakoPolicy should not have "Import" type.
  5. The target DrakoPolicy should be exported to the current (source) namespace.

### Changed

- `userId` in Drakogroups has been added to accept a unique user identifier (also known as Woven ID) and is now mandatory. `username` in Drakogroups is *deprecated* and will soon be removed, please use `userId` instead:

```yaml
apiVersion: woven-city.global/v1alpha2
kind: DrakoGroup
metadata:
  name: admins
  namespace: example
spec:
  userList:
    - userId: 19c89e94-8d20-447d-aca9-889b9f7dc020 # alice
    - userId: bb3688f5-80b1-41ee-bb64-02e6b25139a6 # bob
```

### Removed

- Support for "on-behalf-of" (OBO) consent check requests, since this support was removed from Consent Service (#28149).
- Support for deprecated `authorizedParties` field in DrakoPolicyBinding. Please use DrakoPolicy with AuthorizedParties type instead.

### Security

- Extracting header values from Envoy now does case-insensitive header key matching.

## [0.10.0] - 2024-05-31

### Added

- Provide earlier feedback to developers by validating contents of DrakoPolicyBinding and DrakoPolicy, updating status field accordingly as soon as resources are applied. Developers can check if their resources have been applied successfully in the cluster through kubectl command:

  ```shell
    kubectl get drakopolicies -n <namespace>
    kubectl get drakopolicybindings -n <namespace>
  ```

- Stricter validations of DrakoPolicyBinding:
  1. If the policies listed in policies field are not included in the expression/decisionStrategy field, DrakoPolicyBinding will be marked as invalid.
  2. If the policies used in the expression/decisionStrategy field are not listed in `policies` field, DrakoPolicyBinding will be marked as invalid.
  3. Only allow boolean operators (i.e., ||, &&, !, !=, ==), boolean constants (i.e., true or false), and policy names in the `expression`/`decisionStrategy` field.
- New authentication mode: `FederatedIdentityService`. The implementation is not yet production-ready.

### Changed

- DrakoGroup resource definition has been changed to reference users by their Woven id instead of their username. Usernames will be deprecated in an upcoming release.

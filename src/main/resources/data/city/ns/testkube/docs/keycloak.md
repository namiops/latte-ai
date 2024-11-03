# Fine-grained access control

## Requirements
- Testkube uses `drako` for authorization
- Keycloak has groups

## Keycloak client setup
- Log in to the keycloak administration console.
- Find the testkube client.
- Authorization Tab
    + Add resource for tests of the group. You can use regex in the URI (example: `regex:/results/v1/tests/myteam-.*`)
    + Add group policy.
    + Add permission for the resource and apply group policy from the previous step.
    + Make sure the permission uses the `Unanimous` decision strategy.

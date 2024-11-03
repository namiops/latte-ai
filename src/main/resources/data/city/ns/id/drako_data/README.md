# drako-data

This is a rust lib project containing the shareable data types from drako.

Initially it contains the CRD definitions and an example binary that is capable
of outputting the CRDs that must be loaded into the cluster at a later date.

## Development

If you modify any of the CRDs, you can auto-generate their YAML description in
the console by running the following:

```console
bazel run //ns/id/drako_data:drako_data_crd_gen
```

## Custom Resource Definition

We provide a few CRDs inspired by the kubernete's [RBAC](https://kubernetes.io/docs/reference/access-authn-authz/rbac/).

We provide the Drako family of CRDs. It is inspired by istio's [Authorization
 Policy](https://istio.io/latest/docs/reference/config/security/authorization-policy/)
CRD, but doesn't follow the exact scheme.


### NOW:

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicy
metadata:
  name: keycloak
  namespace: foo
spec:
  type: keycloak
  mapping:
    requestMethodRoleMapping:
      - method: POST
        role: CREATE
      - method: DELETE
        role: DELETE
      - method: GET
        role: VIEW
      - method: PUT
        role: EDIT
```

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: service
  namespace: foo
spec:
  policies: ["keycloak"]
  services: ["fooservice"]
  expression: "keycloak"
```

### Ideas for the future

Those need more work before properly consolidated, but they serve as a starting
point for our conversation about it.

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicy
metadata:
  name: example-expression
  namespace: foo
spec:
  type: uma
  mapping:
    kind: MultiPartForm
    method: PUT
    path: "/{woven_id}"
    attributes:
      woven_id: id
      metadata:
        role: EDIT

```


```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicy
metadata:
  name: example-expression
  namespace: foo
spec:
  type: consent
  mapping:
    path: "/baseurl/{woven_id}/{resource_id}"
```

```yaml
apiVersion: woven-city.global/v1alpha1
kind: DrakoPolicyBinding
metadata:
  name: example-expression-eval
  namespace: foo
spec:
  policies: ["keycloak", "uma", "dps"]
  services: ["a", "b", "c"]
  expression: "(uma || keycloak) && dps"
```

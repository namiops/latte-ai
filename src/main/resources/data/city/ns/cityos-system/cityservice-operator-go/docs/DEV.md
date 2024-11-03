## Prepare

- aqua https://aquaproj.github.io/docs/reference/install

```
$ aqua install
```

## Usage for developing

```
# Create a kind cluster and install cert-manager
$ make start

$ screen -d -m aqua exec tilt up --host xxx.xxx.xxx.xxx

# access http://xxx.xxx.xxx.xxx:10350

# Delete a kind cluster
$ make stop
```

## Memo
- To update a CRD `config/crd/bases/woven-city.global_cityservices.yaml`
    - Update `api/[version]/cityservice_types.go` using [some markers](https://book.kubebuilder.io/reference/markers/crd-validation.html)
    - `make manifest`, then generated this.
- To update a Role `config/rbac/role.yaml`
    - Update `controllers/cityservice_controller.go` using some markers (same as above)
    - `./bin/controller-gen crd rbac:roleName=manager-role webhook paths="./..." output:crd:artifacts:config=config/crd/bases;`
- Copy some Keycloak operator sources to api/v1alpha1/*
    - Add `// +kubebuilder:skip` on front line of `package` to prevent `controller_gen` creating CRD.


## Plan to implement

- [ ] Validate cityservice custom resource must be only one per namespaces.
- [ ] Using some template engine (helm like)
- [ ] validation with jsonschema (sync with STS)

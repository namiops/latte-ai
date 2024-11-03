# E2E tests in KinD local cluster
This directory enables running a local development environment. The ginkgo execution creates a KinD cluster and deploys the [manifests](./manifests) before running the automated tests files (e.g. [keystone_e2e_test](./keystone_e2e_test.go)).

## Requirements

The following CLIs are required to bootstrap and run the tests. The versions used to successfully run the tests are included.

- docker (version 26.1.3, build b72abbb6f0)
- kubectl (Client Version: v1.29.2, Kustomize Version: v5.0.4-0.20230601165947-6ce0bf390ce3)
- kind (kind v0.20.0 go1.20.4 darwin/arm64)
- skaffold (v2.13.2)
- openssl (OpenSSL 3.3.2 3 Sep 2024 (Library: OpenSSL 3.3.2 3 Sep 2024))
- ginkgo (Ginkgo Version 2.15.0)

## Suggested workflow

- Make any desired modifications to environment bootstrap in [skaffold.yaml](./skaffold.yaml) or [kind-config.yaml](./kind-config.yaml)
- Make any desired modifications to Kubernetes resources in [manifests/](./manifests)
- Extend the test cases in ./e2e_test.go or in a new file

## Execution

```sh
ginkgo run .
```

:warning: The files `localhost.key`, `tls.crt`, and `tls.key` will be generated during bootstrap and are used to configure TLS for KinD, Keystone and the test clients.


To skip re-deploying resources and only run tests (requires a `kubeconfig` file in [this](./) directory):

```sh
ginkgo run . -- --skip-kind=true --skip-skaffold=true;
```

## TODO

- use bazel built images in Skaffold bootstrap [file](./skaffold.yaml)

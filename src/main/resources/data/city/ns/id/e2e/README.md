# E2E test with Playwright
This repository includes E2E test script for namespace `id`

## Table Of Contents
- [Test against `lab` cluster](#try-this-against-lab-cluster)
- [Test against `local` cluster](#how-to-run-the-test-against-local-cluster)
- [How to debug test runner](#how-to-debug-test-code)


## Try this against `lab` cluster
If you already setup `lab` cluster kubectl context. There is no prerequisite needed for run against `lab` cluster 🥳🥳🥳 
```bash

bazel run //ns/id/e2e:image
export USER=$(kubectl --context="lab" get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_USERNAME}" | base64 --decode)
export PASS=$(kubectl --context="lab" get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_PASSWORD}" | base64 --decode)
export TEST_ID=$(date +%Y%m%dT%H%M%S-%s)
docker run -it \
    -e USER=$USER \
    -e PASS=$PASS \
    -v $HOME/.kube:/root/.kube \
    -v /tmp/id-e2e-test/$TEST_ID/share:/share \
    -v /tmp/id-e2e-test/$TEST_ID/config:/root/.id/e2e \
    --network host --entrypoint=/bin/bash bazel/ns/id/e2e:image \
    -c '
    #!/bin/bash
    ./run-config-cli generate --keycloak-admin-client-id=admin-cli --keycloak-admin-username=$USER --keycloak-admin-password=$PASS --keycloak-admin-base-url=https://id.agora-lab.woven-planet.tech/auth --cluster-domain=agora-lab.woven-planet.tech --force
    cd /test/ns/id/e2e/run.runfiles/wp_wcm_city/ns/id/e2e
    /test/ns/id/e2e/run test --config playwright.config.ts --reporter list ./tests --output=/share/results/
    '
```

---

## How to run the test against `local` cluster
### Prerequisites
- prepare `id-test-drako-v1` and `id-test-sts-v2` namespace in local cluster
- make sure `hostfile` and `minikube tunnel` is setup properly includes these domain
    - `id.woven-city.local`
    - `id-test-drako-v1.woven-city.local`
    - `id-test-sts-v2.woven-city.local`
- run config generate cli (See below) 
- run test runner command (See below)
### Quick start
```bash
bazel run //ns/id/e2e:image
export TEST_ID=$(date +%Y%m%dT%H%M%S-%s)
docker run -it \
    -v $HOME/.kube/config:/root/.kube/config \
    -v $HOME/.minikube:$HOME/.minikube \
    -v /tmp/id-e2e-test/$TEST_ID/share:/share \
    -v /tmp/id-e2e-test/$TEST_ID/config:/root/.id/e2e \
    --network host --entrypoint=/bin/bash bazel/ns/id/e2e:image \
    -c '
    #!/bin/bash
    kubectl config use-context minikube # Or kind-kind 
    export USER=$(kubectl get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_USERNAME}" | base64 --decode)
    export PASS=$(kubectl get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_PASSWORD}" | base64 --decode)
    ./run-config-cli generate --keycloak-admin-client-id=admin-cli --keycloak-admin-username=$USER --keycloak-admin-password=$PASS --keycloak-admin-base-url=https://id.woven-city.local/auth --force
    cd /test/ns/id/e2e/run.runfiles/wp_wcm_city/ns/id/e2e
    /test/ns/id/e2e/run test --config playwright.config.ts --reporter list ./tests --output=/share/results/
    '
```
> Note: make sure `/tmp/id-e2e-test` has a right permission for container RW

After test. Any result from the runner will be output to `/tmp/id-e2e-test/${timestamp}/share`

### Explanation
1. build test images with
    ```bash
        bazel run //ns/id/e2e:image
    ```
2. mount `kubecontext` and output folder also use `host` network mode to make sure this docker container able to reach minikube cluster like host machine
    ```bash
        export TEST_ID=$(date +%Y%m%dT%H%M%S-%s)
        docker run -it \
        -v /tmp/id-e2e-test/$TEST_ID/share:/share \
        -v /tmp/id-e2e-test/$TEST_ID/config:/root/.id/e2e \
        -v $HOME/.kube/config:/root/.kube/config -v $HOME/.minikube:$HOME/.minikube --network host --entrypoint=/bin/bash bazel/ns/id/e2e:image
    ```
    > noted that for kind, you don't need to mount `.minikube` folder
3. execute config generate commands (inside test container)
    ```bash
      #!/bin/bash

      # Grab admin username/password
      export USER=$(kubectl get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_USERNAME}" | base64 --decode) 
      export PASS=$(kubectl get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_PASSWORD}" | base64 --decode) 

      # Grab create configuration for test runner (include test user credential)
      ./run-config-cli generate --keycloak-admin-client-id=admin-cli --keycloak-admin-username=$USER --keycloak-admin-password=$PASS --keycloak-admin-base-url=https://id.woven-city.local/auth --force
    ```
4. run test commands
    ```bash
    cd /test/ns/id/e2e/run.runfiles/wp_wcm_city/ns/id/e2e
    /test/ns/id/e2e/run test \
        --config playwright.config.ts \
        --reporter list ./tests \
        --output=/share/results/
    ```
    > you can narrow down the test file by changing the value from `./tests` to something like `./tests/drako` to run only test under `drako` folder
    > you can specific test case to run with `-g` flag. For instance `-g "Successful.*"`

---

## How to debug test code
VSCode debugger is supported (Ofcourse not with Bazel build 🤗) Underhood we execute with traditional `node` runtime and VSCode Plugin
1. Generate configuration for test runner in your machine (config will be written to `$HOME/.id/e2e`)
```bash
export USER=$(kubectl get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_USERNAME}" | base64 --decode)
export PASS=$(kubectl get secret credential-keycloak -n id -o jsonpath="{.data.ADMIN_PASSWORD}" | base64 --decode)
npx ts-node --esm ./cli/config.ts generate --keycloak-admin-client-id=admin-cli --keycloak-admin-username=$USER --keycloak-admin-password=$PASS --keycloak-admin-base-url=https://id.woven-city.local/auth --force
```

2. Install Playwright Extension for VSCode
3. Add breakpoint in VSCode and try to run test in Test Explorer 🍻🍻
    ![ss](./e2e-debug-ss.png)

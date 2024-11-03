# Backend Car Gate

## Work in Progress

**Note:** This document is currently a work in progress. Some sections may be incomplete or subject to change.


## Dashboards

- Over View

## Set up development environments

### Install asdf

See [Local Environment Building for Windows - asdf (but will not be maintained any more)](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=151770232#id-%E3%83%AD%E3%83%BC%E3%82%AB%E3%83%AB%E7%92%B0%E5%A2%83%E6%A7%8B%E7%AF%89forWindows-asdf)

### Install Go

```sh
asdf install golang 1.21.0
asdf global golang 1.21.0
```

#### How to check

```sh
go version
go version go1.21.0 linux/amd64
```

### Install library

See [here](https://github.com/wp-wcm/city/pull/22707#:~:text=https%3A//github.com/wp%2Dwcm/city/blob/main/docs/development/go/README.md%23updating%2Ddependencies).

```sh
bazel run @go_sdk//:bin/go -- get -u <some_dependency>@<version>
bazel run //:go_mod_tidy
```

### Export Environment variables

```sh
export TLS_CONFIG_INSECURE_SKIP_VERIFY=true
```

## Unit test

### Run unit test

```sh
bazel test //projects/traffic-signal/backend-car-gate/...
```

### Coverage collection

Measure test coverage.

```sh
go test -cover -count=1 ./...
```

Specifically display which rows are not covered.

```sh
rm cover.out
rm cover.html
go test -cover "-coverprofile=cover.out" "-coverpkg=./..." "./..."
go tool cover -html=cover.out -o cover.html
```

## Build and Run

### Build

```sh
bazel build //projects/backend-car-gate/internal/...
```

### Run

```sh
bazel run //projects/backend-car-gate/internal/cmd
```

### Generate Mock Files

```sh
go generate ./...
```

#### Run only the first time for generating mock files

This project uses `go.uber.org/mock/mockgen` because `github.com/golang/mock/mockgen` has not been maintained anymore.

If `github.com/golang/mock/mockgen` is installed to your environment.

```bash
go clean -i -n go get github.com/golang/mock/mockgen
```

then, run command below.

```bash
go install go.uber.org/mock/mockgen@latest
```

## Test cases

TBD.

## Launch docker container for local debugging

**Note:** In most cases, when debugging, we would like to connect to RabbitMQ on Agora from the local machine, so preparing RabbitMQ on local is skipped.

### Step 1: Change the Directory

First, go to the `.local_debug` directory in your project. Run this command:
```bash
cd $(git rev-parse --show-toplevel)/projects/backend-car-gate/.local_debug
```

### Step 2: Get Certification Files

You need some certification files from 1Password. Here's how to get them:

1. Open 1Password.
2. Go to **TrafficSignal** > **[For PreProd]MQTT_IOTA_***.
3. Download these files:
  - `fss-ts-test00_ca.pem`
  - `fss-ts-test00_crt.pem`
  - `fss-ts-test00_key.pem`

### Step 3: Put Certification Files in `/certs/` Folder

After downloading, put these files in the `certs` folder inside `.local_debug`. Your folder should look like this:

```console
.local_debug
├── certs
│   ├── fss-ts-test00_ca.pem
│   ├── fss-ts-test00_crt.pem
│   └── fss-ts-test00_key.pem
```

### Step 4: Get Password(IOTA_SVC_PASSWORD) from 1Password

You also need a password from 1Password. Here’s how to get it:

1. Open 1Password.
2. Go to **[For PreProd]MQTT_IOTA_USERNAME_PASSWORD**.
3. Copy the password.

### Step 5: Update `local.env` File

Open the `local.env` file and find the line for `IOTA_SVC_PASSWORD`. Replace `PASSWORD` with the password you copied from 1Password.


### Step 6: Common Docker Commands

- Load the target image `//projects/backend-car-gate/internal/cmd:cmd` into your local Docker client.
    ```bash
      bazel run //projects/backend-car-gate/internal/cmd:image.load
    ```
    - for macOS user:
    ```bash
      bazel run //projects/backend-car-gate/internal/cmd:image.load --platforms=@io_bazel_rules_go//go/toolchain:linux_amd64 -- --norun
    ```

```bash
docker compose up
```

- after debugging finishes, run

```bash
docker compose down
```

- When re-building the container, run

```bash
docker compose up --build
```

## Test Tools

For detailed instructions on how to use the Test Tools, please refer to the [Test Tool README](./tests/README.md).

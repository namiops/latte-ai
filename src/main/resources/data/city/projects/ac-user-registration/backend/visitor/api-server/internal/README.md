# ac-user-registration-visitor-registration-api-server

## Set environment variables for local development

Create `local.env` by copying a template file `local.env.template`.

```bash
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/visitor
cp local.env.template local.env
```

Ask backend members how to set each secret variables.

## Build and Run

```bash
# get dependencies if you need
bazel run @go_sdk//:bin/go -- get
# update BUILD files if you need
bazel run //:gazelle 
bazel run //:buildifier 

# build image
# Run the build command whenever you update the source code
bazel run //projects/ac-user-registration/backend/visitor/api-server/internal/cmd:image.load
# run image
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/visitor
docker compose -f compose_visitor_api.yaml up
```

## About dummy keycloak account used in BURR mocks

To check the operation using the BURR mocks,
dummy WovenID to be returned as Guardianship for the child is as follows

`6441df1f-7f06-4aee-9c06-692eca6d949d`

This dummy guardianIds account user & pass
fsstest-fsstestvisitor0001@woven-planet.global, Test-pass-0001

Please use this account for tokens used with child API.

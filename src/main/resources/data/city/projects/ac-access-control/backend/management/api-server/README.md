# ac-access-control-management-api-server

TBU

## Build and Run

```bash
# get dependencies if you need
bazel run @go_sdk//:bin/go -- get
# update BUILD files if you need
bazel run //:gazelle 
bazel run //:buildifier 

# Run the command whenever you update the source code
bazel run //projects/ac-access-control/backend/management/api-server/internal/cmd:image.load
# run image with databases
cd $(git rev-parse --show-toplevel)/projects/ac-access-control/backend/.local_debug/management
docker compose -f compose_management_api.yaml up
```

In case of building on a Mac, use below option to cross compile.
```bash
# build
bazel build //projects/ac-access-control/backend/management/api-server/internal/cmd --platforms=@io_bazel_rules_go//go/toolchain:linux_amd64
# Run the command whenever you update the source code
bazel run //projects/ac-access-control/backend/management/api-server/internal/cmd:image.load --platforms=@io_bazel_rules_go//go/toolchain:linux_amd64 -- --norun
```

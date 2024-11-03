# ac-user-info-stayer-cronjob

TBU

## Build and Run

```bash
# get dependencies if you need
bazel run @go_sdk//:bin/go -- get
# update BUILD files if you need
bazel run //:gazelle
bazel run //:buildifier

# build image
# Run the command whenever you update the source code
bazel run //projects/ac-access-control/backend/user-info/stayer-cronjob/internal/cmd:image.load

# Run the command whenever you update the source code (for macOS user)
bazel run //projects/ac-access-control/backend/user-info/stayer-cronjob/internal/cmd:image.load --platforms=@io_bazel_rules_go//go/toolchain:linux_amd64 -- --norun

# run image
cd $(git rev-parse --show-toplevel)/projects/ac-access-control/backend/.local_debug/user-info/stayer-cronjob/
docker compose -f compose_stayer-cronjob.yaml up
```

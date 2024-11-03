# ac-user-info-api

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
bazel run //projects/ac-access-control/backend/user-info/api/internal/cmd:image.load
# run image
cd $(git rev-parse --show-toplevel)/projects/ac-access-control/backend/.local_debug/user-info/api
docker compose -f compose_user_info_api.yaml up
```

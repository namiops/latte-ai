# ac-access-control-management-worker

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
bazel run //projects/ac-access-control/backend/management/worker/internal/cmd:image.load
# run image
cd $(git rev-parse --show-toplevel)/projects/ac-access-control/backend/.local_debug/management
docker compose -f compose_management_worker.yaml up
```

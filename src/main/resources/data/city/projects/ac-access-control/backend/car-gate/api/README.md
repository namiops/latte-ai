# ac-car-gate-api

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
bazel run //projects/ac-access-control/backend/car-gate/api/internal/cmd:image.load
# run image
cd $(git rev-parse --show-toplevel)/projects/ac-access-control/backend/.local_debug/car-gate
docker compose -f compose_car_gate_api.yaml up

# run the following command when you run the local server for the first time 
bash $(git rev-parse --show-toplevel)/projects/ac-access-control/backend/.local_debug/setup_couchdb.sh
```

# ac-user-registration-worker-registration-api-server

## Set environment variables for local development

Create `local.env` by copying a template file `local.env.template`.

```bash
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/worker
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
bazel run //projects/ac-user-registration/backend/worker/api-server/internal/cmd:image.load
# run image
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/worker
docker compose -f compose_worker_api.yaml up
```

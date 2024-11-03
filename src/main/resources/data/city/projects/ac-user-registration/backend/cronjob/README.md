# Cron Job Applications

## Set environment variables for local development

Create `local.env` by copying a template file `local.env.template`.

```bash
cd $(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/cronjob
cp local.env.template local.env
```

## Build and Run

Build

```bash
# get dependencies if you need
bazel run @go_sdk//:bin/go -- get
# update BUILD files if you need
bazel run //:gazelle 
bazel run //:buildifier 

# build image
# Run the build command whenever you update the source code
bazel run //projects/ac-user-registration/backend/cronjob/{foldername}/internal/cmd:image.load
```

Run the command to execute jobs locally.

## get-id-results

```bash
LOCAL_DEBUG_FILE_PATH="$(git rev-parse --show-toplevel)/projects/ac-user-registration/backend/.local_debug/cronjob"
ENV_FILE_PATH=$LOCAL_DEBUG_FILE_PATH/local.env
bazel run //projects/ac-user-registration/backend/cronjob/get-id-results/internal/cmd:image.load
docker run --net=host -it --env-file $ENV_FILE_PATH -v "$(git rev-parse --show-toplevel)"/projects/ac-user-registration/backend/.local_debug/cronjob/secrets:/vault/secrets projects/ac-user-registration/backend/cronjob/get-id-results/internal/cmd:image
```

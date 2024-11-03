# ac-access-control-backend common modules

## Run unit tests

Move directory under each module.
Execute the following command.

```bash
$(git rev-parse --show-toplevel)/bazel-external/go_sdk/bin/go test -v
```

Coverage measurements are execute following command.

```bash
$(git rev-parse --show-toplevel)/bazel-external/go_sdk/bin/go test -coverage
```

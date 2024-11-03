# Outputs 

## build vs. run
Bazel, in general, does not support modifying the source tree. All `bazel build` commands generate artifacts inside the build sandbox that are intended to be used by *other* build artifacts, or the resulting output - not sent back to source.

The exception to this is a `bazel run` command, which *is* allowed to modify the underlying filesystem. In Agora, with the current setup, copying to source is required, or Flux will not be able to locate the files and deploy them. Until Agora is able to deploy artifacts generated from inside Bazel, all Zebra rules should include a target that allows copying back to the source tree. 

The current recommendation is using the Aspect `write_source_files` library, which will generate a `.copy` target.

## A note on the infrastructure directories

Because Flux deploys YAML artifacts from the `/infrastructure` tree, and several validation tests in `/infrastructure` use Bazel's glob function to collect files for the tests, this provides some extra complexity for code generation with copy-to-source if deployable k8s artifacts are the desired output.

This is due to two limitations, by design, in Bazel.

* `write_source_files` can only write to subpackages, i.e. folders below the BUILD file that defines the target

* globs are not allowed to cross package boundaries, defined as any folder with a BUILD file.

Therefore, if the following folder setup is used:

```
infrastructure
├── k8s
│   ├── lab
│       ├── example-service
|           ├── BUILD
|           ├── values.yaml
|           ├── generated.yaml
```

the BUILD file in the `example-service`, while required to put the `generated.yaml` in the correct place, specifies `example-service` as a package. Since the globs cannot cross the boundary into this new package, the files in `example-service` are hidden files from the top-level infrastructure tests and will cause the build to fail.

To work around this limitation, create an exported filegroup containing the required files in the service folder's BUILD file:

```
filegroup(
    name = "yaml-files",
    srcs = glob(["*.yaml"]),
    visibility = ["//visibility:public"],
)
```

and add it to the dependencies of the top-level environment infrastructure Bazel file e.g. `/infrastructure/k8s/lab/zebra_files.bzl`:

```python
ZEBRA_FILES = [
    ...

    "//infrastructure/k8s/lab/example-service/example-service:yaml-files",
]
```

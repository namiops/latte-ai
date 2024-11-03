# Istio QuitQuitQuit

This is a small project to build a binary to simplify calling istio's
quitquitquit endpoint for jobs and other k8s resources that require calling
this endpoint. The binary is officially called `istio_quitquitquit` but uses an
alias of `iqqq` for simplicity. From here on, the project will be referred to
as `iqqq`.

### Background

Before this, teams were often switching off distroless images, and adding curl
just so they could use bash and curl to kill the sidecar. This drastically
increases the image sizes, and adds extra vulnerabilities within the image. By
adding this binary to your image, you can continue to use a distroless image,
and call this binary in the post stages of your k8s resource.

## How to Use

The `iqqq` binary functions as a standalone or wrapper binary. If no arguments
are provided, then it acts as a standalone binary and will immediately try to
quit the istio sidecar. If any arguments are provided, then the first argument
must be a binary, and the remaining arguments will be provided to that binary.
`iqqq` will immediately execute your binary with its arguments, and upon exit
will try to quit the istio sidecar.

#### Settings

`iqqq` behaviour configured via environment variables. 

*Note: when `iqqq` is a nop (no operation) or disable, your wrapped program still 
executes in its entirety! Only the command to quit the istio sidecar is 
disabled.*

* `DISABLE_IQQQ`
  * Whether k8s is detected or not, do NOT try to quit the sidecar
  * This turns `iqqq` into essentially a nop
  * This is useful for migrating to or off `iqqq`
    * You can add the `iqqq` wrap command, but disable until you remove the 
      other tools from quitting the sidecar
* `KUBERNETES_SERVICE_HOST`
  * This is an env variable always set by k8s, if NOT set then `iqqq` will be a nop
* `ISTIO_PORT`
  * If not set, the port defaults to `15020`
  * This can be used if your istio sidecar is on a different port

#### Examples

Standalone Usage: This will immediately try to quit the istio sidecar
```shell
iqqq
```

Normal Program Usage: This is a standard usage of the ls binary
```shell
ls -la
```

Wrapped Program Usage: This will immediately execute the ls binary with the
arguments provided. Once completed, then attempt to quit the istio sidecar.
```shell
iqqq ls -la
```

## Enabling IQQQ

### Update your OCI Image Target

The city monorepo provides a helper macro for oci_image that simplifies using
istio quitquitquit. The full details will be on the
[oci_image macro](/ns/bazel_oci/private/image.bzl). The binary allows wrapping
any program execution, or using it as a standalone process to quit istio's
sidecar.

You can use any of the [ns/bazel_oci](/ns/bazel_oci) image macros to add or
enable the iqqq wrapping. All macros take in the attribute called `iqqq`. This
attribute has two available options: `IQQQ_ADD` and `IQQQ_WRAP`.

* `IQQQ_ADD` will add the `iqqq` binary to your image
* `IQQQ_WRAP` will also add the binary and wrap the entrypoint with `iqqq`

#### IQQQ_WRAP

The RECOMMENDED option is `IQQQ_WRAP`. This adds the binary, and wraps your
image's entrypoint to first call `iqqq`. The `iqqq` program will then execute
all additional arguments as if you just called the program naturally. When your
program exits, `iqqq` will call the quit endpoint to the istio sidecar only if
you are running in a k8s container.

If using a <lang>_image macro, such as js_image, go_image, rust_image, etc.,
then you do not need to provide an entrypoint, as these macros do this for you.
To enable iqqq, all you have to do is use this option: `IQQQ_WRAP`.

Example:

```build
load ("//ns/bazel_oci/java:image.bzl", "java_image")

java_image(
    name = "server_image",
    binary = ":server",
    iqqq = "IQQQ_WRAP",
)
```

*The entrypoint for this image will be `iqqq java -j /app`. This method will
immediately execute your program, and after it exits, the istio quit endpoint
will be called.*

#### IQQQ_ADD

The `IQQQ_ADD` option is good when you don't have a set entrypoint, or program
that you wish to wrap. However, this requires that you MUST execute `iqqq` on
the image when you wish to stop the istio sidecar.

This is NOT recommended, as it requires additional effort when setting up how
your image is used. This is usually only useful for generic images which do not
have a standard entrypoint.

Example:

```build
load ("//ns/bazel_oci:city.bzl", "oci_image")

oci_image(
    name = "my_image",
    base = ":some_base_image",
    tars = [":additional_binaries"],
    iqqq = "IQQQ_WRAP",
)
```

*With this approach, iqqq will be available on path, but you must call it yourself.*

### Remove Custom Entrypoint From Your Manifest

Please remove the custom entry point for your manifest. Many teams use a custom
entry point, such as using bash to call your image binary, followed by curl to
call quitquitquit. With these changes, that will no longer be necessary.
Ideally we want our k8s manifests to use the same entrypoint that we define on
the image by default for improved simplicity!

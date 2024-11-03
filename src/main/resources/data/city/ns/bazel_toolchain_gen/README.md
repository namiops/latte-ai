# Toolchains, repository rules, and magic

All dependencies, generated or external, must be specified or Bazel will not be able to use them.

## About toolchains

Bazel toolchains have advantages over writing dependencies directly in `BUILD` files:

_Consistency_: When dependencies are written directly in `BUILD` files, the exact versions of the dependencies used may vary across different environments, which can result in subtle differences in the built artifacts. By defining a toolchain, developers can ensure that the same versions of the tools and dependencies are used consistently across different environments.

_Portability_: By defining a toolchain, developers can make their software more portable, as the software can be built with the same set of tools and dependencies on different platforms. This can help reduce the effort required to port the software to new platforms or to update dependencies.

## About repository_rules

Repository rules are used for non-hermetic behavior, particularly downloading and managing dependencies from external sources. They are then used as packages, using the `@` syntax

## Why generate?

There is a large amount of boilerplate required to add new tools to our current setup, and prior behavior required copy-and-paste, find-and-replace, and trial-and-error.

This module:

* Takes a series of platforms, URLs, and SHA hashes for a new tool
* Generates the required repository_rules
* Unifies the dowloaded binary with a consistent name
* Exposes the binary target with a filegroup
* Registers the toolchains with Bazel

Note that there are two separate paths required for Bazel toolchains. The toolchain `repository_rule`s and registration must happen during the `WORKSPACE` file evaluation, which is `bazel_toolchain_gen_register`. 

Due to Bazel's internal plumbing, toolchains themselves are not, however, allowed to be defined during `WORKSPACE` and will throw a `toolchain cannot be in the WORKSPACE file` error. Therefore, the platform definition is reused in a macro that generates the actual toolchain targets and implementation, `bazel_toolchain_gen`

## Usage

Defining toolchains now only requires two files:

* a .bzl containing your platforms and the macro to be called from `WORKSPACE`
* a BUILD with the instantiation of the  `bazel_toolchain_gen` rule.

## Example

In the following, the `ytt` binary is downloaded for four separate platforms and is available via the 

```
toolchains = [
        "//ns/bazel_ytt/toolchain:toolchain_type",
]
```

addition to a rule declaration and 

```
  ytt = ctx.toolchains["//ns/bazel_ytt/toolchain:toolchain_type"].binary
```

in the rule implementation.

`//ns/bazel_ytt/toolchain/bazel_ytt.bzl`

```
load("//ns/bazel_toolchain_gen:toolchain.bzl", "bazel_toolchain_gen_register")

PLATFORMS = {
    "darwin_amd64": struct(
        urls = ["https://github.com/carvel-dev/ytt/releases/download/v0.44.3/ytt-darwin-amd64"],
        sha256 = "fb9cc00c4b6285e04595c493df73da425a2d5f9a551630e52559dd9ee2d58252",
        binary_name = "ytt",
        original_binary_name = "ytt_darwin_amd64",
        compatible_with = [
            "@platforms//os:osx",
            "@platforms//cpu:x86_64",
        ],
    ),
    "darwin_arm64": struct(
        urls = ["https://github.com/carvel-dev/ytt/releases/download/v0.44.3/ytt-darwin-arm64"],
        sha256 = "c8dbc767a7008c5097bfeefdfd2530bfc4a02bf10f5d0e522aed2e65baff5820",
        binary_name = "ytt",
        original_binary_name = "ytt_darwin_arm64",
        compatible_with = [
            "@platforms//os:osx",
            "@platforms//cpu:arm64",
        ],
    ),
    "linux_amd64": struct(
        urls = ["https://github.com/carvel-dev/ytt/releases/download/v0.44.3/ytt-linux-amd64"],
        sha256 = "c047bd7084beea2b4a585b13148d7c1084ee6c4aee8a68592fc8ed7d75ecebc5",
        binary_name = "ytt",
        original_binary_name = "ytt_linux_amd64",
        compatible_with = [
            "@platforms//os:linux",
            "@platforms//cpu:x86_64",
        ],
    ),
    "windows_amd64": struct(
        urls = ["https://github.com/carvel-dev/ytt/releases/download/v0.44.3/ytt-windows-amd64.exe"],
        sha256 = "f7a7e8ef12bda28402bfb2f37e0eaab2c06ec6345bb27bd6145496b8fd77e043",
        binary_name = "ytt.exe",
        original_binary_name = "ytt_windows_amd64.exe",
        compatible_with = [
            "@platforms//os:windows",
            "@platforms//cpu:x86_64",
        ],
    ),
}

# called from WORKSPACE
def register_ytt_toolchains():
    bazel_toolchain_gen_register(
        path = "//ns/bazel_ytt/toolchain",
        name = "bazel_ytt",
        platforms = PLATFORMS,
        is_archive = False,
    )

```

`//ns/bazel_ytt/toolchain/BUILD`

```
load("//ns/bazel_toolchain_gen:toolchain.bzl", "bazel_toolchain_gen")
load(":bazel_ytt.bzl", "PLATFORMS")

bazel_toolchain_gen(
    name = "bazel_ytt",
    platforms = PLATFORMS,
)
```

and an addition to `WORKSPACE`

```
load("//ns/bazel_ytt/toolchain:bazel_ytt.bzl", "register_ytt_toolchains")

register_ytt_toolchains()
```


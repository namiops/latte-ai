# Developing in Rust

Rust is supported in Bazel with the set of rules in the [rules_rust](https://github.com/bazelbuild/rules_rust)
repository.

## Creating new Projects Manually

New Rust projects can either be created and integrated with Bazel manually, or they can be generated from an OpenAPI
specification using Agoractl.  First we will look at manual creation, to understand the process better.

Begin by creating a new namespace directory under the "ns" directory of the mono repo and then create the new project
from within that namespace as you usually would using Cargo.

```shell
$ mkdir ns/my-namespace
$ cd ns/my-namespace
$ cargo new my-rust-app
warning: compiling this new package may not work due to invalid workspace configuration

current package believes it's in a workspace when it's not:
current:   /Users/colin.ward/Source/city/ns/my-namespace/my-rust-app/Cargo.toml
workspace: /Users/colin.ward/Source/city/Cargo.toml

this may be fixable by adding `ns/my-namespace/my-rust-app` to the `workspace.members` array of the manifest located at: /Users/colin.ward/city/Cargo.toml
Alternatively, to keep it out of the workspace, add the package to the `workspace.exclude` array, or add an empty `[workspace]` table to the package's manifest.
```

You can see from the above that we've hit a snag related to the monorepo.  The warning displayed above will turn into
an error if you try to run the project with "cargo run".  Edit the Cargo.toml file in the monorepo's root directory and
add the path to your new project under the "members = [" section.  Please add it in alphabetial order, as this helps
our automation tools to work better.

At this point, we can run the newly created Rust app:

```shell
$ cargo run
    Finished dev [unoptimized + debuginfo] target(s) in 0.32s
     Running `/Users/colin.ward/Source/city/target/debug/my-rust-app`
Hello, world!
```

Next, we want to integrate the application with Bazel.  The easiest way to do this is to copy the template BUILD file
included with this documentation into the app's directory and to edit it.  The file can be found [here](BUILD).  Rename
it from BUILD.template to just BUILD when copying it.

Replace all instances of {{ name }} with the name of your application, and {{ namespaceName }} with the name of your
namespace.  In the rust_binary target, the template uses the single Rust file src/main.rs, so update that list with your
own list of file, if necessary.

At this point, you can try to build and run this application, but you will get a long error message that ends with the
following lines:

```shell
$ bazel run //ns/my-namespace/my-rust-app:my-rust-app
...
ERROR: Error computing the main repository mapping: no such package '@crate_index//': Digests do not match: Digest("7a86798944472d3ed62299f367ad14efcf63dbcef132842c95d71faf69488140") != Digest("6ff9143d466e6b40ae131577e8c1079819a6e35af6fb29b4dbd91d0c4b5918fb")

The current `lockfile` is out of date for 'crate_index'. Please re-run bazel using `CARGO_BAZEL_REPIN=true` if this is expected and the lockfile should be updated.
```

So follow the instructions given here and run the command again with the CARGO_BAZEL_REPIN=true environment variable
set:

```shell
$ CARGO_BAZEL_REPIN=true bazel build //ns/my-namespace/my-rust-app:my-rust-app
...
Error: Some manifests are not being tracked. Please add the following labels to the `manifests` key: {
    "//ns/my-namespace/my-rust-app:Cargo.toml",
}
...
```

Oh dear, another long and complicated error!  You need to add your application's Cargo.toml file to the
crates_repository.bzl file that is in the root directory of the monorepo.  Add it under the "manifests = [" section
of the file and again, please add it in alphabetical order.

Finally now we can successfully build the app:

```shell
% CARGO_BAZEL_REPIN=true bazel build //ns/my-namespace/my-rust-app:my-rust-app
INFO: Analyzed target //ns/my-namespace/my-rust-app:my-rust-app (1 packages loaded, 2 targets configured).
INFO: Found 1 target...
Target //ns/my-namespace/my-rust-app:my-rust-app up-to-date:
  bazel-bin/ns/my-namespace/my-rust-app/my-rust-app
INFO: Elapsed time: 0.810s, Critical Path: 0.65s
INFO: 2 processes: 1 internal, 1 darwin-sandbox.
INFO: Build completed successfully, 2 total actions
```

Running with the CARGO_BAZEL_REPIN=true environment variable will be a bit slow (1+ minute) but it only needs to be
done once.  After this, we can build and run the application without it:

```shell
% bazel run //ns/my-namespace/my-rust-app:my-rust-app
INFO: Analyzed target //ns/my-namespace/my-rust-app:my-rust-app (1 packages loaded, 2 targets configured).
INFO: Found 1 target...
Target //ns/my-namespace/my-rust-app:my-rust-app up-to-date:
  bazel-bin/ns/my-namespace/my-rust-app/my-rust-app
INFO: Elapsed time: 5.973s, Critical Path: 0.01s
INFO: 1 process: 1 internal.
INFO: Build completed successfully, 1 total action
INFO: Running command line: bazel-bin/ns/my-namespace/my-rust-app/my-rust-app
Hello, world!
```

You can also build and run the application in a Docker image:

```shell
$ bazel run //ns/my-namespace/my-rust-app:image
INFO: Analyzed target //ns/my-namespace/my-rust-app:image (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //ns/my-namespace/my-rust-app:image up-to-date:
  bazel-out/k8-fastbuild-ST-5f994f96b8a3/bin/ns/my-namespace/my-rust-app/image-layer.tar
INFO: Elapsed time: 0.325s, Critical Path: 0.01s
INFO: 1 process: 1 internal.
INFO: Build completed successfully, 1 total action
INFO: Running command line: bazel-out/k8-fastbuild-ST-5f994f96b8a3/bin/ns/my-namespace/my-rust-app/image.executable
Loaded image ID: sha256:db877ef73b82e7e910c6b746bf46c245cf47ff24c4e46c8073f561d1bd6c2b6b
Tagging db877ef73b82e7e910c6b746bf46c245cf47ff24c4e46c8073f561d1bd6c2b6b as bazel/ns/my-namespace/my-rust-app:image
Hello, world!
```

Of course, if your project does not need to run in Docker (perhaps it is a client-side application that will not be
run inside of Agora) then you can simply remove the rust_image and agora_container_push targets.  Note that this will
not work on an arm64-based macOS, due to problems with Bazel's Rust support on that platform.

## Creating new Projects using Agoractl

When creating an application that will have server and client side components, it is good to specify its API using the
[OpenAPI specification](https://www.openapis.org/).  In fact, in Agora, we consider this best practice and encourge it.
To make the process of creating an Agora service from an OpenAPI spcification easier, we have created a plugin for the
[Agoractl](../../../ns/agoractl) utility.

By using this utility, all of the steps described above are automated, including editing the global Cargo.toml and
crates_repository.bzl files.  Here is an example of generating a service using an example OpenAPI specification
(clock.yaml) that we use for experimentation in Agora:

```shell
$ cd ns/agoractl
$ ./agoractl.py openapi clock clock-namespace ~/Source/city/ns/demo/clock/src/clock/openapi/clock.yaml rust-server
Generating code from OpenAPI specification file...

Adding new project to workspace crates_repository.bzl...
Adding new project to workspace Cargo.toml...
Updating generated project Cargo.toml to work with Bazel...
...
```

At this point, everything is ready to go**, although you do need to do the very first build with the
CARGO_BAZEL_REPIN=true environment variable set, as described above.

## Building and Testing

** Actually I lied about being ready to go.  For the clock example, you need to hack the crates_repository.bzl file
or you will get the following error:

```shell
$ CARGO_BAZEL_REPIN=true bazel build //ns/clock-namespace/clock:clock
ERROR: /private/var/tmp/_bazel_colin.ward/7961b46a9c75d2f86038abaeab7a69fd/external/crate_index__swagger-6.4.1/BUILD.bazel:20:13: Compiling Rust rlib swagger v6.4.1 (16 files) failed: (Exit 1): process_wrapper failed: error executing command (from target @crate_index__swagger-6.4.1//:swagger) bazel-out/darwin_arm64-opt-exec-2B5CBBC6/bin/external/rules_rust/util/process_wrapper/process_wrapper --arg-file ... (remaining 175 arguments skipped)

Use --sandbox_debug to see verbose messages from the sandbox and retain the sandbox build root for debugging
error[E0433]: failed to resolve: use of undeclared crate or module `hyper`
 --> external/crate_index__swagger-6.4.1/src/body.rs:3:5
```

This is due to a bug in Bazel's Rust support.  You need to add the following line inside the "annotations = {"
section of the crates_repository.bzl file (for the clock.yaml example file only):

```
"swagger": [crate.annotation(
	deps = [
		"@crate_index__hyper-0.14.27//:hyper",
	],
)],
```

Depending on what libraries your application depends on, you might hit this problem as well.  The problem has to do
with transient dependencies and the solution varies depending on the project and its dependencies.  Please ask for help
on the #wcm-cicd-support channel on Slack if you hit this problem and don't know what to add.

### Running single test-case

Usually, in order to run a single test-case from a target with Bazel, one would use the `--test_filter` flag, but this
*doesn't work* with Rust. Instead use:

```sh
bazel test <target> --test_output=all --test_arg=<test case name>
```

## Binaries

Because the monorepo uses a Rust _workspace_, the binaries can be found in the <repo_root>/target/debug and
<repo_root>/target/release directories, and not in a target directory under the application's source directory.

## Updating Dependencies

Any time that you add a new dependency to your application's Cargo.toml file, you need to run Bazel with the
CARGO_BAZEL_REPIN environment variable set to true:

```shell
$ CARGO_BAZEL_REPIN=true bazel build //ns/my-namespace/my-rust-app:my-rust-app
```

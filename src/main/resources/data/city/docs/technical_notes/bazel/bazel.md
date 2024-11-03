# Bazel

### Why Bazel?

Bazel is a popular build tool that works really well with a monorepo set up. In our use case,
we are looking to use multiple languages while normalizing the DevOps required to support all
these languages. Bazel allows us to do this with minimal overhead to developers. 

### Containerization

This monorepo uses [rules_oci](https://github.com/bazel-contrib/rules_oci/) to build docker
images and push to image registries. To learn more about how to build containers, please read our [docker development documentation](/docs/development/docker). 

### Remote Capabilities

Remote caching and remote execution are supported by the monorepo. These features provide rapid and consistent build times that can be shared across all users within the monorepo. To learn more about this, please read the [remote documentation](/docs/development/bazel/remote.md). 

### Bazel Setup For Each Language

#### Go

As stated in the [Go Guidelines](/docs/development/go/README.md#dependencies-and-gomod), all the dependencies
for Go will be managed in a singular, global `go.mod` file. This means that Bazel will only have to 
build from and manage a single source and version for each dependency. All of the deps are managed
through [gazelle](https://github.com/bazelbuild/bazel-gazelle). For more information on how to develop
with a global `go.mod` file, see the Go Guidelines.

Building go images are done through [rules_go](https://github.com/bazelbuild/rules_go) using the 
`go_library` and `go_binary` rules. 

#### Java

Please refer to the [Java Guidelines](/docs/development/java/README.md) for
information about our Java setup.

Like Go, Java is now able to have all deps managed through Gazelle for allowing
Java developers to work off of a global dependency file.

#### JavaScript

Please refer to
the [JavaScript Guidelines](/docs/development/javascript/README.md)
for information about our JavaScript setup.

#### Rust

Rust is supported via [rules_rust](https://github.com/bazelbuild/rules_rust).
Rust setup has the following rules set up:

* `all_crate_deps`
* `rust_image`
* `cargo_build_script`
* `rust_binary`
* `rust_clippy`

Rust crates are managed by a tool known
as [Crate Universe](https://bazelbuild.github.io/rules_rust/crate_universe.html)
that allows all Rust projects to build on a global `Cargo.lock` file.

#### Python

Please refer to the [Python Guidelines](/docs/development/python/README.md) for
information about our Python setup.

<!-- 
TODO: Add testing docs here
https://jira.tri-ad.tech/browse/WCMDO-62

TODO: When we add protobufs, also add docs here
https://jira.tri-ad.tech/browse/WCMDO-63
-->

### Developing With Bazel

See [Developing With Bazel](../../development/bazel/README.md).
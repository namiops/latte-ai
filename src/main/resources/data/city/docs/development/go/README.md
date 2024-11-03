# Developing in Golang

Golang has support through the [rules_go](https://github.com/bazelbuild/rules_go) repository. When it comes to
development, you can mostly operate as normal. While bazel manages the dependencies for all of our builds and tests, we
still use `go.mod` to allow an easier adoption for golang users. But it is still expected that developers
use `bazel build` and `bazel test` as the bazel build represents the single source of truth for how your project is
built. So while migration is made to be easier, try to familiarize yourself with the
[bazel](/docs/development/bazel/README.md) early on.

## Building and Testing

We use a tool called `gazelle` to help automate the creation and management of the BUILD files of golang projects. If
you haven't yet, please read about [Developing with Gazelle](/docs/development/gazelle/README.md). You should be able
to write golang as your normally would, but use gazelle to modify your build files, and bazel to build and test code.

If you need to manually adjust build or test targets in bazel, please read more about the options and rules available in
the [rules_go](https://github.com/bazelbuild/rules_go) repository.

## Binaries

With golang, `gazelle` will automatically create the binary targets for you. A binary is created in any `main package`
directory, that also has `func main` declared. If these conditions are met, then in the adjacent BUILD.bazel file, 
gazelle will create the `go_binary` target alongside a `go_library` target for the package.

## Updating Dependencies

As mentioned earlier, for golang we still keep the `go.mod` file for adoption to be easier. However, we can also use it
to easily update and manage our dependencies in golang for bazel. If your project needs a dependency which the monorepo
does not yet have for golang, or needs to update a dependency, you can use the following commands (be sure to use both!).

```shell
bazel run @go_sdk//:bin/go -- get -u <some_dependency>@<version>
bazel run //:go_mod_tidy
```

This will update the `go.mod` and `go.sum` file.

Why are we calling Bazel and not directly `go get`/`go mod tidy`? Using `bazel run` ensures that you're using the
exact same version of the go tooling that's used to build the code afterwards. That way, we avoid problems caused
by different tooling versions, which may be hard to diagnose with so many developers working in one repo!

Next, we need to update bazel to be aware of the new dependencies. Now we can run the bazel command:

```shell
bazel run //:gazelle_update_repos_go
```

This command will parse the `go.mod` and `go.sum` file, and update the [deps.bzl](/tools/languages/golang/deps.bzl) file
with the latest changes. Once this is done, you can import the new package from your code.

Once you've imported the package, use gazelle to update your BUILD file(s) accordingly:

```shell
bazel run //:gazelle
```

That's it, now you're ready to use the new dependency to develop your feature!

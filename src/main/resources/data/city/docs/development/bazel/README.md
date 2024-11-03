# Developing With Bazel

## Brief Understanding

### What is Bazel?

Bazel is a build tool. This means that for your code to be built and tested, you must tell Bazel what to do. These are
what the `BUILD.bazel` files are for that you will see throughout the repository. In these files are targets that can be
executed via the bazel CLI. For example, in a golang project you may see a file with something like:

```bazel title="projects/my-cool-app/cmd/server/BUILD.bazel"
load("//ns/bazel_oci/go:image.bzl", "go_image")
load("@io_bazel_rules_go//go:def.bzl", "go_binary", "go_library")

go_library(
    name = "server_lib",
    srcs = ["main.go"],
    importpath = "github.com/wp-wcm/city/projects/my-cool-app/cmd/server",
    visibility = ["//visibility:private"],
    deps = [
        "@com_github_labstack_echo_v4//:echo",
        "@com_github_labstack_echo_v4//middleware",
    ],
)

go_binary(
    name = "server",
    embed = [":server_lib"],
)
```

In here we have the load method calls, which are similar to imports in other languages. These load statements give us
access to the rules `go_library` and `go_binary`. When calling these rules, we must always provide a parameter
called `name`. These names are what we commonly refer to as bazel targets.

In this example, we can see that the `go_binary`'s `embed` parameter points to the target `:server_lib`. The `:` is to
indicate the following letters are a target name. And because there is nothing before the `:`, the location is within
the same file. So in this example, the `go_binary` rule named `server` is embedding the output of the `go_library` rule
name `server_lib`.

### Targets

Targets can also be used to build, test or run specific rules from bazel's CLI. As denoted in the example file, we know
the file is located at `projects/my-cool-app/cmd/server/BUILD.bazel`. This gives us all the information we need to
specifically build, test, or run this target.

When we want to point to a specific target we typically use the full syntax: `//<path_to_build_file:<target_name>`. To
break down meaning of this syntax we have:

* `//` is for the project's root
* `<path_to_build_file>` is the directory path from the project's root to the BUILD.bazel file
  * e.g. `projects/my-cool-app/cmd/server`
* `:` is to denote the next characters is the target name
* `<target_name>` is the value of the `name` parameter we provided in the rule invocation
  * e.g. `server`
* And lastly, there is a way to build or test everything by using the following instead of a specific target: `//...`. The triple dot is indicating to bazel to select all targets in the current directory and all subdirectories. Remember this is only for build and test. If you want to run a target, bazel only allows running one target at a time.

So, if we want to build the binary for my-cool-app server, we can run:

```shell linenums="0"
bazel build //helloworld/cmd:my_binary
```

Or, if we want to run the binary, we can do:

```shell linenums="0"
bazel run //helloworld/cmd:my_binary
```

**Note**: Read more about targets on [Bazel's official documentation](https://docs.bazel.build/versions/main/build-ref.html#targets).

### Testing

There is also a `bazel test` command that works against test targets. Test targets are commonly built from rules which
have the `_test` suffix. For example, `go_test(name = "my_test", ...)`.

## Installing Bazel

As recommended by the [official Bazel documentation](https://docs.bazel.build/versions/main/install.html), we use Bazelisk (a user-friendly Bazel tool) to install and run Bazel. It automatically picks the right version of Bazel for the repo, so you don't have to worry about manually updating Bazel on your machine.

First, check if Bazel is pre-installed by running `which bazel`. If this doesn’t print anything, you’re good, please follow the [official Bazelisk instructions](https://github.com/bazelbuild/bazelisk/blob/master/README.md) to install it.
If there was a printout similar to `/usr/bin/bazel`, please uninstall Bazel first (the process will vary per operating system) and then proceed to install Bazelisk as per the link above.

## Enabling Remote Cache and Execution

To enable remote caching, and other remote capabilities, please read the [Bazel Remote](./remote.md) documentation.

## Set up access to Artifactory

Note: This is *required* in order to be able to build with Bazel!

* Generate token and API Key:
  * Go to <https://artifactory-ha.tri-ad.tech/> in your browser
  * Log in from the top right login button if you haven’t
  * From the top right dropdown, “Edit Profile”
  * Click “Generate an Identity Token”
  * A window will appear containing the Token ID and Reference Token. You can copy the reference token using the copy button, and paste it into the file below.
* Create the file with the following content:
* Repeat the steps for <https://artifactory.stargate.toyota/>

```plain title="~/.netrc"
machine artifactory-ha.tri-ad.tech
login <YOUR_NAME>@woven-planet.global
password <reference_token_from_above>

machine docker.artifactory-ha.tri-ad.tech
login <YOUR_NAME>@woven-planet.global
password <reference_token_from_above>

machine artifactory.stargate.toyota
login <YOUR_NAME>@woven-planet.global
password <reference_token_from_above>

machine jp1-artifactory.stargate.toyota
login <YOUR_NAME>@woven-planet.global
password <use the same token for artifactory.stargate.toyota>
```

If you want to push images to Artifactory directly from your build system, you also have to login to Docker using the
same E-Mail and token mentioned above:

```shell linenums="0"
docker login docker.artifactory-ha.tri-ad.tech
docker login jp1-artifactory.stargate.toyota
docker login artifactory.stargate.toyota
```

Further information can be found on the Artifactory page [here](https://docs.woven-planet.tech/engineering_software/artifactory/support/Docker-Registry/).

## Optional step: Authenticate to private npm registry

If you are working on npm ecosystem, you also need to authenticate to Stargate npm registry.

1. Go to <https://artifactory-ha.tmc-stargate.com/ui/> and log in if necessary. On the top right drop-down menu, select `Set me up`, then NPM, and finally click the `Generate Token & Create Instructions` button; this will give you the `YOUR_API_KEY` needed for the next step.
1. Run the following command using the key that was just obtained:

    ```sh linenums="0"
    curl -su your.name@woven-planet.global:YOUR_API_KEY https://artifactory-ha.tmc-stargate.com/artifactory/api/npm/auth 2>/dev/null | grep '_auth =' | sed -e 's/.*_auth =\s*//' -e 's/ //' | pbcopy
    ```

    After running the command the auth string should be in your clipboard, so you can just paste it into your config file directly

1. For your global config at `~/.npmrc` please set it up the following way. Replace `YOUR_AUTH_STRING` with the string that was just copied to your clipboard:

    ```plain title="~/.npmrc"
    always-auth = true
    email = your.name@woven-planet.global

    //artifactory-ha.tmc-stargate.com/artifactory/api/npm/:_auth = YOUR_AUTH_STRING
    ```

If you get a 401 error related to Docker artifacts, please add the following flag, `--experimental_downloader_config=`. e.g.:

```sh
bazel build //ns/... --experimental_downloader_config=
```

## Setting Up Your Project with Bazel

Now that we generally understand bazel, and how to use it, we need to understand how it interfaces with the languages we
write our code in. Bazel is a language agnostic build tool. Instead of having language specific capabilities, the
community manages and creates bazel `rules` that tell bazel how a language should be compiled, tested, or ran.

Also, rules are for more than just languages. Rules can be used for anything! For example, we use docker rules to manage
the creation of containerized images, and pushing them to our registries.

*Note: For rules to be available, the monorepo must be updated to provide the rules required for your needs. If you have
a particular build, test, or other requirement that isn't supported by an existing rule, please reach out to the
monorepo maintainers to add support!*

When it comes to writing your code, please check out the [language support guides](#language-support) to understand how
you can interface with bazel to get your builds and tests running. For various tooling support, check out
the [tooling support guides](#tooling-support).

### IDE Support

While not required, here are some tools to get better support wit bazel in your development environment.

#### Jetbrains IDE (IntelliJ, Goland, Webstorm, etc.)

It is recommended to use the bazel plugin for JetBrains IDEs. To have your project indexed as a bazel project, you must
use the `Import Bazel Project` after the plugin is installed. This is managed by the
[bazel plugin](https://github.com/bazelbuild/intellij) which is maintained by both google and the community.

#### VSCode

There is an official plugin for bazel that can be found [here](https://github.com/bazelbuild/vscode-bazel). However, the
extent of its capabilities have not been tested within this monorepo.

Some languages might require language specific tooling, such as golang. See the section below for more details.

More details coming soon...
<!-- TODO(WCMDO-67): research and detail VScode usage with bazel -->

#### Other

If using another tool for code editing, such as vim, you can use language specific tooling for better integration wtih
bazel. For golang, there is a go package driver that can be configured using
the [rules_go instructions](https://github.com/bazelbuild/rules_go/wiki/Editor-setup).

More details may be added as other tools are discovered...
<!-- TODO(WCMDO-67): research and detail other usages with bazel -->

### Language Support

To learn about the support for various languages and how to work with them, please check out their respective documents:

* [Golang](./../go/README.md)
* [Java](./../java/README.md)
* [JavaScript](./../javascript/README.md)
* [Kotlin](./../kotlin/README.md)
* [Python](./../python/README.md)
* [Rust](./../rust/README.md)

#### How to update specific Go module versions with Bazel?

1. Change module version in ns/go.mod.
2. Run `GO_BAZEL_REPIN=1 bazel build //ns/brr/...` (just randomly picked BRR, no
   specific reason).

Currently, we recommend this way since other ways might break dependencies
weirdly.

### Tooling Support

* [Docker](./../docker/README.md)

### Further system dependencies

Bazel is a hermetic tool, meaning that it can function mostly without external dependencies. However, this is not currently 100% the case, so you need to install a couple of dependencies:

#### For Linux

Currently we rely on the system's cc compiler. You will likely need to run:

```shell linenums="0"
sudo apt install build-essential # For GCC
```

#### For Mac

Currently we rely on xcode to build certain cc projects. Please make sure that xcode app has been installed before proceeding the following instructions. Not every project relies on compiling cc, but gazelle projects, which rely on proto tools, also require xcode app and won't work with xcode command line tools. If your project does, you can run the following commands:

```shell linenums="0"
sudo xcode-select --install
```

```shell linenums="0"
sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
```

After installing xcode, you may need to run this sync command to have bazel refresh itself with your local installation.

```shell linenums="0"
bazel sync --configure
```

## Linting Bazel Files

Similar to other languages, and tools, bazel has linting capabilities for its files. To lint bazel files we use a tool
called [buildifier](https://github.com/bazelbuild/buildtools/blob/master/buildifier/README.md). This tool is managed
directly by our bazel project, and there is no need to install it yourself. The majority of issues are capable of being
auto fixed, and to perform the auto-fixes you can run:

```shell linenums="0"
bazel run //:buildifier
```

This command will build the lint tool and run it with the auto-fix settings. Afte completion it should have auto-updated
any bazel files that had lint issues it could automatically fix.

To check if your changes to bazel files have any remaining lint issues that need manual adjustment, you can run the
following to report the lint issues:

```shell linenums="0"
bazel run //:buildifier_check
```

This is also ran in the CI pipelines when you modify any bazel files. If the CI workflow fails, use these commands to
resolve any lint issues!

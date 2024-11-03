# Developing with Gazelle

Certain languages utilize a tool called [gazelle](https://github.com/bazelbuild/bazel-gazelle), to help simplify the
workflow of using bazel with these languages. This tool will read the source files, to generate and update build files
to create the proper library and binary targets.

Currently, our repository supports the languages:

* golang
    * native support
    * [documentation](https://github.com/bazelbuild/bazel-gazelle)
* protocol buffers
    * native support
    * [documentation](https://github.com/bazelbuild/bazel-gazelle)
* python
    * plugin support
    * [documentation](https://github.com/bazelbuild/rules_python/tree/main/gazelle)

If you have another language which you would like support for, please reach out to the maintainers of this repository! A
list of commonly supported languages can be
found [here](https://github.com/bazelbuild/bazel-gazelle#supported-languages).

## Building & Testing

When working with the supported languages, you can just add, remove, or change language files as you need. However, to
be able to build your project, bazel will need instructions on how to do so. This is where gazelle will be helpful. If
you have done the following, you likely need to run gazelle:

* add or remove files
* add or remove imports
* modify gazelle directives
* ...and more!

If you are unsure if your change requires a BUILD file update, then it is always safe and okay to run gazelle. To run
gazelle use the following command:

```shell
bazel run //:gazelle
```

This command will generate and update all of your `BUILD.bazel` based on your code changes. For example, if new imports
were added, this will add them to your target's `deps` parameter. If imports were removed, it will remove them from the
target's `deps` parameter.

*Note: These `BUILD.bazel` files and targets are required for the CI/CD pipelines to be able to build and test your
code. Without them, your code will may not succeed to build, or may not be built nor tested at all!*

When you are ready to build or test your changes, and you have run the above command, you can now build and test your
code using bazel. To do so, it is recommended not to target specific targets, but instead just always build your whole
project. This is because bazel's caching is incredibly efficient and safe. If your project is found in the
directory `projects/my-cool-app`, then you can always just run either of the following:

```shell
bazel build //projects/my-cool-app/...
bazel test //projects/my-cool-app/...
```

## Creating Binaries

Gazelle will not only create library and test targets, but also runnable binary targets for each language. How a binary
is chosen to be created varies from language to language. To know more you must read your desired language's
documentation:

* [java](/docs/development/java/README.md#binaries)
* [python](/docs/development/python/README.md#binaries)
* [go](/docs/development/go/README.md#binaries)

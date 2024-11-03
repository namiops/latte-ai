# Developing with Docker (and OCI images)

The monorepo uses [OCI (Open Container Initiative)](https://opencontainers.org/) to create container images. This is a more general format than the classic docker container format, and is supported by a wider array of tools. Details about the OCI image spec, can be read on their [official github](https://github.com/opencontainers/image-spec).

To support this, we leverage the open-source [`rules_oci`](https://github.com/bazel-contrib/rules_oci/) to create our images. We provide documentation for our internal rules and macros for the images, but for any core rules and macros that are a part of `rules_oci` itself, please refer to the respective documentation, especially if you are doing more complex builds.

Building OCI images with [`rules_oci`](https://github.com/bazel-contrib/rules_oci/) differs from the standard Dockerfile approach in that we don't run commands within the image. Instead, we do all our build commands separately, then group the desired files into a `tar`. Each `tar` represents a layer on the final OCI image.

For extended support, our own extension of `rules_oci` can be found in the [`bazel_oci`](/ns/bazel_oci) directory. It does not contain any markdown files, but all Bazel macros are thoroughly documented. Please read the documentation on the macro that you plan to use.

## Outline

This document covers the following:

* [**1. Creating an image**](#1-creating-an-image)
    * [**a. Building a language image**](#a-building-a-language-image)
    * [**b. Building a generic OCI image**](#b-building-a-generic-oci-image)
    * [**c. Adding files to your image**](#c-adding-files-to-your-image)
* [**2. Running your image locally**](#2-running-your-image-locally)
* [**3. Pushing your image to the Artifactory**](#3-pushing-your-image-to-artifactory)

## Process

### 1. Creating an image

There are two options for creating an image. 

* **a.** Creating an image from a `<lang>_binary`: e.g. `go_binary`, `java_binary`, etc.
  * This is the most common option and is often used to create a program (such as a microservice) and run it in environments like the Agora cluster(s).
* **b.** Building an image from a custom base for generic purposes 
  * An example would be a third party image, that allows providing special plugins in a particular directory, e.g. flink

#### a. Building a language image

After creating a runnable binary for your program, we can create a container for it. This is often referred to as a `<lang>_image` target. Several languages are supported, and you can find all actively available languages in our internal project [`bazel_oci`](/ns/bazel_oci).

> [!Note] 
> This guide only covers how to create images. If you are just starting with a language, please check out the corresponding development guide in the [development directory](/docs/development).*

Initial language images:

* [go_image](/ns/bazel_oci/go/image.bzl)
* [java_image](/ns/bazel_oci/java/image.bzl)
* [js_image](/ns/bazel_oci/js/image.bzl)
* [python_image](/ns/bazel_oci/python/image.bzl)
* [rust_image](/ns/bazel_oci/rust/image.bzl)

All language macros have near-identical attributes for consumption. For simplicity, the example below uses `go_image`, but please verify the in-line documentation for your desired language macro in [`bazel_oci`](/ns/bazel_oci).

**Adding the image target**

> [!Note]
> Before creating an image, you must have created your program and be building it with bazel! If you have not yet done this, be sure to read any of the [language guides in the development directory](/docs/development).

You should already have a `<lang>_binary` target in a `BUILD` file of your project. To create an image, we need to add a `<lang>_image` that matches the name of this target. You must always provide the name of the binary target to the `binary` attribute of the `<lang>_image` target. These language image macros mostly take in the same attributes as [`rules_oci#oci_image`](https://github.com/bazel-contrib/rules_oci/blob/main/docs/image.md#oci_image_rule), with some exceptions such as `base` and `entrypoint`. 

For the additional attributes provided in your language of choice, read the inline documentation at [`bazel_oci`](/ns/bazel_oci).

##### Important Attributes

* `run_as_root`
    * Defaults to `False`. 
    * Options:
      * `True`: Your image will contain only a `root` user and use it.
      * `False`: Your image will contain a `nonroot` user and use it.
    * It is required for security purposes to migrate all images off of `run_as_root`. Because of this, you should avoid using this attribute and setting to true unless absolutely required.
* `debian12`
    * Defaults to `True`.
    * Options:
      * `True`: Your image base will be `debian12`.
      * `False`: Your image base will be `debian11`.
    * It is required for security purposes that all teams update to Debian 12 images, as they come with many security updates for core system libraries. 
    * If you encounter a breaking/compatibility issue with your binary, set this attribute to `False`, upgrade to Debian 12 at a later date, and consult with the security team.
* `iqqq`
    * Defaults to `None`.
    * This is used to provide a static binary that can be used to call istio's quitquitquit endpoint. Before this option, teams were often making custom images to use curl or wget to call quitquitquit. With this option, that is no longer necessary, and you should no longer need to provide a custom base.
    * Options:
        * `IQQQ_WRAP`: Wraps the execution of your binary with a program to call `quitquitquit` for Istio's sidecar.
        * `IQQQ_ADD`: Adds the `iqqq` binary to your image to be executed however you want.
    * For full details, see [`istio_quitquitquit`](/ns/istio_quitquitquit/README.md#how-to-use).

**Example usage**

The example below uses `go_image`, but all `<lang>_image` targets should be used in the same way. 

First, we need a `go_binary` target. In Golang, this would be defined in a `BUILD` file located in `projects/my-cool-app/cmd/server/BUILD`. The file contents would be something like:

```bazel
load("@io_bazel_rules_go//go:def.bzl", "go_binary", "go_library")

go_library(
    name = "server_lib",
    srcs = ["main.go"],
    importpath = "github.com/wp-wcm/city/projects/helloworld/go/cmd/server",
    visibility = ["//visibility:private"],
    deps = [
        "@com_github_labstack_echo_v4//:echo",
        "@com_github_labstack_echo_v4//middleware",
    ],
)

go_binary(
    name = "server",
    embed = [":server_lib"],
    visibility = ["//visibility:public"],
)
```

Note the `go_binary` with the name `server` in the above example. This means we can reference this binary with the Bazel label `//projects/my-cool-app/cmd/server:server`.

To create a `go_image`, we can update an existing `BUILD` file or make a new one. If we want to define our image at the project root, we could create (or update) `projects/my-cool-app/BUILD`. We will need to add our image definition to this file as follows:

```bazel
load("//ns/bazel_oci/go:image.bzl", "go_image")

go_image(
    name = "server_image",
    binary = "//projects/my-cool-app/cmd/server:server",
)
```

> [!Note]
> The `go_image` can be added adjacent to the `go_binary` in the same `BUILD` file. It is up to you how you wish to structure your code and targets. To see examples and recommendations for the general structure, refer to the [helloworld projects](/projects/helloworld) for each language.*

After adding your image, you should now be able to build the OCI image. In this example, that can be done using `bazel build //projects/my-cool-app:server_image`. For details on running your image locally, see [2. Running your image locally](#2-running-your-image-locally).

##### What's the base image?

All our `<lang_image>` macros use a [distroless image](https://github.com/GoogleContainerTools/distroless), which is ultra-compact and minimalistic. The main benefits of this kind of image are:

* **Reduced size**
    * The smallest distroless image is around 2MiB, which is half the size of an Alpine image (~5MiB) and ~1% that of a Debian image (124 MiB)
* **Limited capabilities**
    * The image contains only what is needed: the main application itself and its runtime dependencies.
    * This reduces the scope and prevents misuse of your image, as usually the only executable is your application.
* **Improved security**
    * Reducing what is in the image in turn reduces surface area for vulnerabilities in dependencies.
    * Having no shell or other executables reduces surface area for execution attacks on your image.

However, distroless images can be more painful to debug as they lack common tools like a `shell` or `wget`. To address this problem, we provide the option to build your image with a "debug" distroless image, which includes tools like [busybox](https://busybox.net/). To learn more about this, see [Debugging your image](#debugging-your-image). 

#### b. Building a generic OCI image

Generally, we recommend _always_ using a `<lang>_binary` target with a `<lang>_image` to create your images. However, there are exceptions, such as:

* Executor images that are used to run arbitrary code or scripts and don't serve a singular purpose
  * e.g. Testkube executors that are used on a wide-range of custom test scripts
* Extending an existing third-party image that allows special code to be added to a plugin directory for extended capabilities.
  * e.g. flink images require using a flink base image, and building a special plugin directory

To create a generic OCI image, we need to use an `oci_image` as our target. We can assign a `base`, and then all the additional files that we want to add to the image should be added in the `tars` attribute. To learn more about adding files to your image, see [c. Adding files to your image](#c-adding-files-to-your-image).

When converting a Dockerfile to a `BUILD` file, we need to break apart "build" actions from "bundling" actions. A line in a Dockerfile used to download a file should be added to the `WORKSPACE` as a properly managed dependency. A line used to build an artifact should be its own target in the `BUILD` file. Lastly, we create tars when copying files to the final image and add them as layers to the `oci_image` target.

The example below creates a Flink image to execute a Python project. First, we create a Python library by adding a `py_library` target that includes our sources. Next, we package our library into a `tar` using `pkg_tar`. Lastly, we create an `oci_image` with the `flink` base and add our `tar` to the `tars` attribute, as shown in the code here: 

```bazel
load("@rules_pkg//pkg:tar.bzl", "pkg_tar")
load("@rules_python//python:defs.bzl", "py_library")
load("//ns/bazel_oci:city.bzl", "city_oci_push", "oci_image")

py_library(
    name = "poc",
    srcs = ["python_demo.py"],
    imports = ["../../.."],
    visibility = ["//:__subpackages__"],
)

pkg_tar(
    name = "demo_tar",
    srcs = [":poc"],
    mode = "0555", # If copying this example, be sure to set mode based on your needs!
    package_dir = "/opt/flink/usrlib",
)

oci_image(
    name = "image",
    base = "@agora_flink_image",
    tars = [":demo_tar"],
)
```

#### c. Adding files to your image

`oci_image` and all `<lang>_image` macros support the `tars` attribute. Under-the-hood `<lang>_image` macros add all the required files and artifacts to run your `<lang>_binary` target to the image. However, you may have additional files that you want to include in the final image, such as configuration files. In any case, you always use the `tars` attribute to add files to your image.

`tars` is an array of tar files, where each tar represents a layer to the image. The first entries in the `tars` array are the lower level layers. If these change, then all the higher level layers need to be re-built. To improve the caching performance of your image, the most frequently updated layers should be added last in the array. It is up to your team to create one or more tars for your files to optimize the layer performance.

When creating tars, we recommend using [`rules_pkg`](https://github.com/bazelbuild/rules_pkg/blob/main/docs/latest.md) to collect the output of your targets and create directory structures or symlinks. Below is an example of how to create such a tar, which can be added to the `tars` attribute of an image macro.

```bazel
pkg_files(
    name = "istio_quitquitquit_files",
    srcs = ["//ns/istio_quitquitquit/cmd/istio_quitquitquit"],
    attributes = pkg_attributes(mode = "0o555"),
    prefix = "/usr/bin",
    strip_prefix = strip_prefix.files_only(),
    visibility = ["//visibility:private"],
)

pkg_mklink(
    name = "istio_quitquitquit_ln",
    attributes = pkg_attributes(mode = "0o555"),
    link_name = "/usr/bin/iqqq",
    target = "/usr/bin/istio_quitquitquit",
    visibility = ["//visibility:private"],
)

pkg_tar(
    name = "istio_quitquitquit",
    srcs = [
        ":istio_quitquitquit_files",
        ":istio_quitquitquit_ln",
    ],
    visibility = ["//visibility:public"],
)
```

In this example, `pkg_files` is used to place the `go_binary` in the `/usr/bin/` directory with the permissions `0o555`. Another target using `pkg_mklink` creates a symlink of the binary to be added, providing an abbreviated name of our binary for simplicity. Finally, `pkg_tar` is used to group both `pkg_files` and `pkg_mklink`, as specified in the `srcs` attribute. Now, this `pkg_tar` target called `istio_quitquitquit` can be added to any image macros `tars` attribute. Doing so creates a layer that adds the file `/usr/bin/istio_quitquitquit` and the symlink `/usr/bin/iqqq` to the image.

### 2. Running your image locally

Running an image is different from running your binary directly: we provide a helper target that loads the OCI image into your Docker or Podman daemon. The subsequent process is the same as running any other container image.

Continuing with the Go example from above, we use the following command to run the `go_binary` directly:

```shell
bazel run //projects/my-cool-app/cmd/server
```

To use the image instead, you can run the helper target instead. It has the same name as our `go_image`, but with the suffix `.load`. When running this helper target, your image will be loaded to your container tool with the same path as your image.
In our example, we can load the image like this:

```shell
bazel run //projects/my-cool-app:server_image.load
```

In this example, the loaded image would be tagged as `projects/my-cool-app:server_image`. This tag makes script writing easier, as it has the same value as the Bazel label (excluding the Bazel prefix `//`).

Now that the image is loaded, we can run our image with our container tool. For this example, we are using Docker:

```shell
docker run --rm -it -p 8082:8082 projects/my-cool-app:server_image
```

For more information on how to run images, see either the [Docker](https://docs.docker.com/engine/reference/commandline/run/) or the [Podman](https://docs.podman.io/en/stable/markdown/podman-run.1.html) documentation.

#### Debugging your image

We use [distroless](#whats-the-base-image) images with `<lang>_image` macros. These do not have tools such as shell and `ls`, which makes debugging the image difficult. To address this problem, we can build your image using a `debug` variant of the distroless image. The variant contains [BusyBox](https://busybox.net/) and other system libraries for debugging. To use this variant, we need to add a special flag when building our image.

When building or running our target, we can add the `--distroless_debug` flag to enable image debugging. Alternatively, you can compile your code in debug mode (i.e. `-c dbg`), which also enables image debugging. The following commands use the same example as above, but with the distroless `debug` flag enabled. After loading this variant, we can properly access the shell and other tools.

```shell
bazel run --distroless_debug //projects/my-cool-app:server_image.load
```

```shell
docker run --rm -it -p 8082:8082 --entrypoint sh projects/my-cool-app:server_image
```

### 3. Pushing your image to Artifactory

Once we have set up our image, we need to add a target to push it to the CI/CD pipelines. Without this, the image will NOT get pushed. This is done by adding a `city_oci_push` target in a `BUILD` file, typically the one containing the image. This macro contains logic to simplify the process of pushing to the city-internal Artifactory.

Let's revisit the Go example from the [Building a language image](#a-building-a-language-image) section above. In your `projects/my-cool-app/BUILD` file, add the load statement for `city_oci_push` at the top of the file. Next, define your target and provide the label of your image target to the `image` attribute. You also need to define the `repository` to which the image will be pushed.

(As always, please see the [helloworld projects](/projects/helloworld) for live examples per language.)

```bazel
load("//ns/bazel_oci:city.bzl", "city_oci_push")

city_oci_push(
    name = "push_server_image",
    image = ":server_image",
    repository = "my-cool-app/server",
)
```

##### Important Attributes

* `legacy_prefix`
    * Supports the legacy Artifactory structure that divides the service teams and the Agora platform.
    * Defaults to `True`.
    * If `True`, one of the prefixes below will be automatically prepended to your repository based on the path of your `BUILD` file.
        * Under the `/ns/` directory: `wcm-cityos`
        * Under any other directory: `wcm-backend`
    * You can disable the prefix by setting this attribute to `False`.

> [!Note]
> The `repository` value is the name of your image in the registry. It MUST be unique to other push targets!*

#### a. Pushing in CI/CD

By adding the `city_oci_push` target to a `BUILD` file, the targeted image will automatically be pushed when the continuous delivery pipelines push images. Currently, this occurs when changes to the image are merged to the default branch, `main`. Remember, the `BUILD` file is how you connect your code to the CI/CD pipelines!

This image push only occurs if your image was detected to have any changes, to understand more about this, you should read the section [Why was my Image Pushed](#why-was-my-image-pushed). The `main` push is considered the "official" release for your image. The repository and tag, should be matched by the [Continuous Deployment](../flux) tooling to use your released image in the agora clusters. 

The tag of your image will use the monorepo's default if you do not provide a custom tag. The default tag pattern is set to: `main-<commit>-<commit-time>`.

`<commit>` corresponds to the abbreviated commit SHA-1 hash and `<commit-time>` corresponds to the [Unix time](https://en.wikipedia.org/wiki/Unix_time) of the commit. By using the commit, it is easy to see which commit pushed the image. The commit time allows tools such as Flux to sort and find the latest images.

To verify the pushing of your image, you can view the pipeline on main's commit! The title of the action will be: `Continuous Delivery / commit (<commit>)`. It is possible your commit was part of a merge group. In this action you can verify the execution of pushing your image, and the latest tag it produced.

> [!Note]
> You can disable your image from pushing by adding the tag `manual` to the push target. This prevents the push from running when your image is updated in main. Even with the manual tag, you can still use [Deployment Preview](#deployment-preview) or [Pull Request Push](#pull-request-push).
>
> ```bazel
> city_oci_push(
>   name = "push",
>   image = ":image",
>   repository = "my/cool/image",
>   tags = ["manual"],
> )
> ```

##### Why was my Image Pushed?

Teams often wonder why their image was updated without directly modifying their code in the monorepo. This is because we try to adopt a centralized dependency approach for all our languages, as well as an explicit relationship between shared code. With Bazel, we can closely follow a modification to any direct or transitive dependency of your image. When any changes occur, they cause a modification to the files in your image, which triggers a new push.

Common reasons for updates to your image:

* Your code was directly modified
* A version update was made to a direct or transitive dependency
* A code update was made to a dependency in the monorepo
* The base container image was updated
* First commit adding the push target

##### Deployment Preview

The [Deployment Preview](/docs/development/deployment-preview/README.md) feature allows you to update your image and test it within the cluster before merging your pull request. As mentioned above, `city_oci_push` is a required input here, so please make sure that you have created this target correctly before using the feature.

##### Pull Request Push

If you need to push your image without creating a new deployment as done in [Deployment Preview](#deployment-preview), you can use a GitHub Action to push your image from your pull request as follows:

1. Go to the [Preview Delivery](https://github.com/wp-wcm/city/actions/workflows/preview_delivery.yaml) action tab.
2. Click `Run workflow`.
3. Select your branch and provide the label of the `city_oci_push` target, then hit `Run workflow`.

This will push your image, and the Actions logs will provide the tag to use. It is also a good way to verify that your push rule works when you first create it! Your image will be pushed under a different tag than what is used in the `main` branch push. This prevents conflicting with the [Continuous Deployment](../flux) system for images that are still in development.

#### b. Pushing locally

Currently, we do not encourage you to push your image manually. This is an advanced use case that may have unexpected results. Instead, please use either [Deployment Preview](#deployment-preview) or [Pull Request Push](#pull-request-push) if you need to push an image before merging your code to `main`.

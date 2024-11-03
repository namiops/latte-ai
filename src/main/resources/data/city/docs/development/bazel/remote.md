# Bazel Remote

From the beginning, bazel was designed to build not on a singular machine but
across a fleet of machines. This works off different remote API features:

* [build event streaming](#build-invocations-url)
* [remote caching](#remote-caching)
* [remote build execution (RBE)](#remote-execution)

This document won't describe in detail how these work with bazel, but will
describe how to utilize remote capabilities within our bazel setup.

!!! info "Optional: Additional Reading"
    If you wish to learn more about how these work in bazel, please read more at:

    * <https://bazel.build/remote/rbe>
    * <https://bazel.build/remote/caching>
    * <https://www.buildbuddy.io/blog/bazels-remote-caching-and-remote-execution-explained/>
    * <https://docs.engflow.com/re/index.html>

## Enable Remote Capabilities

To start using our remote services, you will need to enable your account and
download the credentials. We currently use a third-party solution called
[Engflow](https://www.engflow.com/product/remoteExecution) to provide our
remote cache and RBE service.

### Login

1. Visit our remote cluster's URL: <https://wavellite.cluster.engflow.com/>
2. Read and agree to the Privacy Policy
3. Click `Sign in with OpenId Connect`
    * You will need to authenticate with your Woven microsoft account

### Download Credentials

1. Go to the [Getting Started page](https://wavellite.cluster.engflow.com/gettingstarted)
2. Click the button: `GENERATE AND DOWNLOAD MTLS CERTIFICATE`
    * This will download a zip to your default download directory
3. Extract the certificates to: `$HOME/.config/engflow/`
4. Create and/or update your [user.bazelrc](/user.bazelrc) in the repository
   root directory to contain the below snippet:
    * Note: The `user.bazelrc` is a gitignored file!

```text
# Enable Remote Caching and BES
common --config=engflow

# Authenticate to the EngFlow cluster using mTLS certificate
common:engflow --tls_client_key=/home/<USERNAME>/.config/engflow/engflow.key
common:engflow --tls_client_certificate=/home/<USERNAME>/.config/engflow/engflow.crt
```

!!! warning

    * Replace `<USERNAME>` with your own username.
    * The path in this file MUST be absolute.
    * The path cannot contain environment variables, and cannot use `~`
    * If on native Windows, be sure to correct the path to the right syntax

## Build Invocations URL

* **Enabled by default**
* **Enabled in CI**

Once you have completed [Enable Remote Capabilities](#enable-remote-capabilities),
you should start to see a new output line indicating your build results are
being streamed. This line looks like:

```text
INFO: Streaming build results to: https://wavellite.cluster.engflow.com/invocation/f942dfca-a797-4d30-87f7-87bca5e9f4b8
```

When clicking this URL, it will take you to Engflow's UI for analyzing your
bazel build or test. This is a great place to review and verify what happened
during your build. You can view cache hits, individual test logs, build
failures, terminal output, and more. It is **strongly** recommend to share this
URL when working with your teammates or other teams about build or test issues.
This page provides a detailed view of what exactly occurred.

!!! warning
    The invocation URL will be **required** when asking @agora-build for support with a build and/or test issues! If not provided, they may ask for it before being able to provide additional support.

Engflow has this page, and their other pages, fully documented
[here](https://docs.engflow.com/web_ui/invocation_details/index.html). If you
wish to understand more about how to use this website, please review their
documentation!

!!! note
    All of our CI bazel build and tests have this feature enabled! When looking at the GitHub action log, the invocation URL can be used to see more details about your CI build on the invocation page. This page also has a link back to the GitHub Action that triggered this build!

## Remote Caching

* **Enabled by default**
* **Enabled in CI**

After setup, your build and tests should now start utilizing a shared remote
cache. Bazel's efficiency is based around its caching invalidation accuracy.
Before remote caching, developers were only relying on a local cache, but the
local cache has many limitation on the amount of data that can be stored. With
the remote cache you should see a lot more cache hits when swapping between
branches, projects, and even making local changes.

There is no actions that you need to take for understanding the remote cache
or managing it. The management of the remote cache is handled entirely by bazel
and the build team. If interested in how remote caching works, you can read more
at [bazel's documentation](https://bazel.build/remote/caching).

## Remote Execution

* **Disabled by default**
* **Enabled in CI**

One of the most important features is the ability to offload your build across
a fleet of machines. The idea is that the local bazel client analyzes the build
graph for the requested invocation, and uploads the build events to a remote
service, which then distributes every build/test action in parallel,
distributed across a dynamically scalable set of machines. Some core benefits
to this approach is:

* Less power needed on the main client machine
* Static/controllable resources per build/test action for consistent behavior
* Controlled environments for more increased hermeticity in builds
* Overall more computational power available to speed up large builds

If you wish to learn more, consider reading
[Engflow's documentation](https://docs.engflow.com/re/index.html).

### How to Enable

To enable RBE you can pass the flag `--config=rbe` when running bazel. If you
wish to enable it permanently then you can add the following to your
`user.bazelrc` file:

```text
build --config=rbe
```

!!! warning
    Currently it is only recommended to add this configuration to the user.bazel
    for **linux** users. At this time, **Windows** and **MacOS** users cannot use
    RBE.

#### MacOS and Windows

**Not Yet Supported**

!!! Note
    Windows with WSL works with RBE!

Currently, using RBE with Windows or MacOS is not supported. We have future
plans of supporting the use of RBE from MacOS. When support is added for MacOS
we will update this section, and announce how you can use RBE.

For Windows users we strongly recommend using
[Windows with WSL](https://learn.microsoft.com/en-us/windows/wsl/install).

**Why no Direct Support?**

Currently, there is no need to natively compile on MacOS or Windows for the
city monorepo. The few projects that do target these platforms in their
builds use golang which has great cross-compilation support. These platform
executors cost a large amount of money, relative to linux executors, and have
increased limitations for auto-scaling.

### Where is the build output?

By default, bazel is designed with RBE to use a feature called
[Build without the Bytes](https://blog.bazel.build/2023/10/06/bwob-in-bazel-7.html#what-is-build-without-the-bytes).
In short, this feature is designed to only upload and download essential files
to build the code. The majority of network requests will instead occur within
the remote server. The "Build without the Bytes" is an essential feature to
significantly reduce network usage with RBE.

The `bazel run` command is designed to work with this feature by default.
When you execute a `bazel run` command with RBE, everything will be built
remotely, and only the final binary and required run files will be downloaded
by the bazel client to your machine.

If you do need to inspect the outputs from `bazel build`, you can provide one
of the following flags when building:

Download all outputs of the specified bazel targets:

```text
--remote_download_toplevel
```

Download all outputs, including transitive targets:

```text
--remote_download_all   
```

!!! Note
    One example of needing `toplevel` would be when building a `pkg_tar` target,
    and wanting to verify the content of the tar. If you add
    `--remote_download_toplevel` to your build command, you can then inspect the
    downloaded tar's content after the build.

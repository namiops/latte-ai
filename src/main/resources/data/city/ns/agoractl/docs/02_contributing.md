# Contributing to Agoractl

This document is to help provide guidelines and requirements for contributing a
new plugin to Agoractl

## How Agoractl determines plugins

All plugins are required to extend the `AgoraCtl` abstract class that is provided in the agoractl_base directory.
Extending the abstract class is how Agoractl enforces a common interface that allows the main Agoractl application
to call plugins. It also allows plugins to reuse common functionality such as support for adding files to the local
git repo, creating GitHub pull requests and manipulating YAML files in a consistent way.

## Creating a new plugin for Agoractl

Agoractl provides a plugin that creates a skeleton framework for new
plugins. You can find more information about this plugin [here](plugins/05_agoractl_plugin.md).

Agoractl is arranged in the following way:

```
agoractl
|
|__agoractl_iota
|__agoractl_postgres
|__agoractl_plugin
|__agoractl_xxx
|
|__commander
|__zaml
```

`commander` and `zaml`are the two main umbrella applications that will call into the `agoractl_xxx` plugins.
Commander is a CLI application and Zaml (Zero YAML) is a web application that allows calling the plugins from a
web browser, without requiring a local monorepo checkout (if it is running inside Agora itself).

Initially, Agoractl did not have any strong rules for naming and plugin structure, but plugins have turned out to
be written very differently, causing some problems. The Agoractl team has thus come up with some recommendations for
writing plugins, to ensure they are more consistent.

When creating a new plugin, please use the `agoractl_plugin` to create the plugin skeleton. It provides a good basic
structure and layout for you to begin your work on top of:

```
agoractl
|
|__agroactl_xxx
|__|
|__|__ __init__.py
|__|__ __main__.py
|__|__ config.py
|__|__ plugin.py
|__|__ BUILD
```

Please follow this directory layout and ensure that you have an __init__.py file present (even if it is zero bytes
long) as Bazel requires this to work properly.  When adding a plugin, you will need to add Bazel BUILD files as
described in the [Agora Python Documentation](../../../docs/development/python/README.md). If you include new packages,
please add them to the global py_requirements.in file as described in the README file mentioned above. We also like to
add the packages to Agoractl's local [requirements.txt](../requirements.txt) file, to enable running Agoractl without
Bazel (although strictly speaking, this is not supported).

## Contributing Guidelines

Please freel free to write your own plugin! People usually write one of two types of plugin:

- Plugins that manipulate processes directly inside Agora, in a way similar to the Kubectl command.
- Plugins that create k8s manifests in order to perform an operation such as adding a service to Agora.

There are some dos and don'ts when writing a plugin.

- Do remember that Agoractl has two frontends - the CLI-based Agoractl and the web-based Zaml. When adding parameters
that are passed to your plugin, consider the user experience of how these might be passed to the plugin from both
Agoractl and Zaml.
- Do make plugins standalone. Plugin A *must* not be calling plugin B to do tasks on its behalf. Plugin B might not
even be present on all installations of Agoractl.
- Do use the abstractions of functionality that are provided in the base Agoractl class. These allow things such as
accessing git, creating Pull Requests, manipulating JSON, TOML and YAML files, editing the CODEOWNERS file and SSO
sign-ons to Keycloak. Come to DevRel if you are missing something - don't reinvent the wheel.
- Do not write your plugin in a language such as Rust and then wrap it in Python. It creates difficult to resolve Bazel
dependencies and you won't be able to use the support methods in the AgoraCtl base class. "But I like Rust/Go/C++/Java"
is *not* a sufficient excuse to do this, and you might be asked to rewrite the plugin.
- Do supply documentation with your plugin, in the [plugins](plugins) directory, but make the plugin mainly
self-documenting using the --help parameter. Run "bazel run //ns/agoractl -- service_manifests --help" for a good
example of a self-documenting plugin.

### Agoractl's Shared Responsibility Model (SRM)

The plugin authors shall be responsible for:

* Implementation of the AgoraCtl-derived plugin class
* Updates and bug fixes related directly to the plugin
* Documentation of the available commands and the tool itself

The main Agoractl authors shall be responsible for:

* Providing guidelines and rules for use and integration with Agoractl
* Annoying you at your desk when you don't follow the rules!
* Providing helper tools and functions
* Updating and enhancing the abstract class to allow new features that can be leveraged by plugins
* Roadmaps and feature documentation
* Strategy for plugin adoption and dependency updates for Agoractl
* Making a good-looking web UI for your plugin to run in the Zaml frontend
* General support and help for plugins and `agoractl` usage
* Announcements of any major decision points to affected plugin developers

If you find a problem with the Agoractl core code, please approach the DevRel team. Do not fix it yourself as in
the past such fixes by plugin authors have not taken into account all Agoractl/Zaml use cases and have caused
regressions.

### Things to do before moving submitting to git

When your plugin is ready, some things to make sure are working before adding it
to the main `commander` application are as follows:

* The plugin has been verified to work with our CI: in other words, you can
  build this with Bazel
* The plugin has been verified to run both with Bazel or Python
* The plugin has been documented to clarify its use, scope, and interface

You don't need to worry about adding your plugin to the `zaml` web UI. DevRel will do that for you, so please let us
know about the new plugin!

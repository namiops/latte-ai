# Agoractl Plugin

Plugin to generate new Agoractl plugins.

## Introduction

Many plugins are expected to be written for Agoractl, so it is useful to have a plugin to generate new plugins.  Then,
the plugin developer only needs to implement the contents of the `__init__()` and `run()` methods, in order to create a
new plugin.

## Usage

The plugin is trivial and currently requires only two arguments.  Here is an example of using the plugin to generate a
new plugin called "HelloWorld":

```shell
bazel run //ns/agoractl -- plugin --name hello_world --class_name HelloWorld
```

In this case, _hello_world_ is used for file and directory names, and _HelloWorld_ is used for the Python class that
represents the plugin.

## Arguments

The arguments are explained in detail in the plugin itself, so will not be repeated here.  To see this documentation,
use the following command:

```shell
bazel run //ns/agoractl -- plugin --help
```

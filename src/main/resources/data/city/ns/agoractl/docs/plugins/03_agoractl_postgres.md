# Agoractl Postgres

Plugin to generate Postgres clusters.

## Introduction

You can create a Postgres deployment to set up a PostgresSQL database to be used by your project.
This deployment sets up a basic configuration to help teams get started.

After running the plugin, the following files will be created:

* A `postgres-values.yaml` YTT schema file
* A Bazel `BUILD` file that sets up Postgres via Helm and Zebra

For more information about Agora's Postgres support, please refer to the [Developer Portal](https://developer.woven-city.toyota/docs/default/Component/postgres-service)

## Usage

### Pre-requisites

The tool is currently only available for use inside of **city monorepo and for
any projects working inside the city monorepo CI/CD only**. If your code is
deployed via tenancy in another monorepo this tool is not for you.

You need the following to operate the plugin:

* Bazel
    * Installation instructions are [**here**](https://bazel.build/start)

### Quickstart

```
$ bazel run //ns/agoractl -- postgres

Enter the name of the database: my-db
Enter the namespace to deploy the database: my-ns
Enter the desired location for the output. This should be relative to the 'city' repository root. Do not start with a '/': tmp
Making sure things are correct...

Generating Postgres Values YAML...

---
name: my-db

Done! You can find your file at /Users/joseph.orme/Workspaces/city/tmp/postgres-values.yaml

Generating your BUILD file...
Done! You can find your file at /Users/joseph.orme/Workspaces/city/tmp/BUILD

Making sure your file is formatted...

Adding your files to the code generation...

Process completed. From here, you can create a PR on this repository to generate the Postgres
  * Please make sure that you are on a branch that is not 'main' when you make the commit
  * Please make sure to follow your progress at https://github.com/wp-wcm/city
```

### Commands

The Postgres plugin has the following commands. This output is from
running `-h` or `--help`

```
usage: agoractl postgres [-h] [-env {dev,lab,lab2}] [-n NAME] [-ns NAMESPACE] [-o OUTPUT]

Agoractl Postgres
------
Plugin that helps with setting up a Postgres Cluster

This plugin builds using the 'postgrescluster_build' macro that generates files and manifests via Bazel. This plugin
currently allows for a good default cluster that provides access to Postgres for users. For more details about the macro
please refer to: https://github.com/wp-wcm/city/blob/main/ns/postgres-operator/bazel/postgrescluster_build.bzl

'postgrescluster_build' is backed by Agora's usage of 'helm_template' rule which executes a given Helm Chart for
provided values. For additional details on the Bazel rule please refer to: https://github.com/wp-wcm/city/blob/main/ns/bazel_helm/helm_template.bzl

The plugin currently deploys based on the defaults for the Agora Postgres Cluster Helm Chart. For more details on the
Chart and the defaults provided please refer to: https://github.com/wp-wcm/city/tree/main/infrastructure/helm/agora-postgres-cluster

options:
  -h, --help            show this help message and exit
  -env {dev,lab,lab2}, --environment {dev,lab,lab2}
                        the desired environment
  -n NAME, --name NAME  the database name
  -ns NAMESPACE, --namespace NAMESPACE
                        the namespace of the database
  -o OUTPUT, --output OUTPUT
                        output path to push files, this is from the root of the city repository
```

This binary is run via bazel you can run the plugin via the `commander`:

```shell
bazel run //ns/agoractl -- postgres <FLAGS>
```

### CLI Command Description

#### Optional Arguments

`--name NAME`

* The desired name of the database. Follows Kubernetes naming conventions for
  string fields (lower or upper case letters, plus hyphens or underscores, max
  63
  characters length)

`--namespace NAMESPACE`

* The desired namespace of the database. Follows Kubernetes naming conventions
  for string fields (lower or upper case letters, plus hyphens or underscores,
  max 63 characters length)

`--output OUTPUT`

* The desired path for output, the tool will write the files to the
  corresponding path. Paths are relative to the monorepo root directory (`~
  /city`), **do not start paths with a slash (`/`)**


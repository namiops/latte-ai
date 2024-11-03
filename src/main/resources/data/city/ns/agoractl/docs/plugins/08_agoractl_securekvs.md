# Agoractl Secure KVS

Plugin to generate Secure KVS CouchDB Database and setup access to it.

## Introduction

This plugin helps you create a database inside the Secure KVS CouchDB.
It also adds an Access Policy, which will allow you access to the created database.

After running the plugin, the following files will be created or edited:

* A `secure-kvs-values.yaml` YTT schema file
* A Bazel `BUILD` file that sets up CouchDB database via YTT and Zebra
* A `access-policies-values.yaml` file in the selected environment will be edited

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
$ bazel run //ns/agoractl -- securekvs
Enter the database name suffix. The final db name will be 'namespace_name': sample-db
Enter the namespace to deploy the database: your-namespace
Enter the desired environment [lab2]: lab2
Enter the desired location for the output. This should be relative to the 'city' repository root. Do not start with a '/': tmp
Enter the name of the Service Account you will use to access Secure KVS: my-service-account
Generating code...

Making sure things are correct...

Generating Values YAML...

#@data/values
---
name: sample-db
namespace: your-namespace
databaseName: "your-namespace_sample-db"

Done! You can find your file at /home/vilius/repos/city/tmp/secure-kvs-values.yaml

Generating your BUILD file...
Done! You can find your file at /home/vilius/repos/city/tmp/BUILD

Creating Access Policy...

Done!

Process completed. From here, you can create a PR on this repository to save the service

        * Please make sure that you are on a branch that is not 'main' when you make the commit

        * Please make sure to follow your progress at https://github.com/wp-wcm/city
```

### Commands

The Secure KVS plugin has the following commands. This output is from
running `-h` or `--help`

```
$ bazel run //ns/agoractl -- securekvs -h
...

options:
  -h, --help            show this help message and exit
  -env {lab2}, --environment {lab2}
                        the desired environment (for now only lab2)
  -n NAME, --name NAME  the database name suffix. The final name will be 'namespace_suffix'
  -ns NAMESPACE, --namespace NAMESPACE
                        the namespace of the database
  -o OUTPUT, --output OUTPUT
                        output path to push files, this is from the root of the city repository
  -s SERVICEACCOUNT, --serviceaccount SERVICEACCOUNT
                        the name of the service account to give access to
```

This binary is run via bazel you can run the plugin via the `commander`:

```shell
bazel run //ns/agoractl -- securekvs <FLAGS>
```

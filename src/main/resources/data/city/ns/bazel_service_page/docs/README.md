# Agora Service Page Generator

## Overview

A Service Page (SP) is a central location where relevant information about a
service can be documented.

For more details please see [TN-0386](http://go/tn-0386).

For an overview of all Service Pages currently existing, see
[the Service Page Index](../../service-page/docs/README.md).

## How to add a new Service Page

To add a new Service Page, do the following:

1. Create a directory for your new Service Page under
   [ns/service-page/docs](https://github.com/wp-wcm/city/tree/main/ns/service-page/docs).
   If you have multiple related services for which you want to add Service
   Pages, you can also create a subdirectory structure to group them together.

   For example, the Agora Identity team has a service page for Drako in
   [ns/service-page/docs/id/drako](https://github.com/wp-wcm/city/tree/main/ns/service-page/docs/id/drako)
   and one for Keycloak in
   [ns/service-page/docs/id/keycloak](https://github.com/wp-wcm/city/tree/main/ns/service-page/docs/id/keycloak).

2. Create a values file in the new directory to define the contents of the
   Service Page. The file can be a YAML file or a JSON file, and it is
   conventionally called `values.yaml` or `values.json`, respectively.

   For example, the values file for Drako is
   `ns/service-page/docs/id/drako/values.json`.

   You can use one of the existing files as a starting point (e.g.
   [Drako's file](https://github.com/wp-wcm/city/tree/main/ns/service-page/docs/id/drako/values.json)),
   or start from scratch if you prefer.

   The file will be validated against a schema later, and you can use the same
   schema for assistance if your editor supports it, see the section
   [Editing the Service Page values file](#editing-the-service-page-values-file)
   for more info.

3. Create a `BUILD` file inside the new directory
   (e.g. `ns/service-page/docs/id/drako/BUILD`), and add the following:

   ```bazel
   load("//ns/bazel_service_page:defs.bzl", "agora_service_page")
  
   agora_service_page(
       name = "page",
       copy_to_source = True,
       values_file = "values.json",   # or "values.yaml", respectively
       visibility = ["//visibility:public"],
   )
   ```

4. If you added additional subdirectories between `ns/service-page/docs` and
   your Service Page directory, create or update the `BUILD` file in each
   intermediate directory to aggregate the targets in its subdirectories.

   For example, for Drako and Keycloak, there is such a `BUILD` file at
   `ns/service-page/docs/id/BUILD`.

   This `BUILD` file must define a `filegroup` target with the name "pages", and
   its visibility set to public.
   The `srcs` argument for this target must list all relevant targets in
   _direct_ subdirectories. The relevant targets are individual Service Pages,
   by their `page` target as defined above, and other intermediate directories,
   by their `pages` target as we're defining here.

   - For example, for the Identity services, the intermediate file lists all the
      Service Pages in the immediate subdirectories like this:

      ```bazel
      filegroup(
         name = "pages",
         srcs = [
            # the "page" target of each Service Page:
            "//ns/service-page/docs/id/drako:page",
            "//ns/service-page/docs/id/keycloak:page",
            "//ns/service-page/docs/id/postgresql:page",
         ],
         visibility = ["//visibility:public"],
      )
      ```

   - As another example, assume we have a group of Service Pages with subgroups,
      with a directory structure like this:

      ```plain
      ns/service-page/docs/demo-group
      ├── BUILD
      ├── subgroup-1
      │   ├── BUILD
      │   └── demo-sp-A
      │       ├── BUILD
      │       └── values.json
      └── subgroup-2
          ├── BUILD
          ├── demo-sp-X
          │   ├── BUILD
          │   └── values.yaml
          └── demo-sp-Y
              ├── BUILD
              └── values.json
      ```

      In this case, the intermediate file `ns/service-page/docs/demo-group/BUILD`
      would list the intermediate targets below it like this:

      ```bazel
      filegroup(
         name = "pages",
         srcs = [
            # the "pages" target of each sub-group:
            "//ns/service-page/docs/demo-group/subgroup-1:pages",
            "//ns/service-page/docs/demo-group/subgroup-2:pages",
         ],
         visibility = ["//visibility:public"],
      )
      ```
  
5. If you added a new directory in `ns/service-page/docs`, update the
   `agora_service_page_index` target in
   [ns/service-page/docs/BUILD](https://github.com/wp-wcm/city/tree/main/ns/service-page/docs/BUILD).

   The `pages` attribute of this target must list all the `pages` targets of the
   immediate sub-directories.

   To pick up the example from above, after adding a new directory
   `ns/service-page/docs/demo-group`, amend the index target in
   `ns/service-page/docs/BUILD` as follows:

   ```bazel
   agora_service_page_index(
       name = "index",
       copy_to_source = True,
       pages = [
           # other existing entries . . .
           "//ns/service-page/docs/id:pages",
           "//ns/service-page/docs/demo-group:pages",   # <--- new 
       ],
   )
   ```

6. Run the buildifier tool to re-format all `BUILD` files properly:

   ```shell
   bazel run //:buildifier
   ```

7. Finally, run the update script as explained in the section
   [Convenience script to (re-)generate all Service Pages and the Index](#convenience-script-to-re-generate-all-service-pages-and-the-index) below,
   to generate the markdown for your new Service Page and to update the index.

## Editing the Service Page values file

The input for each Service Page comes from a values file
(`values.yaml`/`values.json`). The Bazel rule that generates the markdown output
from a values file validates the contents of the values file using a schema, and
any validation issues are reported as errors, making the generator fail.

Some editors are capable of using the same schema file to assist you while
working on a values file, such as auto-completion and inline validation.

For Visual Studio Code, the shared configuration in the repo has the necessary
settings to enable this support automatically. All you need to do is open the
repository root, so that the shared config at
[.vscode/settings.json](https://github.com/wp-wcm/city/tree/main/.vscode/settings.json)
applies. You also need to have the built-in JSON language features and the
[Redhat YAML plugin](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-yaml)
enabled, but these are on by default, so it should usually just work.

> [!NOTE]
> The schema is only applied in VSCode for files that are called `values.yaml`
> or `values.json` inside `ns/service-page/docs` or its subdirectories.

## How to (re-)generate the Service Pages and the Index

### Automation (Zebra integration)

The Bazel rules for generating Service Pages and their index is integrated with
[Zebra](https://developer.woven-city.toyota/docs/default/component/zebra-service/),
our code abstraction tooling.

Whenever you open a PR that changes existing service page source files, the
Zebra automation will pick them up and re-generate the dependent output files.

As one caveat, the Zebra automation does not create new output files in a PR if
they don't already exist; it only updates the contents of existing output files.
When you add a new Service Page, you have to run the generation manually once
and commit the results to the repo.

### Convenience script to (re-)generate all Service Pages and the Index

For convenience, we provide a script that updates all Service Pages as well as
the Service Page index.

The script is located here: [ns/service-page/update-all.sh](../../service-page/update-all.sh).

To call it from anywhere in the workspace, you can use this snippet in a bash or
a compatible shell:

```shell
$(bazel info workspace)/ns/service-page/update-all.sh
```

### Running the Bazel targets manually

To create (or update) a specific Service Page, call `bazel run` for the
`:update` target of that page.

For example, to update the Service Page for drako (located in
[ns/service-page/docs/id/drako](../../service-page/docs/id/drako)), run:

```shell
bazel run //ns/service-page/docs/id/drako:update
```

To update just the Service Page Index, do the following:

```shell
bazel run //ns/service-page/docs:update
```

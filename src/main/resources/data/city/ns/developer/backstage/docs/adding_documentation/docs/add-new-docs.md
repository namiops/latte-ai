# Adding new Documentation to the Developer Portal

This document gives a quick-start overview of the process for adding documents to the Developer Portal.
The direction or steps you should take depend on the location of your code.

## Who should read this document?

This document contains current guidelines and best practices aimed at the Agora Team developers as subject matter experts and writers for an external audience (ie. users of the Agora platform). By following this document, the Agora Team should be able to host their documentation on the Developer Portal in a way that maximizes visibility and accessibility of their contents.

## Create the necessary folder and files in your project directory

Given the following example structure:

```plain linenums="0"
city
  ├── infra
  ├── ns
  ...
  └── projects
      └── example-project
        ├── src
        │   ├── components
        │   ├── pages
        │   └── ...
        └── package.json
```

To add documentation for `example-project`, you will need to:

- Create `catalog-info.yaml` and `mkdocs.yml` at the project's root (see below for examples)
- Create a `docs` folder where your documentation's Markdown files will be organized. There should be at least a `README.md` or `index.md` file, otherwise your documentation might not work properly
- Optional: If you need to add images or other assets, add an `assets` folder inside `docs` to place such files in

Afterwards, the file structure should look like this:

```plain  linenums="0"
city
  ├── docs
  ├── infra
  ├── ns
  ...
  └── projects
      └── example-project
        ├── docs
        │   ├── assets
        │   │   └── my-image.png
        │   ├── README.md
        │   ├── 01-document.md
        │   └── 02-document.md
        ├── src
        │   ├── components
        │   ├── pages
        │   └── ...
        ├── catalog-info.yaml
        ├── mkdocs.yml
        └── package.json
```

### catalog-info.yaml

This file is used to [declare your entity inside Backstage](https://backstage.io/docs/features/software-catalog/descriptor-format/#overall-shape-of-an-entity) so that the Developer Portal can automatically render it, and should look like this (with edits wherever needed):

```yaml title="catalog-info.yaml"
# nonk8s
apiVersion: backstage.io/v1alpha1
kind: Component
metadata:
  name: example-project # The project name
  title: Example Project # Your project's title, which will show up in the documentation header
  description: An example project to add docs.
  annotations:
    backstage.io/techdocs-ref: dir:.
spec:
  type: documentation
  lifecycle: experimental
  owner: group:team-name # Your group name within Agora
```

If you'd like to list your documentation in the portal homepage and sidebar, add the following lines after `annotations`:

```diff linenums="0"
  annotations:
    backstage.io/techdocs-ref: dir:.
+ labels: {
+   category: "services"
+ }
```

For adding an item to the Components list, chose one of these categories: `data`, `identity`, `iot`, `services`. For the Codelabs section, use `codelab`.

### mkdocs.yml

Backstage uses techdocs -its own wrapper for [MkDocs](https://www.mkdocs.org/)- in order to render documentation, so this file is used to configure things like MkDocs or mkdocs-material's (the MkDocs theme techdocs uses) extensions and plugins.
Copy the `mkdocs.yml` file [from the root of the monorepo](https://github.com/wp-wcm/city/blob/main/mkdocs.yml), change the `site_name` to your project's name and remove comments if you wish. Of course, you can also edit it to add extensions and plugins you'd like to use:

```yaml title="mkdocs.yml"
site_name: Example Project

theme:
  name: material
  highlightjs: true

markdown_extensions:
  - admonition
  - attr_list
  - md_in_html
  - meta
  - tables
  - pymdownx.details
  - pymdownx.emoji:
      emoji_index: !!python/name:material.extensions.emoji.twemoji
      emoji_generator: !!python/name:material.extensions.emoji.to_svg
  - pymdownx.snippets
  - pymdownx.superfences:
      custom_fences:
        - name: mermaid
          class: mermaid
          format: !!python/name:pymdownx.superfences.fence_code_format
  - pymdownx.tabbed:
      alternate_style: true

plugins:
  - techdocs-core
  - awesome-pages
  - kroki:
      FencePrefix: ""
      ServerURL: "/api/proxy/diagrams"
  - tags
```

!!! Info
    If you have any issues creating new documenation, refer to the [Markdown guidelines](./markdown-guidelines.md) for in-depth details on how documentation is processed.

## Link your documentation to the main catalog

Open the `catalog-info.yaml` at the root of the monorepo, and add your project's `catalog-info` file to the list of targets:

```diff  linenums="0" title="catalog-info.yaml"
spec:
  targets:
    ...
+   - ./projects/example-project/catalog-info.yaml
```

## Merge your changes in

Create a new pull request and add your changes to the monorepo.

## Verify the documentation is on the portal

After your changes are in the `main` branch, Backstage should pick up your documentation automatically, which you can verify on the Developer Portal; this may take a few minutes.
One place to check first would be the list of [Components](https://developer.woven-city.toyota/catalog/default?filters%5Bkind%5D=component&filters%5Buser%5D=all), or if your documentation is for a System or Tutorial, it should show up in the homepage's respective list.

## Questions and Feedback

Please feel free to reach out to the Agora Developer Relations Team if you have any questions or comments. You can reach us on the [AMA Channel](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7).

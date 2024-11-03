# Guidelines for Markdown Files in the Developer Portal

The following is a set of guidelines and best practices for how to use Markdown in the developer portal.

## How Markdown Works in the Developer Portal

Markdown (`.md`) files are the main resource that is hosted on the portal.
These files can then be rendered by the portal with additional bells and whistles.

To host the markdown, the portal uses [Backstage](https://www.backstage.io/) that helps with rendering and provides additional plugins for support for things like tabbed content and use of in-markdown graphics via [Kroki](https://kroki.io/).

## How to use Markdown on the Developer Portal

- To generate technical documentation in the portal, you need to prepare a configuration file named `mkdocs.yml`.
- Markdown files that are located in the same directory as `mkdocs.yml` **cannot** be converted, only files located one level deeper. The default name of the directory is `docs`.

    ```plain
    ROOT_DIR
    ├── CAN_NOT_BE_GENERATED.md
    ├── mkdocs.yml
    └── docs (can be renamed)
        ├── CAN_BE_GENERATED_1.md
        └── CAN_BE_GENERATED_2.md
    ```

- It is acceptable for Markdown files to be stored in a deep hierarchy under the `docs` directory.
The hierarchical structure will be maintained and technical documentation will be generated (however, the detailed behavior depends on active MkDocs plugins).

    ```plain
    ROOT_DIR
    ├── CAN_NOT_BE_GENERATED.md
    ├── mkdocs.yml
    └── docs
        ├── section_1
        │   ├── CAN_BE_GENERATED_1.md
        │   └── CAN_BE_GENERATED_2.md
        └── section_2
            └── CAN_BE_GENERATED_3.md
    ```

- The default file displayed among multiple Markdown files is `README.md`, after that the files are displayed in alphabetical order by filename.
If you'd like to have them listed in a different order, you can use numeric prefixes (`01-`, `02-`, etc.), or [customize the navigation via plugin](./tips-and-tricks.md#custom-navigation).

!!! Warning
    If neither `index.md` nor `README.md` are present, your documentation will not display correctly. `readme.md` is not considered equivalent to `README.md`.

## External and internal documentation

Some teams have external documentation that they wish to have published in the Developer Portal, and internal documents for their own reference.
The recommended structure for this is to use `ext` and `int` subdirectories inside of `docs`:

```plain
example-project
├── docs
│   ├── assets
│   │   └── my-image.png
│   ├── ext
│   │   ├── README.md
│   │   ├── 01-external-document.md
│   │   └── 02-external-document.md
│   └── int
│       ├── README.md
│       ├── 01-internal-document.md
│       └── 02-internal-document.md
├── src
├── catalog-info.yaml
└── mkdocs.yml
```

Then in `mkdocs.yml`, after the plugins section, point to your external documentation folder by adding `docs_dir: docs/ext`.

!!! Warning
    <!-- markdownlint-disable code-block-style -->
    If you have documentation already published in the Developer Portal and want to change the documentation's structure, your entity will become [orphaned](https://backstage.io/docs/features/software-catalog/life-of-an-entity/#orphaning) and won't reflect any updates until it is re-built.

    If you see the message `This entity is not referenced by any location and is therefore not receiving updates. Click here to delete.` in your entity's overview (`/catalog/default/component/<entity-name>`) after re-organizing the directories, click to delete it, and then merge any change to your documentation to re-trigger a build on Backstage.

## File naming

The general consensus for URLs is to use hyphens (`-`), so file names for Markdown documents **must** use kebab-case.
The system does some changes under the hood when converting files to render them in the Developer Portal, like removing numeric prefixes (`01_`, `01-`, etc.) or transforming spaces into hyphens for headers, but for ensuring consistency of the entire URL, please use hyphens for all file names.

Correct names ✅

- 01-document-name.md
- multilingual-document-en.md
- how-to-create-a-docker-deployment.md

Incorrect names 🙅

- 01_document-name.md
- multilingual-document_en.md
- how_to_create_a_docker_deployment.md

## Line breaks

It is _recommended_ to avoid line breaks in sentences whenever possible.
Line breaks between sentences are acceptable, although they will not reflect as new lines in the rendered document.
Instead, consider creating a new paragraph by adding an empty line before the next text block.

```markdown
This sentence is the first one in this paragraph.
This second sentence also belongs to the first paragraph.

This is the first sentence of the second paragraph.
```

Since this is a recommendation, we encourage teams to discuss and decide on how they want to use line breaks and stick to that decision, to avoid unnecessary diffs in pull reviews between members of the same team.

## How Relative Links work in the Developer Portal

In the Developer Portal you are able to use relative links to redirect people to pages.
The Developer Portal works by setting up the URL based on the following format:

```plain
<Host name>/docs/<namespace>/<entity-type>/<path>/<sub-path>
```

This can be used so that Markdown files can point to other entities inside the portal, once processed by Backstage; this will not work on GitHub.

For example, this link will open the README for this documentation, anywhere in the Developer Portal: [/docs/default/Component/backstage-website/](/docs/default/Component/backstage-website/).

### Validating links

VS Code can validate links in Markdown to make sure a linked file exists.
To enable this option open the Settings menu, find Extensions > Markdown, and check the `Markdown > Validate: Enabled` checkbox; also make sure that `Markdown › Validate › File Links: Enabled` is not set to `ignore`.

To stop the validation from being triggered by relative Backstage links, look for the `Markdown > Validate: Ignored Links` option and add an item for `/docs/default/**/`.

## Best Practices for Markdown

The following is some best practices that are current for the Developer Portal.

### Familiarize yourself with Markdown

If you're not familiar with Markdown, you might not be aware of the features you can take advantage of for writing your documentation.
GitHub has a good, extensive [beginners guide for Markdown](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) that is worth going over and bookmarking for reference.

### Follow the Markdown style guide

Markdown like all files can have potential issues, so to prevent any problems with the rendering of the Markdown and to ensure consistency, it's recommended to follow the [Markdown Style Guide](./markdown-style-guide.md).

### Use a Linter for Markdown

Agora recommends the [VS Code Markdownlint extension](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint) as a way to lint markdown.
The style guide is based on its rules, so it makes writing documentation easier by automatically highlighting and fixing issues wherever possible.

If you don't use VS Code, Markdownlint is available for other editors and IDEs:

- [Vim](https://github.com/fannheyward/coc-markdownlint)
- [Emacs](https://melpa.org/#/flymake-markdownlint)
- [JetBrains](https://plugins.jetbrains.com/plugin/20851-markdownlint)

However, for the best support in previewing documents and extension availability, VS Code is _strongly_ recommended for writing documentation.

!!! note
    The markdownlint extension is included in the Developer Portal Markdown Extensions pack mentioned below.

### Use preview tools to check your documents

VS Code and other IDEs have a preview mode and extensions for visualizing Markdown in real time, making it very easy to see what your document will be rendered like as you write.

For easy setup, you can use the [Developer Portal Markdown Extensions](https://github.com/wp-wcm/devportal-markdown-extensions) pack for VS Code, which includes all of our recommended extensions:

1. [Download the extension's `.vsix` file](https://github.com/wp-wcm/devportal-markdown-extensions/raw/main/devportal-markdown-extensions-1.0.0.vsix)
1. Go to the Extensions tab in VS Code and click the three-dot menu, then select `Install from VSIX...`

    ![VS Code extensions tab menu](./assets/vscode-extensions-menu.png)

1. Select the file you downloaded previously and that's it! All the included extensions will be installed. If the preview doesn't update automatically, close and open it again to refresh the content.

If you prefer to install the extensions manually, these are the ones included, in addition to Markdownlint:

- [MkDocs Syntax Highlight](https://marketplace.visualstudio.com/items?itemName=aikebang.mkdocs-syntax-highlight) - Adds syntax highlighting for MkDocs and mkdocs-material features, like admonitions and tabs
- [Markdown Checkboxes](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-checkbox) - Adds `[ ]` task list support to the Markdown preview
- [Markdown yaml Preamble](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-yaml-preamble) - Makes yaml front matter (meta-data) render as a table
- [Markdown Preview Mermaid Support](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-mermaid) - Adds Mermaid diagram and flowchart support the preview and to Markdown cells in notebooks
- [Mermaid Markdown Syntax Highlighting](https://marketplace.visualstudio.com/items?itemName=bpruitt-goddard.mermaid-markdown-syntax-highlighting) - Adds syntax highlighting inside of ` ```mermaid ` code blocks

### Verifying your changes with MkDocs and Backstage

Unfortunately not every MkDocs feature is supported by live previews using the above extensions (such as admonitions), so using only the editor might not be enough for making sure your documentation looks as expected.

For a quick check, running `mkdocs serve` in a directory with an `mkdocs.yml` file will serve your documentation using MkDocs which wil be very close to the final product.
However, we strongly recommend that you do a final preview of your documents by pushing your branch, [changing the local backstage configuration](https://github.com/wp-wcm/city/blob/main/ns/developer/backstage/app/app-config.local-override.yaml#L47) to point to it, and [running Backstage locally](https://github.com/wp-wcm/city/blob/main/ns/developer/backstage/README.md#running-backstage-locally).

### Use supported in-document graphing tools

[Mermaid](./tips-and-tricks.md#Mermaid) is recommended for in-document graphics.
Agora provides a setup for these graphic tools to be used by the Developer Portal, so you don't have to do any setup on your own via [Kroki](https://kroki.io/):

You can add this support via our provided `mkdocs.yml` template or adding the following to an existing config:

```yaml title="mkdocs.yml"
plugins:
  - kroki:
      FencePrefix: ''
      ServerURL: '/api/proxy/diagrams'
```

### Use of doctoc

[doctoc](https://github.com/thlorenz/doctoc) is a tool for automatically creating a table of contents (TOC) in documents based on its headers.
It's a pretty handy tool if you have a long document with many sections and would like to maintain a TOC.
However, MkDocs and techdocs already provide a table of contents for navigation on the right-hand side of documentation pages they render.

Therefore, adding a TOC to Developer Portal documents (whether via doctoc or with another method) SHALL NOT be done, as it is redundant.
Of course, if your document is meant to be internal and looked at on GitHub (using an `/int` folder), then feel free to add a TOC to facilitate navigation for your readers.
Even for internal documentation though, please follow these rules for using doctoc so that its generated code can be cleaned up:

- Use the [title option](https://github.com/thlorenz/doctoc?tab=readme-ov-file#specifying-a-custom-toc-title) when generating the TOC to override doctoc's default title (or update manually after generating).
The reason for this is that it inserts wording and a link to its doctoc GitHub which are unnecessary for our documentation.
doctoc's title also uses emphasis as a header, which breaks linter rules
  - Use a header of the correct level to replace the title, such as `## Table of Contents` or `## In this document` if below the main header, as mentioned in the next point
- After the TOC is generated, it will most likely be placed at the beginning of the document, with its first link being to the document's title/main header.
Please cut the table and paste it (and doctoc's HTML comment tags) after the document's header, remove the list's link to it, and correct the indenting for the remaining list entries.
The first line of every document should be a level 1 header and doctoc breaks this rule.

## Spelling checks

In case you would like to check the spelling in your Markdown files, the [Code Spell Checker extension](https://marketplace.visualstudio.com/items?itemName=streetsidesoftware.code-spell-checker) is recommended. It comes with dictionaries for popular programming languages so common words won't clash with human language, and is highly configurable.

There's still a chance that some words will trigger it within code blocks though, so you can change its settings to ignore inline code (single backticks) and code blocks with no indentation or indented up to 4 spaces:

1. Open your VS Code user options by opening the command palette (`Ctrl+Shift+P` or `Command+Shift+P`) and search for `Preferences: Open User Settings (JSON)`
1. Add the following to your options JSON

    ```json
    "cSpell.languageSettings": [
      {
        "languageId": "markdown",
        "ignoreRegExpList": [
          "`.*`",
          "/^([ ]{2,4})?```(?:.|\\s)+?^([ ]{2,4})?```/mgi"
        ]
      }
    ]
    ```

If there's other frequently used non-English words like `kustomization` that you would like ignored in paragraphs, you can use the Quick Fix menu to add that word to your user or workspace settings.

## Troubleshooting

### Broken lists

In some cases, lists with the correct indentation (per Markdownlint checks) will look broken:

<div class="grid" markdown>
Code:

```markdown linenums="0"
- Item 1
  - Sub-item 1
  - Sub-item 2
- Item 2
  - Sub-item 1
```

</div>
<div class="grid" markdown>
Expected result:

- Item 1
  - Sub-item 1
  - Sub-item 2
- Item 2
  - Sub-item 1

</div>
<div class="grid" markdown>
Actual result:

- Item 1
- Sub-item 1
- Sub-item 2
- Item 2
- Sub-item 1

</div>

To fix this, the `mdx_truly_sane_lists` markdown extension needs to be added to your documentation's `mkdocs.yml`.

```diff title="mkdocs.yml" linenums="0"
markdown_extensions:
  - admonition
  - attr_list
  - md_in_html
+ - mdx_truly_sane_lists
  - meta
  - tables
```

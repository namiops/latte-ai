# Markdown style guide

This style guide is based off of the [Markdownlint rules](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md), and it is meant to help reduce potential rendering issues on the Developer Portal, as well as to help keep consistency across all teams' documentation.

There are many rules that can be applied to markdown documents, but this document will focus on some of the most common and basic rules, while a tool such as the Markdownlint plugin for VS Code can help automate these checks.

!!! tip
    Install the [Developer Portal Markdown Extensions](./markdown-guidelines.md#use-preview-tools-to-check-your-documents) pack for VS Code to add all our recommended extensions for linting and previewing Markdown.

## Rules

### Follow proper header order

Use headers in the correct order to properly organize your document and its table of contents. The first line in any document should be a level 1 header, preferably matching the file name.

<div class="grid">

```markdown linenums="0" title="✅ Do this"
# Document Title

Paragraph

## Section

Paragraph

### Sub-section

Paragraph
```

```markdown linenums="0" title="❌ Not this"
Paragraph

# Document Title

### Sub-section

Paragraph

## Section

Paragraph
```

</div>

### Add empty lines between headers and lists

<div class="grid">

```markdown linenums="0" title="✅ Do this"
# Document Title

- List item
- List item
- List item

## Section

Paragraph text.
```

```markdown linenums="0" title="❌ Not this"
# Document Title
- List item
- List item
- List item
## Section
Paragraph text.
```

</div>

### Add a space between header and list markers and text

Most editors will also highlight these issues without any special plugins, and previews won't work correctly.

<div class="grid">

```markdown linenums="0" title="✅ Do this"
# Document Title

- List item
- List item
- List item
```

```markdown linenums="0" title="❌ Not this"
#Document Title

-List item
-List item
-List item
```

</div>

### Add a language to code blocks

MkDocs provides syntax highlighting for a large variety of languages, which makes code examples easier to read and understand. For reference on the available lexers and short names please refer to [this list](https://pygments.org/docs/lexers/).

````markdown linenums="0" title="✅ Do this"
```javascript
const start = Date.now();
longProcess();
console.log(`Time elapsed: ${Date.now() - start} ms`);
```
````

````markdown linenums="0" title="❌ Not this"
```
const start = Date.now();
longProcess();
console.log(`Time elapsed: ${Date.now() - start} ms`);
```
````

If no syntax highlighting for a programming language is needed, `plain` or `text` can be used.

### Take advantage of lazy numbering for ordered lists

When creating ordered lists, you don't have to explicitly number each item in its correct order, Markdown can automatically order a list made up of 1s. This is particularly useful during pull request reviews, since you don't have to edit subsequent items after adding a new item in the middle of a list, creating noisy diffs.

<div class="grid" markdown>

```` linenums="0"
```markdown
1. First item
1. Second item
1. Third item
```
````

1. First item
1. Second item
1. Third item

</div>

### Other rules to keep in mind

- Remove trailing spaces
- Add a new line at the end of files
- Keep indentation consistent
- Don't use bare URLs, wrap them in angle brackets or add descriptive link titles (`[title](http://example.com)`)

!!! tip
    For the full list of rules, please visit the [Markdownlint docs](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md).

## Exceptions

As mentioned in other sections of this documentation, not all MkDocs/techdocs features are supported by editors, meaning that their syntax highlighting will clash with that of Markdownlint.
When these cases happen and you know your code is correct, Markdownlint exception comments can be used to remove warnings from your editor.

The following code for an admonition might report an error on the second paragraph in VS Code:

```markdown
!!! Info
    First paragraph.

    Second paragraph.
```

Adding a comment to the top of the admonition content suppresses the warning:

```markdown
!!! Info
    <!-- markdownlint-disable code-block-style -->
    First paragraph.

    Second paragraph.
```

You can also disable and re-enable specific rules, disable Markdownlint for a whole document, or use disable/enable comments. For full information of Markdownlint's configuration please refer to [its documentation](https://github.com/DavidAnson/markdownlint?tab=readme-ov-file#configuration).

## Markdown features that do not work in MkDocs/techdocs

Backstage built techdocs on top of MkDocs, and MkDocs has its own rules for rendering markdown via HTML, so some things will not work as expected.

### HTML

While MkDocs allows HTML inside Markdown, for security reasons techdocs [uses DOMPurify](https://github.com/backstage/backstage/issues/3998) which strips almost all HTML tags away. Certain tags like anchors or images work and Markdownlint can be configured to allow them, but script tags will never work, for example.

We recommend that you try to use pure Markdown as much as possible. Use your own judgement for adjusting the linter's rules if there are times when you have to use HTML tags, and verify that they work as intended on your local instance of the Developer Portal.

# Developer Portal Contributing Guide 👷‍♂️

Welcome to the Agora Developer Portal! Whether you're a new Developer Relations team member, or from a different team but would like to contribute changes to the Portal, please read this guide to learn about working on this project. If you have any questions, please reach out to any of our team members.

## IDE Setup 👨‍💻

VS Code is the editor that the team officially uses to work on the Developer Portal. Aside from its out-of-the-box features, it has a great extensions system that provides many functionalities we use. By standardizing the tools we use, we can guarantee consistency in the code style and quality.

### Required extensions 🔌

If you don't have them installed yet, these are the extensions that you should have installed at minimum:

- [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) - JavaScript and TypeScript code analyzer and fixer, can be configured per project
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) - Code formatter that helps keep consistency for things like semicolons and trailing commas
- [YAML](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-yaml) - YAML language support and formatting provided by RedHat; adds intellisense for compatible schemas and helps auto-fix indentation, quotes, etc.
- [devportal-markdown-extensions](https://github.com/wp-wcm/devportal-markdown-extensions) An in-house extension pack that includes several other extensions that enhance VS Code's Markdown preview to help write documentation:
  - [Markdownlint](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint) - This extension helps lint Markdown files according to CommonMark rules
  - [MkDocs Syntax Highlight](https://marketplace.visualstudio.com/items?itemName=aikebang.mkdocs-syntax-highlight) - Adds syntax highlighting for MkDocs and mkdocs-material features, like admonitions and tabs
  - [Markdown Checkboxes](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-checkbox) - Adds `[ ]` task list support to the Markdown preview
  - [Markdown yaml Preamble](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-yaml-preamble) - Makes yaml front matter (meta-data) render as a table
  - [Markdown Preview Mermaid Support](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-mermaid) - Adds Mermaid diagram and flowchart support the preview and to Markdown cells in notebooks
  - [Mermaid Markdown Syntax Highlighting](https://marketplace.visualstudio.com/items?itemName=bpruitt-goddard.mermaid-markdown-syntax-highlighting) - Adds syntax highlighting inside of ` ```mermaid ` code blocks

### Other recommended extensions

These extensions are not necessary for style or standards purposes, but they can be useful when working in Backstage or other related Devrel tasks.

- [Code Spell Checker](https://marketplace.visualstudio.com/items?itemName=streetsidesoftware.code-spell-checker) - Since the Developer Portal is mainly focused on documentation, and we constantly author and edit it, this is a good tool to make sure there's no typos in a document; it can also be [configured to ignore code blocks and inline code](https://developer.woven-city.toyota/docs/default/component/backstage-website/markdown-guidelines/#spelling-checks)
- [Pretty TypeScript Errors](https://marketplace.visualstudio.com/items?itemName=yoavbls.pretty-ts-errors) - Adds better formatting to TypeScript errors to make them easier to read

### Other pre-requisites

Independent of Backstage's setup, we're also using Husky to lint staged files using Git hooks. This will perform two tasks: perform style and formatting checks on staged files, and check commit messages.

To get this set up, you need to export a couple environment variables in your shell, which you can do via your configuration file (`~/.zshrc` or its equivalent):

```zsh
export ENABLE_COMMITLINT=1
export ENABLE_LINT_STAGED=1
```

Afterwards you should reload your shell or open a new terminal window, and now any commit you make will trigger these checks.
The checks run by lint-staged will depend on whether a configuration file (`.lintstagedrc`) is detected in the current working directory and what configuration it provides.

## Git branch guidelines 🌳

- The working directory for Backstage/the Developer Portal [is in the City monorepo](https://github.com/wp-wcm/city/tree/main/ns/developer)
- Please check out a new branch based on `main` and work on your feature, then create a merge request that goes back into `main`. Of course, in case your work requires smaller merges into a feature branch which will later go into `main`, that is OK
  - Branches must use kebab case, in lowercase; there should _no slashes_ in the name since previewing branches locally doesn't work correctly if they do
  - While there are no other strict rules aside from using kebab case, [conventional commit convention](https://www.conventionalcommits.org/en/v1.0.0/#summary) is recommended for branch names, i.e. `feat-add-user-details-page`
  - Try to keep the branch name as short and clear as possible, and then use a more descriptive title and full description in the pull request
  - Do not add task ticket number/ID in the branch name, as for most reference tasks the GitHub UI will be used to find a specific pull request. The pull request template does include a ticket number section to easily link to Monday (and automatically detect branch status)
- It's OK to have multiple small commits as you work on the PR, as the PRs are squash merged

### Committing Changes ✅

Commit messages should follow [conventional commit convention](https://www.conventionalcommits.org/en/v1.0.0/#summary). A type is required, scope is optional, and commit messages will be automatically validated upon commit using commitlint as previously mentioned.

Valid commit types are: `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `style`, `test`.

### Pull Requests ↩️

When opening a Pull Request, prefix the title with `[Dev Portal]`, and fill out the [PR template](../../../.github/PULL_REQUEST_TEMPLATE/devrel_template.md) to add a description.

As a review requester, and as a gesture of good teamwork, please provide enough context for reviewers to more easily understand and review your code; the answer to the question "how will this change improve the product/service" should be clear form the description.

If your PR's changes exceed ±500 lines of code, consider breaking it up into smaller PRs. Of course this depends on the situation and files changed, for example npm, pnpm and yarn lock files can add thousands of lines in diffs, but the main changes can still be quantified.

As a reviewer, try to provide a [good review](https://google.github.io/eng-practices/review/reviewer/) for your teammates in order to improve the product and codebase. 💪

## Issue Reporting 📋

Please reach out to us in our [Slack channel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD) to report any issues or if you have any questions regarding Backstage/Developer Portal.

# Tooling for JavaScript projects

## Husky

Husky is a package that allows you to use Git hooks to automate tasks such as linting, testing, and formatting code.

## Configuration

Because of the nature of the monorepo, where we have a diversity of users with different preferences and needs, even though hooks are initiated upon installing dependencies with pnpm, enabling them is **opt-in**.
To enable hooks, you have to add specific environment variables to your shell, depending on the tools you want to use:

```sh title="~/.zshrc"
export ENABLE_COMMITLINT=1
export ENABLE_LINT_STAGED=1
```

!!!tip
    Don't forget to reload your shell so that the variables work properly.
    Using `source ~/.<your shell config file>` or opening a new terminal window can do this.

### Commitlint

[Commitlint](https://commitlint.js.org/) can check commit messages to make sure that they're following a specific convention.
After adding `ENABLE_COMMITLINT` to your shell, your commit messages are ready to be checked.
By default, commits will be checked using the [Conventional Commits convention](https://www.conventionalcommits.org/en/v1.0.0/) via [@commitlint/config-conventional](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional).

In a nutshell, commits must be written like this `type(scope): commit message` with type being one of:

- build
- chore
- ci
- docs
- feat
- fix
- perf
- refactor
- revert
- style
- test

Scope is optional and can be anything, and the message cannot be empty.

Once enabled, commitlint will check all commits done anywhere in the monorepo's structure.

### Lint-staged

[Lint-staged](https://github.com/lint-staged/lint-staged) can be used to lint files that have you staged, right before they are committed.
This is very useful for catching anything that might have gotten past linters in an IDE if they are misconfigured or not installed, since we currently do not run style linters on CI.

If for example you want to run ESLint and prettier on staged files, after adding the `ENABLE_LINT_STAGED` variable as instructed above, create a configuration file at the root of your JavaScript project and lint-staged will us it for that directory:

```js title=".lintestagedrc"
{
  "*.{js,jsx,ts,tsx}": "eslint --ignore-path .gitignore --fix",
  "*.md": "prettier . --write"
}
```

You can keep adding more checks for other types of files with different tools.
For more information and for alternative configuration options, please refer to the [lint-staged documentation](https://github.com/lint-staged/lint-staged?tab=readme-ov-file#configuration).

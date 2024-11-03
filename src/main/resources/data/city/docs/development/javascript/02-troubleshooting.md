# Troubleshooting

These are some issues that team members have coma across in the past, and how to solve them. If your issue is not mentioned here.

## Artifactory Node permissions

If you receive a 403 or other permissions error when installing npm dependencies from Artifactory, please follow the instructions to [authenticate with Artifactory](../bazel/README.md#optional-step-authenticate-to-private-npm-registry).

## Artifactory Docker permissions

For 401 errors when getting Docker artifacts, add `--experimental_downloader_config=` to you Bazel commands:

```sh
bazel build //ns/... --experimental_downloader_config=
```

## IDE Issues

### VS Code: Formatting on save doesn't work or there are other linting issues

First, make sure that the [VS Code ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) and other extensions you need (Prettier, Stylelint, etc.) are installed. If you add project directories one by one to your workspace, then linting should work as expected.

If you would like to open the `city` root directory directly and see all its subdirectories, follow these steps:

1. Save your workspace by selecting `File` > `Save Workspace As...`.
1. Open the command palette with `Cmd + Shift + P` on Mac or `Ctrl + Shift + P` on Windows.
1. Search for the `Preferences: Open Workspace Settings (JSON)` command and click it.
1. In this file, add the following setting and save:

```json
{
  "settings": {
    "eslint.workingDirectories": [{ "mode": "auto" }]
  }
}
```

This should now allow formatting on save to work in monorepo subdirectories.

If it is still not working, check your user settings: open the Settings tab can be quickly opened with `Cmd + ,`/`Ctrl + ,` then you can search for `Editor: Format On Save` to make sure it's turned on. If issues persist, check other user settings you might have changed that could possibly override linting settings.

As fallbacks, there are two ways to perform linting outside of the IDE to maintain coding styles and avoid unwanted diffs in PRs:

1. Run linting scripts manually on the terminal, if you have set them up in your `package.json`.
1. Create a `.lintstagedrc` in your _project's_ root directory, and add the `ENABLE_LINT_STAGED=1` environment variable to your shell. This way, your linters will format your code whenever you commit changes, since Husky is already set up to work on the monorepo with git hooks.

```json
/* .lintstagedrc */
{
  "*.{js,ts,tsx}": "eslint --ignore-path .gitignore --fix"
}
```

### My IDE shows 'Cannot find module or its corresponding type declarations' errors

Check if you have accidentally installed your dependencies in you project's `package.json` and created a `node_modules` folder in its directory, as this has been known to cause issues. If this is the case, remove the dependencies from your project and place them in the `package.json` of the monorepo root, install dependencies [with Bazel](README.md#managing-dependencies), and remove the `node_modules` folder from your project directory.

If a missing dependency error is instead being shown by Bazel after adding a new dependency, double-check that you have also added that dependency in your project's BUILD file.

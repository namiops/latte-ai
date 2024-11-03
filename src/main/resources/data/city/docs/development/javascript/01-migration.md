# Migrating an existing web application to the monorepo

This document provides step-by-step instructions on how to migrate an existing web app into the monorepo, to complement the [Running and building apps](README.md#running-and-building-apps) section in the main document. After following these instructions, your application should be up and running in Agora, having been tested and built in its CI/CD pipeline.

## 1. Moving the main code

The first step will be to copy the code for your application from wherever it is currently hosted, into the monorepo; projects should be placed in the `city/projects/` directory.

Once you create a directory for your project, paste your project's code, commit and merge it in. You should be able to still use npm/pnpm/yarn to install and run your application as normal.

## 2. Migrating your dependencies

### npm dependencies in the monorepo

As previously mentioned  in [Why Bazel?](README.md#why-bazel) the monorepo currently manages dependencies via Bazel, a build system. Since bazel takes a list of dependencies to create a dependency graph, it does not rely on `package.json` or the `node_modules` folder to build or run an application.

Instead, the monorepo is currently set up so that there is a single `package.json` file at the root, from which the dependencies for all projects are installed with pnpm. This allows npm and IDEs to go up the directory tree and use the root `node_modules` folder for type references and local builds. To make the project itself run, there should be a `BUILD` file in its root directory and that is where Bazel will know which dependencies to use.

### npm to Bazel dependency switching

The next step will be to create a BUILD file in your project's root with an empty dependency list:

```bazel
load("@aspect_bazel_lib//lib:copy_to_bin.bzl", "copy_to_bin")
load("@npm//:vite/package_json.bzl", vite_bin = "bin")

# Your project's necessary source files (not dependencies) should be referenced here
copy_to_bin(
  name = "srcs",
  srcs = glob(
      [
          "public/**/*",
          "src/**/*",
      ],
      exclude = ["tests/*"],
  ) + [
      "index.html",
      "tsconfig.json",
      "tsconfig.node.json",
      "vite.config.ts",
  ],
)

# Build dependencies
BUILD_DEPENDENCIES = ["//:node_modules/" + d for d in [
  # The dependencies will go here
]]

# Vite dev server
vite_bin.vite_binary(
  name = "vite",
  args = [
      "--host",
  ],
  chdir = package_name(),
  data = BUILD_DEPENDENCIES + [
      ":srcs",
  ],
)
```

Then, you can compare the monorepo's `package.json` with the one in your project's directory, check if your dependency has already been installed, delete it from your project's `package.json`, and add it to the BUILD file. For example:

=== ":material-code-json: Root package.json"
  <!-- markdownlint-disable-next-line code-block-style -->
    ```json
    // Redacted for brevity, there will be a lot more dependencies
    {
      "name": "city",
      "type": "module",
      "dependencies": {
        ...
        "react": "18.2.0", // <- The required dependencies already exist
        "react-dom": "18.2.0"
      },
      "devDependencies": {
        ...
        "typescript": "5.3.3",
        "vite": "5.0.12"
      }
    }
    ```

=== ":material-code-json: Project package.json"
  <!-- markdownlint-disable-next-line code-block-style -->
    ```diff
    {
      "name": "my-project",
      // Remove the dependencies
    - "dependencies": {
    -   "react": "^18.2.0",
    -   "react-dom": "^18.2.0",
    - },
    - "devDependencies": {
    -   "typescript": "~5.3.3",
    -   "vite": "^5.0.12"
    - },
      "scripts": {
        // Replace your scripts with bazel
    -   "dev": "vite",
    +   "dev": "ibazel run :vite",
      }
    }
    ```

=== ":material-file-code-outline: BUILD"
  <!-- markdownlint-disable-next-line code-block-style -->
    ```bazel
    # Update the dependency list
    BUILD_DEPENDENCIES = ["//:node_modules/" + d for d in [
      "react",
      "react-dom",
      "typescript",
      "vite"
    ]]
    ```

After you move the dependencies to the root `package.json` and use Bazel to run the build file you created, the project's dev server should now be up and running almost exactly as if it was being run by npm in a traditional setup.

## 3. Updating the rest of the scripts

Depending on how your web app is built (Vite, SWC, Next, etc.) this will vary a bit, but adding build targets in the BUILD file that run the same actions as npm scripts is a straightforward process. As en example we can add typechecking via tsc next.

```diff
# Add these lines to the BUILD file
load("@aspect_bazel_lib//lib:copy_to_bin.bzl", "copy_to_bin")
+load("@npm//:typescript/package_json.bzl", tsc_bin = "bin")
load("@npm//:vite/package_json.bzl", vite_bin = "bin")

+tsc_bin.tsc_test(
+    name = "typecheck",
+    args = [
+        "-p",
+        package_name() + "/tsconfig.json",
+    ],
+    data = glob([
+        "src/**/*",
+    ]) + BUILD_DEPENDENCIES + [
+        "tsconfig.json",
+        "tsconfig.node.json",
+    ],
+)
```

In the top section, load statements are used to import symbols of extensions (.bzl files); in this case we're loading typescript with `@npm//:typescript/package_json.bzl` and creating an alias for the `bin` symbol called `tsc_bin`. This alias can now be used to run a tsc type check on the application's code.

There's a bit more happening behind the scenes in this case, and that is that the Aspect Rules for JavaScript define the tsc binary, and that's why it can be imported with the load statement that was added, you can see [their workspace code here](https://github.com/aspect-build/rules_js/blob/main/MODULE.bazel#L100-L106).

So for the rest of the script like `vite build`, you can use `vite_binary` similar to how the dev script was converted. For an example to use as reference, please take a look at the BUILD files in the [hello world and existing projects in the monorepo](README.md#examples).

## Dependency version mismatches

For most major frameworks like React or Vue, the dependencies should already be in the monorepo's `package.json`. If the dependency is at the same or higher version than your project's requirement, then nothing needs to be done except list it in the BUILD file, and if the dependency is missing it can be installed via [pnpm](README.md#managing-dependencies).

In the event that a required dependency is installed, but it's at a lower version than the minimum needed for your project, then the recommended process is:

1. In a separate PR, bump the project dependency to the version number you require
1. Create a PR and wait for CI to run its tests (`bazel / build-and-test (pull_request)` GitHub action)
1. If the tests pass, merge the changes into main in after approval from codewoners
1. Merge/rebase from main to your project migration branch and now you can add the updated dependency to your BUILD file

If the tests fail however, the process is a little bit more complex, and there's a couple of ways to resolve this:

- If the changes are simple and you feel confident enough to fix the issues in the affected project's code, you can commit the changes into your PR and request a review from the corresponding codeowner team. [This PR](https://github.com/wp-wcm/city/pull/17624) is an example of this process
- Reach out to the affected team(s), share your PR and talk to figure out if and when they could help you solve the issues so that you can merge the dependency version bump

## Keeping local scripts

Sometimes you still want to run some scripts locally for ease of use, for example to check TypeScript types, then verify them in Bazel before pushing changes so that you can see them run as they would in CI.

It's as simple as just using a different name to add a Bazel equivalent for a script:

```json
// Project package.json
{
  "name": "my-project",
  ...
  "scripts": {
    "dev": "vite",
    "dev:bazel": "ibazel run :vite",
    "build": "vite build",
    "build:bazel": "bazel build :build-target",
    "typecheck": "tsc --noEmit",
    "typecheck:bazel": "bazel test :typecheck"
    // ...etc.
  }
}
```

These names are just examples, so you can use the naming convention that best fits your team. Keep in mind that the recommended way to run apps is still with Bazel.

## Conclusion

After converting your app following these instructions it should now be able to run in the monorepo without issues. If you do encounter eny problems, check the [troubleshooting document](02-troubleshooting.md) or if your issue is not listed, please reach out with any questions in the [Agora AMA channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).

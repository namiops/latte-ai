# Developing in JavaScript and TypeScript

Bazel supports JavaScript and TypeScript through [Aspect Build](https://github.com/aspect-build) rules. Compared to other languages such as Go, JavaScript ecosystem support is still limited, so some adjustments need to be made to the normal development workflow. To start working with code in the monorepo, you need to install [Bazelisk](https://github.com/bazelbuild/bazelisk), a user-friendly launcher for Bazel.

## Contents

- [Why Bazel?](#why-bazel)
- [Managing dependencies](#managing-dependencies)
- [Running and building apps](#running-and-building-apps)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Why Bazel?

Bazel is a build and test tool, similar to Make or Gradle, that supports multiple languages and platforms. It enables different projects in the monorepo to use the same system allowing for more optimized builds and releases at scale.

Bazel works on the concept of packages, defined by a directory with a BUILD or BUILD.bazel file. The BUILD file is a user-provided file to define build and test targets with specific inputs. A target's inputs can be source files, external dependencies, or even outputs from another target.

All previous builds are cached, and changes are tracked for both file content and build commands. By doing this, Bazel knows when and what exactly needs to be rebuilt, making build times very short.

As previously mentioned, Bazel is extended with rules, and in city we use the Aspect rule sets such as [rules_js](https://github.com/aspect-build/rules_js) and [rules_ts](https://github.com/aspect-build/rules_ts) to describe how to build various languages and frameworks. If you have any questions or issues related to building your JavaScript projects, refer to their documentation.

For more in-depth information, you can refer to the [Bazel in the monorepo](../bazel/README.md) document.

## Managing dependencies

Bazel managed dependencies by integrating with the [pnpm](https://pnpm.io/) JavaScript package manager. This locks in a specific version of pnpm, which ensures that all developers will have the same features and behavior when running it (for example, keeping the lock file version the same).

First, create a simple alias for the Bazel command in your shell configuration to make it easier for you to work with dependencies. For example, you can use `bnpm` as a contraction for Bazel + pnpm:

```sh
# In .zshrc:
alias bnpm='bazel run -- @pnpm//:pnpm --dir $(git rev-parse --show-toplevel)'
```

Now, run pnpm with the `bnpm` command. This runs at the root level of the monorepo, so dependencies can be installed or added from any project folder in it. You can also add new dependencies this way.

```sh
# Install all dependencies to your environment
bnpm install

# Lockfile-only update, nothing gets written to node_modules
bnpm install --lockfile-only

# Adding a new development dependency:
bnpm add react react-dom
bnpm add -D eslint
```

Although Bazel itself doesn't depend on the `node_modules` folders in the source tree, it is still required for your editor to find your typings. Therefore, developers are still expected to use `bazel build` and `bazel test`. The Bazel build represents the single source of truth for how your project is built, so you might want to [familiarize yourself with it](/docs/development/bazel/README.md) early on.

[rules_js](https://github.com/aspect-build/rules_js) will handle the dependencies via the `npm_link_all_packages` rule to set up `node_modules` folders based
on the `pnpm-lock.yaml` lockfile. This is similar to the `pnpm install` command, with the difference that `pnpm install` creates the `node_modules` folders in source tree while `npm_link_all_packages` creates them in the bin tree (Bazel directory).

For example, if we declare this dependency:

```json
"react": "18.2.0"
```

`npm_link_all_packages` will automatically create the Bazel target `//:node_modules/react`.

## Running and building apps

Since project dependencies are listed in the `package.json` file within the root directory of the monorepo instead of the project folder itself, the process for running the scripts for dev servers, bundling, etc. is also different.

Aside from adding dependencies to the root `package.json`, the key difference is that now they must also be added to your project's `BUILD` file, which instructs Bazel on how to run a project.

By passing the dependency list to your corresponding build target, everything should run in Bazel (or iBazel) the same way it would with a normal dependency list and pnpm itself. If a build fails with a "missing dependency" error, double check that you have also added the new dependency to your `BUILD` file.

After you create a [target for Bazel to execute](https://docs.aspect.build/rulesets/aspect_rules_ts/docs/transpiler/#ts_projecttranspiler), substitute the corresponding script in `package.json` as follows:

```bazel
# BUILD (or BAZEL.build) file

# An example Vite dev server
vite_bin.vite_binary(
    name = "vite", # this is the identifier for your target, that you will use in bazel commands
    chdir = package_name(),
    data = glob(
      [
        "public/**/*",
        "src/**/*",
      ]
    ),
)

# A project that will be transpiled
ts_project(
    name = "my_project",
    srcs = glob(["*.ts"]),
    # This deps list replaces the package.json dependencies
    deps = [
      "@npm//@types/node",
      "@npm//@types/foo",
      "@npm//somelib",
      "//path/to/other:library",
    ],
)
```

Bazel is used for building and testing, and iBazel can run projects in watch mode, so HMR is supported when saving your code changes.

```diff
// package.json
{
  "scripts": {
-   "dev": "vite",
+   "dev": "ibazel run :vite",
-   "build": "vite build",
+   "build": "bazel build :my_project",
  }
}
```

### Examples

Hello world code templates for monorepo web apps:

- [React, TypeScript and Vite template](https://github.com/wp-wcm/city/tree/main/projects/helloworld/javascript/react-ts-vite)
- [Next.js 13 with Pages Router](https://github.com/wp-wcm/city/tree/main/projects/helloworld/javascript/next-13-pages-router)
- [Vue, TypeScript and Vite template](https://github.com/wp-wcm/city/tree/main/projects/helloworld/javascript/vue-ts-vite/)

Some existing JavaScript projects in the monorepo:

- [Agora UI Admin Tool](https://github.com/wp-wcm/city/tree/main/ns/agora-ui/admin-tool) - Vite + Vue

### Migrating an existing web application

For more detailed instructions of the previous section to help migrate a standalone web app from a different repository or local folder into the monorepo, you can [read more in the next document](01-migration.md).

## Troubleshooting

Please refer to the [troubleshooting page](02-troubleshooting.md) for common issues and how to solve them.

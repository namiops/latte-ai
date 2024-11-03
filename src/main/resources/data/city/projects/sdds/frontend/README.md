# React + TypeScript + Vite Template

This template provides a minimal setup to get React working in Vite with TypeScript, HMR and ESLint + Prettier rules in the Agora monorepo.

## Prerequisites ✅

If you're not familiar with developing using Javascript/TypeScript in the monorepo, please refer to the [Javascript development documentation](../../../../docs/development/javascript/README.md) for a quick introduction. You must also have [Bazelisk](https://github.com/bazelbuild/bazelisk) installed in order to run all the workflows with Bazel, and the recommended version of [Node.js](https://nodejs.org/en) to have installed is at least LTS.

## Setup 🔨

### Files 📂

To get your project started, all you have to do is copy the contents of this folder into a new project directory inside `city/projects`. After that, you can edit the files as needed, such as renaming your project in `package.json`, changing the `index.html` title and editing this README to fit your project's needs.

If you have just cloned the monorepo and have not installed npm dependencies, please install them by following the steps in the [Javascript development documentation](../../../../docs/development/javascript/README.md).

### IDE 💻

[VS Code](https://code.visualstudio.com/) is the recommended IDE for web development. To take full advantage of this template, the following plugins should be installed; when opening this project for the first time, you will see a prompt showing them as recommended extensions.

- [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) - Statically analyzes code to find potential runtime issues and enforce best practices
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) - Enforces a formatting style on a codebase

## Development 👷

When you have completed your project setup, the following commands will be available:

```sh
npm run dev
```

Starts the development server with hot module reloading. Your web app will be running on `http://localhost:8000`, and it will automatically open and reload when saving any changes to your code.

```sh
npm run build
```

Creates an optimized production build of your web app. Note that when using Bazel to run the build process, the output will not show up in your project directory.
<!-- TODO: Add more details here about how to use the output for deployments -->

```sh
npm run preview
```

Runs a web server that serves a production build of the app on port 5173. If you'd like to change the port number, you can do so in the `args` option of the preview target in the BUILD file.

```sh
npm run typecheck
```

Runs the typecheck via Bazel in the same way as it would in the CI/CD pipeline, so we recommend doing typechecks using this command. If any errors occur, you can view them in the Bazel output folder shown after the run fails.

```sh
npm run typecheck:local
```

Uses `tsc` to check your typings. Any errors will be shown in your terminal.

```sh
npm run lint
```

Lints code style with ESLint. Any errors will be shown in your terminal.

```sh
npm run lint:fix
```

Fixes any linting errors that can be automatically fixed. Any remaining errors or warnings that have to be fixed manually will be shown in your terminal.

# Next.js 13 Template

This template provides a minimal setup to get Next.js working with TypeScript and ESLint + Prettier rules in the Agora monorepo. This template uses Next's Pages router, for use of the App router please check the `next-13-app-router` example.

## Prerequisites ✅

If you're not familiar with developing using Javascript/TypeScript in the monorepo, please refer to the [Javascript development documentation](../../../../docs/development/javascript/README.md) for a quick introduction. You must also have [Bazelisk](https://github.com/bazelbuild/bazelisk) installed in order to run all the workflows with Bazel, and the recommended version of [Node.js](https://nodejs.org/en) to have installed is at least LTS.

## Setup 🔨

### Files 📂

To get your project started, all you have to do is copy the contents of this folder into a new project directory inside `city/projects`. After that, you can edit the files as needed, such as renaming your project in `package.json` and editing this README to fit your project's needs.

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

Open [http://localhost:3000](http://localhost:3000) with your browser to see your app. You can start editing the page by modifying `pages/index.tsx`. The page auto-updates as you edit the file.

```sh
npm run build
```

Creates an optimized production build of your web app. Note that when using Bazel to run the build process, the output will not show up in your project directory.
<!-- TODO: Add more details here about how to use the output for deployments -->

```sh
npm run start
```

Runs a web server that serves a production build of the app.

```sh
npm run typecheck
```

Uses `tsc` to check your typings. Any errors will be shown in your terminal.

```sh
npm run lint
```

Lints code style with ESLint via Bazel. Any errors will be shown in your terminal.

```sh
npm run lint:fix
```

Fixes any linting errors that can be automatically fixed. Any remaining errors or warnings that have to be fixed manually will be shown in your terminal.

## Next.js Development ▲

[API routes](https://nextjs.org/docs/api-routes/introduction) can be accessed on [http://localhost:3000/api/hello](http://localhost:3000/api/hello). This endpoint can be edited in `pages/api/hello.ts`.

The `pages/api` directory is mapped to `/api/*`. Files in this directory are treated as [API routes](https://nextjs.org/docs/api-routes/introduction) instead of React pages.

### Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

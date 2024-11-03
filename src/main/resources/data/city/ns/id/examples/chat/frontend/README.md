# Project Setup

This project is based on Vue 3 + TypeScript + Vite, built with Bazel Aspect JS rules.

## Prerequisites

Please install [Bazel](https://bazel.build/install) build tool and [pnpm](https://pnpm.io/installation) package manager.

## Package Management

Instead of using `npm` in the project folder, the developer should execute the following in the repository root `/cityos`:

```
pnpm install
```

All new dependencies should be added in the same manner using `pnpm`.

## Starting Hot-Reload Development Server

You may install ibazel for hot-reloading

```
npm run start
```

or

```
ibazel run :vite
```

Otherwise, use the command to run the app without hot-reloading

```
bazel run :vite
```

## Running Type Check

```
npm run type-check
```

## Local environment

If you're running a local application, please make sure the environment variables in `public/config.json` have been set accordingly.

# Project Development Guide

## Table of Contents
- [Project Development Guide](#project-development-guide)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Directory Structures](#directory-structures)
  - [Development Environment Setup](#development-environment-setup)
    - [Prerequisites](#prerequisites)
    - [For using clib ~Setting up Stargate JFrog Artifactory~](#for-using-clib-setting-up-stargate-jfrog-artifactory)
    - [Initial Package Installation](#initial-package-installation)
    - [Installing Development Tools](#installing-development-tools)
    - [Starting the Development Server](#starting-the-development-server)
  - [Testing](#testing)
    - [Running Tests](#running-tests)
    - [Testing strategy](#testing-strategy)
  - [Linting](#linting)
    - [Running Lint Tests](#running-lint-tests)
    - [Auto-fixing Lint Issues](#auto-fixing-lint-issues)
    - [VSCode Automatic Linting and Formatting on Save](#vscode-automatic-linting-and-formatting-on-save)
  - [Adding a Package](#adding-a-package)
    - [Check for Existing Package](#check-for-existing-package)
    - [Add New Package](#add-new-package)
    - [Update BUILD.bazel](#update-buildbazel)
    - [Generate API Clients from OpenAPI Automatically](#generate-api-clients-from-openapi-automatically)
    - [Send requests to the backend server manually](#send-requests-to-the-backend-server-manually)
  - [Appendix](#appendix)

## Introduction

Purpose of this document is starting development.  
The target audience for this document is web front-end developers.

## Directory Structures

The basic directory structure is as follows. It is subject to change with future development.

```
src/
　├ components/ # Reusable components can be added. And You have to add test code.
　│　  ├ __tests__/
　│　  │    └ Button.test.tsx
　│　  │    
　│　  └ Button.tsx
　│　
　├ utils/ # As much as possible, keep logic separate from each page or component.
　│　  ├ __tests__/
　│　  │    └ sum.test.ts
　│　  │   
　│　  └ sum.ts
　│　
　├ auto-generated/ # Generate API Clients tool from OpenAPI Automatically.
　│
　├ hooks/ # Insert custom hooks.
　│　  ├ apis/
　│　  │    ├ apiSettings.ts
　│　  │    ├ index.ts
　│　  │    └ useCallApi.ts
　│　  │    
　│　  └ useSample.ts
　│　
　├ language/
　│　
　├ pages/
　│　
　├ providers/
　│　
　├ routes/
　│　
　├ App.css
　├ App.tsx
　├ const.ts
　├ index.css
　├ main.tsx
　├ vite-env.d.ts
　└ vitest.setup.ts
```

## Development Environment Setup

### Prerequisites

- The following environment must be completed.
  - [How to build an enviroment](https://confluence.tri-ad.tech/display/CISAM/Dev+Environment)

- [The city repository](https://github.com/wp-wcm/city) must be cloned. 
  - For Windows cloned to `"WSL"`.

- Your account must exist in the list below.
  - [terraform account](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/accounts/835215587209/aad/config.auto.tfvars.json)
    - Group name `area-management`
  - [github-ci](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/ci/github/config.auto.tfvars.json)
    - Group name `cis-ac`

### For using clib ~Setting up Stargate JFrog Artifactory~ 

We use [clib](https://github.com/wp-wcm/clib/tree/main) that is aiming to be a centralized storage space for all Woven UI components. We have to set up the JFrog Artifactory for using clib. It needs to be set up according to [this document](https://github.com/wp-wcm/clib/blob/main/react/README.md#setting-up-stargate-jfrog-artifactory).

> [!NOTE]
> If you should be happend authentication failuer, you should edit ```~/.npmrc``` write to city root. Once the authentication is successful, there is no need to save the .npmrc information.


### Initial Package Installation

Ensure `pnpm` is installed on your system. If not, follow the instructions at [pnpm installation guide](https://pnpm.io/installation).

1. Navigate to the root directory of the repository:

    ```sh
    cd path/to/repository
    ```

2. Install the necessary packages:

    ```sh
    pnpm install
    ```

### Installing Development Tools

Install tools required for the `ac-management-ui`.

1. Navigate to the `ac-management-ui` directory:

    ```sh
    cd project/ac-access-control/frontend/ac-management-ui
    ```

2. Install the development tools:

    ```sh
    npm install
    ```

### Starting the Development Server

After successfully installing the necessary packages and development tools, you can start the development server. Follow these steps:

1. Launch the development server by running the following command in the project root directory:

    ```sh
    pnpm dev
    ```

    The development server will start, and you will see output indicating that the server is running.Launch the development server:

2. Open your web browser and enter the following URL:
    - `http://localhost:8080/ac-management-ui`

    After accessing [this URL](http://localhost:8080/ac-management-ui/), you should see the ac-management-ui page displayed in your browser.

The server will automatically restart upon code changes. Refresh the web browser after the server restarts to view changes.

## Testing

### Running Tests

To run tests, follow simple steps:

1. Navigate to the project's root directory:

    ```sh
    cd path/to/project/ac-access-control/frontend/ac-management-ui
    ```

2. Run the tests using the following command:

    ```sh
    pnpm test
    ```

3. Run the following command if you want to output the coverage:

    ```sh
    pnpm test:coverage
    ```

If you want to test only specific test codes, it is recommended to install [the vitest extension for vscode](https://marketplace.visualstudio.com/items?itemName=vitest.explorer).

### Testing strategy

See [TestPolicy](../../../../ac-access-control/frontend/docs/TestPolicy.md)

## Linting

Linting is an essential part of our development process, helping us maintain code quality and adhere to coding standards. We use the linting tool [eslint](https://eslint.org/) for JavaScript and TypeScript files.

### Running Lint Tests

To run lint tests and check your code for style violations, follow these steps:

1. Navigate to the project's root directory:

    ```sh
    cd path/to/project/ac-access-control/frontend/ac-management-ui
    ```

2. Run the lint test using the following command:

    ```sh
    ibazel run :lint-test
    ```

    This command will analyze your code and report any style violations or errors. It's important to run lint tests regularly to catch and fix issues early in the development process.

### Auto-fixing Lint Issues

Many linting issues can be automatically fixed by eslint. To automatically fix these issues, you can use the following command:

```sh
pnpm lint-fix
```

Running this command will attempt to fix linting issues in your codebase where possible. It's a good practice to run this command after running lint tests to apply automatic fixes to your code.

By regularly running lint tests and using the autofix feature, you can ensure that your code adheres to our coding standards and maintains a high level of quality throughout the development process.

### VSCode Automatic Linting and Formatting on Save

You can configure VSCode to automatically perform linting and code formatting when you save a file. To set up this feature, create or edit the `ac-management-ui/.vscode/settings.json` file in your project and add the following content:

```json
{
    "editor.tabSize": 2, // 文字入力行の自動フォーマット有効
	"editor.codeActionsOnSave": {
		"source.fixAll.eslint": "explicit"
	},
	"editor.formatOnType": true,
	"editor.formatOnPaste": true,
	"cSpell.words": ["clib"]
}
```

With these settings in place, VSCode will automatically format your code and fix any ESLint errors whenever you save a file. This helps developers maintain consistent code formatting and improve code quality throughout the development process.

## Adding a Package

Follow these simple steps to add a package to your project using `pnpm`. This process is applicable if you're working from the root of city repository.

### Check for Existing Package

Before adding a new package, verify if it's already installed:

- Inspect the [package.json](https://github.com/wp-wcm/city/blob/main/package.json) file at the root of city repository for the package name.
- If the package is listed there, you can skip next step. You should only update BUILD.bazel

### Add New Package

If the package is not present, use the following command to install it:

```bash
pnpm add <package-name>
```

Replace `<package-name>` with the name of the package you want to add.

### Update BUILD.bazel

After installation, proceed with these steps:

Navigate to the ac-management-ui directory:

```bash
cd projects/ac-access-control/frontend/ac-management-ui
```

Edit the BUILD.bazel file and append the package name to the _DEPS array:

```bazel
_DEPS = [
  # ... existing dependencies ...
  "<package-name>",
  # ... more dependencies ...
]
```

Make sure to replace `<package-name>` with the actual name of the package you've added. After updating the BUILD.bazel file, your package is successfully added and ready for use.

### Generate API Clients from OpenAPI Automatically

Run the following command to generate clients for visitor registration APIs.

```bash
pnpm run generate-api-client
```

- You can configure the generation by editing `openapitools.json`.
- Please DO NOT generate an API client separately. The above command includes post processing after all clients are generated.

### Send requests to the backend server manually

See [the document](../../../backend/README.md#send-a-request-to-the-http-api-on-the-dev-environment)

## Appendix

Bellow link TBD.
- [Ac management API Spec]()
- [UI Design]()

When you have completed your project setup, the following commands will be available:

```sh
pnpm run dev
```

Starts the development server with hot module reloading. Your web app will be running on `http://localhost:8080`, and it will automatically open and reload when saving any changes to your code. 

```sh
pnpm run build
```

Creates an optimized production build of your web app. Note that when using Bazel to run the build process, the output will not show up in your project directory.
<!-- TODO: Add more details here about how to use the output for deployments -->

```sh
pnpm run preview
```

Runs a web server that serves a production build of the app on port 3000. If you'd like to change the port number, you can do so in the `args` option of the preview target in the BUILD file.

```sh
pnpm run typecheck
```

Runs the typecheck via Bazel in the same way as it would in the CI/CD pipeline, so we recommend doing typechecks using this command. If any errors occur, you can view them in the Bazel output folder shown after the run fails.

```sh
pnpm run typecheck:local
```

Uses `tsc` to check your typings. Any errors will be shown in your terminal.

```sh
pnpm run lint
```

Lints code style with ESLint. Any errors will be shown in your terminal.

```sh
pnpm run lint:fix
```

Fixes any linting errors that can be automatically fixed. Any remaining errors or warnings that have to be fixed manually will be shown in your terminal.

```sh
pnpm run buildImage
```

Generates container image in the local environment. If running for the first time, [setup](https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/bazel/#set-up-access-to-artifactory) is required before execution.
After the container image has been generated, it can be started with the following command.
```sh
docker run -it -p 8080:8080 <Image ID>
```



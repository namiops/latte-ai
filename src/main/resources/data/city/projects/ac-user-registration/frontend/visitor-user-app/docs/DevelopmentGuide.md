# Project Development Guide

## Table of Contents
- [Project Development Guide](#project-development-guide)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Development Environment Setup](#development-environment-setup)
    - [Prerequisites](#prerequisites)
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

## Introduction

Purpose of this document is starting development.  
The target audience for this document is web front-end developers.

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

Install tools required for the `visitor-user-app`.

1. Navigate to the `visitor-user-app` directory:

    ```sh
    cd project/ac-user-registration/frontend/visitor-user-app
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
    - `http://localhost:3000/visitor`

    After accessing [this URL](http://localhost:8080/visitor), you should see the visitor page displayed in your browser.

The server will automatically restart upon code changes. Refresh the web browser after the server restarts to view changes.

## Testing

### Running Tests

To run tests, follow simple steps:

1. Navigate to the project's root directory:

    ```sh
    cd path/to/project/ac-user-registration/frontend/visitor-user-app
    ```

2. Install package into the project:
    ```sh
    npm install
    ```

3. Run the tests using the following command:

    ```sh
    pnpm test
    ```

4. Run following command if you want to calculate a test coverage (from /src/components and /src/utils):
     ```sh
     pnpm test:coverage
     ```

### Testing strategy

See [TestPolicy.md](../../../../ac-access-control/frontend/docs/TestPolicy.md)

## Linting

Linting is an essential part of our development process, helping us maintain code quality and adhere to coding standards. We use the linting tool [eslint](https://eslint.org/) for JavaScript and TypeScript files.

### Running Lint Tests

To run lint tests and check your code for style violations, follow these steps:

1. Navigate to the project's root directory:

    ```sh
    cd path/to/project/ac-user-registration/frontend/visitor-user-app
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

You can configure VSCode to automatically perform linting and code formatting when you save a file. To set up this feature, create or edit the `visitor-user-app/.vscode/settings.json` file in your project and add the following content:

```json
{
    "editor.codeActionsOnSave": {
        "source.fixAll.eslint": true // Format using ESLint on file save
    },
    "editor.formatOnType": true,
    "editor.formatOnPaste": true
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

Navigate to the visitor-user-app directory:

```bash
cd projects/ac-user-registration/frontend/visitor-user-app
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

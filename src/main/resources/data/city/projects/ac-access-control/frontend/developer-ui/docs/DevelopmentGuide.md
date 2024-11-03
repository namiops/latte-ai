# Project Development Guide

## Introduction

Purpose of this document is starting development.  
The target audience for this document is web front-end developers.

## Development Environment Setup

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

Install tools required for the `developer-ui`.

1. Navigate to the `developer-ui` directory:

    ```sh
    cd project/ac-access-control/frontend/developer-ui
    ```

2. Install the development tools:

    ```sh
    pnpm install
    ```

### Starting the Development Server

After successfully installing the necessary packages and development tools, you can start the development server. Follow these steps:

1. Launch the development server by running the following command in the project root directory:

    ```sh
    PORT=8080 pnpm dev
    ```

    The development server will start, and you will see output indicating that the server is running.Launch the development server:

2. Open your web browser and enter the following URL:
    - `http://localhost:8080/developer`

    After accessing [this URL](http://localhost:8080/developer), you should see the developer page displayed in your browser.

The server will automatically restart upon code changes. Refresh the web browser after the server restarts to view changes.

## Testing

### Running Tests

To run tests, follow simple steps:

1. Navigate to the project's root directory:

    ```sh
    cd path/to/project/ac-access-control/frontend/developer-ui
    ```

2. Run the tests using the following command:

    ```sh
    pnpm test
    ```

### Testing strategy

See [Testing_strategy.md](./Testing_strategy.md)

## Linting

Linting is an essential part of our development process, helping us maintain code quality and adhere to coding standards. We use the linting tool [eslint](https://eslint.org/) for JavaScript and TypeScript files.

### Running Lint Tests

To run lint tests and check your code for style violations, follow these steps:

1. Navigate to the project's root directory:

    ```sh
    cd path/to/project/ac-access-control/frontend/developer-ui
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

You can configure VSCode to automatically perform linting and code formatting when you save a file. To set up this feature, create or edit the `developer-ui/.vscode/settings.json` file in your project and add the following content:

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

Navigate to the developer-ui directory:

```bash
cd projects/ac-access-control/developer-ui
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

Run the following command to generate API clients. The command will generate clients for all required APIs (Management API, Auth API, Log API).

```bash
pnpm run generate-api-client
```

- You can configure the generation by editing `openapitools.json`.
- Please DO NOT generate an API client separately. The above command includes post processing after all clients are generated.

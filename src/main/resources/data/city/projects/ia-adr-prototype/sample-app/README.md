# README

## How to develop in VSCode

* setup

  * open vscode here (the directory where this readme file is stored)
  * create a symbolic link to the noed_modules managed by the bazel.

    ```bash
    ln -s  ../../../bazel-bin/node_modules ./node_modules
    ```

  * install esbuild (globally)

    ```bash
    npm install -g esbuild
    ```

* run esbuild command

  ```bash
  esbuild --bundle src/index.tsx --outfile=www/bundle.js --watch --servedir=www
  ```

* (before push) run bazel command from project root directory

  ```bash
  bazel run //projects/ia-adr-prototype/sample-app:serve
  ```

# Setup Husky to add pre-commit hook
1. Create ~/.huskyrc file (if not yet exists) and this as the content of the file
   ```
   export ENABLE_LINT_STAGED=1
   ```
1. `bazel run -- @pnpm//:pnpm install -g husky`
1. `bazel run -- @pnpm//:pnpm -C $PWD prepare`

## (Optional) Setup lint-staged.config.cjs
Add `lint-staged.config.cjs` to the nearest directory that you want to run pre-commit hook if the files on the directory is changes. For example

`infrastructure/k8s/lint-staged.config.cjs`
```javascript
module.exports = {
  ...baseConfig,
  "*.+(yaml|yml)": ["bazel run //:gazelle"],
};
```

This will run `bazel run //:gazelle` if the files with `yaml` or `yml` is changed under `infrastructure/k8s` directory.

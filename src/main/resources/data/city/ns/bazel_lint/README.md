# bazel_lint

A ruleset to complement the existing linters at [rules_lint](https://github.com/aspect-build/rules_lint) by
aspect-build. This is not where we register rules for use within the monorepo, but where linters can be setup and
integrated into bazel. Ideally, all custom linters should strive to use the default configurations so IDEs can use
those files to provide live feedback to developers. Please be sure to read the rules_lint repository on guidelines and
goals before implementing a new linter yourself.

*Note: Linters should be registered in the monorepo root [BUILD](/BUILD) and [lint.bzl](/lint.bzl) for
ease-of-use of developers.*

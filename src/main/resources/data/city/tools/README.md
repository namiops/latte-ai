# Tools

## Using rust analyzer (e.g. generate-rust-project.sh)

By default Rust analyzer relies on `cargo` to inspect project. This behavior
can be changed to support other build tools.

In our mono repo we added `//tools/generate-rust-project.sh` to facilitate exactly
that.

### Usage

When starting your development or adding new dependencies just run the script.

It will generate the //rust-project.json (.gitignored) with all the references
needed. From there rust analyzer will rely on the information provided by Bazel.

# third_party

This is the location for the modification, vendoring, or building of external
(third party) code! While most open source projects can just be consumed 
directly, some require extra effort to get working within our monorepo. These
efforts should be stored in this directory. This includes, but is not limited
to: patching, vendoring, and build configurations.

### Directory Structure

```
third_party
│
├── .patches/        # Contains all .patch files for external projects that do 
│   │                  not have their own directory
│   ├─ BUILD         # For exporting the patch files for consumption in bazel
│   └─ *.patch       # A patch file for any external project withour a directory
│
├── openssl          # The directory containing the build for openssl
│   ├── ...          # Any file required to build or vendor the openssl project
│   └── some.patch   # A patch specifically for the openssl project
│
├── <name>           # The name of the external project to vendor/configure
│   ├── ...          # Any file required to build or vendor the <name> project
│   └── some.patch   # A patch specifically for the <name> project
└── ...              # Files for configuring and managing third_party directory
```

#### .patches Directory

All .patch files must be placed here, _unless_ the patch is for an external
project that has its own directory under `third_party`. For example, openssl
has a directory as seen in the above structure, so its .patch file is under the
openssl directory. All `*.patch` files are set to be exported automatically by
the BUILD file in the .patches directory. When adding a new .patch you just
need to place the file there, and you can then properly reference it's label.

The name of the patch MUST be in the form of:
`<external_repository_name>.<short_description>.patch`. The external repository
name MUST be an exact match of the name used for the external project in bazel.
The short description SHOULD contain `_` as word seperators, unless logically
makes sense to use a `-`.

For example, when creating a patch for `github.com/aspect-build/rules_esbuild` 
that adds the binary npm package as a dependency we would name the patch 
something like:

* `<external_repository_name>`
  * `aspect_rules_esbuild`
  * Reason: in BAZEL, we load this external project under the same name
* `<short_description>`
  * `add_bin_packages`
  * Reason: provides a brief hint at what the patch is for

Example usage of this patch:
```bazel
http_archive(
    name = "aspect_rules_esbuild",
    patches = [
        "//third_party/.patches:aspect_rules_esbuild.add_bin_packages.patch",
    ],
    sha256 = "84419868e43c714c0d909dca73039e2f25427fc04f352d2f4f7343ca33f60deb",
    strip_prefix = "rules_esbuild-0.15.3",
    url = "https://github.com/aspect-build/rules_esbuild/releases/download/v0.15.3/rules_esbuild-v0.15.3.tar.gz",
)
```

#### <name> Directories

Any external project that requires heavy lifting, or vendoring can be provided
its own directory under third_party. This is for cases where we can't fully
rely on just loading the external code, but also need to provide custom BUILD
files or other modifications to work with the monorepo. A common use case is
for cc project, e.g. openssl, postgres, which do not work with bazel by 
default. These directories will contain how and where to acquire the source 
code, as well as any required build files, or other requirements to be able to
build the external project.

Another use case is if we want to vendor the external project. For example, we
vendor the bazel protobufs, as these are all defined in bazel's large
repository. We could pull them from this repository, but due to how protobufs
work, it is smaller and more effecient to just vendor the protobuf files 
explicitly, and provide our own BUILD files to specify how to build the 
protobufs for our monorepo's usage.

Any patch files that are tied to an external project with its own <name> 
directory SHOULD be placed in that directroy and not in .patches. This is to 
keep all of the code tied to an external project contained in one location for
easier refactoring, or removal of unused external projects.

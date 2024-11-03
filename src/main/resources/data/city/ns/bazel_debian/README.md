# bazel_debian

A project designed to wrap the distroless team's
[debian_package_manager](https://github.com/GoogleContainerTools/distroless/tree/main/debian_package_manager)
project to simplify the use of pulling and consuming debian packages within our images.

## Using Debian Packages

TODO...

## Adding Debian Packages 

There are three steps when adding a package:

1. [Add the Package to the YAML](#add-the-package-to-the-yaml)
2. [Updating the Lockfile](#updating-the-lockfile)
3. [Upload the Artifact(s) to Artifactory](#upload-the-packagess-to-artifactory)

### Add the Package to the YAML

To add a deb package, you need to add it to the `debian_package.yaml` with the desired distro and arch. Transitive
deb packages are NOT automatically added. This means you must define all packages explicitly. For each entry in the
array, the packages array must not have duplicates. Because of this, it is recommended to make a separate entry for
packages that have many collective transitive dependencies.

It is _your_ discretion on what the best way to group a new package is. If you think it belongs with another set, then
add it with that set. If you think it should be its own entry, give it its own entry. If you are unsure, then always
default to its own entry!

#### Example

In this example we can see three entries:
* sudo
* patch for amd4
* patch for arm64

Each entry defines what packages should be downloaded for a list of distros and archs. In this example, "patch" 
requires a special arch specific package. To handle this, we created a unique entry for patch, one that defines amd64
and the other that defines arm64.

We can also see that "sharedlib" is duplicated across all the entries. This is okay, and it will only be downloaded one
time for each combination of distro and arch that references it. By creating a separate entry for each major package,
it allows us to reference "sharedlib" in each to make it clear to consumers that they _likely_ want to also include
"sharedlib" when add "sudo".

```yaml
# sudo and related packages
- distros: [ "debian11", "debian12" ]
  archs: [ "amd64", "arm64" ]
  packages:
    - "sudo"
    - "sharedlib"

# patch and related amd64 packages
- distros: [ "debian12" ]
  archs: [ "amd64" ]
  packages:
    - "patch"
    - "sharedlib"
    - "shared_amd64_lib"

# patch and related arm64 packages
- distros: [ "debian12" ]
  archs: [ "arm64" ]
  packages:
    - "patch"
    - "sharedlib"
    - "shared_arm64_lib"
```

### Updating the Lockfile

Once you've updated the `debian_package.yaml` you need to update the lockfiles. This can be done by running the
following command:
```shell
bazel run //:debian.generate
```

This will go and fetch the data from the currently used snapshot, and update the lockfiles with any additional packages
that have been added.

### Upload the Packages(s) to Artifactory

Lastly, we do not allow our debian packages to pull from the debian snapshot, as it has very aggressive rate-limiting.
To handle this, we use artifactory to store the packages we need to serve as a mirror. However, artifactory can't 
mirror the official snapshot without manual effort, so we must run a script to upload the artifacts we need. To do this
you can run the command

```shell
bazel run //ns/bazel_debian:upload -- $arch_$distro_$package
```

For example, if we just added a debian12 amd64 and arm64 `sudo` package, we would do:

```shell
bazel run //ns/bazel_debian:upload -- amd64_debian12_sudo
bazel run //ns/bazel_debian:upload -- arm64_debian12_sudo
```

Once, all of your packages have been uploaded, then you can safely use them in your project.

### Updating the Snapshot

If you need to update the snapshot to the latest, then you can run the following command:

```shell
bazel run //:debian.update
```

This is only intended if you must update the snapshot. If updated, this means all artifacts on the new snapshot will
require having their packages uploaded to artifactory, as describe in 
[uploading packages](#upload-the-packagess-to-artifactory) section.

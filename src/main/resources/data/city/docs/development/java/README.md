# Developing in Java

Java support is built in natively to bazel itself. To get started, it is heavily recommended to read the
[Bazel and Java](https://bazel.build/docs/bazel-and-java) documentation by the bazel team. This document may be
referenced by various documents within our monorepo as it contains the core information behind java and bazel.

The monorepo also uses a few additional rules to enable additional features for java support. The libraries related to
java are:

* [rules_java](https://bazel.build/reference/be/java)
    * rules for java that exist natively within bazel
* [rules_jvm_external](https://github.com/bazelbuild/rules_jvm_external)
    * rules for managing maven artifacts
* [rules_jvm](https://github.com/bazel-contrib/rules_jvm)
    * rules for supplemental features (e.g. junit5 support)

## Best Practices

For common best practices within bazel and java, please read the
[Bazel and Java](https://bazel.build/docs/bazel-and-java#best-practices) documentation.

## Build Your Code

When writing your code, we require that BUILD files for java projects are written manually. It is greatly encouraged
that each directory is its own `java_library`. For more details on this, please see
the [Best Practices](#best-practices).

Let's break down an example
[BUILD.bazel file](/projects/helloworld/java/src/main/java/com/example/helloworld/controller/BUILD.bazel):

```bazel
# file: /projects/helloworld/java/src/main/java/com/example/helloworld/controller/BUILD.bazel
package(default_visibility = ["//projects/helloworld/java:__subpackages__"])

java_library(
    name = "controller",
    srcs = glob(["*.java"]),
    deps = [
        "@maven//:org_springframework_spring_web",
    ],
)
```

### Visibility

The first statement on the file is a `package` method to declare the `default_visibility`. If not set, the default
visibility is `//visibility:private`, which prevents any other BUILD file from reference the target. You will likely
want other directories to reference your target, which is why we must set the visibility.

It is recommended to either set the `default_visibility` in the BUILD file, or on every rule set the `visibility`
parameter. For the `helloworld` project, we want to use the value `//projects/helloworld/java:__subpackages__`. This
tells bazel that only BUILD files that exist in the `/projects/helloworld/java` directory, and any subdirectories is
allowed to import our targets with this visibility level.

### java_library

The [Best Practices](#best-practices) goes into further detail for this target, but for a short recap:

* `name` should be the name of the directory the BUILD file is in
* `srcs` should be a non-recursive glob of the java source files, or specifically list the source files

### deps

In your `java_library` target you may need to set dependencies on local targets, or external targets. You should only
list the required dependencies to compile the source files.

Targeting a local library must be done with the full path.
For example, if we had a dependency on another directory in our project, we could use
`///projects/helloworld/java/src/main/java/com/example/helloworld/model/user`. This would load in the
java library found in the BUILD.bazel file named `user` in that directory.

To target a third party dependency, we use the external repository name `@maven`. As seen in the example, we load in the
dependency `@maven//:org_springframework_spring_web`. This dependency references the maven artifact
`org.springframework:spring-web`. However, bazel cannot use the maven syntax, so we must use the format
`<repository>//:<formatted_artifact>`. In our monorepo, the `<repository>` is `@maven`, and a `<formatted_artifact>` is
the full artifact name with special characters replaced with an `_`.

Examples:

* `org.hamcrest:hamcrest` → `@maven//:org_hamcrest_hamcrest`
* `org.junit.jupiter:junit-jupiter-api` → `@maven//:org_junit_jupiter_junit_jupiter_api`
* `org.springframework.boot:spring-boot-test` → `@maven//:org_springframework_boot_spring_boot_test`

#### Explicit vs Transitive Deps

Java can compile if the dependencies your source code depends on are on the transitive path via the deps, but this
is a bad practice to follow in bazel. Instead, we should always be explicit about the dependencies required by your
source code. For example if we have the following:

##### Example Source Code

**A.java**

```java
package com.example;

import com.example.B;
import com.example.C;

// consume both B and C
```

**B.java**

```java
package com.example;

import com.example.C;

// consume C
```

**C.java**

```java
package com.example;

// code without any external dependencies
```

##### Transitive Dependency Example

```bazel
java_library(
  name = "A"
  srcs = ["A.java"],
  deps = [":B"],
)

java_library(
  name = "B"
  srcs = ["B.java"],
  deps = [":C"],
)

java_library(
  name = "C"
  srcs = ["C.java"],
)
```

This transitive dependency example will compile. `A` depends on both `B` and `C`, but only `B` is listed as a
dependency. This works, because `C` is transitively loaded to the classpath as `B` depends on it. This _SHOULD NOT BE
DONE_ in bazel, even though it can compile.

If `B` were to be updated to no longer depend on `C`, it would break `A`'s ability to compile. This is one example
reason as to why it is better to be explicit about your dependencies. Bazel expects each rule to be self-contained and
to not leak information to each other.

However, if `A` did NOT import `C`, then there is no need to declare `C` as a dependency just because `B` depends on it.
The only reason we should add it here is because, `A` itself also depends on `C`!

Instead of using depending on transitive dependencies, we should explicitly declare all of our dependencies.

##### Explicit Dependency Example

```bazel
java_library(
  name = "A"
  srcs = ["A.java"],
  deps = [
    ":B",
    ":C",
  ],
)

java_library(
  name = "B"
  srcs = ["B.java"],
  deps = [":C"],
)

java_library(
  name = "C"
  srcs = ["C.java"],
)
```

In this example, we can see that `A` explicitly states `C` as a dependency. This is how we should declare all
dependencies consumed by the sources of our java rules. Now, in the event that `B` stops depending on `C` our code will
continue to compile, and `A` won't be unnecessarily impacted because it no longer depends on a transitive dependency.

*Note: This is not yet required, but eventually will be when #446 is done. Until this feature is complete, you must be
self diligent on adding all the deps. However, it is not a problem if a dependency is loaded transitively from another
dep, so do not stress about getting this perfect till #446 is completed.*

<!-- TODO(#446): document how to add and verify all explicit dependencies -->

### runtime_deps

In certain circumstances, you may want to specify `runtime_deps` for your library. These dependencies will not be used
at compile time, but will be made available on the classpath if your library is used in a java_binary or java_test rule.
This is particularly useful in frameworks like spring, which load annotations from the classpath.

In the [BUILD.bazel](/projects/helloworld/java/src/main/java/com/example/helloworld/BUILD.bazel) file for the
`Application.java` we have the following java_library definition:

```

java_library(
    name = "helloworld",
    srcs = glob(["*.java"]),
    runtime_deps = [
        "//projects/helloworld/java/src/main/java/com/example/helloworld/controller",
        "@maven//:org_springframework_boot_spring_boot_starter_web",
    ],
    deps = [
        "@maven//:org_springframework_boot_spring_boot",
        "@maven//:org_springframework_boot_spring_boot_autoconfigure",
    ],
)
```

Here, we can see that two runtime_deps are declared. If we run a java test or binary using this library, the spring boot
starter web and our controller library, will be available on the classpath. This is essential for our `helloworld`
library to be able to properly run. If these runtime_dependencies never get declared then we could have unexpected side
effects, such as our URL paths defined in our controller not being available.

## Testing Your Code

Similar to the source code, it is expected that a `BUILD.bazel` exists for every directory in your tests directories.
The biggest change, however, is that the `java_test` rule may only specify a single test class to run. To alleviate this
overhead, we have a utility rule to easily create a test suite for every directory.

Let's look at the example for our Application test:

```
load("@contrib_rules_jvm//java:defs.bzl", "JUNIT5_DEPS", "java_test_suite")

java_test_suite(
    name = "helloworld_tests",
    srcs = glob(["*.java"]),
    runner = "junit5",
    runtime_deps = [
        "@maven//:org_springframework_spring_test",
    ] + JUNIT5_DEPS,
    deps = [
        "//projects/helloworld/java/src/main/java/com/example/helloworld",
        "@maven//:org_junit_jupiter_junit_jupiter_api",
        "@maven//:org_springframework_boot_spring_boot_test",
    ],
)
```

Parameters:

* `name` is recommended to be `<directory-name>_tests`
* `srcs` should be a non-recursive glob on all java files
* `runner` can be set to `junit5` or `junit4` (default)
    * If using `junit5`, be sure to add `JUNIT5_DEPS` as `runtime_deps`!
* `deps` and `runtime_deps` are described in the above [sections](#deps)
    * be aware that certain frameworks, like spring, may require specific `runtime_deps` for tests to run!

`java_test_suite` is a macro that will create a `java_test` rule for every `*Test.java` file in the sources. Any
non-test file will be added to a `java_library` rule and added as a dependency for all the tests rules. For more details
on this rule, please read the [documentation](https://github.com/bazel-contrib/rules_jvm#java_test_suite).

When running your tests, you should typically always run every test, as bazel has strong caching. This would be done via

```shell
bazel test //projects/helloworld/java/...
```

If for some reason you want to avoid running all tests, maybe because you are changing a common package and only want
to rebuild 1 library and test it first, you can use bazel's filter feature. This will be passed to the junit runner
to only run matching tests. For example, we can do:

```shell
bazel test //projects/helloworld/java/... --test_filter=com.example.helloworld.controller.HelloControllerTest#getHello
```

*Note: For simplicity, the suite is the recommended approach, but if you need or wish to configure your java_test rules
by hand, then please be sure to read the [java_test documentation](https://bazel.build/reference/be/java#java_test).*

## Binaries

In java, a binary is created for any class containing a `public static void main` function. The binary will be created
in a BUILD.bazel file adjacent to the java class.

When running gazelle, we should get an binary target that looks something like the following:

```bazel
# file: projects/helloworld/java/src/main/com/example/helloworld/BUILD.bazel

load("@rules_java//java:defs.bzl", "java_library", "java_binary)

java_library(
    name = "helloworld",
    srcs = ["Application.java"],
    visibility = ["//:__subpackages__"],
    deps = [
        "@maven//:org_springframework_boot_spring_boot",
        "@maven//:org_springframework_boot_spring_boot_autoconfigure",
    ],
)

java_binary(
    name = "Application",
    main_class = "com.example.helloworld.Application",
    visibility = ["//visibility:public"],
    runtime_deps = [":helloworld"],
)

```

We can see how a binary and library is created for our code. However, if we tried to run the binary, we would have an
issue with the spring boot server. This is because bazel only loads explicitly what is requested by the targets, and
gazelle has no way to know what runtime dependencies our application needs. In this example, we need to include the
`org.springframework.boot:spring-boot-starter-web` dependency to the runtime_deps. But, we also need to inform gazelle
that we need to keep this change. To do this, we will add the following line to the `runtime_deps` attribute:

```
"@maven//:org_springframework_boot_spring_boot_starter_web",  # keep
```

The runtime_deps attribute tells bazel to include the declared dependencies at runtime on the classpath. The `# keep`
informs gazelle to not remove this line. After our change, the file would look like:

```bazel
# file: projects/helloworld/java/src/main/com/example/helloworld/BUILD.bazel

load("@rules_java//java:defs.bzl", "java_library", "java_binary")

java_library(
    name = "helloworld",
    srcs = ["Application.java"],
    visibility = ["//:__subpackages__"],
    runtime_deps = [
        "@maven//:org_springframework_boot_spring_boot_starter_web",  # keep
    ],
    deps = [
        "@maven//:org_springframework_boot_spring_boot",
        "@maven//:org_springframework_boot_spring_boot_autoconfigure",
    ],
)

java_binary(
    name = "Application",
    main_class = "com.example.helloworld.Application",
    visibility = ["//visibility:public"],
    runtime_deps = [":helloworld"],
)

```

Because the `java_library` runtime dependencies will be included at runtime in any target that depends on it. In this
case that would be the `java_binary` target.

While gazelle does the majority of the heavy lifting for us, in java it is still important we remember to explicitly
declare any runtime dependencies.

#### Aliasing

It is recommended to alias your binary in java projects, as java projects tend to follow a very nested directory
pattern. In the above example, to run our java binary, we'd need to run the following command:

```shell
$ bazel run //projects/helloworld/java/src/main/com/example/helloworld:Application
```

As we can see, this is very long. To improve the developer experience with testing and running your binary locally,
we can create an alias. In the project's root [BUILD.bazel](/projects/helloworld/java/BUILD.bazel) file, we can add an
alias similar to:

```bazel
alias(
  name = "java",  # NOTE: It is recommended to name your "primary" target the same as the directory. 
  actual = "//projects/helloworld/java/src/main/java/com/example/helloworld:Application",
)
```

**Note: We used the `name = "java"` because our directory name is java for the helloworld java example. Be sure to name
your alias the name of your directory! This makes it clear to users that this is the primary target in your package.**

This small change allows us to now run our binary by using the command:

```shell
$ bazel run //projects/helloworld/java
```

### Running our Binary

To run the java binary we can use the alias target or the java_binary target! To use the alias, run the command:

```shell
bazel run //projects/helloworld/java
```

When you are running your java program you may encounter runtime issues, especially with tools like spring, that depend
on various classes being on the classpath. This often occurs when we forget to add required `runtime_deps` to either our
various `java_library` rules, or the `java_binary` rule itself. Bazel requires us to explicitly set what dependencies
most be loaded at either compile or runtime. However, please do not just add every dependency to every rule, as this
removes one of the core advantages of using bazel. Bazel can incrementally build our code, and safely know exactly when
it must rebuild changed code, but only if we set the correct and bare-minimum dependencies!

## Dependency Management

With bazel, our java projects do not use gradle, maven, or any other related tool for dependency management. Instead,
we use a set of rules managed by the bazel team and community called
[rules_jvm_external](https://github.com/bazelbuild/rules_jvm_external). This project provides rules to allow managing
the maven artifact resolution for bazel. This tool still uses maven repositories to acquire the dependencies.

Our core configuration for dependencies is managed in the [/tools/languages/java](/tools/languages/java) directory.
The most important file for managing is the [deps.bzl](/tools/languages/java/deps.bzl). In this file there are two
variables:

### `_ARTIFACTS`

* The list of artifacts to load from the maven repositories
* Value can be
    * A string representation, similar to gradle
    * A complex value using the helper
      method [maven.artifact](https://github.com/bazelbuild/rules_jvm_external/blob/433a989abe1ac26449d48c243c560bef1195939c/specs.bzl#L35)

Example:

```bazel
[
    maven.artifact(
        group="com.google.guava",
        artifact="guava",
        version="27.0-jre",
        exclusions=[
            maven.exclusion(
                group="org.codehaus.mojo",
                artifact="animal-sniffer-annotations"
            ),
            "com.google.j2objc:j2objc-annotations",
        ]
    ),
    "org.springframework.boot:spring-boot-starter-test:2.6.6",
    "org.junit.jupiter:junit-jupiter-engine:5.8.2",
    "org.junit.jupiter:junit-jupiter-api:5.8.2",
    "org.junit.platform:junit-platform-suite-api:1.8.2",
]
```

### `_REPOSITORIES`

* A list of repositories to load artifacts from
* Value can be
    * a string URL
    * A complex value using the helper
      method [maven.repository](https://github.com/bazelbuild/rules_jvm_external/blob/433a989abe1ac26449d48c243c560bef1195939c/specs.bzl#L5)

Example:

```bazel
[
    maven.repository(
        "https://some.private.maven/repo",
        user="johndoe",
        password="example-password"
    ),
    "https://repo1.maven.org/maven2",
]
```

*Warning: do NOT commit passwords! This is purely an example of their helper method. If passwords are required, they
must be injected securely through the CI process. To enable a securely protected repository, please
[contact us](/docs/technical_notes/troubleshooting.md#contact)!*

### Adding/Removing/Updating Dependencies

If you need to adjust the dependencies, then please modify the variables, [_ARTIFACTS](#_artifacts)
and [_REPOSITORIES](#_repositories), as needed. The links in these sections provide the documentation on how to
configure your artifact for more advanced needs.

After making a modification you must run the command:

```shell
bazel run @unpinned_maven//:pin
```

This command updates the [maven_install.json](/tools/languages/java/maven_install.json) with all the metadata required
for bazel to properly pin our artifact versions. This is essential for building a hermetic environment. This metadata
also ensures a faster CI pipeline, as the maven_install rule no longer needs to look up information from the maven
repositories.

### Referencing Your Artifact

As described in the [deps](#deps) section above, our artifacts are available from the external repository `@maven`. To
see which artifacts have been downloaded and are available for use, you can run:

```shell
bazel query "kind(jvm_import, @maven//...)"
```

This command will list all the available artifacts that may be imported as dependencies in your rules! Some example
dependencies:

```
@maven//:org_slf4j_jul_to_slf4j
@maven//:org_slf4j_slf4j_api
@maven//:org_springframework_boot_spring_boot
@maven//:org_springframework_boot_spring_boot_autoconfigure
@maven//:org_springframework_boot_spring_boot_starter
...
```

If you have modified the artifacts, and ran the command to update the pin and do not see your artifact then try to
re-fetch your `@maven` repository. This can be done via the command:

```shell
bazel fetch @maven//...
```

If you have any other issues, do not hesitate to [contact us](/docs/technical_notes/troubleshooting.md#contact)!

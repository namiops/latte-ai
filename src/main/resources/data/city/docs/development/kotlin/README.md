# Developing in Kotlin (jvm)

Unlike Java, Kotlin is not natively supported by bazel. Instead, we take advantage of
[rules_kotlin](https://github.com/bazelbuild/rules_kotlin) to be able to build and test kotlin applications. This guide
will be exclusive to using kotlin in the jvm variant.

#### Javascript and Kotlin

While the kotlin rules are set up to support both JS and Android, at the time of writing, the monorepo cannot guarantee
support on our side.. Please feel free to try to use these additional rules to build your JS or android app with kotlin,
but be aware problems may be encountered! As always, if you need support for Kotlin JS or Kotlin Android, please
[contact us](/docs/technical_notes/troubleshooting.md#contact)!

### Getting Started

Kotlin is interoperable with Java, which means the kotlin rules are heavily similar to the java rules. It is heavily
recommended to read the [Developing with Java](../java/README.md) guide to get started. To reduce duplication, this
guide will not go into as much detail about building or testing. Instead, only the core differences will be covered, as
well as some kotlin specific information.

#### Best Practices

Please follow the [Bazel and Java](https://bazel.build/docs/bazel-and-java#best-practices) documentation for best
practices. While this document is about java, the kotlin jvm rules function very similarly to java rules.

### Example Project

For an example project see the [HelloWorld service](/projects/helloworld/kotlin).

## rules_java vs rules_kotlin

The rules_kotlin [documentation](https://github.com/bazelbuild/rules_kotlin/blob/master/docs/kotlin.md#kt_jvm_binary)
details the specific labels available for the kotlin targets. For jvm projects, make sure to read the documentation for
the `kt_jvm_*` rules.

### Rules

At a basic level, `kt_jvm_*` rules can replace the `java_*` rules used in the [Developing with Java](../java/README.md)
documentation guide. But for short, the base comparison is:

* `java_binary` → `kt_jvm_binary`
* `java_library` → `kt_jvm_library`

### Srcs & Deps

When compiling kotlin code, it is required to use the `kt_jvm_*` rules. However, one special trait to the kotlin rules,
is they support both java and kotlin sources and dependencies. If necessary it is possible to mix the sources and
configure the compilation accordingly with these rules. This is an advanced use case though, so be sure to read the
rule's documentation!

#### Explicit vs Transitive Deps

As described in the [java docs](../java/README.md#explicit-vs-transitive-deps), kotlin should also be explicit about its
dependencies.

*Note: This is not yet required, but eventually will be when #446 is done. Until this feature is complete, you must be
self diligent on adding all the deps. However, it is not a problem if a dependency is loaded transitively from another
dep, so do not stress about getting this perfect till #446 is completed.*

<!-- TODO(#446): document how to add and verify all explicit dependencies -->

### kt_jvm_* rule outputs with java_* rules

The output of kotlin rules is supported as deps for `java_*` rules. This allows you to write libraries in a kotlin
library, but consume this library in a java project. This also allows us to take advantage of creating a java_binary
from a kotlin library.

In the HelloWorld example root [BUILD.bazel](/projects/helloworld/kotlin/BUILD.bazel) file, we showcase this by using
`java_image` to create our jvm docker image. This macro also creates a target called `server.binary` which is
a `java_binary` rule. In this example, we see how it is possible to use the java_* rules mixed with the kotlin rules
when we don't need any kotlin specific functionality!

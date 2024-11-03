# Customized citrus-api jar
### Why this customized jar is needed
In the cucumber project, we initially used a third-party citrus framework jar from Maven, which had a special logic for loading source files that conflicted with our Bazel rule, causing issues with Docker execution. To resolve this, we modified the special logic to build this customized jar.

### Changes made to the customized jar
The `if` block in this code was commented out: https://github.com/citrusframework/citrus/blob/main/core/citrus-api/src/main/java/org/citrusframework/spi/ResourcePathTypeResolver.java#L233

### How to use this customized jar
Use `java_import` to import this customized jar into the execution environment.


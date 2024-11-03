# (Just for reference) Iceberg Rest Catalog

This Iceberg Rest Catalog was replaced by Hive Metastore.

Read the O'reilly book for more details about catalogs:

- [5. Iceberg Catalogs | Apache Iceberg: The Definitive Guide](https://learning.oreilly.com/library/view/apache-iceberg-the/9781098148614/ch05.html#introduction)

(We can use [Woven by Toyota - Dojo](https://sites.google.com/tri-ad.global/tri-ad-intranet/global-content/dojo) to read
the O'reilly book)

This directory is kept just for reference.

If you want to use this, you need to create bazel `BUILD` like the following:

```BUILD.bazel
load("//ns/postgres-operator/bazel:postgrescluster_build.bzl", "LAB2_CHART", "postgrescluster_build")

postgrescluster_build(
    name = "iceberg-rest-catalog-db",
    chart = LAB2_CHART,
    namespace = "data-platform-demo",
    values_file = "postgres-values.yaml",
)

filegroup(
    name = "files",
    srcs = glob(["**/*.yaml"]),
    visibility = ["//visibility:public"],
)
```

# New Postgres instance from a template

We noticed that most postgres yaml files end up looking very similar. This is because for Postgres to work correctly in Agora the instance needs to be configured specifically. For your convenience, we came up with a way to generate the files for you using `Bazel`, `Helm Chart`, and `Zebra`.

## Preparing the Files
You will need two files to get this to work.

!!!Note
    Make sure to keep these files in your repository. These files will be the source of truth for your database.

### BUILD

Add your project path to `infrastructure/k8s/(local|dev|lab|common)/zebra_files.bzl` so that it helps the manifest validation processes in our CI pipeline to correctly get all manifests with the Bazel `glob` function. [For more details](https://developer.woven-city.toyota/docs/default/component/zebra-service/03_outputs/#a-note-on-the-infrastructure-directories).

```
ZEBRA_FILES = [
    "//infrastructure/k8s/common/YOUR_PROJECT:files",
]
```

Create a BUILD file where you want your Postgres to exist.

```
load("//ns/postgres-operator/bazel:postgrescluster_build.bzl", "postgrescluster_build")

postgrescluster_build(
    name = "postgres",
    namespace = "$\\{namespace\\}",
    values_file = "postgres-values.yaml",
)

filegroup(
    name = "files",
    srcs = glob(["**/*.yaml"]),
    visibility = ["//visibility:public"],
)
```

### Values

Next, create a `postgres-values.yaml` file. You can have a look at the value list [here](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/agora-postgres-cluster).

Here is a minimal example.

```yaml
---
name: <your-db-name>
storageClass: postgresql-sample-postgresql
```

As you can see it will use defaults for everything else which in most cases is enough.

!!!Note
    Please use the StorageClass `postgresql-sample-postgresql` for the database as a temporary solution for now.
    StorageClass represents disk-related configurations, such as ones for EBS volumes, for PersistentVolumes,
    and we plan to support creating a dedicated StorageClass for each PostgreSQL cluster to enable teams with different use cases.
    But we need more time to prepare because StorageClass is a cluster-wide resource 
    and having an appropriate way to control the creation of it is necessary to protect all applications inside our cluster.
    Until we are ready, you can use the shared StorageClass `postgresql-sample-postgresql`.

## Generating the database yaml files
After you create the `BUILD` and `values.yaml` files you need to generate the database file from these. You can do it using two different ways.

### Using Pipeline and Zebra

!!!Note
    The initial build failing is the expected behavior!

Once you push the files and open a PR you will notice that your build fails. This is the expected behavior. The failure will trigger the zebra bot that will generate the missing files for you and will push them to your branch.

## Creating it Manually 
You can also manually create the files by running the `bazel` command.

```
bazel run //{path to your directory}:postgres.copy
```

Then push the generated files to your branch.

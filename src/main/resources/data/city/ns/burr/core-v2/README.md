# BURR Core v2 Service

[[Atlas](http://go/consent-burr-atlas)]
[[Service Page](../../service-page/docs/burr/README.md)]
[[API](../../brr/api/core-v2.yaml)]
[[BURR v1](../../brr)]

## Local development tips

### Set up local DB docker container

For local development, you can run a Postgres docker container. The BURR Core v2
service can use this when you're running the service, and the SeaORM CLI tool
can also use it for migrations etc.

```shell
# either directly from docker hub:
docker run --name postgres-burr -e POSTGRES_PASSWORD=pg1234 -e POSTGRES_DB=core_v2_alpha -d -p 5432:5432 postgres:14.5

# or alternatively, from Artifactory (on office network / VPN):
docker run --name postgres-burr -e POSTGRES_PASSWORD=pg1234 -e POSTGRES_DB=core_v2_alpha -d -p 5432:5432 docker.artifactory-ha.tri-ad.tech/postgres:14.5
```

### Set up OpenTelemetry container

Traces are automatically sent to the OpenTelemetry collector whose endpoint is defined in config.
As default, the endpoint is configured as `http://127.0.0.1:4318` and this can cause some errors which you can see in logs.

You can run the OpenTelemetry collector by executing the following command.

```shell
docker run -d -p4318:4318 -p16686:16686 jaegertracing/all-in-one:latest
```

This also provides a great UI to see traces, you can access `http://localhost:16686/` after the container started.

### Running the service
```shell
bazel run //ns/burr/core-v2:core_v2
```

Send POST requests to `http://localhost:3000/core/v2alpha/persons` with JSON request body and try [BURR v2 APIs](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition)

## Database migrations

### How to create a new migration

* In `src/migration`, copy the newest file, and give it a name with the current
  timestamp and a short, descriptive name, i.e. following the pattern 
  `mYYYYMMDD_hhmmss_short_title.rs`.
* Implement your migration in the new file.
  * **Important:** Remember to implement **both** the `up()` function **and** 
    the `down()` function!
  * Don't import other parts of the code (no other migrations, no models, no 
    constants), except for `sea_orm_migration::prelude::*` that is already
    imported. Migrations must be stable over time even when our other code 
    changes.
* Register your migration in our Migrator: In `src/migration.rs`, 
  declare your new migration module (`mod mYYYYMMDD_hhmmss_short_title`), and
  append its `Migration` struct to the list in the `migrations()` function.
* Apply your migration:

  ```shell
  bazel run //ns/burr/core-v2:migration_runner -- -u postgres://postgres:pg1234@localhost:5432/core_v2_alpha
  ```

* (Re)Generate the SeaORM entity definitions (see below)

## How to generate seaorm entity definition

The entities can be generated from database schema running on your local.

When doing this the first time, install sea-orm-cli with cargo.

As of 2024-03-19, sea-orm-cli the latest version of sea-orm-cli has a bug that makes it fail to generate entities from our schema (see [this thread](https://woven-by-toyota.slack.com/archives/C06230AUVSS/p1710813789095239?thread_ts=1710808998.731909&cid=C06230AUVSS) for discussion and research).

For the moment, we need to use version 0.12.6 and use the `--locked` option when installing to make Cargo use the exact sea-schema library version that was used at time of publishing:

```shell
cargo install --force --locked sea-orm-cli@0.12.6
```

To (re)generate the seaorm entities:

* Make sure that the DB docker containers is running with the credentials shown above.
* Make sure you have applied your migration (see above).
* Run the re-generator script:

  ```shell
  ns/burr/core-v2/scripts/generate_seaorm_entities.sh
  ```
  
## Print DB schema for your information

It can be helpful to print out the DB schema as it currently exists in your Postgres docker container, to understand the effect that migrations have, or to show it to other people.

We have a script that will print the schema of all tables using the `psql` tool inside the docker container. Just run:

```shell
ns/burr/core-v2/scripts/print_db_schema.sh
```

## Generic timestamps in tables

Every table has two timestamp columns for the operation purpose, `created_at` and `updated_at`. Unfortunately, sea-orm doesn't offer a feature to manage it, so we have to implement it on our side. We have an [extension](https://github.com/wp-wcm/city/blob/main/ns/burr/core-v2/src/store/modelext.rs) to support it. Make sure that you've added `impl_timestamps!` line for the new `ActiveModel` after creating a new table.

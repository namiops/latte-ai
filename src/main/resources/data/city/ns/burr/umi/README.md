# BURR User Managed Info Service

[[API](../../brr/api/umi.yaml)]

## Local development tips

### Set up local DB docker container

For local development, you can run a Postgres docker container. The BURR Core v2
service can use this when you're running the service, and the SeaORM CLI tool
can also use it for migrations etc.

```shell
# either directly from docker hub:
docker run --name postgres-umi -e POSTGRES_PASSWORD=pg1234 -e POSTGRES_DB=umi -d -p 5432:5432 postgres:14.5

# or alternatively, from Artifactory (on office network / VPN):
docker run --name postgres-umi -e POSTGRES_PASSWORD=pg1234 -e POSTGRES_DB=umi -d -p 5432:5432 docker.artifactory-ha.tri-ad.tech/postgres:14.5
```

## Database migration
1. In `src/migration`, copy the newest file and give it a name with the current timestamp and a short, descriptive name, i.e. following the pattern `mYYYYMMDD_hhmmss_short_title.rs`.
2. Implement your migration in the new file.
   - **Important**: Remember to implement both the `up()` function and the `down() function!
   - Don't import other parts of the code (no other migrations, no models, no constants), except for  `sea_orm_migration::prelude::*` that is already imported. Migrations must be stable over time even when our other code changes. 
3. Register your migration in our Migrator: In `src/migration.rs`. declare your new migration module (`mod mYYYYMMDD_hhmmss_short_title`), and append its `Migration` struct to the list in the `migrations()` function.
4. Apply your migration.
   ```shell
   bazel run //ns/burr/umi:migration_runner -- -u postgres://postgres:pg1234@localhost:5432/umi
   ```
5. (Re)Generate the SeaORM entity definitions. (see below)

## How to generate SeaOrm entity definition
The entities can be generated from database schema running on your local. When doing this the first time, install sea-orm-cli with cargo.

As of 2024-03-19, the latest version of sea-orm-cli has a bug that makes it fail to generate entities from our schema (see [this thread](https://woven-by-toyota.slack.com/archives/C06230AUVSS/p1710813789095239?thread_ts=1710808998.731909&cid=C06230AUVSS) for discussion and research).

For the moment, we need to use version 0.12.6 and use the `--locked` option when installing to make Cargo use the exact sea-schema library version that was used at time of publishing:

```shell
cargo install --force --locked sea-orm-cli@0.12.6
```

To (re)generate the SeaOrm entities:

* Make sure that the DB docker containers is running with the credentials shown above.
* Make sure you have applied your migration (see above).
* Run the re-generator script:
  ```shell
  ns/burr/umi/scripts/generate_seaorm_entities.sh
  ```

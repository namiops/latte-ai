# Consent Service

[[Atlas](http://go/consent-burr-atlas)]
[[Service Page](../../service-page/docs/consent/README.md)]

This is the Agora Consent Service.

## Directory Structure

This is directory is the root of the consent service source code, but also
houses other sub-projects in some directories. The structure is as follows:

- [**.cargo**](./.cargo) - Cargo configuration for local development for consent service
- [**api**](./api/README.md) - Consent service OpenAPI spec
- [**deployment_test**](./deployment_test/README.md) - testkube test for consent service
- [**middleware**](./middleware/README.md) - Consent check middleware, by language
- [**migrations**](./migrations) - Consent service DB migrations
- [**proto**](./proto) - Consent service gRPC API definition
- [**sidecar**](./sidecar/README.md) - Reverse proxy that enforces consent when retrieving data from resource server
- [**src**](./src) - Main consent service source code
- [**tests**](./tests) - Consent service integration tests

## Architecture Overview

Our architecture diagrams are currently maintained in [this Lucid file](https://lucid.app/lucidchart/f979117e-a2c0-4cfa-b71d-171a40731862/edit?invitationId=inv_792b4cae-ed58-44f8-9bd3-42e117c44179&page=.aiMSmt5o9rJ#).

### Internal Architecture Diagram

![Internal Architecture Diagram](docs/consent-system-internal-architecture.png)

([Diagram source in Lucid](https://lucid.app/lucidchart/f979117e-a2c0-4cfa-b71d-171a40731862/edit?invitationId=inv_792b4cae-ed58-44f8-9bd3-42e117c44179&page=.aiMSmt5o9rJ#))

### Interface Usage Overview

![Interface Usage Overview](docs/consent-system-interface-usage-overview.png)

([Diagram source in Lucid](https://lucid.app/lucidchart/f979117e-a2c0-4cfa-b71d-171a40731862/edit?invitationId=inv_792b4cae-ed58-44f8-9bd3-42e117c44179&page=x-NvQ8c29GJV#))

## Random dev notes

### Dev Env Setup & prerequisites

The project can be built with either Bazel or Cargo.

Requirements:

- [On macOS] Xcode must be installed, see the [official Agora docs](../../../docs/agora_developers/development_environment/02_setting_up_agora_dev_env.md#for-mac)

- [Only if building with Cargo] Recent Protobuf compiler (v23 or later)
  - [On macOS] Install with `brew install protobuf`
  - [On linux]
    - If your package manager contains a recent version (v23 or later), you can use that (run
      `protoc --version` to check).
    - Otherwise, you can install it manually like this:

      <!-- Adapted from https://lindevs.com/install-protoc-on-ubuntu  -->

      ```shell
      sudo apt update
      sudo apt purge protobuf-compiler
      sudo apt install -y unzip
      PROTOC_VERSION=23.4
      PROTOC_FILE="protoc-${PROTOC_VERSION}-linux-x86_64.zip"
      wget "https://github.com/protocolbuffers/protobuf/releases/download/v${PROTOC_VERSION}/${PROTOC_FILE}"
      sudo unzip -q "${PROTOC_FILE}" bin/protoc -d /usr/local
      sudo chmod a+x /usr/local/bin/protoc
      ```

      Then run `protoc --version` to check to make sure it works.

- [Only if building with Cargo on macOS] *Homebrew version* of `libpq`. Install with `brew install libpq`

- Diesel CLI for migration. Install with `cargo install diesel_cli --no-default-features --features postgres`

### Code Style

- In general, follow [Rust's Style Guide](https://doc.rust-lang.org/style-guide/).
  - Use `rustfmt` to help with this, see [the rustfmt section below](#rustfmt).

- Follow [our style guide](./STYLE_GUIDE.md).

- Be consistent with the surrounding code.

- If you're unsure, do what feels right to you, and get feedback from the team (e.g. in Slack, in PR).

### Patterns

#### Domain Types with Newtype Pattern and Validation

Use the following pattern to implement a domain type using the 
[newtype idiom](https://doc.rust-lang.org/rust-by-example/generics/new_types.html),
making it usable both with Diesel in DB types/queries and in REST API types.

```rust
use diesel_derive_newtype::DieselNewType;
use serde::{Deserialize, Serialize};

#[derive(Clone, Debug, Deserialize, DieselNewType, Eq, Hash, derive_more::Into, PartialEq, Serialize)]
#[into(owned, ref)] // also generate impl From<&UserId> for &String (ref of inner type)
#[serde(try_from = "String", into = "String")] // use the inner type as value for 'try_from' and 'into' here
// use an appropriate name for your domain type
pub struct UserId(String);

impl UserId {
    // add a function like this to provide example instances for the REST API docs 
    // (adjust as needed / leave out if not needed) 
    pub fn example() -> Self {
        Self("12345".to_string())
    }
}

impl TryFrom<String> for UserId {
    type Error = UserIdError;

    // Use this conversion method to implement any validation logic that applies
    // to your domain type. Add appropriate error variants below.
    fn try_from(value: String) -> std::result::Result<Self, Self::Error> {
        if value.is_empty() {
            return Err(UserIdError::Empty);
        }

        Ok(Self(value.to_string()))
    }
}

#[derive(Debug, derive_more::Display, derive_more::Error)]
// name this type '<domain type name>Error'
pub enum UserIdError {
    // add enum variants to represent your validation error cases here
    #[display(fmt = "Empty user ID is invalid")]
    Empty,
}
```

To use the domain type in the REST API, you also need to manually add the new 
domain type in the `schemas()` list in `oapi_generator.rs`, and also add a 
`ToSchema` implementation for utoipa (detailed schema attributes currently cannot
be derived by utoipa attributes).

You can find more info about implementing `ToSchema` manually in its docs 
[here](https://docs.rs/utoipa/3.3.0/utoipa/trait.ToSchema.html), but here's also
a small example for a string that has a `minLength` specification:

```rust
impl<'a> ToSchema<'a> for UserId {
    fn schema() -> (&'a str, RefOr<Schema>) {
        (
            "UserId",
            ObjectBuilder::new()
                .schema_type(SchemaType::String)
                .min_length(Some(1))
                .into(),
        )
    }
}
```

#### Evolving the database schema with the Expand-Migrate-Contract Pattern

As our software evolves, changes to the DB schema sometimes become necessary. Since our service is the only thing that interacts with our DB, we have more control over the schema and how to change it, but we still need to be careful to make changes in a way that is correct, safe, and does not require or cause downtime.

We can use the _expand-migrate-contract_ pattern to evolve the DB schema in such a way. This pattern helps break down the schema change into smaller steps of code and schema changes in such a way that each step is backwards compatible, thus minimizing the risk of issues while allowing us to apply the steps without downtime.

Some good articles about this pattern are [here by PlanetScale](https://planetscale.com/blog/backward-compatible-databases-changes) and [here by Prisma ORM](https://www.prisma.io/dataguide/types/relational/expand-and-contract-pattern).  
For an example of how we applied this pattern in the past, see task [CONSENT-143](https://wovencitymanagement.atlassian.net/browse/CONSENT-143).

The general goal here is that each database schema change is done in such a way that both the currently live software version and the next upcoming software version can work with that schema. That way, in case a software update / deployment fails, the version that is already live can continue to operate.

##### But doesn't k8s protect us from errors during rollout?

Kubernetes offers us zero-downtime deployment of updates, and it does this by starting a new pod with the updated image while the old pods are still active and handling all the traffic. The new pod will only start receiving incoming traffic once it has become "healthy" and kubernetes adjusts the load balancing away from the old pods and to the new one.

For issues in the application code that prevent the new pod from becoming healthy, this prevents such a broken update to go into actual usage. That pod will just keep being restarted and hopefully our alert metrics will notify us that something is wrong.

If a database migration fails to apply during the startup of a new pod, this causes the DB transaction to be rolled back and the service to terminate with an error. In this case, the database contents remain untouched and the old pods can continue to operate as usual.

However, there is a small chance or the DB migration getting applied successfully (transaction committed), and the new pod _then_ failing to become healthy. In this scenario, the database schema has already been migrated, but the old pod continues to run and receive traffic. If the DB schema is changed in an incompatible way, the old pod will fail to interact with the DB, and there will be a (partial) service outage.

Thus, we need to take care to evolve the schema in incrementally backward compatible steps even in our k8s environment. 

##### Basic Steps of the Expand-Migrate-Contract Pattern

In general, each schema change is broken down into the following six steps:

<!-- 
Markdown table doesn't look very readable with these long cell contents,
but an HTML table is slightly nicer
-->
<table>
<thead>
<tr>
  <th>#</th>
  <th width="50%">Database schema</th>
  <th width="50%">Application code/logic</th>
</tr>
</thead>
<tbody>
<tr>
  <th>1</th>
  <td>Add new column or table to existing schema</td>
  <td></td>
</tr>
<tr>
  <th>2</th>
  <td></td>
  <td>Update the code to write both the old and new schema (but still read only from the old schema)</td>
</tr>
<tr>
  <th>3</th>
  <td>Migrate the existing data from the old schema to the new schema</td>
  <td></td>
</tr>
<tr>
  <th>4</th>
  <td></td>
  <td>Update the code to read from the new schema</td>
</tr>
<tr>
  <th>5</th>
  <td></td>
  <td>Update the code to write only to the new schema</td>
</tr>
<tr>
  <th>6</th>
  <td>Once verified, remove the old schema</td>
  <td></td>
</tr>
</tbody>
</table>

For more details about each step, see the articles linked above or search for more resources online.

##### Applying the pattern in our environment

For Consent Service, we apply DB migrations during the startup of the service itself, which means that we always deploy DB migrations with our regular update mechanism (the same as code changes). As described above, our updates are rolled out by Kubernetes, and so the DB migrations are applied before the rest of the service starts up and other application logic changes take effect. If a DB migration fails, any code changes that accompany it won't go live.

**Note:** Of course, there is _some_ application code that runs before and during the application of migrations. If any of _that_ code changes, this might affect the migrations. But this should be a very rare case.

This means that we _can_ combine two consecutive steps of the expand-migrate-contract pattern if it's a DB schema change first and an application logic change second (but we don't have to).

Specifically, we can deploy the following pairs of steps as one instead of two updates:

- steps 1 and 2:
  - Add new column or table to existing schema
  - Update the code to write both the old and new schema (but still read only from the old schema)
- steps 3 and 4:
  - Migrate the existing data from the old schema to the new schema
  - Update the code to read from the new schema

### Some useful commands

#### Running the service (cargo or bazel)

```shell
cargo run
bazel run //ns/privacy/consent
```

> [!NOTE]
> Run the following to check the available command line arguments
>
> ```bash
> cargo run -- --help
> bazel run //ns/privacy/consent -- --help
> ```

By default, the service tries to connect to PG on
`postgres://postgres:password@localhost:5432/consent`. You can use this command
to run a PG instance the service will connect to:

```shell
# either directly from docker hub:
docker run --name postgres-consent -e POSTGRES_PASSWORD=password -e POSTGRES_DB=consent -d -p 5432:5432 postgres:14.7

# or alternatively, from Artifactory (on office network / VPN):
docker run --name postgres-consent -e POSTGRES_PASSWORD=password -e POSTGRES_DB=consent -d -p 5432:5432 docker.artifactory-ha.tri-ad.tech/postgres:14.7
```

#### Running tests

Tests can be run using either `cargo test` from the consent root, or `bazel test //ns/privacy/consent:all`.

Integration tests will spawn a new PG container for to run the test against.
To preserve the container and the test schemas after the test finishes, set
`CONSENT_TEST_KEEP_DB` to any non-empty value.

NOTE: To pass env variables to Bazel, use `--test_env=CONSENT_TEST_KEEP_DB=<URL>`
as Bazel tests ignore the current environment.

#### Build with clippy

Clippy is now enabled for targets in this package, so you don't have to run
this manually.

```shell
# bazel
bazel build //ns/privacy/consent:clippy
# or, cargo
cargo clippy
```

#### Rustfmt

From the consent root, run

```shell
cargo +nightly fmt
```

NOTE: The Bazel `rustfmt` target that runs in CI does not (currently?) take into
account our `rustfmt.toml`, so this needs to be done manually before pushing!

#### Generating the API schema

The schema is now automatically generated using zebra, so if you
forget it will be synced by CI. Still, to generate it yourself, you can use

```shell
bazel run //ns/privacy/consent/api:generate_v3alpha.copy
```

#### Diesel migrations

Set `DATABASE_URL=postgres://postgres:password@localhost:5432/consent`.

Whenever your PR requires a schema change, create a new migration using:

```shell
diesel migration generate <migration_name>
```

To test migrations locally, you can use `diesel migration run/revert/redo` to
apply, rollback or rollback+apply migrations locally.

Before writing code, make sure to apply your migration (`run` or `redo`) to
update `src/schema.rs`.

#### Print DB schema for your information

It can be helpful to print out the DB schema as it currently exists in your
Postgres docker container, to understand the effect that migrations have, or to
show it to other people.

We have a script that will print the schema of all tables using the `psql` tool
inside the docker container. Just run:

```shell
ns/privacy/consent/scripts/print_db_schema.sh
```

#### Regenerating REST client (for tests)

Zebra should do this for us in CI, but in case of compilation failures (rather
than test failures) it might not. You may also want to run it manually 
to save time:

```shell
bazel run //ns/privacy/consent/clients/rustv3alpha:generate_consent-v3alpha_client.copy
```

#### List the container images deployed to all environments

If you want to check which images are currently deployed where (e.g. to confirm
whether you've deployed an update to all environments), you can use the script
`list_deployed_consent_images.sh` in the `scripts` directory:

```shell
> ./scripts/list_deployed_consent_images.sh

Consent Service (Namespace: consent)
  Cluster dev:                      docker.artifactory-ha.tri-ad.tech/wcm-cityos/privacy/consent:main-631f37043cb-1703227685
  Cluster lab:                      docker.artifactory-ha.tri-ad.tech/wcm-cityos/privacy/consent:main-631f37043cb-1703227685
  . . . 

Testkube test executor: (Namespace: testkube)
  Cluster dev:                      docker.artifactory-ha.tri-ad.tech/wcm-cityos/privacy/consent/deployment-test:main-dd607d1f6ab-1703118033
  Cluster lab:                      docker.artifactory-ha.tri-ad.tech/wcm-cityos/privacy/consent/deployment-test:main-dd607d1f6ab-1703118033
  . . .
```

### Additional Dev Env Tips

#### Auto-Rustfmt in JetBrains IDEs

If you use a JetBrains IDE with Rust integration (e.g. RustRover, IntelliJ IDEA Ultimate, etc.), you can have the IDE automatically run `rustfmt` for you. This makes it very easy and convenient to always have consistently formatted code.

- Open the IDE settings (<kbd>Cmd</kbd>+<kbd>,</kbd>)
- Search for "rustfmt", and open the resulting page: *Languages & Frameworks > Rust > Rustfmt*
- Leave the inputs for *Additional arguments* and for *Environment variables* empty
- Set *Channel* to "nightly" (1)
- Check both checkboxes to use Rustfmt and to auto-format on save (2)

![](docs/intellij-rustfmt-config.png)

### Build info included in the binary (Bazel Stamping)

For diagnostic and debugging purposes, we include some information in the Consent Service binary about the build that produced that binary.

- This information is injected _at compile time_ via environment variables, which we let Bazel set using the `rustc_env` argument on our `rust_binary` target.

  - When _stamping_ is enabled, Bazel preprocesses the content of those env files and replaces certain placeholders with workspace status values (see [Bazel docs][bazel-docs-stamping]).

    - Stamping is enabled in our CI pipeline for builds that will be deployed (i.e. on the `main` branch and for deployment previews), you can find the respective scripts filling the workspace status in the [scripts](../../../scripts) directory in the workspace root.
    - You can manually run a build with stamping enabled locally by passing the `--stamp` argument and setting a workspace status script to Bazel. For example to build and run the Consent Service:

       ```sh
      bazel run //ns/privacy/consent:consent --stamp --workspace_status_command=scripts/workspace-status.release.sh
      ```
      
  - When stamping is not enabled (default for most Bazel builds), Bazel uses the env file contents unprocessed.

  - When building with Cargo instead of Bazel, the env vars are not set at all.

- In our `main.rs` file, we read the compile-time env vars using the `option_env!` macro ([docs][rust-option-env]).

  - If the env vars are not set, or if we detect the unprocessed Bazel placeholders, we fall back to the string "(unset)" instead.

- Then we put the information into the `BuildInfo` struct, which can be passed around and included in log context to provide diagnostic information.

[bazel-docs-stamping]: https://bazel.build/docs/user-manual#workspace-status
[rust-option-env]: https://doc.rust-lang.org/std/macro.option_env.html

## Talking to the servers: 

Install `grpcurl` and `httpie`:
```shell
brew install grpcurl httpie
```

```
# check that service is up and running
❯ http :3000/readyz
HTTP/1.1 200 OK
content-length: 0
date: Wed, 14 Jun 2023 07:01:10 GMT

# create a service + client (using httpie with POST + body)
❯ http POST ':3000/v2alpha/admin/service_mapping' \
      --raw='{
        "service_name": "My Service",
        "clients": [{"client_id": "my_client"}]
      }'
HTTP/1.1 204 No Content
date: Tue, 26 Sep 2023 00:43:11 GMT

# check consent with REST API
❯ http ":3000/v2alpha/be/check_consent?client=my_client&dataattrs=attr1&dataattrs=attr2&user=tal"
HTTP/1.1 200 OK
content-length: 4
content-type: text/plain; charset=utf-8
date: Fri, 02 Jun 2023 05:40:31 GMT
{
    "status": "CONSENT_NOT_GRANTED"
}

# check consent with gRPC API
❯ grpcurl -plaintext -proto proto/v0/consent_service.proto \
      -d '{"user":"tal", "client":"my_client", "data_attrs":["financial", "health"]}' \
      localhost:3001 agora.consent.v0.Consent/CheckConsent
{
  "status": "CONSENT_NOT_GRANTED"
}

#### Connecting to "prod" database

Start by verifying your k8s context is pointing at the right context.

Now, get the name of a PG db instance pod, and store it in an env var:

```shell
export CONSENT_DB_INSTANCE_POD_NAME=$(kubectl get pods -n consent | grep instance | head -1 | cut -f 1 -d ' ')
```

Next, get the database, host, username and password from PG user secret, and
store those in env vars as well:

```shell
export CONSENT_DB_DBNAME=$(kubectl get secret -n consent consent-db-pguser-consent -o jsonpath='{.data.dbname}' | base64 -d)
export CONSENT_DB_HOST=$(kubectl get secret -n consent consent-db-pguser-consent -o jsonpath='{.data.host}' | base64 -d)
export CONSENT_DB_USER=$(kubectl get secret -n consent consent-db-pguser-consent -o jsonpath='{.data.user}' | base64 -d)
export CONSENT_DB_PASSWORD=$(kubectl get secret -n consent consent-db-pguser-consent -o jsonpath='{.data.password}' | base64 -d)
```

Finally, do the same for the password, but pipe it to the clipboard (it may
contain characters that don't render well in your terminal):

```shell
kubectl get secret -n consent consent-db-pguser-consent -o jsonpath='{.data.password}' | base64 -d | pbcopy
```

Now we can execute psql:

```
$ kubectl exec -it ${CONSENT_DB_INSTANCE_POD_NAME} -n consent -- \
  psql -h ${CONSENT_DB_HOST} -p 5432 -d ${CONSENT_DB_DBNAME} -U ${CONSENT_DB_USER}
Password for user consent: <Paste the password from the previous step>
psql (14.7)
Type "help" for help.

consent=# select count(*) from consent;
 count
-------
    28
(1 row)

consent=#
```

## Kubectl access to environments:

To make sure you have installed necessary tools:
- [For lab2/dev2 access follow this instruction](../../../infrastructure/k8s/environments/lab2/README.md#kubectl-setup)
- [For Speedway/SMC access follow this instruction](../../../infra/kubeconfig/README.md#requirements)

### Kubeconfigs for different environments:

Locations for different environments are listed below.  
You can use them individually or merge them to you local configuration.

- **Lab2**
  - [Follow the steps from the README to merge all cluster configs](../../../infrastructure/k8s/environments/lab2/README.md#kubectl-setup)
  - [Or use cluster specific kubeconfig like worker1-east](../../../infrastructure/k8s/environments/lab2/clusters/worker1-east/kubeconfig.yaml)
- **Dev2**
  - [Follow the steps from the README to merge all cluster configs](../../../infrastructure/k8s/environments/lab2/README.md#kubectl-setup)
  - [Or use cluster specific kubeconfig like worker1-east](../../../infrastructure/k8s/environments/dev2/clusters/worker1-east/kubeconfig.yaml)
- **Speedway Dev**
  - [Use the following kubeconfig](../../../infra/kubeconfig/speedway-dev-vcluster-kubeconfig.yaml)
- **Speedway Prod**
  - [Use the following kubeconfig](../../../infra/kubeconfig/speedway-prod-vcluster-kubeconfig.yaml)
- **SMC non-prod** 
  - [Download and use non-prod kubeconfig](https://portal.tmc-stargate.com/mtfuji)
- **SMC production**
  - [Download and use production kubeconfig](https://portal.tmc-stargate.com/mtfuji)
    - Make sure to switch to the `Production` tab in the `Download Kubeconfig` modal

### Merging kubeconfig to your current config:
```
# Make sure you moved to the directory where your target <kubeconfig_name> file exists
export KUBECONFIG=${HOME}/.kube/config:$(pwd)/<kubeconfig_name>
mkdir -p ~/.kube
kubectl config view --flatten > ~/.kube/config.new
mv ~/.kube/config ~/.kube/config.bak
mv ~/.kube/config.new ~/.kube/config
```

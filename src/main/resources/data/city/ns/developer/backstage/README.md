# Backstage

[Backstage](https://backstage.io/) is a portal for developers.

In an effort to provide a single point of entry for service teams developing on Agora, we will use Backstage to collect documentation.

Backstage has a complex system built on top of [MkDocs](https://www.mkdocs.org/) called [TechDocs](https://backstage.io/docs/features/techdocs/), which provides additional functionality like templates and other useful features.

## Architecture

Refer to our [Configuration Documents](./docs/backstage_configuration/docs/01_configuration_and_setup.md) for details on setup and architectural decisions.

## Development

### Running Backstage Locally

If you are working on just the Backstage portion of Developer Portal, you are at the right place.
If you are working on some features that require interactions between containers (e.g. openapi-generator, kroki-server, etc.), you may want to [set up Backstage in the Local cluster](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/developer-portal/README.md).

Before getting started, please take some time to read [the team's contribution guidelines](./CONTRIBUTING.md) for the portal project.

#### Pre-requisites

To run Backstage, you will need to have the following software installed:

- Node.js v18.19.0 or later, as indicated in `app/.node-version`. It's recommended to use a node version manager such as [nodenv](https://github.com/nodenv/nodenv) to install it
- yarn v4.1.0 - npm will install yarn v1.x, so you will need to run  `corepack enable` and run `yarn set version 4.1.0`; for more detailed instructions please [read here](https://yarnpkg.com/getting-started/install)
  - If the `corepack` command is not available, you can install it with `brew install corepack`.

#### Move to the appropriate working directory

```shell
cd ns/developer/backstage
```

#### Create secret files

Run the following commands to set up the credential YAMLs:

```shell
cp ./secrets/secret-azure-credentials.example.yaml ./secrets/secret-azure-credentials.yaml
cp ./secrets/secret-grafana-credentials.example.yaml ./secrets/secret-grafana-credentials.yaml
```

> [!IMPORTANT]
> These files are included in our .gitignore for security purposes. **Be careful not to commit any credentials should you change the name for your local environment**.

#### Adding Azure credentials

For the Azure AAD credentials, please check the `Developer Portal Azure secrets` secure note in the DevRel team's shared 1password vault and copy the information to  `secret-azure-credentials.yaml`.

For local development, you need to install the [VS Code Azure Account extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode.azure-account) and sign in.

#### Enable the Grafana alerts card

The `grafana-cards` plugin needs two tokens in order to work properly. As with the Azure credentials, go to 1password and find the `Grafana Dev Portal Viewer Token` file in the team's shared vault, then copy the corresponding values to the `secret-grafana-credentials.yaml` file.

#### Enable the Grafana analytics

The analytics need a Grafana RUM link which is obtained in `Front end` in Grafana `Create a dashboard`. The environment config `backend.grafana.environment` needs to be correctly filled according to the environment. eg. `local`, `dev2`, `speedway dev`. For more information please read the [Grafana frontend observability documentation](https://grafana.com/docs/grafana-cloud/monitor-applications/frontend-observability).

#### Enable Local Template building (optional)

If you have a need to generate code from templates locally, you will need to have a Docker image running on port 8888 of your development machine. Open a separate shell window and start it as follows:

```shell
docker run --rm -p 8888:8080 openapitools/openapi-generator-online:latest
```

#### Enable local diagram building (optional)

If you have a need to visualize diagrams (e.g. Mermaid) locally, you will need to have Kroki running which requires a core kroki server and a mermaid-specific kroki server.
The [officially recommended way](https://github.com/yuzutech/kroki/blob/main/DOCKERHUB.md) of achieving this for local development purposes is to use [docker compose](https://docs.docker.com/compose/):

Open a separate shell window and run it as follows:

```shell
cd ns/developer/backstage

docker compose up
[+] Running 3/3
 ✔ Network city_default      Created            0.1s 
 ✔ Container city-mermaid-1  Created            0.0s 
 ✔ Container city-core-1     Created            0.0s 
Attaching to city-core-1, city-mermaid-1
...
```

#### Enable Local TechDocs building (optional - but recommended)

Use python to install the MkDocs dependency for techdocs.

```shell
cd app
pip install -c constraints.txt -r requirements.txt
```

Make sure MkDocs is accessible on the command line:

```shell
$ mkdocs --version
mkdocs, version x.x.x
```

#### Start the Portal

> [!NOTE]
> Some Node.js packages require Python modules that use `distutils`, and that are used on building via [node-gyp](https://github.com/nodejs/node-gyp) at install.
> If you use the latest Python runtime, then make sure that you have `setuptools` module in your env. Because it has been [removed](https://github.com/python/cpython/pull/101039) from the standard library in Python `>= 3.12`.

```shell
$ pip freeze | grep setuptools
setuptools==...
```

If it's your first time running Backstage, you will need to install its dependencies first:

```shell
$ cd app
$ yarn install
... # Omitted for brevity
success Saved lockfile.
➤ YN0000: └ Completed
➤ YN0000: ┌ Fetch step
➤ YN0000: └ Completed in 13s 359ms
➤ YN0000: ┌ Link step
➤ YN0000: └ Completed in 1s 276ms
➤ YN0000: · Done with warnings in 15s 265ms
```

To run the local development server:

```shell
$ yarn dev
... # Omitted for brevity
[0] Loaded config from app-config.yaml, app-config.local-override.yaml
[0] <i> [webpack-dev-server] Project is running at:
[0] <i> [webpack-dev-server] Loopback: http://localhost:3000/, http://[::1]:3000/
[0] <i> [webpack-dev-server] 404s will fallback to '/index.html'
[0] <i> [webpack-dev-middleware] wait until bundle finished: /settings
[0] webpack compiled successfully
```

And Boom! DevPortal is up and running! Your browser should open to `http://localhost:3000`

### Running backstage locally with the bootstrap script

If you want to quickly check some Developer Portal updates on your working branch,
you could use [bin/start\_local\_backstage.sh](./bin/start_local_backstage.sh) like the following.

```bash
$ GH_BACKSTAGE_TOKEN=<GitHub Personal Access Token>\
    GH_BACKSTAGE_OAUTH_CLIENT_ID=<GitHub OAuth Client ID>\
    GH_BACKSTAGE_OAUTH_CLIENT_SECRET=<GitHub OAuth Client Secret>\
    bin/start_local_backstage.sh
```

The script sets the branch that Backstage monitors to the one you're currently checking out.

> [!WARNING]
> The branch name should not contain any slashes (`/`) for local mkdocs building to work. Please use kebab case instead.

The script requires [yq](https://github.com/mikefarah/yq) to modify the YAML files.
And you need to create a GitHub Personal Access Token and a GitHub OAuth App described in the previous sections.

### Pointing to components in a repo

The provider configuration is set up to point to whatever branch you point it to

```yaml
# app/app-config.local-override.yaml
catalog:
  providers:
    github:
      city:
        host: github.com
        organization: 'wp-wcm'
        catalogPath: '/catalog-info.yaml' <--- Changing this changes where to look for the 'root' catalog
        filters:
          # Note: do not use slashes (/) in branch names
          branch: 'main'  <--- Changing this changes the target branch to build entities from
          repository: 'city' <--- Changing this changes the repository target
        validateLocationExists: true
        schedule:
          frequency: { minutes: 5 }
          timeout: { minutes: 3 }
```

### Working on Markdown files on your local file system

Instead of using GitHub provider for catalog integration, you also can render HTML documents from `.md` files that are already fetched to the local city repository on your file system.

To do that, comment `providers.github` out and use `locations` as below:

```yaml
# app/app-config.local-override.yaml
catalog:
  locations:
    - type: file
      target: ../../all-debug.local.yaml
```

And then create and modify this `all-debug.local.yaml` file contains local catalog file locations as you need.

```shell
# see .sample file
$ cp app/all-debug.local.yaml{.sample,}
```

## Learn More

- [How to register new components in the portal](./docs/adding_documentation/)

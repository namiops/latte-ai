# Agora UI

![Vue badge](https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vue.js&logoColor=4FC08D)
![TypeScript badge](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Vue badge](https://img.shields.io/badge/Vite-4FC08D?style=for-the-badge&logo=Vite&logoColor=ffffff)

Agora UI is Agora's admin interface. As the single entry point for interacting with the Agora platform, it is aimed at a wide user base that includes engineers, product owners, admins, and other stakeholders.

## Setup 🔨

### Prerequisites ✅

This project is based on Vue 3, TypeScript and Vite, built with Bazel Aspect JavaScript rules. Please install [Bazelisk](https://github.com/bazelbuild/bazelisk) in order to use Bazel and iBazel, and the [pnpm](https://pnpm.io/installation) package manager.

You will also need to [configure Artifactory access](https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/bazel/#optional-step-authenticate-to-private-npm-registry) in order to install dependencies [via pnpm and Bazel](https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/javascript/#managing-dependencies).

Read more about [Bazel](https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/bazel/) and [developing with JavaScript and Typescript](https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/javascript/) in Agora for more in-depth information about developing applications in the monorepo.

### Local environment 💻

To run the application locally, please create an `.env.local` file in the project's root directory with the following contents:

```env
VITE_APP_ENV = local
VITE_APP_NAME = Agora Admin UI
VITE_APP_AUTHORITY = <keycloak_url>
VITE_APP_CLIENT_ID = <keycloak_public_client_id>
VITE_APP_REALM = <keycloak_realm>
VITE_APP_GITHUB_TOKEN = <personal_github_access_token>
VITE_APP_GITHUB_DOMAIN = https://github.tri-ad.tech
VITE_APP_GITHUB_TEMPLATE_PATH = /api/v3/repos/cityos-platform/notification-template-registry/contents/templates/
VITE_APP_GITHUB_TEMPLATE_LIST_PATH = /api/v3/repos/cityos-platform/notification-template-registry/git/trees/main?recursive = 1
VITE_APP_WEBHOOK_SERVER_NAME = <webhook_server_url>
VITE_APP_BACKEND_URL = <backend_url>
VITE_APP_WEBHOOK_TIME_INTERVAL_MS = <webhook_time_interval_ms>
VITE_APP_NOTIFIER_API_DOMAIN = <notifier_api_url>/notify
VITE_APP_ENCRYPTION_API_DOMAIN = <secure-kvs_demo_service>/securekvs-demo-api/api/Example
VITE_APP_BURR_DATA_API_DOMAIN = <burr_data_service_url>
VITE_APP_CONSENT_API_DOMAIN=<consent_service_url>
VITE_APP_IOTA_SERVICE_DOMAIN=<iota_service_url>
VITE_APP_IOTA_CONFIG_SERVICE_DOMAIN=<iota_config_service_url>
VITE_APP_SLACK_WEBHOOK_URL=<slack_webhook_url>
VITE_APP_IOTA_DOC_URL=<iota_doc_dev_portal_url>
VITE_APP_NOTIFIER_DOC_URL = <notifier_doc_dev_portal_url>
VITE_APP_CONSENT_DOC_URL = <consent_doc_dev_portal_url>
VITE_APP_XENIA_CONTACT_URL = <xenia_contact_url>
VITE_APP_AMA_CONTACT_URL = <ama_contact_url>
VITE_APP_AGREEMENTS_SERVICE_DOMAIN = <agreements_service_url>
VITE_DEFAULT_LOCALE = en
VITE_FALLBACK_LOCALE = en
```

To see the correct values to use in this file, ask the [Agora DevRel team](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD) for access to the 1Password shared secrets.

For `VITE_APP_GITHUB_TOKEN`, go to the [Personal Access Tokens page in GitHub's settings](https://github.com/settings/tokens) and create a PAT to use with Agora UI.
The token doesn't require any additional permissions as it is only for reading repo and file information.

Additionally, please set an environment variable by adding `export ENABLE_LINT_STAGED=1` to your shell configuration (for example `~/.zshrc`) in order to automatically lint staged files whenever a new change is committed.

### IDE Setup 📝

Microsoft's [VS Code](https://code.visualstudio.com/) is **strongly** recommended as the code editor for working in this project. Upon opening the project directory, a prompt will show up for installing the recommended plugins:

- [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar): Adds Vue language features to VS Code
- [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint): JavaScript/TypeSscript linting utility for common problems and enforcing certain code rules.
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode): Code formatter that helps keep the style consistent by formatting code on save.

## Development 👷

Start the development server with hot-reload:

```sh
npm run start
```

Check the TypeScript types:

```sh
npm run type-check
```

Preview the static build. Make sure you have entered the real values in the `.env.production` env file:

```sh
npm run build && npm run preview
```

## Storybook 📚

Storybook is a frontend tool for building UI components and pages in isolation. To run it, use the following command:

```sh
npm run storybook
```

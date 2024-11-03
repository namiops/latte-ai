# ac-hr-app (namespace: ac-user-registration)

**This project is deprecated. You can use it for demonstration if needed.**

## Common Setups and Rules

See [confluence](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=377865408)

## Repository Rules

### Branch Name

**ac-user-registration/_branch description_**

      *branch title* : Describe branch name。
      例 : ac-user-registration/100_add_logout_button
      (100 is number of a JIRA ticket, you can omit if there is not a applicable ticket.)

※ The branch will be deleted,actually archived, after merged into main.

## Development

### Prerequisites

- Install bazel and pnpm. See [Common Setups and Rules](#Common-Setups-and-Rules).
- Install local development dependencies by `pnpm install` at this project's directory.
- Install npm dependencies by `pnpm install` at the monorepo root.

### npm run scripts for development

- `$ pnpm run lint` to run ESLint
- `$ pnpm run dev` to run dev-server
- `$ pnpm run mock-server` to run mock-server of backend
  - It serves the app at `http://localhost:3000/hr/`
  - The server watches the file change and rebuild automatically
  - But it doesn't support live-reloading, so **you need to reload on the browser manually**

### Bazel commands

- Bundle
  - `$ bazel build //projects/ac-user-registration/hr-app:bundle`
- Run the app with serve
  - `$ bazel run //projects/ac-user-registration/hr-app:serve --define mode=dev`
- Run ESLint
  - `$ bazel test //projects/ac-user-registration/hr-app:lint-test`
- Run the app with nginx docker container
  - `$ bazel run //projects/ac-user-registration/hr-app`

# ac-reg-face-app-for-poc (namespace: ac-reg-visitor-personal)

## Common Setups and Rules

See [confluence](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=377865408)

## Repository Rules

### Branch Name

**ac-reg-visitor-personal/_branch description_**

      *branch title* : ブランチ名を記述。
      例 : ac-reg-visitor-personal/100_add_logout_button
      (100 は JIRA チケット番号, 対応チケット無い場合は省略)

※ ブランチは main へマージ後に削除(archive)されます

## Development

### Prerequisites

- Install bazel and pnpm. See [Common Setups and Rules](#Common-Setups-and-Rules).
- Install local development dependencies by `pnpm install` at this project's directory.
- Install npm dependencies by `pnpm install` at the monorepo root.

### npm run scripts for development

- `$ pnpm run lint` to run ESLint
- `$ pnpm run dev` to run dev-server
- `$ pnpm run mock-server` to run mock-server of backend
  - It serves the app at `http://localhost:3000/face/{service-name}`. Example: `http://localhost:3000/face/access-control`
  - The server watches the file change and rebuild automatically
  - But it doesn't support live-reloading, so **you need to reload on the browser manually**

### Bazel commands

- Bundle
  - `$ bazel build //projects/ac-reg-face-app-for-poc:bundle`
- Run the app with serve
  - `$ bazel run //projects/ac-reg-face-app-for-poc:serve --define mode=dev`
- Run ESLint
  - `$ bazel test //projects/ac-reg-face-app-for-poc:lint-test`
- Run the app with nginx docker container
  - `$ bazel run //projects/ac-reg-face-app-for-poc`

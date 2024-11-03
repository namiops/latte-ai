# Woven ID Keycloak themes
This directory includes themes for keycloak. To change the visual of login page, account console and admin console for Woven ID, change these files. We provide [docker-compose](https://docs.docker.com/compose/) file to run keycloak for development on your local machine. With this docker-compose, you can view and test developing theme on local.

# How to develop
## Keycloak theme feature
Keycloak allows developers to customize the visual of pages using template engine. [Keycloak Server Developer Guide](https://www.keycloak.org/docs/latest/server_development/#_themes) explains the detail of theme feature. It use [freemaker](https://freemarker.apache.org/) template engine. To develop theme, you need to learn the syntax of freemaker template language.

Keycloak theme can "inherit" other theme. Basically, almost all keycloak themes inherits "[base](https://freemarker.apache.org/docs/dgui.html)" template. In this `base` template, each page in keycloak has one file. This "base" does not have any decoration, but minimum fields and functionality. When your custom theme does not have corresponding template to showing page, keycloak fall back to use this base template.

In these days, Keycloak community move to "[keycloakify](https://www.keycloakify.dev/)" which allows developers to write custom theme with [React](https://reactjs.org/). We need to further research on keycloakify to migrate theme in near future.

> NOTED: THIS THEME ONLY COMPATIBLE WITH KEYCLOAK 22

## Testing
You can run Keycloak on your local machine with the following command.
```
# run on this directy("ns/id/keycloak-theme-woven")
./start-dev.sh
# and everytime to make a change run
./build.sh
```
This command will rebuild themes and prepare a mount folder for docker compose

### 1. Login to admin console
- Go to [admin console]
- The admin user is username: `admin`, password: `admin`

### 2. Configure theme
- After log in to admin console, please go to `woven` realm.
- And go to [theme](http://localhost:8080/admin/master/console/#/woven/realm-settings/themes) tab on `Realm setting` menu
- Change theme to choose your developing theme
- Push `Save` button

### 3. Try normal login
- Go to [account console](http://localhost:8080/realms/woven/account/)
- Push "Sign in" button at top right on the account console
- Login with username and password. A test user is registered with username: `alice`, password: `alice`
- Push "Sign out" button at top right on the account console

### 4. Try federation Login
- Go to [account console](http://localhost:8080/realms/woven/account/)
- Push "Sign in" button at top right on the account console
- Login with "Woven Planet SSO"
- You will be redirected to "another" realm on the same keycloak
- Login with username and password. A test user is already registered with username: `alice`, password: `alice` in "another" realm
- Push "Sign out" button at top right on the account console

### 5. Register passwordless sign-in
- Go to [account console](http://localhost:8080/realms/woven/account/)
- Push "Sign in" button at top right on the account console
- Login with username and password. A test user is registered with username: `alice`, password: `alice`
- Go to [Signing in](http://localhost:8080/realms/woven/account/#/security/signingin) page in "Account security" menu.
- Click the link "Set up Security key" in "Passwordless" section
- Push the "Register" button and choose your security key. If you do not have security key, you can use [chrome's Webauthn emulator](https://developer.chrome.com/docs/devtools/webauthn/)
- Push "Sign out" button ad top right on the account console

### 6. Try passwordless Login
- Go to [account console](http://localhost:8080/realms/woven/account/)
- Push "Sign in" button at top right on the account console
- Click "Try Another Way" link
- Push "Security Key" button
- Choose your security key on the prompt and authenticate
- Push "Sign out" button ad top right on the account console

# Fix the theme
With docker-compose in this directory, it runs keycloak with theme cache disabled in [this way](https://keycloakthemes.com/blog/how-to-turn-off-the-keycloak-theme-cache).
So when you change templates and reload the page, the page will re-render with the changed template.
If you run keycloak without this configuration, Keycloak reads theme template during first rendering and caches it and does not reload even if you modified. Be careful.

# How to build image
The keycloak image is built by bazel. When the PR including change for theme is merged to main branch, our CI system will build new keycloak image and push [company image repository](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/id/keycloak).

# How to deploy
Keycloak is deployed by Keycloak-operator now. You need to update keycloak image in [patch](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/cityos-system/patches/keycloak-legacy-image.patch.yaml) for each environment.


# Initial Setup

- `root` means `city/`
- `foodagri-root` means `city/projects/foodagri-frontend`

## prerequisites

Install the followings

- docker (latest version)
- nodejs v20.11.0 and npm

Install `pnpm` nodejs package globally:

```bash
npm install -g pnpm
```

## Backend

From the `foodagri-root`:

```bash
cd backend
```

### Build and start containers

```bash
docker compose up -d --force-recreate --remove-orphans --build
```

If you get an error about `compose` not being a valid command, you likely have an old version of docker and you might have `docker-compose` installed as well, in that case, try to replace `docker compose` by `docker-compose` (you just need to add the extra `-`) in the following command.

The above command starts the following containers:

- redis-commander on port 8081
- redis-sentinel on port 26379
- postgreslocal on port 5432
- keycloak-instance on port 8080
- redis on port 6379

WARNING: it does not start the backend server

TODO: consider adding the backend server the docker-compose file(s)

### Build the project and populate the database

Execute only ONCE:

```bash
pnpm install
pnpm run db:migration
pnpm run db:seed
```

**WARNING**:

Note that, because there is a `package.json` at the `root` too, all those packages will be intalled as well and `node_modules` will at the `root`.

### Start the backend

```bash
pnpm run start
```

Apollo server (graphql): <http://localhost:9093/graphql>

### Create Keycloak users and clients

This needs the keycloak container to be running.

Execute once in the following cases:

- during the first setup
- after a `docker compose down`, followed by a `docker compose up` as the keycloak container needs to be running

```bash
. ./setup-keycloak.sh
```

## Frontend

From the `foodagri-root``:

```bash
cd frontend/yott-app
pnpm install
pnpm run dev
```

e-commerce site: <http://127.0.0.1:3000/>

**WARNING**:

<http://localhost:3000> should be avoided because cookies are set for <http://127.0.0.1:3000/> (unless you change the config in the `.env` file)

[backend](http://localhost:9093/graphql) alternative: <http://127.0.0.1:3000/api/proxy>

TODOs:

- explain why we have the backend proxy, its difference and it is used
- fix dockerisation

### Create a user account

- create a user for the EC site:
  - go to <http://localhost:3000/>
  - click on the "->]" icon
  - click on "Continue with Woven Account"
  - click on "Register"
  - fill the form
  - click on "Register", you should see something like "⚠️ You need to verify your email address to activate your account."
- verify the user
  - go to <http://localhost:8080/> -> Administration Console, log in with admin/admin
  - click on `Users` in the left side bar
  - click on `View all users` on the central page
  - click on the ID of the user you want to confirm
  - set `Email Verified` to `On`
  - remove the `Verify Email` from `Required User Actions`
  - click on `Save`
- set a user as an admin (optional)
  - in the postgres `database_development` database, `customers` table, set `type` of the user to `ADMIN`
  - log in with your woven account, then you should be able to access the admin interface: <http://127.0.0.1:3000/admin/products>

## TODO

- document how to use [Prism](https://docs.stoplight.io/docs/prism/f51bcc80a02db-installation) to simulate a Woven Payment server

- Find payment specs yaml file here : https://developer.woven-city.toyota/catalog/woven-passport/api/woven-passport-api/definition#/payment-method/checkPaymentMethod

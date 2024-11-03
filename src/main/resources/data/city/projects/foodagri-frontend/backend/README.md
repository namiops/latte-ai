# EC-Backend

Backend server

### Important Points

1. Setting Node_ENV=production disables stacktrace in API response

### Folder Structure

#### config

Define configuration for database (for postgresql)

#### migrations

If need to create table or alter table for database, need to put it on here using script to keep the history and also capable to run it sequential
To run migration just need to run a command `npm run db:migration` that defined on `package.json`
This command just run the migration which never been run previously.
To create new migration file use this command `node_modules/.bin/sequelize migration:generate --name <migration name>`

#### seeders

If need to add static data for table database, need to put it on here
To run seeder you just need to run a command `npm run db:seed` that defined on `package.json`
This command just run the seeder which never been run previously.
To create new seeder file use this command `node_modules/.bin/sequelize seed:generate --name <seeder name>`

#### models

This folder is contains all of the models to define the table structure for `sequelize`

#### schemas

Define the `schema`, `type`, `mutation`, `query` for `graphql`
Once you create new file under this folder, please register it inside `resolvers/index.js`

#### resolvers

Consists of `mutation` (CUD from CRUD) and `query` (R from CRUD). Should be domain based - each resolver should cover 1 buissnes entity (For example providing query and mutations for `Product`).
Should contain following functionality:

- Logic of formating and validation of I/O (input/output).
- Logic of calling a proper service, or a sequence of services.
- Top level error handling
- Working with auth and access levels (through the context).

Once you create new file under this folder, please register it inside `resolvers/index.js`

#### services

Services is a layer that connecting different data sources (like db, 3rd party API etc) with the `routing` layer (resolvers). Providing a way to incapsulate implementation spesifics inside "isolated module" and provide clear and consistent internal API for resolvers.
Services should be placed in `services/...` directory.
Services should provide a functionality for one entity and for entities that closely coupeled with this entity (from data layer).

#### utils

For utils function and helper

#### hierarchy

At this point we have 3 basic layers of logic:

- Routing layer (`/resolvers/...`)
- Service layer (`/services/...`)
- Data access layer (`/models/...`)

It's important to maintain a hierarchy of dependencies across the different layers to make sure code will be as decoupled as possible. Here's the rules:

- Resolvers should have only a services as a dependencies. (can have more then 1 service)
- Services should have only a `data access` layer or/and a `services` layer dependencies. (can have more then 1)
- Data access layer should not depend on other 2 layers but can import other modules from `data access` layer if this modules is sharing same data source (For example both working with same SQL DB with sequilize).
- Other logic entities - like `constants`, `utils`, `env`, `configs` etc can be a dependency for any types of modules in the project.

### Information

- Don't use sequlize.sync() , to prevent unwanted action happened in the future (like drop table)
- Instead of use that prefer to use `migration`, but need to run `migration` and `seeder` first to make sure up to date the latest version
- When run `npm run start` then http://localhost:8080/graphql is default url to play with the `graphql http`

### Required

#### .env file

Latest file is commited to Github, please refer to it.

### Command

#### Start

npm run start

#### Migration

npm run db:migration

#### Seeder

npm run db:seed

#### Create New Migration File

node_modules/.bin/sequelize migration:generate --name <migration name>

#### Create New Seeder File

node_modules/.bin/sequelize seed:generate --name <seeder name>

## ローカル環境で動作させたい場合

### DB + Graphql の設定を反映する

1. .env を作成する

Refer to .env file from Github

### Using docker-compose

1.  This should automatically create DB, seed data and start the API server

```bash
   docker-compose up --force-recreate
```

### Create category list required in seeder file.

1. Download the csv file and put the file in "/dataImportScripts/categories".
   ・csv file link
   https://docs.google.com/spreadsheets/d/1XyH_lnyhQhWbcj4hAKTITLegTRRzCKyQuVMfWzVIloo/edit#gid=2026804724

Put the file in "/dataImportScripts/categories", and rename "data.csv".

2. Run script

```bash
node dataImportScripts/categories/generateCategoryJson.js
```

Create "outCategory.json" and "outAncestor.json" file.

### Create a map between GMO-PG and our own error code

If payment related errors should be updated, please refer [this](dataImportScripts/gmoError/README.md).

### Graphql tooling

#### code generation

There is a way to generate ts types based on graphql schemas [link](https://www.apollographql.com/docs/apollo-server/workflow/generate-types/).
to do this - graphql server should be launched on 9093 port (`npm start`)
after this launch command - `npm run codegen`. It will generate a file (replacing existing one) in `/generated/graphql.ts`. This file should be commited and pushed in same branch with schema changing.

#### schema interactive diagram

For dev environment (`ENV_NAME === 'local'`) `http://localhost:9093/voyager` page is available. It contains interactive diagram of current graphql schema.

## Debug

- `nodemon` is used to watch file changes and automatically restart on change, see config in `nodemon.json`
- `ts-node` is used to "run" the typecript files

### VScode

Add the following files at the same level as this file i.e. in `backend/`

- `backend.code-workspace`

```json
{
  "folders": [
    {
      "path": "."
    }
  ],
  "settings": {},
  "launch": {
    "version": "0.2.0",
    "configurations": []
  }
}
```

- `.vscode/launch.json`

```json
{
  "configurations": [
    {
      "name": "debug backend",
      "type": "node",
      "request": "launch",
      "runtimeArgs": ["run-script", "start"],
      "runtimeExecutable": "pnpm",
      "skipFiles": ["<node_internals>/**"],
      "internalConsoleOptions": "openOnSessionStart"
    }
  ]
}
```

To start debugging the server, select `debug backend` in the debug section and click on the green ▶️ or press F5

# Overview

end-to-end folder contains all dockerfiles for back-end front-end cypress and monorepo deps. Additionally all nessesery configs for keycloak, passport and env files.
All this made to totally isolate end-to-end environment from local dev and from any other, and for easer management of buidling configs.

# Before start

Don't need to stop docker-compose, all other instances (db, redis keyclock) is totally separated

# Building

## deps

This step is moved to separate dockerfile and separate process to optimize time and number of reruns. 2nd step should be relaunched every time some dependecy nessesary for `foodagri-fronted` was updated

1. create `.npmrc_creds` file inside end-to-end directory
2. add your npm creds:

```
email=
//artifactory-ha.tmc-stargate.com/artifactory/api/npm/:_auth =
```

this file is in `.gitignore`, so it will not be comitted to git. 3. run following command to build monorepo dependency

```
   docker build --secret id=npmrc,src=.npmrc_creds -t monorepo-deps:latest -f ./Dockerfile.deps ../../../
```

3. Create `.nextauth_creds` and pass random base64 string to this file. It will be used for creating JWT tokens from next-auth.
   Generation command (taken from https://next-auth.js.org/configuration/options):

```
openssl rand -base64 32
```

## Build FE

```
docker build  -f ./Dockerfile.fe -t woven-ec_fe:latest ../frontend/yott-app
```

## Build BE

```
docker build  -f ./Dockerfile.be -t woven-ec_be:latest ../backend
```

## Build File service

```
docker build  -f ./Dockerfile.fileupload -t woven-ec_fileupload:latest ../file-uploader
```

## Build and launch recommendation

This part is still in development, so it's not fully automated yet. Please read the instruction completely befor start following this guid.

### before start

1. Copy `lauch.sh` file to the `./recommendations` folder
   This file responsible for bootstraping DB and launch the app
   Bootstraping includes refreshing DB, running migrations and seeds

Note: to refresh DB you need to restart container manually
Integration with refreshDB command will be added soon.

2. Add missing env variables to .env.recommendation file (can be taken from 1password). Make sure, that you erase all duplicating env from it (DB configs and backend host should be taken from existing file)
   You should not commit this changes.

### build

```
docker build  -f ./Dockerfile.recommendation -t woven-ec_recommendation:latest ../recommendations
```

### launch

1. launch DB:

```
docker-compose up -d db-recommendations
```

If it's a first run, make sure DB is running before moving to the next step. For some reason DROP script start executing before DB actually ready to accept connections (will be resolved in future)

2. launch the container:

```
docker-compose -f docker-compose.yml up -d recommendation-api
```


## Building test container

```
docker build -t woven-ec_e2e:latest -f ./Dockerfile.cypress ../frontend/yott-app
```

### Note - it is possible to pass Dockerfile-s to docker-compose.yml and skip build steps, but at this point I think it will be better to run it separatly to catch all possible issues

## Launching tests

first, run

```
docker-compose -f docker-compose.yml up -d front-end test-utils
```

open following url:

```
http://127.0.0.1:9094/refreshDB?seedName=[seed-name]&seedPath=[seed-path]
```

for example:
`http://127.0.0.1:9094/refreshDB?seedName=20240123073636-im_2&seedPath=%22seeders/inventoryManagement%22` to run im_2 seed

This will recreate DB and run migration and seeds. This DB is isolated from local dev environment, so it is safe to restart it at any point of time.
Endpoint is availale only for test env (by env variable).
The running of new seeds will be automatized in the future and will be a part of test sute. (after this endpoint will be removed from public access).

### Note:

At this point, front-end should be available by url - `127.0.0.1:3001`
And inside docker-compose network it is available by `http://front-end:3001` shortcut
Because ssr part of the front-end runed inside docker, it is required to use this url for `cypress.config.js` file.

Next launch test container

```
docker-compose up e2e
```

in console test run and test results will be displayed.

it is possible to run cypress on test env without docker-compose
for this change url to `http://127.0.0.1:3001` in `cypress.config.js` and use following command:

```
docker run -it -v $PWD/../frontend/yott-app:/e2e -w /e2e cypress/included
```

or by using npx.

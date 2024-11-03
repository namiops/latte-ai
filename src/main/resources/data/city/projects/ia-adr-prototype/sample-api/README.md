# README

## Prerequisite

* installation
  * bazel
  * docker
  * docker compose
  * jdk-17
* network
  * connect to the office network (so that this app can reach burr service)

## How to develop in VSCode

* setup
  * open vscode here (the directory where this readme file is located)
  
  * create ./.vscode/settings.json and add following lines.  

    ```json
    {
      "java.project.referencedLibraries": [
        "../../../bazel-bin/external/maven/**/*.jar",
      ]
    }
    ```

  * fetch dependencies  

    ```sh
    bazel fetch :sample-api
    ```

## How to run

### run midlewares

```sh
docker compose up -d
```

### run with jvm

```bash
bazel run :sample-api -- --spring.profiles.active=local
```

### clean up

```bash
bazel clean --expunge
```

### test

currently test tasks keep failing. so the entire test folders are removed.

## Command samples

### Get User

```bash
curl localhost:8080/api/v1alpha/users/me \
-H 'Content-Type: application/json' \
-H 'X-User-Id: 2507253b-15ac-477f-9dac-55d12a93e084'
```

### Create User

```bash
curl localhost:8080/api/v1alpha/users \
-X POST \
-H 'Content-Type: application/json' \
-H 'X-User-Id: 2507253b-15ac-477f-9dac-55d12a93e084' \
-d '{"givenName": "Test", "primaryName": "User"}'
```

### Delete User

this command will remove user in Burr directly

```bash
curl https://brr.cityos-dev.woven-planet.tech/api/v1alpha/person/2507253b-15ac-477f-9dac-55d12a93e084 \
-X DELETE
```

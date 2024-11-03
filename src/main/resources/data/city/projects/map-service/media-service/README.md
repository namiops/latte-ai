# Map Media Service

The Map Media Service microservice is responsible for exposing APIs for clients to get photos related to points of interests on Map.<br>
Also, APis for Map Service to upload/replace/delete photos are exposed.

## External Access
This service uses Postgres working on Agora. It manages information about stored photos.<br>
This service uses AWS S3 working on in-house AWS. It stores photos uploaded by the Map Service.

## Configuration
This service uses Vault to get KVs to access Postgres and AWS S3.<br>
The sample of Postgres secrets file is below.
```JSON
{
  "host":"localhost",
  "port":5432,
  "username":"postgres",
  "password":"password",
  "db_name":"map-db",
  "max_idle_connects":10,
  "max_open_connects":10,
  "max_connect_life_time":1000
}

```
The sample of AWS S3 secrets file is below.
```JSON
{
  "access_key":"access key",
  "secret_key":"secret key",
  "region":"ap-northeast-1",
  "bucket":"map-media-storage",
  "endpoint":"s3 endpoint"
}

```

Each secrets file path is specified in the configuration file.<br>
When you run in the local environment, please create files manually and set the paths to the configuration file.

## Build and run the project with Bazel
```
cd projects/map-media-service
bazel build map-media-service
bazel run binary -- -c=$(pwd)/local/config/config.toml
```

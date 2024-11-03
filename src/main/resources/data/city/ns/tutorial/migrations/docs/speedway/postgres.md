# Postgres setup on Speedway

This document is meant to be a self service guide on configuring Postgres on Speedway.

!!! Note
    Please note that the process for setting up Postgres may change in future. We will constantly update this guide with the latest information.

Setting up Postgres in Speedway is a bit different than in Preprod for 2 reasons:

- We use [Zalando Postgres Operator](https://github.com/zalando/postgres-operator) instead of Crunchy
- We need to setup an S3 bucket which will work as a backup for your data

Please follow below steps to setup Postgres within your namespace

## Setup S3 bucket for backup

We use agoractl to generate the yaml file for S3 bucket, please change the `environment` and `namespace` accordingly and leave other parameters unchanged.

```sh
bazel run //ns/agoractl:agoractl -- bucket --environment=prod --namespace=agora-tenant-ac-user-registration-prod create --prefixes postgres,postgres-pod
```

For `--environment`, use `dev3` for Speedway Dev and `prod` for Speedway Prod. Above command will create a yaml file as shown below. This is the `terraform_target` in the next step.

```yaml
bucketName: prod-agora-tenant-ac-user-registration-prod-9ea559
environment: prod
keyAccessorARN: ""
namespace: agora-tenant-ac-user-registration-prod
prefixes:
  postgres:
    iamRoleName: prod-agora-tenant-ac-user-registration-prod-9ea559-postgres
    serviceAccountName: postgres-pod
```

Next step is to setup [IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) so that your postgres is able to connect to the S3 bucket.

Create a new folder in your directory and copy the [bazel target](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-ac-user-registration/speedway/prod/postgres/visitor-db/BUILD#L1C1-L10C2) below:

```bazel
load("//ns/object-storage/bazel:agora_s3_bucket.bzl", "agora_s3_bucket_k8s_resources")

agora_s3_bucket_k8s_resources(
    name = "agora_bucket",
    environment = "prod",
    namespace = "agora-tenant-ac-user-registration-prod",
    terraform_target = "//infrastructure/terraform/environments/prod/accounts/storage-valet/buckets:agora-tenant-ac-user-registration-prod.yaml",
)
```

Change `namespace`, `environment` and `terraform_target` accordingly. The terraform target refers to the S3 bucket file that we created in the previous step.

Now, run the bazel target by changing the directory where the above `BUILD` file exists and execute below command to generate your manifests:

```sh
bazel run :agora_bucket.k8_resources.copy
```

This will create your `ServiceAccount` and `ServiceEntry`

```sh
$ tree
.
├── BUILD
├── out
│   └── agora_bucket_k8_resources
│       ├── BUILD
│       ├── kustomization.yaml
│       ├── serviceaccount-postgres-pod.yaml
│       └── serviceentry-prod-agora-tenant-ac-user-registration-prod-9ea559-aws.yaml
```

## Setup Postgresql Cluster

Generating manifests for postgres is similar to what we do in preprod but the `CHART` that we are going to use for Speedway will be different. Within your directory copy below [bazel target](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-ac-user-registration/speedway/prod/postgres/visitor-db/BUILD):

```bazel
load("//ns/postgres-operator/bazel:postgrescluster_build.bzl", "PGAGORA_CHART", "postgrescluster_build")

postgrescluster_build(
    name = "postgresdb",
    chart = PGAGORA_CHART,
    format = False,
    namespace = "agora-tenant-ac-user-registration-prod",
    values_file = "postgres-values.yaml",
)
```

Change `namespace` accordingly. Create a `postgres-values.yaml` file in the same folder and copy the [content](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-ac-user-registration/speedway/prod/postgres/visitor-db/postgres-values.yaml) below:

```yaml
---
name: visitor-db
backupConfiguration:
  bucketName: prod-agora-tenant-ac-user-registration-prod-9ea559
  bucketRegion: ap-northeast-1
  objectKeyPostfix: visitor-db
  objectKeyPrefix: postgres
storage:
  volume:
    size: 16Gi
numberOfInstances: 2
targetEnv: speedway-prod
spilo:
  profile: smc
users:
  su:
    databases:
      - visitor_db
    options: [ superuser ]
```

Please change the above values accordingly:

- `name: YOUR_DB_NAME`
- `bucketName:` Copy this from your `terraform_target` file generated above
- `objectKeyPostfix: YOUR_DB_NAME`
- `users:` has keys as owner ("su" for example) and add your `databases`
- `targetEnv:` `speedway-dev` or `speedway-prod` depending on your environment

Please DO NOT use underscores in the following fields as it will though an error, instead use hyphens:

* name
* objectKeyPrefix

Leave other fields as it is. After that run the bazel target by changing to the same directory as the `BUILD` file and executing below command:

```sh
bazel run :postgresdb.copy
```

You manifest files will be generated and your complete directory should look something like this:

```sh
 % tree
.
├── BUILD
├── kustomization.yaml
├── out
│   ├── agora_bucket_k8_resources
│   │   ├── BUILD
│   │   ├── kustomization.yaml
│   │   ├── serviceaccount-postgres-pod.yaml
│   │   └── serviceentry-prod-agora-tenant-ac-user-registration-prod-9ea559-aws.yaml
│   └── postgresdb
│       ├── BUILD
│       ├── configmap-cm-vol-visitor-db.yaml
│       ├── kustomization.yaml
│       ├── postgresql-visitor-db.yaml
│       └── service-visitor-db-headless.yaml
└── postgres-values.yaml
```

Don't forget to add the `kustomization.yaml` in your directory and add both the `out/agora_bucket_k8_resources` and `out/postgresdb` under `resources:` section.

With this, Create a PR and merge with main.

## Connecting to Postgres Cluster from your Application

Since we have changed the operator to Zalando, the secrets generated by the operator (containing details of connection to postgres) only contains `username` and `password`. Hence, we need to configure our deployment file with correct connection information. Please use below [working deployment](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-ac-user-registration/speedway/prod/patches/patch-visitor-api-server-deployment.yaml) as an example on how we can connect to postgres:

```yaml
env:
# PostgreSQL variables
- name: POSTGRES_USER
    valueFrom:
    secretKeyRef:
        name: postgres.visitor-db.credentials.postgresql.acid.zalan.do
        key: username
- name: POSTGRES_PASSWORD
    valueFrom:
    secretKeyRef:
        name: postgres.visitor-db.credentials.postgresql.acid.zalan.do
        key: password
- name: POSTGRES_HOST
    value: "visitor-db.agora-tenant-ac-user-registration-prod.svc.cluster.local"
- name: POSTGRES_DB
    value: "visitor_db"
- name: POSTGRES_PORT
    value: "5432"
```

Change the values above accordingly for your deployment. You can find the generated `Secret` in your namespace.

If you have any questions please reach out to Devrel on [#wcm-org-agora-devrel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD)

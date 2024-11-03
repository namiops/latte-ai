# Quickstart Tutorial to Deployment on Agora

## Overview

This guide details the process of deploying an application to the Agora cluster. For demonstration purposes, we will be deploying a sample application using [Agora CityService](https://developer.woven-city.toyota/docs/default/component/agora-deployment-tutorial/en/02_service_mesh/#the-agora-cityservice) and the [Postgres operator](https://developer.woven-city.toyota/docs/default/component/postgresql-tutorial/en/00_index/). By following through the steps, you will learn the basic framework for any application, including frontend and backend APIs and databases (DBs).

### About the directory structure

The directory for this deployment is organized as follows:

* **Initial directory:** Contains the initial setup of the sample application. This setup serves as a basic reference for integrating CityService and Postgres into your applications. For this tutorial, replace this initial setup with your own configuration.
* **Updated directory:** Reflects the configuration of the sample application after completing the tutorial. Use this configuration as a reference to apply similar changes to your applications.

## What you'll learn

By the end of this guide, you will have learned how to:

* [**Set up Agora CityService**](#1-set-up-agora-cityservice)
  * [Create CityService file and add it to resources](#a-create-cityservice-file-and-add-it-to-resources)
  * [Verification](#b-verification)
* [**Switch to the Postgres operator**](#2-set-up-the-postgres-operator)
  * [Create BUILD file and set the values](#a-create-build-file-and-set-the-values)
  * [Generate the resource YAML files](#b-generate-resource-yaml-files)
  * [Configure the Docker image](#c-configure-the-docker-image)
  * [Add Postgres connection information to the API deployment resource](#d-add-postgres-connection-information-to-api-deployment-resource)
  * [Activate your new Postgres instance in Kustomization](#e-activate-postgres-instance-in-kustomization)
  * [Verification](#d-verification)

## What you’ll need

Before starting this tutorial, you should already have the following tools and files in place. If you haven't done so, use the links below to get started on the respective pre-requisites:

* Setup of the [Agora platform](https://developer.woven-city.toyota/docs/default/Component/agora-onboarding)
* An existing [tenant and namespace](https://developer.woven-city.toyota/docs/default/domain/agora-domain/agora_developers/02-tenant-ns-quickstart//)
* Your application, which should contain:
  * APIs (frontend and backend)
  * A database

Also, you need to copy the folder `city/ns/tutorial/agora-api-deployment-101/Initial` over to your own namespace directory (i.e., `infra/k8s/dev/<YourNamespace>`).

## Steps

### 1. Set up Agora CityService

To get started, we will set up and use Agora CityService to manage routing and authentication for your application.

#### a. Create CityService file and add it to resources

First, create the following `cityservice.yaml` under `infra/k8s/dev/<YourNamespace>/web/`:

```
apiVersion: woven-city.global/v1alpha3
kind: CityService
metadata:
  namespace: <YourNamespace>
  name: <YourNamespace>-cityservice
spec:
  paths:
    /:
      pathType: Prefix
      # Specify the name of the frontend Kubernetes Service resource
      service: web
      auth: true
  idp:
    usePpid: false
```

Then, add it to `infra/k8s/dev/<YourNamespace>/web/kustomization.yaml` as follows:

```
resources:
  # Add cityservice.yaml to frontend Kustomization
  - cityservice.yaml
  - web.yaml
```

When you are done, create a pull request for these changes, Once approval is obtained from the code owners, you can merge them. See the guidelines for establishing code ownership here: [Setting Up Github Teams: Establishing Code Ownership](https://github.com/wp-wcm/city/blob/main/docs/setup/setup-teams.md#establishing-code-ownership)

Upon merging, the Kubernetes manifests will be deployed to the Dev cluster. If you encounter any errors, you can check them in the [wcm-city-os-bots](https://toyotaglobal.enterprise.slack.com/archives/C02RD6HTJG5) Slack channel.

#### b. Verification

##### Domain retrieval

You can get the accessible domain of the deployed application using the following command, which displays the hostname set by the CityService VirtualService.

```
kubectl get virtualservice ingress-virtualservice -n <YourNamespace> -o jsonpath='{.spec.hosts[0]}'
```

When you run this command, you should see the domain for accessing your application displayed as a string in your terminal (i.e., `<YourNamespace>.cityos-dev.woven-planet.tech`).

##### Accessing the application

The retrieved domain can be used to access the application via web browser. Enter the URL in the following format in the address bar:

```
# Access in the browser
https://<RetrievedDomain>
```

Example: `https://<YourNamespace>.cityos-dev.woven-planet.tech`

If set up correctly, you should see the frontend of your application displayed in your browser.

### 2. Set up the Postgres operator

The next step is to set up the Postgres operator to manage your Postgres instances more efficiently.

#### a. Create BUILD file and set the values

Add your project path to `infrastructure/k8s/dev/zebra_files.bzl` as follows:

```
ZEBRA_FILES = [
  # Existing entries
  # ...
  # Add new entry
  "//infra/k8s/dev/<YourNamespace>:postgres-files",
]
```

Prepare the `BUILD` and `postgres-values.yaml` files to generate the Postgres resource. First, create a file called `infra/k8s/dev/<YourNamespace>/db/postgres-template/BUILD` with the following contents:

```
load("//ns/postgres-operator/bazel:postgrescluster_build.bzl", "postgrescluster_build")

postgrescluster_build(
    name = "postgres",
    namespace = "<YourNamespace>",
    values_file = "postgres-values.yaml",
)

filegroup(
    name = "postgres-files",
    srcs = glob(["**/*.yaml"]),
    visibility = ["//visibility:public"],
)
```

Next, make the following `postgres-values.yaml` file in the same directory:

```
---
name: postgres
storageClass: postgresql-sample-postgresql
# If Init processing is required, please add it.
# In this sample application, we are using Init processing, so we are adding it.
databaseInitSQL:
  name: db-schema
  key: words.sql
```

#### b. Generate resource YAML files

You can generate the necessary resources in one of two ways: manually, or using Pipeline/Zebra. Both methods will generate your Postgres resource YAML files under `infra/k8s/dev/<YourNamespace>/db/postgres-template/out`.

**Manual generation**

Run the Bazel command below:

```
bazel run //infra/k8s/dev/<YourNamespace>/db/postgres-template:postgres.copy
```

**Pipeline and Zebra**

Push your files and create a pull request. You will see that your initial build has failed. This is the expected behavior, as the failure will trigger the Zebra bot to generate the missing files and push them to your branch.

#### c. Configure the Docker image

You also need to connect the backend Docker image to your new Postgres instance.

Update the `image` field in `infra/k8s/dev/<YourNamespace>/api/api.yaml` as follows:

```
containers:
  - name: api
    image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/tutorials/agora-api-deployment-101/api:1.0
```

#### d. Add Postgres connection information to API deployment resource

In the same `api.yaml` file, add connection information to the Postgres database as environment variablesm using the command below. This connects your application to the new Postgres instance.

```
containers:
  - name: api
    env:
    - name: DB_ADDR
      valueFrom: { secretKeyRef: { name: postgres-pguser-postgres, key: host } }
    - name: DB_PORT
      valueFrom: { secretKeyRef: { name: postgres-pguser-postgres, key: port } }
    - name: DB_DATABASE
      valueFrom: { secretKeyRef: { name: postgres-pguser-postgres, key: dbname } }
    - name: DB_USER
      valueFrom: { secretKeyRef: { name: postgres-pguser-postgres, key: user } }
    - name: DB_PASSWORD
      valueFrom: { secretKeyRef: { name: postgres-pguser-postgres, key: password } }
```

#### e. Activate Postgres instance in Kustomization

Finally, add the necessary changes to `infra/k8s/dev/<YourNamespace>/db/kustomization.yaml`:

```
resources:
  # Activate the new postgres instance
  - postgres-template/out/postgres
  # Deactivate the old one
  # - db.yaml
```

When you are done, create a pull request for these changes, Once approval is obtained from the code owners, you can proceed to merge. You can find the guidelines for establishing code ownership here: [Establishing Code Ownership](https://github.com/wp-wcm/city/blob/main/docs/setup/setup-teams.md#establishing-code-ownership)
Upon merging, the Kubernetes manifests will be deployed to the Dev cluster. In case of any errors, they can be checked in the following Slack channel: [wcm-city-os-bots](https://toyotaglobal.enterprise.slack.com/archives/C02RD6HTJG5)

#### f. Verification

To verify if you have successfully switched to the Postgres operator, access your application again and check if the correct values are displayed on the board.

## Conclusion

Through this guide, you have learned the basic steps for deploying an application on the Agora cluster with CityService and Postgres.

For more details, please refer to the following resources:

* [CityService](https://developer.woven-city.toyota/docs/default/Component/city-service-operator-service)
* [Postgres operator](https://developer.woven-city.toyota/docs/default/Component/postgres-service/)：
  * [New Postgres instance from a template](https://developer.woven-city.toyota/docs/default/Component/postgres-service/01_postgres-zebra/)
  * [Database connection using environment variables](https://access.crunchydata.com/documentation/postgres-operator/5.3/tutorials/basic-setup/connect-cluster#connect-an-application)
* Sample Application Reference
  * [Docker Samples: Wordsmith](https://github.com/dockersamples/wordsmith)

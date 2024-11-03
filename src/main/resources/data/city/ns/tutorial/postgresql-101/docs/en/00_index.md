# Introduction
This tutorial is an example of how to use the Postgres operator on the Agora
platform. It comes with a simple Todo list API that lets you add and view your
todo items. The API is written in ASP.NET Core 6 and uses Entity Framework to
work with the database. The API creates the actual database tables when it
starts with the help of entity frameworks migrations. That means that this
tutorial doesn't cover how to manage the database's tables.

This tutorial is also not a replacement for the [official Postgres operator documentation](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/quickstart/). So be sure to check it out.

## Why Crunchy PostgreSQL Operator?
You are not required to use the operator to manage your Postgres instances.
You can create an instance on the Agora platform however you want. However,
the Agora team highly recommends you use the operator to create and manage
your Postgres instances.

So what does the operator do for you? Well, let's let the operator devs explain
it.

> Designed for your GitOps workflows, it is easy to get started with Postgres
on Kubernetes with PGO. Within a few moments, you can have a production grade
Postgres cluster complete with high availability, disaster recovery, and
monitoring, all over secure TLS communications. Even better, PGO lets you
easily customize your Postgres cluster to tailor it to your workload!
>
> With conveniences like cloning Postgres clusters to using rolling updates to
roll out disruptive changes with minimal downtime, PGO is ready to support your
Postgres data at every stage of your release pipeline. Built for resiliency and
uptime, PGO will keep your desired Postgres in a desired state so you do not
need to worry about it.
>
> PGO is developed with many years of production experience in automating
Postgres management on Kubernetes, providing a seamless cloud native Postgres
solution to keep your data always available.

If this is not enough to convince you, feel free to look at the example API to
see how easy it is to set it up.

## Supported Versions
Agora platform comes with the Postgres operator preinstalled. That means that
the operator might not be the newest available version. That means that not all
versions of Postgres database instances are supported.

As of writing this, the Agora platform uses

```
Crunchy PostgreSQL Operator 5.0.4
```

Please find the list of supported Postgres instances [here](https://access.crunchydata.com/documentation/postgres-operator/v5/releases/5.0.4/). You can find docker images for specific versions [here](https://www.crunchydata.com/developers/download-postgres/containers).

## File Structure Overview
This chapter overviews the folder structure and important files.

### deploy
This folder contains the `yaml` files required for deployment on kubernetes.

#### namespace.yaml
Creates a namespace `postgresql-101`.

#### postgres.yaml
Here we have the main file. This file describes the Postgres instance that the
operator needs to create. In this file, you can customize the Postgres
instance. Many of the Postgres instance's specs use the defaults, so the
configuration file doesn't contain them. For the list of all specs, please
refer to the [official documentation](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/tutorial/customize-cluster/). Or, when you have Postgres
operator installed, you can use this command. 

```Shell
$ kubectl explain postgresclusters
```

This is a standard file used in the official example for the Postgres operator with one small change. Let's look at the change now.

```yml
apiVersion: postgres-operator.crunchydata.com/v1beta1
kind: PostgresCluster
metadata:
  name: hippo
  namespace: postgresql-101
spec:
  # Redacted
  ...
  users:
    - name: exampleuser
      databases:
        - TodoItems
      options: "SUPERUSER"
  # Redacted
  ...
    
```

As you can guess, we create an `exampleuser` user and give it access to the
database `TodoItems`. Since this user will need to make the tables, we give
it the `SUPERUSER` permissions. Keep note of the name of the user we created.
We will use it later. Also keep note of the `metadata.name`.

#### api.yaml
With this file, we configure the ASP.NET Core API. A few things to note. First,
we store the image on Artifactory. You will also need to store your image on
Artifactory to be able to publish to the Agora platform. To see how to connect
to the Artifactory, follow this [guide](https://docs.woven-planet.tech/engineering_software/artifactory/support/Docker-Registry/). Once you are connected,
you will have to tag and push the image.

The next thing to pay attention to is the environment variables. This is how
the Postgres cluster details get injected into the API. Let's look at the
password variable closer.

```yml
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: hippo-pguser-exampleuser
      key: password
```

The thing to pay attention to here is the secret key ref. You can get the
`secretKeyRef` like this.

First goes the db cluster name, in this case, `hippo`. Second goes `pguser`.
Finally, the user name we noted before, in this case, `exampleuser`. Both
`hippo` and `exampleuser` are set in the `postgres.yaml`. You can read more
about this in the official documentation [here](https://access.crunchydata.com/documentation/postgres-operator/5.0.4/tutorial/user-management/).

#### service.yaml
This file contains the configuration for the `service`. In this case, we name
the service `postgresql-101` the same as the API.

### docs
This folder contains these docs that you are reading.

### src
Here we store the actual source code for the API. This contains a pretty
standard ASP.NET Core API. Feel free to have a look. Just remember that while
the code is here when we launch the API, we are not using this particular code.
We are using the image on artifactory we saw when we looked at the `api.yaml`
file.

# Storage Valet RDS - Quickstart

This document will cover how to start using Storage Valet RDS. We expect that
you've already read
[Storage Valet RDS - Introduction](./01-storage-valet-rds-introduction.md).
If you haven't, please read it first.

The process to start using Storage Valet RDS can be divided into three steps:

* Create an RDS instance and supporting AWS resources.
* Create the necessary supporting kubernetes resources in your respective namespace:
  * Pod ServiceAccount enabling your pod to access AWS resources using IRSA.
  * Istio ServiceEntry for accessing the RDS proxy.
  * External secret store and secret for syncing RDS user credentials from AWS secret manager.
* Create a privatelink by raising a pull request on [mtfuji-private-link-configs](https://github.com/sg-innersource/mtfuji-private-link-configs) repository.

These steps can all be handled manually by the team provisioning the RDS
instance, but to make it easier to manage Agora Storage provides a plugin to
the agoractl tool to do this automatically.

Let's look into each step one by one.

## Create an instance with Storage Valet RDS

You can get your RDS instance and supporting AWS resources by putting one YAML
file in the specific configuration folder in the Woven City mono-repository.
This file will be generated and placed in the appropriate location for you by
using the "agoractl rds" plugin.

You can create the YAML file by executing [agoractl rds create](https://developer.woven-city.toyota/docs/default/Component/storage-valet-rds-service/03-storage-valet-rds-agoractl/#onboarding-update-rds-configuration-agoractl-rds-create)

```bash
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> create -u <username> -i <instance class> -s <allocated storage> -sn <shortname>
```

After executing the command there will be a YAML configuration file that should
be added and submitted in a pull request to the Woven City monorepo. This will
require approval from
[the Agora Data team](https://github.com/orgs/wp-wcm/teams/agora-data). Please
feel free to ask the Agora Data team to review your PR review on the
`#wcm-org-agora-ama` Slack channel with the `@agora-storage` mention :)

After merging the PR, your instance will be provisioned after several minutes.

## Create supporting kubernetes resources for your workload
Storage Valet RDS creates several kubernetes resources for you using the
[agoractl rds k8s](https://developer.woven-city.toyota/docs/default/Component/storage-valet-rds-service/03-storage-valet-rds-agoractl/#generating-serviceaccount-and-serviceentry-manifests-agoractl-bucket-k8s) command.
The `agoractl rds k8s` command execution outputs a Bazel target to generate the following kubernetes manifests:

* Pod ServiceAccount enabling your pod to access AWS resources using IRSA.
* Istio ServiceEntry for accessing the RDS proxy.
* External secret store and secret for syncing RDS user credentials from AWS secret manager.

You can copy and paste the code outputted by the  `agoractl rds k8s` command to the Bazel `BUILD` file
for your namespace, and then the Woven City mono-repository CI pipeline will generate
the manifests.

The `agoractl rds k8s` can be run as follows:
```
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> k8s -p <proxy endpoint> -s <aws-secret-manager-arn-or-name>
```

> ⚠️ Note:
> To retrieve the value of `<proxy endpoint>` and `<aws-secret-manager-arn-or-name>`, refer to the terragrunt apply (exclude vault) section in the [protected workflow](https://github.com/wp-wcm/protected-workflows/actions/workflows/dev2_terragrunt_apply.yaml) github action triggered when the RDS was provisioned.
>
> For example:
> https://github.com/wp-wcm/protected-workflows/actions/runs/11156590563/job/31009382912#step:12:10160
>
> There are both `endpoint_service_name` and `proxy_endpoint`, but when connecting to the DB, use the `proxy_endpoint`.

A more detailed discussion related to the `agoractl rds k8s` command can be found
[here](https://developer.woven-city.toyota/docs/default/Component/storage-valet-rds-service/03-storage-valet-rds-agoractl/#generating-serviceaccount-and-serviceentry-manifests-agoractl-bucket-k8s).

## Add a privatelink

Because RDS requires IP routing between the workload SMC cluster and the Agora
Storage AWS account we must add a privatelink to enable this. Privatelink
provisioning is controlled by SMC inside a repository called
[mtfuji-private-link-configs](https://github.com/sg-innersource/mtfuji-private-link-configs)

The configuration itself is fairly simple so long as you know what values
should be included, but to simplify even further we provide the
[agoractl rds privatelink](https://developer.woven-city.toyota/docs/default/Component/storage-valet-rds-service/03-storage-valet-rds-agoractl/#generating-privatelinks-agoractl-rds-privatelink)
subcommand to provision the privatelink correctly.

```
bazel run //ns/agoractl -- rds --environment <environment> --namespace <namespace> privatelink -pr <path to privatelink repository> -p <proxy endpoint> -e <endpoint service> -c <cluster to which to add the privatelink>
```

After running the command, the file in [mtfuji-private-link-configs](https://github.com/sg-innersource/mtfuji-private-link-configs) will be updated. Commit the changes and request a pull request in the [kubernetes](https://toyotaglobal.enterprise.slack.com/archives/C02JB3YLR1U) channel.

## Accessing the RDS instance from your kubernetes pod

You can connect the pod using the ServiceAccount created in the
[Create supporting kubernetes resources for your workload](#create-supporting-kubernetes-resources-for-your-workload) step
step along with the DB connection information.

### Connecting to your RDS instance using psql
Storage Valet RDS intends to support both traditional SQL based authentication and AWS [IAM database authentication](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAMDBAuth.html).

#### Connecting using SQL based authentication
Typically, when connecting via a database client like `psql` you need to specify the host, port, user, and database:

```bash
psql -h my-rds-proxy.proxy-aaaa.ap-northeast-1.rds.amazonaws.com -p 5432 -U sampleuser -d postgres
```
The RDS host (`-h`) can be found by referring to the `terragrunt apply (exclude vault)` section in the pull request created when the RDS was provisioned (or by referring to a later workflow run
[here](https://github.com/wp-wcm/protected-workflows/actions/workflows/dev2_terragrunt_apply.yaml)).

> For example:
> https://github.com/wp-wcm/protected-workflows/actions/runs/11136851424/job/30949311144#step:12:10742
>
> There are both `endpoint_service_name` and `proxy_endpoint`, but when connecting to the DB, use the `proxy_endpoint`.

The port (`-p`) will be the standard PostgreSQL port: `5432`. Similarly the default database (`-d`) created
is `postgres`

As discussed in the [rds authentication introduction](./01-storage-valet-rds-introduction.md#rds-authentication), a kubernetes secret synced by the external secret operator, defined in [agoractl rds k8s](#create-supporting-kubernetes-resources-for-your-workload) step, contains the database user password. The username (`-U`) is the username set when the [agoractl rds create](#create-an-instance-with-storage-valet-rds) command was run.

> ⚠️ A note regarding RDS secret rotation:
> RDS password rotation is enabled by default (and happens every 7 days).

> ⚠️ A note regarding the generated RDS secret string:
> The password string can contain non-URL escaped characters.

> ⚠️ A note regarding SSL:
> The connection will be rejected if it is not established using SSL.

Please see [RDS connections requires SSL](#rds-connections-requires-ssl) section for more.

If you have any issues, please feel free to reach out the Agora Data team on the `#wcm-org-agora-storage` Slack channel with the `@agora-storage` mention :).

#### RDS connections requires SSL
A connection to RDS will be rejected if it is not established using SSL. You may see an error similar to:
```bash
asyncpg.exceptions.InvalidAuthorizationSpecificationError: This RDS Proxy requires TLS connections
```
To resolve, please ensure you are using SSL when establishing a connection. For example, when using psql, you can set the
following environment variable to ensure that the connection request is sent using SSL:
```bash
PGSSLMODE=require
```

### Pod deployment

As discussed in the [Connecting using SQL based authentication](#connecting-using-sql-based-authentication)
section, you will need the following DB connection information:

* RDS DB HOST
* RDS DB NAME
* RDS DB PASSWORD
* RDS DB PORT
* RDS DB USER

If you would like to store these values in Vault and sync down via External Secret operator, refer to [this](https://github.com/wp-wcm/city/tree/main/infrastructure/helm/vault-external-secrets).

A sample deployment could look as follows:

> ⚠️ Note:
> As discussed in the [rds authentication introduction](./01-storage-valet-rds-introduction.md#rds-authentication) section, a kubernetes secret synced by the external secret operator, contains the database user password.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sample-deployment
spec:
  template:
    spec:
      serviceAccountName: agora-sample-dev-sa
      containers:
      - name: sample-deployment
        image: docker.artifactory-ha.tri-ad.tech/dbaron/pgagora-tools-pod:v0.2
        env:
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: externalsecret-sample-secret
              key: RDS_DB_HOST
        - name: DB_NAME
          valueFrom:
            secretKeyRef:
              name: externalsecret-sample-secret
              key: RDS_DB_NAME
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: rds-synced-secret
              key: RDS_DB_PASSWORD
        - name: DB_PORT
          value: 5432
        - name: DB_USER
          value: myuser
```

You could then exec into the pod running on SMC:
```
KUBECONFIG=~/.kube/config kubectl exec -n my-ns-dev --as=my-ns-dev-admin sample-deployment -i -t -- bash
```
and connect via psql:
```
PGSSLMODE=require PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME
```

A reference kubernetes pod manifest which makes use of the created service account and RDS kubernetes secret can be found
[here](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-pgagora/speedway/dev/rds/dummy-pod.yaml).

Please see [Agora Storage Valet RDS password rotation](./05-storage-valet-rds-faq.md#question-10-agora-storage-valet-rds-password-rotation) FAQ for considerations related to environment variable
updates.

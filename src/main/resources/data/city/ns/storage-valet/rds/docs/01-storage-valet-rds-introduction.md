# Storage Valet RDS - Introduction

## What is Storage Valet RDS

Storage Valet RDS is a solution that makes it easy for you to get an RDS
instance for your workload running inside the Kubernetes clusters that run
Agora.

Storage Valet RDS is intended to provide the following benefits for you without
complex processes and setup:

* An RDS instance provisioned on an Agora-managed AWS account
* [IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) configurations to make it so your workload (pods) can transparently get short-lived, appropriately managed credentials to access to the provisioned RDS instance.
* Security configurations aligned with the Woven City security policy.

## Concepts

![Storage Valet RDS Concept](./storage_valet_rds_concepts.svg)

Storage Valet RDS provisions one RDS instance per Kubernetes namespace for you.

In contrast to [Storage Valet S3](https://developer.woven-city.toyota/docs/default/Component/storage-valet-s3-service), Storage Valet RDS does not provide multiple
service accounts for fine grained access to the RDS instance. This is because
AWS itself does not provide access on a logical database level (for postgres
and MariaDB) only on a connection level.

As such connections for Storage Valet RDS are somewhat less fine grained than
Storage Valet S3 and care should be taken by the user because the namespace
will share access with all other users and applications that use them in the
same namespace (but not other namespaces).

Another notable difference from Storage Valet S3 is that RDS instances are
required to be ip routable unlike S3 instances that are routable regardless of
ip connectivity. Because of this a mechanism must exist that establishes this
ip routability. Currently the clusters that Storage Valet RDS runs on are the
same as Agora, the SMC dev and prod clusters. SMC offers privatelinks for per
application route provisioning and this infrastructure has been used in the
implementation of Storage Valet RDS.

The above diagram[^1] depicts this architecture. A pod inside a namespace has a
service account that provides the AWS credentials necessary to access the RDS
proxy, then the privatelink provides a connection to a Nat Load Balancer inside
the Agora Storage AWS account which connects that traffic to an RDS proxy which
is then connected to the RDS instance itself.

[^1]: The diagram was drawn using Lucidchart. You can access the original data from
[here](https://lucid.app/lucidchart/bd73ec8d-d208-4296-ab3d-58a4632aabc1/edit?invitationId=inv_480be3c8-0314-4946-8890-ce9e05371f33).

## RDS Authentication
Storage Valet RDS intends to support both traditional SQL based authentication and AWS [IAM database authentication](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAMDBAuth.html).

Storage Valet RDS creates the necessary [IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) configurations so that IAM token based authentication can be used. It is important to note that the database IAM authentication
mechanism requires you to make use of the AWS SDK or CLI to generate the temporary auth token.

As illustrated in diagram[^2], for the traditional SQL based authentication mechanism Storage Valet RDS manages the master
user credential's creation and rotation using AWS secrets manager.
It is important to note that password rotation is enabled by default (and happens every 7 days).
Storage Valet RDS then makes use of the [external secrets operator](https://external-secrets.io/latest/provider/aws-secrets-manager/) to sync the respective user credentials from AWS secrets manager into a kubernetes secret in your namespace.
All of the underlying [IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) configurations are created by Storage Valet RDS. Your kubernetes workload (pod) can simply load or mount the kubernetes secret. Please see [Agora Storage Valet RDS password rotation](./05-storage-valet-rds-faq.md#question-10-agora-storage-valet-rds-password-rotation) FAQ for considerations related to environment variable
updates.

![Storage Valet RDS external Secrets](./storage_valet_rds_external_secrets.svg)

[^2]: The diagram was drawn using Lucidchart. You can access the original data from
[here](https://lucid.app/lucidchart/bd58fa72-5ba9-4810-8ffb-80c930333caf/edit?viewport_loc=-1702%2C-96%2C2464%2C1176%2CJ1qe9eUP9uka&invitationId=inv_bd722d5a-5c33-4b9c-8524-dc6fefed3fcc).

## Supported Engines

Storage Valet RDS currently supports only the PostgreSQL engine type, however the
current development plan also has Aurora supported soon.

Supported:

* PostgreSQL
  * Versions:
    * 16.3
  * Please see Amazon RDS [PostgreSQL Release Notes](https://docs.aws.amazon.com/AmazonRDS/latest/PostgreSQLReleaseNotes/Welcome.html) for full list of available engine support.

Soon:

* Aurora

Other engines are currently not prioritized, but please reach out to
@agora-storage on slack or
[#wcm-org-agora-storage](https://toyotaglobal.enterprise.slack.com/archives/C05KQ69SHEW)
if you have specific requirements

## Limitations

- Storage Valet RDS provisions up to one RDS instance per namespace. We have stated this limitation regarding the limit of the number of RDS instances for one AWS account described in the [technical note](https://docs.google.com/document/d/173Xe8x4yd1tDMCImvpMtXb2KSqw9hawPAsTjd-qQdbo/edit#heading=h.95um4r4mvfqu).
- Storage Valet RDS targets only workloads inside clusters where Agora is also run. The IRSA is confidured for these clusters and workloads external to them cannot utilize this solution.

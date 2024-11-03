# Storage Valet RDS - Architecture

This document will explain Storage Valet RDS’s architecture. Storage Valet RDS
won’t cover every database use case, and sometimes, your setup will require you
to configure AWS resources in addition to Storage Valet RDS or doing it from
scratch. We hope this document will be helpful for those kinds of situations.

# Architecture

![Storage Valet RDS Architecture](./storage_valet_rds_architecture.svg)

Storage Valet RDS aims to help Woven City's development teams (the service
teams and the Agora internal teams) to easily get an RDS instance with some
default configurations that align with Woven City's and Woven by Toyota's
policies, such as security.

The above diagram[^1] demonstrates how Storage Valet RDS provisions resources
and integrates them with the Woven City mono-repository CI/CD pipeline.

[^1]: The diagram was drawn using Lucidchart. You can access the original data from
[here](https://lucid.app/lucidchart/bd58fa72-5ba9-4810-8ffb-80c930333caf/edit?invitationId=inv_bd722d5a-5c33-4b9c-8524-dc6fefed3fcc).

## Resource provisioning

Storage Valet RDS takes an approach to implement the solution by utilizing the
Woven City mono-repository's CI/CD pipeline. The actual deployment of Storage
Valet RDS, such as the provisioning of an RDS instance, happens when the user
merges a simple YAML-format Storage Valet RDS configuration file into the Woven
City mono-repository's `main` branch by its CI/CD pipeline.

Storage Valet RDS offers a plugin for
[Agoractl](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial)
to help create the necessary files. See more details in
[Storage Valet RDS - Manage your instance with Agoractl](https://developer.woven-city.toyota/docs/default/Component/storage-valet-rds-service/03-storage-valet-rds-agoractl/).

## Access control for RDS

Storage Valet RDS uses IRSA (IAM Roles for Service Accounts) to offer an easy
way for users to access the provisioned RDS instance.

In IRSA, AWS credentials to access the RDS instance are automatically issued
and configured for a pod that has the specific ServiceAccount by a Kubernetes
mutating webhook for IRSA.
([amazon-eks-pod-identity-webhook](https://github.com/aws/amazon-eks-pod-identity-webhook))
The webhook uses AWS STS's AssumeRole action to get the credential.
To allow the Agora EKS cluster to do IRSA, Storage Valet RDS configures the
Storage AWS account's STS to recognize the Agora EKS cluster as a trusted
AssumeRole requester.

Regarding the IRSA configurations, you can see more details from the following
documents.

* [TN-0373 BYO AWS or - using IAM Roles for Service Accounts (IRSA) to for cross-account AWS resource access](https://docs.google.com/document/d/1Uac8ESK6Uf83kNqg7vJvTcs1MM6L4S5N8Yonhr5FMlw/edit#heading=h.t2wwxeng0uj4)
* [Amazon EKS | IAM roles for service accounts](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html)
* [AWS Blog | Diving into IAM Roles for Service Accounts](https://aws.amazon.com/blogs/containers/diving-into-iam-roles-for-service-accounts/)

## Privatelinks

Beyond the near identical architecture that exists for IRSA in Storage Valet
S3, RDS requires ip routability and this solution uses the SMC standard
privatelinks to provision this routability for each RDS instance. The traffic
is routed to a Nat Load Balancer in the Agora Storage AWS account that is then
forwarded to an RDS Proxy

## Proxy Architecture

Each Storage Valet RDS resource set includes an RDS instance with an RDS proxy
in front of it that is the actual resource to which users will connect. Agora
Storage has elected to use Proxy RDS instance in front of the standard RDS
instance due to the volitility involved in the use of privatelinks.

Privatelinks fundamentally connect two sides: a Service Provider (In our case
the Agora Storage AWS services) and a Service Consumer (The SMC VPC endpoint
defined in the same VPC as the Kubernetes cluster). The Service Provider side
must set up a Nat Load Balancer to front the Services it is trying to share.
The Nat Load Balancer has IP addresses associated with the services it is
trying to share. If these resources are correctly configured the traffic will
then flow with no issue.

However, using IP addresses in this manner is fragile. If we wish to failover
to a backup RDS instance, or restore the RDS instance in a destructive manner
(for example encrypting the database), or anything else that would cause the IP
address to change then the Nat Load Balancer would no longer be pointing to the
correct IP address and would fail. As such we instead have the privatelink and
NLB apparatus point to an RDS proxy and the proxy can instead point to the RDS
instance using target groups which would support these kinds of IP changes.

In short, using RDS proxy allows us to have traffic flowing properly through a
privatelink while also allowing us to flexibly update the instances behind the
proxy without losing the privatelink connection.

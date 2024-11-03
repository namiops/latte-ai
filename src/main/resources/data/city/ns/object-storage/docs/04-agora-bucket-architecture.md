# Storage Valet S3 - Architecture

This document will explain Storage Valet S3’s architecture. Storage Valet S3
won’t cover every object storage use case, and sometimes, you need your setup
for object storage by configuring AWS resources in addition to Storage Valet S3
or doing it from scratch. We hope this document will be helpful for those kinds
of situations.

# Architecture

![Storage Valet S3 Architecture](./agora_bucket_architecture.svg)

Storage Valet S3 aims to help Woven City's development teams (the service teams
and the Agora internal teams) to easily get an S3 bucket with some default
configurations that align with Woven City's and Woven by Toyota's policies,
such as the security one.

The above diagram[^1] demonstrates how Storage Valet S3 provisions resources
and integrates them with the Woven City mono-repository CI/CD pipeline.

[^1]: The diagram was drawn using Lucidchat. You can access the original
Lucidchart data from
[here](https://lucid.app/lucidchart/752d5657-7df7-4ce3-b209-aae8de3f65d3/edit?invitationId=inv_b99d5c6f-2a1a-4d8f-8f0d-8ba23ecf0ff5&page=wtmWi24NtSpX#).

## Resource provisioning

Storage Valet S3 takes an approach to implement the solution by utilizing the
Woven City mono-repository's CI/CD pipeline. The actual deployment of Storage
Valet S3, such as the provisioning of an S3 bucket, happens when the user
merges a simple YAML-format Storage Valet S3 configuration file into the Woven
City mono-repository's `main` branch by its CI/CD pipeline.

Storage Valet S3 offers a plugin for
[Agoractl](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial)
to help create the necessary files. See more details in
[Storage Valet S3 - Manage your bucket with Agoractl](https://developer.woven-city.toyota/docs/default/Component/object-storage-service/03-agora-bucket-agoractl/).

## Access control for a bucket

Storage Valet S3 uses IRSA (IAM Roles for Service Accounts) to offer an easy
way for users to access the provisioned bucket.

In IRSA, AWS credentials to access the bucket are automatically issued and
configured for a pod that has the specific ServiceAccount by a Kubernetes
mutating webhook for IRSA.
([amazon-eks-pod-identity-webhook](https://github.com/aws/amazon-eks-pod-identity-webhook))
The webhook uses AWS STS's AssumeRole action to get the credential. To allow
the Agora EKS cluster to do IRSA, Storage Valet S3 configures the Storage Valet
S3 AWS account's STS to recognize the Agora EKS cluster as a trusted AssumeRole
requester.

Regarding the IRSA configurations, you can see more details from the following
documents.

* [TN-0373 BYO AWS or - using IAM Roles for Service Accounts (IRSA) to for cross-account AWS resource access](https://docs.google.com/document/d/1Uac8ESK6Uf83kNqg7vJvTcs1MM6L4S5N8Yonhr5FMlw/edit#heading=h.t2wwxeng0uj4)
* [Amazon EKS | IAM roles for service accounts](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html)
* [AWS Blog | Diving into IAM Roles for Service Accounts](https://aws.amazon.com/blogs/containers/diving-into-iam-roles-for-service-accounts/)

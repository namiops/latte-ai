# Storage Valet S3 - Introduction

For those that have followed this documentation closely, Storage Valet - S3
(Formerly Agora Bucket) is no longer in a draft state and is ready for
workloads on all previous and current gen clusters.

## What is Storage Valet S3

Storage Valet S3 is a solution that makes it easy for you to get a private
object storage bucket for your workload running inside the Agora Kubernetes
cluster. We use the term `a private bucket` here to mean a bucket that isn’t
opened to anonymous users.

!!!Note
    Storage Valet S3 currently focuses only on supporting a private bucket
    that is not open to anonymous users. The company’s security team tries to
    protect us from accidental data leaks by restricting the creation of a
    public object storage bucket. For example, the company’s AWS account is
    prohibited from making an S3 bucket public, except for some exceptional
    accounts that get special approval as described in
    [Cybersecurity & Privacy Portal](https://security.woven-planet.tech/standards/cloud-and-kubernetes/aws-security-standard/). 
    If you need a public bucket for some use cases, such as hosting a website
    on the bucket, please reach out to {some team} (TODO: Who? Dev/Rel?
    Storage? WC security? or EnTech?) to get some guides to take reviews for
    getting the exceptions. 

Storage Valet S3 aims to bring the following benefits for you without complex
processes and setup.

* A bucket provisioned on the Agora-managed AWS account.
* [IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) configurations to make it so your workload (pods) can transparently get short-lived, appropriately managed credentials to access to the provisioned bucket.
*  Security configurations aligned with the Woven City security policy.

You can see more details of Storage Valet S3's architecture in [Storage Valet S3 - Architecture](./agora-bucket-architecture.md).

## Concepts

![Storage Valet S3 Concept](./agora_bucket_concept.svg)

Storage Valet S3 provisions up to one bucket per Kubernetes namespace for you.

In many cases, one namespace may hold multiple workloads, and some might need
object storage. To cover those typical situations, Storage Valet S3 expects you
to use a bucket for multiple workloads in your namespace by using object key
prefixes to share one bucket with the workloads.

You can specify any number of object key prefixes you want.
Storage Valet S3 configures IRSA for each object key prefix.

The above diagram[^1] depicts an example situation in which `App A` and `App B`
store their objects with the `app_a` and `app_b` object key prefixes,
respectively.

[^1]: The diagram was drawn using Lucidchart. You can access the original data
from
[here](https://lucid.app/lucidchart/7f1eb96f-4166-4fcc-ac54-e3fc044a392f/edit?page=0_0&invitationId=inv_1881de37-5886-4ae1-99f3-4cbe3c17ac67#).

## Supported object storage solutions

Storage Valet S3 currently supports only Amazon S3.

If you have some demands to use another object storage solution, such as Woven
City's self-hosted one and Google Cloud Storage (GCS), we would appreciate it
if you share it with us. We’ll include your voice in the next planning
activity.

## Limitations

- Storage Valet S3 provisions up to one S3 bucket per namespace. We have stated this limitation regarding the limit of the number of S3 buckets for one AWS account described in [the S3 document](https://docs.aws.amazon.com/AmazonS3/latest/userguide/BucketRestrictions.html).
- Storage Valet S3 doesn't cover public buckets as described above.
- Storage Valet S3 targets only the workloads inside the Agora Kubernetes clusters. The IRSA is configured for the Agora Kubernetes clusters, and the workloads outside of the clusters can't utilize it.

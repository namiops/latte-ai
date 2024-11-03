# How to create an S3 bucket on your AWS account

This document gives instructions on how to create an S3 bucket on each team's AWS account.

This document is intended as a short-term recommendation until Agora Storage is ready to offer a general Object Storage solution.
It may be deprecated in the future in favor of the guide for <TODO: Future Object Storage solution name\>.
The instructions contained in this document are intended only for development and do not reflect the usage in production environments, as early mentioned it is expected to have Object Storage solution in production environments that will deprecated this short-term solutions..

## AWS account

The AWS account should be an EnTec-vended AWS account for your team ([the related FAQ on Agora concepts FAQ](https://docs.google.com/document/d/1A41P7TnmWUpT6FHdWekNFkkxWfwb-dHcV1q8UqBFmqA/edit#heading=h.ru48zlpxub3i)).

## FAQ: Can I make a bucket public or host a website with S3?

No, public access requires exceptional approval from the security and privacy teams.

Every EnTec-vended AWS account except for exceptional ones (belonging to `exceptions` OU) is prohibited from making S3 buckets public.
This policy was enacted to decrease the likelihood of data breaches and is recommended in [Amazon S3 security and access control best practices](https://d1.awsstatic.com/events/Summits/reinvent2022/STG301_Amazon-S3-security-and-access-control-best-practices.pdf\).
We can see the details of the AWS account configuration in "[Cybersecurity & Privacy Portal | AWS Configuration Standard](https://security.woven-planet.tech/standards/cloud-and-kubernetes/aws-security-standard/)".

Even if you manage to get exceptional approval rather than use S3 to host the website you are advised to host it with [Amazon CloudFront](https://aws.amazon.com/cloudfront/) and [AWS WAF](https://docs.aws.amazon.com/waf/index.html).
It brings more granular access control, and AWS also recommends the way as the best practice ([Amazon S3 security and access control best practices](https://d1.awsstatic.com/events/Summits/reinvent2022/STG301_Amazon-S3-security-and-access-control-best-practices.pdf)).

## Get consultation from the Security and Privacy teams

The necessary bucket configurations differ based on the stored data and its usage.
The below section shows some basic configurations and ideas, 
but you need to ensure the usage follows Woven City policies first.

Every team can speak with the privacy and security teams during the project onboarding process,
but the contact points for the teams are also listed below.

Privacy team<br/>
Kana Oshimi <kana.oshimi@woven-planet.global\>

Security team<br/>
Ismael Kane <ismael.kane@woven-planet.global\>

## Required or recommended bucket configurations

- Follow "[Tagging Standard v5](https://security.woven-planet.tech/standards/common/tagging-standard/)" offered by the security team.
- Follow "[FinOps - S3 Controls](https://security.woven-planet.tech/guides/corpsec/prodsec/aws/guides/fin-ops-s3/)" offered by the security team.
- It is recommended to enable [SSE-KMS with Amazon S3 bucket keys](https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucket-key.html) to get a cost-efficient additional security layer. Check with Security team alternatives as [DSSE-KMS](https://docs.aws.amazon.com/AmazonS3/latest/userguide/UsingDSSEncryption.html), [SSE-C](https://docs.aws.amazon.com/AmazonS3/latest/userguide/ServerSideEncryptionCustomerKeys.html) or [client-side encryption](https://docs.aws.amazon.com/AmazonS3/latest/userguide/UsingClientSideEncryption.html). By default, Starting January 5 2023, Amazon S3 managed keys (SSE-S3) are used to server-side encryption of Amazon S3.
- It is recommended to manage AWS credentials with [Vault](https://developer.woven-city.toyota/docs/default/Component/vault-tutorial/en/00_index/). Agora Storage plan to look into "[IRSA (IAM roles for service accounts)](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html)". We'll make an announcement when we're ready, and it will become another recommended way to pass the IAM role to workloads in the Agora platform. 

## References

- [Amazon S3 security and access control best practices (A talk from AWS at re:Invent 2022)](https://d1.awsstatic.com/events/Summits/reinvent2022/STG301_Amazon-S3-security-and-access-control-best-practices.pdf)

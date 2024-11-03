# Storage Valet S3 - FAQ

## How to test with my bucket?

We can test to access our bucket by deploying a pod containing AWS CLI and
attaching the provisioned ServiceAccount. The Agora Data team shares a sample
pod manifest that we can use to test the bucket in an ad-hoc manner
[here](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/modules/agora_object_storage/examples/lab2-sandbox-test/k8s_manifests/pod-agora-bucket-sandbox-test-bar.yaml).

The Agora Data team is discussing how to offer a more convenient way to test or
debug with the buckets. If you have some good ideas, please share them with us!
Your contributions are always welcome!

## Can we use AWS console for a bucket?

No, we can't.
We're discussing the support, and we'll make an announcement when we're ready.

Until the AWS console support is ready, please consider using AWS CLI from the
inside of the pod you attach the appropriate ServiceAccount.

Agora Data team backlog ticket:
[Open AWS console for Storage Valet S3 users](https://wovencity.monday.com/boards/3650470399/views/124082704/pulses/5718041791)

## Can we have a ServiceAccount for read-only or write-only users?

We plan to support it, but we can't at this moment.
We'll make an announcement when it's ready.
Please wait for it.

Agora Data team backlog ticket: [Make Storage Valet S3 able to issue read-only or write-only users](https://wovencity.monday.com/boards/3650470399/views/124082704/pulses/5580678236)

## How to make a pod able to access multiple buckets or object key prefixes?

In some cases, your workload might need to access multiple buckets or multiple
object key prefixes. If you face those kinds of situations, please reach out to
us by posting a message with the `@agora-storage` mention on
`#wcm-org-agora-ama` Slack channel.

Storage Valet S3 currently supports issuing an IAM role that has permission to
access the objects with the specified key prefix on the created bucket.
For special cases, the Agora Data team can support manually issuing an IAM role
with some additional configurations.

For example, if your workload needs to access a bucket on your team's AWS
account,
[the cross-account bucket permissions](https://docs.aws.amazon.com/AmazonS3/latest/userguide/example-walkthroughs-managing-access-example2.html)
might be an option for the additional configuration. Regarding the
cross-account bucket permissions, you can also check
[TN-0390 Agora Service access to BYOBs in multiple AWS accounts](https://docs.google.com/document/d/1SDVFjxii_78GgD3sHx6ZZOKp8-R2Uq9eSU1As_cOXH8/edit#heading=h.5qm13wuvtiz9).

# Storage Valet S3 - Quickstart

This document will cover how to start using Storage Valet S3. We expect that
you've already read
[Storage Valet S3 - Introduction](./agora-bucket-introduction.md).
If you haven't, please read it first.

The process to start using Storage Valet S3 can be divided into two steps:
- Create a bucket on Storage Valet S3.
- Deploy a ServiceAccount and attach it to your pod.

Let’s look into each process more!

## Create a bucket with Storage Valet S3

You can get your bucket by putting one YAML file in the specific folder on the
Woven City mono-repository using Agoractl. The YAML file will be generated and
put in an appropriate location on the Woven City mono-repository, so you don't
need to handle it manually.

You can create the YAML file by executing
[agoractl bucket create](https://developer.woven-city.toyota/docs/default/Component/object-storage-service/03-agora-bucket-agoractl/#onboarding-update-bucket-configuration-agoractl-bucket-create)
like this.

```bash
$ bazel run //ns/agoractl -- bucket \
    --namespace {your namespace} \
    --environment {target environment} \
    create --prefixes {prefixes you want separated by a space}
```

Regarding `--environment`, you can see the available keywords in the
[Storage Valet S3 - Manage your bucket with Agoractl](https://developer.woven-city.toyota/docs/default/Component/storage-valet-s3-service/03-agora-bucket-agoractl/#agoractl-bucket).

After executing the command, you will see a created YAML configuration file.
It’s currently required to get approval from
[the Agora Data team](https://github.com/orgs/wp-wcm/teams/agora-data) to merge
the YAML configuration file. Please feel free to ask the Agora Data team to
review your PR review on the `#wcm-org-agora-ama` Slack channel with the
`@agora-storage` mention :)

After merging the PR, your bucket will be provisioned with IAM roles for each
prefix after several minutes.

## Deploy a ServiceAccount for your workload

Storage Valet S3 supports easily and securely configuring the credentials for
your workload (Kubernetes pod) to access the space on the provisioned bucket
under the specified object key prefix.

Storage Valet S3 uses
[IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html), 
which transparently attaches short-lived and regularly-rotated credentials to a
Kubernetes ServiceAccount. So, you can easily and securely configure the
credentials just by attaching the ServiceAccount to your pod.

To use IRSA, it needs to accurately put the specific labels to the
ServiceAccount, but you don't need to handle it by yourself because
[agoractl bucket service-account](https://developer.woven-city.toyota/docs/default/Component/object-storage-service/03-agora-bucket-agoractl/#generate-serviceaccount-manifests-agoractl-bucket-service-account)
will handle it instead of you. You can execute the command like this.

```bash
$ bazel run //ns/agoractl -- bucket \
    --namespace {your namespace} \
    --environment {target environment} \
    service-account
```

The command execution outputs a Bazel target code to generate the
ServiceAccount manifests with [Zebra](https://developer.woven-city.toyota/docs/default/Component/zebra-service),
the Woven City in-house code-generating technology utilizing Bazel. You can
copy and paste the code outputted by the command to the Bazel `BUILD` file
for your namespace, and then the Woven City mono-repository CI pipeline will
generate the ServiceAccount manifests.

## Access the provisioned bucket

Now, a bucket is ready for you with related AWS resources, such as the IAM
roles for IRSA. Let’s try to access the bucket from your pod to which the
ServiceAccount for IRSA is attached.

### Check the names of your bucket and the ServiceAccounts

The bucket name and the service accounts' names are automatically decided based
on your configurations.

The S3 bucket name should be globally unique across every AWS region. So, the
name includes a hash as its postfix to reduce the risk of name conflicts.

You can see the allocated names by executing the
[agoractl bucket info](https://developer.woven-city.toyota/docs/default/Component/object-storage-service/03-agora-bucket-agoractl/#view-your-buckets-configuration-agoractl-bucket-info).

```bash
# Check your bucket name
$ bazel run //ns/agoractl -- bucket \
    --namespace {your namespace} \
    --environment {target environment} \
    info 2>&/dev/null | yq .bucketName
{your bucket name}

# Check your ServiceAccounts' names
$ bazel run //ns/agoractl -- bucket \
    --namespace {your namespace} \
    --environment {target environment} \
    info 2>&/dev/null | yq .spaces
{an object key prefix}: {the name of the ServiceAccount to access the objects under the key prefix via IRSA}
...
```

!!!Note
    Bazel sends its debug message to standard error.
    In the above command sample, we put `2>&/dev/null` to skip the debug
    message to simplify the output.

### Deploy a pod to access the bucket via IRSA

Let's deploy a pod that contains AWS CLI to access your bucket.

Here, we will use
[the official AWS CLI image](https://hub.docker.com/r/amazon/aws-cli/tags)
for the pod.
Let's write K8S manifests for the pod using the image and deploy them. Please
don't forget to attach the ServiceAccount that generated via
`agoractl bucket service-account`.

You can see sample manifests [here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/postgresql-sample/agora-bucket-test).

### Access a bucket with AWS CLI

The manifests create and attach a ServiceAccount to transparently configure the
AWS credentials for the pod to access the S3 bucket provisioned by Storage
Valet S3. The pod runs the official AWS CLI container image, and you can play
with the provisioned bucket with it just by logging in the container.

```bash
$ kubectl exec -n {your namespace} -it {your pod name} -- /bin/bash 
bash-4.2# 
```

The ServiceAccount attached to the pod is
`lab2-postgresql-sample-4bf7ca-zalando-sample-wal` that allows the pod to use
the bucket with the object key prefix `zalando-sample-wal`. Let's try to upload
some sample files and list the uploaded files with AWS CLI!

You can get more information about the AWS CLI subcommands used in this
document by executing them with the `help` argument like the following if
necessary.

```bash
bash-4.2# aws s3 cp help
```

#### Upload files

We can upload a file with `aws s3 cp`.

```bash
bash-4.2# echo "Single file upload" > single_file.txt
bash-4.2# aws s3 cp single_file.txt s3://{your bucket name}/{your object key prefix}/single_file.txt
upload: ./single_file.txt to s3://{your bucket name}/{your object key prefix}/single_file.txt
```

We can upload multiple files with `aws s3 sync`.

```bash
bash-4.2# mkdir multiple_files
bash-4.2# mkdir multiple_files/subfolder
bash-4.2# echo "Multiple file #1" > multiple_files/multiple_1.txt
bash-4.2# echo "Multiple file #2" > multiple_files/multiple_2.txt
bash-4.2# echo "A file in a subfolder" > multiple_files/subfolder/file.txt
bash-4.2# ls -R multiple_files/
multiple_files/:
multiple_1.txt  multiple_2.txt  subfolder

multiple_files/subfolder:
file.txt
bash-4.2# aws s3 sync multiple_files s3://{your bucket name}/{your object key prefix}/multiple_files
upload: multiple_files/multiple_1.txt to s3://{your bucket name}/{your object key prefix}/multiple_files/multiple_1.txt
upload: multiple_files/subfolder/file.txt to s3://{your bucket name}/{your object key prefix}/multiple_files/subfolder/file.txt
upload: multiple_files/multiple_2.txt to s3://{your bucket name}/{your object key prefix}/multiple_files/multiple_2.txt
```

#### List files

We can list files on the bucket with `aws s3 ls`.

```bash
bash-4.2# aws s3 ls --recursive s3://{your bucket name}/{your object key prefix}/
2024-01-11 00:35:02         17 {your object key prefix}/multiple_files/multiple_1.txt
2024-01-11 00:35:02         17 {your object key prefix}/multiple_files/multiple_2.txt
2024-01-11 00:35:02         22 {your object key prefix}/multiple_files/subfolder/file.txt
2024-01-11 00:09:28         19 {your object key prefix}/single_file.txt
```

!!!Note
    We must put `/` after the object key prefix when listing the files.
    For example, if we forget to put `/` in the above, `aws s3 ls` tries to
    list objects under the other prefixes, such as
    `s3://{your bucket name}/{your object key prefix}-other`,
    that the IAM role attached to the ServiceAccount doesn't have the right to
    access to.

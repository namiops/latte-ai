# Test with Lab2 East and an EnTech-vendor developer sandbox AWS account

This folder includes Terraform codes and Kubernetes manifests to test the Agora Bucket Terraform module with the Lab2 East and your EnTech-vendor AWS account.

This sample will create one S3 bucket and two ServiceAccounts in the `postgresql-sample` namespace, which is owned by the Storage project.

Here is the list of the files contained in this folder.

```sh
$ tree
.
├── README.md
├── k8s_manifests
│   ├── BUILD
│   ├── kustomization.yaml
│   ├── out
│   │   └── k8s_manifests.k8_resources
│   │       ├── kustomization.yaml
│   │       ├── serviceaccount-agora-bucket-sandbox-test-bar.yaml
│   │       ├── serviceaccount-agora-bucket-sandbox-test-foo.yaml
│   │       └── serviceentry-agora-bucket-sandbox-test-aws.yaml
│   ├── pod-agora-bucket-sandbox-test-bar.yaml
│   └── pod-agora-bucket-sandbox-test-foo.yaml
├── main.tf
├── namespaces
│   ├── BUILD
│   └── postgresql-sample.yaml
├── provider.tf
├── terraform.tfstate
└── terraform.tfstate.backup
```

## Preparation

The manifests for the ServiceAccounts include the AWS account ID.
The ID is hard-coded in [the Bazel macro](/ns/object-storage/bazel/agora_s3_bucket.bzl) for integrating Agora Bucket with Zebra.

If you need to use another namespace, you can change it by updating the following files.
- [k8s\_manifest/BUILD](k8s_manifest/BUILD): the namespace configuration for the Kubernetes manifests
- [namespaces/postgresql-sample.yaml](namespaces/postgresql-sample.yaml): the namespace configuration for AWS resources

After doing the above, you can re-generate the Kubernetes manifests with the following command.

```sh
$ bazel run //infrastructure/terraform/modules/agora_object_storage/examples/lab2-sandbox-test/k8s_manifests:k8s_manifests.k8_resources.copy
```

## Setup AWS resources

You can create AWS resources with the `terraform` command as follows.

```sh
# Refresh your developer sandbox AWS account's credentials
$ aws sso login --sso-session <your AWS SSO session name>

# Make sure you're in the example folder
$ pwd
<the root folder of our mono-repository>/infrastructure/terraform/modules/agora_object_storage/examples/lab2-sandbox-test

# Initialize the working directory for Terraform
$ terraform init

# Deploy the AWS resources
$ AWS_PROFILE=<your AWS credential profile name> terraform apply
```

The above execution will create the following types of AWS resources.
- An S3 bucket for the namespace
- A KMS key for the S3 server-side encryption for the namespace's S3 bucket
- IAM roles
- An S3 bucket to store server-side access logs of the namespace's S3 buckets

## Deploy Kubernetes resources

```sh
# Make sure if we connect to the Lab2 East
$ kubectl config current-context
lab2-worker1-east

# Deploy the ServiceAccounts and the ServiceEntry for AWS's APIs
$ kubectl apply -n postgresql-sample -k k8s_manifests --as sudo --as-group=<AAD group ID>
...
```

## Access to the bucket

```sh
# Log in to the pod that has right to access to "bar/*"
$ kubectl exec -n postgresql-sample -it agora-bucket-sandbox-test-bar -- /bin/bash

# Upload something to "bar/test.txt"
bash-4.2# echo test > test.txt
bash-4.2# aws s3 cp test.txt s3://agora-bucket-sandbox-test/bar/test.txt
upload: ./test.txt to s3://agora-bucket-sandbox-test/bar/test.txt

# Download the uploaded object
bash-4.2# aws s3 cp s3://agora-bucket-sandbox-test/bar/test.txt uploaded_test.txt
download: s3://agora-bucket-sandbox-test/bar/test.txt to ./uploaded_test.txt
bash-4.2# cat uploaded_test.txt
test

# Confirm being able to access "bar/*"
bash-4.2# aws s3 ls s3://agora-bucket-sandbox-test/bar
                           PRE bar/

# Confirm not being able to access "foo/*" 
bash-4.2# aws s3 ls s3://agora-bucket-sandbox-test/foo

An error occurred (AccessDenied) when calling the ListObjectsV2 operation: Access Denied
```

## Clean up

To delete S3 buckets, we need to delete every object in the buckets first.
Make sure every object is deleted, including the versioned ones.
We can see how to delete the versioned objects in the following document.\
[Deleting object versions from a versioning-enabled bucket](https://docs.aws.amazon.com/AmazonS3/latest/userguide/DeletingObjectVersions.html)

After deleting the objects, we can delete the AWS resources through the `terraform` command.

```sh
# Make sure you're in the example folder
$ pwd
<the root folder of our mono-repository>/infrastructure/terraform/modules/agora_object_storage/examples/lab2-sandbox-test

# Delete the AWS resources
$ AWS_PROFILE=<your AWS credentials profile> terraform destroy
```

Then, let's delete the Kubernetes resources.
```sh
# Make sure if we connect to the Lab2 East
$ kubectl config current-context
lab2-worker1-east

# Delete the ServiceAccounts and the ServiceEntry for AWS's APIs
$ kubectl delete -n postgresql-sample -k k8s_manifests --as sudo --as-group=<AAD group ID>
```

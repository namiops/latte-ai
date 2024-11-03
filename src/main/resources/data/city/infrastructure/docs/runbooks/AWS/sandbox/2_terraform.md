# deploy the Terraform code, follow these general steps:

## Install TF
Install Terraform on your local machine. You can download the installer from
the Terraform website: https://www.terraform.io/downloads.html.

## prepare TF

### verify account ###

To make sure that you're using the correct AWS account, you'll need to verify
that your AWS access key ID and secret access key correspond to the correct
account. You can do this by logging in to the AWS Management Console and
checking the account ID displayed in the top right corner of the console.

You can also use the aws sts get-caller-identity command in your terminal or
shell to check the account ID associated with your AWS credentials:

```sh
aws sts get-caller-identity --profile sandbox
```

This command will display the AWS account ID, ARN, and user ID associated with
your credentials.

### Mandatory variables ###
You __must__ provide the following variables by setting them in `terraform.tfvars`:
  - `vpc_id`

#### Example ####
```sh
echo "vpc_id=\"$(aws --region ap-northeast-1 --profile sandbox ec2 describe-vpcs --query 'Vpcs[*].VpcId' --output text)\"" > tf/terraform.tfvars
```
__This will only work if you have only one VPC. If you have multiple, just put
in the ID manually.__

### Optional variables
You may want to override the defaults in () of the following variables in
`terraform.tfvars`:
  - `aws_profile` ("sandbox"). Set this accordingly if you choose some
    other string than in the example.
  - `eks_instance_types` (["t3.medium"]). Change this, if you need other node
    types.
  - `kube_ver` ("1.22"). Set this to the desired Kubernetes version
  - `public_access_cidrs` ("103.175.111.128/25"). Change this if you want to
    allow access from other networks than our VPN `imras.ts.tri-ad.global`
  - `region` ("ap-northeast-1").

### SSH access ###
Generate a SSH key and save to ~/.ssh/sandbox
```sh
ssh-keygen -t rsa -m PEM -f ~/.ssh/sandbox
```
_You can change the options to your liking (type, password)_

### init ###

Change into the `tf` directory.

```sh
cd tf
```

```sh
export AWS_PROFILE=sandbox
```

Initialize your Terraform workspace by running the `terraform init` command in
your terminal or shell:

```sh
terraform init
```

This command will download the necessary provider plugins and initialize your
working directory.

Preview the changes that Terraform will make by running the terraform plan
command:

```sh
terraform plan
```

This command will show you a summary of the resources that Terraform will
create or modify.

Apply the changes to your AWS account by running the terraform apply command:

## create your EKS Sandbox environment ##

```
terraform apply
```
This command will create the resources in your AWS account.

Troubleshooting:

### Links ###

- [AWS automation runbook for troubleshooting](https://ap-northeast-1.console.aws.amazon.com/systems-manager/automation/execute/AWSSupport-TroubleshootEKSWorkerNode?region=ap-northeast-1#)

### Error CloudWatch Log Group ###
If you get an error like:
```
 Error: Creating CloudWatch Log Group failed: ResourceAlreadyExistsException: The specified log group already exists:  The CloudWatch Log Group '/aws/eks/sandbox/cluster' already exists.
 │
 │   with aws_cloudwatch_log_group.sandbox,
 │   on main.tf line 26, in resource "aws_cloudwatch_log_group" "sandbox":
 │   26: resource "aws_cloudwatch_log_group" "sandbox" {

```
Then you have probably recreated the cluster. You can either change the name of
the `aws_cloudwatch_log_group` resource in `main.tf` or just delete the
Cloudwatch log group from the admin console.

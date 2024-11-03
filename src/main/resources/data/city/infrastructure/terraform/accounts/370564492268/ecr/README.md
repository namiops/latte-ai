# overview

Aggregate ECR repositories into a single account, and configure each account to have permissions to that ECR
https://aws.amazon.com/premiumsupport/knowledge-center/secondary-account-access-ecr/?nc1=h_ls

# Usage

If the account is added, add arn:aws:iam

#### **`ecr.tf`**

```
data "aws_iam_policy_document" "policy" {
  statement {
    effect = "Allow"

    principals {
      type = "AWS"

      identifiers = [
        "arn:aws:iam::xxxxxxxxxxxx:root",
        "arn:aws:iam::yyyyyyyyyyyy:root",
        "arn:aws:iam::zzzzzzzzzzzz:root",
      ]
    }
```

If you want to add a repository, add the repository name.

#### **`terraform.tfvars`**

```
# If you want to add a repository, add the repository name.
ecr_repositories =  [
    "wcm-cityos/infra/aaaaa",
    "wcm-cityos/services/bbbbb",
    "wcm-cityos/kafka/ccccc",
    "wcm-cityos/poc/ddddd",
]
```

Pull image from ECR to Kubernetes manifest file

#### **`manifest.yml`**

```
apiVersion: v1
kind: Pod
metadata:
  name: sample
  namespace: sample
  labels:
    app: sample-app
spec:
  containers:
    - name: sample
      image: <aws-account-id>.dkr.ecr.<aws-region>.amazonaws.com/<image-name>:<tag>
```

The Amazon EKS worker node IAM role (NodeInstanceRole) that you use with your worker nodes must possess the following IAM policy permissions for Amazon ECR.
https://docs.aws.amazon.com/AmazonECR/latest/userguide/ECR_on_EKS.html

#### **`Note`**

```
If you used eksctl or the AWS CloudFormation templates in Getting Started with Amazon EKS to create your cluster and worker node groups, these IAM permissions are applied to your worker node IAM role by default.
```

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecr:BatchCheckLayerAvailability",
                "ecr:BatchGetImage",
                "ecr:GetDownloadUrlForLayer",
                "ecr:GetAuthorizationToken"
            ],
            "Resource": "*"
        }
    ]
}
```

The following permissions are required for the IAM User or Role to push and pull images
`arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryPowerUser`

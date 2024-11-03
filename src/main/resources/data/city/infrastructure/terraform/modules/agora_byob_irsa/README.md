# AWS Bring Your Own Bucket (BYOB) for IRSA

Sets up an IAM role that federates the principals from the Agora EKS cluster(s)
OIDC provider(s).

You can use this module to set up for one or multiple k8s cluster, depending on
your workload's requirements. Each cluster has its own OIDC provider.

OIDC providers need to be protocol-less per the requirements for a given
statement's trust. For more details refer to
the [AWS Documentation](https://docs.aws.amazon.com/eks/latest/userguide/associate-service-account-role.html)

## Example

```
module "agora_aws_oidc_1" {
  source            = "../../../../infrastructure/terraform/modules/agora_aws_oidc_provider"
  oidc_provider_url = "https://oidc.eks.ap-northeast-1.amazonaws.com/id/xxxx"
}

module "agora_aws_oidc_2" {
  source            = "../../../../infrastructure/terraform/modules/agora_aws_oidc_provider"
  oidc_provider_url = "https://oidc.eks.ap-northeast-1.amazonaws.com/id/xxxx"
}

module "agora_byob_irsa" {
  source               = "../../../../infrastructure/terraform/modules/agora_byob_irsa"
  service_account_name = "my-service-account"
  agora_oidc_providers = {
    cluster1 = {
      arn              = trimprefix(module.agora_aws_oidc_1.oidc_provider_arn, "https://")
      url              = trimprefix(module.agora_aws_oidc_1.oidc_provider_url, "https://")
      service_accounts = [
        "my-namespace/my-service-account1", "my-namespace/my-service-account2"
      ]
    },
    cluster2 = {
      arn              = trimprefix(module.agora_aws_oidc_2.oidc_provider_arn, "https://")
      url              = trimprefix(module.agora_aws_oidc_2.oidc_provider_url, "https://")
      service_accounts = [
        "my-namespace/my-service-account1", "my-namespace/my-service-account2"
      ]
    }
  }
}
```

## Requirements

| Name      | Version |
|-----------|---------|
| aws       | >= 5.3  |
| terraform | >= 0.13 |

## Providers

| Name                                                                | Version |
|---------------------------------------------------------------------|---------|
| [aws](https://registry.terraform.io/providers/hashicorp/aws/latest) | >= 5.3  |
| [tls](https://registry.terraform.io/providers/hashicorp/tls/latest) | >= 4.0  |

## Resources

| Name                                                                                                                                                  | Type        |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| [aws_iam_policy_document.byob_assume_role_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_role.byob_account_federated_role](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role)                      | resource    |

## Inputs

| Name                 | Description                                                                                                                                | Type        | Default | Required |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------|-------------|---------|:--------:|
| agora_oidc_providers | object that describes the ARN and URL for the OIDC providers to establish trusted providers. URLS MUST omit the protocol (e.g. 'https://`) | map(object) | `null`  |   true   |
| service_account_name | name of the service account to label the trust poilcy                                                                                      | string      | `null`  |   true   |

## Outputs

| Name                              | Description              |
|-----------------------------------|--------------------------|
| byob_acocunt_assume_iam_role_name | Name of IRSA assume role | 
| byob_account_assumed_iam_role_arn | ARN of IRSA assumed role |

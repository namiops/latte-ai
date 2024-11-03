# Accessing External S3 via IRSA setup example

## Description
This example displays how serverless access with Agora External S3 bucket.

## Setup
To accessing S3 bucket inside Agora K8S serverless we need to setup policies in external AWS account you can either

* External AWS account: to setup the account that own bucket choose between,
  * Run agora_byob_irsa terraform module, (Still in PR As of Oct 23rd, check out the branch https://github.com/wp-wcm/city/pull/8765)
  * Manually create https://docs.aws.amazon.com/eks/latest/userguide/associate-service-account-role.html

* Agora Serverless: Setup serverless configuration
  * This sample is created with `agoractl-serverless` command, see detail in [agoractl-serverless doc](../../../../../../ns/agoractl/docs/plugins/07_agoractl_serverless.md)
```sh
  agoractl-serverless create service \
    --name=access-byob-s3-service-sample \
    --namespace=serverless \
    --application_at=/ns/serverless/demo/access-byob-s3-service-sample \ 
    --application_language=go \
    --with_additional_configuration
```
  * Create ServiceAccount and annotate  `eks.amazonaws.com/role-arn` with role from above step, 
```
eks.amazonaws.com/role-arn: arn:aws:iam::<YOUR_ACCOUNT_ID>:role/<YOUR_ROLE_NAME>
```
  see [byob-service-account.yaml](./byob-service-account.yaml) for example. In this sample, the byob-service-account.yaml is manually wired in `serverless_kustomization` target at `external_resources` see [build file](./BUILD)
  * Open [additional-configuration.yaml](./additional-configuration.yaml) file and attach ServiceAccount using `templateServiceAccountName` in additional configuration, e.g.
  ```yaml
#@data/values
#@overlay/match-child-defaults missing_ok=True
---
templateServiceAccountName: serverless-access-byob-s3-service-account
  ```

## Service usage
Access the service via `https://access-byob-s3-service-sample-serverless-lambda.agora-lab.woven-planet.tech/`
it will list the item in S3 bucket

```sh
curl https://access-byob-s3-service-sample-serverless-lambda.agora-lab.woven-planet.tech/
```
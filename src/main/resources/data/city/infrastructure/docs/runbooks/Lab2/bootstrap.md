# Bootstrap a new environment and AWS accounts

* [Bootstrap a new environment and AWS accounts](#bootstrap-a-new-environment-and-aws-accounts)
  * [Naming convention](#naming-convention)
  * [Steps](#steps)
    * [1 AWS accounts](#1-aws-accounts)
      * [1.1 Provision new accounts](#11-provision-new-accounts)
      * [1.2 Update profile config](#12-update-profile-config)
    * [2 Setup Automation](#2-setup-automation)
      * [2.1 AzureAD groups](#21-azuread-groups)
      * [2.2 Github Actions](#22-github-actions)
    * [3 Add terraform code](#3-add-terraform-code)
      * [3.1 Bootstrap](#31-bootstrap)
      * [3.2 IPAM](#32-ipam)
        * [3.2.1 Create a provider](#321-create-a-provider)
        * [3.2.2 Create a terraform file for IPAM](#322-create-a-terraform-file-for-ipam)
        * [3.2.3 Add a RAM Principal](#323-add-a-ram-principal)
      * [3.3 TGW and VPC](#33-tgw-and-vpc)
        * [3.3.1 Create terraform files for TGW and VPC](#331-create-terraform-files-for-tgw-and-vpc)
        * [3.3.2 Update IPAM in the transit account](#332-update-ipam-in-the-transit-account)
        * [3.3.3 Add provider](#333-add-provider)
        * [3.3.4 Deploy changes](#334-deploy-changes)
        * [3.3.5 Add VPC link](#335-add-vpc-link)
      * [3.4 EKS](#34-eks)
      * [3.5 Vault secrets](#35-vault-secrets)
    * [4 Kubernetes bootstrap](#4-kubernetes-bootstrap)
      * [4.1 Deploy Flux and base manifest files](#41-deploy-flux-and-base-manifest-files)
      * [4.2 Deploy Secrets for Flux Source](#42-deploy-secrets-for-flux-source)
      * [4.3 Set up Gloo Mesh](#43-set-up-gloo-mesh)
        * [4.3.1 Configure the management cluster](#431-configure-the-management-cluster)
        * [4.3.2 Configure your worker cluster](#432-configure-your-worker-cluster)

## Naming convention

- Description:
  - Format: `<environment>2 <name> <region>`
  - Ex: `Dev2 MLOps 1 East`
- Account name & VPC name:
  - Format: `agora-<environment>2-<name>-<region>`
  - Ex: `agora-dev2-mlops1-east`

## Steps

### 1 AWS accounts

#### 1.1 Provision new accounts

**NOTE:** It might take around 10 minutes to provision each AWS account.

**WARNING:** Make sure to choose `woven/services/dev` as OU, since the default one (sandbox) is not considered to be moved to any other OUs.

- Visit https://devops.tri-ad.tech/
- Create transit account
  - `Create a CONNECTED VPC`: checked ✅
  - Line of business code: 414
  - Function code: 5431
  - Cost center ID: 40702
  - Project Code: 0000
  - Product Code: 0000
  - Organizational Unit: `woven/services/dev`
 
  ![](images/transit_account_creation.png)

- Create management and worker accounts
  - `Create a CONNECTED VPC`: unchecked 🔲
  - Line of business code: 414
  - Function code: 5431
  - Cost center ID: 40702
  - Project Code: 0000
  - Product Code: 0000
  - Organizational Unit: `woven/services/dev`
 
  ![](images/worker_account_creation.png)

- EnTec is responsible for moving accounts so create a ticket on [Service Now](https://now.woven.tech) to move previously created accounts to the appropriate Agora Organization Unit (OU). The ticket should include: 
  - The AWS account number
  - The target OU: For example, `woven-agora/dev`.
  - **NOTE:** Other Agora OUs can be found at [Organization Units](https://security.woven-planet.tech/standards/cloud-and-kubernetes/aws-security-standard/)
- Verify that the accounts have been moved from woven/services/dev to the new organization unit. One simple way to verify is checking EC2 dashboard. If the accounts have been moved, you will be able to see all available status without `N/A` status.
- (optional) Follow [Tagging Standard](https://security.woven-planet.tech/standards/common/tagging-standard/) according to the security guidelines.
- (optional) [Update account owners](https://security.woven-planet.tech/guides/corpsec/prodsec/aws/guides/aws-change-account-ownership/)
- (optional) [Update account notification channel](https://security.woven-planet.tech/guides/corpsec/prodsec/aws/guides/aws-add-slack-notifications-to-channel/)

#### 1.2 Update profile config

Update AWS profile configs at [aws_config.ini](../../../terraform/environments/dev2/aws_config.ini)

- Add a profile with a new account ID and a region.

  ```ini
  [profile dev2-<account_name>-<region>]
  region = <aws_region>
  # output = json
  sso_start_url = https://woven.awsapps.com/start
  sso_region = ap-northeast-1
  sso_account_id = <account_number>
  sso_role_name = AdministratorAccess
  ```

- **NOTE:** sso_region must be ap-northeast-1 since SSO is set up only on ap-northeast-1 by EnTec.

### 2 Setup Automation

#### 2.1 AzureAD groups

**NOTE:** Only the AzureAD group owner can perform this step. More explanation is in [AAD - Import existing AzureAD resources to Terraform](../aad/import-to-terraform.md)

- DevOps pipeline creates these two groups for AWS SSO. Group names:

  ```
  aws-<account_number>-AdministratorAccess
  aws-<account_number>-ViewOnlyAccess
  ```

- Add Terraform client's service principal as the following (used by Github action runner) to each AAD group as an owner.
  - Service Principal object IDs for DEV and PROD are available at [Import existing AzureAD resources to Terraform](../aad/import-to-terraform.md#import-existing-azuread-resources-to-terraform)

  - To add a group owner via Azure CLI:
  
    ```bash
    az login
    export ACCOUNT_NUMBER=<ACCOUNT_NUMBER>
    export SP_OBJECT_ID=<SP_OBJECT_ID>
    az ad group owner add -g aws-${ACCOUNT_NUMBER}-AdministratorAccess --owner-object-id ${SP_OBJECT_ID}
    az ad group owner add -g aws-${ACCOUNT_NUMBER}-ViewOnlyAccess --owner-object-id ${SP_OBJECT_ID}
    ```

#### 2.2 Github Actions

- Setup Github action pipeline (protected-workflow)
  - Create workflow. [Example](https://github.com/wp-wcm/protected-workflows/blob/main/.github/workflows/dev2_terragrunt_plan.yaml)
  - Update prow to include the workflow. [Here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/ci/prow/emu/configmaps/pony.yaml)

### 3 Add terraform code

#### 3.1 Bootstrap

- Bootstrap account with terraform (starting from transit account)
  - Copy bootstrap directory from other environments i.e. Lab2
  - Comment out s3 backend as this and KMS key do not exist yet
  - Comment out an assumable role in provider when necessary
  - (By the AWS profile of the target account) Apply it manually and take notes of KMS key id by using a local Terraform state
  - Uncomment backend block and update KMS key id
  - Uncomment an assumable role in provider
  - (By transit account's profile) Upload the Terraform state to the bucket specified in `backend.tf`
    - **NOTE:** Always use transit account's profile since it owns the bucket.
    - Command: `terraform init -migrate-state`
  - Update [common.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/dev2/common.yaml), specifically:
    - Account ID
    - SSO Role (found in AWS IAM)
    - ASN (the number must be sequential following from the ASN of the latest-created accounts)
    - vpc-cidr
  - Update [aws_sso/config.yaml](../../../terraform/environments/dev2/bootstrap/aws_sso/config.yaml)
    - Add 2 sets of AAD group name and ID for accessing AWS accounts via SSO
      - Admin access: `aws-<account_numeer>-AdministratorAccess`
      - ViewOnly access: `aws-<account_numeer>-ViewOnlyAccess`
    - **NOTE:** you can get group ID by az cli. For example:
 
      ```bash
      $ az ad group show -g aws-<account_number>-AdministratorAccess | jq id
      $ az ad group show -g aws-<account_number>-ViewOnlyAccess | jq id
      ```
  - In `aws_sso` folder, import the existing 2 AAD groups to Terraform state so that the CI pipeline won't try to create new groups.
    - Commands:
    
    ```bash
    $ terraform import 'module.aws_sso_group["<account_name>-admins"].azuread_group.this' `az ad group show -g aws-<account_number>-AdministratorAccess | jq id`
    $ terraform import 'module.aws_sso_group["<account_name>-viewers"].azuread_group.this' `az ad group show -g aws-<account_number>-ViewOnlyAccess | jq id`
    ```

    - For example,
    
    ```bash
    $ terraform import 'module.aws_sso_group["worker1-east-admins"].azuread_group.this' `az ad group show -g aws-074769536177-AdministratorAccess | jq id`
    $ terraform import 'module.aws_sso_group["worker1-east-viewers"].azuread_group.this' `az ad group show -g aws-074769536177-ViewOnlyAccess | jq id`
    ```

#### 3.2 IPAM

For this step, you can follow changes in these PRs as example:
  - [PR #12995](https://github.com/wp-wcm/city/pull/12995)

##### 3.2.1 Create a provider

- Add a new provider config to `ipam/provider.tf`

##### 3.2.2 Create a terraform file for IPAM

- In `ipam` folder, create a file for each new account:
  - `<account_name>-<region>.tf`

##### 3.2.3 Add a RAM Principal

- In `common.yaml`, append a new account number to `ipam` config. Make sure to add it to the correct region.


```
ipam:
  regions:
    <region>:
      ram_principals:
        - ...
        - <new_account_number>
```

#### 3.3 TGW and VPC

For this step, you can follow changes in these PRs as example:
  - [PR #18075](https://github.com/wp-wcm/city/pull/18075).
  - [PR #18141](https://github.com/wp-wcm/city/pull/18141). (depends on the first PR)

##### 3.3.1 Create terraform files for TGW and VPC

- In `base` folder, create 2 files for each new account:
  - `<account_name>_<region>-tgw.tf`
  - `<account_name>_<region>-vpc.tf`
    - **NOTE:** You need to add a dummy variable for EKS cluster name. Remove the dummy variable later when EKS is created.
- Add new account data and resources to `base/data.tf`:
  - local variables related to `ipv6_cidrs`
  - resource `aws_caller_identity`
  - resource `aws_iam_session_context`

##### 3.3.2 Update IPAM in the transit account

- At `ipam/transit-east.tf`, add IPAM discovery ID of new accounts to `ipam_resource_discovery_associator`.

##### 3.3.3 Add provider

- Add a new provider config to `base/provider.tf`

##### 3.3.4 Deploy changes

- Create a pull request to deploy the changes.
- This is required before we create VPC link, since Terraform cannot solve dependencies between VPC and VPC link correctl.

##### 3.3.5 Add VPC link

- Add new VPC to local variables in `base/data.tf`
  - At a map named `east_vpcs` or `west_vpcs`
- In `base` folder, create `<account_name>_<region>-vpc_link.tf`.
- Create a pull request to deploy the VPC link

#### 3.4 EKS

**NOTE:** For this step, you can follow changes in these PRs as example:
  - [PR #13265](https://github.com/wp-wcm/city/pull/13265).

- In `base`, create a new EKS module at `<name>_<region>-eks.tf`
  - Ex. `mlops1_east-eks.tf`
- Remove any dummy variable in the previously-created VPC file (`<account_name>_<region>-vpc.tf`).

#### 3.5 Vault secrets

**NOTE:** For this step, you can follow changes in these PRs as example:
  - [PR #14448](https://github.com/wp-wcm/city/pull/14448).

We set up a new public key infrastructure (PKI) for Gloo Mesh on Vault.

- In `base`, create a new EKS module at `<name>_<region>-vault.tf`

### 4 Kubernetes bootstrap

#### 4.1 Deploy Flux and base manifest files

- Install required tools:
  - yamlfmt: `sudo snap install yamlfmt`
  - jq: `sudo apt install jq`
- Generate files by a script [new_cluster](../../../k8s/bin/new_cluster). For example:

```bash
cd infrastructure/k8s
./bin/new_cluster -c agora-dev2-mlops1-east-cluster -d environments/dev2/clusters/mlops1-east -p dev2-mlops1-east -r ap-northeast-1 -e dev2 -D agora-dev.w3n.io
```
- Manually apply `flux-system` folder. For example:

```bash
kubectl --as sudo --as-group aad:0f158ca2-948a-4d79-83b1-f21380bd16aa -k infrastructure/k8s/environments/dev2/clusters/mlops1-east/flux-system
```

- Create a PR with those files.

#### 4.2 Deploy Secrets for Flux Source

The remaining manifest files from the previous step cannot be deployed, until we set up a credential for Flux.
Refer to [Flux - Prepare GitHub Secrets](../flux/flux.md#prepare-github-secrets) to set up the secret.

#### 4.3 Set up Gloo Mesh

**NOTE:** For this step, you can follow changes in these PRs as example:
  - [PR #14362](https://github.com/wp-wcm/city/pull/14362)

##### 4.3.1 Configure the management cluster

In `gloo-mesh` folder, make these changes:
- Create a new `KubernetesCluster` object named `<name>-<region>`.
- Add the new cluster to `IstioLifeCycleManager` object.

##### 4.3.2 Configure your worker cluster

- Add `gloo-mesh` and `istio-system` folders

# AWS Setup

## Creating the AWS Account

To create a new AWS Account, head to the `DevOps Portal` on Okta. If you cannot see the
`DevOps Portal`, contact IT in `#wit-service-desk` to get it set up. Under `AWS` > `Accounts`,
you can create a new AWS Account. Make sure you choose the appropriate Organization Unit (OU).
The OU will determine what restrictions you have on your AWS Account. To see what each OU can do,
check out the security team's [guide](https://security.woven-planet.tech/information-security-policy/aws/security-standard/#organizational-units-ou).

Once you create the account, you can manage its access under `AWS Group Management`. This will
allow you to give permissions to other developers who need to use it.

## Setting Up Local Authentication

For local development, it is beneficial to set up authentication using the aws-cli.

See [this guide provided by IT](https://docs.woven-planet.tech/cloud_infrastructure/IEKB/AWS-Support---How-to-use-AWS-CLI-with-Okta-Authentication/)

## Setting Up Authentication with the Cluster

Currently, authentication and connection from AWS accounts are done manually. If you wish to connect your service in
the CityOS cluster to your AWS account, please contact `@wcm-cicd` team in `#cicd-backend-wcm` on Slack.

## Managing AWS Resources With Terraform

It is possible to manage your AWS resources with terraform. This helps in keeping track of the resources in your AWS
account for security and auditing purposes as well as making the management of resources much easier.

### Setting Up Authentication with Github
<!-- 
TODO: Write guide on setting up auth with Github
https://jira.tri-ad.tech/browse/WCMDO-61
-->
Coming Soon!

### Terraform Automation

When making a pull request that changes a terraform configuration, a terraform job will run in the CI.
This job will run `terraform fmt`, `terraform init`, `terraform validate`, and `terraform plan`.
The results will be commented on the PR by the CI. **MAKE SURE TO REVIEW THE PLAN BEFORE MERGING.** Once
merged, the CI will trigger and run `terraform apply` and apply the changes. Make sure to monitor the job
after merging to make sure the apply was successful.

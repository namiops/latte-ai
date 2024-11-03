# Create AWS account using the DevOps Vending Machine

## References
  - [Official DOC](https://docs.woven-planet.tech/engineering_software/devops-portal/support/How-to-create-a-new-AWS-account/# "WovenIT Documentation")
  - [Cybersecurity & Privacy Portal on AWS Configuration Standard](https://security.woven-planet.tech/information-security-policy/aws/security-standard/ "WovenIT Documentation")

## TL;DR

1. Log into the [DevOps Portal](https://devops.tri-ad.tech/) and go to the [AWS section](https://devops.tri-ad.tech/aws/account)
2. Click on "create"
3. Fill out the form
  - Our organization code is `AC810`. You can also lookup codes [here](https://docs.google.com/spreadsheets/d/1DrT0veBi-iHQAyWta312qAb7LcWi1CpMykrd2NwMR3w/edit#gid=1707432895)
  - Description: Can be anything you want.
  - Account Name: Use something that makes it easy to identify what this account is used for.
  - Region: In most cases this should be `ap-northeast-1`
  - Organizational Unit: `woven/developers-sandbox`
  - Account Type: `Sandbox`
  - VPC Name: Can be anything. __Should be descriptive.__
4. Wait for the account to be provisioned. You will receive a notification in Slack when the account is ready. After receiving the notification, the account information is synchronized between Azure AD and AWS SSO, which may take 45 minutes at the longest.
5. Go to [woven.awsapps.com/start](https://woven.awsapps.com/start) and log in with your new account.
6. Configure AWS CLI access following the [official documentation](https://docs.aws.amazon.com/cli/latest/userguide/sso-configure-profile-token.html) (or read the below TL;DR)

## AWS CLI TL;DR

### Shell ENV ###

Configure your AWS credentials. You can set your AWS access key ID and secret
access key using environment variables or a shared credentials file. For
example, you can set the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
environment variables in your terminal or shell:

```sh
$ export AWS_ACCESS_KEY_ID=<your-access-key-id>
$ export AWS_SECRET_ACCESS_KEY=<your-secret-access-key>
```

### manually editing credentials file ###

Alternatively, you can create a shared credentials file ~/.aws/credentials with
the following contents:

#### ~/.aws/config ####
Replace `000000000000` with your account ID

```ini
[profile AdministratorAccess-000000000000]
sso_session = sandbox-kube1.22-upgrade
sso_account_id = 000000000000
sso_role_name = AdministratorAccess
region = ap-northeast-1
output = yaml
sso_start_url = https://woven.awsapps.com/start
sso_region = ap-northeast-1
sso_registration_scopes = sso:account:access

[profile sandbox]
sso_start_url = https://woven.awsapps.com/start
sso_region = ap-northeast-1
sso_account_id = 000000000000
sso_role_name = AdministratorAccess

[sso-session sandbox]
sso_start_url = https://woven.awsapps.com/start
sso_region = ap-northeast-1
sso_registration_scopes = sso:account:access
```

#### ~/.aws/credentials ####

Replace <your-access-key-id> and <your-secret-access-key> with your AWS access
key ID and secret access key obtained from IAM-> Users -> create new user -> Attach policies directly -> AdministratorAccess. Then go to user -> security credentials and create access key


```ini
[sandbox]
aws_access_key_id = <your-access-key-id>
aws_secret_access_key = <your-secret-access-key>
```

### Test ###
Verify that you can log in. In the example above, this would be:

```sh
aws sso login --profile sandbox
```

## Example
![screenshot of example AWS account with fields filled](images/example_aws_form.gif "example_aws_form.gif")

## Adding users
If you want to share your environment with somebody, here the TL;DR for adding
a user.

1. Go to `IAM service` on the AWS management console
2. Under access management, click on `Users`.
3. Add a new user and attach the `AdminstratorAccess` policy in the `Set
   permissions` step by choosing `Attach policies directly` in the `Permissions
   options`.
4. Return to the `Users` page and click on the newly added user.
5. Under the `security credentials` tab, click `Create access key`.

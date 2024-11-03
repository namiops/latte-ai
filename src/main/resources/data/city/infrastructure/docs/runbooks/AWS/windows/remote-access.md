# Remote Access to Windows EC2

## 1 Example IaC

- [Windows VM for VisionAI team](../../../terraform/modules/agora_aws_vai_vms_vm)

## 2 RDP with Fleet Manager

The Remote Desktop Protocol (RDP) is a common way to access remote Windows machines.
You can gain access to Windows GUI by RDP.
AWS provides RDP sessions in AWS System Manager Fleet Manager (AKA. Fleet Manager).

### 2.1 Prerequisite

- Grant an IAM policy with Fleet Manager permissions to EC2
- Configured EC2 with AWS Systems Manager Agent.
  - NOTE: The agent is installed by default, if you use an official AMI provided by AWS

More details in [Getting started with Fleet Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/fleet-getting-started.html)

### 2.2 To connect

- In AWS console, go to Fleet Manager dashboard (ex. [Fleet Manager in Tokyo region](https://ap-northeast-1.console.aws.amazon.com/systems-manager/fleet-manager?region=ap-northeast-1#))
- Select your EC2. Then, click 
  - Node Actions button
  - Connect
  - Connect with Remote Desktop
- Choose an authentication type:
  - User credentials
  - Key pair
    - Provide a TLS private key to decrypt a password.
    - You can get the TLS private key from a Terraform state file.
  - Single sign-on
    - This method takes a name from your SSO account to create a local user.
    - The local Windows user is named `sso-{firstname}.{lastname}`.
    - However, the length of a username is limited to 16 characters. Then, those who have a long name cannot log in with this method.

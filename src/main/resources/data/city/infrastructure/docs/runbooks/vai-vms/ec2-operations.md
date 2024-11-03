# EC2-related Operations

* [EC2-related Operations](#ec2-related-operations)
  * [Create Windows instance from our custom Windows AMI](#create-windows-instance-from-our-custom-windows-ami)
  * [Reset Windows instance's password](#reset-windows-instances-password)
    * [Setting a password by PowerShell](#setting-a-password-by-powershell)
    * [Generating a password by AWS EC2Launch](#generating-a-password-by-aws-ec2launch)
      * [Generating passwords of all instances in one run](#generating-passwords-of-all-instances-in-one-run)

## Create Windows instance from our custom Windows AMI

We can create a new AMI based on running Windows instances and share it across account by [the module](../../../terraform/modules/agora_aws_vai_vms_ami).
See the example in [dev2/base/mlops1_east-ec2.tf](../../../terraform/environments/dev2/base/mlops1_east-ec2.tf).

However, the new Windows instance does not have a password for Administrator by default.
Follow the steps in [Reset Windows instance's password](#reset-windows-instances-password)

## Reset Windows instance's password

### Setting a password by PowerShell

You can simply remote access to a Windows instance and run the below PowerShell command:

```powershell
net user Administrator "new_password"
```

However, by this approach, we can retrieve the password by AWS console and difficult to automate password sharing.

Reference: https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-windows-passwords.html#change-admin-password

### Generating a password by AWS EC2Launch


Generally, `EC2Launch` generates the default strong password for Windows instances.
After an instance is created from a custom Windows AMI, we need to force `EC2Launch` to generate the password by shutting down without Sysprep.
You can do that clicking button `Shutdown without Sysprep` as shown in [EC2Launch UI](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2launch-v2-settings.html#ec2launch-v2-ui)

or

run the following PowerShell command

```powershell
Start-Process -FilePath "$env:ProgramFiles\Amazon\EC2Launch\EC2Launch.exe" -Argument 'reset' -Wait
Stop-Computer -Force
```

**NOTE:** To reboot, run `Restart-Computer -Force` instead.

#### Generating passwords of all instances in one run

AWS Systems Manager Run Command can be used to run a shell/powerscript on all instances managed by Systems Manager.
You can follow the below steps to reset Adminstrator's password on all instances:

1. On AWS console, go to Systems Manager.
2. Go to Node Management > Run Command
3. Click the button `Run command`
4. Select the command document `AWS-RunPowerShellScript`. You can search it by `shell` keyword.
5. In `Command parameters` section, add these commands

```powershell
Start-Process -FilePath "$env:ProgramFiles\Amazon\EC2Launch\EC2Launch.exe" -Argument 'reset' -Wait
Restart-Computer -Force
```

6. In `Target selection` section, choose your target instances. You may specify the below tags to select EC2 instances managed by Terraform.

```
woven:agora-tenant=vision-ai
woven:org-code=AC810
woven:env=prod
woven:deployment=tf
```

7. In `Output options` section, uncheck 🔲 `Enable an S3 bucket`.
8. Then, click the button `Run`. After that, you will see the command status like below screenshot.

![command status](./static/resetting_password_command_status.png)

9. It would take around 15 minutes, until you can decrypt a password from AWS console. 


References:

- `For Windows Server 2022 and later, EC2Launch v2 generates the default password.` from https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-windows-passwords.html
- https://answers.microsoft.com/en-us/windowserver/forum/all/not-able-to-perform-shutdown-without-sysprep-using/e218e233-f3cf-4993-b987-4b83985a7bf3

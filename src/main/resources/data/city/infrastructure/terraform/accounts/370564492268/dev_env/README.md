# CityOS Dev Environments

This terraform module contains code to create CityOS developer VMs.

In order to connect to vm via ssh, it requires a ssh key configured so this module use 
Github integration to read ssh public key added to your account (it will read the first key if there are multiple keys).

## How to add a new Dev VM for a user

Add an entry to [users.auto.tfvars.json](users.auto.tfvars.json)

Entries have the following format

```
{
  "github_login":  "example-user",
  "instance_name": "example",
  "user":          "example",
  "instance_type": "m5.xlarge",
  "shell":         "/bin/bash"
}
```

Add an entry to the end of the list of entries, commit the change to a branch and open a PR.

Get the PR reviewed by a member of the Agora Infrastructure team team. 

## Logging into the instance

You will need saml2aws installed and configured correctly


## Updating ssh keys

The public key from Github will be created as `aws_key_pair` and will be configured to `~/.ssh/authorized_keys`
on the **first time boot only**, so if you wish to change the key it has to be done manually.

If you still has access to your vm, you can update `~/.ssh/authorized_keys` to add your new key.

If you lost access to your vm, please follow this steps
1. Go to aws lab account. From [here](https://woven.awsapps.com/start#/), select `370564492268`
1. Go to [ec2 page](https://ap-northeast-1.console.aws.amazon.com/ec2/home)
1. Click `instances`
1. Find your instance, tick the checkbox on the left side and click `connect` on the top right of the page
1. Choose `Session Manager` and click `connect`
1. You will be redirected to new page with cli
1. Type `sudo su {your_username}` and enter
1. Now you are logged in as you normally do when using ssh
1. Update the `~/.ssh/authorized_keys` to add your new key

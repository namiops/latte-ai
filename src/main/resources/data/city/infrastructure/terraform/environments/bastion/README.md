# Bastion account

Terraform code Bastion account containing Developer VMs.

## Requrirements
  - [aws-cli](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) >= 2.12

## Add your VM

Append your VM settings to the [common.yaml](common.yaml) under `instances:`
file as below, replacing the values in between '< >'.

```yaml
instances:
# ...
  - username: < your preferred user name (must be unique in this yaml) >
    github_username: < your stargate github username>
    instance_name: (optional) default: agora-dev-vm-< username >
    instance_type: < A reasonable-sized instance type >
    shell: < Your preferred shell (bash|zsh) >
# ...
```

Once your change has been merged into main, it will be applied automatically by
the pipeline.

Your user will be granted access to the `028081328740` / `ag-bh-lab2-nKr4UIZ`
account automatically. This can take up to 45 minutes. If you don't see this
account appearing in your [aws start page](https://woven.awsapps.com/start#/),
log out from your MS account and re-login.

## Accessing your instance

### aws config
You need to configure the account to use the `aws-cli`.
There are many ways to do that, here an example config for putting in your `~/.aws/config`

_Below an example_
```yaml
[profile 028081328740VMAccess]
sso_session = woven
sso_account_id = 028081328740
sso_role_name = 028081328740VMAccess
region = ap-northeast-1
output = yaml
```
_The above expects the section to below to be present too._
```yaml
[sso-session woven]
sso_start_url = https://woven.awsapps.com/start
sso_region = ap-northeast-1
sso_registration_scopes = sso:account:access
```
Confirm you can authenticate:
```sh
aws sso login --profile 028081328740VMAccess
```

### instance ID
You can get your instance ID by querying ec2 for e.g. your username:
```sh
aws ec2 describe-instances --profile 028081328740VMAccess --filter 'Name=tag:Name,Values=*<YOUR_USER>' --query 'Reservations[-1].Instances[0].InstanceId' --output text
```

### ssh config
Here an example of a `~/.ssh/config` entry.

Replace `<YOUR_USER>` with your username that you configured for your instance.
Replace `<YOUR_INSTANCE_ID>` with the instance ID you want to connect to.
```ini
Host ec2-dev
    User <YOUR_USER>
    ProxyCommand aws ssm start-session --profile 028081328740VMAccess --target <YOUR_INSTANCE_ID> --document-name AWS-StartSSHSession --parameters portNumber=%p
    SetEnv LC_CTYPE=C.UTF-8
```
### SSM session
In case ssh is not working (missing keys, config error), this might be useful.

```sh
aws ssm start-session --profile 028081328740VMAccess --target <YOUR_INSTANCE_ID>
```
The ssm-user has root access via `sudo`.

### (Re)starting
#### WebUI
1. Go to aws lab account. From [here](https://woven.awsapps.com/start#/), select `028081328740` / `ag-bh-lab2-nKr4UIZ`
2. Click the `028081328740VMAccess` link to sign in to the AWS Console website.
3. Go to [ec2 page](https://ap-northeast-1.console.aws.amazon.com/ec2/home)
4. Click `instances`
5. Find your instance, tick the checkbox on the left side and click `start` on the top right of the page

#### CLI
```sh
aws ec2 start-instances --profile 028081328740VMAccess  --instance-ids <ID of your instance>
```

## Known issues

### SSH to my EC2 is slow or a harddisk for your home directory is not mounted

If you experienced these issues:

- Opening SSH connection takes more than 90 seconds.
- There is no dedicated volume for your home directory. If you cannot see your home path from the following command, it means that your dedicated home volume is not mounted:

    ```shell
    $ df -h
    ...
    /dev/nvme1n1     295G  108K  280G   1% /home/<your_username>
    ```

The cause of issue is that the UUID of your volume in `/etc/fstab` is set with an empty string.

```shell
$ cat /etc/fstab
LABEL=cloudimg-rootfs	/	 ext4	defaults,discard	0 1
LABEL=UEFI	/boot/efi	vfat	umask=0077	0 1
UUID=	  /home/<your_username>	ext4	defaults,nofail	0 2
```

#### Solution

The issue has been fixed in the new pipeline, but the existing VMs require manual steps to fix and migrate your data. Please follow these steps to fix this issue and move your data from the old home directory to the new volume:

1. Mount the dedicated volume

    ```shell
    sudo mount /dev/nvme1n1 /mnt
    ```

2. Copy your data to the new volume with preserving file permissions. Then, confirm that your files are in `/mnt` (especially your SSH keys in `/mnt/.ssh`).

    **NOTE:** It's recommended to run the command in a terminal multiplexer like `tmux` or `screen`, since it takes time to copy these data between disks.

    **⚠️WARNING:⚠️** Make sure to add the slash `/` at the end of the source path `/home/<your_username>/`. Otherwise, rsync creates a new folder at `/mnt/<your_username>` and your files will be copied to this wrong path.

    ```shell
    sudo rsync -aurvP --info=progress2 /home/<your_username>/ /mnt
    ```

3. Make sure that there is a dedicated volume for the home directory is labelled with `DEV_VM_HOME`. You can check it by the following command. You should see a volume `/dev/nvme1n1` with `LABEL=DEV_VM_HOME`.

    ```shell
    $ blkid
    ...
    /dev/nvme1n1: LABEL="DEV_VM_HOME" UUID="<SOME_UUID>" TYPE="ext4"
    ```

4. Update your home directory entry in `/etc/fstab` by replacing `UUID=` with `LABEL=DEV_VM_HOME`.

    ```shell
    sudo vim /etc/fstab
    ```

    After the update, your `/etc/fstab` should look like the below

    ```shell
    $ cat /etc/fstab
    LABEL=cloudimg-rootfs	/	 ext4	defaults,discard	0 1
    LABEL=UEFI	/boot/efi	vfat	umask=0077	0 1
    LABEL=DEV_VM_HOME	  /home/<your_username>	ext4	defaults,nofail	0 2
    ```

5. (Optional) Back up your home directory just in case. You can delete it later.

    ```shell
    sudo rsync -aurvP --info=progress2 /home/<your_username>/ /home/<your_username>.bak
    ```

6. Reboot your VM.

    ```shell
    sudo reboot now
    ```

7. Try to SSH to the VM again. The connection should become faster. Then, make sure that your dedicated volume is mounted correctly by running the below command

    ```shell
    $ df -h
    ...
    /dev/nvme1n1     295G  108K  280G   1% /home/<your_username>
    ```

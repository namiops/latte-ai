# Move to virtual machine

This document gives some help and tips on how to easily move from one DEV VM to another.

## References
  - [Bastion Host VMs](../../../terraform/environments/bastion/)

## Copy over HOME (and other directories) over SSH

### (optionally) Cleanup first
Before you start copying everything over, it is generally a good idea to do
some cleanup. Since connection speed is not great sometimes, removing some
temporary files or scrap might speed up things a lot.
Find big files with something like:
```sh
du -hs | sort -hr
```

### Copy data via your Laptop
The following code examples assume that your old VM is called `ec2-dev` and
your new machine is called `ec2-dev2`.  You can use a similar command to
migrate any other folder (e.g.,`/usr/local`) using this command line.

This method has the advantage, that you also have a backup, in case something
goes very wrong shortly after your migration.

1. Copy the HOME from your old EC2 instance to your laptop (or dev-pc)
```sh
ssh ec2-dev "tar -Jcvf - ~/" > ec2-home.backup.tar.xz
```
_Tip To speed things up, clean up anything you do not want to move before hand._

2. Restore your HOME to the new EC2 instance
```sh
cat ec2-home.backup.tar.xz | ssh ec2-dev2 "tar Jxvf - --strip-components=2 -C ~/";
```
_The example is for the case where the user name changed._

### Directly copy from one VM to the other
You can connect both EC2 instances directly and use `rsync`.

For this to work, you need to configure (e.g. just copy the folders from your
laptop / current VM):
* `~/.ssh`
* `~/.aws`

Then just login as usual and copy everything over using `rsync`.

E.g. copying from the new VM
```sh
aws sso login --profile AdministratorAccess-370564492268
rsync -Par --exclude=git --exclude=tmp --exclude=.cache --exclude=cache ec2-dev: ~/
```

## Restore installed software (apt)
Install manually installed apt packages:
* Get a list of packages installed on both machines:
```sh
dpkg -l | awk '        /ii/ {print $2}'
```
_store each in a file to diff later_

* Backup and restore any custom apt source. Copy the following folders over using the ssh / tar method above
   * `/usr/share/keyrings`
   * `/etc/apt/sources.d`
   * `/etc/apt/keyrings`
* (optional) Block packages that you do __not__ want on your new VM
   * I had a case where some 3rd party apt package pulled in tor. To make sure this does not happen again:
```sh
sudo apt-mark hold tor
sudo apt-mark hold tor-geoipdb
sudo apt-mark hold torsocks
```
* Install packages:
```sh
sudo apt install $(diff -uw newvm.apt.txt oldvm.apt.txt | awk -F+ '/^+/ {print $2}')
```

## Restore git repos
Make sure you push all pending branches and changes to remote.
Then, instead of copying your git repository over, just re-clone them on the new machine.

_Get the remote sources on the old machine_
```sh
git remote -v
```

_Then clone the repo on the new machine_
```sh
git clone <copy from above>
```

## Cleanup old VM

Once you confirmed everything is working, please stop and cleanup your old VM by
removing it from
[here](../../../terraform/accounts/370564492268/dev_env/users.auto.tfvars.json).

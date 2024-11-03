## Access files on a EKS node

### References:[ ](https://wovencity.monday.com/boards/3890482591/pulses/3948365414)

* [Link to the operational issue in Monday.com that triggered the creation of this document for reference](https://wovencity.monday.com/boards/3890482591/pulses/3948365414)
* [Link to the issue in Monday.com about writing this document](https://wovencity.monday.com/boards/3891277781/pulses/4129137184)
* [Amazon EKS troubleshooting](https://docs.aws.amazon.com/eks/latest/userguide/troubleshooting.html)

## Quickly open a session

[Woven AWS start page](https://woven.awsapps.com/start/#/) -> AWS Account -> &lt;Account of your choice> -> AdministratorAccess Management console -> Services: Elastic Kubernetes Service -> Clusters &lt;Your cluster’s name> -> Compute -> search / find your node -> node name -> instance

Connect -> session manager -> connect

You can open an interactive session to any EKS node using the Session Manager.

![alt_text](images/SessionManager.png "Connection Manager")

## Copy files from a EKS node using ssh / scp / sftp

### Add your public ssh-key to the node via the Session Manager

```sh
$ mkdir ~/.ssh
$ vi ~/.ssh/authorized_keys
```

Paste in your key (you may use another editor)


### Setup your ssh client configuration

#### Example: OpenSSH (on MacOS)

Put the following into your ~/.ssh/config file


```
Host ip-10-13-93-52.ap-northeast-1.compute.internal
    User ssm-user
    ProxyCommand aws ssm start-session --profile AdministratorAccess-370564492268 --target i-0ef46ef3d786c9363 --document-name AWS-StartSSHSession --parameters portNumber=%p
```


Adjust as needed:

* Host (ip-10-13-93-52.ap-northeast-1.compute.internal): Can be anything. It is probably a good idea to use the instance name or IP address.
* --profile (AdministratorAccess-370564492268): Needs to be the correct AWS profile as defined in your ~/.aws/config file matching the environment you are connecting to.
* --target (i-0ef46ef3d786c9363): **Must** be the target instance name.

Test the connection:

```sh
ssh ip-10-13-93-52.ap-northeast-1.compute.internal
```

You should get a prompt on the target host.


### Copy files

#### Example 1 sftp on macos/Linux

```sh
$ sftp ip-10-13-93-52.ap-northeast-1.compute.internal
sftp> mget -r /var/log/eks-*
```

#### Example 2 lftp on macos/Linux

```sh
$ lftp sftp://ip-10-13-93-52.ap-northeast-1.compute.internal
lftp ip-10-13-93-52.ap-northeast-1.compute.internal:~> cd /var/log
lftp ip-10-13-93-52.ap-northeast-1.compute.internal:/var/log> get dmesg
```

#### Example 3 scp

```ssh
$ scp ip-10-13-93-52.ap-northeast-1.compute.internal:/var/log/dmesg.old ./
```

#### Example 4: copy root only accessible files via ssh
```ssh
$ ssh ip-10-13-93-52.ap-northeast-1.compute.internal "cd /var/log; sudo tar Jcvf - yum.log" > eks_root_yum_log.tar.xz
```
---

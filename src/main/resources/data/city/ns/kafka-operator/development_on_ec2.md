# how to develop on ec2

## setup ec2

- [Provisioning your EC2 dev VM and connecting to it | Docs | CityOS Developer Portal](https://developer.woven-city.toyota/catalog/default/component/setup-ec2-dev-vm-document/docs)

`~/.ssh/config` should be as follows:

Note: please add `LocalForward 10351 127.0.0.1:10350` line to access tilt UI. 

```~/.ssh/config
# ~/.ssh/config
# Change <user> and <instance_name> as per the Terraform entry you made in a previous step.
# Change <SSH_KEY_FILE> to the GH key you made, default /home/.ssh/id_ed25519

Host ec2-dev
  User <user>
  IdentityFile <SSH_KEY_FILE>
  ProxyCommand aws ssm start-session --profile AdministratorAccess-370564492268 --target $(aws ec2 describe-instances --profile AdministratorAccess-370564492268 --filter 'Name=tag:Name,Values=cityos-dev-<instance_name>' --query 'Reservations[-1].Instances[0].InstanceId' --output text) --document-name AWS-StartSSHSession --parameters portNumber=%p
  DynamicForward 9091
  LocalForward 8250 127.0.0.1:8250
  
  # This is for Tilt
  LocalForward 10351 127.0.0.1:10350
  # This is for Kafka-admin(Kafka-UI, AKHQ)
  LocalForward 8081 127.0.0.1:8080

Host *
  ControlMaster auto
  ControlPath ~/.ssh/%r@%h-%p
  ControlPersist 600
  ServerAliveInterval 60
  ForwardAgent yes
```

- login
```shell
aws sso login --profile AdministratorAccess-370564492268
```

- ssh

```shell
ssh ec2-dev
```

## start minikube

the same as the local setting

## export environment variables

the same as the local setting

## setup tilt

- run tilt 
```
# setup background thread
## Here screen is used but please use the tool you prefer such as tmux.
## about screen: https://www.geeksforgeeks.org/screen-command-in-linux-with-examples/
screen -S tilt

cd <HERE>

tilt up
```

Access the tilt UI on　your browser
- http://localhost:10351
Access the Kafka UI on　your browser
- http://localhost:8081

Edit the yamls in `./config/samples/` and check the log of operator!


## (optional) setup your editor

- vscode
  - This plugin enables edit the code on ec2 from your laptop. 
    - [Developing on Remote Machines using SSH and Visual Studio Code](https://code.visualstudio.com/docs/remote/ssh)

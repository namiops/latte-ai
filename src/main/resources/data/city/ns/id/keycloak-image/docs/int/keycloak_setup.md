# id configuration

This directory contains manifests used to install and configure the following resources.

- Keycloak
- Postgres Cluster ([PGO](https://github.com/CrunchyData/postgres-operator))
- Security Token Service
- User Manipulation Service

## General layout

## Tasks

## Requirements

The following resources must be applied.

* istio-system
* city-ingress
* city-egress
* cityos-system/keycloak-operator
* cityos-system/postgres-operator

## How to access to Keycloak hosted on your local machine

### Step1. Establish a tunnel

Execute the following command. After execution, it must be left running, so please execute it in a separate terminal session using nohup, screen, tmux, etc.

```shell
minikube tunnel
```

When started, a virtual IP address accessible from the local machine will be assigned to the Load Balancer of Istio's ingress gateway. Confirm the IP address with the following command.

```shell
kubectl -n city-ingress get svc
NAME             TYPE           CLUSTER-IP       EXTERNAL-IP      PORT(S)                                                                    AGE
ingressgateway   LoadBalancer   10.107.131.250   10.107.131.250   15021:32225/TCP,80:32427/TCP,443:32088/TCP,8081:32590/TCP,7029:31309/TCP   4h
```

`EXTERNAL-IP` is the IP address which we can access from local machine.

### Step2. Configure /etc/hosts

After you deployed Keycloak, Keycloak service will be listening [here](http://id.woven-city.local).
This domain name is not public domain, so you may need to add the entry at `/etc/hosts` in your computer like this.

```/etc/hosts
# /etc/hosts
127.0.0.1 localhost
10.107.131.250 id.woven-city.local observability.woven-city.local
```

The IP address that is pointed by the local domain is `external ip address` (`EXTERNAL-IP`) of `ingressgateway` service.

## How to access to Keycloak hosted on your EC2

### Step1. Establish a tunnel

The same as `Step1` in `How to access to Keycloak hosted on your local machine`, but run the command on your EC2.

### Step2. Configure /etc/hosts

The same as `Step2` in `How to access to Keycloak hosted on your local machine`, but configure it on your EC2.

### Step3. Configure ssh connection to turn on dynamic port-forwarding

You can configure Dynamic Port Forward by setting the following options in the SSH command when connecting. Replace `<ec2-host>` with the hostname you specify in `~/.ssh/config` for your EC2.

```shell
ssh -D 1080 <ec2-host>
```

After establishing the connection, you need to configure your browser to use SOCKS5 proxy; 
if you are using Chrome, you can use the [SwitchyOmega plugin](https://chrome.google.com/webstore/detail/proxy-switchyomega/padekgcemlokbadohgkifijomclgjgif) to easily turn the proxy on and off.
You can import [the sample SwitchyOmega setting](./configs/OmegaOptions_woven_local.bak).

### Step4. Check the connection

Try accessing the following URL:

- https://id.woven-city.local
- https://observability.woven-city.local

Some users registered in local cluster that you can use for testing. You can deep dive the permissions of each user from the configuration file.
   
| Username | Password    | Configuration File                                                                                          |
|----------|-------------|-------------------------------------------------------------------------------------------------------------|
| alice    | alice12345! | [infrastructure/k8s/local/id/keycloakuser-alice.yaml](/infrastructure/k8s/local/id/keycloakuser-alice.yaml) |
| bob      | bob12345!   | [infrastructure/k8s/local/id/keycloakuser-bob.yaml](/infrastructure/k8s/local/id/keycloakuser-bob.yaml)     |

These testing users will automatically be loaded during the deployment: [infrastructure/k8s/local/id/kustomization.yaml](/infrastructure/k8s/local/id/kustomization.yaml).

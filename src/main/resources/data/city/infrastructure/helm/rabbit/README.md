# Rabbit Stateful Set Chart

## === WARNING: DEPRECATED ===
This Chart was made approximately at around November 2021 and is vastly outdated. This is kept for the purposes of allowing someone an example to work off of with RabbitMQ locally 
as a stateful set. The recommended way to use RabbitMQ similar to how it's used in Agora would be the use of the **[Rabbit Cluster Operator](https://github.com/rabbitmq/cluster-operator)**. Please use this chart at your own risk as this is a very old chart that is not being worked on nor maintained by Agora.

### Rabbit
This directory contains charts that help deploy the following:
* The Istio secure gateway that sets up mTLS connectivity to the **cityos-gateway**
* The stateful set that enables Rabbit to run as a quorum
* The headless service that acts as a Load Balancer for the rabbit quorum
* The virtual service that directs traffic from the secure gateway to the headless service

#### How to deploy (locally)
Deployment to a local environment (minikube, for example) is possible via `helm install`
```shell
helm install <deployment_name> . [--dry-run] #dry-run allows for a check of the deployment before pushing
```

The deployment can be modified by changing the various flags provided by the `Values.yaml` chart which is in the repo. 
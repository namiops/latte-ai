# Local Kafka setup

## Overview

This is a Strimzi setup that will resemble the production MSK cluster.

Strimzi requires installation of several CRDs and controllers before deploying a Kafka resource
that will trigger actual deployment of the cluster.

The broker listener certs have been specified in order to provide an easier setup - the broker-listener-* files contain the CA cert, cert, and key, these are valid for 10 years. The Certificate object for cert-manager to issue them is provided in broker-listener-cert-sample.yaml as reference. 

## Installation:

```shell
# Go to the local kafka directory
cd <repo_root>/infrastructure/k8s/environments/local/clusters/worker1-east/kafka

# deploy cert-manager (repeat the command until no error happens. It might take time to get the cert-manager webhook started so please repeat until it succeeds)
kubectl apply -k ../cert-manager

# deploy the kafka cluster (repeat the command until no error happens)
kubectl apply -k .
```

## Connecting

Kafka is then available at the following URLs inside the k8s cluster:

* PLAINTEXT: cityos-kafka-kafka-bootstrap.kafka.svc:9092
* SSL: cityos-kafka-kafka-bootstrap.kafka.svc:9093

## Plaintext

For testing purposes and basic Kafka connections, the plaintext port may be used. 

When connecting to the plaintext port the connection will appear to be from the special Kafka user `ANONYMOUS`. `ANONYMOUS` has been set up as a superuser in this deployment, so all authorization will be bypassed.

You can use kafka-admin (AKHQ) to create topics and publish/consume messages. It is not necessary to create topics & ACLs using `CityOsKafka` resource (kafka-operator CR).

## Authentication and Certificates:

mTLS is supported on this setup. To simplify connection and deployment, a local CA has been set up (see ../cert-manager) and a 10-year certificate issued from that CA has been specified for Kafka's brokers. This allows clients to have an unchanging cert that they may specify in truststores. This certificate is available in `certs/broker-listener-*`

For convenience, a superuser has been set up and a 10-year cert has been issued for it. This certificate is available in `certs/super-user-*`

Clients wishing to connect using mTLS must trust this listener cert and use a certificate issued by the cert-manager setup. This enables testing of ACLs.

## On strimzi-artifacts.yaml

```sh
curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/0.41.0/strimzi-cluster-operator-0.41.0.yaml |\
    sed s/myproject/kafka/g > ./strimzi-artifacts.yaml
```

## On 9xx-ClusterRoleBinding-xxx.yaml

Taken from infrastructure/k8s/common/kafka-strimzi-operator/kafka-strimzi-operator-0.33.1/9xx-ClusterRoleBinding-xxx.yaml and tweaked the namespace.

# IoT Deployment
This directory is home to the deployment in K8s for the IoT Gateway and Broker service(s), currently running on 
[RabbitMQ](https://www.rabbitmq.com/#features).

#### Disclaimer
The keywords "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "NOT RECOMMENDED", 
"MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC8174](https://tools.ietf.org/html/rfc8174) 
when, and only when, they appear in all capitals, as shown here.

## Deployment Details
Deployment to the **development** cluster is handled by Flux. The files located under this directory describe to Flux 
the deployment of:
* A [Rabbit Cluster Operator](https://github.com/rabbitmq/cluster-operator) which is a CRD that knows how to deploy RabbitMQ clusters
  * The operator and its details can be found [here](../cityos-system/rabbit-cluster-operator-crd.yaml) per the organization of deployment for the **dev** cluster
* A [Cluster Deployment](./rabbit-cluster.yaml) with pre-configured settings per the decisions made by the Agora Team for the entire City.
  * The Agora Team has based configuration slightly on the recommendations of the Rabbit team, which can be found in further detail [here](https://github.com/rabbitmq/cluster-operator/tree/main/docs/examples/production-ready)
* A [Virtual Service](https://istio.io/latest/docs/reference/config/networking/virtual-service/) to direct traffic verified by the Secure Ingress to the Broker


## Inbound Traffic (Secure Gateway)

![img](/ns/iot/img/IoTGateway.jpg)

The IOT Broker is secured via mTLS and all passing traffic MUST comply by providing x509 certificates.

Currently, traffic is secured at the ingress with a certificate provided to Istio. The gateway has been configured to
allow traffic that provides a certificate that has been signed by the Agora Private CA. This allows Agora to track teams
that would like to use the gateway in a way that can be managed more cleanly. To request a certificate that can talk to 
the gateway, please contact the Agora team, primarily [Joey](mailto:joseph.orme@woven-planet.global)

## Internal Traffic (Inside the Cluster)
The StatefulSet enforces known hosts for the user to use. The well known names should be in the template form of:

#### Reaching Rabbit
```shell
$(MY_POD_NAME).$(K8S_SERVICE_NAME).$(MY_POD_NAMESPACE).svc.cluster.local
```
Where:
* **MY_POD_NAME** is derived from the `metadata.name` field of the pod
* **MY_POD_NAMESPACE** is derived from the `metadata.namespace` field of the pod, enforced by Agora
* **K8S_SERVICE_NAME** is the name of the given service by the operator that handles the pods default name

For example a default would be:
```shell
rabbitmq-server-0.rabbitmq-nodes.iot.svc.cluster.local
rabbitmq-server-1.rabbitmq-nodes.iot.svc.cluster.local
rabbitmq-server-2.rabbitmq-nodes.iot.svc.cluster.local
```

In addition, the cluster deployment comes with a **headless service** that helps the cluster to remediate K8s IP 
addresses with the stable DNS names. The headless service acts as a load balancer for the pods deployed and can be
reached at

```
rabbitmq-nodes.iot.svc.cluster.local
```

#### Logging into Rabbit
Currently, the Rabbit deployment for the purposes of the POC is set up with credentials, auto-generated by the 
deployment at deployment time. These credentials for the POC phase can be provided by Agora Team. **These credentials 
will be removed and SHOULD be considered temporary**

The Agora Team intends to do and implement one of the following (or both): **TODO: Investigate these steps and update 
this README**
* Use of a [Vault](https://www.vaultproject.io/) to provide interested services an ephemeral user that can transparently log in to the Rabbit
* Use of Istio and passing requests via the Envoy, allowing Istio to validate traffic without having the calling service be aware of it

## Roadmap
- [x] Users/Secrets/ACLs
    - [x] Figure out a way to bootstrap secrets that comply with Agora Team's protocol and standards
    - [x] Provide mechanisms for user accounts and ACLS
- [ ] Monitoring/Logging/Performance
    - [x] Provide monitoring and metrics for the cluster
    - [ ] Determine, or fine tune, the cluster configuration to allow it to be a 'best fit' for Agora
- [x] Configuration
    - [x] Provide default exchanges to allow for some default AMQP topics
- [ ] Features/Future work
  - [ ] Operators and Self-Service
     - [x] Investigate use of Operator in CI/CD Pipeline
     - [ ] Determine leverage of [RabbitMQ Cluster](https://github.com/rabbitmq/cluster-operator) and [RabbitMQ Topology](https://github.com/rabbitmq/messaging-topology-operator) Operators

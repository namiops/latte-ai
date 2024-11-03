# Namespace IoT

![logo](https://www.rabbitmq.com/img/rabbitmq_logo_strap.png)

This repository is home to the Agora Team's IoT Broker, currently running on [RabbitMQ](https://www.rabbitmq.com/#features). The IoT broker is meant to allow for traffic into
Agora that is:
* Low Priority,
* Not Time-Sensitive, and
* Of interest to multiple parties in a Many-to-Many relationship (Pub/Sub).


#### Disclaimer
The keywords "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "NOT RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be
interpreted as described in [RFC8174](https://tools.ietf.org/html/rfc8174) when, and only when, they appear in all capitals, as shown here.


## High Level Architecture (Ongoing)
![arch](img/High-Level-Arch.png)

### High-Level Concepts
* **Messages are "smart", the broker is "dumb"**
    * The broker is meant to be a giant pipe for services to hook into messages they care about. Services and the messages they send are in the more relevant and better position to
      determine how to handle messages. The broker's responsibility it to ensure that messages are received, filtered, and delivered per the correct usage of Service-determined queues.
* **Message Consistency over Availability**
    * The priority for the IoT broker currently is message consistency over service availability. The broker only considers a message 'delivered' if and only if the broker has 
      received an acknowledgement from the client receiving the message. Any messages that are not acknowledged are stored via persistence that is provided to the broker by k8s 
      persistent volumes. For more details refer to the Technical Note [TN-0040](https://docs.google.com/document/d/1lTgnfAG0QS6sK_6faBacGgDoDxp5YgaptzlYDslset0/edit).
* **"No entry without an ID"**
    * The gateway leverages Istio and a [Secure Ingress](https://istio.io/latest/docs/tasks/traffic-management/ingress/secure-ingress/) to enforce Mutual TLS. All traffic that comes into
      Agora **must** be authenticated by the gateway.
* **Security**
    * While not currently enabled, users will authenticate to the broker via given user accounts, tied to the specified service and granted permissions per ACLs. While
      services are allowed to hook into multiple exchanges and work with multiple queues, this does not necessarily mean they should be given access to other services' queues.
* **API is the tool, the broker is the "black box"**
    * The broker can be replaced with another broker tool should non-functional requirements change. The API should allow the services to use the same
      mechanics for creating topics, queues, exchanges, or whatever logical structures of the broker with minimal friction.


## Technical Notes on the Broker
For details on the investigation of the broker you can refer to the Agora Technical Note [TN-0040](https://docs.google.com/document/d/1lTgnfAG0QS6sK_6faBacGgDoDxp5YgaptzlYDslset0/edit).

#### Deployment (Live)
Deployment to the `dev` cluster is handled by Flux, and the files are located under `infrastructure/k8s/dev/iot`. For more
details as to the live deployment, please refer to the [README there](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/iot/README.md).

#### How to talk to the Broker (Clients)
One MQTT client we recommend is [Eclipse Paho](https://www.eclipse.org/paho/), which allows you to set up connection credentials as well as a session to talk with the broker. Paho provides client libraries for Go ([paho.mqtt.golang](https://github.com/eclipse/paho.mqtt.golang)), Python, Java, and other common languages.

The [Topic Manager module](https://github.com/wp-wcm/city/tree/main/ns/iot/topic-manager) also provides some simple examples of using the Go client, for further details refer to its README and code.


## Modules

### [Subscriber](https://github.com/wp-wcm/city/tree/main/ns/iot/subscriber)
This module contains a small, simplistic demo listener that listens on a MQTT channel. The subscriber is connected to the broker via injected values that let it talk to the RabbitMQ broker.  
For more details refer to [its README](https://github.com/wp-wcm/city/tree/main/ns/iot/subscriber/README.md).

### Topology Operator
_Located in [/infrastructure/k8s/common/rabbitmq-system/messaging-topology-operator-1.7.1](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/rabbitmq-system/messaging-topology-operator-1.7.1)._  
The topology operator is used to deploy resources to the cluster. The operator file used is slighlty modified from the [official release](https://github.com/rabbitmq/messaging-topology-operator/releases/tag/v1.7.1) because our RabbitMQ broker is deployed to the `iot` namespace.
More info and [examples](https://github.com/rabbitmq/messaging-topology-operator/blob/main/docs/examples) on how to add resources in the [official doc](https://github.com/rabbitmq/messaging-topology-operator/#quickstart).

### [Topic Manager](https://github.com/wp-wcm/city/tree/main/ns/iot/topic-manager)
**==Under Construction==**

## Roadmap and TODOs
- [x] Users/Secrets/ACLs
    - [x] Figure out a way to bootstrap secrets that complies with Agora Team's protocol and standards
    - [x] Provide mechanisms for user accounts and ACLS
- [ ] Monitoring/Logging/Performance
    - [x] Provide monitoring and metrics for the cluster
    - [ ] Determine, or fine tune, the cluster configuration to allow it to be a 'best fit' for Agora
- [x] Configuration
    - [x] Provide default exchanges to allow for some default AMQP topics
- [ ] Features/Future work
    - [ ] Add documentation on API for the Topic Manager and additional documents and diagrams
    - [x] Operators and Self-Service
        - [x] Investigate use of Operator in CI/CD Pipeline
        - [x] Determine leverage of [RabbitMQ Cluster](https://github.com/rabbitmq/cluster-operator) and [RabbitMQ Topology](https://github.com/rabbitmq/messaging-topology-operator) Operators

## Checking for component changes

In order to check the changes made to an IoT component, please execute the following script under this path:

```bash
./changes-check.sh <SUBFOLDER> <FROM_COMMIT> <TO_COMMIT>
```

Usually, you'll be able to get commit hashes from image tags in an automated PRs like this one:
https://github.com/wp-wcm/city/pull/8326/files

Extract a middle part from a tag, a hash from `red` line goes to `<FROM_COMMIT>`, and a hash from `green` - to `<TO_COMMIT>`.

Example:
```bash
./changes-check.sh iota 2b3040bf 7566d113
```

Use this script to determine a diff for a component and decide if it is safe to push the changes to dev. 
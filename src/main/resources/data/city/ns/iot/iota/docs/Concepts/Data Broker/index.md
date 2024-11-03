# Intro to RabbitMQ
[RabbitMQ](https://rabbitmq.com/) is the data broker of choice for IoTA. It supports multiple protocols like MQTT, AMQP and STOMP and virtual data separation through vhosts, used to achieve multitenancy. The official [documentation](https://rabbitmq.com/documentation.html) page offers tutorials to get started.

## Topology Operator
We add static resources using the [operator pattern](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/). This choice is most suitable for permanent resources, so in general vhosts, durable queues and exchanges and users. For IoT devices we want to maintain a degree of flexibility that allows us to scale quickly and operate timely in case of emergency (eg. block a device that has been hacked).

See in the related [section](00_rabbitmq-crd.md) how to add a Vhost for your tenant, define durable queues, policies and permissions.

Furthermore, [TN-0285](https://docs.google.com/document/d/1XHhWmUTbrh34Smyw2hHoveu3m2LHl0-Q_VoJAuNXF0Y/edit#heading=h.5qm13wuvtiz9) provides more details about the internal configuration and use.

## Testing tools


- [MQTT Explorer](http://mqtt-explorer.com/): has a UI and easily allows to save configurations and use certificates to test devices e2e.
- [Mosquitto](https://mosquitto.org/): more powerful and flexible, less user friendly.
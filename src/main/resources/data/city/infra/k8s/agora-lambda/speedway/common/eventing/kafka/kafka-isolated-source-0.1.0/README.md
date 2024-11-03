## This is a PoC of an isolated dataplane for Knative Kafka Source

### Background

This deployment reuses a dataplane (dispatcher) of [eventing-kafka-broker](https://github.com/knative-extensions/eventing-kafka-broker) just like regular [kafka-source](https://knative.dev/docs/eventing/sources/kafka-source/) does.

The problem with the out-of-the-box `kafka-source` is the fact that it deploys a common dispatcher for all tenants in `knative-eventing` namespace.

Deep-dive and concerns on this matter can be found in [this document](https://docs.google.com/presentation/d/1OWlZyGeeKUaRR-u6eUk-W80ymGmWI8BjoWpVOrv8QIE/edit#slide=id.g283fef6adfe_0_4).

This solution isolates a dataplane making it possible to deploy a "customized" kafka dispatcher per tenant namespace which also mitigates our current security concerns.

### How?

After some reverse engineering it became clear that knative kafka broker, source and sink all use a same dataplane code, called `dispatcher`.
This is a Java container that reads a configuration from mounted configmaps (volumes) and behaves as either sink, source or both depending on values provided.
These config maps were evicted from a vanilla solution into a separate "namespaced" one, this allowed us to modify them without conflicting with a respective knative controller and set dispatcher as we see fit.

### [DEPRECATED] Modifying a destination

A configuration related to ingresses and egresses is located in a `configmap-kafka-source-dispatcher.yaml` CM, which is a base64 representation of `config.json`.

Use following commands to apply a modified a configuration:

```bash
KAFKA_CONFIG=$(cat config.json | base64 -w 0) &&
echo 'apiVersion: v1
kind: ConfigMap
metadata:
  name: kafka-source-dispatcher
  namespace: lambda
binaryData:
  data: '$KAFKA_CONFIG | kubectl apply -f -
```

### [NEW] Modifying a destination

Currently, the configuration is supplied to the Java code by the init container, which in turn takes it from ENVs, use `statefulset-kafka-source-dispatcher.yaml` as a reference.

This allows to propagate a `K_SINK` value [injected](https://github.com/knative/eventing/blob/f321ba576d89f07aa72d4341a11eee23f7c1d105/pkg/apis/sources/v1/sinkbinding_lifecycle.go#L143-L145) 
by a [SinkBinding](https://knative.dev/docs/eventing/custom-event-source/sinkbinding/) (`sink-binding-kafka.yaml`) to main container via a custom json config file (`kafka-source-init-config.json`).

### Additional settings

To make things work in lab2 following modifications has been made to a configuration:

configmap-kafka-config-source-data-plane.yaml
```
config-kafka-source-consumer.properties: |
  ...
  partition.assignment.strategy=org.apache.kafka.clients.consumer.RangeAssignor
```

Additional StatefulSet ENVs (for IPv6):
```
- name: JDK_JAVA_OPTIONS
  value: -Djava.net.preferIPv6Addresses=true
- name: KUBERNETES_DISABLE_HOSTNAME_VERIFICATION
  value: "true"
```

### Sending events to Kafka and verifying results.

There is an `kafka-admin-cli` toolbox deployment in a `lambda` namespace.

1. Connect to it via `-n lambda exec -it deploy/kafka-admin-cli -- bash`
2. Execute `kafka-console-producer --topic lambda.knative-source-topic --bootstrap-server kafka.default:9094`
   - `lambda.knative-sink-topic` can also be used, the as the source listens to the both topics.
3. Send some data, for example: `{"message":"Hello from Source topic"}`
4. You'll see events at https://sockeye-lambda-lambda.agora-lab.w3n.io/ (lab2 only for now).

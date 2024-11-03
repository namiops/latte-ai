# Example - cross-namespace traffic

One of the key benefits of Kafka is the ability for different services to communicate with each other.

Since the control over topic data is held by the namespace that created the topic, some coordination may be required if the topic is not publicly-readable.

In this example, two services `service-a` and `service-b` want to share data. `service-a` has full access to the topic, `service-b` has read-only. All consumer groups from `service-b` are allowed. `service-a` does not actually consume from the topic, to enable this it would require a `group` ACL similar to the one in the `service-b` namespace.

## Topics

The following is deployed by `service-a` and sets up the topic, read-write access for `service-a`, and read access for `service-b`.

This will result in the creation of `service-a.topic-1`, which must be fully specified in the application code.

```yaml
---
apiVersion: kafkagroup.woven-city.global/v1alpha3
kind: CityOsKafka
metadata:
  name: service-a
  namespace: service-a
spec:
  topics:
  - name: topic-1
    partitions: 1
    replicationFactor: 2
  acls:
  - resource:
      resourceName: topic-1
      resourcePattern: literal
      resourceType: topic
    policies:
    - principal:
      - User:CN=service-a.${cluster_domain},OU=CityOS
      operation:
      - read
      - write
      - describe
      permission: allow
  - resource:
      resourceName: topic-1
      resourcePattern: literal
      resourceType: topic
    policies:
    - principal:
      - User:CN=service-b.${cluster_domain},OU=CityOS
      operation:
      - read
      permission: allow
```

The following is deployed by `service-b` and sets up the consumer groups.

This will result in all services presenting a `service-b` certificate to specify any consumer groups with the format `service-b.*`

```yaml
---
apiVersion: kafkagroup.woven-city.global/v1alpha3
kind: CityOsKafka
metadata:
  name: service-b
  namespace: service-b
spec:
  acls:
  - resource:
      resourceName: ""
      resourcePattern: prefixed
      resourceType: group
    policies:
    - principal:
      - User:CN=service-b.${cluster_domain},OU=CityOS
      operation:
      - read
      - describe
      permission: allow
```

# Setting up Kafka for your application

You, the developer, have control over your own topics and who has access to them. 

This is done by the creation of a Kubernetes Custom Resource in your namespace. These changes will then automatically be applied. 

A full example is available at [Quickstart](01_quickstart)

For details on the concepts, please see [Key concepts](04_keyconcepts)

!!!Note
    If you are using [the city monorepo](https://github.com/wp-wcm/city), consider using [zebra](https://developer.woven-city.toyota/docs/default/Component/zebra/) so that you can configure in a simpler manner and generate the following resources. 
    To see an example, plus further documentation, on what configurations options are available, please see [the sample YAML](https://github.com/wp-wcm/city/blob/main/ns/kafka-docs/configtemplate/commented-values-schema/commented-values-schema.yaml).

## Topics

```yaml
spec:
  topics:
    - name: topic-1
      partitions: 5
      replicationFactor: 2
      ## optional
      cleanupPolicy: compact,delete
      maxMessageSize: 1MB
      retentionPeriod: 3d
```

`name`: To avoid collisions and provide security, the namespace that the CR is deployed in will be prepended, separated by a dot (.). If the above example were deployed in `test-ns`, the resulting topic would be `test-ns.topic-1`

`partitions`: The number of partitions for the topic

`replicationFactor`: The number of copies of data. For now, `replicationFactor` should be 2, unless you are very certain of what you are doing.

The following optional topic configs are supported. 

`cleanupPolicy`: Designates the retention policy to use on old log segments. The default setting, `delete`, will discard old segments when their retention time or size limit has been reached. The `compact` setting will enable log compaction on the topic (only the most recent record for a given key is retained). Both may also be specified with `compact,delete`
  
`maxMessageSize`: The largest message that can be written to the topic. Defaults to 1MB. Valid units are `KB`, `MB`. Note that sending large messages to Kafka is extremely inefficient - please consult the Agora team for assistance with system design if large messages are required.
  
`retentionPeriod`: The maximum time a message will be retained before deletion if `cleanupPolicy` is set to `delete` (or unspecified). If set to -1, no time limit is applied. Valid time units are `ns`, `us`, `ms`, `s`, `m`, `h`, `d`, `w`. Defaults to 1 week. 

## ACLs

```yaml
acls: 
  - resource:
      resourceName: orders
      resourcePattern: literal
      resourceType: topic
    policies:
    - principal:
      - User:CN=kafka-quickstart.${cluster_domain},OU=CityOS
      operation:
      - read
      - write
      - describe
      permission: allow
```

`resource`: Corresponds a Kafka resource. 

`resourceType` is one of `topic` or `group`

`resourcePattern` is one of `literal` (the exact name of the resource to apply the policy to) or `prefixed` which will use the provided string as a prefixed pattern match. For example, `resourceType: prefixed` with `resourceName: test-` will match `test-1`, `test-2`...

`policies`: Policies to apply to that resource.

`principal`: The principal to apply this policy to. Corresponds to the full subject on the x.509 certificate presented by the connecting service.

`operation`: is one of `read`, `write`, or `describe`. Multiple operations can be specified in a single policy declaration, as above.

`permission`: is one of `allow` or `deny`. Note that `deny` is the default for all resources, and `deny` ACLs generally do not need to be specified.

# Key concepts

## Cluster

Kafka operates on multiple computers, generally 3 or more, for data I/O. These machines know of each others' existence and have automated failover, load balancing, and replication. This collection of machines is known as a cluster.

## Broker

A broker is a computer operating in a Kafka cluster. Services wishing to read or write data connect directly to a broker's network address. The brokers are able to discover each other and load balance requests accordingly.

## Topic

Topics are the basic logical segmentation of data inside Kafka. Examples of topics may be a sensor stream from an IoT device, alerts from a monitoring system, or responses to data processing requests. Access to topics may be restricted to specific principals, or they can be publicly available for reading, writing, or both. 

## Partition

Each topic log is broken up into partitions. This allows for scale and parallelism - the entire contents of a topic do not have to live on the same broker node in the cluster. Messages with keys are assigned to a partition using the hash of their keys, messages without keys are assigned in a rotating order. As stated above, partitions are allocated to members of a given consumer group. Also worth noting - messages within the same partition have a guaranteed order. If messages are in different partition, no such guarantee applies.

## Replication factor

Replication factor is the number of copies of topic data across the brokers in the cluster. 

## Producer

A producer is a service that writes data to a Kafka topic.

## Consumer

A consumer is a service that reads data from a Kafka topic.

## Consumer group

All consumers are part of a consumer group. Partitions are allocated among all members of the group so that each member receives data from an equal number of partitions, automatically rebalanced as consumers join or leave the group. 

Consumer Groups are **mandatory** and must be declared explicitly as there is
no "default" consumer group provided

If the application design requires all consumers to see each message, the consumers should be in separate consumer groups. If the design can benefit from parallel processing, with each consumer operating on their share of messages, those consumers should be in the same consumer group.

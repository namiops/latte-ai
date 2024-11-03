# Partitions, parallelism, and ordering

Data in topics is divided into partitions, which have important implications for application design. 

## When ordering is required

As discussed in the Key Concepts section, if ordering is required, ordering is only guaranteed within a given partition, which limits parallelism. Each consumer in a consumer group will only receive messages from one partition.

```
  Topic partitions: 1
  Consumer group 1: Consumer1
```

If multiple consumers are added to the group:

```
  Topic partitions: 1
  Consumer group 1: Consumer1, Consumer2
```

then only Consumer1 will receive the messages, Consumer2 will remain idle.


If your application design can tolerate the same message being received multiple times, multiple consumer groups can be used:

```
  Topic partitions: 1
  Consumer group 1: Consumer1
  Consumer group 2: Consumer2
```

Consumer1 and Consumer2 will both receive copies of all messages, in write-order.


Although more complex, if your application design enables you to split data by keys, you can utilize multiple partitions and multiple consumers with ordering guaranteed *only for that partition and its corresponding key*.

```
  Topic partitions: 2 (data shard A and B)
  Consumer group 1: Consumer1 (responsible for data shard A), Consumer2 (responsible for data shard B)
```

Data shard A and B will be consumed in write order, but there is no guarantee on ordering between the two consumers. 


## Parallelism when ordering is not required

If ordering is not a requirement, read operations can be done in parallel for faster throughput. Note that this only refers to ordering *when read by the consumer* - applications can be designed so that ordering is handled later, such as by including a timestamp or ordering key in the message payload for batch processing at a later time. 

```
  Topic partitions: 2
  Consumer group 1: Consumer1, Consumer2
```

Write operations will split the data into two partitions, Consumer1 and Consumer2 will each read from one partition and receive all messages from that partition (effectively each receiving half of all messages and doubling the throughput of the entire system). Kafka guarantees that a message will be received by only one consumer for a given consumer group.

How the data is split is specified by the producer. By default, if no partitioning scheme is specified, a round-robin strategy is used. 

# Data integrity in Kafka

There are a few similar, yet distinct, configuration settings that control durability in Kafka. As with most complex systems, there are tradeoffs available depending on individual application needs and design - here, durability is traded for throughput and lower latency.

*Replication Factor* is a topic-level configuration that specifies the number of copies of a given topic's data. This must not be less than the *min.insync.replicas*, specified below. Agora currently has a 3-broker cluster, so this number must be <= 3. 

*min.insync.replicas* is a system-level configuration that specifies the *minimum* number of copies of data that must exist before further data writes are allowed. In Agora, this is set to 2 and should be considered a fixed number. If you have different needs for your system, please consult with the Agora team.

*acks* is a producer-level configuration that specifies the number of writes that must be acknowledged for the write operation to be considered successful. When set to `all` or `-1`, this is considered to be the same as *min.insync.replicas*

## Optimization for durability

```
  Replication Factor: 3
  acks=all
```

This configuration provides the strongest data durability guarantees Agora can offer. Data will be written across all 3 brokers, with a minimum of 2 writes (specified by min.insync.replicas) needed for the application code to return success for the write operation. The producers must wait for both writes to complete before continuing to write to Kafka.

Note that setting *acks* to 3 is not recommended - if one broker in the cluster is down (due to a crash, reboot, system upgrade, and so on) then all further write operations will be blocked.

## Optimization for throughput

```
  Replication Factor: 3
  acks=1
```

This configuration provides some data durability by replication across brokers, but at write time only one commit is guaranteed. This increases throughput as the producers only need to wait for one confirmation, further replication will happen in the background. However, if the writing broker goes down before the data is replicated to the other brokers, your data may be lost. 

## Fire-and-forget

```
  Replication Factor: 3
  acks=0
```

For maximum throughput, producers may opt to not wait for any write confirmations at all. This has a strong possibility of data loss (network problems, broker issues), and should only be specified where throughput is important above all else and data loss is tolerable.

# Kafka CLI for DBX

## Log in to the Pod

Here is the sample command to log in the dbx-poc Kafka Pod on Speedway Dev:

```shell
# check the pod name
❯ kubectl get pod  --context agora-dbx-admin-dev-gc-0-apps-ap-northeast-1
NAME                                READY   STATUS    RESTARTS   AGE
dbx-poc-kafka-cli-bb5999f6b-gdflb   2/2     Running   0          7d23h

# run exec
❯ kubectl exec --stdin --tty dbx-poc-kafka-cli-bb5999f6b-gdflb --as agora-dbx-admin-dev-admin --context agora-dbx-admin-dev-gc-0-apps-ap-northeast-1 -- /bin/bash
dbx-poc-kafka-cli-bb5999f6b-gdflb:/$
```

## Commands to confirm the connectivity with DBX PoC MSK

```sh
# check connectivity
nc -vz $KAFKA_BOOTSTRAP_HOST 9098

# Listing topics
kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --command-config $KAFKA_CLI_CONFIG --list

# Consuming messages
kafka-console-consumer --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --consumer.config $KAFKA_CLI_CONFIG --topic agora_test_topic

# Producing messages
kafka-console-producer --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --producer.config $KAFKA_CLI_CONFIG --topic agora_test_topic
```

## Commands to confirm the connectivity with DBX Dev MSK

```sh
# Check connectivity
nc -vz $KAFKA_BOOTSTRAP_HOST 9098

# Listing topics
kafka-topics.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --command-config $KAFKA_CLI_CONFIG --list

# Producing messages
kafka-console-producer.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --producer.config $KAFKA_CLI_CONFIG --topic admin-dev.read_write_delete_topic

# Check consumer position
kafka-consumer-groups.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --command-config $KAFKA_CLI_CONFIG --describe --group smc-gc_agora-dbx-admin-dev_dbx-kafka-cli-sa

# Reset offset of the consumer group 
kafka-consumer-groups.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS \
  --group smc-gc_agora-dbx-admin-dev_dbx-kafka-cli-sa \
  --topic admin-dev.read_write_delete_topic \
  --reset-offsets \
  --to-earliest \
  --command-config $KAFKA_CLI_CONFIG \
  --execute
  
# Consuming messages
kafka-console-consumer.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --consumer.config $KAFKA_CLI_CONFIG --topic admin-dev.read_write_delete_topic --group smc-gc_agora-dbx-admin-dev_dbx-kafka-cli-sa --from-beginning

# Delete topic
kafka-topics.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --command-config $KAFKA_CLI_CONFIG  --delete --topic admin-dev.read_write_delete_topic

```

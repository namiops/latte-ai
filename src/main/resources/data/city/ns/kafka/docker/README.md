# Kafka CLI Docker

The image provides use not
only [the basic kafka cli tools](https://docs.confluent.io/kafka/operations-tools/kafka-tools.html) but
also [the additional network tools](https://github.com/jonlabelle/docker-network-tools) including [kcat](https://docs.confluent.io/platform/current/tools/kafkacat-usage.html)

## Build image

- TODO: [Sprint [Orc] - Bazelify kafka-cli Dockerfile](https://wovencity.monday.com/boards/3813113014/pulses/6925036806)

```shell
docker build . -t docker.artifactory-ha.tri-ad.tech/wcm-cityos/confluentinc/cp-kafka:7.6.1-with-network-iam-tools
docker push docker.artifactory-ha.tri-ad.tech/wcm-cityos/confluentinc/cp-kafka:7.6.1-with-network-iam-tools
```

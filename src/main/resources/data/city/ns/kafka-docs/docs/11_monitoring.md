# Monitoring

Two monitoring tools are available:

- [AKHQ](https://akhq.io/)
- [Grafana](https://grafana.com/)

## AKHQ

The main functions our AKHQ provides are:

- see topics and their configurations in your namespace
- live tail messages of your topics
- check ACLs
- update consumer group offsets ([You need to stop the consumer group in order to update group offsets.](https://github.com/tchiotludo/akhq/discussions/910#discussioncomment-1651084) Otherwise, you will face `Internal Server Error: Commit cannot be completed since the group has already rebalanced and assigned the partitions to another member...`)

The URLs are:

- Dev1: https://kafka-monitor.cityos-dev.woven-planet.tech/
- Dev2: https://kafka-monitor.agora-dev.w3n.io/
- Speedway Dev: https://dev-kafka-monitor.woven-city.toyota/
- Speedway Prod: https://kafka-monitor.woven-city.toyota/

To enable kafka-monitor to display only Kafka topics belonging to the user's namespace, it needs to retrieve information about the namespace the user belongs to. 

In Dev1, `KeycloakGroup` is used for that purpose so please make contact with `@agora-data-orchestration` in [#wcm-org-agora-ama](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7) slack channel.
In Dev2/Speedway, `DrakoGroup` is used. This is managed by the yaml files so please create a PR that creates/edits the file in

- Dev2: https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/kafka-monitor/drako-groups/dev2-worker1-east 
- Speedway Dev: https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-kafka-monitor/speedway/dev/drako-groups
- Speedway Prod: https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-kafka-monitor/speedway/prod/drako-groups

and ask `@agora-data-orchestration` for the review.

The file should be named like `drako-group_<YOUR_NAMESPACE>.yaml` and the sample yaml is as follows:

```yaml
apiVersion: woven-city.global/v1alpha2
kind: DrakoGroup
metadata:
  name: <YOUR_NAMESPACE>
spec:
  userList:
  # Specifying the Keycloak(Woven) IDs
  - userId: 7c40083b-d4c4-4863-904a-d02650ba6f77 # justin.levinson@woven-planet.global
  - userId: ac1eb134-5051-4617-8dfc-ac6ee168a6b7 # koh.satoh@woven-planet.global
  - userId: 6803fa22-21d4-4516-a27f-2f4013a9524e # kohei.watanabe@woven-planet.global
  - userId: 99908f6e-0413-4888-8e6b-569087319b1f # yujiro.yahata@woven-planet.global
```

Your userId can be confirmed after logging into the kafka-monitor UI. Alternatively, you can check the value of 'x-user-id' by accessing the endpoint provided by the id team, such as https://id-test-drako-v1.agora-dev.w3n.io/ in the dev2 environment.

![kafka-monitor-user-id.jpg](kafka-monitor-user-id.jpg)

After your permission is updated, you will need to logout once and re-login so your new permission is reflected.

## Grafana

You can check the health of our Kafka, throughput, latency in our grafana dashboard.

- Dev1: https://observability.cityos-dev.woven-planet.tech/grafana/d/3d20c501d5658e9be6ff7d15915b4c49b524373b/kafkaoverview?orgId=1
- Dev2: https://athena.agora-dev.w3n.io/grafana/d/3d20c501d5658e9be6ff7d15915b4c49b524373b/kafkaoverview?orgId=1

# (JFYI) Alerting

The agora orchestration team cares about the alerting, so you don't have to check that.

The alerting list is in http://go/agora-orchestration-alerts and the alerts will be notified
in [the slack channel for orchestration alerts](https://woven-by-toyota.slack.com/archives/C04RE5GP8JC).

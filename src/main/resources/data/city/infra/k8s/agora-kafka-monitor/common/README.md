# agora-kafka-monitor in common

kafka-monitor([AKHQ](https://akhq.io/) ) is the monitoring tool for service team.

> [!NOTE]
>  it behaves slightly unstably when a user attempts OIDC login after Pod restart. (to be addressed in https://wovencity.monday.com/boards/3813113014/pulses/4930238604)


### (Temporarily) Copy KeyCloak Secret from Dev2

Until the ticket ([Sprint [Orc] - [Kafka-monitor] Change Keycloak Client from Dev2 to Speedway Dev/Prod](https://wovencity.monday.com/boards/3813113014/views/90945203/pulses/7066170265)) is completed, copying the Secret from Dev2 by hand is necessary. Here are the commands to copy them:

- Speedway Dev

```shell
kubectl get secret keycloak-client-secret-kafka-monitor-akhq-external-speedway-dev -n kafka-monitor --context dev2-worker1-east -o yaml \
| sed -e 's/name: .*/name: keycloak-client-secret-kafka-monitor-akhq-external-copied-from-dev2/' \
-e 's/namespace: .*/namespace: agora-kafka-monitor-dev/' \
| yq e 'del(.metadata.ownerReferences,.metadata.labels,.metadata.creationTimestamp,.metadata.resourceVersion,.metadata.uid)' - \
| kubectl apply --context=vcluster_dev_agora-control-plane-dev_agora-control-plane-dev-gc-0-apps-ap-northeast-1-pinniped -f -
```

- Speedway Prod

```shell
kubectl get secret keycloak-client-secret-kafka-monitor-akhq-external-speedway-prod -n kafka-monitor --context dev2-worker1-east -o yaml \
| sed -e 's/name: .*/name: keycloak-client-secret-kafka-monitor-akhq-external-copied-from-dev2/' \
-e 's/namespace: .*/namespace: agora-kafka-monitor-prod/' \
| yq e 'del(.metadata.ownerReferences,.metadata.labels,.metadata.creationTimestamp,.metadata.resourceVersion,.metadata.uid)' - \
| kubectl apply --context=vcluster_prod_agora-control-plane-prod_agora-control-plane-prod-gc-0-apps-prod-ap-northeast-1-pinniped -f -
```

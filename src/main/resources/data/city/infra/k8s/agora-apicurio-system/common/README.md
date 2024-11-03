# agora-apicurio-system common

## Setup

### Add the ACL for Apicurio-registry consumer group 

Before deploying the Apicurio-registry, it's necessary to add the acls for the consumer groups using `kafka-admin` CLI.

Otherwise, you'll see the error `Exception in thread "KSQL Kafka Consumer Thread" org.apache.kafka.common.errors.GroupAuthorizationException: Not authorized to access group: apicurio-registry-23b0f4c2-692d-4e7a-9b1a-95151a9ab150`
 
Here is the sample command.

- Speedway Prod

```shell
# Change the context
❯ kubectx agora-kafka-admin-prod-gc-0-apps-prod-ap-northeast-1

Switched to context "agora-kafka-admin-prod-gc-0-apps-prod-ap-northeast-1".

# Check the Pod name
❯ k get pod

Warning: Use tokens from the TokenRequest API or manually created secret-based tokens instead of auto-generated secret-based tokens.
NAME                                       READY   STATUS    RESTARTS   AGE
kafka-admin-679df845cf-c9tgj               2/2     Running   0          31h
kafka-cli-77bbc446d8-jphzq                 2/2     Running   0          2d23h
kafka-cli-without-istio-599675ccb7-7hchz   1/1     Running   0          2d23h

# Log in to the Pod
❯ kubectl exec --stdin --tty kafka-cli-77bbc446d8-jphzq --as agora-kafka-admin-prod-admin -- /bin/bash

Warning: Use tokens from the TokenRequest API or manually created secret-based tokens instead of auto-generated secret-based tokens.

# Add the ACL
kafka-cli-77bbc446d8-jphzq:/$ kafka-acls.sh --bootstrap-server "b-1.agoraprodkafkaclu.590bdx.c2.kafka.ap-northeast-1.amazonaws.com:9094,b-2.agoraprodkafkaclu.590bdx.c2.kafka.ap-northeast-1.amazonaws.com:9094,b-3.agoraprodkafkaclu.590bdx.c2.kafka.ap-northeast-1.amazonaws.com:9094" --add \
--allow-principal User:CN=agora-apicurio-system-prod.woven-city.toyota,OU=CityOS \
--allow-host "*" \
--operation read \
--group "apicurio-registry-" \
--resource-pattern-type prefixed
Picked up _JAVA_OPTIONS: -Xms1g -Xmx1g
Adding ACLs for resource `ResourcePattern(resourceType=GROUP, name=apicurio-registry-, patternType=PREFIXED)`:
 	(principal=User:CN=agora-apicurio-system-prod.woven-city.toyota,OU=CityOS, host=*, operation=READ, permissionType=ALLOW)

```

- Speedway Dev

```shell
# Change the context
❯ kubectx agora-kafka-admin-dev-gc-0-apps-ap-northeast-1

Switched to context "agora-kafka-admin-dev-gc-0-apps-ap-northeast-1".

# Check the Pod name
❯ kubectl get pod

Warning: Use tokens from the TokenRequest API or manually created secret-based tokens instead of auto-generated secret-based tokens.
NAME                                       READY   STATUS    RESTARTS   AGE
kafka-cli-7fcf849488-7fdnb                 2/2     Running   0          8d
kafka-cli-without-istio-645cbdf85f-wbx4w   1/1     Running   0          8d

# Log in to the Pod
❯ kubectl exec --stdin --tty kafka-cli-7fcf849488-7fdnb --as agora-kafka-admin-dev-admin -- /bin/bash

Warning: Use tokens from the TokenRequest API or manually created secret-based tokens instead of auto-generated secret-based tokens.

# Add the ACL
kafka-cli-7fcf849488-7fdnb:/$ kafka-acls.sh --bootstrap-server "b-1.agoraspeedwaydevk.lym4n6.c4.kafka.ap-northeast-1.amazonaws.com:9094,b-2.agoraspeedwaydevk.lym4n6.c4.kafka.ap-northeast-1.amazonaws.com:9094,b-3.agoraspeedwaydevk.lym4n6.c4.kafka.ap-northeast-1.amazonaws.com:9094" --add \
--allow-principal User:CN=agora-apicurio-system-dev.woven-city.toyota,OU=CityOS \
--allow-host "*" \
--operation read \
--group "apicurio-registry-" \
--resource-pattern-type prefixed

Picked up _JAVA_OPTIONS: -Xms1g -Xmx1g
Adding ACLs for resource `ResourcePattern(resourceType=GROUP, name=apicurio-registry-, patternType=PREFIXED)`:
 	(principal=User:CN=agora-apicurio-system-dev.woven-city.toyota,OU=CityOS, host=*, operation=READ, permissionType=ALLOW)
```


### (Temporarily) Copy KeyCloak Secret from Dev2

Until the ticket ([Sprint [Orc] - [Apicurio] Change Keycloak Client from Dev2 to Speedway Dev/Prod](https://wovencity.monday.com/boards/3813113014/views/90945203/pulses/7066594095)) is completed, copying the Secret from Dev2 by hand is necessary. Here are the commands to copy them:

- Speedway Dev

```shell
kubectl get secret keycloak-client-secret-apicurio-speedway-dev -n apicurio --context dev2-worker1-east -o yaml \
| sed -e 's/name: .*/name: keycloak-client-secret-apicurio-copied-from-dev2/' \
-e 's/namespace: .*/namespace: agora-apicurio-system-dev/' \
| yq e 'del(.metadata.ownerReferences,.metadata.labels,.metadata.creationTimestamp,.metadata.resourceVersion,.metadata.uid)' - \
| kubectl apply --context=vcluster_dev_agora-control-plane-dev_agora-control-plane-dev-gc-0-apps-ap-northeast-1-pinniped -f -
```

- Speedway Prod

```shell
kubectl get secret keycloak-client-secret-apicurio-speedway-prod -n apicurio --context dev2-worker1-east -o yaml \
| sed -e 's/name: .*/name: keycloak-client-secret-apicurio-copied-from-dev2/' \
-e 's/namespace: .*/namespace: agora-apicurio-system-prod/' \
| yq e 'del(.metadata.ownerReferences,.metadata.labels,.metadata.creationTimestamp,.metadata.resourceVersion,.metadata.uid)' - \
| kubectl apply --context=vcluster_prod_agora-control-plane-prod_agora-control-plane-prod-gc-0-apps-prod-ap-northeast-1-pinniped -f -
```

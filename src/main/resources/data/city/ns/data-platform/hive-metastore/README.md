# hive-metastore

## Build image

### HMS image

```shell
docker build . -t docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/hive:4.0.0-beta-1-0.0.3
docker push docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/hive:4.0.0-beta-1-0.0.3
```

### HMS admin cli image

```shell
docker build . -f Dockerfile.hms-admin-cli -t docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/hive:4.0.0-beta-1-0.0.3-with-auth
docker push docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/hive:4.0.0-beta-1-0.0.3-with-auth
```

## Check image

```shell
kubectl apply -f pod.yaml
```

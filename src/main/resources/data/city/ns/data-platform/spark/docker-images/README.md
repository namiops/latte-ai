# Spark Docker images

```shell
# jupyter-spark-hudi image
docker build . -t docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/jupyter-spark-hudi-image:0.0.4 -f jupyter-spark-hudi-image/Dockerfile

# spark-hudi image (manually)
bazel run //ns/data-platform/spark/docker-images/spark-hudi-image:manual_push
```


You can download the jars from the maven repo like the following:

```shell
wget https://repo1.maven.org/maven2/org/apache/hudi/hudi-datahub-sync-bundle/0.13.1/hudi-datahub-sync-bundle-0.13.1.jar
wget ...
```

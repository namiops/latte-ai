# Develop custom Hudi transformer

When you need to transform the data, please consider using the followings first:
- [the Hudi official transformers](https://hudi.apache.org/docs/next/transforms/)
  - see [our recipe](https://developer.woven-city.toyota/docs/default/Component/data-platform/06_import_and_transform_and_db/) for more details
- [Trino functions and operators](https://trino.io/docs/current/functions/json.html) 
  - see [our sample](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/data-platform/demo-transform-json-parse) for more details.

If there are requirements that cannot be fulfilled by these tools, such as wanting to use Python, you can create a custom transformer.

## How to develop that quickly

### Local environment

We have prepared a sample using MinIO instead of S3 for those who want to try ingestion locally using `skaffold`.
This includes the custom Hudi transformer using pyspark. 
If you're interested, please take a look.
- https://github.com/wp-wcm/city/tree/main/ns/data-platform/local-env

### Deploy the SparkApplication to Agora

In the AWS environment, it is necessary to configure [IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) because using access/secret keys are easy to leak.

Please modify [these lines](https://github.com/wp-wcm/city/blob/313a02d74bea9585845120b8dc7b0d27ae53114e/ns/data-platform/local-env/spark-hudi-sample/02-pyspark-hudi-transformer.yaml#L45-L49) and deploy that to Agora. 


## Running HiveSyncTool as a post process

To query the data from Trino, it is necessary to register the metadata information to our centralized Hive-metastore(HMS).

Our templated Hudi streamer registers the schema to the HMS as a postprocess automatically.
When using the custom Hudi transformer, however, it's necessary to register that using [HiveSyncTool](https://hudi.apache.org/docs/syncing_metastore/#hive-sync-tool) after the custom Hudi transform job has completed.

Here is the sample yaml to run that (please update the line that has `# FIXME: Modify this according to your settings` comment):

```yaml
apiVersion: sparkoperator.k8s.io/v1beta2
kind: SparkApplication
metadata:
  name: hudi-hive-sync-example  # FIXME: Modify this according to your settings
  namespace: data-platform-demo  # FIXME: Modify this according to your settings
spec:
  type: Scala
  mode: cluster
  image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/spark-hudi-image:main-61d41ced3ead-1709112082
  imagePullPolicy: Always
  mainClass: org.apache.hudi.hive.HiveSyncTool
  mainApplicationFile: local:///opt/spark/jars/processed_hudi-hive-sync-bundle-0.14.0.jar
  sparkVersion: 3.4.1
  restartPolicy:
    type: Never
  volumes:
    - name: hms-cm
      configMap:
        name: hive-metastore-conf
  # sparkConf is not specified here because HiveSyncTool ignores these configurations including spark.hadoop.fs.s3a.***
  # using HADOOP_CONF_DIR environment instead.
  driver:
    env:
      - name: HADOOP_CONF_DIR
        value: /etc/hive/conf
    annotations:
      traffic.sidecar.istio.io/excludeInboundPorts: "39000"   # necessary for the connection between driver and executor in the istio env
    podSecurityContext:
      fsGroup: 65534  # necessary for IRSA
    cores: 1
    coreLimit: 1200m
    memory: 1024m
    labels:
      version: 3.4.1
    serviceAccount: hudi-s3-ingest-irsa-sa  # FIXME: Modify this according to your settings
    javaOptions: -Djava.net.preferIPv6Addresses=true
    volumeMounts:
    - name: hms-cm
      mountPath:  /etc/hive/conf
  executor:
    env:
      - name: HADOOP_CONF_DIR
        value: /etc/hive/conf
    annotations:
      traffic.sidecar.istio.io/excludeOutboundPorts: "39000"  # necessary for the connection between driver and executor in the istio env
    cores: 1
    coreLimit: 1200m
    memory: 1024m
    instances: 1
    serviceAccount: hudi-s3-ingest-irsa-sa  # FIXME: Modify this according to your settings
    labels:
      version: 3.4.1
    javaOptions: -Djava.net.preferIPv6Addresses=true
    volumeMounts:
      - name: hms-cm
        mountPath: /etc/hive/conf
  arguments: # ref: https://hudi.apache.org/docs/syncing_metastore/#hive-sync-configuration
    - --metastore-uris
    - thrift://hive-metastore.hive-metastore:9083
    - --sync-mode
    - hms
    - --base-path
    - s3a://data-platform-sandbox-s3/hudi/hudi-transformed   # FIXME: Modify this according to your settings
    - --database
    - data-platform-demo   # FIXME: Modify this according to your settings
    - --table
    - hudi-hive-sync-example   # FIXME: Modify this according to your settings
    - --partitioned-by
    - _hoodie_partition_path
---
# Based on https://github.com/alexcpn/presto_in_kubernetes/blob/main/hive/metastore-cfg.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: hive-metastore-conf
  namespace: data-platform-demo
  labels:
    name: hive-metastore-conf
data:
  core-site.xml: |
    <configuration>
        <property>
            <name>fs.s3a.endpoint</name>
            <value>https://s3.ap-northeast-1.amazonaws.com</value>
        </property>
        <property>
            <name>fs.s3a.impl</name>
            <value>org.apache.hadoop.fs.s3a.S3AFileSystem</value>
        </property>
        <property>
            <name>fs.s3a.aws.credentials.provider</name>
            <value>com.amazonaws.auth.WebIdentityTokenCredentialsProvider</value>
        </property>
        <property>
            <name>fs.s3a.path.style.access</name>
            <value>true</value>
        </property>
    </configuration>
```

`HiveSyncTool` doesn't support the `--continuous` option unlike the `Hudi streamer`.
If it is necessary to run the `HiveSyncTool` periodically such as the partitions increases as time goes, we can use `ScheduledSparkApplication`.
See [the official document](https://github.com/GoogleCloudPlatform/spark-on-k8s-operator/blob/master/docs/user-guide.md#running-spark-applications-on-a-schedule-using-a-scheduledsparkapplication) for more details.

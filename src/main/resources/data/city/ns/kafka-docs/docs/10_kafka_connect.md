# Kafka Connect

Kafka Connect is a framework that allows you to bridge external data sources, like flat files, databases, or specialized data repositories to Kafka. 

This guide is designed to get you up and running quickly, and point out Agora-specific requirements. For full details on Kafka Connect and configuration options, please see the [Connect section of the Kafka documentation](https://kafka.apache.org/documentation/#connect).

## Connectors

Kafka Connect is a framework that allows plugins, called "connectors", to communicate with Kafka. 

A connector can be used to move data from an external location into a Kafka topic (called a *source connector*) or from Kafka to an external location (called a *sink connector*).

Connectors are available for many common tools and technologies. Please see [this list](https://docs.confluent.io/kafka-connectors/self-managed/kafka_connectors.html) for examples, though many are available only via commercial license. Database connections are common and supported via the open-source JDBC connector, which will be used for the examples in the rest of this document.

## Deploying Kafka Connect in Agora

Agora has Kubernetes controllers that enable you to deploy your own Kafka Connect worker cluster and configure it to talk to your external data repository by simply checking in custom resources to your namespace.

Two separate configurations are required: one for the cluster of Kafka Connect workers, and one (or more) for determining how those workers talk to your external data source.

### Building your container image

As recommended in the [operator documentation](https://strimzi.io/docs/operators/latest/deploying.html#creating-new-image-from-base-str), begin with the base Kafka Connect image.

Dockerfile:

```
FROM quay.io/strimzi/kafka:0.32.0-kafka-3.3.1
USER root:root
COPY ./plugins/ /opt/kafka/plugins/
USER 1001
```

Make sure the .jar files for all required plugins (including the specific DB driver, if using JDBC) are included in a subfolder of plugins - these will be registered in the Java CLASSPATH automatically.

```
.
├── Dockerfile
└── plugins
    └── jdbc
        ├── kafka-connect-jdbc-10.6.0.jar
        └── sqlite-jdbc-3.40.0.0.jar
```

Build and push this image to your repository, in this case Artifactory.

The sample is in https://github.com/wp-wcm/city/tree/main/ns/kafka-connect/docker

### Kafka Connect configuration

Create the custom resource for the Kafka Connect worker cluster.

Note that topic and ACL configuration is also required for the topics specified here, please see the 'Setting up Kafka for your application' for details.

Using [Zebra](https://developer.woven-city.toyota/docs/default/system/zebra), we can generate the resources in a simple manner.

The sample including Bazel BUILD file is in 
- Dev1, Dev2: https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/kafka-connect-sample
- Speedway Prod:  https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-strimzi-system/common/test/kafka-connect-wikimedia-0.1.0

!!! Note

    [Zebra](https://developer.woven-city.toyota/docs/default/system/zebra)  can only be used inside [the city monorepo](https://github.com/wp-wcm/city) for now (April 14th, 2023).
    If you are using the other repo, please create your resource by replacing the variable(`#@ data.values.<foo>`) with your value in [the template yaml](https://github.com/wp-wcm/city/blob/main/ns/kafka_connect_ytt/kafka_connect_template.yaml)


```yaml
#@data/values
---
namespace: "my-service"
clusterName: "my-service-connect-cluster"
replicas: 1
groupId: "my-service.connect-cluster"
offsetTopic: "my-service.connect-cluster-offsets"
configTopic: "my-service.connect-cluster-configs"
statusTopic: "my-service.connect-cluster-status"
image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/kafka-connect-jdbc:0.0.3

env: "speedway"  #! <=== The default is the empty string "". When using Speedway env, please set this to "speedway" 
```

This will generate the `KafkaConnect`, `ConfigMap`, `Service` resource as follows:

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaConnect
metadata:
  name: my-service-connect-cluster
  namespace: my-service
  annotations:
    strimzi.io/use-connector-resources: "true"
spec:
  # Number of worker nodes for connect tasks
  replicas: 1
 
  # Addresses of Kafka brokers
  bootstrapServers: kafka.default:9094
  
  # Topic configuration for Kafka Connect
  config:
    group.id: my-service.connect-cluster
    offset.storage.topic: my-service.connect-cluster-offsets
    config.storage.topic: my-service.connect-cluster-configs
    status.storage.topic: my-service.connect-cluster-status
    
    # Specify how to convert keys and values. When using the JDBC 
    # (or other database) connector, schemas are required in order to map
    # from tables to data schemas.
    key.converter: org.apache.kafka.connect.json.JsonConverter
    value.converter: org.apache.kafka.connect.json.JsonConverter
    key.converter.schemas.enable: true
    value.converter.schemas.enable: true
  
  # The custom image for your Kafka Connect cluster, including any required plugins
  
  # Note: plugins are packaged as .jar files, and sometimes these need to be retrieved
  # directly from a maven repo, e.g. https://packages.confluent.io/maven/io/confluent/kafka-connect-jdbc/10.6.0/
  # The DB driver is also required, such as https://github.com/xerial/sqlite-jdbc 
  # - kafka-connect doesn't include specific DB drivers
  image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/kafka-connect-jdbc:0.0.3

  # The following is copied from https://github.com/strimzi/strimzi-kafka-operator/blob/main/examples/metrics/kafka-connect-metrics.yaml
  metricsConfig:
    type: jmxPrometheusExporter
    valueFrom:
      configMapKeyRef:
        name: connect-metrics
        key: metrics-config.yml
---
kind: ConfigMap
apiVersion: v1
metadata:
  name: connect-metrics
  namespace: kafka-connect-sample
  labels:
    app: strimzi
data:
  metrics-config.yml: |
    # Inspired by kafka-connect rules
    # https://github.com/prometheus/jmx_exporter/blob/master/example_configs/kafka-connect.yml
    # See https://github.com/prometheus/jmx_exporter for more info about JMX Prometheus Exporter metrics
    lowercaseOutputName: true
    lowercaseOutputLabelNames: true
    rules:
      #kafka.connect:type=app-info,client-id="{clientid}"
      #kafka.consumer:type=app-info,client-id="{clientid}"
      #kafka.producer:type=app-info,client-id="{clientid}"
      - pattern: 'kafka.(.+)<type=app-info, client-id=(.+)><>start-time-ms'
        name: kafka_$1_start_time_seconds
        labels:
          clientId: "$2"
        help: "Kafka $1 JMX metric start time seconds"
        type: GAUGE
        valueFactor: 0.001
      - pattern: 'kafka.(.+)<type=app-info, client-id=(.+)><>(commit-id|version): (.+)'
        name: kafka_$1_$3_info
        value: 1
        labels:
          clientId: "$2"
          $3: "$4"
        help: "Kafka $1 JMX metric info version and commit-id"
        type: GAUGE
      #kafka.producer:type=producer-topic-metrics,client-id="{clientid}",topic="{topic}"", partition="{partition}"
      #kafka.consumer:type=consumer-fetch-manager-metrics,client-id="{clientid}",topic="{topic}"", partition="{partition}"
      - pattern: kafka.(.+)<type=(.+)-metrics, client-id=(.+), topic=(.+), partition=(.+)><>(.+-total|compression-rate|.+-avg|.+-replica|.+-lag|.+-lead)
        name: kafka_$2_$6
        labels:
          clientId: "$3"
          topic: "$4"
          partition: "$5"
        help: "Kafka $1 JMX metric type $2"
        type: GAUGE
      #kafka.producer:type=producer-topic-metrics,client-id="{clientid}",topic="{topic}"
      #kafka.consumer:type=consumer-fetch-manager-metrics,client-id="{clientid}",topic="{topic}"", partition="{partition}"
      - pattern: kafka.(.+)<type=(.+)-metrics, client-id=(.+), topic=(.+)><>(.+-total|compression-rate|.+-avg)
        name: kafka_$2_$5
        labels:
          clientId: "$3"
          topic: "$4"
        help: "Kafka $1 JMX metric type $2"
        type: GAUGE
      #kafka.connect:type=connect-node-metrics,client-id="{clientid}",node-id="{nodeid}"
      #kafka.consumer:type=consumer-node-metrics,client-id=consumer-1,node-id="{nodeid}"
      - pattern: kafka.(.+)<type=(.+)-metrics, client-id=(.+), node-id=(.+)><>(.+-total|.+-avg)
        name: kafka_$2_$5
        labels:
          clientId: "$3"
          nodeId: "$4"
        help: "Kafka $1 JMX metric type $2"
        type: UNTYPED
      #kafka.connect:type=kafka-metrics-count,client-id="{clientid}"
      #kafka.consumer:type=consumer-fetch-manager-metrics,client-id="{clientid}"
      #kafka.consumer:type=consumer-coordinator-metrics,client-id="{clientid}"
      #kafka.consumer:type=consumer-metrics,client-id="{clientid}"
      - pattern: kafka.(.+)<type=(.+)-metrics, client-id=(.*)><>(.+-total|.+-avg|.+-bytes|.+-count|.+-ratio|.+-age|.+-flight|.+-threads|.+-connectors|.+-tasks|.+-ago)
        name: kafka_$2_$4
        labels:
          clientId: "$3"
        help: "Kafka $1 JMX metric type $2"
        type: GAUGE
      #kafka.connect:type=connector-metrics,connector="{connector}"
      - pattern: 'kafka.(.+)<type=connector-metrics, connector=(.+)><>(connector-class|connector-type|connector-version|status): (.+)'
        name: kafka_connect_connector_$3
        value: 1
        labels:
          connector: "$2"
          $3: "$4"
        help: "Kafka Connect $3 JMX metric type connector"
        type: GAUGE
      #kafka.connect:type=connector-task-metrics,connector="{connector}",task="{task}<> status"
      - pattern: 'kafka.connect<type=connector-task-metrics, connector=(.+), task=(.+)><>status: ([a-z-]+)'
        name: kafka_connect_connector_task_status
        value: 1
        labels:
          connector: "$1"
          task: "$2"
          status: "$3"
        help: "Kafka Connect JMX Connector task status"
        type: GAUGE
      #kafka.connect:type=task-error-metrics,connector="{connector}",task="{task}"
      #kafka.connect:type=source-task-metrics,connector="{connector}",task="{task}"
      #kafka.connect:type=sink-task-metrics,connector="{connector}",task="{task}"
      #kafka.connect:type=connector-task-metrics,connector="{connector}",task="{task}"
      - pattern: kafka.connect<type=(.+)-metrics, connector=(.+), task=(.+)><>(.+-total|.+-count|.+-ms|.+-ratio|.+-seq-no|.+-rate|.+-max|.+-avg|.+-failures|.+-requests|.+-timestamp|.+-logged|.+-errors|.+-retries|.+-skipped)
        name: kafka_connect_$1_$4
        labels:
          connector: "$2"
          task: "$3"
        help: "Kafka Connect JMX metric type $1"
        type: GAUGE
      #kafka.connect:type=connector-metrics,connector="{connector}"
      #kafka.connect:type=connect-worker-metrics,connector="{connector}"
      - pattern: kafka.connect<type=connect-worker-metrics, connector=(.+)><>([a-z-]+)
        name: kafka_connect_worker_$2
        labels:
          connector: "$1"
        help: "Kafka Connect JMX metric $1"
        type: GAUGE
      #kafka.connect:type=connect-worker-metrics
      - pattern: kafka.connect<type=connect-worker-metrics><>([a-z-]+)
        name: kafka_connect_worker_$1
        help: "Kafka Connect JMX metric worker"
        type: GAUGE
      #kafka.connect:type=connect-worker-rebalance-metrics
      - pattern: kafka.connect<type=connect-worker-rebalance-metrics><>([a-z-]+)
        name: kafka_connect_worker_rebalance_$1
        help: "Kafka Connect JMX metric rebalance information"
        type: GAUGE
---
apiVersion: v1
kind: Service
metadata:
  name: my-service-connect-cluster-prometheus
  namespace: my-service
  labels:
    app: strimzi
    strimzi.io/kind: KafkaConnect
spec:
  ports:
  - name: tcp-prometheus
    port: 9404
    targetPort: 9404
    appProtocol: http
  selector:
    strimzi.io/kind: KafkaConnect
```

`ConfigMap`, `Service` resource is necessary for the Prometheus to collect the metrics. (`ServiceMonitor` named `kafka-resources-metrics` in `kafka-strimzi-operator` namespace will find the Service.)

Thanks to these settings, `@agora-data-orchestration` team can notice when status of KafkaConnect pods are not healthy.  

Also, you can check the grafana dashboard for this. 
- http://go/agora-kafka-connect-dashboard

### Connector configuration

Next, create a separate custom resource for the configuration of the connector itself

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaConnector
metadata:
  name: my-service-sink-connector
  namespace: my-service
  labels:
    strimzi.io/cluster: my-service-connect-cluster
spec:
  class: io.confluent.connect.jdbc.JdbcSinkConnector
  tasksMax: 1
  config:
    topics: my-service-source-topic
    connection.url: "jdbc:sqlite:/tmp/my-service-sink.db"
    auto.create: "true"
    name: "my-service-sink-connector"
```

With this configuration, once deployed and running, all data written to the `my-service-source-topic` topic will be automatically written to the `my-service-sink.db` SQLite database. 

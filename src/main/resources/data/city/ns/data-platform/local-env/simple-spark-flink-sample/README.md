# java Spark/Flink local dev environment to iterate quickly on Spark/Flink jobs

When we want to use `SparkApplication` or `FlinkDeployment`, it would take time to code, build, push, and update
manifests everytime.

Here, you can find reference applications here that utilize Skaffold for accelerated development.

Currently, we offer the following sample applications:

- `SparkApplication` CsvToKafka sample
- `FlinkDeployment` CsvToKafka sample

## Setup

> **Note**
> This sample works exclusively in a Linux amd64 environment.

If you intend to write code on a different environment, such as Apple Silicon machines, and run it on a Linux AMD
machine, consider using the following tools:

- Visual Studio Code
    - Remote
      SSH: [Developing on Remote Machines using SSH and Visual Studio Code](https://code.visualstudio.com/docs/remote/ssh)
- IntelliJ IDEA
    - Remote
      SSH: [Connect to a remote server from IntelliJ IDEA | IntelliJ IDEA Documentation](https://www.jetbrains.com/help/idea/remote-development-starting-page.html)
    - SFTP: you can synchronize the code
      using [Configure synchronization with a server | IntelliJ IDEA Documentation](https://www.jetbrains.com/help/idea/configuring-synchronization-with-a-remote-host.html)
        - Note that remote SSH can consume a lot of CPU, so SFTP might be a better option.

### Download skaffold

```shell
curl -Lo skaffold https://storage.googleapis.com/skaffold/releases/latest/skaffold-linux-amd64 && \
sudo install skaffold /usr/local/bin/
```

### start minikube

```shell
minikube start  --container-runtime=docker  --memory=8192
```

```shell
eval $(minikube docker-env)
```

### start kafka

Following [Quickstarts](https://strimzi.io/quickstarts/), let's deploy Kafka.

```shell
kubectl create namespace kafka

# Deploy the Strimzi cluster operator
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka

# Follow the deployment of the operator
kubectl get pod -n kafka --watch
```

Once the operator is running it will watch for new custom resources and create the Kafka cluster, topics or users that correspond to those custom resources.


```shell
# Deploy Kafka
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka 

# Wait 
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka 
```

### start spark-operator

````shell
helm repo add spark-operator https://googlecloudplatform.github.io/spark-on-k8s-operator
helm install spark-operator spark-operator/spark-operator --namespace spark-operator --set webhook.enable=true --create-namespace
````

### start flink-operator

Following [the official document](https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-main/docs/try-flink-kubernetes-operator/quick-start/#deploying-the-operator),
run the followings:

````shell
kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml

helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.6.1
helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
````

## Start Development

### start skaffold

```shell
skaffold dev
```

This deploys:

- `SparkApplication` that reads `data.csv` and write it to Kafka.
- `FlinkDeployment` that reads `data.csv` and write it to Kafka.
- `Pod` for debugging that has the same image with the `SparkApplication`/`FlinkDeployment`

If you make changes to the code, Skaffold will automatically detect and rebuild the image and push it to the local minikube image registry, and then reapply the
manifest.

### check the result

Let's check the result in Kafka.

```shell
kubectl exec -n kafka my-cluster-kafka-0 --stdin --tty  -- /bin/bash

[kafka@my-cluster-kafka-0 kafka]$ bin/kafka-topics.sh --list --bootstrap-server localhost:9092
__consumer_offsets
__strimzi-topic-operator-kstreams-topic-store-changelog
__strimzi_store_topic
flink-csv-to-kafka-topic
spark-csv-to-kafka-topic

[kafka@my-cluster-kafka-0 kafka]$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic spark-csv-to-kafka-topic --from-beginning
{"Name":"John","Age":" 25"}
{"Name":"Jane","Age":" 30"}
{"Name":"Bob","Age":" 40"}
{"Name":"Alice","Age":" 35"}
{"Name":"Mike","Age":" 28"}
{"Name":"Sara","Age":" 45"}
{"Name":"David","Age":" 32"}
{"Name":"Emily","Age":" 27"}
{"Name":"Tom","Age":" 38"}

```

### Add jars

Add your target maven jar to `maven_install.bzl`

Then run the following command:

```shell
bazel run @unpinned_maven//:pin
```

Check if your jar can be used.

```shell
bazel query "@maven//:*" | grep <YOUR_JAR_NAME>
```

## Question

If your application has some issues, please reach out to the Agora Orchestration Team (`@agora-data-orchestration`) via
the
[Agora AMA Chanel](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7)

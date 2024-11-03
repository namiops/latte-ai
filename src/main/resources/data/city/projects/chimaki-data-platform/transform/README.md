# Spark Hudi local Job

This job supports the following:
- a pyspark job that transform the ingested data

## Setup

> **Note**
> This job works exclusively in a Linux amd64 environment.

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

### Install bazelisk(bazel)

Follow [the doc](https://developer.woven-city.toyota/docs/default/domain/agora-domain/agora_developers/development_environment/02_setting_up_agora_dev_env/#install-bazelisk) and install `bazelisk(bazel)` CLI.


### Install skaffold

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

### start spark-operator

````shell
helm repo add spark-operator https://googlecloudplatform.github.io/spark-on-k8s-operator
helm install spark-operator spark-operator/spark-operator --namespace chimaki-data-platform --set webhook.enable=true --create-namespace
````

## Develop pyspark job to transform the ingested data

You can see the code in `python-app/hudi_transform.py`  
If you want to change the date to be analyzed, please change `TARGET_DATE` in the code.

Since S3 is used for data input and output, an access key must be set in `transformer.yaml`.

```
    spark.hadoop.fs.s3a.access.key: "########## Please fill in the key ##########"
    spark.hadoop.fs.s3a.secret.key: "########## Please fill in the key ##########"
```


Let's deploy the spark application (and the pod just for debugging the image)

```shell
skaffold dev
```

If you make changes to the code, Skaffold will automatically detect and rebuild the image and push it to the local minikube image registry, and then reapply the manifest.

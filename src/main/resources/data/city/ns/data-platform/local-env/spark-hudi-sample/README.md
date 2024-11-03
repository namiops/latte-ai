# Spark Hudi local samples

This sample supports the following:
- a sample Hudi streamer ingestion job
  - this will ingest the `minio/data/raw/iris.csv`
- a pyspark job that transform the ingested data

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
helm install spark-operator spark-operator/spark-operator --namespace spark-operator --set webhook.enable=true --create-namespace
````

### deploy [min-io(object storage)](https://github.com/kubernetes/examples/tree/master/staging/storage/minio)

```shell
kubectl apply -f ./minio/minio-standalone-pvc.yaml
kubectl apply -f ./minio/minio-standalone-deployment.yaml
kubectl apply -f ./minio/minio-standalone-service.yaml
```

#### Upload sample data

First, run the following command:

```shell
kubectl port-forward svc/minio-service 9090
```

Then, 
- Access http://localhost:9090 (user/pass: `minio/minio123`) in your browser
- Create `test-bucket` bucket
- Upload the `minio/data/raw` directory to the bucket so that `test-bucket` contains the `raw/iris.csv`


## Run Hudi-streamer ingestion Spark job

Let's ingest this sample data first.

```shell
kubectl apply -f 01-spark-hudi-streamer-ingestion.yaml
```

You can check the result:

```shell
kubectl get sparkapplications.sparkoperator.k8s.io -A
```

This will show the following

```log
NAMESPACE   NAME                STATUS      ATTEMPTS   START                  FINISH                 AGE
default     hudi-minio-ingest   COMPLETED   1          2024-01-17T02:21:43Z   2024-01-17T02:22:32Z   26h
```

Also, you can check the log of this spark job.

```shell
kubectl logs hudi-minio-ingest-driver spark-kubernetes-driver
```

You can see `hudi` directory in the minio UI:

![ingested-minio](./fig/ingested-minio-result.jpg)

## Develop pyspark job to transform the ingested data

Let's add the additional column named `petal_mult`. This is the product of `petal_width` and `petal_length`

You can see the sample code in `python-app/sample_hudi_transform.py`

Let's deploy the spark application (and the pod just for debugging the image)

```shell
skaffold dev
```

If you make changes to the code, Skaffold will automatically detect and rebuild the image and push it to the local minikube image registry, and then reapply the manifest.

This will not only deploy the spark app but also add port-forwarding configuration(`kubectl port-forward svc/minio-service 9090`)


Let's check the log.

```shell
kubectl logs pyspark-hudi-transformer-driver spark-kubernetes-driver -f
```

You will see that `petal_mult` is newly added!

```log
...

+-------------------+--------------------+--------------------+----------------------+--------------------+------------+-----------+------------+-----------+----------+-------------------+
|_hoodie_commit_time|_hoodie_commit_seqno|  _hoodie_record_key|_hoodie_partition_path|   _hoodie_file_name|sepal_length|sepal_width|petal_length|petal_width|   variety|         petal_mult|
+-------------------+--------------------+--------------------+----------------------+--------------------+------------+-----------+------------+-----------+----------+-------------------+
|  20240120051739479|20240120051739479...|20240120051739479...|                      |cadd2066-ebae-44c...|         4.4|        3.2|         1.3|        0.2|    Setosa|               0.26|
|  20240120051739479|20240120051739479...|20240120051739479...|                      |cadd2066-ebae-44c...|         5.5|        2.3|         4.0|        1.3|Versicolor|                5.2|
|  20240120051739479|20240120051739479...|20240120051739479...|                      |cadd2066-ebae-44c...|         7.1|        3.0|         5.9|        2.1| Virginica|              12.39|

...
```

Also, you can check the result in the MinIO
- Access http://localhost:9090 (user/pass: `minio/minio123`) in your browser
- Click `test-bucket` bucket => Click `hudi-transformed` directory. You will see the partitioned output directories. (This example does not enable partitioning for simplicity so there will be no partitioned output directories.) If you want to check the parquet content, download the parquet file from the MinIO then use the tools such as [parquet-tools](https://pypi.org/project/parquet-tools/)
  ```shell
  $ parquet-tools show f70f26a8-811c-4532-8dfb-f9fd31da189f-0_1-9-0_20240119060722291.parquet
  
    +-----------------------+-------------------------+-------------------------+--------------------------+------------------------------------------------------------------------+----------------+---------------+----------------+---------------+------------+--------------+
    |   _hoodie_commit_time |    _hoodie_commit_seqno |      _hoodie_record_key | _hoodie_partition_path   | _hoodie_file_name                                                      |   sepal_length |   sepal_width |   petal_length |   petal_width | variety    |   petal_mult |
    |-----------------------+-------------------------+-------------------------+--------------------------+------------------------------------------------------------------------+----------------+---------------+----------------+---------------+------------+--------------|
    |     20240120051739479 |   20240120051739479_0_1 |   20240120051739479_0_0 |                          | cadd2066-ebae-44c9-8e72-ebffe6ba055e-0_0-8-0_20240120051739479.parquet |            4.4 |           3.2 |            1.3 |           0.2 | Setosa     |         0.26 |
    |     20240120051739479 |   20240120051739479_0_2 |   20240120051739479_0_1 |                          | cadd2066-ebae-44c9-8e72-ebffe6ba055e-0_0-8-0_20240120051739479.parquet |            5.5 |           2.3 |            4   |           1.3 | Versicolor |         5.2  |
    |     20240120051739479 |   20240120051739479_0_3 |   20240120051739479_0_2 |                          | cadd2066-ebae-44c9-8e72-ebffe6ba055e-0_0-8-0_20240120051739479.parquet |            7.1 |           3   |            5.9 |           2.1 | Virginica  |        12.39 |
  
  ...
  ```

That's it! Enjoy pyspark coding!

## Tips

### How to add pip libraries

Just update `requirements.txt`

### How to build and push image

#### login

Generate your api key at https://artifactory-ha.tri-ad.tech/ui/admin/artifactory/user_profile

Then use the key to login:

```shell
docker login docker.artifactory-ha.tri-ad.tech
```

#### build & push

```shell
# set your tag
TAG="" 

# build image
docker build -t docker.artifactory-ha.tri-ad.tech/wcm-cityos/ns/data-platform/local-env/spark-hudi-sample-transform:$TAG .

# push image
docker push docker.artifactory-ha.tri-ad.tech/wcm-cityos/ns/data-platform/local-env/spark-hudi-sample-transform:$TAG
```

## TODO
- [Sprint [Orc] - Bazelify sample pyspark transformer image](https://wovencity.monday.com/boards/3813113014/views/86794788/pulses/6068408619)

## Question

If your application has some issues, please reach out to the Agora Orchestration Team (`@agora-data-orchestration`) via
the
[Agora AMA Chanel](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7)

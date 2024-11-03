# SteelRug

## Development on the local machine

> **Note**
> This sample works exclusively in a Linux amd64 environment.

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

### update the credential

Please update the `spark.hadoop.fs.s3a.access.key` and `spark.hadoop.fs.s3a.secret.key` in `spark-application.yaml`

### start skaffold

Hudi checks if the job has been run before running the job.

To make Hudi run the SparkJob, we can update `target-base-path` in `SparkApplication.spec.arguments` or delete the
existing the `target-base-path` bucket.

```yaml
apiVersion: sparkoperator.k8s.io/v1beta2
kind: SparkApplication
metadata:
  name: hudi-s3-ingest-local
spec:
  ...
  arguments:
    ...
    - --target-base-path
    - s3a://data-platform-sandbox-s3/hudi/electric-example-local 
```

Then, let's run the skaffold command:

```shell
skaffold dev
```

This deploys:

- `SparkApplication` that read a small JSON file
  in `hoodie.deltastreamer.source.dfs.root (s3a://data-platform-sandbox-s3-source-power/data)` then write a Hudi output
  to the `target-base-path (s3a://data-platform-sandbox-s3/hudi/electric-example-local)` bucket
- `Pod` for debugging that has the same image with the `SparkApplication`

If you make changes to the code, Skaffold will automatically detect and rebuild the image and push it to the local
minikube image registry, and then reapply the manifest.

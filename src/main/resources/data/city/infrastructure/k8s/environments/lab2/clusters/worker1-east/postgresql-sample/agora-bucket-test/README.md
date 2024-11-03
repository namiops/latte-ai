# Manifests for having an ad-hoc test with Agora Bucket

The manifests in this folder intends to support an ad-hoc test with Agora Bucket for the `postgresql-sample` namespace in Lab2.

## Deploy

You can use these manifests by deploying them to the `postgresql-sample` namespace like the following.

```bash
$ kubectl config use-context lab2-worker1-east
Switched to context "lab2-worker1-east".

$ kubectl apply -k infrastructure/k8s/environments/lab2/clusters/worker1-east/postgresql-sample/agora-bucket-test -n postgresql-sample 
```

## Access a bucket with AWS CLI

You can see more information about how to use the pod from [the document on Developer Portal](https://developer.woven-city.toyota/docs/default/Component/object-storage-service/02-agora-bucket-quickstart/#access-the-provisioned-bucket).
